-- V3: Seed problems 1-6, 8-10; add steps for all 10 problems
-- Step IDs: p1=1-7, p2=8-14, p3=15-21, p4=22-28, p5=29-35, p6=36-42,
--           p7=43-49, p8=50-56, p9=57-63, p10=64-70
-- Option IDs: step N -> options 4*(N-1)+1 .. 4*N

-- ─── Problems 1-6, 8-10 ───────────────────────────────────────────────────

INSERT INTO problems (id, title, difficulty, category, description, starter_code, sort_order, status, source, reference_solution)
VALUES
(1, '判断质数', 'EASY', '数学',
 '给定一个整数 n，判断它是否是质数。如果 n 只能被 1 和它本身整除，则返回 true，否则返回 false。',
 $CODE$public class Solution {
    public boolean isPrime(int n) {
        return false;
    }
}$CODE$,
 0, 'published', 'builtin',
 $CODE$public class Solution {
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
}$CODE$),

(2, '打印 # 三角形', 'EASY', '模拟',
 '给定整数 n，打印一个由 # 组成的直角三角形，共 n 行，第 i 行包含 i 个 #。',
 $CODE$public class Solution {
    public String printTriangle(int n) {
        return "";
    }
}$CODE$,
 1, 'published', 'builtin',
 $CODE$public class Solution {
    public String printTriangle(int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            builder.append("#".repeat(i));
            if (i < n) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}$CODE$),

(3, '杨辉三角', 'EASY', '数组',
 '给定整数 numRows，返回杨辉三角的前 numRows 行。',
 $CODE$import java.util.*;

public class Solution {
    public List<List<Integer>> generate(int numRows) {
        return new ArrayList<>();
    }
}$CODE$,
 2, 'published', 'builtin',
 $CODE$import java.util.*;

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
}$CODE$),

(4, '数组最大值', 'EASY', '数组',
 '给定一个整数数组 nums，返回数组中的最大值。',
 $CODE$public class Solution {
    public int maxValue(int[] nums) {
        return 0;
    }
}$CODE$,
 3, 'published', 'builtin',
 $CODE$public class Solution {
    public int maxValue(int[] nums) {
        int max = nums[0];
        for (int num : nums) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }
}$CODE$),

(5, '数组反转', 'EASY', '双指针',
 '给定一个整数数组 nums，原地将数组反转。',
 $CODE$public class Solution {
    public void reverse(int[] nums) {

    }
}$CODE$,
 4, 'published', 'builtin',
 $CODE$public class Solution {
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
}$CODE$),

(6, '移动零', 'EASY', '双指针',
 '给定一个数组 nums，将所有 0 移动到末尾，同时保持非零元素的相对顺序。',
 $CODE$public class Solution {
    public void moveZeroes(int[] nums) {

    }
}$CODE$,
 5, 'published', 'builtin',
 $CODE$public class Solution {
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
}$CODE$),

(8, '有效括号', 'MEDIUM', '栈',
 '给定一个仅包含 ()[]{} 的字符串，判断字符串是否有效。',
 $CODE$import java.util.*;

public class Solution {
    public boolean isValid(String s) {
        return false;
    }
}$CODE$,
 7, 'published', 'builtin',
 $CODE$import java.util.*;

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
}$CODE$),

(9, '乘积小于 k 的子数组', 'MEDIUM', '滑动窗口',
 '给定一个正整数数组 nums 和整数 k，返回乘积严格小于 k 的连续子数组个数。',
 $CODE$public class Solution {
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        return 0;
    }
}$CODE$,
 8, 'published', 'builtin',
 $CODE$public class Solution {
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
}$CODE$),

(10, '无重复字符最长子串', 'MEDIUM', '滑动窗口',
 '给定一个字符串 s，返回不含重复字符的最长子串长度。',
 $CODE$import java.util.*;

public class Solution {
    public int lengthOfLongestSubstring(String s) {
        return 0;
    }
}$CODE$,
 9, 'published', 'builtin',
 $CODE$import java.util.*;

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
}$CODE$);

-- Update problem 7 sort_order (already inserted in V2 without it)
UPDATE problems SET sort_order = 6 WHERE id = 7;

-- ─── Examples 1-6, 8-10 ──────────────────────────────────────────────────

INSERT INTO problem_examples (problem_id, input, output, explanation, sort_order) VALUES
(1, 'n = 7',  'true',  '7 只能被 1 和 7 整除。', 0),
(1, 'n = 12', 'false', '12 可以被 2、3、4、6 整除。', 1),
(2, 'n = 3',  '#\n##\n###', '每一行比上一行多一个 #。', 0),
(3, 'numRows = 5', '[[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]', '每个内部元素等于上一行相邻两个元素之和。', 0),
(4, 'nums = [3,1,5,2]', '5', '遍历过程中持续维护最大值。', 0),
(5, 'nums = [1,2,3,4]', '[4,3,2,1]', '首尾双指针交换。', 0),
(6, 'nums = [0,1,0,3,12]', '[1,3,12,0,0]', '使用慢指针维护下一个非零元素应放置的位置。', 0),
(8, 's = "()[]{}"', 'true',  '左括号入栈，右括号匹配栈顶。', 0),
(8, 's = "(]"',    'false', '括号类型不匹配。', 1),
(9, 'nums = [10,5,2,6], k = 100', '8', '维护一个乘积小于 k 的滑动窗口。', 0),
(10, 's = "abcabcbb"', '3', '最长无重复子串是 "abc"。', 0);

