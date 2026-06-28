package com.algoquest.backend.progress;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public class ProgressItemDto {

    private Long problemId;
    private String status;
    private String code;
    private JsonNode stepResults;
    private Integer currentStepIndex;
    private JsonNode lastSubmitResult;
    private String updatedAt;
    private List<MistakeRecordDto> mistakeRecords;
    private List<HintUsageRecordDto> hintUsageRecords;

    public Long getProblemId() { return problemId; }
    public void setProblemId(Long problemId) { this.problemId = problemId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public JsonNode getStepResults() { return stepResults; }
    public void setStepResults(JsonNode stepResults) { this.stepResults = stepResults; }

    public Integer getCurrentStepIndex() { return currentStepIndex; }
    public void setCurrentStepIndex(Integer currentStepIndex) { this.currentStepIndex = currentStepIndex; }

    public JsonNode getLastSubmitResult() { return lastSubmitResult; }
    public void setLastSubmitResult(JsonNode lastSubmitResult) { this.lastSubmitResult = lastSubmitResult; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public List<MistakeRecordDto> getMistakeRecords() { return mistakeRecords; }
    public void setMistakeRecords(List<MistakeRecordDto> mistakeRecords) { this.mistakeRecords = mistakeRecords; }

    public List<HintUsageRecordDto> getHintUsageRecords() { return hintUsageRecords; }
    public void setHintUsageRecords(List<HintUsageRecordDto> hintUsageRecords) { this.hintUsageRecords = hintUsageRecords; }
}
