package com.algoquest.backend.judge.spec;

import com.algoquest.backend.db.entity.JudgeSpecEntity;
import com.algoquest.backend.db.entity.TestCaseEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public final class JudgeSpecConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JudgeSpecConverter() {}

    public static JudgeSpecData toSpecData(JudgeSpecEntity entity, String problemSource) {
        try {
            List<ParamSpec> params = MAPPER.readValue(entity.getParams(), new TypeReference<>() {});
            ComparisonStrategy strategy = ComparisonStrategy.valueOf(entity.getComparisonStrategy());
            ComparisonOptions options = null;
            if (entity.getComparisonOptions() != null) {
                options = MAPPER.readValue(entity.getComparisonOptions(), ComparisonOptions.class);
            }
            return new JudgeSpecData(
                    entity.getMethodName(),
                    params,
                    entity.getReturnType(),
                    entity.getOutputTarget(),
                    strategy,
                    options,
                    problemSource
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse judge spec for problem " + entity.getProblemId(), e);
        }
    }

    public static JudgeSpecData toSpecData(JudgeSpecEntity entity) {
        return toSpecData(entity, "builtin");
    }

    public static TestCaseData toTestCaseData(TestCaseEntity entity) {
        try {
            List<InputValue> inputs = MAPPER.readValue(entity.getInputs(), new TypeReference<>() {});
            ExpectedValue expected = MAPPER.readValue(entity.getExpected(), ExpectedValue.class);
            return new TestCaseData(entity.getDisplayInput(), inputs, expected);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse test case " + entity.getId(), e);
        }
    }
}