-- ─── Judge specs 1-6, 8-10 ───────────────────────────────────────────────

INSERT INTO problem_judge_specs (problem_id, method_name, params, return_type, output_target, comparison_strategy, time_limit_ms, memory_limit_mb)
VALUES
(1,  'isPrime',                     '[{"name":"n","type":"int"}]',                                          'boolean',          NULL,   'EXACT',          5000, 256),
(2,  'printTriangle',               '[{"name":"n","type":"int"}]',                                          'String',           NULL,   'TEXT_NORMALIZE',  5000, 256),
(3,  'generate',                    '[{"name":"numRows","type":"int"}]',                                    'List<List<Integer>>', NULL, 'EXACT',          5000, 256),
(4,  'maxValue',                    '[{"name":"nums","type":"int[]"}]',                                     'int',              NULL,   'EXACT',          5000, 256),
(5,  'reverse',                     '[{"name":"nums","type":"int[]"}]',                                     'void',             'nums', 'EXACT',          5000, 256),
(6,  'moveZeroes',                  '[{"name":"nums","type":"int[]"}]',                                     'void',             'nums', 'EXACT',          5000, 256),
(8,  'isValid',                     '[{"name":"s","type":"String"}]',                                       'boolean',          NULL,   'EXACT',          5000, 256),
(9,  'numSubarrayProductLessThanK', '[{"name":"nums","type":"int[]"},{"name":"k","type":"int"}]',           'int',              NULL,   'EXACT',          5000, 256),
(10, 'lengthOfLongestSubstring',    '[{"name":"s","type":"String"}]',                                       'int',              NULL,   'EXACT',          5000, 256);

-- ─── Test cases ───────────────────────────────────────────────────────────

-- Problem 1: isPrime → boolean
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(1, 'n = 2',  '[{"name":"n","type":"int","value":2}]',  '{"value":true}',  true,  0),
(1, 'n = 9',  '[{"name":"n","type":"int","value":9}]',  '{"value":false}', false, 1),
(1, 'n = 29', '[{"name":"n","type":"int","value":29}]', '{"value":true}',  false, 2),
(1, 'n = 1',  '[{"name":"n","type":"int","value":1}]',  '{"value":false}', false, 3);

-- Problem 2: printTriangle → String (TEXT_NORMALIZE)
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(2, 'n = 1', '[{"name":"n","type":"int","value":1}]', '{"value":"#"}',           true,  0),
(2, 'n = 3', '[{"name":"n","type":"int","value":3}]', '{"value":"#\n##\n###"}',  false, 1),
(2, 'n = 5', '[{"name":"n","type":"int","value":5}]', '{"value":"#\n##\n###\n####\n#####"}', false, 2);

-- Problem 3: generate → List<List<Integer>>
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(3, 'numRows = 1', '[{"name":"numRows","type":"int","value":1}]', '{"value":[[1]]}',                                      true,  0),
(3, 'numRows = 3', '[{"name":"numRows","type":"int","value":3}]', '{"value":[[1],[1,1],[1,2,1]]}',                         false, 1),
(3, 'numRows = 5', '[{"name":"numRows","type":"int","value":5}]', '{"value":[[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]}',  false, 2);

-- Problem 4: maxValue → int
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(4, 'nums = [3, 1, 5, 2]', '[{"name":"nums","type":"int[]","value":[3,1,5,2]}]', '{"value":5}',  true,  0),
(4, 'nums = [-7, -3, -9]', '[{"name":"nums","type":"int[]","value":[-7,-3,-9]}]', '{"value":-3}', false, 1),
(4, 'nums = [8]',           '[{"name":"nums","type":"int[]","value":[8]}]',        '{"value":8}',  false, 2);

-- Problem 5: reverse → void (output=nums)
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(5, 'nums = [1, 2, 3, 4]', '[{"name":"nums","type":"int[]","value":[1,2,3,4]}]', '{"value":[4,3,2,1]}', true,  0),
(5, 'nums = [5, 6, 7]',    '[{"name":"nums","type":"int[]","value":[5,6,7]}]',   '{"value":[7,6,5]}',   false, 1),
(5, 'nums = [9]',           '[{"name":"nums","type":"int[]","value":[9]}]',       '{"value":[9]}',       false, 2);

-- Problem 6: moveZeroes → void (output=nums)
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(6, 'nums = [0, 1, 0, 3, 12]', '[{"name":"nums","type":"int[]","value":[0,1,0,3,12]}]', '{"value":[1,3,12,0,0]}', true,  0),
(6, 'nums = [0, 0, 1]',        '[{"name":"nums","type":"int[]","value":[0,0,1]}]',       '{"value":[1,0,0]}',      false, 1),
(6, 'nums = [4, 5, 6]',        '[{"name":"nums","type":"int[]","value":[4,5,6]}]',       '{"value":[4,5,6]}',      false, 2);

-- Problem 8: isValid → boolean
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(8, 's = "()"',     '[{"name":"s","type":"String","value":"()"}]',     '{"value":true}',  true,  0),
(8, 's = "()[]{}"', '[{"name":"s","type":"String","value":"()[]{}"}]', '{"value":true}',  false, 1),
(8, 's = "(]"',     '[{"name":"s","type":"String","value":"(]"}]',     '{"value":false}', false, 2),
(8, 's = "([)]"',   '[{"name":"s","type":"String","value":"([)]"}]',   '{"value":false}', false, 3),
(8, 's = "{[]}"',   '[{"name":"s","type":"String","value":"{[]}"}]',   '{"value":true}',  false, 4);

