-- Seed problem 7: Two Sum

INSERT INTO problems (id, title, difficulty, category, description, starter_code, sort_order, status, source, reference_solution)
VALUES (
    7,
    '两数之和',
    'EASY',
    '哈希',
    '给定整数数组 nums 和目标值 target，返回两数之和等于 target 的两个下标。',
    E'import java.util.*;\n\npublic class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        return new int[0];\n    }\n}',
    6,
    'published',
    'builtin',
    E'import java.util.*;\n\npublic class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> indexMap = new HashMap<>();\n        for (int i = 0; i < nums.length; i++) {\n            int need = target - nums[i];\n            if (indexMap.containsKey(need)) {\n                return new int[]{indexMap.get(need), i};\n            }\n            indexMap.put(nums[i], i);\n        }\n        return new int[0];\n    }\n}'
);

INSERT INTO problem_examples (problem_id, input, output, explanation, sort_order)
VALUES (7, 'nums = [2,7,11,15], target = 9', '[0,1]', '可以边遍历边在哈希表中查找补数。', 0);

INSERT INTO problem_judge_specs (problem_id, method_name, params, return_type, output_target, comparison_strategy, time_limit_ms, memory_limit_mb)
VALUES (
    7,
    'twoSum',
    '[{"name":"nums","type":"int[]"},{"name":"target","type":"int"}]',
    'int[]',
    NULL,
    'EXACT',
    5000,
    256
);

INSERT INTO problem_test_cases (problem_id, display_input, inputs, expected, is_sample, sort_order)
VALUES
    (7, 'nums = [2, 7, 11, 15], target = 9',
     '[{"name":"nums","type":"int[]","value":[2,7,11,15]},{"name":"target","type":"int","value":9}]',
     '{"value":[0,1]}', true, 0),
    (7, 'nums = [3, 2, 4], target = 6',
     '[{"name":"nums","type":"int[]","value":[3,2,4]},{"name":"target","type":"int","value":6}]',
     '{"value":[1,2]}', false, 1),
    (7, 'nums = [3, 3], target = 6',
     '[{"name":"nums","type":"int[]","value":[3,3]},{"name":"target","type":"int","value":6}]',
     '{"value":[0,1]}', false, 2);
