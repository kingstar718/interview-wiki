/**
 * LeetCode 516: 最长回文子序列 (Longest Palindromic Subsequence)
 * https://leetcode.cn/problems/longest-palindromic-subsequence/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（挂账还账·区间 DP——5 的子序列版）
 *
 * <p>■ 题目描述
 *   给字符串 s，找出**最长回文子序列**并返回长度。
 *   子序列=可以删字符但**不改变剩余字符顺序**（≠子串——不要求连续）。
 *
 * <p>■ 输入输出（约束）
 *   输入：String s（1 ≤ len ≤ 1000）
 *   输出：int（最长回文子序列长度）
 *
 * <p>■ 示例逐条
 * <pre>
 * "bbbab" → 4（"bbbb"——跳过 a；子串最长只有 3，子序列可以跳选）
 * "cbbd" → 2（"bb"）
 * "a" → 1
 * "aaaa" → 4
 * "abcde" → 1（全不同，单字符兜底）
 * "character" → 5（"carac"？——参考验证）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·字节/微软）
 * <p>【标签】字符串 · 动态规划
 * <p>【题型】区间 DP（5 的 dp 表直接变体——连续变可跳选）：
 * <p>　　dp[i][j]=s[i..j] 最长回文子序列长度；
 * <p>　　两端相等：dp[i][j]=dp[i+1][j-1]+2；不等：max(dp[i+1][j], dp[i][j-1])
 * <p>【考点】与 5 的两处差别（不连续→跳选分支；存长度→存 boolean）；
 * <p>　　　　区间 DP 填表顺序（按 L 从小到大——5 刚学的）；i 倒序遍历的等价形态
 * <p>【关联】P5（子串版——刚收官）；P1143 LCS（同族：本题=LCS(s, reverse(s)) 对偶）；
 * <p>　　　　P647（连续计数版）
 *
 * <p>LCS 签名：longestPalindromeSubseq(String s)。
 */
public class P516LongestPalindromicSubsequence {

    /** 你的实现（区间 DP：两端白捡+2 / 二选一扔取大；L 与 i 的双 off-by-one 已修）。 */
    public int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];
        for (int i = 0; i < n; i++) {
            dp[i][i] = 1;
        }
        for (int L = 2; L <= n; L++) {
            for (int i = 0; i <= n - L; i++) {
                int j = i + L - 1;
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = dp[i + 1][j - 1] + 2;
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][n - 1];
    }

    public static void main(String[] args) {
        P516LongestPalindromicSubsequence solver = new P516LongestPalindromicSubsequence();

        run(solver, "bbbab", "官方例1 → 4");
        run(solver, "cbbd", "官方例2 → 2");
        run(solver, "a", "单字符 → 1");
        run(solver, "aaaa", "全等 → 4");
        run(solver, "abcde", "全不同 → 1");
        run(solver, "character", "混合 → 5");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举全部 2^n 子序列→回文判定取最长（指数级，机制独立）。 */
    private static int bruteForce(String s) {
        int n = s.length(), best = 1;
        for (int mask = 1; mask < (1 << n); mask++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) if ((mask >> i & 1) == 1) sb.append(s.charAt(i));
            String t = sb.toString();
            if (t.length() > best && t.equals(new StringBuilder(t).reverse().toString()))
                best = t.length();
        }
        return best;
    }

    private static void run(P516LongestPalindromicSubsequence solver, String s, String label) {
        try {
            int want = bruteForce(s);
            int got = solver.longestPalindromeSubseq(s);
            boolean ok = got == want;

            System.out.printf("[%s] %-14s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-14s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-14s | 异常: %s%n", label, e);
        }
    }
}
