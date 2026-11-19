import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 438: 找到字符串中所有字母异位词 (Find All Anagrams in a String)
 * https://leetcode.cn/problems/find-all-anagrams-in-a-string/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 11/50——定长滑动窗口）
 *
 * <p>■ 题目描述
 *   给两个字符串 s 和 p，找出 s 中所有 p 的**字母异位词子串**的**起始下标**
 *   （异位词 = 字母相同、顺序可不同；子串 = 连续）。
 *
 * <p>■ 输入输出（约束）
 *   输入：String s（1 ≤ length ≤ 3×10^4）、String p（1 ≤ length ≤ 3×10^4，全小写字母）
 *   输出：List<Integer>（所有起始下标，任意顺序）
 *
 * <p>■ 示例逐条
 * <pre>
 * s="cbaebabacd", p="abc" → [0,6]（"cba"@0、"bac"@6）
 * s="abab", p="ab" → [0,1,2]（"ab"@0、"ba"@1、"ab"@2——重叠也算）
 * s="a", p="ab" → []（p 比 s 长——直接空）
 * s="aa", p="a" → [0,1]（单字符窗口滑两步）
 * s="baa", p="aa" → [1]（"aa"@1）
 * s="af", p="aa" → []（字母不在集合内）
 * s="abaacb", p="abc" → [3]（"acb"@3）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·美团）
 * <p>【标签】哈希表 · 滑动窗口 · 字符串
 * <p>【题型】定长滑动窗口：窗口=p.length() 固定，进一个出一个，26 计数数组比对
 * <p>【考点】"进"在判断前、"出"在判断前（i>=k 时先出后判——定长窗口的经典节奏）；
 * <p>　　　　Arrays.equals(need, win) O(26) 比对（好写）vs valid 增量计数（优化）；p>s 短路
 * <p>【关联】P560（前驱课：560 有负数不能用窗口——438 全小写正数窗口正好上场）；P49（异位词判定的 key 版）；
 * <p>　　　　P567 排列子串（同题返回 boolean——同一份代码）；P76 最小覆盖子串（变长窗口进阶）
 *
 * <p>LCS 签名：findAnagrams(String s, String p)。
 */
public class P438FindAllAnagrams {

    /** 你的实现（灵茶山艾府定长滑窗：cnt=p的字母−窗口字母差值画像，cnt<0 收缩，等长即异位词）。 */
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> res = new ArrayList<>();
        int[] cnt = new int[26];
        for (char c : p.toCharArray()) cnt[c-'a']++;
        int left = 0;
        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i) - 'a';
            cnt[c]--;
            while (cnt[c] < 0) {
                cnt[s.charAt(left) - 'a']++;
                left++;
            }
            if (i - left + 1 == p.length()) {
                res.add(left);
                cnt[s.charAt(left++) - 'a']++;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P438FindAllAnagrams solver = new P438FindAllAnagrams();

        run(solver, "cbaebabacd", "abc", "官方例1 → [0,6]");
        run(solver, "abab", "ab", "官方例2 → [0,1,2]（重叠）");
        run(solver, "a", "ab", "p>s → []");
        run(solver, "aa", "a", "单字符窗 → [0,1]");
        run(solver, "baa", "aa", "右端 → [1]");
        run(solver, "af", "aa", "字母不齐 → []");
        run(solver, "abaacb", "abc", "中段 → [3]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：每个起点取定长子串、排序后与排序的 p 比对——机制与计数窗口完全不同。 */
    private static List<Integer> bruteForce(String s, String p) {
        List<Integer> res = new ArrayList<>();
        if (s.length() < p.length()) return res;
        char[] pp = p.toCharArray();
        Arrays.sort(pp);
        String key = new String(pp);
        for (int i = 0; i + p.length() <= s.length(); i++) {
            char[] w = s.substring(i, i + p.length()).toCharArray();
            Arrays.sort(w);
            if (key.equals(new String(w))) res.add(i);
        }
        return res;
    }

    private static void run(P438FindAllAnagrams solver, String s, String p, String label) {
        try {
            List<Integer> got = solver.findAnagrams(s, p);
            List<Integer> want = bruteForce(s, p);
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-28s s=%-12s p=%-5s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, s, p, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s s=%-12s p=%-5s | TODO 未实现%n", label, s, p);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s s=%-12s p=%-5s | 异常: %s%n", label, s, p, e);
        }
    }
}
