import java.util.Arrays;

/**
 * LeetCode 53: 最大子数组和 (Maximum Subarray)
 * https://leetcode.cn/problems/maximum-subarray/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · 动态规划(DP) · 数组（一鱼两吃：DP 章第一块地基）
 * <p>【题型】Kadane：cur = max(nums[i], cur + nums[i])，历史 max 单独记账
 * <p>【考点】子数组至少一个元素（全负返回 max 单值，不能空躲 0）；贪心"负前缀丢弃"= dp 正装说法
 * <p>【关联】贪心入门三件套之三；DP 章后续 152 乘积最大子数组（负负得正的孪生变体）
 *
 * <p>给定整数数组 nums，找到和最大的【连续】子数组（至少一个元素），返回其最大和。
 *
 * <p>示例：[-2,1,-3,4,-1,2,1,-5,4] → 6（子数组 [4,-1,2,1]）；[-1] → -1；[-2,1] → 1
 * <p>约束：1 &lt;= nums.length &lt;= 10^5；-10^4 &lt;= nums[i] &lt;= 10^4
 *
 * <p>进阶（可选思考）：用 O(n) 一趟解决（贪心/DP 即是）；分治 O(n log n) 另一种写法。
 */
public class P53MaximumSubarray {

    /** 你的实现（Kadane：cur 续/重启二选一，maxSum 记历史巅峰）。 */
    public int maxSubArray(int[] nums) {
        int curSum = nums[0];
        int maxSum = nums[0];
        for (int i = 1; i < nums.length; i++) {
            curSum = Math.max(nums[i], curSum + nums[i]);
            maxSum = Math.max(maxSum, curSum);
        }
        return maxSum;
    }

    public static void main(String[] args) {
        P53MaximumSubarray solver = new P53MaximumSubarray();

        run(solver, new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}, "示例1 官方 → 6");
        run(solver, new int[]{1}, "单元素 → 1");
        run(solver, new int[]{-1}, "全负单元素 → -1（不能空躲 0）");
        run(solver, new int[]{-3, -1, -2}, "全负多元素 → -1（找最不负的那个）");
        run(solver, new int[]{-2, 1}, "先负后正 → 1");
        run(solver, new int[]{5, 4, -1, 7, 8}, "全正 → 全加 23");
        run(solver, new int[]{8, -19, 5, -4, 20}, "负断点重组 → 21 [5,-4,20]");
        run(solver, new int[]{-5, 8, -2, 3, -1, 6, -9}, "多段峰谷 → 14 [8,-2,3,-1,6]");
        run(solver, new int[]{0, 0, 0, -3, 0}, "零与负 → 0（全零子数组）");
        run(solver, new int[]{100, -100, 100}, "峰谷往返 → 100 还是 200？暴力定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举全部子数组 [l..r] 求最大和（O(n²)，循环累加不重算）。 */
    private static int bruteForce(int[] nums) {
        int best = Integer.MIN_VALUE;
        for (int l = 0; l < nums.length; l++) {
            int sum = 0;
            for (int r = l; r < nums.length; r++) {
                sum += nums[r];
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    private static void run(P53MaximumSubarray solver, int[] nums, String label) {
        try {
            int got = solver.maxSubArray(Arrays.copyOf(nums, nums.length));
            int bf = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == bf;

            System.out.printf("[%s] %-30s nums=%-34s | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), bf, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s nums=%-34s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s nums=%-34s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
