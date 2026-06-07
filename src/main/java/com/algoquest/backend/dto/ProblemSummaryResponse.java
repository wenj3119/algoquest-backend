package com.algoquest.backend.dto;

import com.algoquest.backend.model.Difficulty;

public record ProblemSummaryResponse(
        Long id,
        String title,
        Difficulty difficulty,
        String category,
        String status
) {
}
