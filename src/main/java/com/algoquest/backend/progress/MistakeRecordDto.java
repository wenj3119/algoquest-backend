package com.algoquest.backend.progress;

public class MistakeRecordDto {

    private String id;
    private Long problemId;
    private String submitStatus;
    private String reason;
    private String note;
    private String createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public String getSubmitStatus() { return submitStatus; }
    public void setSubmitStatus(String submitStatus) { this.submitStatus = submitStatus; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
