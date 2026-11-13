import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 392: 判断子序列 (Is Subsequence)
 * https://leetcode.cn/problems/is-subsequence/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（子序列组 4/7——1143 特例 + 双指针秒杀 + 进阶位置表）
 *
 * <p>■ 题目描述
 *   给字符串 s 和 t。判断 s 是否为 t 的【子序列】：
 *     s 的全部字符都能在 t 中按 s 的顺序找到（可以跳着找、不要求连续）。
 *   即：从 t 中删除（或不删）一些字符，能恰好拼出 s。
 *
 * <p>■ 输入输出
 *   输入：String s（0 ≤ length ≤ 100）、String t（0 ≤ length ≤ 10^4；两串均小写英文）
 *   输出：boolean
 *
 * <p>■ 进阶（官方原话）：如果有大量 s 需要判断（s 很短、t 很长如数百万字符），
 *   怎么优化？——预建 t 的字符位置索引，每个 s 查询 O(|s|·log|t|) 或 O(|s|)。
 *
 * <p>■ 示例逐条
 * <pre>
 * s="abc", t="ahbgdc"   → true（官方）
 * s="axc", t="ahbgdc"   → false（官方：x 不在 t）
 * s=""（空串）           → true（空串是任何串的子序列——0 个字符的"拼接"）
 * s="abc", t=""          → false（t 空 s 非空）
 * s="bb", t="ahbgdc"     → false（b 只出现一次，bb 要两个 b——配额）
 * s="b", t="abc"         → true
 * s="twn", t="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxtwn" → true（尾部命中——参考定）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★）
 * <p>【标签】双指针 · 动态规划(DP)（进阶·1143 特例）· 二分（进阶位置表）
 * <p>【题型】三流派：①双指针 O(n+m) 单查最优；②1143 式 DP 判定 `LCS(s,t)==s.length()`；
 * <p>　　　　③位置表二分（大量 s 查同一 t 的预处理流——进阶官方答案）
 * <p>【考点】空串边界（空串恒 true）；字符配额（重复字符消耗）；"次序"的双指针表达
 * <p>【关联】P1143（本题 = LCS==|s| 的布尔化特例）；P300（子序列组骨架）
 *
 * <p>LCS 签名：isSubsequence(String s, String t)——注意参数顺序（s 在前！与 1143 text1/text2 语义对调：短的在前）。
 */
public class P392IsSubsequence {

    /** 你的实现（双指针主解：i 只在匹配时进、j 无条件进——次序与配额由结构天然表达）。 */
    public boolean isSubsequence(String s, String t) {
        int i = 0, j = 0;
        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) {
                i++;
            }
            j++;
        }
        return i == s.length();
    }

    /** 你的 DP 版（1143 特例修正版：n=t.length()、比 t.charAt——dp[m][n]==m 判定）。 */
    public boolean isSubsequenceDp(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n] == m;
    }

    public static void main(String[] args) {
        P392IsSubsequence solver = new P392IsSubsequence();

        run(solver, "abc", "ahbgdc", "官方例1 → true");
        run(solver, "axc", "ahbgdc", "官方例2 → false（x 不在）");
        run(solver, "", "ahbgdc", "空串 → true ★ 边界");
        run(solver, "abc", "", "t 空 s 非空 → false ★ 边界");
        run(solver, "", "", "双空 → true");
        run(solver, "bb", "ahbgdc", "配额不足（一个 b）→ false");
        run(solver, "b", "abc", "单字符命中 → true");
        run(solver, "twn", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxtwn", "尾部命中 → 参考定");
        run(solver, "acb", "ahbgdc", "次序反（a,c,b 的 b 在 c 前）→ false ★ 次序哨兵");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：递归"从 t 的 pos 起找 s[idx] 的下一个出现"——机制与双指针/DP 都不同。 */
    private static boolean bruteForce(String s, String t, int idx, int pos) {
        if (idx == s.length()) return true;
        for (int j = pos; j < t.length(); j++) {
            if (t.charAt(j) == s.charAt(idx)) {
                if (bruteForce(s, t, idx + 1, j + 1)) return true;
            }
        }
        return false;
    }

    /** 参考二（进阶位置表）：预建 t 每字符出现位置列表，逐 s 字符二分跳查——大量 s 时的预处理流。 */
    private static boolean refIndexTable(String s, String t) {
        Map<Character, List<Integer>> pos = new HashMap<>();
        for (int i = 0; i < t.length(); i++) {
            pos.computeIfAbsent(t.charAt(i), c -> new ArrayList<>()).add(i);
        }
        int prev = -1;                          // 上一个匹配字符在 t 中的位置
        for (int i = 0; i < s.length(); i++) {
            List<Integer> list = pos.get(s.charAt(i));
            if (list == null) return false;
            // 二分找第一个 > prev 的位置
            int lo = 0, hi = list.size();
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (list.get(mid) > prev) hi = mid;
                else lo = mid + 1;
            }
            if (lo == list.size()) return false;   // 该字符在 prev 之后不再出现
            prev = list.get(lo);
        }
        return true;
    }

    private static void run(P392IsSubsequence solver, String s, String t, String label) {
        try {
            boolean got = solver.isSubsequence(s, t);
            boolean want = bruteForce(s, t, 0, 0);
            boolean ref2 = refIndexTable(s, t);
            boolean dpGot = solver.isSubsequenceDp(s, t);
            boolean ok = got == want && got == ref2;
            String extra = (got != ref2) ? "（位置表参考不一致！）" : "";
            if (dpGot != want) extra += "（DP 版=" + dpGot + " 与参考不符）";

            System.out.printf("[%s] %-32s s=%-6s t=%-38s | 参考=%s 位置表=%s 实得=%s %s%n",
                    ok ? "PASS" : "FAIL", label, s, t, want, ref2, got, extra);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-32s s=%-6s t=%-38s | TODO 未实现%n", label, s, t);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-32s s=%-6s t=%-38s | 异常: %s%n", label, s, t, e);
        }
    }
}
