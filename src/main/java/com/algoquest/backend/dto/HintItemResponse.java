package com.algoquest.backend.dto;

public record HintItemResponse(
        int level,
        String title,
        String content
) {
}
