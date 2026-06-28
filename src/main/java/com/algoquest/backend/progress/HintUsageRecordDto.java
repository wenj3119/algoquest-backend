package com.algoquest.backend.progress;

public class HintUsageRecordDto {

    private String id;
    private Long problemId;
    private String submitStatus;
    private String mistakeReason;
    private Integer maxUnlockedLevel;
    private String createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public String getSubmitStatus() { return submitStatus; }
    public void setSubmitStatus(String submitStatus) { this.submitStatus = submitStatus; }

    public String getMistakeReason() { return mistakeReason; }
    public void setMistakeReason(String mistakeReason) { this.mistakeReason = mistakeReason; }

    public Integer getMaxUnlockedLevel() { return maxUnlockedLevel; }
    public void setMaxUnlockedLevel(Integer maxUnlockedLevel) { this.maxUnlockedLevel = maxUnlockedLevel; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
