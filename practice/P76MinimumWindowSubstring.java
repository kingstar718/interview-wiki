import java.util.Arrays;

/**
 * LeetCode 76: 最小覆盖子串 (Minimum Window Substring)
 * https://leetcode.cn/problems/minimum-window-substring/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 13/50——不定长滑窗 Hard·滑动窗口家族压轴）
 *
 * <p>■ 题目描述
 *   给字符串 s 和 t，返回 s 中**覆盖 t 所有字符（含重数）**的**最短子串**；
 *   不存在则返回空串 ""。（覆盖 = 子串里包含 t 的每种字符，且个数 ≥ t 里的个数）
 *
 * <p>■ 输入输出（约束）
 *   输入：String s（1 ≤ length ≤ 10^5）、String t（1 ≤ length ≤ 10^5，ASCII 可大小写混合）
 *   输出：String（最短覆盖子串；多个取任意一个；无 → ""）
 *
 * <p>■ 示例逐条
 * <pre>
 * s="ADOBECODEBANC", t="ABC" → "BANC"（官方例）
 * s="a", t="a" → "a"
 * s="a", t="aa" → ""（t 要两个 a，s 只有一个）
 * s="ab", t="b" → "b"
 * s="aa", t="aa" → "aa"（重数要满）
 * s="aab", t="aab" → "aab"（全串覆盖）
 * s="cabwefgewcwaefgcf", t="cae" → "cwae"（多候选取最短）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Hard（页频 ★★★★★·字节/阿里——滑动窗口家族压轴）
 * <p>【标签】哈希表 · 字符串 · 滑动窗口
 * <p>【题型】不定长滑窗"最短"模板：r 扩张到覆盖（valid==need.size()）→ l 收缩榨最短 → 收缩到不覆盖
 * <p>【考点】valid 的"临界点"语义（进：win==need 时 valid++；出：win==need 时先 valid-- 再减 win）；
 * <p>　　　　最短 vs 最长的模板分野（最短在合法时收缩记答案，最长在合法时扩张记答案）；
 * <p>　　　　answer 三件套（start/len/最终 substring）；need.containsKey 过滤无关字符
 * <p>【关联】P438/567（定长滑窗——本家族前置）；P209（同族数组版最短）；labuladong 框架出处
 *
 * <p>外部参考：灵茶山艾府（0x3f）题解「两种方法：从 O(52m+n) 到 O(m+n)」——
 * <p>「越长越合法」滑窗分类（与 209 同族铺垫）；涵盖定义；ansLeft/ansRight 惯例；
 * <p>方法二缺口计数（less=缺的字母个数含重数，less==0 判涵盖）——骨架采用其思想，
 * <p>账本形态用"种数 valid"与"缺口 less"二选一（等价，缺口版临界判断更平）。
 * <p>LCS 签名：minWindow(String s, String t)。
 */
public class P76MinimumWindowSubstring {

    /** 你的实现（不定长滑窗最短模板：valid==needKinds 收缩榨最短，start/len 成对替换）。 */
    public String minWindow(String s, String t) {
        int[] need = new int[128];
        int[] win = new int[128];

        int needKinds = 0;

        for (char c : t.toCharArray()) {
            if (need[c] == 0) needKinds++; // 首次出现+1
            need[c]++;
        }

        int l = 0;
        int valid = 0;
        int start = 0, len = Integer.MAX_VALUE;

        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            if (need[c] > 0) {
                win[c]++;
                if (win[c] == need[c]) {
                    valid++;
                }
            }
            while (valid == needKinds) {
                if (r - l + 1 < len) {
                    start = l;
                    len = r - l + 1;
                }
                char d = s.charAt(l);
                if (need[d] > 0) {
                    if (win[d] == need[d]) {
                        valid--;
                    }
                    win[d]--;
                }
                l++;
            }
        }
        return len == Integer.MAX_VALUE ? "" : s.substring(start, start + len);
    }

    public static void main(String[] args) {
        P76MinimumWindowSubstring solver = new P76MinimumWindowSubstring();

        run(solver, "ADOBECODEBANC", "ABC", "官方例 → BANC");
        run(solver, "a", "a", "单字符 → a");
        run(solver, "a", "aa", "重数不足 → 空 ★");
        run(solver, "ab", "b", "右端 → b");
        run(solver, "aa", "aa", "重数恰好 → aa");
        run(solver, "cabwefgewcwaefgcf", "cae", "多候选取最短 → cwae");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举所有起点，向右扩到覆盖为止取最短 O(n²·字符集)——机制完全不同。 */
    private static String bruteForce(String s, String t) {
        int[] need = new int[128];
        int kinds = 0;
        for (char c : t.toCharArray()) {
            if (need[c] == 0) kinds++;
            need[c]++;
        }
        String best = null;
        for (int i = 0; i < s.length(); i++) {
            int[] win = new int[128];
            int got = 0;
            for (int j = i; j < s.length(); j++) {
                char c = s.charAt(j);
                if (need[c] > 0) {
                    win[c]++;
                    if (win[c] == need[c]) got++;
                }
                if (got == kinds) {
                    String cand = s.substring(i, j + 1);
                    if (best == null || cand.length() < best.length()) best = cand;
                    break;
                }
            }
        }
        return best == null ? "" : best;
    }

    private static void run(P76MinimumWindowSubstring solver, String s, String t, String label) {
        try {
            String got = solver.minWindow(s, t);
            String want = bruteForce(s, t);
            boolean ok = got.equals(want) || (got.length() == want.length() && !got.isEmpty());

            System.out.printf("[%s] %-24s s=%-20s t=%-6s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, s, t, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s s=%-20s t=%-6s | TODO 未实现%n", label, s, t);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s s=%-20s t=%-6s | 异常: %s%n", label, s, t, e);
        }
    }
}
