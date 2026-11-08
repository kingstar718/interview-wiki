import java.util.Arrays;

/**
 * LeetCode 746: 使用最小花费爬楼梯 (Min Cost Climbing Stairs)
 * https://leetcode.cn/problems/min-cost-climbing-stairs/
 *
 * <p>【难度】Easy
 * <p>【标签】动态规划(DP) · 数组
 * <p>【题型】DP 五部曲第 3 题：dp[i]=爬到 i 阶（**已支付** cost[0..?]）的最小花费；转移取 min 侧
 * <p>【考点】"楼顶在 n 位"（dp[n] 是答案，而 dp[n] 是虚拟层）；起始免费（可从 0 或 1 直接登场）
 * <p>【关联】P70（方法数计数——同模型换"费用"维度，加法→min）
 *
 * <p>cost[i] 为从第 i 阶向上爬的花费。可以从第 0 或 1 阶开始。每次 1 或 2 阶。
 * 到达楼顶（数组末尾之后）的最小总花费。
 *
 * <p>示例：[10,15,20] → 15；[1,100,1,1,1,100,1,1,100,1] → 6
 * <p>约束：2 &lt;= cost.length &lt;= 1000；0 &lt;= cost[i] &lt;= 999
 */
public class P746MinCostClimbingStairs {

    /** 你的实现（到达付流派：dp[i]=踩上 i 已付的钱，返回 min(dp[n-1], dp[n-2)] —— 注意！你的版本返回 dp[last]）。 */
    public int minCostClimbingStairs(int[] cost) {
        if (cost.length == 0) return 0;
        if (cost.length == 1) return cost[0];
        int[] dp = new int[cost.length];
        dp[0] = cost[0];
        dp[1] = cost[1];
        for (int i = 2; i < cost.length; i++) {
            dp[i] = Math.min(dp[i - 1] + cost[i], dp[i - 2] + cost[i]);
        }
        return Math.min(dp[cost.length - 1], dp[cost.length - 2]);
    }

    public static void main(String[] args) {
        P746MinCostClimbingStairs solver = new P746MinCostClimbingStairs();

        run(solver, new int[]{10, 15, 20}, "官方示例1 → 15（从 1 起两步到顶）");
        run(solver, new int[]{1, 100, 1, 1, 1, 100, 1, 1, 100, 1}, "官方示例2 → 6");
        run(solver, new int[]{1, 100}, "两级 → 1（从 0 起付 1 两步到顶）");
        run(solver, new int[]{100, 1}, "两级 → 1（从 1 起付 1）");
        run(solver, new int[]{100, 1, 100, 1}, "跳跃组 → 2（从1跳，再从1跳 or 1+100? 采取 0 起跳 → min 组）");
        run(solver, new int[]{10, 15, 20, 5, 4, 10}, "偶长数组（终结 min 双位）");
        run(solver, new int[]{2, 2, 2, 2}, "同级同价 → 4");
        run(solver, new int[]{0, 0, 0, 100}, "被 0 垫平 → 0");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：DFS 枚出"每一格跳 1 或 2 步/支付当前 cost、从 0 或 1 分别起跳"的所有路径取最小（n &lt;= 22 内可行）。
     */
    private static int bruteForce(int[] cost, int pos, int paid) {
        if (pos >= cost.length) return paid;
        // 抵达 pos（若 >= cost.length 返回），先支付 cost[pos]
        int startPaid = paid + cost[pos];
        int best = Integer.MAX_VALUE;
        best = Math.min(best, bruteForce(cost, pos + 1, startPaid));
        best = Math.min(best, bruteForce(cost, pos + 2, startPaid));
        return best;
    }

    private static int brute(int[] cost) {
        int fromZero = bruteForce(cost, 0, 0);
        int fromOne = bruteForce(cost, 1, 0);
        return Math.min(fromZero, fromOne);
    }

    private static void run(P746MinCostClimbingStairs solver, int[] cost, String label) {
        try {
            int got = solver.minCostClimbingStairs(Arrays.copyOf(cost, cost.length));
            int want = Arrays.toString(cost).length() <= 40
                    ? brute(Arrays.copyOf(cost, cost.length))
                    : (label.contains("官方示例1") ? 15 : (label.contains("官方示例2") ? 6 : 4));
            boolean ok = got == want;

            System.out.printf("[%s] %-48s cost=%-24s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(cost), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-48s cost=%-24s | TODO 未实现%n", label, Arrays.toString(cost));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-48s cost=%-24s | 异常: %s%n", label, Arrays.toString(cost), e);
        }
    }
}
