package com.algoquest.backend.judge.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComparisonOptions(
        @JsonProperty("epsilon") Double epsilon
) {}
