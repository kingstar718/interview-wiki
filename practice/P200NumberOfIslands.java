import java.util.Arrays;

/**
 * LeetCode 200: 岛屿数量 (Number of Islands)
 * https://leetcode.cn/problems/number-of-islands/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（图论章 1/6——DFS 灌水原型）
 *
 * <p>■ 题目描述
 *   给一个 m×n 的二维网格 grid，'1' 表示陆地，'0' 表示水。
 *   岛屿由**四方向连通**（上下左右，不含斜角）的陆地组成——整个网格被水包围。
 *   求**岛屿的数量**（可以假设网格四周都是水，即边界外全是 '0'）。
 *
 * <p>■ 输入输出
 *   输入：char[][] grid（1 ≤ m, n ≤ 300；每格是 '0' 或 '1'）
 *   输出：int（岛屿数）
 *
 * <p>■ 示例逐条
 * <pre>
 * grid=["11110","11010","11000","00000"] → 1（左上大岛一体）
 * grid=["11000","11000","00100","00011"] → 3（左上岛 + 中间独柱 + 右下岛）
 * grid=["1"]            → 1
 * grid=["0"]            → 0
 * grid=["101","010","101"] → 5（斜角不连通！五个独立单格岛）★ 四联通哨兵
 * grid=["111","010","111"] → 1（环形环岛——中间水，外圈陆地连通）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★——全厂手撕高频）
 * <p>【标签】DFS · BFS · 并查集 · 矩阵
 * <p>【题型】"连通分量计数"——遍历格子遇新陆地 count++，DFS 沉没整个岛（'1'→'0' 防回头）
 * <p>【考点】沉没防回头（visited 的就地版）；四方向数组 dr/dc；越界+水的双守卫；
 * <p>　　　　斜角不算连通（四联通）；**原地修改** vs visited 数组的取舍（改输入副作用——面试提一句）
 * <p>【关联】P695（面积版——本课加返回值）；P130（反向灌水）；P547（并查集镜像）；回溯章的 DFS 肌肉直接迁移
 *
 * <p>LCS 签名：numIslands(char[][] grid)。
 */
public class P200NumberOfIslands {

    /** 你的实现（沉没 DFS：主循环遇 '1' 计数+沉岛；dfs 双守卫+四方向扩散——"洪水从发现点漫开"）。 */
    public int numIslands(char[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || j < 0 || i >= grid.length || j >= grid[0].length || grid[i][j] == '0') {
            return;
        }
        grid[i][j] = '0';
        dfs(grid, i, j - 1);
        dfs(grid, i - 1, j);
        dfs(grid, i + 1, j);
        dfs(grid, i, j + 1);
    }

    public static void main(String[] args) {
        P200NumberOfIslands solver = new P200NumberOfIslands();

        run(solver, new String[]{"11110", "11010", "11000", "00000"}, "官方例1 → 1");
        run(solver, new String[]{"11000", "11000", "00100", "00011"}, "官方例2 → 3");
        run(solver, new String[]{"1"}, "单格 → 1");
        run(solver, new String[]{"0"}, "单水格 → 0");
        run(solver, new String[]{"101", "010", "101"}, "斜角不连通 → 5 ★ 四联通哨兵");
        run(solver, new String[]{"111", "010", "111"}, "环岛 → 1");
        run(solver, new String[]{"0000"}, "全水 → 0");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：并查集（按行列编号 union 相邻陆地，数不同根）——机制与 DFS 沉没完全不同。 */
    private static int bruteForce(String[] rows) {
        int m = rows.length, n = rows[0].length();
        int[] parent = new int[m * n];
        for (int i = 0; i < m * n; i++) parent[i] = i;
        for (int x = 0; x < m * n; x++) {
            int r = x / n, c = x % n;
            if (rows[r].charAt(c) != '1') continue;
            // 右、下两个方向 union（避免重复）
            if (c + 1 < n && rows[r].charAt(c + 1) == '1') union(parent, x, x + 1);
            if (r + 1 < m && rows[r + 1].charAt(c) == '1') union(parent, x, x + n);
        }
        java.util.Set<Integer> roots = new java.util.HashSet<>();
        for (int x = 0; x < m * n; x++) {
            int r = x / n, c = x % n;
            if (rows[r].charAt(c) == '1') roots.add(find(parent, x));
        }
        return roots.size();
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) { parent[x] = parent[parent[x]]; x = parent[x]; }
        return x;
    }

    private static void union(int[] parent, int a, int b) {
        parent[find(parent, a)] = find(parent, b);
    }

    private static void run(P200NumberOfIslands solver, String[] rows, String label) {
        try {
            char[][] grid = new char[rows.length][];
            for (int i = 0; i < rows.length; i++) grid[i] = rows[i].toCharArray();
            int got = solver.numIslands(grid);

            char[][] fresh = new char[rows.length][];
            for (int i = 0; i < rows.length; i++) fresh[i] = rows[i].toCharArray();
            int want = bruteForce(rows);
            boolean ok = got == want;

            System.out.printf("[%s] %-34s grid=%-42s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(rows), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s grid=%-42s | TODO 未实现%n", label, Arrays.toString(rows));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s grid=%-42s | 异常: %s%n", label, Arrays.toString(rows), e);
        }
    }
}
