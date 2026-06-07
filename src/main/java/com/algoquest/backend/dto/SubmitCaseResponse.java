package com.algoquest.backend.dto;

public record SubmitCaseResponse(
        String input,
        String expected,
        String actual,
        boolean passed
) {
}
