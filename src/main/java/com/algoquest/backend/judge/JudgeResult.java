package com.algoquest.backend.judge;

import java.util.List;

public record JudgeResult(
        String status,
        int passedCount,
        int totalCount,
        String message,
        List<JudgeCaseResult> cases,
        String stdout,
        String stderr,
        Integer exitCode
) {
}
