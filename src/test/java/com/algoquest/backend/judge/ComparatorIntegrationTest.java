package com.algoquest.backend.judge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.algoquest.backend.judge.spec.ComparisonOptions;
import com.algoquest.backend.judge.spec.ComparisonStrategy;
import com.algoquest.backend.judge.spec.ExpectedValue;
import com.algoquest.backend.judge.spec.InputValue;
import com.algoquest.backend.judge.spec.JudgeSpecData;
import com.algoquest.backend.judge.spec.ParamSpec;
import com.algoquest.backend.judge.spec.TestCaseData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ComparatorIntegrationTest {

    @Autowired
    private JudgeExecutor judgeExecutor;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // UNORDERED: identity(int[]) passthrough — same elements, different order → PASS
    @Test
    void unorderedSameMultisetShouldPass() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "identity",
                List.of(new ParamSpec("nums", "int[]")),
                "int[]",
                null,
                ComparisonStrategy.UNORDERED,
                null
        );

        TestCaseData tc = new TestCaseData(
                "nums = [10, 2, 10]",
                List.of(new InputValue("nums", "int[]", MAPPER.readTree("[10,2,10]"))),
                new ExpectedValue(MAPPER.readTree("[2,10,10]"))
        );

        String code = """
                public class Solution {
                    public int[] identity(int[] nums) {
                        return nums;
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, List.of(tc), code);
        assertEquals("passed", result.status(), "Same multiset should pass with UNORDERED comparator");
        assertEquals(1, result.passedCount());
    }

    // UNORDERED: different multiset → FAIL
    @Test
    void unorderedDifferentMultisetShouldFail() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "identity",
                List.of(new ParamSpec("nums", "int[]")),
                "int[]",
                null,
                ComparisonStrategy.UNORDERED,
                null
        );

        TestCaseData tc = new TestCaseData(
                "nums = [2, 10]",
                List.of(new InputValue("nums", "int[]", MAPPER.readTree("[2,10]"))),
                new ExpectedValue(MAPPER.readTree("[2,2]"))
        );

        String code = """
                public class Solution {
                    public int[] identity(int[] nums) {
                        return nums;
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, List.of(tc), code);
        assertEquals("failed", result.status(), "Different multiset should fail with UNORDERED comparator");
        assertEquals(0, result.passedCount());
    }

    // UNORDERED: multi-digit numbers must sort numerically not lexicographically
    // [10, 2, 9] sorted numerically = [2, 9, 10], sorted lexicographically = [10, 2, 9]
    @Test
    void unorderedMultiDigitNumericSortShouldPass() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "identity",
                List.of(new ParamSpec("nums", "int[]")),
                "int[]",
                null,
                ComparisonStrategy.UNORDERED,
                null
        );

        // actual returns [10, 2, 9], expected is [9, 10, 2] — same multiset, numeric sort must match
        TestCaseData tc = new TestCaseData(
                "nums = [10, 2, 9]",
                List.of(new InputValue("nums", "int[]", MAPPER.readTree("[10,2,9]"))),
                new ExpectedValue(MAPPER.readTree("[9,10,2]"))
        );

        String code = """
                public class Solution {
                    public int[] identity(int[] nums) {
                        return nums;
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, List.of(tc), code);
        assertEquals("passed", result.status(), "Multi-digit multiset should pass with numeric UNORDERED sort");
    }

    // FLOAT_TOLERANCE: 1.0/3.0 vs 0.333333 within epsilon=1e-6 → PASS
    @Test
    void floatToleranceWithinEpsilonShouldPass() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "compute",
                List.of(new ParamSpec("a", "double"), new ParamSpec("b", "double")),
                "double",
                null,
                ComparisonStrategy.FLOAT_TOLERANCE,
                new ComparisonOptions(1e-6)
        );

        TestCaseData tc = new TestCaseData(
                "a = 1.0, b = 3.0",
                List.of(
                        new InputValue("a", "double", MAPPER.readTree("1.0")),
                        new InputValue("b", "double", MAPPER.readTree("3.0"))
                ),
                new ExpectedValue(MAPPER.readTree("0.333333"))
        );

        String code = """
                public class Solution {
                    public double compute(double a, double b) {
                        return a / b;
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, List.of(tc), code);
        assertEquals("passed", result.status(), "1.0/3.0 ≈ 0.333333 within 1e-6 should pass");
    }

    // FLOAT_TOLERANCE: 0.5 vs 0.3 with epsilon=1e-6 → FAIL
    @Test
    void floatToleranceOutsideEpsilonShouldFail() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "compute",
                List.of(new ParamSpec("a", "double"), new ParamSpec("b", "double")),
                "double",
                null,
                ComparisonStrategy.FLOAT_TOLERANCE,
                new ComparisonOptions(1e-6)
        );

        TestCaseData tc = new TestCaseData(
                "a = 1.0, b = 2.0",
                List.of(
                        new InputValue("a", "double", MAPPER.readTree("1.0")),
                        new InputValue("b", "double", MAPPER.readTree("2.0"))
                ),
                new ExpectedValue(MAPPER.readTree("0.3"))
        );

        String code = """
                public class Solution {
                    public double compute(double a, double b) {
                        return a / b;
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, List.of(tc), code);
        assertEquals("failed", result.status(), "0.5 vs 0.3 outside 1e-6 should fail");
    }
}
