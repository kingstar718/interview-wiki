import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 79: 单词搜索 (Word Search)
 * https://leetcode.cn/problems/word-search/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·网格回溯）
 *
 * <p>■ 题目描述
 *   m×n 字符网格 board 和单词 word，判断 word 是否**存在于**网格中。
 *   存在=从某格出发，沿**上下左右**相邻格逐步匹配 word 的字符；
 *   **同一格在一条路径里不许重复使用**（走过的要标记，回溯时撤销）。
 *
 * <p>■ 输入输出（约束）
 *   输入：char[][] board（m,n ∈ [1,6]）；String word（1 ≤ len ≤ 15，含大小写）
 *   输出：boolean
 *
 * <p>■ 示例逐条
 * <pre>
 * ABCE / SFCS / ADEE  word="ABCCED" → true（A(0,0)→B→C→C→E→D）
 * 同上 word="SEE" → true（S(1,1)→E(2,1)→E(2,2)）
 * 同上 word="ABCB" → false（B 用过不能重用）
 * 单格 A，word="A" → true
 * 单格 A，word="AB" → false
 * 网格 [a] word="a" → true
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/亚马逊）
 * <p>【标签】数组 · 回溯 · 矩阵
 * <p>【题型】网格回溯（22 撤销纪律 + 994 网格四方向的双积木合体）：
 * <p>　　从每个格子尝试作为起点；每步匹配 word[k]，四方向递归 k+1
 * <p>　　走过标记（改字符或 boolean 矩阵）→递归→撤销
 * <p>【考点】起点枚举（外层双 for）；标记/撤销的对称（22 纪律原样适用）；
 * <p>　　　　出口=k 走到 len（true 条件）；字符改写法 vs 访问矩阵法
 * <p>【关联】P994（四方向+边界）；P22（撤销纪律）；P46（回溯模板）；P212（本题 Trie 加持版）
 *
 * <p>LCS 签名：exist(char[][] board, String word)。
 */
public class P79WordSearch {

    /** 你的实现（起点枚举+访问矩阵；两检查先于标记；撤销双出口配对）。 */
    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        boolean[][] used = new boolean[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (dfs(board, word, used, i, j, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, boolean[][] used, int i, int j, int k) {
        if (board[i][j] != word.charAt(k)) return false;
        if (k == word.length() - 1) return true;

        used[i][j] = true;
        for (int[] xy : new int[][]{{1, 0}, {-1, 0}, {0, -1}, {0, 1}}) {
            int x = i + xy[0];
            int y = j + xy[1];
            if (x >=0 && x < board.length && y >= 0 && y < board[0].length && !used[x][y]
                    && dfs(board, word, used, x, y, k + 1)) {
                used[i][j] = false;
                return true;
            }
        }
        used[i][j] = false;
        return false;
    }

    public static void main(String[] args) {
        P79WordSearch solver = new P79WordSearch();

        run(solver, g("ABCE|SFCS|ADEE"), "ABCCED", "官方例1 → true");
        run(solver, g("ABCE|SFCS|ADEE"), "SEE", "官方例2 → true");
        run(solver, g("ABCE|SFCS|ADEE"), "ABCB", "官方例3 重用禁 → false");
        run(solver, g("A"), "A", "单格命中 → true");
        run(solver, g("A"), "AB", "单格不中 → false");
        run(solver, g("ab|cd"), "acdb", "蛇形路径 → true");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** "ABCE|SFCS" → char[][]（| 分行）。 */
    private static char[][] g(String s) {
        String[] rows = s.split("\\|");
        char[][] out = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) out[i] = rows[i].toCharArray();
        return out;
    }

    /** 独立暴力参考：每个起点 DFS 全路径枚举（与用户实现机制相同但独立编写，互为对账）。 */
    private static boolean bruteForce(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        boolean[][] used = new boolean[m][n];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (ref(board, word, used, i, j, 0)) return true;
        return false;
    }

    private static boolean ref(char[][] b, String w, boolean[][] used, int i, int j, int k) {
        if (b[i][j] != w.charAt(k)) return false;
        if (k == w.length() - 1) return true;
        used[i][j] = true;
        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : dirs) {
            int x = i + d[0], y = j + d[1];
            if (x >= 0 && x < b.length && y >= 0 && y < b[0].length
                    && !used[x][y] && ref(b, w, used, x, y, k + 1)) {
                used[i][j] = false;
                return true;
            }
        }
        used[i][j] = false;
        return false;
    }

    private static void run(P79WordSearch solver, char[][] board, String word, String label) {
        try {
            boolean want = bruteForce(board, word);
            boolean got = solver.exist(board, word);
            boolean ok = got == want;

            System.out.printf("[%s] %-24s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s | 异常: %s%n", label, e);
        }
    }
}
