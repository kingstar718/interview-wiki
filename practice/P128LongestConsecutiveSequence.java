import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 128: 最长连续序列 (Longest Consecutive Sequence)
 * https://leetcode.cn/problems/longest-consecutive-sequence/
 *
 * <p>【难度】Medium
 * <p>【标签】哈希表(Hash Set) · 数组(Array) · 并查集(Union-Find，进阶解法)
 * <p>【题型】哈希去重 + 序列起点枚举；本质是「用 O(1) 查询把排序省掉」的经典套路
 * <p>【考点】能否想到用 HashSet 换掉排序，并用「只从起点扩展」把复杂度压到 O(n)
 * <p>【关联】{@link P3LongestSubstringWithoutRepeating} —— 同为「避免重复计算」把 O(n²) 压回 O(n)
 * <p>{@link P1TwoSum} —— 哈希表基础
 *
 * <p>给定一个未排序的整数数组 nums，找出数字连续的最长序列（不要求序列元素在原数组中连续）的长度。
 * 要求时间复杂度为 O(n)。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：先排序再扫描一遍，但排序是 O(n log n)，不满足 O(n) 的要求。
 *
 * <p>2. 关键观察：判断「一个数是否存在」用 HashSet 只要 O(1)。
 *    于是我们把所有数丢进 Set，就能从任意一个数出发，
 *    不断问「x+1 在不在？x+2 在不在？……」来数出它所在连续序列的长度。
 *
 * <p>3. 避免重复计算（这一步是 O(n) 的核心）：
 *    如果对每个数都向后数一遍，会重复扫描同一段序列（例如 [1,2,3,4] 会从 1、2、3、4 各数一次）。
 *    技巧：只从「序列的起点」开始数。
 *    什么是起点？就是 x-1 不在 Set 里的那个 x —— 它左边没有相邻的数，所以它是这段连续序列的最小值。
 *    这样每段连续序列只会被它的起点触发一次，内层 while 里每个数总共只被访问一次。
 *    => 总时间复杂度 O(n)。
 *
 * <p>⚠️ 易错点：外层必须遍历 **numSet** 而不是原数组 nums，否则重复元素会让复杂度退化。
 *    反例：nums = [1,1,1,...,1(n/2 个), 2,3,...,n/2]
 *      - 遍历 nums 时，每一个重复的 1 都满足「1-1 不在 Set 中」而被当作起点，
 *        于是把整条长度 n/2 的序列反复扫了 n/2 遍 => Θ(n²)，O(n) 的要求直接失效；
 *      - 遍历 numSet 则天然去重，每个起点只触发一次 => 稳定 O(n)。
 *    两种写法**答案一样**，测试用例也测不出差别，只有复杂度分析能发现 —— 面试官常在此追问。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(n)（HashSet）。
 *    重复元素被 Set 自动去重，负数也天然支持，无需特殊处理。
 */
public class P128LongestConsecutiveSequence {

    public int longestConsecutive(int[] nums) {
        // Step 1: 全部入 Set，实现 O(1) 的存在性查询 + 自动去重
        Set<Integer> numSet = new HashSet<>();
        for (int i : nums) {
            numSet.add(i);
        }

        int res = 0;
        // Step 2: 遍历 numSet 而非 nums —— 去重后每个值只处理一次，这是 O(n) 的必要条件
        for (int num : numSet) {
            // Step 3: 只处理「起点」—— num-1 不存在，说明 num 是某段连续序列的最小值
            if (!numSet.contains(num - 1)) {
                // 从起点开始向后延伸
                int curNum = num;
                int curLen = 1;

                // Step 4: 从起点向后逐一延伸，直到断链
                while (numSet.contains(++curNum)) {
                    curLen++;
                }

                // 更新全局最长长度
                res = Math.max(res, curLen);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P128LongestConsecutiveSequence p128 = new P128LongestConsecutiveSequence();

        // 示例 1
        int[] nums1 = {100, 4, 200, 1, 3, 2};
        assertEqual(4, p128.longestConsecutive(nums1), "Example 1");

        // 示例 2
        int[] nums2 = {0, 3, 7, 2, 5, 8, 4, 6, 0, 1};
        assertEqual(9, p128.longestConsecutive(nums2), "Example 2");

        // 空数组
        int[] nums3 = {};
        assertEqual(0, p128.longestConsecutive(nums3), "Empty array");

        // 单个元素
        int[] nums4 = {5};
        assertEqual(1, p128.longestConsecutive(nums4), "Single element");

        // 没有连续元素
        int[] nums5 = {10, 30, 50, 70};
        assertEqual(1, p128.longestConsecutive(nums5), "No consecutive");

        // 全部连续
        int[] nums6 = {1, 2, 3, 4, 5};
        assertEqual(5, p128.longestConsecutive(nums6), "All consecutive");

        // 包含负数
        int[] nums7 = {-5, -4, -3, -2, -1, 0, 1};
        assertEqual(7, p128.longestConsecutive(nums7), "Negative numbers");

        // 包含重复元素
        int[] nums8 = {1, 2, 2, 3, 3, 4};
        assertEqual(4, p128.longestConsecutive(nums8), "Duplicates");

        // 乱序且有间隔
        int[] nums9 = {9, 1, 4, 7, 3, 2, 6, 8};
        assertEqual(4, p128.longestConsecutive(nums9), "Unsorted with gaps");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(
                String.format("[FAIL] %s: expected %d, but got %d", testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
