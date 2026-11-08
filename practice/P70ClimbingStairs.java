/**
 * LeetCode 70: 爬楼梯 (Climbing Stairs)
 * https://leetcode.cn/problems/climbing-stairs/
 *
 * <p>【难度】Easy
 * <p>【标签】动态规划(DP) · 数学（斐波那契换皮）
 * <p>【题型】DP 五部曲第 2 题：dp[i]=到 i 阶的方法数；转移是"最后一次动作"的分类穷举
 * <p>【考点】dp[1]=1/dp[2]=2（与 509 的 0,1 不同——n=2 是合法输入）;驾驭 n=45 上限（int 恰好 1836311903）
 * <p>【关联】P509（同式对照，509 骨架几乎可以直接抄）；P746（最小花费爬楼梯，初始化变体）
 *
 * <p>每次爬 1 或 2 阶，到 n 阶顶有多少种不同方法。
 *
 * <p>示例：n=2 → 2（1+1 / 2）；n=3 → 3（111/12/21）；n=45 → 1836311903
 * <p>约束：1 &lt;= n &lt;= 45
 */
public class P70ClimbingStairs {

    /** 你的实现（dp[0]=1 流派：0-based 数组，i 循环到 n-1，返回 dp[n-1]）。 */
    public int climbStairs(int n) {
        if (n == 1 || n == 2) return n;
        int[] dp = new int[n];
        dp[0] = 1;
        dp[1] = 2;
        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n - 1];
    }

    /** 对照实现 v2（dp[1]=1 流派：下标=阶数，1-based，返回 dp[n]）。 */
    public int climbStairsV2(int n) {
        if (n == 1 || n == 2) return n;
        int[] dp = new int[n + 1];
        dp[0] = 1;                       // 站在 0 阶（未动）= 1 种"空走法"
        dp[1] = 1;                       // 一阶一跳
        dp[2] = 2;
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    public static void main(String[] args) {
        P70ClimbingStairs solver = new P70ClimbingStairs();

        run(solver, 1, "1 → 1（只有 1+1+…? 不对：单阶只有一种走法）");
        run(solver, 2, "官方示例 → 2（11 / 2）");
        run(solver, 3, "官方示例 → 3（111/12/21）");
        run(solver, 4, "5（1111/112/121/211/22）");
        run(solver, 5, "8");
        run(solver, 10, "89");
        run(solver, 20, "10946");
        run(solver, 30, "1346269");
        run(solver, 40, "165580141");
        run(solver, 45, "LeetCode 上限 1836311903（int 最大不变，验证状态）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：记忆化递归（自顶向下，与迭代滚动机制不同）。
     * 递归式按题意写：ways(i) = 消耗 1 级或 2 级的两种"第一次动作"之和。
     */
    private static final long[] MEMO = new long[46];   // 0..45

    private static long refWays(int n) {
        if (n <= 2) return n;
        if (MEMO[n] != 0) return MEMO[n];
        long v = refWays(n - 1) + refWays(n - 2);
        MEMO[n] = v;
        return v;
    }

    private static void run(P70ClimbingStairs solver, int n, String label) {
        try {
            int got = solver.climbStairs(n);
            int want = (int) refWays(n);
            int alt = solver.climbStairsV2(n);
            if (alt != want) {
                System.out.println("    [MISMATCH] 双流派（dp[0]=1 / dp[0]=1+dp[1]=1）结果不一致！");
            }
            boolean ok = got == want && alt == want;

            System.out.printf("[%s] %-52s n=%-3d | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, n, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-52s n=%-3d | TODO 未实现%n", label, n);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-52s n=%-3d | 异常: %s%n", label, n, e);
        }
    }
}
