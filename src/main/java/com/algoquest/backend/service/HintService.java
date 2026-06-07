package com.algoquest.backend.service;

import com.algoquest.backend.dto.GenerateHintRequest;
import com.algoquest.backend.dto.GenerateHintResponse;
import com.algoquest.backend.dto.HintItemResponse;
import com.algoquest.backend.dto.SubmitCodeResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class HintService {

    public GenerateHintResponse generateHints(Long problemId, GenerateHintRequest request) {
        String submitStatus = normalize(request == null ? null : request.submitStatus());
        String mistakeReason = normalize(request == null ? null : request.mistakeReason());
        FailedCaseContext failedCase = extractFirstFailedCase(request).orElse(null);

        if ("compile_error".equals(submitStatus)) {
            return new GenerateHintResponse("mock", compileErrorHints(problemId, request));
        }

        List<HintItemResponse> hints = switch (problemId.intValue()) {
            case 1 -> primeHints(mistakeReason, failedCase);
            case 7 -> twoSumHints(mistakeReason, failedCase);
            case 9 -> productLessThanKHints(mistakeReason, failedCase);
            default -> genericHints(mistakeReason, failedCase);
        };

        return new GenerateHintResponse("mock", hints);
    }

    private List<HintItemResponse> primeHints(String mistakeReason, FailedCaseContext failedCase) {
        String levelOne = switch (mistakeReason) {
            case "boundary_case" -> "先别急着进循环，先想 n < 2 这类边界值应该返回什么。质数必须大于等于 2。";
            case "misread_problem" -> "这题不是统计有多少个质数，而是判断“当前这个 n 是否是质数”。";
            default -> "质数是只能被 1 和自身整除的数，先把 n < 2 的情况单独处理。";
        };
        if (isCompositeCase(failedCase)) {
            levelOne += " 这类数虽然大于 1，但能被其他数整除，所以不能直接因为 n > 1 就判成质数。";
        }

        return List.of(
                hint(1, "方向提示", levelOne),
                hint(2, "关键判断提示", appendFailedCase(switch (mistakeReason) {
                    case "key_condition" -> "循环里只要发现 n % i == 0，就已经能确定它不是质数，可以直接返回 false。";
                    case "variable_design" -> "核心变量是候选除数 i。你不需要维护很多状态，只需要不断尝试 i 是否能整除 n。";
                    default -> "判断核心在于“是否存在一个除数 i 能整除 n”，关键条件是 n % i == 0。";
                }, failedCase)),
                hint(3, "接近代码提示", switch (mistakeReason) {
                    case "answer_update" -> "不要一进循环就返回 true。应该在循环全部检查完、没有发现任何因子之后，再返回 true。";
                    default -> "检查 i 的范围可以到 i * i <= n。循环中发现可整除就 return false，循环结束后再 return true。";
                })
        );
    }

    private List<HintItemResponse> twoSumHints(String mistakeReason, FailedCaseContext failedCase) {
        return List.of(
                hint(1, "方向提示", switch (mistakeReason) {
                    case "misread_problem" -> "题目要你返回的是两个下标，不是那两个数字本身。";
                    case "input_output" -> "输入有 nums 和 target，输出是长度为 2 的 int[]，不是 boolean。";
                    default -> "先确认目标：你要找到两数之和等于 target 的两个下标。";
                }),
                hint(2, "关键变量提示", appendFailedCase(switch (mistakeReason) {
                    case "variable_design" -> "最关键的辅助变量是 HashMap，用来记录“已经遍历过的数字 -> 它的下标”。";
                    case "key_condition" -> "每轮真正要判断的不是 nums[i] == target，而是 target - nums[i] 是否已经出现过。";
                    default -> "用 HashMap 记录已经遍历过的数字和下标，边遍历边查补数。";
                }, failedCase)),
                hint(3, "接近代码提示", appendFailedCase(switch (mistakeReason) {
                    case "answer_update" -> "更新顺序要小心：先查 target - nums[i] 是否存在；没找到时，再把当前 nums[i] 放入 map。";
                    default -> "先确认你返回的是下标数组。每次先查 target - nums[i] 是否存在，再把当前 nums[i] 放入 map。找到补数时立即构造并返回答案。";
                }, failedCase))
        );
    }

    private List<HintItemResponse> productLessThanKHints(String mistakeReason, FailedCaseContext failedCase) {
        return List.of(
                hint(1, "方向提示", switch (mistakeReason) {
                    case "misread_problem" -> "这题求的是连续子数组个数，不是把所有子数组都列出来，也不是求最长长度。";
                    case "input_output" -> "输入是正整数数组 nums 和整数 k，输出是一个 int 计数结果。";
                    default -> "这题求的是连续子数组个数，不是列出子数组内容。";
                }),
                hint(2, "关键判断提示", appendFailedCase(switch (mistakeReason) {
                    case "key_condition" -> "窗口不合法的条件是 product >= k。只要不合法，就要持续收缩 left。";
                    case "variable_design" -> "先确认核心变量：left、right、当前窗口乘积 product，以及累计答案 count。";
                    case "boundary_case" -> "别漏掉 k <= 1 的情况。因为数组元素都是正整数，这时结果会直接是 0。";
                    default -> "滑动窗口的关键判断是 product >= k 时收缩窗口，product < k 时窗口才合法。";
                }, failedCase)),
                hint(3, "接近代码提示", appendFailedCase(switch (mistakeReason) {
                    case "answer_update" -> "答案不是每次只加 1。窗口合法后，以 right 结尾的新增子数组数量是 right - left + 1。";
                    default -> "right 扩张后先把 nums[right] 乘进去；窗口不合法时继续按 product >= k 收缩。窗口合法后，本轮新增数量就是 right - left + 1。";
                }, failedCase))
        );
    }

    private List<HintItemResponse> genericHints(String mistakeReason, FailedCaseContext failedCase) {
        return List.of(
                hint(1, "方向提示", switch (mistakeReason) {
                    case "misread_problem" -> "先重新用一句话描述题目要你“最终返回什么”，避免一开始就把目标写偏。";
                    case "input_output" -> "先把输入类型和输出类型写清楚，再决定方法签名和返回值更新位置。";
                    default -> "先回到拆题：这题到底要你求什么，输出是什么，不要一上来就写循环。";
                }),
                hint(2, "关键变量提示", switch (mistakeReason) {
                    case "variable_design" -> "想想你最少要维护哪些变量：循环指针、当前状态、以及最终答案。变量越少越容易理清逻辑。";
                    case "key_condition" -> "把题目里的“什么时候合法/什么时候不合法”翻译成 if 或 while 条件，这通常就是出错点。";
                    default -> "先列出核心变量，再把“关键判断条件”单独写出来，别把判断和答案更新混在一起。";
                }),
                hint(3, "接近代码提示", appendFailedCase(switch (mistakeReason) {
                    case "answer_update" -> "重点检查答案更新时机：是每轮更新、满足条件时更新，还是循环结束后更新。";
                    case "boundary_case" -> "再回看空输入、最小值、最大值、单元素这些边界，很多错误都卡在这里。";
                    default -> "把代码分成三段看：初始化、关键判断、答案更新。先确认每一段都对应题意中的一个明确动作。";
                }, failedCase))
        );
    }

    private List<HintItemResponse> compileErrorHints(Long problemId, GenerateHintRequest request) {
        String compileMessage = extractCompileMessage(request);
        String firstLine = firstLine(compileMessage);
        return List.of(
                hint(1, "方向提示", "先看 javac 编译错误第一行，先判断它是语法、返回值、import 还是方法签名问题。"
                        + (firstLine.isBlank() ? "" : " 当前首条报错：" + firstLine)),
                hint(2, "关键判断提示", "先确认类名必须是 public class Solution，方法签名必须和题目要求一致。"
                        + switch (problemId.intValue()) {
                    case 1 -> " 这题还要确认 isPrime 的返回类型始终是 boolean。";
                    case 7 -> " 这题要确认 twoSum 返回的是 int[]，并且导入了需要的集合类。";
                    case 9 -> " 这题要确认滑动窗口相关变量都已声明，while 条件括号完整。";
                    default -> "";
                }),
                hint(3, "接近代码提示", compileErrorKeywordHint(compileMessage))
        );
    }

    private String compileErrorKeywordHint(String compileMessage) {
        String normalizedMessage = normalize(compileMessage);
        if (normalizedMessage.contains("missing return statement") || normalizedMessage.contains("missing return value")) {
            return "编译器提示缺少返回值时，通常是某个分支没有 return。重点检查 if/while 后面是否每条路径都返回了符合类型的值。";
        }
        if (normalizedMessage.contains("incompatible types")) {
            return "类型不兼容时，先检查返回值类型、局部变量类型和方法签名是否一致。不要把 boolean、int、int[] 这些类型混用。";
        }
        if (normalizedMessage.contains("cannot find symbol")) {
            return "cannot find symbol 通常说明方法名、变量名或 import 不一致。先核对拼写，再确认需要的类是否已经 import。";
        }
        if (normalizedMessage.contains("';' expected")) {
            return "出现 ';' expected 时，先检查上一行是否少了分号，或者括号、数组字面量、return 语句结尾是否写完整。";
        }
        return "先对照 starterCode：保留 public class Solution 和方法签名不变，再逐行修复编译器指出的第一处错误。后面的报错常常会连带消失。";
    }

    private HintItemResponse hint(int level, String title, String content) {
        return new HintItemResponse(level, title, content);
    }

    private Optional<FailedCaseContext> extractFirstFailedCase(GenerateHintRequest request) {
        if (request == null || request.lastSubmitResult() == null || request.lastSubmitResult().cases() == null) {
            return Optional.empty();
        }

        return request.lastSubmitResult().cases().stream()
                .filter(caseResult -> !caseResult.passed())
                .findFirst()
                .map(caseResult -> new FailedCaseContext(
                        safeText(caseResult.input()),
                        safeText(caseResult.expected()),
                        safeText(caseResult.actual())
                ));
    }

    private String appendFailedCase(String baseContent, FailedCaseContext failedCase) {
        if (failedCase == null) {
            return baseContent;
        }
        return baseContent + " 重点检查用例：input=" + failedCase.input()
                + ", expected=" + failedCase.expected()
                + ", actual=" + failedCase.actual() + "。";
    }

    private boolean isCompositeCase(FailedCaseContext failedCase) {
        if (failedCase == null) {
            return false;
        }
        String input = failedCase.input();
        return input.contains("9") || input.contains("15") || input.contains("21");
    }

    private String extractCompileMessage(GenerateHintRequest request) {
        if (request == null) {
            return "";
        }
        SubmitCodeResponse lastSubmitResult = request.lastSubmitResult();
        return lastSubmitResult == null ? "" : safeText(lastSubmitResult.message());
    }

    private String firstLine(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        return text.lines()
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .findFirst()
                .orElse("");
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private record FailedCaseContext(String input, String expected, String actual) {
    }
}
