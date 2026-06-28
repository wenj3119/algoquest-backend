package com.algoquest.backend.judge;

import com.algoquest.backend.judge.spec.JudgeSpecData;
import com.algoquest.backend.judge.spec.TestCaseData;
import java.util.List;

/**
 * Contract for executing a judge run.
 *
 * <p>Inputs are strictly limited to what the sandbox needs: the user's source code, the problem
 * spec (method signature + comparison strategy), and the test cases. No credentials, no database
 * handles, no Spring context.
 *
 * <p>Implementations may run locally (dev only), call a remote judge microservice, or dispatch to
 * a sandboxed runtime — the caller ({@link com.algoquest.backend.service.ProblemService}) is
 * unaware of which.
 */
public interface JudgeExecutor {

    /**
     * Compiles and runs {@code code} against every test case defined in {@code spec}/{@code
     * testCases} and returns a structured result.
     *
     * @param spec      problem specification (method name, param types, comparison strategy)
     * @param testCases ordered list of test cases to evaluate
     * @param code      raw Java source submitted by the user (must contain {@code public class
     *                  Solution})
     * @return judge result with per-case pass/fail details and aggregate status
     */
    JudgeResult execute(JudgeSpecData spec, List<TestCaseData> testCases, String code);
}
