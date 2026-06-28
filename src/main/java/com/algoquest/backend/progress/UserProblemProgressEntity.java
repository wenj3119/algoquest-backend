package com.algoquest.backend.progress;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_problem_progress")
@IdClass(UserProblemProgressId.class)
public class UserProblemProgressEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "code", nullable = false, columnDefinition = "text")
    private String code;

    @Column(name = "step_results", nullable = false, columnDefinition = "text")
    private String stepResults;

    @Column(name = "current_step_index", nullable = false)
    private Integer currentStepIndex;

    @Column(name = "last_submit_result", columnDefinition = "text")
    private String lastSubmitResult;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getStepResults() { return stepResults; }
    public void setStepResults(String stepResults) { this.stepResults = stepResults; }

    public Integer getCurrentStepIndex() { return currentStepIndex; }
    public void setCurrentStepIndex(Integer currentStepIndex) { this.currentStepIndex = currentStepIndex; }

    public String getLastSubmitResult() { return lastSubmitResult; }
    public void setLastSubmitResult(String lastSubmitResult) { this.lastSubmitResult = lastSubmitResult; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
