package com.algoquest.backend.model;

import java.util.List;

public record Problem(
        Long id,
        String title,
        Difficulty difficulty,
        String category,
        String description,
        List<Example> examples,
        String starterCode,
        List<ProblemStep> steps
) {
}
