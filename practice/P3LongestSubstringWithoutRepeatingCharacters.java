import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 3: 无重复字符的最长子串 (Longest Substring Without Repeating Characters)
 * https://leetcode.cn/problems/longest-substring-without-repeating-characters/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 14/50——不定长滑窗"最长"面·与 76 对仗）
 *
 * <p>■ 题目描述
 *   给字符串 s，求**不含重复字符**的**最长子串**（连续）的长度。
 *
 * <p>■ 输入输出（约束）
 *   输入：String s（0 ≤ length ≤ 5×10^4；ASCII 可含空格/符号/大小写）
 *   输出：int（最长无重复子串长度；空串 → 0）
 *
 * <p>■ 示例逐条
 * <pre>
 * s="abcabcbb" → 3（"abc"）
 * s="bbbbb" → 1（"b"）
 * s="pwwkew" → 3（"wke"——注意 pwke 是子序列不是子串）
 * s="" → 0
 * s=" " → 1（空格也是字符）
 * s="au" → 2
 * s="dvdf" → 3（"vdf"——l 只能前跳不能回退的哨兵）★
 * s="abba" → 2（重复在左边界左侧时的哨兵）★
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★·全厂）
 * <p>【标签】哈希表 · 字符串 · 滑动窗口
 * <p>【题型】不定长滑窗"最长"模板：窗口保持无重复，r 进，重复则 l 收缩，每步记长度
 * <p>【考点】"最长 vs 最短"模板分野（合法时扩张记答案 vs 合法时收缩记答案）；
 * <p>　　　　**l 只能前跳不能回退**（Math.max(l, last[c]+1)——重复出现在 l 左侧时的哨兵）；
 * <p>　　　　哈希存"最近下标"实现跳跃收缩（逐步收缩版也行——两版对照）
 * <p>【关联】P76（姊妹——最短面刚收官）；P209（同族数组版）；灵神基础算法精讲 03 的"最长"代表题
 *
 * <p>LCS 签名：lengthOfLongestSubstring(String s)。
 */
public class P3LongestSubstringWithoutRepeatingCharacters {

    /** 你的实现【灵神原生模板】（布尔窗=窗口内存；重复则 while 收缩到合法；合法窗每步记答案）。 */
    public int lengthOfLongestSubstring(String s) {
        boolean[] has = new boolean[128];
        int res = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            while (has[c]) {
                has[s.charAt(left)] = false;
                left++;
            }
            has[c] = true;
            res = Math.max(res, right - left + 1);
        }
        return res;
    }

    public static void main(String[] args) {
        P3LongestSubstringWithoutRepeatingCharacters solver = new P3LongestSubstringWithoutRepeatingCharacters();

        run(solver, "abcabcbb", "官方例1 → 3");
        run(solver, "bbbbb", "全同 → 1");
        run(solver, "pwwkew", "官方例3 → 3（子序列陷阱）");
        run(solver, "", "空串 → 0");
        run(solver, " ", "空格 → 1");
        run(solver, "au", "两异 → 2");
        run(solver, "dvdf", "l 不回退 → 3 ★ 哨兵");
        run(solver, "abba", "左外重复 → 2 ★ 哨兵");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举所有起点向右扩到首个重复——O(n²)，机制与滑窗跳跃完全不同。 */
    private static int bruteForce(String s) {
        int best = 0;
        for (int i = 0; i < s.length(); i++) {
            java.util.Set<Character> seen = new java.util.HashSet<>();
            for (int j = i; j < s.length(); j++) {
                if (!seen.add(s.charAt(j))) break;
            }
            best = Math.max(best, seen.size());
        }
        return best;
    }

    private static void run(P3LongestSubstringWithoutRepeatingCharacters solver, String s, String label) {
        try {
            int got = solver.lengthOfLongestSubstring(s);
            int want = bruteForce(s);
            boolean ok = got == want;

            System.out.printf("[%s] %-24s s=%-16s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, s, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s s=%-16s | TODO 未实现%n", label, s);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s s=%-16s | 异常: %s%n", label, s, e);
        }
    }
}
