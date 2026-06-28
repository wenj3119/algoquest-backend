package com.algoquest.backend.judge.spec;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParamSpec(
        @JsonProperty("name") String name,
        @JsonProperty("type") String type
) {}
