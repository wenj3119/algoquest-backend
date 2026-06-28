package com.algoquest.backend.judge.spec;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record ExpectedValue(
        @JsonProperty("value") JsonNode value
) {}
