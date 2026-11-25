import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 64: 最小路径和 (Minimum Path Sum)
 * https://leetcode.cn/problems/minimum-path-sum/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·62 的加权版）
 *
 * <p>■ 题目描述
 *   m×n 非负整数网格，从**左上角**走到**右下角**，
 *   每步只能**向下或向右**，使路径上数字总和最小，返回最小和。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] grid（m,n ∈ [1,200]；0 ≤ grid[i][j] ≤ 200）
 *   输出：int
 *
 * <p>■ 示例逐条
 * <pre>
 * [1,3,1 / 1,5,1 / 4,2,1] → 7（1→3→1→1→1 沿上边再下右边）
 * [1,2,3 / 4,5,6] → 12（1→2→3→6）
 * [1] → 1（单格）
 * [1,2,5] → 8（单行走到底）
 * [1,2,5] 转置 3x1 → 8
 * [1,2 / 1,1] → 3（1→1→1 走左边再下）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/华为）
 * <p>【标签】数组 · 动态规划 · 矩阵
 * <p>【题型】网格 DP（62 路径数的加权版——递推从"计数"变"取 min"）：
 * <p>　　dp[i][j] = 到 (i,j) 的最小和 = min(dp[i-1][j], dp[i][j-1]) + grid[i][j]
 * <p>【考点】转移方程；第一行/第一列的初始化（只有单侧来路）；
 * <p>　　　　滚动数组/原地改写（空间 O(n) 或 O(1) 的收敛）
 * <p>【关联】P62（计数版同构）；P63（带障碍版）；P120 三角形最小路径（倒序 DP）；
 * <p>　　　　P746 爬楼梯最小花费（同族初始化/收账）
 *
 * <p>LCS 签名：minPathSum(int[][] grid)。
 */
public class P64MinimumPathSum {

    /** 你的实现（二维 DP：首行首列单侧初始化，内部 min(上,左)+本格）。 */
    public int minPathSum(int[][] grid) {
        // 1 3 1
        // 1 5 1
        // 4 2 1 -> 7
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for (int i = 1; i < n; i++) {
            dp[0][i] = dp[0][i-1] + grid[0][i];
        }
        for (int i = 1; i < m; i++) {
            dp[i][0] = dp[i-1][0] + grid[i][0];
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + grid[i][j];
            }
        }
        return dp[m - 1][n - 1];
    }

    public static void main(String[] args) {
        P64MinimumPathSum solver = new P64MinimumPathSum();

        run(solver, g("1,3,1!1,5,1!4,2,1"), "官方例 → 7");
        run(solver, g("1,2,3!4,5,6"), "两行 → 12");
        run(solver, g("1"), "单格 → 1");
        run(solver, g("1,2,5"), "单行 → 8");
        run(solver, g("1!2!5"), "单列 → 8");
        run(solver, g("1,2!1,1"), "2x2 → 3");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** "1,3,1|1,5,1" → int[][]（| 分行，避免正则竖线陷阱）。 */
    private static int[][] g(String s) {
        String[] rows = s.split("!");
        int[][] out = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cs = rows[i].split(",");
            out[i] = new int[cs.length];
            for (int j = 0; j < cs.length; j++) out[i][j] = Integer.parseInt(cs[j].trim());
        }
        return out;
    }

    /** 独立暴力参考：递归枚举所有右/下路径取 min（指数级，机制独立）。 */
    private static int bruteForce(int[][] grid) {
        return walk(grid, 0, 0);
    }

    private static int walk(int[][] g, int i, int j) {
        int m = g.length, n = g[0].length;
        if (i == m - 1 && j == n - 1) return g[i][j];
        int best = Integer.MAX_VALUE;
        if (i + 1 < m) best = Math.min(best, walk(g, i + 1, j));
        if (j + 1 < n) best = Math.min(best, walk(g, i, j + 1));
        return g[i][j] + best;
    }

    private static void run(P64MinimumPathSum solver, int[][] grid, String label) {
        try {
            int want = bruteForce(grid);
            int got = solver.minPathSum(grid);
            boolean ok = got == want;

            System.out.printf("[%s] %-12s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-12s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-12s | 异常: %s%n", label, e);
        }
    }
}
