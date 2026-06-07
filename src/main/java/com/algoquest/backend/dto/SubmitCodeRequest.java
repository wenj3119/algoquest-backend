package com.algoquest.backend.dto;

public record SubmitCodeRequest(
        String language,
        String code
) {
}