-- Problem 9: numSubarrayProductLessThanK → int
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(9, 'nums = [10, 5, 2, 6], k = 100', '[{"name":"nums","type":"int[]","value":[10,5,2,6]},{"name":"k","type":"int","value":100}]', '{"value":8}', true,  0),
(9, 'nums = [1, 2, 3], k = 0',       '[{"name":"nums","type":"int[]","value":[1,2,3]},{"name":"k","type":"int","value":0}]',      '{"value":0}', false, 1),
(9, 'nums = [1, 1, 1], k = 2',       '[{"name":"nums","type":"int[]","value":[1,1,1]},{"name":"k","type":"int","value":2}]',      '{"value":6}', false, 2);

-- Problem 10: lengthOfLongestSubstring → int
INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order) VALUES
(10, 's = "abcabcbb"', '[{"name":"s","type":"String","value":"abcabcbb"}]', '{"value":3}', true,  0),
(10, 's = "bbbbb"',    '[{"name":"s","type":"String","value":"bbbbb"}]',    '{"value":1}', false, 1),
(10, 's = "pwwkew"',   '[{"name":"s","type":"String","value":"pwwkew"}]',   '{"value":3}', false, 2),
(10, 's = ""',         '[{"name":"s","type":"String","value":""}]',         '{"value":0}', false, 3),
(10, 's = "dvdf"',     '[{"name":"s","type":"String","value":"dvdf"}]',     '{"value":3}', false, 4);

-- ─── Steps for all 10 problems ────────────────────────────────────────────
-- Problem 1 (isPrime): step_ids 1-7
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(1,  1, '1', '题目要求我求什么？', '这道题最终要你返回什么结果？', 'single_choice', 'B', '目标不是统计质数个数，也不是输出因子，而是判断单个整数 n 是否为质数。', 0),
(2,  1, '2', '输入是什么？', '输入数据的形态是什么？', 'single_choice', 'A', '输入只有一个整数 n，所以你不需要处理数组或字符串遍历。', 1),
(3,  1, '3', '输出是什么？', '方法返回值应该是什么？', 'single_choice', 'C', '如果 n 是质数返回 true，否则返回 false，所以输出是布尔值。', 2),
(4,  1, '4', '关键词是什么？', '题目里最关键的判断线索是什么？', 'single_choice', 'D', '质数的定义是只能被 1 和它本身整除，所以关键在于检查是否存在其他因子。', 3),
(5,  1, '5', '需要维护哪些变量？', '做这题最核心的循环变量是什么？', 'single_choice', 'A', '通常维护一个从 2 开始的除数 i，并检查 i * i <= n 即可。', 4),
(6,  1, '6', '关键判断条件是什么？', '什么时候可以立刻判定它不是质数？', 'single_choice', 'B', '只要发现 n % i == 0，说明存在其他因子，可以直接返回 false。', 5),
(7,  1, '7', '什么时候更新答案？', '这题在什么时机可以确定最终答案？', 'single_choice', 'C', '如果循环中没找到任何因子，循环结束后才能返回 true；特殊情况 n <= 1 先返回 false。', 6);

-- Problem 2 (printTriangle): step_ids 8-14
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(8,  2, '1', '题目要求我求什么？', '最终要构造什么结果？', 'single_choice', 'A', '这题要求返回一个由多行 # 组成的字符串，不是直接打印到控制台。', 0),
(9,  2, '2', '输入是什么？', '输入代表什么？', 'single_choice', 'D', '输入是一个整数 n，表示三角形有 n 行。', 1),
(10, 2, '3', '输出是什么？', '返回值类型是什么？', 'single_choice', 'B', '因为题目要求返回整段三角形文本，所以输出是字符串。', 2),
(11, 2, '4', '关键词是什么？', '最明显的构造模式是什么？', 'single_choice', 'C', '第 i 行有 i 个 #，并且相邻两行之间用换行连接。', 3),
(12, 2, '5', '需要维护哪些变量？', '构造字符串时最常用哪些变量？', 'single_choice', 'A', '一般用 StringBuilder 存结果，再用行号 i 控制每一行的 # 数量。', 4),
(13, 2, '6', '关键判断条件是什么？', '什么时候需要补换行？', 'single_choice', 'D', '通常在当前行不是最后一行时追加换行，避免结尾格式多出无意义字符。', 5),
(14, 2, '7', '什么时候更新答案？', '结果字符串在什么时候增长？', 'single_choice', 'B', '每构造完一行，就把该行内容追加进总结果。', 6);

-- Problem 3 (generate/Pascal): step_ids 15-21
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(15, 3, '1', '题目要求我求什么？', '你需要返回什么结构？', 'single_choice', 'B', '要返回杨辉三角前 numRows 行的二维列表。', 0),
(16, 3, '2', '输入是什么？', 'numRows 表示什么？', 'single_choice', 'A', 'numRows 表示要生成多少行杨辉三角。', 1),
(17, 3, '3', '输出是什么？', '方法返回值类型最贴切的是？', 'single_choice', 'D', '输出是嵌套列表，外层按行存储，内层存每行数字。', 2),
(18, 3, '4', '关键词是什么？', '杨辉三角内部元素怎么得到？', 'single_choice', 'C', '非边界位置等于上一行左上和右上的两个数之和。', 3),
(19, 3, '5', '需要维护哪些变量？', '构造时通常会维护什么？', 'single_choice', 'B', '通常维护总结果 result、当前行 current、行号 row 和列号 col。', 4),
(20, 3, '6', '关键判断条件是什么？', '哪些位置必定是 1？', 'single_choice', 'A', '每一行的首尾位置一定是 1，这是边界条件。', 5),
(21, 3, '7', '什么时候更新答案？', '总结果何时追加一整行？', 'single_choice', 'D', '一整行 current 构造完毕后，再加入 result。', 6);

