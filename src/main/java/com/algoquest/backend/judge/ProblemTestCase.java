package com.algoquest.backend.judge;

public record ProblemTestCase(
        String input,
        String expected,
        String setupCode,
        String executionCode,
        String comparisonCode
) {
    public ProblemTestCase(String input, String expected, String setupCode, String executionCode) {
        this(input, expected, setupCode, executionCode, null);
    }
}
