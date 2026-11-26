/**
 * LeetCode 5: 最长回文子串 (Longest Palindromic Substring)
 * https://leetcode.cn/problems/longest-palindromic-substring/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·回文双流派）
 *
 * <p>■ 题目描述
 *   给字符串 s，返回 s 中**最长的回文子串**（连续；若有多个等长，返回任一）。
 *   （回文=正读反读相同）
 *
 * <p>■ 输入输出（约束）
 *   输入：String s（1 ≤ len ≤ 1000，仅数字和英文字母）
 *   输出：String
 *
 * <p>■ 示例逐条
 * <pre>
 * "babad" → "bab"（"aba" 也算对——任一）
 * "cbbd" → "bb"（偶数长度回文——中心是两个字符之间）
 * "a" → "a"
 * "ac" → "a"（"c" 也对——单字符兜底）
 * "aaaa" → "aaaa"（全等串——中心扩展要去重吗？答案唯一即可）
 * "bananas" → "anana"
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★·字节/腾讯/微软/亚马逊）
 * <p>【标签】字符串 · 动态规划 · 双指针
 * <p>【题型】中心扩展（主推）/ 区间 DP（对照）：
 * <p>　　中心扩展：每个"中心"向两侧扩——中心有 2n-1 个（n 个字符 + n-1 个缝隙）
 * <p>　　区间 DP：dp[i][j]=s[i..j] 是否回文，dp[i][j]=s[i]==s[j] && dp[i+1][j-1]
 * <p>【考点】奇偶两中心（单字符/双字符起点）；边界 while 的越界检查；
 * <p>　　　　647（回文子串计数）的兄弟——计数变"记最长"
 * <p>【关联】P647（一阶段计数版）；P516（挂账区间 DP 版——可跳选的子序列）；
 * <p>　　　　P131（回文切割——判断复用）
 *
 * <p>LCS 签名：longestPalindrome(String s)。
 */
public class P5LongestPalindromicSubstring {

    /** 你的实现（中心扩展：2n-1 中心共用 expand；len/start 由 r-l-1 和 i-(r-1)/2 换算）。 */
    public String longestPalindrome(String s) {
        if (s.length() == 1) return s;
        int start = 0, len = 1;
        for (int i = 0; i < s.length(); i++) {

            int r1 = expand(s, i, i);
            int r2 = expand(s, i, i + 1);

            int r = Math.max(r1, r2);
            if (r > len) {
                len = r;
                start = i - (r - 1) / 2;
            }
        }
        return s.substring(start, start + len);
    }

    private int expand(String s, int l, int r) {
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
            l--;
            r++;
        }
        return r - l - 1;
    }

    /** 对照入口（区间 DP 版：dp[i][j]=s[i..j] 是否回文，按区间长度 L 填表）。 */
    public String longestPalindromeDP(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int start = 0, len = 1;
        for (int L = 1; L <= n; L++) {
            for (int i = 0; i + L - 1 < n; i++) {
                int j = i + L - 1;
                if (s.charAt(i) != s.charAt(j)) {
                    continue;
                }
                if (L <= 3 || dp[i + 1][j - 1]) {   // L≤3 内部为空/单字符恒真
                    dp[i][j] = true;
                    if (L > len) {
                        start = i;
                        len = L;
                    }
                }
            }
        }
        return s.substring(start, start + len);
    }

    public static void main(String[] args) {
        P5LongestPalindromicSubstring solver = new P5LongestPalindromicSubstring();

        run(solver, "babad", "官方例1 → bab 或 aba");
        run(solver, "cbbd", "官方例2 偶回文 → bb");
        run(solver, "a", "单字符 → a");
        run(solver, "ac", "无回文对 → a 或 c");
        run(solver, "aaaa", "全等串 → aaaa");
        run(solver, "bananas", "五元回文 → anana");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举所有子串→反转比对（O(n³) 机制完全独立）。 */
    private static String bruteForce(String s) {
        String best = "";
        for (int i = 0; i < s.length(); i++)
            for (int j = i; j < s.length(); j++) {
                String sub = s.substring(i, j + 1);
                if (sub.length() > best.length() && sub.equals(new StringBuilder(sub).reverse().toString()))
                    best = sub;
            }
        return best;
    }

    private static void run(P5LongestPalindromicSubstring solver, String s, String label) {
        try {
            String want = bruteForce(s);
            String got = solver.longestPalindrome(s);
            String gotDp = solver.longestPalindromeDP(s);
            // 多答案题：只比长度（任一等长合法）
            boolean ok = got.length() == want.length()
                    && isPalindrome(got) && s.contains(got)
                    && gotDp.length() == want.length()
                    && isPalindrome(gotDp) && s.contains(gotDp);

            System.out.printf("[%s] %-20s | 参考=%s(len=%d) 扩展=%s DP=%s%n",
                    ok ? "PASS" : "FAIL", label, want, want.length(), got, gotDp);
            if (!ok) System.out.println("    got 不是最长回文: " + got + " / " + gotDp);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-20s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-20s | 异常: %s%n", label, e);
        }
    }

    private static boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false;
        return true;
    }
}
