package com.algoquest.backend.judge;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;

@Service
public class JavaJudgeService {

    private static final Duration EXECUTION_TIMEOUT = Duration.ofSeconds(10);
    private static final int RUNTIME_ERROR_EXIT_CODE = 2;

    public JudgeResult judge(long problemId, String code) {
        ProblemJudgeConfig config = ProblemJudgeConfig.forProblem(problemId);
        if (config == null) {
            return new JudgeResult(
                    "judge_not_supported",
                    0,
                    0,
                    "当前题目暂未接入真实判题",
                    List.of(),
                    "",
                    "",
                    null
            );
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("algoquest-judge-");
            Files.writeString(tempDir.resolve("Solution.java"), code, StandardCharsets.UTF_8);
            Files.writeString(tempDir.resolve("Main.java"), JavaMainGenerator.generate(config), StandardCharsets.UTF_8);

            ProcessExecutionResult compileResult = runProcess(
                    tempDir,
                    List.of(javacCommand(), "Solution.java", "Main.java"),
                    EXECUTION_TIMEOUT
            );

            if (compileResult.timedOut()) {
                return new JudgeResult(
                        "timeout",
                        0,
                        0,
                        "编译超时，请检查代码复杂度或死循环问题。",
                        List.of(),
                        compileResult.stdout(),
                        compileResult.stderr(),
                        null
                );
            }

            if (compileResult.exitCode() != 0) {
                return new JudgeResult(
                        "compile_error",
                        0,
                        0,
                        buildCompileMessage(compileResult.stderr(), compileResult.stdout()),
                        List.of(),
                        compileResult.stdout(),
                        compileResult.stderr(),
                        compileResult.exitCode()
                );
            }

            ProcessExecutionResult runResult = runProcess(
                    tempDir,
                    List.of(javaCommand(), "Main"),
                    EXECUTION_TIMEOUT
            );

            List<JudgeCaseResult> cases = parseCases(runResult.stdout());
            int passedCount = (int) cases.stream().filter(JudgeCaseResult::passed).count();

            if (runResult.timedOut()) {
                return new JudgeResult(
                        "timeout",
                        passedCount,
                        cases.size(),
                        "运行超时，请检查是否存在死循环或高复杂度逻辑。",
                        cases,
                        runResult.stdout(),
                        runResult.stderr(),
                        null
                );
            }

            if (runResult.exitCode() != 0) {
                String status = runResult.exitCode() == RUNTIME_ERROR_EXIT_CODE ? "runtime_error" : "runtime_error";
                return new JudgeResult(
                        status,
                        passedCount,
                        cases.size(),
                        buildRuntimeMessage(runResult.stderr(), runResult.stdout()),
                        cases,
                        runResult.stdout(),
                        runResult.stderr(),
                        runResult.exitCode()
                );
            }

            String status = passedCount == cases.size() ? "passed" : "failed";
            String message = passedCount == cases.size() ? "全部测试用例通过。" : "部分测试用例未通过。";
            return new JudgeResult(
                    status,
                    passedCount,
                    cases.size(),
                    message,
                    cases,
                    runResult.stdout(),
                    runResult.stderr(),
                    runResult.exitCode()
            );
        } catch (IOException exception) {
            return new JudgeResult(
                    "runtime_error",
                    0,
                    0,
                    "判题系统执行失败: " + exception.getMessage(),
                    List.of(),
                    "",
                    exception.toString(),
                    null
            );
        } finally {
            if (tempDir != null) {
                deleteDirectoryQuietly(tempDir);
            }
        }
    }

    private ProcessExecutionResult runProcess(Path directory, List<String> command, Duration timeout) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(directory.toFile());
        processBuilder.redirectErrorStream(false);
        processBuilder.environment().remove("JAVA_TOOL_OPTIONS");
        Process process = processBuilder.start();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> stdoutFuture = executor.submit(() -> readAll(process.getInputStream()));
            Future<String> stderrFuture = executor.submit(() -> readAll(process.getErrorStream()));

            boolean finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                process.waitFor();
                return new ProcessExecutionResult(null, getFutureValue(stdoutFuture), getFutureValue(stderrFuture), true);
            }

            return new ProcessExecutionResult(
                    process.exitValue(),
                    getFutureValue(stdoutFuture),
                    getFutureValue(stderrFuture),
                    false
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            return new ProcessExecutionResult(null, "", "Judge interrupted.", true);
        }
    }

    private List<JudgeCaseResult> parseCases(String stdout) {
        List<JudgeCaseResult> results = new ArrayList<>();
        for (String line : stdout.split("\\R")) {
            if (!line.startsWith("CASE_RESULT|")) {
                continue;
            }

            List<String> parts = splitStructuredLine(line);
            if (parts.size() != 5) {
                continue;
            }

            results.add(new JudgeCaseResult(
                    unescape(parts.get(1)),
                    unescape(parts.get(2)),
                    unescape(parts.get(3)),
                    Boolean.parseBoolean(parts.get(4))
            ));
        }
        return results;
    }

    private List<String> splitStructuredLine(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaping = false;

        for (char ch : line.toCharArray()) {
            if (escaping) {
                current.append(ch);
                escaping = false;
                continue;
            }

            if (ch == '\\') {
                escaping = true;
                current.append(ch);
                continue;
            }

            if (ch == '|') {
                parts.add(current.toString());
                current.setLength(0);
                continue;
            }

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

            if (ch == '\\') {
                escaping = true;
                continue;
            }

            builder.append(ch);
        }

        if (escaping) {
            builder.append('\\');
        }

        return builder.toString();
    }

    private String buildCompileMessage(String stderr, String stdout) {
        String details = firstNonBlank(stderr, stdout, "编译失败，请检查 Java 语法、方法签名或类定义。");
        return "编译失败: " + details;
    }

    private String buildRuntimeMessage(String stderr, String stdout) {
        String details = firstNonBlank(stderr, stdout, "运行时异常，请检查边界条件和数组访问。");
        return "运行失败: " + details;
    }

    private String firstNonBlank(String first, String second, String fallback) {
        if (first != null && !first.isBlank()) {
            return first.strip();
        }
        if (second != null && !second.isBlank()) {
            return second.strip();
        }
        return fallback;
    }

    private String javacCommand() {
        return javaBinary("javac");
    }

    private String javaCommand() {
        return javaBinary("java");
    }

    private String javaBinary(String command) {
        String executable = isWindows() ? command + ".exe" : command;
        return Path.of(System.getProperty("java.home"), "bin", executable).toString();
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private String readAll(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String getFutureValue(Future<String> future) {
        try {
            return future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return "";
        } catch (ExecutionException | TimeoutException exception) {
            return "";
        }
    }

    private void deleteDirectoryQuietly(Path directory) {
        try (var paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    private record ProcessExecutionResult(
            Integer exitCode,
            String stdout,
            String stderr,
            boolean timedOut
    ) {
    }
}
