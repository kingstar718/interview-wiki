/**
 * LeetCode 695: 岛屿的最大面积 (Max Area of Island)
 * https://leetcode.cn/problems/max-area-of-island/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（图论章 2/6——油漆桶第二次使用：顺便数格数）
 *
 * <p>■ 题目描述
 *   给一个 m×n 的二维网格 grid（**int 数组**，1 = 陆地，0 = 水）。
 *   岛屿由**四方向连通**（上下左右，不含斜角）的 1 组成，面积 = 岛上 1 的格数。
 *   求**最大的岛屿面积**（没有岛则 0）。
 *
 * <p>■ 输入输出
 *   输入：int[][] grid（1 ≤ m, n ≤ 50；grid[i][j] ∈ {0, 1}）
 *   输出：int（最大面积）
 *
 * <p>■ 示例逐条
 * <pre>
 * 官方例1（13×8 形状图）→ 6（右中那块）
 * grid=[[0,0,0,0,0,0,0,0]] → 0（全水）
 * grid=[[1]] → 1（单格岛）
 * grid=[[1,1],[1,0],[0,1]] → 3（L 形：斜角不连通——(1,1) 不算进左上岛）★ 哨兵
 * grid=[[1,0,1],[0,1,0],[1,0,1]] → 1（对角阵：五个孤格互不四联通——斜角不算哨兵）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】DFS · BFS · 数组 · 矩阵
 * <p>【题型】200 沉没 DFS 的直接换皮：dfs 带返回值（沉一格记一格，return 1+四方向之和），主循环取 max
 * <p>【考点】递归返回值的累加聚合（vs 200 的 void）；max 收账位置（主循环里，不是全局变量）
 * <p>【关联】P200（原型）；P130（反向灌水）；P547（并查集镜像）
 *
 * <p>LCS 签名：maxAreaOfIsland(int[][] grid)。
 */
public class P695MaxAreaOfIsland {

    /** 你的实现（带返回值的沉没 DFS：return 1+四方向之和，主循环 max 收账）。 */
    public int maxAreaOfIsland(int[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    count = Math.max(count, dfs(grid, i, j));
                }
            }
        }
        return count;
    }

    private int dfs(int[][] grid, int i, int j) {
        if (i < 0 || j < 0 || i >= grid.length || j >= grid[0].length || grid[i][j] != 1) {
            return 0;
        }
        grid[i][j] = 0;
        return 1 + dfs(grid, i - 1, j) + dfs(grid, i + 1, j) + dfs(grid, i, j - 1) + dfs(grid, i, j + 1);
    }

    public static void main(String[] args) {
        P695MaxAreaOfIsland solver = new P695MaxAreaOfIsland();

        run(solver, new int[][]{{0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
                                {0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                                {0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0},
                                {0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0}}, "官方例1 → 6");
        run(solver, new int[][]{{0, 0, 0, 0, 0, 0, 0, 0}}, "全水 → 0");
        run(solver, new int[][]{{1}}, "单格 → 1");
        run(solver, new int[][]{{1, 1}, {1, 0}, {0, 1}}, "L 形斜角断 → 3 ★ 哨兵");
        run(solver, new int[][]{{1, 0, 1}, {0, 1, 0}, {1, 0, 1}}, "对角阵 → 1（斜角不算）");
        run(solver, new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}, "全陆 → 9");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：并查集 + 按根聚合格数（连通块大小 = 同根计数）——机制与 DFS 累加不同。 */
    private static int bruteForce(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[] parent = new int[m * n];
        for (int i = 0; i < m * n; i++) parent[i] = i;
        for (int x = 0; x < m * n; x++) {
            int r = x / n, c = x % n;
            if (grid[r][c] != 1) continue;
            if (c + 1 < n && grid[r][c + 1] == 1) union(parent, x, x + 1);
            if (r + 1 < m && grid[r + 1][c] == 1) union(parent, x, x + n);
        }
        java.util.Map<Integer, Integer> size = new java.util.HashMap<>();
        int max = 0;
        for (int x = 0; x < m * n; x++) {
            int r = x / n, c = x % n;
            if (grid[r][c] == 1) {
                int root = find(parent, x);
                size.merge(root, 1, Integer::sum);
                max = Math.max(max, size.get(root));
            }
        }
        return max;
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) { parent[x] = parent[parent[x]]; x = parent[x]; }
        return x;
    }

    private static void union(int[] parent, int a, int b) {
        parent[find(parent, a)] = find(parent, b);
    }

    private static void run(P695MaxAreaOfIsland solver, int[][] grid, String label) {
        try {
            int got = solver.maxAreaOfIsland(copy(grid));
            int want = bruteForce(grid);
            boolean ok = got == want;

            System.out.printf("[%s] %-28s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s | 异常: %s%n", label, e);
        }
    }

    private static int[][] copy(int[][] g) {
        int[][] c = new int[g.length][];
        for (int i = 0; i < g.length; i++) c[i] = g[i].clone();
        return c;
    }
}
