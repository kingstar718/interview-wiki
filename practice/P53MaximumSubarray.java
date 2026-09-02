/**
 * LeetCode 53: 最大子数组和 (Maximum Subarray)
 * https://leetcode.cn/problems/maximum-subarray/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 动态规划(DP) · 分治(Divide and Conquer)
 * <p>【题型】线性扫描维护状态（Kadane）：只维护「以当前元素结尾的最大子数组和」
 * <p>【考点】能否想到「前面的累积和若为负就该丢弃」，用 O(1) 状态一趟求解；DP 视角与贪心视角的统一
 * <p>【关联】{@link P121BestTimeToBuyAndSellStock} —— 同为「一趟扫描维护一个最优状态」的线性 DP
 *
 * <p>给定整数数组 nums，找出一个具有最大和的连续子数组（至少含一个元素），返回其最大和。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：枚举所有子数组求和，O(n²)（或 O(n³)）。
 *
 * <p>2. 关键观察（Kadane）：设 cur 为「以 nums[i] 结尾的最大子数组和」。
 *    到 nums[i] 时，要么接在前一段后（cur + nums[i]），要么自己另起一段（nums[i]），取较大者。
 *    若前一段的 cur 已是负数，接上只会拖累，不如丢弃另起。
 *
 * <p>3. 核心技巧：一趟扫描，cur = max(nums[i], cur + nums[i])，同时用 best 记录全局最大。
 *    只维护两个变量，无需额外数组。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)。
 *    边界：全为负数时答案是「最大的那个负数」（至少取一个元素），故 best、cur 初值应设 nums[0] 而非 0。
 */
public class P53MaximumSubarray {

    public int maxSubArray(int[] nums) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P53MaximumSubarray p53 = new P53MaximumSubarray();

        assertEqual(6, p53.maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}), "Example 1");  // [4,-1,2,1]
        assertEqual(1, p53.maxSubArray(new int[]{1}), "Single element");
        assertEqual(23, p53.maxSubArray(new int[]{5, 4, -1, 7, 8}), "Mostly positive");
        assertEqual(-1, p53.maxSubArray(new int[]{-2, -1, -3}), "All negative → largest single");
        assertEqual(-5, p53.maxSubArray(new int[]{-5}), "Single negative");
        assertEqual(4, p53.maxSubArray(new int[]{-1, 3, -2, 3, -5}), "Middle segment [3,-2,3]");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(String.format("[FAIL] %s: expected %d, but got %d",
                testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
