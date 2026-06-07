package com.algoquest.backend.dto;

import com.algoquest.backend.model.Difficulty;
import java.util.List;

public record ProblemDetailResponse(
        Long id,
        String title,
        Difficulty difficulty,
        String category,
        String description,
        List<ExampleResponse> examples,
        String starterCode,
        List<ProblemStepResponse> steps
) {
}
