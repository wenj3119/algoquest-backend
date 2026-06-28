package com.algoquest.backend.judge.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record InputValue(
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("value") JsonNode value
) {}
