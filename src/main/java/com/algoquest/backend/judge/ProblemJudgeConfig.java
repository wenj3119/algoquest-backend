package com.algoquest.backend.judge;

import java.util.List;

public record ProblemJudgeConfig(
        long problemId,
        String methodName,
        List<ProblemTestCase> testCases
) {

    public static ProblemJudgeConfig forProblem(long problemId) {
        return switch ((int) problemId) {
            case 1 -> new ProblemJudgeConfig(
                    1L,
                    "isPrime",
                    List.of(
                            new ProblemTestCase("n = 2", "true", "int n = 2;", "String actual = String.valueOf(solution.isPrime(n));"),
                            new ProblemTestCase("n = 9", "false", "int n = 9;", "String actual = String.valueOf(solution.isPrime(n));"),
                            new ProblemTestCase("n = 29", "true", "int n = 29;", "String actual = String.valueOf(solution.isPrime(n));"),
                            new ProblemTestCase("n = 1", "false", "int n = 1;", "String actual = String.valueOf(solution.isPrime(n));")
                    )
            );
            case 2 -> new ProblemJudgeConfig(
                    2L,
                    "printTriangle",
                    List.of(
                            new ProblemTestCase("n = 1", "#", "int n = 1;", "String actual = solution.printTriangle(n);", "normalizeText(actual).equals(normalizeText(\"#\"))"),
                            new ProblemTestCase("n = 3", "#\n##\n###", "int n = 3;", "String actual = solution.printTriangle(n);", "normalizeText(actual).equals(normalizeText(\"#\\n##\\n###\"))"),
                            new ProblemTestCase("n = 5", "#\n##\n###\n####\n#####", "int n = 5;", "String actual = solution.printTriangle(n);", "normalizeText(actual).equals(normalizeText(\"#\\n##\\n###\\n####\\n#####\"))")
                    )
            );
            case 3 -> new ProblemJudgeConfig(
                    3L,
                    "generate",
                    List.of(
                            new ProblemTestCase("numRows = 1", "[[1]]", "int numRows = 1;", "String actual = String.valueOf(solution.generate(numRows));"),
                            new ProblemTestCase("numRows = 3", "[[1], [1, 1], [1, 2, 1]]", "int numRows = 3;", "String actual = String.valueOf(solution.generate(numRows));"),
                            new ProblemTestCase("numRows = 5", "[[1], [1, 1], [1, 2, 1], [1, 3, 3, 1], [1, 4, 6, 4, 1]]", "int numRows = 5;", "String actual = String.valueOf(solution.generate(numRows));")
                    )
            );
            case 4 -> new ProblemJudgeConfig(
                    4L,
                    "maxValue",
                    List.of(
                            new ProblemTestCase("nums = [3, 1, 5, 2]", "5", "int[] nums = new int[]{3, 1, 5, 2};", "String actual = String.valueOf(solution.maxValue(nums));"),
                            new ProblemTestCase("nums = [-7, -3, -9]", "-3", "int[] nums = new int[]{-7, -3, -9};", "String actual = String.valueOf(solution.maxValue(nums));"),
                            new ProblemTestCase("nums = [8]", "8", "int[] nums = new int[]{8};", "String actual = String.valueOf(solution.maxValue(nums));")
                    )
            );
            case 5 -> new ProblemJudgeConfig(
                    5L,
                    "reverse",
                    List.of(
                            new ProblemTestCase("nums = [1, 2, 3, 4]", "[4, 3, 2, 1]", "int[] nums = new int[]{1, 2, 3, 4};", "solution.reverse(nums);\nString actual = Arrays.toString(nums);"),
                            new ProblemTestCase("nums = [5, 6, 7]", "[7, 6, 5]", "int[] nums = new int[]{5, 6, 7};", "solution.reverse(nums);\nString actual = Arrays.toString(nums);"),
                            new ProblemTestCase("nums = [9]", "[9]", "int[] nums = new int[]{9};", "solution.reverse(nums);\nString actual = Arrays.toString(nums);")
                    )
            );
            case 6 -> new ProblemJudgeConfig(
                    6L,
                    "moveZeroes",
                    List.of(
                            new ProblemTestCase("nums = [0, 1, 0, 3, 12]", "[1, 3, 12, 0, 0]", "int[] nums = new int[]{0, 1, 0, 3, 12};", "solution.moveZeroes(nums);\nString actual = Arrays.toString(nums);"),
                            new ProblemTestCase("nums = [0, 0, 1]", "[1, 0, 0]", "int[] nums = new int[]{0, 0, 1};", "solution.moveZeroes(nums);\nString actual = Arrays.toString(nums);"),
                            new ProblemTestCase("nums = [4, 5, 6]", "[4, 5, 6]", "int[] nums = new int[]{4, 5, 6};", "solution.moveZeroes(nums);\nString actual = Arrays.toString(nums);")
                    )
            );
            case 7 -> new ProblemJudgeConfig(
                    7L,
                    "twoSum",
                    List.of(
                            new ProblemTestCase("nums = [2, 7, 11, 15], target = 9", "[0, 1]", "int[] nums = new int[]{2, 7, 11, 15};\nint target = 9;", "String actual = Arrays.toString(solution.twoSum(nums, target));"),
                            new ProblemTestCase("nums = [3, 2, 4], target = 6", "[1, 2]", "int[] nums = new int[]{3, 2, 4};\nint target = 6;", "String actual = Arrays.toString(solution.twoSum(nums, target));"),
                            new ProblemTestCase("nums = [3, 3], target = 6", "[0, 1]", "int[] nums = new int[]{3, 3};\nint target = 6;", "String actual = Arrays.toString(solution.twoSum(nums, target));")
                    )
            );
            case 8 -> new ProblemJudgeConfig(
                    8L,
                    "isValid",
                    List.of(
                            new ProblemTestCase("s = \"()\"", "true", "String s = \"()\";", "String actual = String.valueOf(solution.isValid(s));"),
                            new ProblemTestCase("s = \"()[]{}\"", "true", "String s = \"()[]{}\";", "String actual = String.valueOf(solution.isValid(s));"),
                            new ProblemTestCase("s = \"(]\"", "false", "String s = \"(]\";", "String actual = String.valueOf(solution.isValid(s));"),
                            new ProblemTestCase("s = \"([)]\"", "false", "String s = \"([)]\";", "String actual = String.valueOf(solution.isValid(s));"),
                            new ProblemTestCase("s = \"{[]}\"", "true", "String s = \"{[]}\";", "String actual = String.valueOf(solution.isValid(s));")
                    )
            );
            case 9 -> new ProblemJudgeConfig(
                    9L,
                    "numSubarrayProductLessThanK",
                    List.of(
                            new ProblemTestCase("nums = [10, 5, 2, 6], k = 100", "8", "int[] nums = new int[]{10, 5, 2, 6};\nint k = 100;", "String actual = String.valueOf(solution.numSubarrayProductLessThanK(nums, k));"),
                            new ProblemTestCase("nums = [1, 2, 3], k = 0", "0", "int[] nums = new int[]{1, 2, 3};\nint k = 0;", "String actual = String.valueOf(solution.numSubarrayProductLessThanK(nums, k));"),
                            new ProblemTestCase("nums = [1, 1, 1], k = 2", "6", "int[] nums = new int[]{1, 1, 1};\nint k = 2;", "String actual = String.valueOf(solution.numSubarrayProductLessThanK(nums, k));")
                    )
            );
            case 10 -> new ProblemJudgeConfig(
                    10L,
                    "lengthOfLongestSubstring",
                    List.of(
                            new ProblemTestCase("s = \"abcabcbb\"", "3", "String s = \"abcabcbb\";", "String actual = String.valueOf(solution.lengthOfLongestSubstring(s));"),
                            new ProblemTestCase("s = \"bbbbb\"", "1", "String s = \"bbbbb\";", "String actual = String.valueOf(solution.lengthOfLongestSubstring(s));"),
                            new ProblemTestCase("s = \"pwwkew\"", "3", "String s = \"pwwkew\";", "String actual = String.valueOf(solution.lengthOfLongestSubstring(s));"),
                            new ProblemTestCase("s = \"\"", "0", "String s = \"\";", "String actual = String.valueOf(solution.lengthOfLongestSubstring(s));"),
                            new ProblemTestCase("s = \"dvdf\"", "3", "String s = \"dvdf\";", "String actual = String.valueOf(solution.lengthOfLongestSubstring(s));")
                    )
            );
            default -> null;
        };
    }
}
