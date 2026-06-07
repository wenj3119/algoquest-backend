package com.algoquest.backend.model;

import java.util.List;

public record ProblemStep(
        String id,
        String title,
        String content,
        String type,
        List<StepOption> options,
        String answer,
        String explanation
) {
}
