import java.util.Arrays;

/**
 * LeetCode 63: 不同路径 II (Unique Paths II)
 * https://leetcode.cn/problems/unique-paths-ii/
 *
 * <p>【难度】Medium
 * <p>【标签】动态规划(DP) · 数组
 * <p>【题型】62 + 障碍：障碍格 path 数判 0 跳过递推；初始化"第一行/列 障碍一出现后面全断"
 * <p>【考点】起点可能是障碍（[1] → 0）；推 0 代替"减法"——计数型 DP 的"清零"表达
 * <p>【关联】P62（无障碍原型）；P64（网格最小和——障碍用大数法）
 *
 * <p>obstacleGrid[i][j] == 1 表示障碍（不能踩）0 表示空地。从 (0,0) 到 (m-1,n-1)，
 * 只右/下，求路径数。到不了则 0。
 *
 * <p>示例：[[0,0,0],[0,1,0],[0,0,0]] → 2；[[0,1],[0,0]] → 1；[[1]] → 0；[[1,0],[0,0]] → 0
 * <p>约束：1 ≤ m, n ≤ 100
 */
public class P63UniquePathsII {

    /** 你的实现（二维版：起点/终点特判 + 首行首列"障碍即断"break 初始化 + 递推判零）。 */
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;
        if (obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return 0;
        }
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            if (obstacleGrid[i][0] != 1) {
                dp[i][0] = 1;
            } else {
                break;
            }
        }
        for (int i = 0; i < n; i++) {
            if (obstacleGrid[0][i] != 1) {
                dp[0][i] = 1;
            } else {
                break;
            }
        }
        dp[0][0] = 1;
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    dp[i][j] = 0;
                } else {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
                }
            }
        }
        return dp[m - 1][n - 1];
    }

    public static void main(String[] args) {
        P63UniquePathsII solver = new P63UniquePathsII();

        run(solver, new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}, "官方 → 2");
        run(solver, new int[][]{{0, 1}, {0, 0}}, "官方 → 1");
        run(solver, new int[][]{{1}}, "首格即障碍 → 0");
        run(solver, new int[][]{{0, 0}, {0, 0}}, "无障碍 2×2 → 2");
        run(solver, new int[][]{{1, 0}, {0, 0}}, "首格障碍整盘死 → 0");
        run(solver, new int[][]{{0, 0}, {0, 1}}, "终点即障碍 → 0");
        run(solver, new int[][]{{0, 1, 0}, {0, 0, 0}, {0, 0, 0}}, "第一行中段障碍 → 3（上堵死绕右走）");
        run(solver, new int[][]{{0, 0}, {1, 0}, {0, 0}, {0, 1}, {0, 0}}, "交错障碍 → 0（第二条全堵死）");
        run(solver, new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 1, 0}}, "上下连续障碍 → 1");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立参考实现：BFS 面积展开数每条路径计数不可行（指数级），这里用
     * 【深度优先 + 记忆化】四向一致的另一机制（自顶向下）与迭代滚动互验。
     */
    private static java.util.Map<String, Long> MEMO_63 = new java.util.HashMap<>();

    private static long dfs(int[][] g, int i, int j) {
        if (i < 0 || j < 0 || g[i][j] == 1) return 0;     // 障碍/越界即 0
        if (i == 0 && j == 0) return 1;
        String key = i + "," + j;
        if (MEMO_63.containsKey(key)) return MEMO_63.get(key);
        long v = dfs(g, i - 1, j) + dfs(g, i, j - 1);
        MEMO_63.put(key, v);
        return v;
    }

    private static int bruteForce(int[][] obstacleGrid) {
        MEMO_63.clear();
        if (obstacleGrid[0][0] == 1) return 0;
        long r = dfs(obstacleGrid, obstacleGrid.length - 1, obstacleGrid[0].length - 1);
        return (int) r;
    }

    private static void run(P63UniquePathsII solver, int[][] obstacleGrid, String label) {
        try {
            int[][] in = clone2D(obstacleGrid);
            int got = solver.uniquePathsWithObstacles(in);
            int want = bruteForce(clone2D(obstacleGrid));
            boolean ok = got == want;

            System.out.printf("[%s] %-38s grid=%-40s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, pretty(obstacleGrid), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-38s grid=%-40s | TODO 未实现%n", label, pretty(obstacleGrid));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-38s grid=%-40s | 异常: %s%n", label, pretty(obstacleGrid), e);
        }
    }

    private static int[][] clone2D(int[][] a) {
        int[][] r = new int[a.length][];
        for (int i = 0; i < a.length; i++) r[i] = Arrays.copyOf(a[i], a[i].length);
        return r;
    }

    private static String pretty(int[][] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            sb.append(Arrays.toString(a[i]));
            if (i < a.length - 1) sb.append(',');
        }
        return sb.append(']').toString();
    }
}
