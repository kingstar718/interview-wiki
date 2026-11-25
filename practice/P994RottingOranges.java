/**
 * LeetCode 994: 腐烂的橘子 (Rotting Oranges)
 * https://leetcode.cn/problems/rotting-oranges/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·图论组 1/2——多源 BFS 名场面）
 *
 * <p>■ 题目描述
 *   m×n 网格，三种值：0=空格、1=新鲜橘子、2=腐烂橘子。
 *   每分钟：腐烂橘子【四面上下左右】相邻的新鲜橘子变腐烂。
 *   返回【直到所有橘子都腐烂】所需的最少分钟数；
 *   如果有橘子永远烂不到（被空格隔开），返回 -1。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] grid（1 ≤ m,n ≤ 10；grid[i][j] ∈ {0,1,2}）
 *   输出：int（分钟数或 -1）
 *
 * <p>■ 示例逐条
 * <pre>
 * [2,1,1],[1,1,0],[0,1,1] → 4（2 角扩散到对角）
 * [2,1,1],[0,1,1],[1,0,1] → -1（左下角 1 被空格围死）
 * [0,2] → 0（没有新鲜橘子，0 分钟即完成）
 * [1] → -1（单新鲜橘无人传染）
 * [2,2],[1,1],[0,1] → 2
 * [2,2],[2,1],[0,1] → 1
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/美团）
 * <p>【标签】广度优先 · 网格 · 队列
 * <p>【题型】多源 BFS（199 单源 BFS 的推广）：
 * <p>　　所有腐烂橘【同时】开始扩散——它们是"同一层的起点"
 * <p>　　全体 2 入队 → 层层向外，层数=分钟数（199 的"层"=这里的"分钟"）
 * <p>【考点】多源入队（起点不止一个）；扩散计数（新鲜剩余数倒推完成时刻）；
 * <p>　　　　-1 判定（扩散结束后仍有新鲜残留）；边界四方向数组
 * <p>【关联】P199（单源 BFS 分层锚 size 直系同源）；P200 岛屿（网格四方向 DFS/BFS）；
 * <p>　　　　P542 01 矩阵（多源 BFS 家族）
 *
 * <p>LCS 签名：orangesRotting(int[][] grid)。
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class P994RottingOranges {

    /** 你的实现（多源 BFS：全体 2 入队=第0层，199 的 size 锚=分钟；fresh 双职责）。 */
    public int orangesRotting(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        Deque<int[]> queue = new ArrayDeque<>();
        int fresh = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                }
                if (grid[i][j] == 1) {
                    fresh++;
                }
            }
        }
        if (fresh == 0) return 0;

        int minutes = 0;
        int[][] xys = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!queue.isEmpty() && fresh > 0) {
            minutes++;
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] poll = queue.poll();
                for (int[] xy : xys) {
                    int x = poll[0] + xy[0];
                    int y = poll[1] + xy[1];
                    if (x >= 0 && x < m && y >= 0 && y < n && grid[x][y] == 1) {
                        grid[x][y] = 2;
                        fresh--;
                        queue.offer(new int[]{x, y});
                    }
                }
            }
        }
        return fresh == 0 ? minutes : -1;
    }

    public static void main(String[] args) {
        P994RottingOranges solver = new P994RottingOranges();

        run(solver, g("[2,1,1|1,1,0|0,1,1]"), "官方例1 → 4");
        run(solver, g("[2,1,1|0,1,1|1,0,1]"), "官方例2 围死 → -1");
        run(solver, g("[0,2]"), "无新鲜 → 0");
        run(solver, g("[1]"), "单新鲜 → -1");
        run(solver, g("[2,2|1,1|0,1]"), "阶梯扩散 → 2");
        run(solver, g("[2,2|2,1|0,1]"), "多源并行 → 1");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** "[2,1,1|1,1,0]" → int[][]（| 分行）。 */
    private static int[][] g(String s) {
        String[] rows = s.replaceAll("[\\[\\]]", "").split("\\u007C");
        int[][] out = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cs = rows[i].split(",");
            out[i] = new int[cs.length];
            for (int j = 0; j < cs.length; j++) out[i][j] = Integer.parseInt(cs[j].trim());
        }
        return out;
    }

    /** 独立暴力参考：逐分钟模拟（每轮全网格扫描，找所有会新腐烂的；机制独立于队列）。 */
    private static int bruteForce(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int fresh = 0;
        for (int[] row : grid) for (int v : row) if (v == 1) fresh++;
        if (fresh == 0) return 0;
        int minutes = 0;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (fresh > 0 && minutes <= m * n) {
            boolean changed = false;
            int[][] next = new int[m][];
            for (int i = 0; i < m; i++) next[i] = grid[i].clone();
            for (int i = 0; i < m; i++)
                for (int j = 0; j < n; j++)
                    if (grid[i][j] == 2)
                        for (int[] d : dirs) {
                            int x = i + d[0], y = j + d[1];
                            if (x >= 0 && x < m && y >= 0 && y < n
                                    && grid[x][y] == 1 && next[x][y] == 1) {   // 防多邻居重复扣
                                next[x][y] = 2; fresh--; changed = true;
                            }
                        }
            if (!changed) return -1;
            grid = next;
            minutes++;
        }
        return minutes;
    }

    private static void run(P994RottingOranges solver, int[][] grid, String label) {
        try {
            int want = bruteForce(copy(grid));
            int got = solver.orangesRotting(copy(grid));
            boolean ok = got == want;

            System.out.printf("[%s] %-20s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-20s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-20s | 异常: %s%n", label, e);
        }
    }

    private static int[][] copy(int[][] g) {
        int[][] out = new int[g.length][];
        for (int i = 0; i < g.length; i++) out[i] = g[i].clone();
        return out;
    }
}
