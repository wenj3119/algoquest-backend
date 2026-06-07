package com.algoquest.backend.dto;

import java.util.List;

public record ProblemStepResponse(
        String id,
        String title,
        String content,
        String type,
        List<StepOptionResponse> options,
        String answer,
        String explanation
) {
}
