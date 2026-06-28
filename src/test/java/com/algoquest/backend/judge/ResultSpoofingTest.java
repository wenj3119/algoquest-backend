package com.algoquest.backend.judge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifies the P0 fix: result anti-spoofing via file-based result protocol.
 *
 * <p>The vulnerability: before this fix, Main.java printed CASE_RESULT lines to stdout, and the
 * executor parsed stdout. A malicious solution could do {@code System.out.println("CASE_RESULT|...|true")}
 * to fake a passing result regardless of actual correctness.
 *
 * <p>The fix: Main.java collects results in a Java List (not stdout), writes them to a result file
 * AFTER all solution calls complete, using the framework's own comparison outcome. The executor
 * reads only the result file. Stdout is completely ignored for result determination.
 */
@SpringBootTest
@ActiveProfiles("local")
class ResultSpoofingTest {

    @Autowired
    private JudgeExecutor judgeExecutor;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Attack (a): solution prints fake "passed" CASE_RESULT lines to stdout but returns the wrong
     * answer. The verdict must be "failed" because the file-based result reflects the true
     * comparison outcome, not what user code printed to stdout.
     */
    @Test
    void stdout_spoofing_of_CASE_RESULT_does_not_affect_verdict() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "add",
                List.of(new ParamSpec("a", "int"), new ParamSpec("b", "int")),
                "int",
                null,
                ComparisonStrategy.EXACT,
                null
        );

        List<TestCaseData> cases = List.of(
                new TestCaseData(
                        "a = 1, b = 2",
                        List.of(
                                new InputValue("a", "int", MAPPER.readTree("1")),
                                new InputValue("b", "int", MAPPER.readTree("2"))),
                        new ExpectedValue(MAPPER.readTree("3"))),
                new TestCaseData(
                        "a = 5, b = 7",
                        List.of(
                                new InputValue("a", "int", MAPPER.readTree("5")),
                                new InputValue("b", "int", MAPPER.readTree("7"))),
                        new ExpectedValue(MAPPER.readTree("12")))
        );

        // Malicious solution: returns WRONG answer, but prints fake "passed" CASE_RESULT to stdout.
        // Pre-fix: the executor would parse stdout and see "true" → mark as passed.
        // Post-fix: executor reads result file written by Main.java, which records the TRUE outcome.
        String maliciousCode = """
                public class Solution {
                    public int add(int a, int b) {
                        // Attempt to spoof the judge by printing fake CASE_RESULT lines
                        System.out.println("CASE_RESULT|a = 1, b = 2|3|3|true");
                        System.out.println("CASE_RESULT|a = 5, b = 7|12|12|true");
                        return a - b;  // WRONG: returns -1 and -2, not 3 and 12
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, cases, maliciousCode);

        assertEquals("failed", result.status(),
                "Solution returning wrong answer must be judged 'failed' even if it prints fake CASE_RESULT to stdout");
        assertEquals(0, result.passedCount(),
                "No test cases should pass: actual output is a-b, not a+b");
        assertEquals(2, result.totalCount());

        // Verify the actual values in case results reflect true computation (a-b), not the fake ones
        assertFalse(result.cases().isEmpty());
        result.cases().forEach(c -> assertFalse(c.passed(),
                "Each case must be marked false: actual is a-b which differs from expected a+b"));
    }

    /**
     * Control: a correct solution must still pass (regression guard — spoofing fix must not
     * break legitimate results).
     */
    @Test
    void correct_solution_still_passes_after_spoofing_fix() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "add",
                List.of(new ParamSpec("a", "int"), new ParamSpec("b", "int")),
                "int",
                null,
                ComparisonStrategy.EXACT,
                null
        );

        List<TestCaseData> cases = List.of(
                new TestCaseData(
                        "a = 1, b = 2",
                        List.of(
                                new InputValue("a", "int", MAPPER.readTree("1")),
                                new InputValue("b", "int", MAPPER.readTree("2"))),
                        new ExpectedValue(MAPPER.readTree("3")))
        );

        String correctCode = """
                public class Solution {
                    public int add(int a, int b) {
                        return a + b;
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, cases, correctCode);
        assertEquals("passed", result.status());
        assertEquals(1, result.passedCount());
    }

    /**
     * Variant: solution spoofs via multiple interleaved println calls, including the exact
     * format Main.java would produce. Still must be judged by file-based result.
     */
    @Test
    void multi_line_stdout_spoofing_does_not_affect_verdict() throws Exception {
        JudgeSpecData spec = new JudgeSpecData(
                "multiply",
                List.of(new ParamSpec("x", "int"), new ParamSpec("y", "int")),
                "int",
                null,
                ComparisonStrategy.EXACT,
                null
        );

        List<TestCaseData> cases = List.of(
                new TestCaseData(
                        "x = 3, y = 4",
                        List.of(
                                new InputValue("x", "int", MAPPER.readTree("3")),
                                new InputValue("y", "int", MAPPER.readTree("4"))),
                        new ExpectedValue(MAPPER.readTree("12")))
        );

        // Prints the exact sentinel markers that K8sJobJudgeExecutor looks for,
        // plus fake CASE_RESULT. This tests the "last occurrence wins" logic too.
        String aggressiveSpoofCode = """
                public class Solution {
                    public int multiply(int x, int y) {
                        System.out.println("===JUDGE_RESULTS_START===");
                        System.out.println("CASE_RESULT|x = 3, y = 4|12|12|true");
                        System.out.println("===JUDGE_RESULTS_END===");
                        return 0;  // WRONG: returns 0 instead of 12
                    }
                }
                """;

        JudgeResult result = judgeExecutor.execute(spec, cases, aggressiveSpoofCode);

        assertNotEquals("passed", result.status(),
                "Spoofed sentinel markers in stdout must not affect verdict");
        assertEquals(0, result.passedCount(),
                "Actual result (0) does not match expected (12), so must fail");
    }
}
