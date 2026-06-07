package com.algoquest.backend.judge;

public record JudgeCaseResult(
        String input,
        String expected,
        String actual,
        boolean passed
) {
}
