package com.algoquest.backend.dto;

import java.util.List;

public record SubmitCodeResponse(
        String status,
        int passedCount,
        int totalCount,
        String message,
        List<SubmitCaseResponse> cases
) {
}
