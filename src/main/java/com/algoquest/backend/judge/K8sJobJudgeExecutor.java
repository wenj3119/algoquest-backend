package com.algoquest.backend.judge;

import com.algoquest.backend.judge.spec.JudgeSpecData;
import com.algoquest.backend.judge.spec.TestCaseData;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.BatchV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Capabilities;
import io.kubernetes.client.openapi.models.V1ConfigMap;
import io.kubernetes.client.openapi.models.V1ConfigMapVolumeSource;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1EmptyDirVolumeSource;
import io.kubernetes.client.openapi.models.V1Job;
import io.kubernetes.client.openapi.models.V1JobSpec;
import io.kubernetes.client.openapi.models.V1JobStatus;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSecurityContext;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;
import io.kubernetes.client.openapi.models.V1SeccompProfile;
import io.kubernetes.client.openapi.models.V1SecurityContext;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.util.Config;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Production judge executor. Creates one K8s Job per submission in the {@code judge-sandbox}
 * namespace with full Pod hardening: empty env, non-root user, read-only root filesystem,
 * capabilities dropped, seccomp RuntimeDefault, deny-all-egress NetworkPolicy.
 *
 * <p>Activated only under {@code profile=prod}; {@link JudgeConfig} instantiates this bean.
 * The caller ({@link com.algoquest.backend.service.ProblemService}) is unaware of the switch.
 *
 * <p>Result anti-spoofing: user code runs inside the sandboxed JVM but results are written by
 * the framework-generated {@code Main.java} to a result file ({@code /tmp/judge-result/result.txt})
 * AFTER all solution calls complete. The entrypoint.sh (baked into the judge image, not user
 * controlled) cats the file to stdout between sentinel markers that appear AFTER Java exits.
 * The executor extracts only content between those sentinels.
 */
public class K8sJobJudgeExecutor implements JudgeExecutor {

    private static final Logger log = LoggerFactory.getLogger(K8sJobJudgeExecutor.class);

    static final String RESULTS_START = "===JUDGE_RESULTS_START===";
    static final String RESULTS_END = "===JUDGE_RESULTS_END===";
    static final String COMPILE_ERROR_START = "===JUDGE_COMPILE_ERROR===";
    static final String COMPILE_ERROR_END = "===JUDGE_COMPILE_ERROR_END===";

    private final CoreV1Api coreApi;
    private final BatchV1Api batchApi;
    private final K8sJudgeProperties props;

    K8sJobJudgeExecutor(K8sJudgeProperties props) throws IOException {
        this.props = props;
        ApiClient client = Config.defaultClient();
        client.setHttpClient(client.getHttpClient().newBuilder()
                .readTimeout(Duration.ofSeconds(props.getApiTimeoutSeconds()))
                .build());
        Configuration.setDefaultApiClient(client);
        this.coreApi = new CoreV1Api();
        this.batchApi = new BatchV1Api();
    }

    /** Package-private for unit tests. */
    K8sJobJudgeExecutor(K8sJudgeProperties props, CoreV1Api coreApi, BatchV1Api batchApi) {
        this.props = props;
        this.coreApi = coreApi;
        this.batchApi = batchApi;
    }

