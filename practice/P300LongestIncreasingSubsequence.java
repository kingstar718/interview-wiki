import java.util.Arrays;

/**
 * LeetCode 300: 最长递增子序列 (Longest Increasing Subsequence)
 * https://leetcode.cn/problems/longest-increasing-subsequence/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（子序列组 1/7——二维 DP 主战场开门）
 *
 * <p>■ 题目描述
 *   给一个整数数组 nums，找出其中【最长严格递增子序列】的长度。
 *   子序列的定义：从原数组中删去（或不删）一些元素，**不改变剩余元素的相对顺序**
 *   得到的数组——**可以不连续**（跳着选，但先后次序不变）。
 *   "严格递增"：后一个元素必须【严格大于】前一个（相等不算）。
 *
 * <p>■ 输入输出
 *   输入：int[] nums（1 ≤ length ≤ 2500；0 ≤ nums[i] ≤ 10^4）
 * <p>　　　（进阶：你能把 O(n²) 优化到 O(n log n) 吗？——官方约束支持两种解法）
 *   输出：int（最长严格递增子序列的长度）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[10,9,2,5,3,7,101,18] → 4（官方：[2,3,7,101] 或 [2,5,7,101]、[2,3,7,18]——多解同长）
 * nums=[0,1,0,3,2,3]         → 4（[0,1,2,3]——跳着捡）
 * nums=[7,7,7,7,7]           → 1（严格递增——相等不叠加！）
 * nums=[1,2,3]               → 3（全程递增）
 * nums=[3,2,1]               → 1（全程递减，任取一元素）
 * nums=[5]                   → 1（单元素——"子序列"最短也是 1 个元素；**不是 0**）
 * nums=[1,3,2,3]             → 3（[1,2,3]? 2 在 3 后 → [1,3,3]? 严格——[1,2,3] 取第二个 3 → 3）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★——子序列组门面题）
 * <p>【标签】动态规划(DP) · 贪心 + 二分（进阶）· 子序列
 * <p>【题型】双解：①O(n²) dp[i]="以 nums[i] 结尾的 LIS 长度"（"结尾身份"型状态——
 * <p>　　　　与 198 前缀型不同的**第二种一维语义**）；②O(n log n) tails 数组（贪心+二分）
 * <p>【考点】"以 i 结尾"状态定义（前缀型 dp[i] 不行——子序列可跳，必须锚定结尾）；
 * <p>　　　　严格递增（相等不连）；dp[i] 的初值 1（自身成列）；max 而非继承
 * <p>【关联】P1143（LCS——二维版）；P674（连续版——被砍）；P354（俄罗斯套娃——进阶延伸）
 *
 * <p>LCS 签名：lengthOfLIS(int[] nums)。
 */
public class P300LongestIncreasingSubsequence {

    /** 你的实现（O(n²)：dp[i]=以 i 结尾的 LIS 长度——结尾锚定型，best 全表收账；头单元素特判可省）。 */
    public int lengthOfLIS(int[] nums) {
        if (nums.length == 1) {
            return 1;
        }
        int best = 0;
        int[] dp = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    public static void main(String[] args) {
        P300LongestIncreasingSubsequence solver = new P300LongestIncreasingSubsequence();

        run(solver, new int[]{10, 9, 2, 5, 3, 7, 101, 18}, "官方例1 → 4（[2,3,7,101]）");
        run(solver, new int[]{0, 1, 0, 3, 2, 3}, "官方例2 → 4（[0,1,2,3]）");
        run(solver, new int[]{7, 7, 7, 7, 7}, "全等 → 1（严格！）");
        run(solver, new int[]{1, 2, 3}, "全程递增 → 3");
        run(solver, new int[]{3, 2, 1}, "全程递减 → 1");
        run(solver, new int[]{5}, "单元素 → 1");
        run(solver, new int[]{1, 3, 2, 3}, "局部回撤 → 参考定（[1,2,3] 取后 3）");
        run(solver, new int[]{4, 10, 4, 3, 8, 9}, "重复干扰 → 参考定（[4,8,9]? 参考算）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：2^n 全子集枚举 + 严格递增检查（n ≤ 20 内可信；机制与 dp/二分全不同）。
     */
    private static int bruteForce(int[] nums) {
        int n = nums.length;
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            boolean ok = true;
            int prev = Integer.MIN_VALUE, cnt = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    if (nums[i] <= prev) { ok = false; break; }
                    prev = nums[i];
                    cnt++;
                }
            }
            if (ok) best = Math.max(best, cnt);
        }
        return best;
    }

    /** 参考二（O(n log n) 贪心+二分，对照用）：tails[k] = 长度 k+1 的 LIS 的最小末尾。 */
    private static int refBinarySearch(int[] nums) {
        int[] tails = new int[nums.length];
        int len = 0;
        for (int x : nums) {
            int i = Arrays.binarySearch(tails, 0, len, x);
            if (i < 0) i = -(i + 1);
            tails[i] = x;
            if (i == len) len++;
        }
        return len;
    }

    private static void run(P300LongestIncreasingSubsequence solver, int[] nums, String label) {
        try {
            int got = solver.lengthOfLIS(Arrays.copyOf(nums, nums.length));
            int want = bruteForce(Arrays.copyOf(nums, nums.length));
            int ref2 = refBinarySearch(Arrays.copyOf(nums, nums.length));
            boolean ok = got == want && got == ref2;
            String extra = (got != ref2) ? "（二分参考不一致！）" : "";

            System.out.printf("[%s] %-22s nums=%-34s | 参考=%d 二分=%d 实得=%d %s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, ref2, got, extra);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-22s nums=%-34s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-22s nums=%-34s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
