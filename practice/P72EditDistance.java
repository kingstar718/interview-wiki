import java.util.Arrays;

/**
 * LeetCode 72: 编辑距离 (Edit Distance)
 * https://leetcode.cn/problems/edit-distance/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（子序列组 5/7——二维 DP 终点站）
 *
 * <p>■ 题目描述
 *   给两个字符串 word1 和 word2。返回将 word1 转换成 word2 所需的
 *   【最少操作数】。允许三种操作，每种对一个单字符：
 *     1) 插入一个字符（在 word1 任意位置加一个字符）；
 *     2) 删除一个字符（从 word1 删一个字符）；
 *     3) 替换一个字符（把 word1 的某字符换成另一个字符）。
 *
 * <p>■ 输入输出
 *   输入：String word1（0 ≤ length ≤ 500）、String word2（0 ≤ length ≤ 500；小写英文）
 *   输出：int（最少操作数）
 *
 * <p>■ 示例逐条
 * <pre>
 * word1="horse", word2="ros" → 3（官方：h→r 替换、删 r、删 e）
 * word1="intention", word2="execution" → 5（官方例 2）
 * word1="", word2="" → 0
 * word1="", word2="abc" → 3（三次插入）
 * word1="abc", word2="" → 3（三次删除）
 * word1="a", word2="a" → 0（相同）
 * word1="a", word2="b" → 1（一次替换）
 * word1="abc", word2="yzz" → 3（全替换；参考定）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium 标注但被普遍当 Hard 练（页频 ★★★★★——二维 DP 终点站）
 * <p>【标签】动态规划(DP) · 二维 · 字符串
 * <p>【题型】前缀对语义（1143 同款）+ 三分支 min（替换/删除/插入各记一步）+ 两地基行（删空/插空）
 * <p>【考点】三分支的语义归属（dp[i-1][j-1]+1 是替换、dp[i-1][j]+1 是删除、dp[i][j-1]+1 是插入）；
 * <p>　　　　两行地基（dp[i][0]=i、dp[0][j]=j——"全删/全插"的显式初始化，1143 的 0 行列在此有代价）；
 * <p>　　　　相等分支免费继承（无 +1）
 * <p>【关联】P1143（同表骨架——继承变操作代价）；P583（只删版——被砍）；P44 通配符匹配（延伸）
 *
 * <p>LCS 签名：minDistance(String word1, String word2)。
 */
public class P72EditDistance {

    /** 你的实现（修正版：地基两行 + 相等免费继承——与 1143 的搬运三件套对账后）。 */
    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        P72EditDistance solver = new P72EditDistance();

        run(solver, "horse", "ros", "官方例1 → 3");
        run(solver, "intention", "execution", "官方例2 → 5");
        run(solver, "", "", "双空 → 0");
        run(solver, "", "abc", "空→非空 = 全插 → 3");
        run(solver, "abc", "", "非空→空 = 全删 → 3");
        run(solver, "a", "a", "相同 → 0");
        run(solver, "a", "b", "单字符替换 → 1");
        run(solver, "abc", "yzz", "全替换 → 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：递归"对 (i,j) 尝试全部四路"（相等免费 / 替换 / 删 / 插）+ 记忆化——
     * 机制与自底向上表不同（自顶向下）。
     */
    private static int dfs(String w1, String w2, int i, int j, java.util.Map<String, Integer> memo) {
        if (i == w1.length()) return w2.length() - j;    // word1 耗尽 → 插入剩余
        if (j == w2.length()) return w1.length() - i;    // word2 耗尽 → 删除剩余
        String key = i + "," + j;
        Integer cached = memo.get(key);
        if (cached != null) return cached;
        int best;
        if (w1.charAt(i) == w2.charAt(j)) {
            best = dfs(w1, w2, i + 1, j + 1, memo);      // 免费继承
        } else {
            int rep = dfs(w1, w2, i + 1, j + 1, memo);   // 替换
            int del = dfs(w1, w2, i + 1, j, memo);       // 删除
            int ins = dfs(w1, w2, i, j + 1, memo);       // 插入
            best = Math.min(rep, Math.min(del, ins)) + 1;
        }
        memo.put(key, best);
        return best;
    }

    private static int bruteForce(String word1, String word2) {
        return dfs(word1, word2, 0, 0, new java.util.HashMap<>());
    }

    private static void run(P72EditDistance solver, String word1, String word2, String label) {
        try {
            int got = solver.minDistance(word1, word2);
            int want = bruteForce(word1, word2);
            boolean ok = got == want;

            System.out.printf("[%s] %-18s word1=%-12s word2=%-12s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, word1, word2, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-18s word1=%-12s word2=%-12s | TODO 未实现%n", label, word1, word2);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-18s word1=%-12s word2=%-12s | 异常: %s%n", label, word1, word2, e);
        }
    }
}
