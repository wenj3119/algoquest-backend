package com.algoquest.backend.judge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class JudgeConfig {

    private static final Logger log = LoggerFactory.getLogger(JudgeConfig.class);

    /**
     * Local development executor. Activated only when profile=local (the default).
     *
     * <p>Fail-fast: if KUBERNETES_SERVICE_HOST is set (i.e. the process is running inside a K8s
     * pod), startup is aborted. This prevents accidentally deploying the insecure local executor to
     * the production cluster by forgetting to set {@code spring.profiles.active=prod}.
     */
    @Bean
    @Profile("local")
    public JudgeExecutor localJudgeExecutor() {
        String k8sHost = System.getenv("KUBERNETES_SERVICE_HOST");
        if (k8sHost != null && !k8sHost.isBlank()) {
            throw new IllegalStateException(
                    "Spring profile 'local' is active but KUBERNETES_SERVICE_HOST=" + k8sHost +
                    " indicates this process is running inside a Kubernetes cluster. " +
                    "LocalProcessJudgeExecutor must not run in production. " +
                    "Set spring.profiles.active=prod to activate K8sJobJudgeExecutor.");
        }
        log.warn("LocalProcessJudgeExecutor is active. This executor is UNSAFE for production: " +
                 "user code inherits host environment variables and has unrestricted filesystem/network access.");
        return new LocalProcessJudgeExecutor();
    }

    /**
     * Production K8s executor. Activated only when profile=prod.
     *
     * <p>Uses the official Kubernetes Java client (in-cluster config) to dynamically create one
     * Job per submission in the judge-sandbox namespace with full Pod hardening applied.
     */
    @Bean
    @Profile("prod")
    public JudgeExecutor k8sJobJudgeExecutor(K8sJudgeProperties props) throws Exception {
        return new K8sJobJudgeExecutor(props);
    }
}
