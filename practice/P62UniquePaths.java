import java.util.Arrays;

/**
 * LeetCode 62: 不同路径 (Unique Paths)
 * https://leetcode.cn/problems/unique-paths/
 *
 * <p>【难度】Medium
 * <p>【标签】动态规划(DP) · 数学（组合数）· 二维
 * <p>【题型】二维 DP 开门炮：dp[i][j] = 上 + 左（网格版"爬楼梯"——两个方向代替两步长）
 * <p>【考点】第一行/第一列"天然支持"（初始化全 1）；任意 dp[i][j] 只依赖上方与左方（遍历顺序天然安全）
 * <p>【关联】P70（单维爬楼梯→二维化）；P63（加障碍）、P64（网格最小和）同底盘
 *
 * <p>给 m×n 网格，机器人从 (0,0) 走到 (m-1,n-1)，每步只能向右或向下，
 * 求有多少条不同的路径。
 *
 * <p>示例：m=3,n=7 → 28；m=3,n=2 → 3；m=7,n=3 → 28
 * <p>约束：1 ≤ m, n ≤ 100（答案 ≤ 2×10⁹ 内保证）
 */
public class P62UniquePaths {

    /** 你的实现（二维 dp 数组版：第一行/列全 1，dp[i][j]=上+左）。 */
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 0; i < n; i++) {
            dp[0][i] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i][j - 1] + dp[i - 1][j];
            }
        }
        return dp[m - 1][n - 1];
    }

    /** 对照实现 v2（一维滚动版：dp[j] += dp[j-1]——"旧值=上方，已覆写=左方"）。 */
    public int uniquePathsRoll(int m, int n) {
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] += dp[j - 1];
            }
        }
        return dp[n - 1];
    }

    public static void main(String[] args) {
        P62UniquePaths solver = new P62UniquePaths();

        run(solver, 3, 7, "官方 → 28");
        run(solver, 3, 2, "官方 → 3（3×2 小网格）");
        run(solver, 7, 3, "官方转置 → 28（m/n 对称）");
        run(solver, 1, 1, "1×1 → 1（已在终点）");
        run(solver, 1, 10, "单行 → 1（只能一路右）");
        run(solver, 10, 1, "单列 → 1");
        run(solver, 2, 2, "2×2 → 2");
        run(solver, 3, 3, "3×3 → 6");
        run(solver, 23, 12, "LeetCode 上限规模 → 组合数 C(33,22)");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立参考实现（机制不同）：组合数学公式 C(m+n-2, m-1)。
     * 用 long 乘除防中途溢出（每次乘法后紧跟除法保证整除）。
     */
    private static int combFormula(int m, int n) {
        long ans = 1;
        int k = Math.min(m - 1, n - 1);
        for (int i = 1; i <= k; i++) {
            ans = ans * (m + n - 2 - i + 1) / i;
        }
        return (int) ans;
    }

    private static void run(P62UniquePaths solver, int m, int n, String label) {
        try {
            int got = solver.uniquePaths(m, n);
            int want = combFormula(m, n);
            int roll = solver.uniquePathsRoll(m, n);
            if (roll != want) {
                System.out.println("    [MISMATCH] 一维滚动版与结果不一致！");
            }
            boolean ok = got == want && roll == want;

            int[][] ref2D = null;   // 占位
            System.out.printf("[%s] %-40s m=%-3d n=%-3d | 参考=C公式=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, m, n, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-40s m=%-3d n=%-3d | TODO 未实现%n", label, m, n);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-40s m=%-3d n=%-3d | 异常: %s%n", label, m, n, e);
        }
    }
}
