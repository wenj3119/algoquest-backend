package com.algoquest.backend.judge.spec;

import java.util.List;

public record JudgeSpecData(
        String methodName,
        List<ParamSpec> params,
        String returnType,
        String outputTarget,
        ComparisonStrategy comparisonStrategy,
        ComparisonOptions comparisonOptions,
        String problemSource   // "builtin" | "ai_generated" | "user"
) {
    public JudgeSpecData(String methodName, List<ParamSpec> params, String returnType,
                         String outputTarget, ComparisonStrategy comparisonStrategy,
                         ComparisonOptions comparisonOptions) {
        this(methodName, params, returnType, outputTarget, comparisonStrategy, comparisonOptions, "builtin");
    }
}
