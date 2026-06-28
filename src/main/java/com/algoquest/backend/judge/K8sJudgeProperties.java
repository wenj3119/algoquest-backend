package com.algoquest.backend.judge;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "judge.k8s")
public class K8sJudgeProperties {

    private String namespace = "judge-sandbox";
    private String image = "algoquest-judge:1.0.0";
    private int executionTimeoutSeconds = 15;
    private int jobTtlSeconds = 300;
    private int activeDeadlineSeconds = 30;
    private String cpuLimit = "1000m";
    private String memoryLimit = "256Mi";
    private String tmpfsSizeLimit = "64Mi";
    private int apiTimeoutSeconds = 30;

    public String getNamespace() { return namespace; }
    public void setNamespace(String namespace) { this.namespace = namespace; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getExecutionTimeoutSeconds() { return executionTimeoutSeconds; }
    public void setExecutionTimeoutSeconds(int executionTimeoutSeconds) { this.executionTimeoutSeconds = executionTimeoutSeconds; }

    public int getJobTtlSeconds() { return jobTtlSeconds; }
    public void setJobTtlSeconds(int jobTtlSeconds) { this.jobTtlSeconds = jobTtlSeconds; }

    public int getActiveDeadlineSeconds() { return activeDeadlineSeconds; }
    public void setActiveDeadlineSeconds(int activeDeadlineSeconds) { this.activeDeadlineSeconds = activeDeadlineSeconds; }

    public String getCpuLimit() { return cpuLimit; }
    public void setCpuLimit(String cpuLimit) { this.cpuLimit = cpuLimit; }

    public String getMemoryLimit() { return memoryLimit; }
    public void setMemoryLimit(String memoryLimit) { this.memoryLimit = memoryLimit; }

    public String getTmpfsSizeLimit() { return tmpfsSizeLimit; }
    public void setTmpfsSizeLimit(String tmpfsSizeLimit) { this.tmpfsSizeLimit = tmpfsSizeLimit; }

    public int getApiTimeoutSeconds() { return apiTimeoutSeconds; }
    public void setApiTimeoutSeconds(int apiTimeoutSeconds) { this.apiTimeoutSeconds = apiTimeoutSeconds; }
}
