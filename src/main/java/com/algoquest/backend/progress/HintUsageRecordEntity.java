package com.algoquest.backend.progress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "hint_usage_records")
public class HintUsageRecordEntity {

    @Id
    @Column(name = "id", length = 64)
    private String id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "submit_status", nullable = false, length = 30)
    private String submitStatus;

    @Column(name = "mistake_reason", length = 30)
    private String mistakeReason;

    @Column(name = "max_unlocked_level", nullable = false)
    private Integer maxUnlockedLevel;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public String getSubmitStatus() { return submitStatus; }
    public void setSubmitStatus(String submitStatus) { this.submitStatus = submitStatus; }

    public String getMistakeReason() { return mistakeReason; }
    public void setMistakeReason(String mistakeReason) { this.mistakeReason = mistakeReason; }

    public Integer getMaxUnlockedLevel() { return maxUnlockedLevel; }
    public void setMaxUnlockedLevel(Integer maxUnlockedLevel) { this.maxUnlockedLevel = maxUnlockedLevel; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
