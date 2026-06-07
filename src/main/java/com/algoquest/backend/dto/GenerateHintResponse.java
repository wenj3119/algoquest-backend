package com.algoquest.backend.dto;

import java.util.List;

public record GenerateHintResponse(
        String mode,
        List<HintItemResponse> hints
) {
}