-- Problem 4 (maxValue): step_ids 22-28
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(22, 4, '1', '题目要求我求什么？', '最终需要找出什么？', 'single_choice', 'C', '要返回数组中的最大值，不是下标，也不是排序结果。', 0),
(23, 4, '2', '输入是什么？', '输入数据类型是什么？', 'single_choice', 'A', '输入是一个整数数组 nums。', 1),
(24, 4, '3', '输出是什么？', '返回值类型是什么？', 'single_choice', 'B', '返回数组中的一个最大整数，所以输出是 int。', 2),
(25, 4, '4', '关键词是什么？', '最自然的做法是什么？', 'single_choice', 'D', '从头遍历数组，持续维护当前最大值。', 3),
(26, 4, '5', '需要维护哪些变量？', '核心变量是什么？', 'single_choice', 'A', '需要一个 max 变量记录当前最大值。', 4),
(27, 4, '6', '关键判断条件是什么？', '什么时候更新 max？', 'single_choice', 'C', '当当前元素 num > max 时，说明找到了更大的值。', 5),
(28, 4, '7', '什么时候更新答案？', 'max 在什么时候变化？', 'single_choice', 'B', '每次遇到更大的元素就更新 max，遍历结束后返回。', 6);

-- Problem 5 (reverse): step_ids 29-35
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(29, 5, '1', '题目要求我求什么？', '题目希望你完成什么操作？', 'single_choice', 'D', '需要原地反转数组，不是返回新数组的排序结果。', 0),
(30, 5, '2', '输入是什么？', '输入形态是什么？', 'single_choice', 'A', '输入是一个整数数组 nums。', 1),
(31, 5, '3', '输出是什么？', '方法返回类型是什么？', 'single_choice', 'C', '方法签名是 void，说明直接修改原数组即可。', 2),
(32, 5, '4', '关键词是什么？', '最典型的思路是什么？', 'single_choice', 'B', '用首尾双指针相向移动并交换元素。', 3),
(33, 5, '5', '需要维护哪些变量？', '核心变量是哪两个？', 'single_choice', 'A', '需要 left 和 right 指向当前待交换的首尾位置。', 4),
(34, 5, '6', '关键判断条件是什么？', '循环什么时候继续？', 'single_choice', 'D', '当 left < right 时，说明还存在待交换的两个位置。', 5),
(35, 5, '7', '什么时候更新答案？', '数组内容何时发生变化？', 'single_choice', 'C', '每次交换 nums[left] 和 nums[right] 后，数组就完成一部分反转。', 6);

-- Problem 6 (moveZeroes): step_ids 36-42
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(36, 6, '1', '题目要求我求什么？', '这题要完成什么效果？', 'single_choice', 'B', '要把所有 0 挪到末尾，同时保持非零元素的相对顺序。', 0),
(37, 6, '2', '输入是什么？', '输入是？', 'single_choice', 'C', '输入是整数数组 nums，题目要求原地修改。', 1),
(38, 6, '3', '输出是什么？', '返回值类型是什么？', 'single_choice', 'A', '方法返回 void，结果体现在 nums 的内容变化上。', 2),
(39, 6, '4', '关键词是什么？', '最合适的思路是什么？', 'single_choice', 'D', '用双指针或写指针，把非零元素依次写到前面。', 3),
(40, 6, '5', '需要维护哪些变量？', '最关键的变量是什么？', 'single_choice', 'B', '通常维护 index 或 slow 指针，表示下一个非零元素该放的位置。', 4),
(41, 6, '6', '关键判断条件是什么？', '什么时候把元素往前写？', 'single_choice', 'A', '当 nums[i] != 0 时，把它写到 slow 位置。', 5),
(42, 6, '7', '什么时候更新答案？', '0 在什么时候补到后面？', 'single_choice', 'C', '前面写完所有非零元素后，再把剩余位置填成 0。', 6);

-- Problem 7 (twoSum): step_ids 43-49
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(43, 7, '1', '题目要求我求什么？', '最终需要返回什么？', 'single_choice', 'C', '要求返回两数之和等于 target 的两个下标，而不是两个数本身。', 0),
(44, 7, '2', '输入是什么？', '输入包含哪些内容？', 'single_choice', 'B', '输入有一个数组 nums 和一个目标值 target。', 1),
(45, 7, '3', '输出是什么？', '方法返回值最贴切的是？', 'single_choice', 'A', '返回两个下标组成的数组，所以输出是 int[]。', 2),
(46, 7, '4', '关键词是什么？', '本题的关键字最接近哪一个？', 'single_choice', 'D', '边遍历边查找补数，是典型的哈希表题。', 3),
(47, 7, '5', '需要维护哪些变量？', '除了循环下标，还要维护什么？', 'single_choice', 'A', '核心是一个 map，记录"数值 -> 下标"的映射。', 4),
(48, 7, '6', '关键判断条件是什么？', '什么时候可以确定答案并返回？', 'single_choice', 'B', '如果当前值的补数已经在 map 里，就找到了答案。', 5),
(49, 7, '7', '什么时候更新答案？', 'map 和答案分别何时更新？', 'single_choice', 'D', '先查补数；如果没找到，再把当前数和下标放入 map。找到补数时立即返回答案。', 6);