    @Override
    public JudgeResult execute(JudgeSpecData spec, List<TestCaseData> testCases, String code) {
        String name = "judge-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        try {
            String mainCode = JavaMainGenerator.generateFromSpec(spec, testCases);
            createConfigMap(name, code, mainCode);
            createJob(name);
            waitForCompletion(name);
            String logs = getPodLogs(name);
            return parseLogsToResult(logs, testCases.size());
        } catch (JudgeTimeoutException e) {
            return new JudgeResult("timeout", 0, 0,
                    "判题超时：Job 在 " + props.getExecutionTimeoutSeconds() + " 秒内未完成。",
                    List.of(), "", "", null);
        } catch (Exception e) {
            log.error("K8s judge failed for job {}", name, e);
            return new JudgeResult("runtime_error", 0, 0,
                    "判题系统错误: " + e.getMessage(), List.of(), "", e.toString(), null);
        } finally {
            cleanupSafely(name);
        }
    }

    // ── ConfigMap creation ────────────────────────────────────────────────────

    void createConfigMap(String name, String solutionCode, String mainCode) throws ApiException {
        V1ConfigMap cm = new V1ConfigMap()
                .metadata(new V1ObjectMeta()
                        .name(name)
                        .namespace(props.getNamespace())
                        .labels(Map.of("app", "algoquest-judge")))
                .data(Map.of(
                        "Solution.java", solutionCode,
                        "Main.java", mainCode));
        coreApi.createNamespacedConfigMap(props.getNamespace(), cm).execute();
    }

    // ── Job creation ─────────────────────────────────────────────────────────

    void createJob(String name) throws ApiException {
        V1Job job = buildJob(name);
        batchApi.createNamespacedJob(props.getNamespace(), job).execute();
    }

    /** Package-private: exposed for unit tests that verify the Pod spec is correctly hardened. */
    V1Job buildJob(String name) {
        return new V1Job()
                .metadata(new V1ObjectMeta()
                        .name(name)
                        .namespace(props.getNamespace())
                        .labels(Map.of("app", "algoquest-judge")))
                .spec(new V1JobSpec()
                        .backoffLimit(0)
                        .ttlSecondsAfterFinished(props.getJobTtlSeconds())
                        .activeDeadlineSeconds((long) props.getActiveDeadlineSeconds())
                        .template(buildPodTemplate(name)));
    }

    private V1PodTemplateSpec buildPodTemplate(String jobName) {
        return new V1PodTemplateSpec()
                .spec(new V1PodSpec()
                        .restartPolicy("Never")
                        .automountServiceAccountToken(false)
                        .securityContext(new V1PodSecurityContext()
                                .runAsNonRoot(true)
                                .runAsUser(65534L)
                                .runAsGroup(65534L)
                                .fsGroup(65534L)
                                .seccompProfile(new V1SeccompProfile().type("RuntimeDefault")))
                        .volumes(List.of(
                                new V1Volume()
                                        .name("workspace")
                                        .configMap(new V1ConfigMapVolumeSource()
                                                .name(jobName)
                                                .defaultMode(0444)),
                                new V1Volume()
                                        .name("tmp-work")
                                        .emptyDir(new V1EmptyDirVolumeSource()
                                                .medium("Memory")
                                                .sizeLimit(Quantity.fromString(props.getTmpfsSizeLimit())))))
                        .containers(List.of(buildContainer())));
    }

    private V1Container buildContainer() {
        return new V1Container()
                .name("judge")
                .image(props.getImage())
                .imagePullPolicy("IfNotPresent")
                .securityContext(new V1SecurityContext()
                        .allowPrivilegeEscalation(false)
                        .readOnlyRootFilesystem(true)
                        .capabilities(new V1Capabilities().drop(List.of("ALL"))))
                .resources(new V1ResourceRequirements()
                        .limits(Map.of(
                                "cpu", Quantity.fromString(props.getCpuLimit()),
                                "memory", Quantity.fromString(props.getMemoryLimit())))
                        .requests(Map.of(
                                "cpu", Quantity.fromString("500m"),
                                "memory", Quantity.fromString(props.getMemoryLimit()))))
                // EXPLICIT EMPTY LIST: no env vars whatsoever.
                // K8s does not inherit parent Pod env — this is the architectural guarantee
                // that JWT_SECRET and DB passwords cannot reach user code.
                .env(List.of())
                // envFrom is intentionally absent (null): no env inheritance from ConfigMaps or Secrets.
                // downwardAPI is not used: the Pod spec has no envFrom or valueFrom fields.
                .volumeMounts(List.of(
                        new V1VolumeMount()
                                .name("workspace")
                                .mountPath("/workspace")
                                .readOnly(true),
                        new V1VolumeMount()
                                .name("tmp-work")
                                .mountPath("/tmp")));
    }

    // ── Wait for completion ───────────────────────────────────────────────────

    private void waitForCompletion(String name) throws ApiException, InterruptedException, JudgeTimeoutException {
        long deadlineMs = System.currentTimeMillis() + ((long) props.getExecutionTimeoutSeconds() * 1000);
        while (System.currentTimeMillis() < deadlineMs) {
            V1Job job = batchApi.readNamespacedJob(name, props.getNamespace()).execute();
            V1JobStatus status = job.getStatus();
            if (status != null) {
                Integer succeeded = status.getSucceeded();
                Integer failed = status.getFailed();
                if (succeeded != null && succeeded > 0) return;
                if (failed != null && failed > 0) return;
            }
            Thread.sleep(500);
        }
        throw new JudgeTimeoutException("Job " + name + " did not complete within " +
                props.getExecutionTimeoutSeconds() + "s");
    }

    // ── Read pod logs ─────────────────────────────────────────────────────────

    private String getPodLogs(String name) throws ApiException {
        V1PodList podList = coreApi.listNamespacedPod(props.getNamespace())
                .labelSelector("job-name=" + name)
                .execute();
        List<V1Pod> pods = podList.getItems();
        if (pods.isEmpty()) {
            throw new IllegalStateException("No pod found for job: " + name);
        }
        String podName = pods.get(0).getMetadata().getName();
        try {
            return coreApi.readNamespacedPodLog(podName, props.getNamespace())
                    .container("judge")
                    .execute();
        } catch (ApiException e) {
            log.warn("Could not read logs for pod {}: {}", podName, e.getMessage());
            return "";
        }
    }

    // ── Parse logs → JudgeResult ─────────────────────────────────────────────

    JudgeResult parseLogsToResult(String logs, int expectedCaseCount) {
        if (logs == null) logs = "";

        if (logs.contains(COMPILE_ERROR_START)) {
            String errorText = extractBetween(logs, COMPILE_ERROR_START, COMPILE_ERROR_END);
            return new JudgeResult("compile_error", 0, 0,
                    "编译失败: " + (errorText != null ? errorText.strip() : "未知编译错误"),
                    List.of(), "", errorText != null ? errorText : "", 1);
        }

        String resultSection = extractLastBetween(logs, RESULTS_START, RESULTS_END);
        if (resultSection == null) {
            return new JudgeResult("runtime_error", 0, 0,
                    "判题容器未产生结果（可能因 OOM、超时或 JVM 崩溃）",
                    List.of(), logs, "", null);
        }

        List<JudgeCaseResult> cases = parseCases(resultSection);
        int passedCount = (int) cases.stream().filter(JudgeCaseResult::passed).count();
        String status = passedCount == cases.size() && !cases.isEmpty() ? "passed" : "failed";
        String message = passedCount == cases.size() && !cases.isEmpty()
                ? "全部测试用例通过。"
                : "部分测试用例未通过。";
        return new JudgeResult(status, passedCount, cases.size(), message,
                cases, "", "", 0);
    }

    /**
     * Extracts content between the LAST occurrence of {@code start} and the following
     * {@code end}. Using the last occurrence makes it robust against user code printing
     * the same sentinel strings to stdout before the framework's entrypoint.sh runs.
     */
    private String extractLastBetween(String text, String start, String end) {
        int lastStart = text.lastIndexOf(start);
        if (lastStart == -1) return null;
        int contentStart = lastStart + start.length();
        int endIdx = text.indexOf(end, contentStart);
        if (endIdx == -1) return null;
        return text.substring(contentStart, endIdx);
    }

    private String extractBetween(String text, String start, String end) {
        int startIdx = text.indexOf(start);
        if (startIdx == -1) return null;
        int contentStart = startIdx + start.length();
        int endIdx = text.indexOf(end, contentStart);
        if (endIdx == -1) return null;
        return text.substring(contentStart, endIdx);
    }

    // ── CASE_RESULT parsing (mirrors LocalProcessJudgeExecutor) ─────────────

    private List<JudgeCaseResult> parseCases(String content) {
        List<JudgeCaseResult> results = new ArrayList<>();
        for (String line : content.split("\\R")) {
            if (!line.startsWith("CASE_RESULT|")) continue;
            List<String> parts = splitStructuredLine(line);
            if (parts.size() != 5) continue;
            results.add(new JudgeCaseResult(
                    unescape(parts.get(1)),
                    unescape(parts.get(2)),
                    unescape(parts.get(3)),
                    Boolean.parseBoolean(parts.get(4))));
        }
        return results;
    }

    private List<String> splitStructuredLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;
        for (char ch : line.toCharArray()) {
            if (escaping) { current.append(ch); escaping = false; continue; }
            if (ch == '\\') { escaping = true; current.append(ch); continue; }
            if (ch == '|') { parts.add(current.toString()); current.setLength(0); continue; }
            current.append(ch);
        }
        parts.add(current.toString());
        return parts;
    }

    private String unescape(String value) {
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;
        for (char ch : value.toCharArray()) {
            if (escaping) {
                builder.append(switch (ch) {
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case '|' -> '|';
                    case '\\' -> '\\';
                    default -> ch;
                });
                escaping = false;
                continue;
            }
            if (ch == '\\') { escaping = true; continue; }
            builder.append(ch);
        }
        if (escaping) builder.append('\\');
        return builder.toString();
    }

    // ── Cleanup ───────────────────────────────────────────────────────────────

    private void cleanupSafely(String name) {
        try {
            batchApi.deleteNamespacedJob(name, props.getNamespace()).execute();
        } catch (ApiException e) {
            log.warn("Could not delete job {}: {} (ttlSecondsAfterFinished will clean it up)", name, e.getMessage());
        }
        try {
            coreApi.deleteNamespacedConfigMap(name, props.getNamespace()).execute();
        } catch (ApiException e) {
            log.warn("Could not delete configmap {}: {}", name, e.getMessage());
        }
    }

    // ── Internal exception ────────────────────────────────────────────────────

    static class JudgeTimeoutException extends Exception {
        JudgeTimeoutException(String message) { super(message); }
    }
}
