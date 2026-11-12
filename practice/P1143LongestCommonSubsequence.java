import java.util.Arrays;

/**
 * LeetCode 1143: 最长公共子序列 (Longest Common Subsequence)
 * https://leetcode.cn/problems/longest-common-subsequence/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（子序列组 3/7——与 718 的"归零 vs 继承"对照组）
 *
 * <p>■ 题目描述
 *   给两个字符串 text1 和 text2，返回它们【最长公共子序列】的长度。
 *   "公共子序列" = 在两个字符串中都出现（可以跳着选、**不要求连续**），
 *   且字符的相对顺序一致的最长子序列。若不存在公共子序列，返回 0。
 *
 * <p>■ 输入输出
 *   输入：String text1（1 ≤ length ≤ 1000）、String text2（1 ≤ length ≤ 1000；均小写英文）
 *   输出：int（最长公共子序列的长度）
 *
 * <p>■ 示例逐条
 * <pre>
 * text1="abcde", text2="ace" → 3（官方："ace"——跳着选）
 * text1="abc", text2="abc"   → 3（完全相同）
 * text1="abc", text2="def"   → 0（无公共）
 * text1="bl", text2="yby"    → 2（"by"——text2 里 b,y 分开站；text1 里连续）
 * text1="bsbininm", text2="jmjkbkjkv" → 1（只有单字符公共——参考定）
 * text1="aa", text2="a"      → 1（长不等，取公共的 1）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★——二维 DP 门面）
 * <p>【标签】动态规划(DP) · 二维 · 字符串
 * <p>【题型】双锚二维 dp[i][j] = "text1 前 i 个 与 text2 前 j 个"的 LCS——
 * <p>　　　　匹配 dp[i-1][j-1]+1；不匹配【继承】max(dp[i-1][j], dp[i][j-1])（718 是归零——本题分野）
 * <p>【考点】"前缀型"二维语义（与 718 的"双锚结尾型"一字之差——【dp[i][j] 不要求用到末尾锚】）；
 * <p>　　　　answer = dp[m][n]（前缀型答案在右下角！与 718 全表 max 相反）；
 * <p>　　　　不匹配继承的两方向（跳谁）；一维滚动 + prev 快照（可选对照）
 * <p>【关联】P718（连续对照组）；P392（特例+双指针）；P72（编辑距离——同骨架三分支）
 *
 * <p>LCS 签名：longestCommonSubsequence(String text1, String text2)。
 */
public class P1143LongestCommonSubsequence {

    /** 你的实现（前缀型二维表：匹配 dp[i-1][j-1]+1 / 不匹配双向继承——answer=dp[m][n]）。 */
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        P1143LongestCommonSubsequence solver = new P1143LongestCommonSubsequence();

        run(solver, "abcde", "ace", "官方例1 → 3（ace）");
        run(solver, "abc", "abc", "完全相同 → 3");
        run(solver, "abc", "def", "无公共 → 0");
        run(solver, "bl", "yby", "跳位公共 → 1（仅 b——l 不在 text2）");
        run(solver, "bsbininm", "jmjkbkjkv", "稀疏公共 → 参考定");
        run(solver, "aa", "a", "长短不等 → 1");
        run(solver, "aaa", "aa", "重复字符 → 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：递归"从 (i,j) 出发"全枚举（匹配吃 +1 / 不匹配两方向）——
     * 纯递归无表（n ≤ 12 内可信），机制与 dp 表不同。
     */
    private static int dfsLcs(String a, String b, int i, int j) {
        if (i >= a.length() || j >= b.length()) return 0;
        if (a.charAt(i) == b.charAt(j)) {
            return 1 + dfsLcs(a, b, i + 1, j + 1);   // 匹配必吃（LCS 性质）
        }
        return Math.max(dfsLcs(a, b, i + 1, j), dfsLcs(a, b, i, j + 1));
    }

    private static int bruteForce(String text1, String text2) {
        return dfsLcs(text1, text2, 0, 0);
    }

    private static void run(P1143LongestCommonSubsequence solver, String text1, String text2, String label) {
        try {
            int got = solver.longestCommonSubsequence(text1, text2);
            int want = bruteForce(text1, text2);
            boolean ok = got == want;

            System.out.printf("[%s] %-20s text1=%-12s text2=%-12s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, text1, text2, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-20s text1=%-12s text2=%-12s | TODO 未实现%n", label, text1, text2);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-20s text1=%-12s text2=%-12s | 异常: %s%n", label, text1, text2, e);
        }
    }
}