-- Problem 8 (isValid): step_ids 50-56
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(50, 8, '1', '题目要求我求什么？', '最终要判断什么？', 'single_choice', 'A', '要判断括号字符串是否有效匹配。', 0),
(51, 8, '2', '输入是什么？', '输入是什么类型？', 'single_choice', 'C', '输入是一个只含括号字符的字符串 s。', 1),
(52, 8, '3', '输出是什么？', '返回值类型是什么？', 'single_choice', 'B', '有效返回 true，无效返回 false。', 2),
(53, 8, '4', '关键词是什么？', '最标准的思路是？', 'single_choice', 'D', '括号匹配最典型的数据结构就是栈。', 3),
(54, 8, '5', '需要维护哪些变量？', '核心数据结构是什么？', 'single_choice', 'A', '需要一个 stack 保存还未匹配的左括号。', 4),
(55, 8, '6', '关键判断条件是什么？', '什么时候可以直接判无效？', 'single_choice', 'C', '遇到右括号时，如果栈空或类型不匹配，就可以返回 false。', 5),
(56, 8, '7', '什么时候更新答案？', '最终何时确定为有效？', 'single_choice', 'B', '遍历完成后，如果栈为空，说明所有括号都配对成功。', 6);

-- Problem 9 (productLessThanK): step_ids 57-63
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(57, 9, '1', '题目要求我求什么？', '你最终要返回哪个量？', 'single_choice', 'A', '题目要求返回乘积严格小于 k 的连续子数组个数，不是最长长度。', 0),
(58, 9, '2', '输入是什么？', '输入包含什么？', 'single_choice', 'B', '输入有正整数数组 nums 和整数 k。', 1),
(59, 9, '3', '输出是什么？', '返回值类型是什么？', 'single_choice', 'D', '输出是满足条件的个数，所以返回 int。', 2),
(60, 9, '4', '关键词是什么？', '本题最关键的模式是？', 'single_choice', 'C', '这是典型的滑动窗口题，因为数组元素都是正数，窗口乘积有单调性。', 3),
(61, 9, '5', '需要维护哪些变量？', '核心变量组合是哪一组？', 'single_choice', 'A', '通常维护 left、right、当前窗口乘积 product，以及累计答案 count。', 4),
(62, 9, '6', '关键判断条件是什么？', '什么时候要缩小窗口？', 'single_choice', 'B', '当 product >= k 时，当前窗口不合法，需要移动 left 并把左端元素除掉。', 5),
(63, 9, '7', '什么时候更新答案？', '合法窗口形成后，一次新增多少个答案？', 'single_choice', 'D', '当窗口合法时，以 right 结尾的新增子数组个数是 right - left + 1。', 6);

-- Problem 10 (longestSubstring): step_ids 64-70
INSERT INTO problem_steps (id, problem_id, step_key, title, content, type, answer, explanation, sort_order) VALUES
(64, 10, '1', '题目要求我求什么？', '你要返回什么？', 'single_choice', 'C', '返回不含重复字符的最长子串长度，而不是子串本身。', 0),
(65, 10, '2', '输入是什么？', '输入数据类型是？', 'single_choice', 'A', '输入是一个字符串 s。', 1),
(66, 10, '3', '输出是什么？', '返回值类型是什么？', 'single_choice', 'B', '要返回长度，所以输出是 int。', 2),
(67, 10, '4', '关键词是什么？', '最典型的解法是？', 'single_choice', 'D', '无重复子串通常用滑动窗口维护当前合法区间。', 3),
(68, 10, '5', '需要维护哪些变量？', '最关键的变量组合是什么？', 'single_choice', 'A', '需要 left、right、当前最好答案 best，以及字符最近出现位置的 map。', 4),
(69, 10, '6', '关键判断条件是什么？', '什么时候需要移动 left？', 'single_choice', 'C', '当当前字符 ch 已经在窗口内出现过时，需要把 left 移到重复字符上次位置之后。', 5),
(70, 10, '7', '什么时候更新答案？', 'best 在什么时候更新？', 'single_choice', 'B', '每次窗口重新合法后，用当前窗口长度 right - left + 1 更新 best。', 6);

-- ─── Step options (280 rows = 70 steps × 4 options) ──────────────────────

