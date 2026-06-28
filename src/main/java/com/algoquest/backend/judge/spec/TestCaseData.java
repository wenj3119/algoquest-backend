package com.algoquest.backend.judge.spec;

import java.util.List;

public record TestCaseData(
        String displayInput,
        List<InputValue> inputs,
        ExpectedValue expected
) {}
