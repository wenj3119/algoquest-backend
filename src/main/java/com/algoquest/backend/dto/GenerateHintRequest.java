package com.algoquest.backend.dto;

public record GenerateHintRequest(
        String code,
        String submitStatus,
        String mistakeReason,
        String note,
        SubmitCodeResponse lastSubmitResult
) {
}
