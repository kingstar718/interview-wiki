import java.util.Arrays;

/**
 * LeetCode 647: 回文子串 (Palindromic Substrings)
 * https://leetcode.cn/problems/palindromic-substrings/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（子序列组 6/7——区间 DP / 中心扩展双解）
 *
 * <p>■ 题目描述
 *   给字符串 s，统计【回文子串】的个数。
 *   "子串" = **连续**的一段（区别于子序列——本组 718/1143 的分野在区间题里延续）；
 *   "回文" = 正读反读相同。
 *   **计数规则**：位置不同的回文串【算不同】——即使内容一样（"aaa" 里三个 "a" 算三个）。
 *
 * <p>■ 输入输出
 *   输入：String s（1 ≤ length ≤ 1000；小写英文）
 *   输出：int（回文子串个数）
 *
 * <p>■ 示例逐条
 * <pre>
 * s="abc" → 3（a、b、c——三个单字符回文）
 * s="aaa" → 6（a×3 + aa×2 + aaa×1——位置不同都算）
 * s="a"   → 1（最小）
 * s="ab"  → 2（a、b——无多字符回文）
 * s="aba" → 4（a、b、a、aba——中心 b 的 aba 是长回文）
 * s="abba"→ 6（a、b、b、a、bb、abba——偶长中心 bb 的扩展链）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】动态规划(DP) · 区间 DP · 双指针（中心扩展）
 * <p>【题型】双解：①中心扩展（奇长 2n-1 中心 + 偶长 n-1 中心，O(n²)/O(1)）；②区间 DP
 * <p>　　　　dp[i][j] = s[i..j] 是否回文（布尔判定型——516 的姊妹题）
 * <p>【考点】"位置不同算多次"的计数语义；区间 DP 的依赖方向（dp[i+1][j-1]——**i 依赖更大下标**，
 * <p>　　　　遍历要 i 倒序或按长度枚举）；奇偶中心的双枚举
 * <p>【关联】P516（最长回文子序列——姊妹题，跳选版）；P718（连续语义延续）
 *
 * <p>LCS 签名：countSubstrings(String s)。
 */
public class P647PalindromicSubstrings {

    /** 你的实现【中心扩展流】：奇偶双中心 + 同心圆扩展计数（对照：区间 DP 流见 countSubstringsDp）。 */
    public int countSubstrings(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            count += expand(s, i, i);
            count += expand(s, i, i + 1);
        }
        return count;
    }

    // 中心向两边
    private int expand(String s, int l, int r) {
        int cnt = 0;
        while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
            cnt++;
            l--;
            r++;
        }
        return cnt;
    }

    /** 区间 DP 流（对照位·暂跳过）：dp[i][j]=s[i..j] 是否回文——i 倒序（依赖左下角）+ j-i<2 兜底。
     *  状态：主解（中心扩展）已 AC；此壳留待二刷复盘时补写（骨架提示已存 chat 档案）。 */
    public int countSubstringsDp(String s) {
        // TODO(二刷): i 倒序 for i=n-1..0 / j=i..n-1 / s[i]==s[j] && (j-i<2 || dp[i+1][j-1]) → count++
        throw new UnsupportedOperationException("TODO: 二刷时补写");
    }

    public static void main(String[] args) {
        P647PalindromicSubstrings solver = new P647PalindromicSubstrings();

        run(solver, "abc", "官方例1 → 3");
        run(solver, "aaa", "官方例2 → 6（位置计数）");
        run(solver, "a", "最小 → 1");
        run(solver, "ab", "无多字符 → 2");
        run(solver, "aba", "奇长中心 → 4");
        run(solver, "abba", "偶长中心 → 6");
        run(solver, "aaab", "重复干扰 → 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：全子串枚举 O(n³)——逐段双指针对撞判回文，机制与双解全不同。 */
    private static int bruteForce(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                boolean pal = true;
                for (int l = i, r = j; l < r; l++, r--) {
                    if (s.charAt(l) != s.charAt(r)) { pal = false; break; }
                }
                if (pal) count++;
            }
        }
        return count;
    }

    private static void run(P647PalindromicSubstrings solver, String s, String label) {
        try {
            int got = solver.countSubstrings(s);
            int want = bruteForce(s);
            boolean ok = got == want;

            System.out.printf("[%s] %-24s s=%-10s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, s, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s s=%-10s | TODO 未实现%n", label, s);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s s=%-10s | 异常: %s%n", label, s, e);
        }
    }
}
