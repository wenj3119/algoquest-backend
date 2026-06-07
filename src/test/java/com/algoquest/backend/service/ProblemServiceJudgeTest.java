package com.algoquest.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.algoquest.backend.dto.GenerateHintRequest;
import com.algoquest.backend.dto.GenerateHintResponse;
import com.algoquest.backend.dto.SubmitCaseResponse;
import com.algoquest.backend.dto.SubmitCodeRequest;
import com.algoquest.backend.dto.SubmitCodeResponse;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProblemServiceJudgeTest {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private HintService hintService;

    @Test
    void shouldPassForAllBuiltInProblemsWithCorrectCode() {
        Map<Long, String> solutions = new LinkedHashMap<>();
        solutions.put(1L, """
                public class Solution {
                    public boolean isPrime(int n) {
                        if (n <= 1) {
                            return false;
                        }
                        for (int i = 2; i * i <= n; i++) {
                            if (n % i == 0) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                """);
        solutions.put(2L, """
                public class Solution {
                    public String printTriangle(int n) {
                        StringBuilder builder = new StringBuilder();
                        for (int i = 1; i <= n; i++) {
                            builder.append("#".repeat(i));
                            if (i < n) {
                                builder.append("\\n");
                            }
                        }
                        return builder.toString();
                    }
                }
                """);
        solutions.put(3L, """
                import java.util.*;

                public class Solution {
                    public List<List<Integer>> generate(int numRows) {
                        List<List<Integer>> result = new ArrayList<>();
                        for (int row = 0; row < numRows; row++) {
                            List<Integer> current = new ArrayList<>();
                            for (int col = 0; col <= row; col++) {
                                if (col == 0 || col == row) {
                                    current.add(1);
                                } else {
                                    current.add(result.get(row - 1).get(col - 1) + result.get(row - 1).get(col));
                                }
                            }
                            result.add(current);
                        }
                        return result;
                    }
                }
                """);
        solutions.put(4L, """
                public class Solution {
                    public int maxValue(int[] nums) {
                        int max = nums[0];
                        for (int num : nums) {
                            if (num > max) {
                                max = num;
                            }
                        }
                        return max;
                    }
                }
                """);
        solutions.put(5L, """
                public class Solution {
                    public void reverse(int[] nums) {
                        int left = 0;
                        int right = nums.length - 1;
                        while (left < right) {
                            int temp = nums[left];
                            nums[left] = nums[right];
                            nums[right] = temp;
                            left++;
                            right--;
                        }
                    }
                }
                """);
        solutions.put(6L, """
                public class Solution {
                    public void moveZeroes(int[] nums) {
                        int index = 0;
                        for (int num : nums) {
                            if (num != 0) {
                                nums[index++] = num;
                            }
                        }
                        while (index < nums.length) {
                            nums[index++] = 0;
                        }
                    }
                }
                """);
        solutions.put(7L, """
                import java.util.*;

                public class Solution {
                    public int[] twoSum(int[] nums, int target) {
                        Map<Integer, Integer> indexMap = new HashMap<>();
                        for (int i = 0; i < nums.length; i++) {
                            int need = target - nums[i];
                            if (indexMap.containsKey(need)) {
                                return new int[]{indexMap.get(need), i};
                            }
                            indexMap.put(nums[i], i);
                        }
                        return new int[0];
                    }
                }
                """);
        solutions.put(8L, """
                import java.util.*;

                public class Solution {
                    public boolean isValid(String s) {
                        Map<Character, Character> pairs = new HashMap<>();
                        pairs.put(')', '(');
                        pairs.put(']', '[');
                        pairs.put('}', '{');
                        Deque<Character> stack = new ArrayDeque<>();
                        for (char ch : s.toCharArray()) {
                            if (pairs.containsValue(ch)) {
                                stack.push(ch);
                            } else if (pairs.containsKey(ch)) {
                                if (stack.isEmpty() || stack.pop() != pairs.get(ch)) {
                                    return false;
                                }
                            }
                        }
                        return stack.isEmpty();
                    }
                }
                """);
        solutions.put(9L, """
                public class Solution {
                    public int numSubarrayProductLessThanK(int[] nums, int k) {
                        if (k <= 1) {
                            return 0;
                        }
                        int left = 0;
                        int product = 1;
                        int count = 0;
                        for (int right = 0; right < nums.length; right++) {
                            product *= nums[right];
                            while (product >= k) {
                                product /= nums[left++];
                            }
                            count += right - left + 1;
                        }
                        return count;
                    }
                }
                """);
        solutions.put(10L, """
                import java.util.*;

                public class Solution {
                    public int lengthOfLongestSubstring(String s) {
                        Map<Character, Integer> indexMap = new HashMap<>();
                        int left = 0;
                        int best = 0;
                        for (int right = 0; right < s.length(); right++) {
                            char ch = s.charAt(right);
                            if (indexMap.containsKey(ch)) {
                                left = Math.max(left, indexMap.get(ch) + 1);
                            }
                            indexMap.put(ch, right);
                            best = Math.max(best, right - left + 1);
                        }
                        return best;
                    }
                }
                """);

        for (Map.Entry<Long, String> entry : solutions.entrySet()) {
            SubmitCodeResponse response = problemService.submitCode(entry.getKey(), new SubmitCodeRequest("java", entry.getValue()));
            assertEquals("passed", response.status(), "problem " + entry.getKey());
            assertTrue(response.passedCount() > 0, "problem " + entry.getKey());
            assertEquals(response.totalCount(), response.passedCount(), "problem " + entry.getKey());
        }
    }

    @Test
    void shouldReturnFailedForWrongPrimeLogic() {
        SubmitCodeResponse response = problemService.submitCode(1L, new SubmitCodeRequest("java", """
                public class Solution {
                    public boolean isPrime(int n) {
                        return n > 1;
                    }
                }
                """));

        assertEquals("failed", response.status());
        assertTrue(response.passedCount() < response.totalCount());
        assertTrue(response.cases().stream().anyMatch(caseResult -> !caseResult.passed()));
    }

    @Test
    void shouldReturnCompileErrorForSyntaxError() {
        SubmitCodeResponse response = problemService.submitCode(1L, new SubmitCodeRequest("java", """
                public class Solution {
                    public boolean isPrime(int n) {
                        return ;
                    }
                }
                """));

        assertEquals("compile_error", response.status());
        assertTrue(response.message().contains("编译失败"));
    }

    @Test
    void shouldRejectUnsupportedLanguage() {
        SubmitCodeResponse response = problemService.submitCode(1L, new SubmitCodeRequest("python", "print(1)"));

        assertEquals("failed", response.status());
        assertTrue(response.message().contains("仅支持 Java"));
    }

    @Test
    void shouldRejectEmptyCode() {
        SubmitCodeResponse response = problemService.submitCode(1L, new SubmitCodeRequest("java", "   "));

        assertEquals("failed", response.status());
        assertTrue(response.message().contains("代码不能为空"));
    }

    @Test
    void shouldRejectCodeWithoutSolutionClass() {
        SubmitCodeResponse response = problemService.submitCode(1L, new SubmitCodeRequest("java", """
                public class Demo {
                    public boolean isPrime(int n) {
                        return true;
                    }
                }
                """));

        assertEquals("compile_error", response.status());
        assertTrue(response.message().contains("public class Solution"));
        assertFalse(response.message().isBlank());
    }

    @Test
    void shouldReturnPrimeHints() {
        GenerateHintResponse response = hintService.generateHints(1L, new GenerateHintRequest(
                "public class Solution { public boolean isPrime(int n) { return n > 1; } }",
                "failed",
                "key_condition",
                "while 条件没想清楚",
                null
        ));

        assertHintResponse(response);
        assertTrue(response.hints().get(0).content().contains("n < 2") || response.hints().get(0).content().contains("质数"));
        assertTrue(response.hints().get(1).content().contains("n % i == 0"));
    }

    @Test
    void shouldReturnTwoSumHints() {
        GenerateHintResponse response = hintService.generateHints(7L, new GenerateHintRequest(
                "",
                "failed",
                "variable_design",
                null,
                null
        ));

        assertHintResponse(response);
        assertTrue(response.hints().get(0).content().contains("下标"));
        assertTrue(response.hints().get(1).content().contains("HashMap") || response.hints().get(1).content().contains("map"));
    }

    @Test
    void shouldReturnSlidingWindowHints() {
        SubmitCodeResponse lastSubmitResult = new SubmitCodeResponse(
                "failed",
                0,
                3,
                "部分测试用例未通过。",
                List.of(new SubmitCaseResponse("nums=[10, 5, 2, 6], k=100", "8", "4", false))
        );

        GenerateHintResponse response = hintService.generateHints(9L, new GenerateHintRequest(
                "",
                "failed",
                "key_condition",
                null,
                lastSubmitResult
        ));

        assertHintResponse(response);
        assertTrue(response.hints().get(0).content().contains("连续子数组个数"));
        assertTrue(response.hints().get(1).content().contains("product >= k"));
        assertTrue(response.hints().get(2).content().contains("right - left + 1"));
        assertTrue(response.hints().stream().anyMatch(hint -> hint.content().contains("input=nums=[10, 5, 2, 6], k=100")));
    }

    @Test
    void shouldReturnGenericHintsForOtherProblems() {
        GenerateHintResponse response = hintService.generateHints(2L, new GenerateHintRequest(
                "",
                "failed",
                "answer_update",
                null,
                null
        ));

        assertHintResponse(response);
        assertTrue(response.hints().get(2).content().contains("答案更新"));
    }

    @Test
    void shouldIncludeFailedCaseSummaryInPrimeHints() {
        GenerateHintResponse response = hintService.generateHints(1L, new GenerateHintRequest(
                "",
                "failed",
                "key_condition",
                null,
                new SubmitCodeResponse(
                        "failed",
                        3,
                        4,
                        "部分测试用例未通过。",
                        List.of(new SubmitCaseResponse("n=9", "false", "true", false))
                )
        ));

        assertHintResponse(response);
        assertTrue(response.hints().stream().anyMatch(hint -> hint.content().contains("input=n=9")));
        assertTrue(response.hints().get(0).content().contains("大于 1，但能被其他数整除"));
    }

    @Test
    void shouldReturnCompileErrorHints() {
        GenerateHintResponse response = hintService.generateHints(1L, new GenerateHintRequest(
                "",
                "compile_error",
                "java_syntax",
                null,
                new SubmitCodeResponse(
                        "compile_error",
                        0,
                        0,
                        "cannot find symbol\nsymbol:   variable prime\nlocation: class Solution",
                        List.of()
                )
        ));

        assertHintResponse(response);
        assertTrue(response.hints().get(0).content().contains("javac 编译错误第一行"));
        assertTrue(response.hints().get(1).content().contains("public class Solution"));
        assertTrue(response.hints().get(2).content().contains("方法名、变量名或 import"));
    }

    private void assertHintResponse(GenerateHintResponse response) {
        assertNotNull(response);
        assertEquals("mock", response.mode());
        assertNotNull(response.hints());
        assertEquals(3, response.hints().size());
        assertEquals(1, response.hints().get(0).level());
        assertEquals(2, response.hints().get(1).level());
        assertEquals(3, response.hints().get(2).level());
        assertTrue(response.hints().stream().allMatch(hint -> !hint.content().isBlank()));
    }
}