-- Problem 1, steps 1-7 → option ids 1-28
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(1,  1, 'A', '返回 1 到 n 之间一共有多少个质数', 0),
(2,  1, 'B', '判断整数 n 是否是质数，返回 true 或 false', 1),
(3,  1, 'C', '输出 n 的所有质因子', 2),
(4,  1, 'D', '找到第 n 个质数', 3),
(5,  2, 'A', '一个整数 n', 0),
(6,  2, 'B', '一个整数数组 nums', 1),
(7,  2, 'C', '一个字符串 s', 2),
(8,  2, 'D', '两个整数 n 和 k', 3),
(9,  3, 'A', '一个整数', 0),
(10, 3, 'B', '一个字符串', 1),
(11, 3, 'C', '一个布尔值', 2),
(12, 3, 'D', '一个整数数组', 3),
(13, 4, 'A', '连续子数组', 0),
(14, 4, 'B', '左右指针交换', 1),
(15, 4, 'C', '哈希表查找补数', 2),
(16, 4, 'D', '是否存在 1 和自身之外的因子', 3),
(17, 5, 'A', '候选除数 i', 0),
(18, 5, 'B', '窗口左右边界 left/right', 1),
(19, 5, 'C', '答案数组 ans', 2),
(20, 5, 'D', '栈 stack', 3),
(21, 6, 'A', '当 n > i 时', 0),
(22, 6, 'B', '当 n % i == 0 时', 1),
(23, 6, 'C', '当 i == 1 时', 2),
(24, 6, 'D', '当 n 是偶数时一定返回 false', 3),
(25, 7, 'A', '每轮循环都把答案加一', 0),
(26, 7, 'B', '一进入方法就返回 true', 1),
(27, 7, 'C', '检查完所有候选除数仍未发现因子时返回 true', 2),
(28, 7, 'D', '找到第一个不能整除的 i 就返回 true', 3);

-- Problem 2, steps 8-14 → option ids 29-56
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(29, 8,  'A', '返回一个由 # 构成的三角形字符串', 0),
(30, 8,  'B', '返回三角形总共有多少个 #', 1),
(31, 8,  'C', '返回一个整数数组', 2),
(32, 8,  'D', '判断 # 是否成对出现', 3),
(33, 9,  'A', '一个字符串', 0),
(34, 9,  'B', '一个数组', 1),
(35, 9,  'C', '两个整数', 2),
(36, 9,  'D', '一个整数 n，表示行数', 3),
(37, 10, 'A', '布尔值', 0),
(38, 10, 'B', '字符串', 1),
(39, 10, 'C', '整数', 2),
(40, 10, 'D', '二维数组', 3),
(41, 11, 'A', '哈希查重', 0),
(42, 11, 'B', '滑动窗口', 1),
(43, 11, 'C', '第 i 行放 i 个 #', 2),
(44, 11, 'D', '二分查找', 3),
(45, 12, 'A', 'StringBuilder 和行号 i', 0),
(46, 12, 'B', '栈和队列', 1),
(47, 12, 'C', 'left/right 双指针', 2),
(48, 12, 'D', '乘积 product', 3),
(49, 13, 'A', '当 i 是偶数时', 0),
(50, 13, 'B', '当 # 总数超过 n 时', 1),
(51, 13, 'C', '每个 # 后面都补换行', 2),
(52, 13, 'D', '当前行不是最后一行时追加换行', 3),
(53, 14, 'A', '只在第一行更新一次', 0),
(54, 14, 'B', '每构造完一行后追加到结果', 1),
(55, 14, 'C', '循环结束后统一替换', 2),
(56, 14, 'D', '找到质数时更新', 3);

-- Problem 3, steps 15-21 → option ids 57-84
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(57, 15, 'A', '一维整数数组', 0),
(58, 15, 'B', '前 numRows 行的二维列表', 1),
(59, 15, 'C', '布尔值', 2),
(60, 15, 'D', '单行字符串', 3),
(61, 16, 'A', '需要生成的行数', 0),
(62, 16, 'B', '每行的固定列数', 1),
(63, 16, 'C', '目标和', 2),
(64, 16, 'D', '字符串长度', 3),
(65, 17, 'A', 'String', 0),
(66, 17, 'B', 'int[]', 1),
(67, 17, 'C', 'boolean', 2),
(68, 17, 'D', 'List<List<Integer>>', 3),
(69, 18, 'A', '双指针交换', 0),
(70, 18, 'B', '哈希映射补数', 1),
(71, 18, 'C', '上一行相邻两个数之和', 2),
(72, 18, 'D', '窗口乘积', 3),
(73, 19, 'A', '只需要一个布尔值', 0),
(74, 19, 'B', 'result、current、row、col', 1),
(75, 19, 'C', '栈和队列', 2),
(76, 19, 'D', 'left、right', 3),
(77, 20, 'A', '每行首尾位置', 0),
(78, 20, 'B', '所有偶数列', 1),
(79, 20, 'C', '中间位置', 2),
(80, 20, 'D', '最后一行全部位置', 3),
(81, 21, 'A', '每放一个数就立刻返回', 0),
(82, 21, 'B', '一开始就加入空行', 1),
(83, 21, 'C', '只加入最后一行', 2),
(84, 21, 'D', '当前行构造完成后加入 result', 3);

-- Problem 4, steps 22-28 → option ids 85-112
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(85,  22, 'A', '最小值', 0),
(86,  22, 'B', '最大值的下标', 1),
(87,  22, 'C', '数组中的最大值', 2),
(88,  22, 'D', '排序后的数组', 3),
(89,  23, 'A', '整数数组 nums', 0),
(90,  23, 'B', '字符串 s', 1),
(91,  23, 'C', '单个整数 n', 2),
(92,  23, 'D', '二维数组', 3),
(93,  24, 'A', 'boolean', 0),
(94,  24, 'B', 'int', 1),
(95,  24, 'C', 'String', 2),
(96,  24, 'D', 'int[]', 3),
(97,  25, 'A', '滑动窗口', 0),
(98,  25, 'B', '递归回溯', 1),
(99,  25, 'C', '栈匹配', 2),
(100, 25, 'D', '遍历并维护最大值', 3),
(101, 26, 'A', 'max', 0),
(102, 26, 'B', 'target', 1),
(103, 26, 'C', 'stack', 2),
(104, 26, 'D', 'answer[]', 3),
(105, 27, 'A', 'num < max', 0),
(106, 27, 'B', 'num == max', 1),
(107, 27, 'C', 'num > max', 2),
(108, 27, 'D', 'num % max == 0', 3),
(109, 28, 'A', '只在最后一个元素更新', 0),
(110, 28, 'B', '遇到更大元素时更新', 1),
(111, 28, 'C', '每轮都加一', 2),
(112, 28, 'D', '找到 0 时更新', 3);

