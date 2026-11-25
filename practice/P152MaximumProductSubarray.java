import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 152: 乘积最大子数组 (Maximum Product Subarray)
 * https://leetcode.cn/problems/maximum-product-subarray/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·53 的进阶）
 *
 * <p>■ 题目描述
 *   整数数组 nums，找出**乘积最大**的非空连续子数组，返回该乘积。
 *   （53 求最大和的连续子数组——本题乘法，负负得正带来新问题）
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ len ≤ 2*10^4；-10 ≤ nums[i] ≤ 10；
 *        任意前缀/后缀/子数组的乘积保证在 32 位整数范围内）
 *   输出：int
 *
 * <p>■ 示例逐条
 * <pre>
 * [2,3,-2,4] → 6（[2,3]）
 * [-2,0,-1] → 0（[-2,0] 或 [0]——负×负×负=负，带上 0 反而是最大）
 * [-2] → -2（单元素）
 * [2,3,-2,4,-1] → 48（[2,3,-2,4]？=-48 不对；[-2,4]？=-8；[2,3]=6；[3,-2,4,-1]=24？；
 *                    实际 [2,3]=6 最大？——不对，[2,3,-2,4] 乘积 -48；
 *                    最大是 6？还是 [-2,4]？——留给哨兵验证，答案=48 的组合是 [2,3,-2,4,-1]？=-48。
 *                    正确答案让参考算）
 * [-4,3,2] → 6（[3,2]）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/微软）
 * <p>【标签】数组 · 动态规划
 * <p>【题型】DP 状态扩展（53 的 max 只记一个——本题负号翻转历史需要两个状态）：
 * <p>　　以 i 结尾的最大乘积 fmax / 最小乘积 fmin——负数乘上来时两者互换
 * <p>【考点】为什么必须带 fmin（负×最小负=最大正）；fmax/fmin 的滚动更新次序；
 * <p>　　　　0 的处理（归一化重启）；全负数组（答案可能为负——哨兵覆盖）
 * <p>【关联】P53（最大和子数组——一个状态 vs 两个状态的对照）；P1567（乘积为正最长）
 *
 * <p>LCS 签名：maxProduct(int[] nums)。
 */
public class P152MaximumProductSubarray {

    /** 你的实现（双状态滚动：x1/x2 候选快照——先算三候选再同时更新 fmax/fmin）。 */
    public int maxProduct(int[] nums) {
        if (nums.length == 1) return nums[0];
        int min = nums[0];
        int max = nums[0];
        int ans = nums[0];
        for (int i = 1; i < nums.length; i++) {
            int x = nums[i];
            int x1 = min * x;
            int x2 = max * x;
            max = Math.max(x, Math.max(x1, x2));
            min = Math.min(x, Math.min(x1, x2));
            ans = Math.max(max, ans);
        }
        return ans;
    }

    public static void main(String[] args) {
        P152MaximumProductSubarray solver = new P152MaximumProductSubarray();

        run(solver, a(2, 3, -2, 4), "官方例1 → 6");
        run(solver, a(-2, 0, -1), "官方例2 带0 → 0");
        run(solver, a(-2), "单负 → -2");
        run(solver, a(2, 3, -2, 4, -1), "五元混合 → 参考算");
        run(solver, a(-4, 3, 2), "负×正×正 → 6");
        run(solver, a(0, 2), "0 开头 → 2");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static int[] a(int... v) { return v; }

    /** 独立暴力参考：枚举所有连续子数组求积取 max（O(n²)，机制独立）。 */
    private static int bruteForce(int[] nums) {
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            long p = 1;
            for (int j = i; j < nums.length; j++) {
                p *= nums[j];
                best = Math.max(best, (int) p);
            }
        }
        return best;
    }

    private static void run(P152MaximumProductSubarray solver, int[] nums, String label) {
        try {
            int want = bruteForce(nums);
            int got = solver.maxProduct(nums);
            boolean ok = got == want;

            System.out.printf("[%s] %-16s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-16s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-16s | 异常: %s%n", label, e);
        }
    }
}
