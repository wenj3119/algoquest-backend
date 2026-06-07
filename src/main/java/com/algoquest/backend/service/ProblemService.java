package com.algoquest.backend.service;

import com.algoquest.backend.dto.ExampleResponse;
import com.algoquest.backend.dto.ProblemDetailResponse;
import com.algoquest.backend.dto.ProblemStepResponse;
import com.algoquest.backend.dto.ProblemSummaryResponse;
import com.algoquest.backend.dto.StepOptionResponse;
import com.algoquest.backend.dto.SubmitCaseResponse;
import com.algoquest.backend.dto.SubmitCodeRequest;
import com.algoquest.backend.dto.SubmitCodeResponse;
import com.algoquest.backend.judge.JavaJudgeService;
import com.algoquest.backend.judge.JudgeCaseResult;
import com.algoquest.backend.judge.JudgeResult;
import com.algoquest.backend.model.Difficulty;
import com.algoquest.backend.model.Example;
import com.algoquest.backend.model.Problem;
import com.algoquest.backend.model.ProblemStep;
import com.algoquest.backend.model.StepOption;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProblemService {

    private static final int MAX_CODE_LENGTH = 20_000;

    private final Map<Long, Problem> problemStore = Map.ofEntries(
            Map.entry(1L, new Problem(
                    1L,
                    "判断质数",
                    Difficulty.EASY,
                    "数学",
                    "给定一个整数 n，判断它是否是质数。如果 n 只能被 1 和它本身整除，则返回 true，否则返回 false。",
                    List.of(
                            new Example("n = 7", "true", "7 只能被 1 和 7 整除。"),
                            new Example("n = 12", "false", "12 可以被 2、3、4、6 整除。")
                    ),
                    """
                    public class Solution {
                        public boolean isPrime(int n) {
                            return false;
                        }
                    }
                    """,
                    stepsForPrime()
            )),
            Map.entry(2L, new Problem(
                    2L,
                    "打印 # 三角形",
                    Difficulty.EASY,
                    "模拟",
                    "给定整数 n，打印一个由 # 组成的直角三角形，共 n 行，第 i 行包含 i 个 #。",
                    List.of(
                            new Example("n = 3", "#\\n##\\n###", "每一行比上一行多一个 #。")
                    ),
                    """
                    public class Solution {
                        public String printTriangle(int n) {
                            return "";
                        }
                    }
                    """,
                    stepsForTriangle()
            )),
            Map.entry(3L, new Problem(
                    3L,
                    "杨辉三角",
                    Difficulty.EASY,
                    "数组",
                    "给定整数 numRows，返回杨辉三角的前 numRows 行。",
                    List.of(
                            new Example("numRows = 5", "[[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]", "每个内部元素等于上一行相邻两个元素之和。")
                    ),
                    """
                    import java.util.*;
                    
                    public class Solution {
                        public List<List<Integer>> generate(int numRows) {
                            return new ArrayList<>();
                        }
                    }
                    """,
                    stepsForPascal()
            )),
            Map.entry(4L, new Problem(
                    4L,
                    "数组最大值",
                    Difficulty.EASY,
                    "数组",
                    "给定一个整数数组 nums，返回数组中的最大值。",
                    List.of(
                            new Example("nums = [3,1,5,2]", "5", "遍历过程中持续维护最大值。")
                    ),
                    """
                    public class Solution {
                        public int maxValue(int[] nums) {
                            return 0;
                        }
                    }
                    """,
                    stepsForMaxValue()
            )),
            Map.entry(5L, new Problem(
                    5L,
                    "数组反转",
                    Difficulty.EASY,
                    "双指针",
                    "给定一个整数数组 nums，原地将数组反转。",
                    List.of(
                            new Example("nums = [1,2,3,4]", "[4,3,2,1]", "首尾双指针交换。")
                    ),
                    """
                    public class Solution {
                        public void reverse(int[] nums) {
                            
                        }
                    }
                    """,
                    stepsForReverse()
            )),
            Map.entry(6L, new Problem(
                    6L,
                    "移动零",
                    Difficulty.EASY,
                    "双指针",
                    "给定一个数组 nums，将所有 0 移动到末尾，同时保持非零元素的相对顺序。",
                    List.of(
                            new Example("nums = [0,1,0,3,12]", "[1,3,12,0,0]", "使用慢指针维护下一个非零元素应放置的位置。")
                    ),
                    """
                    public class Solution {
                        public void moveZeroes(int[] nums) {
                            
                        }
                    }
                    """,
                    stepsForMoveZeroes()
            )),
            Map.entry(7L, new Problem(
                    7L,
                    "两数之和",
                    Difficulty.EASY,
                    "哈希",
                    "给定整数数组 nums 和目标值 target，返回两数之和等于 target 的两个下标。",
                    List.of(
                            new Example("nums = [2,7,11,15], target = 9", "[0,1]", "可以边遍历边在哈希表中查找补数。")
                    ),
                    """
                    import java.util.*;
                    
                    public class Solution {
                        public int[] twoSum(int[] nums, int target) {
                            return new int[0];
                        }
                    }
                    """,
                    stepsForTwoSum()
            )),
            Map.entry(8L, new Problem(
                    8L,
                    "有效括号",
                    Difficulty.MEDIUM,
                    "栈",
                    "给定一个仅包含 ()[]{} 的字符串，判断字符串是否有效。",
                    List.of(
                            new Example("s = \"()[]{}\"", "true", "左括号入栈，右括号匹配栈顶。"),
                            new Example("s = \"(]\"", "false", "括号类型不匹配。")
                    ),
                    """
                    import java.util.*;
                    
                    public class Solution {
                        public boolean isValid(String s) {
                            return false;
                        }
                    }
                    """,
                    stepsForValidParentheses()
            )),
            Map.entry(9L, new Problem(
                    9L,
                    "乘积小于 k 的子数组",
                    Difficulty.MEDIUM,
                    "滑动窗口",
                    "给定一个正整数数组 nums 和整数 k，返回乘积严格小于 k 的连续子数组个数。",
                    List.of(
                            new Example("nums = [10,5,2,6], k = 100", "8", "维护一个乘积小于 k 的滑动窗口。")
                    ),
                    """
                    public class Solution {
                        public int numSubarrayProductLessThanK(int[] nums, int k) {
                            return 0;
                        }
                    }
                    """,
                    stepsForProductLessThanK()
            )),
            Map.entry(10L, new Problem(
                    10L,
                    "无重复字符最长子串",
                    Difficulty.MEDIUM,
                    "滑动窗口",
                    "给定一个字符串 s，返回不含重复字符的最长子串长度。",
                    List.of(
                            new Example("s = \"abcabcbb\"", "3", "最长无重复子串是 \"abc\"。")
                    ),
                    """
                    import java.util.*;
                    
                    public class Solution {
                        public int lengthOfLongestSubstring(String s) {
                            return 0;
                        }
                    }
                    """,
                    stepsForLongestSubstring()
            ))
    );
    private final JavaJudgeService javaJudgeService;

    public ProblemService(JavaJudgeService javaJudgeService) {
        this.javaJudgeService = javaJudgeService;
    }

    public List<ProblemSummaryResponse> getProblems() {
        return problemStore.values().stream()
                .sorted((left, right) -> Long.compare(left.id(), right.id()))
                .map(problem -> new ProblemSummaryResponse(
                        problem.id(),
                        problem.title(),
                        problem.difficulty(),
                        problem.category(),
                        "not_started"
                ))
                .toList();
    }

    public ProblemDetailResponse getProblemById(Long id) {
        Problem problem = requireProblem(id);
        return new ProblemDetailResponse(
                problem.id(),
                problem.title(),
                problem.difficulty(),
                problem.category(),
                problem.description(),
                problem.examples().stream()
                        .map(this::toExampleResponse)
                        .toList(),
                problem.starterCode(),
                problem.steps().stream().map(this::toProblemStepResponse).toList()
        );
    }

    public SubmitCodeResponse submitCode(Long id, SubmitCodeRequest request) {
        requireProblem(id);

        if (request == null) {
            return new SubmitCodeResponse("failed", 0, 0, "请求体不能为空。", List.of());
        }

        String language = request.language();
        if (language == null || !"java".equalsIgnoreCase(language.trim())) {
            return new SubmitCodeResponse("failed", 0, 0, "当前仅支持 Java 提交。", List.of());
        }

        String code = request.code();
        if (code == null || code.isBlank()) {
            return new SubmitCodeResponse("failed", 0, 0, "代码不能为空。", List.of());
        }

        if (code.length() > MAX_CODE_LENGTH) {
            return new SubmitCodeResponse("failed", 0, 0, "代码长度不能超过 20000 个字符。", List.of());
        }

        if (!code.contains("public class Solution")) {
            return new SubmitCodeResponse("compile_error", 0, 0, "提交代码必须包含 public class Solution。", List.of());
        }

        JudgeResult judgeResult = javaJudgeService.judge(id, code);
        return new SubmitCodeResponse(
                judgeResult.status(),
                judgeResult.passedCount(),
                judgeResult.totalCount(),
                judgeResult.message(),
                judgeResult.cases().stream().map(this::toSubmitCaseResponse).toList()
        );
    }

    public void assertProblemExists(Long id) {
        requireProblem(id);
    }

    private Problem requireProblem(Long id) {
        Problem problem = problemStore.get(id);
        if (problem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found: " + id);
        }
        return problem;
    }

    private ExampleResponse toExampleResponse(Example example) {
        return new ExampleResponse(example.input(), example.output(), example.explanation());
    }

    private ProblemStepResponse toProblemStepResponse(ProblemStep step) {
        return new ProblemStepResponse(
                step.id(),
                step.title(),
                step.content(),
                step.type(),
                step.options().stream().map(option -> new StepOptionResponse(option.label(), option.content())).toList(),
                step.answer(),
                step.explanation()
        );
    }

    private SubmitCaseResponse toSubmitCaseResponse(JudgeCaseResult caseResult) {
        return new SubmitCaseResponse(caseResult.input(), caseResult.expected(), caseResult.actual(), caseResult.passed());
    }

    private static StepOption option(String label, String content) {
        return new StepOption(label, content);
    }

    private static ProblemStep step(String id, String title, String content, String answer, String explanation, StepOption... options) {
        return new ProblemStep(id, title, content, "single_choice", List.of(options), answer, explanation);
    }

    private static List<ProblemStep> stepsForPrime() {
        return List.of(
                step("1", "题目要求我求什么？", "这道题最终要你返回什么结果？", "B", "目标不是统计质数个数，也不是输出因子，而是判断单个整数 n 是否为质数。",
                        option("A", "返回 1 到 n 之间一共有多少个质数"),
                        option("B", "判断整数 n 是否是质数，返回 true 或 false"),
                        option("C", "输出 n 的所有质因子"),
                        option("D", "找到第 n 个质数")),
                step("2", "输入是什么？", "输入数据的形态是什么？", "A", "输入只有一个整数 n，所以你不需要处理数组或字符串遍历。",
                        option("A", "一个整数 n"),
                        option("B", "一个整数数组 nums"),
                        option("C", "一个字符串 s"),
                        option("D", "两个整数 n 和 k")),
                step("3", "输出是什么？", "方法返回值应该是什么？", "C", "如果 n 是质数返回 true，否则返回 false，所以输出是布尔值。",
                        option("A", "一个整数"),
                        option("B", "一个字符串"),
                        option("C", "一个布尔值"),
                        option("D", "一个整数数组")),
                step("4", "关键词是什么？", "题目里最关键的判断线索是什么？", "D", "质数的定义是只能被 1 和它本身整除，所以关键在于检查是否存在其他因子。",
                        option("A", "连续子数组"),
                        option("B", "左右指针交换"),
                        option("C", "哈希表查找补数"),
                        option("D", "是否存在 1 和自身之外的因子")),
                step("5", "需要维护哪些变量？", "做这题最核心的循环变量是什么？", "A", "通常维护一个从 2 开始的除数 i，并检查 i * i <= n 即可。",
                        option("A", "候选除数 i"),
                        option("B", "窗口左右边界 left/right"),
                        option("C", "答案数组 ans"),
                        option("D", "栈 stack")),
                step("6", "关键判断条件是什么？", "什么时候可以立刻判定它不是质数？", "B", "只要发现 n % i == 0，说明存在其他因子，可以直接返回 false。",
                        option("A", "当 n > i 时"),
                        option("B", "当 n % i == 0 时"),
                        option("C", "当 i == 1 时"),
                        option("D", "当 n 是偶数时一定返回 false")),
                step("7", "什么时候更新答案？", "这题在什么时机可以确定最终答案？", "C", "如果循环中没找到任何因子，循环结束后才能返回 true；特殊情况 n <= 1 先返回 false。",
                        option("A", "每轮循环都把答案加一"),
                        option("B", "一进入方法就返回 true"),
                        option("C", "检查完所有候选除数仍未发现因子时返回 true"),
                        option("D", "找到第一个不能整除的 i 就返回 true"))
        );
    }

    private static List<ProblemStep> stepsForTriangle() {
        return List.of(
                step("1", "题目要求我求什么？", "最终要构造什么结果？", "A", "这题要求返回一个由多行 # 组成的字符串，不是直接打印到控制台。",
                        option("A", "返回一个由 # 构成的三角形字符串"),
                        option("B", "返回三角形总共有多少个 #"),
                        option("C", "返回一个整数数组"),
                        option("D", "判断 # 是否成对出现")),
                step("2", "输入是什么？", "输入代表什么？", "D", "输入是一个整数 n，表示三角形有 n 行。",
                        option("A", "一个字符串"),
                        option("B", "一个数组"),
                        option("C", "两个整数"),
                        option("D", "一个整数 n，表示行数")),
                step("3", "输出是什么？", "返回值类型是什么？", "B", "因为题目要求返回整段三角形文本，所以输出是字符串。",
                        option("A", "布尔值"),
                        option("B", "字符串"),
                        option("C", "整数"),
                        option("D", "二维数组")),
                step("4", "关键词是什么？", "最明显的构造模式是什么？", "C", "第 i 行有 i 个 #，并且相邻两行之间用换行连接。",
                        option("A", "哈希查重"),
                        option("B", "滑动窗口"),
                        option("C", "第 i 行放 i 个 #"),
                        option("D", "二分查找")),
                step("5", "需要维护哪些变量？", "构造字符串时最常用哪些变量？", "A", "一般用 StringBuilder 存结果，再用行号 i 控制每一行的 # 数量。",
                        option("A", "StringBuilder 和行号 i"),
                        option("B", "栈和队列"),
                        option("C", "left/right 双指针"),
                        option("D", "乘积 product")),
                step("6", "关键判断条件是什么？", "什么时候需要补换行？", "D", "通常在当前行不是最后一行时追加换行，避免结尾格式多出无意义字符。",
                        option("A", "当 i 是偶数时"),
                        option("B", "当 # 总数超过 n 时"),
                        option("C", "每个 # 后面都补换行"),
                        option("D", "当前行不是最后一行时追加换行")),
                step("7", "什么时候更新答案？", "结果字符串在什么时候增长？", "B", "每构造完一行，就把该行内容追加进总结果。",
                        option("A", "只在第一行更新一次"),
                        option("B", "每构造完一行后追加到结果"),
                        option("C", "循环结束后统一替换"),
                        option("D", "找到质数时更新"))
        );
    }

    private static List<ProblemStep> stepsForPascal() {
        return List.of(
                step("1", "题目要求我求什么？", "你需要返回什么结构？", "B", "要返回杨辉三角前 numRows 行的二维列表。",
                        option("A", "一维整数数组"),
                        option("B", "前 numRows 行的二维列表"),
                        option("C", "布尔值"),
                        option("D", "单行字符串")),
                step("2", "输入是什么？", "numRows 表示什么？", "A", "numRows 表示要生成多少行杨辉三角。",
                        option("A", "需要生成的行数"),
                        option("B", "每行的固定列数"),
                        option("C", "目标和"),
                        option("D", "字符串长度")),
                step("3", "输出是什么？", "方法返回值类型最贴切的是？", "D", "输出是嵌套列表，外层按行存储，内层存每行数字。",
                        option("A", "String"),
                        option("B", "int[]"),
                        option("C", "boolean"),
                        option("D", "List<List<Integer>>")),
                step("4", "关键词是什么？", "杨辉三角内部元素怎么得到？", "C", "非边界位置等于上一行左上和右上的两个数之和。",
                        option("A", "双指针交换"),
                        option("B", "哈希映射补数"),
                        option("C", "上一行相邻两个数之和"),
                        option("D", "窗口乘积")),
                step("5", "需要维护哪些变量？", "构造时通常会维护什么？", "B", "通常维护总结果 result、当前行 current、行号 row 和列号 col。",
                        option("A", "只需要一个布尔值"),
                        option("B", "result、current、row、col"),
                        option("C", "栈和队列"),
                        option("D", "left、right")),
                step("6", "关键判断条件是什么？", "哪些位置必定是 1？", "A", "每一行的首尾位置一定是 1，这是边界条件。",
                        option("A", "每行首尾位置"),
                        option("B", "所有偶数列"),
                        option("C", "中间位置"),
                        option("D", "最后一行全部位置")),
                step("7", "什么时候更新答案？", "总结果何时追加一整行？", "D", "一整行 current 构造完毕后，再加入 result。",
                        option("A", "每放一个数就立刻返回"),
                        option("B", "一开始就加入空行"),
                        option("C", "只加入最后一行"),
                        option("D", "当前行构造完成后加入 result"))
        );
    }

    private static List<ProblemStep> stepsForMaxValue() {
        return List.of(
                step("1", "题目要求我求什么？", "最终需要找出什么？", "C", "要返回数组中的最大值，不是下标，也不是排序结果。",
                        option("A", "最小值"),
                        option("B", "最大值的下标"),
                        option("C", "数组中的最大值"),
                        option("D", "排序后的数组")),
                step("2", "输入是什么？", "输入数据类型是什么？", "A", "输入是一个整数数组 nums。",
                        option("A", "整数数组 nums"),
                        option("B", "字符串 s"),
                        option("C", "单个整数 n"),
                        option("D", "二维数组")),
                step("3", "输出是什么？", "返回值类型是什么？", "B", "返回数组中的一个最大整数，所以输出是 int。",
                        option("A", "boolean"),
                        option("B", "int"),
                        option("C", "String"),
                        option("D", "int[]")),
                step("4", "关键词是什么？", "最自然的做法是什么？", "D", "从头遍历数组，持续维护当前最大值。",
                        option("A", "滑动窗口"),
                        option("B", "递归回溯"),
                        option("C", "栈匹配"),
                        option("D", "遍历并维护最大值")),
                step("5", "需要维护哪些变量？", "核心变量是什么？", "A", "需要一个 max 变量记录当前最大值。",
                        option("A", "max"),
                        option("B", "target"),
                        option("C", "stack"),
                        option("D", "answer[]")),
                step("6", "关键判断条件是什么？", "什么时候更新 max？", "C", "当当前元素 num > max 时，说明找到了更大的值。",
                        option("A", "num < max"),
                        option("B", "num == max"),
                        option("C", "num > max"),
                        option("D", "num % max == 0")),
                step("7", "什么时候更新答案？", "max 在什么时候变化？", "B", "每次遇到更大的元素就更新 max，遍历结束后返回。",
                        option("A", "只在最后一个元素更新"),
                        option("B", "遇到更大元素时更新"),
                        option("C", "每轮都加一"),
                        option("D", "找到 0 时更新"))
        );
    }

    private static List<ProblemStep> stepsForReverse() {
        return List.of(
                step("1", "题目要求我求什么？", "题目希望你完成什么操作？", "D", "需要原地反转数组，不是返回新数组的排序结果。",
                        option("A", "统计数组和"),
                        option("B", "找到中位数"),
                        option("C", "升序排序"),
                        option("D", "原地反转数组")),
                step("2", "输入是什么？", "输入形态是什么？", "A", "输入是一个整数数组 nums。",
                        option("A", "整数数组 nums"),
                        option("B", "一个字符串"),
                        option("C", "两个整数"),
                        option("D", "一个布尔值")),
                step("3", "输出是什么？", "方法返回类型是什么？", "C", "方法签名是 void，说明直接修改原数组即可。",
                        option("A", "int"),
                        option("B", "int[]"),
                        option("C", "void"),
                        option("D", "String")),
                step("4", "关键词是什么？", "最典型的思路是什么？", "B", "用首尾双指针相向移动并交换元素。",
                        option("A", "前缀和"),
                        option("B", "双指针交换"),
                        option("C", "哈希映射"),
                        option("D", "滑动窗口")),
                step("5", "需要维护哪些变量？", "核心变量是哪两个？", "A", "需要 left 和 right 指向当前待交换的首尾位置。",
                        option("A", "left 和 right"),
                        option("B", "stack 和 queue"),
                        option("C", "sum 和 count"),
                        option("D", "target 和 index")),
                step("6", "关键判断条件是什么？", "循环什么时候继续？", "D", "当 left < right 时，说明还存在待交换的两个位置。",
                        option("A", "left > right"),
                        option("B", "left == 0"),
                        option("C", "nums[left] > nums[right]"),
                        option("D", "left < right")),
                step("7", "什么时候更新答案？", "数组内容何时发生变化？", "C", "每次交换 nums[left] 和 nums[right] 后，数组就完成一部分反转。",
                        option("A", "循环结束后统一更新"),
                        option("B", "只更新中间元素"),
                        option("C", "每次交换后"),
                        option("D", "遇到重复值时"))
        );
    }

    private static List<ProblemStep> stepsForMoveZeroes() {
        return List.of(
                step("1", "题目要求我求什么？", "这题要完成什么效果？", "B", "要把所有 0 挪到末尾，同时保持非零元素的相对顺序。",
                        option("A", "删除所有 0"),
                        option("B", "把 0 移到末尾且保持非零顺序"),
                        option("C", "统计 0 的个数"),
                        option("D", "反转数组")),
                step("2", "输入是什么？", "输入是？", "C", "输入是整数数组 nums，题目要求原地修改。",
                        option("A", "字符串"),
                        option("B", "单个整数"),
                        option("C", "整数数组 nums"),
                        option("D", "二维矩阵")),
                step("3", "输出是什么？", "返回值类型是什么？", "A", "方法返回 void，结果体现在 nums 的内容变化上。",
                        option("A", "void"),
                        option("B", "boolean"),
                        option("C", "int"),
                        option("D", "int[]")),
                step("4", "关键词是什么？", "最合适的思路是什么？", "D", "用双指针或写指针，把非零元素依次写到前面。",
                        option("A", "二分查找"),
                        option("B", "栈匹配"),
                        option("C", "子串去重"),
                        option("D", "双指针/写指针覆盖")),
                step("5", "需要维护哪些变量？", "最关键的变量是什么？", "B", "通常维护 index 或 slow 指针，表示下一个非零元素该放的位置。",
                        option("A", "target"),
                        option("B", "slow/index 指针"),
                        option("C", "stack"),
                        option("D", "result 字符串")),
                step("6", "关键判断条件是什么？", "什么时候把元素往前写？", "A", "当 nums[i] != 0 时，把它写到 slow 位置。",
                        option("A", "nums[i] != 0"),
                        option("B", "nums[i] == 0"),
                        option("C", "nums[i] > nums[0]"),
                        option("D", "i 为偶数")),
                step("7", "什么时候更新答案？", "0 在什么时候补到后面？", "C", "前面写完所有非零元素后，再把剩余位置填成 0。",
                        option("A", "遇到 0 时立即返回"),
                        option("B", "每次都排序"),
                        option("C", "非零元素写完后补 0"),
                        option("D", "只在第一轮循环更新"))
        );
    }

    private static List<ProblemStep> stepsForTwoSum() {
        return List.of(
                step("1", "题目要求我求什么？", "最终需要返回什么？", "C", "要求返回两数之和等于 target 的两个下标，而不是两个数本身。",
                        option("A", "返回两个数字的乘积"),
                        option("B", "返回所有满足条件的组合"),
                        option("C", "返回两个下标"),
                        option("D", "返回 target 是否存在")),
                step("2", "输入是什么？", "输入包含哪些内容？", "B", "输入有一个数组 nums 和一个目标值 target。",
                        option("A", "只有一个整数 n"),
                        option("B", "数组 nums 和目标值 target"),
                        option("C", "一个字符串 s"),
                        option("D", "一个二维矩阵")),
                step("3", "输出是什么？", "方法返回值最贴切的是？", "A", "返回两个下标组成的数组，所以输出是 int[]。",
                        option("A", "int[]"),
                        option("B", "boolean"),
                        option("C", "int"),
                        option("D", "List<List<Integer>>")),
                step("4", "关键词是什么？", "本题的关键字最接近哪一个？", "D", "边遍历边查找补数，是典型的哈希表题。",
                        option("A", "滑动窗口"),
                        option("B", "单调栈"),
                        option("C", "双指针首尾夹逼"),
                        option("D", "哈希表查找补数")),
                step("5", "需要维护哪些变量？", "除了循环下标，还要维护什么？", "A", "核心是一个 map，记录“数值 -> 下标”的映射。",
                        option("A", "map: 数值 -> 下标"),
                        option("B", "stack"),
                        option("C", "product"),
                        option("D", "builder")),
                step("6", "关键判断条件是什么？", "什么时候可以确定答案并返回？", "B", "如果当前值的补数已经在 map 里，就找到了答案。",
                        option("A", "nums[i] > target"),
                        option("B", "map 中已经存在 target - nums[i]"),
                        option("C", "i 到达最后一个位置"),
                        option("D", "数组已经排序")),
                step("7", "什么时候更新答案？", "map 和答案分别何时更新？", "D", "先查补数；如果没找到，再把当前数和下标放入 map。找到补数时立即返回答案。",
                        option("A", "先把当前数放进 map，再无条件返回"),
                        option("B", "每轮都更新 answer"),
                        option("C", "遍历结束后统一构造答案"),
                        option("D", "先查补数，找不到再写入 map"))
        );
    }

    private static List<ProblemStep> stepsForValidParentheses() {
        return List.of(
                step("1", "题目要求我求什么？", "最终要判断什么？", "A", "要判断括号字符串是否有效匹配。",
                        option("A", "字符串中的括号是否有效"),
                        option("B", "最长子串长度"),
                        option("C", "数组最大值"),
                        option("D", "是否存在两数之和")),
                step("2", "输入是什么？", "输入是什么类型？", "C", "输入是一个只含括号字符的字符串 s。",
                        option("A", "整数"),
                        option("B", "整数数组"),
                        option("C", "字符串 s"),
                        option("D", "二维列表")),
                step("3", "输出是什么？", "返回值类型是什么？", "B", "有效返回 true，无效返回 false。",
                        option("A", "int[]"),
                        option("B", "boolean"),
                        option("C", "String"),
                        option("D", "void")),
                step("4", "关键词是什么？", "最标准的思路是？", "D", "括号匹配最典型的数据结构就是栈。",
                        option("A", "哈希表统计频率"),
                        option("B", "双指针交换"),
                        option("C", "滑动窗口"),
                        option("D", "栈匹配")),
                step("5", "需要维护哪些变量？", "核心数据结构是什么？", "A", "需要一个 stack 保存还未匹配的左括号。",
                        option("A", "stack"),
                        option("B", "product"),
                        option("C", "result 数组"),
                        option("D", "builder")),
                step("6", "关键判断条件是什么？", "什么时候可以直接判无效？", "C", "遇到右括号时，如果栈空或类型不匹配，就可以返回 false。",
                        option("A", "遇到左括号"),
                        option("B", "字符串长度是偶数"),
                        option("C", "右括号无法匹配栈顶"),
                        option("D", "出现两个连续左括号")),
                step("7", "什么时候更新答案？", "最终何时确定为有效？", "B", "遍历完成后，如果栈为空，说明所有括号都配对成功。",
                        option("A", "遇到第一个左括号时"),
                        option("B", "遍历结束且栈为空时"),
                        option("C", "发现任意一对匹配时"),
                        option("D", "一开始就默认有效"))
        );
    }

    private static List<ProblemStep> stepsForProductLessThanK() {
        return List.of(
                step("1", "题目要求我求什么？", "你最终要返回哪个量？", "A", "题目要求返回乘积严格小于 k 的连续子数组个数，不是最长长度。",
                        option("A", "满足条件的连续子数组个数"),
                        option("B", "满足条件的最长子数组长度"),
                        option("C", "最小乘积"),
                        option("D", "所有子数组内容")),
                step("2", "输入是什么？", "输入包含什么？", "B", "输入有正整数数组 nums 和整数 k。",
                        option("A", "字符串 s 和整数 k"),
                        option("B", "正整数数组 nums 和整数 k"),
                        option("C", "单个整数 n"),
                        option("D", "两个字符串")),
                step("3", "输出是什么？", "返回值类型是什么？", "D", "输出是满足条件的个数，所以返回 int。",
                        option("A", "boolean"),
                        option("B", "int[]"),
                        option("C", "String"),
                        option("D", "int")),
                step("4", "关键词是什么？", "本题最关键的模式是？", "C", "这是典型的滑动窗口题，因为数组元素都是正数，窗口乘积有单调性。",
                        option("A", "并查集"),
                        option("B", "栈"),
                        option("C", "滑动窗口"),
                        option("D", "回溯")),
                step("5", "需要维护哪些变量？", "核心变量组合是哪一组？", "A", "通常维护 left、right、当前窗口乘积 product，以及累计答案 count。",
                        option("A", "left、right、product、count"),
                        option("B", "stack、queue、sum"),
                        option("C", "map、target"),
                        option("D", "builder、row、col")),
                step("6", "关键判断条件是什么？", "什么时候要缩小窗口？", "B", "当 product >= k 时，当前窗口不合法，需要移动 left 并把左端元素除掉。",
                        option("A", "product < k"),
                        option("B", "product >= k"),
                        option("C", "right == left"),
                        option("D", "nums[right] == 0")),
                step("7", "什么时候更新答案？", "合法窗口形成后，一次新增多少个答案？", "D", "当窗口合法时，以 right 结尾的新增子数组个数是 right - left + 1。",
                        option("A", "每次只加 1"),
                        option("B", "只在窗口长度为 2 时更新"),
                        option("C", "在缩小窗口前更新"),
                        option("D", "窗口合法后加上 right - left + 1"))
        );
    }

    private static List<ProblemStep> stepsForLongestSubstring() {
        return List.of(
                step("1", "题目要求我求什么？", "你要返回什么？", "C", "返回不含重复字符的最长子串长度，而不是子串本身。",
                        option("A", "所有无重复子串"),
                        option("B", "最短无重复子串"),
                        option("C", "最长无重复子串长度"),
                        option("D", "字符出现次数")),
                step("2", "输入是什么？", "输入数据类型是？", "A", "输入是一个字符串 s。",
                        option("A", "字符串 s"),
                        option("B", "整数数组"),
                        option("C", "两个整数"),
                        option("D", "布尔值")),
                step("3", "输出是什么？", "返回值类型是什么？", "B", "要返回长度，所以输出是 int。",
                        option("A", "boolean"),
                        option("B", "int"),
                        option("C", "String"),
                        option("D", "int[]")),
                step("4", "关键词是什么？", "最典型的解法是？", "D", "无重复子串通常用滑动窗口维护当前合法区间。",
                        option("A", "栈匹配"),
                        option("B", "排序"),
                        option("C", "前缀和"),
                        option("D", "滑动窗口")),
                step("5", "需要维护哪些变量？", "最关键的变量组合是什么？", "A", "需要 left、right、当前最好答案 best，以及字符最近出现位置的 map。",
                        option("A", "left、right、best、字符位置 map"),
                        option("B", "stack 和 queue"),
                        option("C", "product 和 count"),
                        option("D", "row 和 col")),
                step("6", "关键判断条件是什么？", "什么时候需要移动 left？", "C", "当当前字符 ch 已经在窗口内出现过时，需要把 left 移到重复字符上次位置之后。",
                        option("A", "当 right 到达末尾"),
                        option("B", "当 ch 是数字"),
                        option("C", "当当前字符在窗口内重复"),
                        option("D", "当子串长度为 1")),
                step("7", "什么时候更新答案？", "best 在什么时候更新？", "B", "每次窗口重新合法后，用当前窗口长度 right - left + 1 更新 best。",
                        option("A", "每次 left 右移时直接减一"),
                        option("B", "窗口合法后用当前长度更新 best"),
                        option("C", "只在字符串结束时更新"),
                        option("D", "发现重复时更新为 0"))
        );
    }
}