-- Problem 5, steps 29-35 → option ids 113-140
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(113, 29, 'A', '统计数组和', 0),
(114, 29, 'B', '找到中位数', 1),
(115, 29, 'C', '升序排序', 2),
(116, 29, 'D', '原地反转数组', 3),
(117, 30, 'A', '整数数组 nums', 0),
(118, 30, 'B', '一个字符串', 1),
(119, 30, 'C', '两个整数', 2),
(120, 30, 'D', '一个布尔值', 3),
(121, 31, 'A', 'int', 0),
(122, 31, 'B', 'int[]', 1),
(123, 31, 'C', 'void', 2),
(124, 31, 'D', 'String', 3),
(125, 32, 'A', '前缀和', 0),
(126, 32, 'B', '双指针交换', 1),
(127, 32, 'C', '哈希映射', 2),
(128, 32, 'D', '滑动窗口', 3),
(129, 33, 'A', 'left 和 right', 0),
(130, 33, 'B', 'stack 和 queue', 1),
(131, 33, 'C', 'sum 和 count', 2),
(132, 33, 'D', 'target 和 index', 3),
(133, 34, 'A', 'left > right', 0),
(134, 34, 'B', 'left == 0', 1),
(135, 34, 'C', 'nums[left] > nums[right]', 2),
(136, 34, 'D', 'left < right', 3),
(137, 35, 'A', '循环结束后统一更新', 0),
(138, 35, 'B', '只更新中间元素', 1),
(139, 35, 'C', '每次交换后', 2),
(140, 35, 'D', '遇到重复值时', 3);

-- Problem 6, steps 36-42 → option ids 141-168
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(141, 36, 'A', '删除所有 0', 0),
(142, 36, 'B', '把 0 移到末尾且保持非零顺序', 1),
(143, 36, 'C', '统计 0 的个数', 2),
(144, 36, 'D', '反转数组', 3),
(145, 37, 'A', '字符串', 0),
(146, 37, 'B', '单个整数', 1),
(147, 37, 'C', '整数数组 nums', 2),
(148, 37, 'D', '二维矩阵', 3),
(149, 38, 'A', 'void', 0),
(150, 38, 'B', 'boolean', 1),
(151, 38, 'C', 'int', 2),
(152, 38, 'D', 'int[]', 3),
(153, 39, 'A', '二分查找', 0),
(154, 39, 'B', '栈匹配', 1),
(155, 39, 'C', '子串去重', 2),
(156, 39, 'D', '双指针/写指针覆盖', 3),
(157, 40, 'A', 'target', 0),
(158, 40, 'B', 'slow/index 指针', 1),
(159, 40, 'C', 'stack', 2),
(160, 40, 'D', 'result 字符串', 3),
(161, 41, 'A', 'nums[i] != 0', 0),
(162, 41, 'B', 'nums[i] == 0', 1),
(163, 41, 'C', 'nums[i] > nums[0]', 2),
(164, 41, 'D', 'i 为偶数', 3),
(165, 42, 'A', '遇到 0 时立即返回', 0),
(166, 42, 'B', '每次都排序', 1),
(167, 42, 'C', '非零元素写完后补 0', 2),
(168, 42, 'D', '只在第一轮循环更新', 3);

-- Problem 7, steps 43-49 → option ids 169-196
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(169, 43, 'A', '返回两个数字的乘积', 0),
(170, 43, 'B', '返回所有满足条件的组合', 1),
(171, 43, 'C', '返回两个下标', 2),
(172, 43, 'D', '返回 target 是否存在', 3),
(173, 44, 'A', '只有一个整数 n', 0),
(174, 44, 'B', '数组 nums 和目标值 target', 1),
(175, 44, 'C', '一个字符串 s', 2),
(176, 44, 'D', '一个二维矩阵', 3),
(177, 45, 'A', 'int[]', 0),
(178, 45, 'B', 'boolean', 1),
(179, 45, 'C', 'int', 2),
(180, 45, 'D', 'List<List<Integer>>', 3),
(181, 46, 'A', '滑动窗口', 0),
(182, 46, 'B', '单调栈', 1),
(183, 46, 'C', '双指针首尾夹逼', 2),
(184, 46, 'D', '哈希表查找补数', 3),
(185, 47, 'A', 'map: 数值 -> 下标', 0),
(186, 47, 'B', 'stack', 1),
(187, 47, 'C', 'product', 2),
(188, 47, 'D', 'builder', 3),
(189, 48, 'A', 'nums[i] > target', 0),
(190, 48, 'B', 'map 中已经存在 target - nums[i]', 1),
(191, 48, 'C', 'i 到达最后一个位置', 2),
(192, 48, 'D', '数组已经排序', 3),
(193, 49, 'A', '先把当前数放进 map，再无条件返回', 0),
(194, 49, 'B', '每轮都更新 answer', 1),
(195, 49, 'C', '遍历结束后统一构造答案', 2),
(196, 49, 'D', '先查补数，找不到再写入 map', 3);

