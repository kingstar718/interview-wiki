/**
 * LeetCode 130: 被围绕的区域 (Surrounded Regions)
 * https://leetcode.cn/problems/surrounded-regions/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（图论章 3/6——灌水三题收官·反向思维）
 *
 * <p>■ 题目描述
 *   给一个 m×n 的网格 board，只含 'X' 和 'O'。
 *   【被 X 围绕的区域】= 完全被 'X' 包住（四联通的 O 区域，任何成员都不与边界相连）。
 *   把所有被围绕区域里的 'O' 全部改成 'X'；贴着边界（或与边界 O 连通的 O）保持不变。
 *   **原地修改** board（不返回值）。
 *
 * <p>■ 输入输出
 *   输入：char[][] board（1 ≤ m, n ≤ 200；board[i][j] ∈ {'X','O'}）
 *   输出：无（原地修改）
 *
 * <p>■ 示例逐条
 * <pre>
 * board=[XXXX / XOOX / XXOX / XOOX?] 官方例：
 *   输入 ["XXXX","XOOX","XXOX","XOXX"] → 输出 ["XXXX","XXXX","XXXX","XOXX"]
 *   （(1,1)(1,2)(2,2) 被围→变 X；(3,1) 贴边界→保留）
 * board=[["X"]] → [["X"]]
 * board=[["O","O"],["O","O"]] → 不变（全贴边界）
 * board=[["O"]] → [["O"]]（单格贴边界）
 * board=[["X","O","X"],["X","O","X"],["X","O","X"]] → 不变（整列贴边界）
 * board=[["O","X","X"],["X","O","X"],["X","X","O"]] → 不变（对角 O 互相不连通、各自贴边）
 * board=[["XXX"],["XOX"],["XXX"]] → [["XXX"],["XXX"],["XXX"]]（唯一 O 被 4 面围死）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节高频）
 * <p>【标签】DFS · BFS · 并查集 · 矩阵
 * <p>【题型】灌水三题收官：反向思维——从【边界】倒漆保护（'O'→'#'），剩下的 O 才是被围死的（→'X'），'#' 恢复 'O'
 * <p>【考点】正难则反（判"被围"难→判"不被围"易）；三段式：边界灌水 → 全盘改 X → 恢复；
 * <p>　　　　原地修改无返回值（vs 200/695 的计数收账——本次是"整形"不是"统计"）
 * <p>【关联】P200（正向灌水原型）；P695（带计数）；P547（并查集章）
 *
 * <p>LCS 签名：solve(char[][] board)。
 */
public class P130SurroundedRegions {

    /** 你的实现（边界倒灌三段式：'O'→'#' 保护 → 处决/恢复）。 */
    public void solve(char[][] board) {
        int m = board.length, n = board[0].length;
        for (int i = 0; i < m; i++) {
            dfs(board, i, 0);
            dfs(board, i,  n - 1);
        }
        for (int i = 0; i < n; i++) {
            dfs(board, 0, i);
            dfs(board, m - 1, i);
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X';
                } else if (board[i][j] == '#') {
                    board[i][j] = 'O';
                }
            }
        }
    }

    private void dfs(char[][] board, int i, int j) {
        if (i < 0 || j < 0 || i >= board.length || j >= board[0].length) {
            return;
        }
        if (board[i][j] != 'O') {
            return;
        }
        board[i][j] = '#';
        dfs(board, i - 1, j);
        dfs(board, i + 1, j);
        dfs(board, i, j - 1);
        dfs(board, i, j + 1);
    }

    public static void main(String[] args) {
        P130SurroundedRegions solver = new P130SurroundedRegions();

        run(solver, new String[]{"XXXX", "XOOX", "XXOX", "XOXX"}, "官方例 → 内部O变X 边界O保留");
        run(solver, new String[]{"X"}, "单 X → 不变");
        run(solver, new String[]{"O"}, "单 O → 不变（贴边）");
        run(solver, new String[]{"OO", "OO"}, "全 O → 不变（全贴边）");
        run(solver, new String[]{"XOX", "XOX", "XOX"}, "整列贴边 → 不变");
        run(solver, new String[]{"XXX", "XOX", "XXX"}, "中心围死 → 全 X");
        run(solver, new String[]{"OX", "XO"}, "对角贴边 → 不变");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：每个 O 区域 BFS 找色，区域含边界格→保留否则→X——机制与"先保护后清理"不同。 */
    private static char[][] bruteForce(String[] rows) {
        int m = rows.length, n = rows[0].length();
        char[][] g = new char[m][];
        for (int i = 0; i < m; i++) g[i] = rows[i].toCharArray();
        boolean[][] visited = new boolean[m][n];
        int[] qr = new int[m * n], qc = new int[m * n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (g[i][j] != 'O' || visited[i][j]) continue;
                // BFS 收集这个 O 区域
                java.util.List<int[]> region = new java.util.ArrayList<>();
                int head = 0, tail = 0;
                qr[tail] = i; qc[tail] = j; tail++; visited[i][j] = true;
                boolean touchesEdge = false;
                while (head < tail) {
                    int r = qr[head], c = qc[head]; head++;
                    region.add(new int[]{r, c});
                    if (r == 0 || r == m - 1 || c == 0 || c == n - 1) touchesEdge = true;
                    if (r + 1 < m && g[r + 1][c] == 'O' && !visited[r + 1][c]) { qr[tail] = r + 1; qc[tail] = c; tail++; visited[r + 1][c] = true; }
                    if (r - 1 >= 0 && g[r - 1][c] == 'O' && !visited[r - 1][c]) { qr[tail] = r - 1; qc[tail] = c; tail++; visited[r - 1][c] = true; }
                    if (c + 1 < n && g[r][c + 1] == 'O' && !visited[r][c + 1]) { qr[tail] = r; qc[tail] = c + 1; tail++; visited[r][c + 1] = true; }
                    if (c - 1 >= 0 && g[r][c - 1] == 'O' && !visited[r][c - 1]) { qr[tail] = r; qc[tail] = c - 1; tail++; visited[r][c - 1] = true; }
                }
                if (!touchesEdge) for (int[] p : region) g[p[0]][p[1]] = 'X';
            }
        }
        return g;
    }

    private static void run(P130SurroundedRegions solver, String[] rows, String label) {
        try {
            char[][] board = new char[rows.length][];
            for (int i = 0; i < rows.length; i++) board[i] = rows[i].toCharArray();
            solver.solve(board);

            char[][] want = bruteForce(rows);
            boolean ok = java.util.Arrays.deepEquals(board, want);

            System.out.printf("[%s] %-26s 入=%-40s | 参考=%s%n",
                    ok ? "PASS" : "FAIL", label, java.util.Arrays.toString(rows),
                    fmt(want));
            if (!ok) System.out.printf("       实得=%s%n", fmt(board));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s 入=%-40s | TODO 未实现%n", label, java.util.Arrays.toString(rows));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s 入=%-40s | 异常: %s%n", label, java.util.Arrays.toString(rows), e);
        }
    }

    private static String fmt(char[][] g) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < g.length; i++) { sb.append(g[i]); if (i < g.length - 1) sb.append(", "); }
        return sb.append("]").toString();
    }
}