-- Problem 8, steps 50-56 → option ids 197-224
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(197, 50, 'A', '字符串中的括号是否有效', 0),
(198, 50, 'B', '最长子串长度', 1),
(199, 50, 'C', '数组最大值', 2),
(200, 50, 'D', '是否存在两数之和', 3),
(201, 51, 'A', '整数', 0),
(202, 51, 'B', '整数数组', 1),
(203, 51, 'C', '字符串 s', 2),
(204, 51, 'D', '二维列表', 3),
(205, 52, 'A', 'int[]', 0),
(206, 52, 'B', 'boolean', 1),
(207, 52, 'C', 'String', 2),
(208, 52, 'D', 'void', 3),
(209, 53, 'A', '哈希表统计频率', 0),
(210, 53, 'B', '双指针交换', 1),
(211, 53, 'C', '滑动窗口', 2),
(212, 53, 'D', '栈匹配', 3),
(213, 54, 'A', 'stack', 0),
(214, 54, 'B', 'product', 1),
(215, 54, 'C', 'result 数组', 2),
(216, 54, 'D', 'builder', 3),
(217, 55, 'A', '遇到左括号', 0),
(218, 55, 'B', '字符串长度是偶数', 1),
(219, 55, 'C', '右括号无法匹配栈顶', 2),
(220, 55, 'D', '出现两个连续左括号', 3),
(221, 56, 'A', '遇到第一个左括号时', 0),
(222, 56, 'B', '遍历结束且栈为空时', 1),
(223, 56, 'C', '发现任意一对匹配时', 2),
(224, 56, 'D', '一开始就默认有效', 3);

-- Problem 9, steps 57-63 → option ids 225-252
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(225, 57, 'A', '满足条件的连续子数组个数', 0),
(226, 57, 'B', '满足条件的最长子数组长度', 1),
(227, 57, 'C', '最小乘积', 2),
(228, 57, 'D', '所有子数组内容', 3),
(229, 58, 'A', '字符串 s 和整数 k', 0),
(230, 58, 'B', '正整数数组 nums 和整数 k', 1),
(231, 58, 'C', '单个整数 n', 2),
(232, 58, 'D', '两个字符串', 3),
(233, 59, 'A', 'boolean', 0),
(234, 59, 'B', 'int[]', 1),
(235, 59, 'C', 'String', 2),
(236, 59, 'D', 'int', 3),
(237, 60, 'A', '并查集', 0),
(238, 60, 'B', '栈', 1),
(239, 60, 'C', '滑动窗口', 2),
(240, 60, 'D', '回溯', 3),
(241, 61, 'A', 'left、right、product、count', 0),
(242, 61, 'B', 'stack、queue、sum', 1),
(243, 61, 'C', 'map、target', 2),
(244, 61, 'D', 'builder、row、col', 3),
(245, 62, 'A', 'product < k', 0),
(246, 62, 'B', 'product >= k', 1),
(247, 62, 'C', 'right == left', 2),
(248, 62, 'D', 'nums[right] == 0', 3),
(249, 63, 'A', '每次只加 1', 0),
(250, 63, 'B', '只在窗口长度为 2 时更新', 1),
(251, 63, 'C', '在缩小窗口前更新', 2),
(252, 63, 'D', '窗口合法后加上 right - left + 1', 3);

-- Problem 10, steps 64-70 → option ids 253-280
INSERT INTO problem_step_options (id, step_id, label, content, sort_order) VALUES
(253, 64, 'A', '所有无重复子串', 0),
(254, 64, 'B', '最短无重复子串', 1),
(255, 64, 'C', '最长无重复子串长度', 2),
(256, 64, 'D', '字符出现次数', 3),
(257, 65, 'A', '字符串 s', 0),
(258, 65, 'B', '整数数组', 1),
(259, 65, 'C', '两个整数', 2),
(260, 65, 'D', '布尔值', 3),
(261, 66, 'A', 'boolean', 0),
(262, 66, 'B', 'int', 1),
(263, 66, 'C', 'String', 2),
(264, 66, 'D', 'int[]', 3),
(265, 67, 'A', '栈匹配', 0),
(266, 67, 'B', '排序', 1),
(267, 67, 'C', '前缀和', 2),
(268, 67, 'D', '滑动窗口', 3),
(269, 68, 'A', 'left、right、best、字符位置 map', 0),
(270, 68, 'B', 'stack 和 queue', 1),
(271, 68, 'C', 'product 和 count', 2),
(272, 68, 'D', 'row 和 col', 3),
(273, 69, 'A', '当 right 到达末尾', 0),
(274, 69, 'B', '当 ch 是数字', 1),
(275, 69, 'C', '当当前字符在窗口内重复', 2),
(276, 69, 'D', '当子串长度为 1', 3),
(277, 70, 'A', '每次 left 右移时直接减一', 0),
(278, 70, 'B', '窗口合法后用当前长度更新 best', 1),
(279, 70, 'C', '只在字符串结束时更新', 2),
(280, 70, 'D', '发现重复时更新为 0', 3);

-- Reset sequences
SELECT setval('problem_steps_id_seq', 70);
SELECT setval('problem_step_options_id_seq', 280);
