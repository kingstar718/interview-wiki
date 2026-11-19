import java.util.Arrays;

/**
 * LeetCode 567: 字符串的排列 (Permutation in String)
 * https://leetcode.cn/problems/permutation-in-string/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 12/50——438 的 boolean 孪生）
 *
 * <p>■ 题目描述
 *   给两个字符串 s1 和 s2，判断 s2 是否**包含 s1 的某个排列**作为子串
 *   （即 s2 中存在一个长度 = |s1| 的连续子串，与 s1 互为字母异位词）。
 *
 * <p>■ 输入输出（约束）
 *   输入：String s1、String s2（1 ≤ 长度 ≤ 10^4，全小写字母）
 *   输出：boolean
 *
 * <p>■ 示例逐条
 * <pre>
 * s1="ab", s2="eidbaooo" → true（"ba"@3）
 * s1="ab", s2="eidboaoo" → false（b 和 a 中间隔着 o）
 * s1="a", s2="a" → true（单字符全同）
 * s1="adc", s2="dcda" → true（"cda"@1）
 * s1="abc", s2="ab" → false（s1 比 s2 长）
 * s1="hello", s2="ooolleoooleh" → false（字母数不齐：l×3 但 h 在末尾成不了连续窗）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·快手）
 * <p>【标签】哈希表 · 双指针 · 字符串
 * <p>【题型】438 完全同构：定长滑窗 + 计数比较，命中即 return true（遍历完 false）
 * <p>【考点】"存在性"改写（找排列子串 = 找等长异位词窗）；灵神模板迁移（cnt<0 收缩）；
 * <p>　　　　命中即返回（收集 vs 存在的终止条件差）
 * <p>【关联】P438（前驱——同代码改返回值）；P76（最小覆盖——不定长进阶）
 *
 * <p>LCS 签名：checkInclusion(String s1, String s2)。
 */
public class P567PermutationInString {

    /** 你的实现（灵神模板迁移：438 同款差值画像，收集改存在性——命中即 return true）。 */
    public boolean checkInclusion(String s1, String s2) {
        int[] cnt = new int[26];
        for (char c : s1.toCharArray()) cnt[c - 'a']++;
        int left = 0;
        for (int right = 0; right < s2.length(); right++) {
            int c = s2.charAt(right) - 'a';
            cnt[c]--;
            while (cnt[c] < 0) {
                cnt[s2.charAt(left) - 'a']++;
                left++;
            }
            if (right - left + 1 == s1.length()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        P567PermutationInString solver = new P567PermutationInString();

        run(solver, "ab", "eidbaooo", "官方例1 → true");
        run(solver, "ab", "eidboaoo", "官方例2 → false");
        run(solver, "a", "a", "单字符 → true");
        run(solver, "adc", "dcda", "乱序窗 → true");
        run(solver, "abc", "ab", "s1>s2 → false");
        run(solver, "hello", "ooolleoooleh", "字母数不齐 → false");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：定长子串逐个排序比对——机制与滑窗计数完全不同。 */
    private static boolean bruteForce(String s1, String s2) {
        if (s2.length() < s1.length()) return false;
        char[] a = s1.toCharArray();
        Arrays.sort(a);
        String key = new String(a);
        for (int i = 0; i + s1.length() <= s2.length(); i++) {
            char[] w = s2.substring(i, i + s1.length()).toCharArray();
            Arrays.sort(w);
            if (key.equals(new String(w))) return true;
        }
        return false;
    }

    private static void run(P567PermutationInString solver, String s1, String s2, String label) {
        try {
            boolean got = solver.checkInclusion(s1, s2);
            boolean want = bruteForce(s1, s2);
            boolean ok = got == want;

            System.out.printf("[%s] %-28s s1=%-8s s2=%-16s | 参考=%b 实得=%b%n",
                    ok ? "PASS" : "FAIL", label, s1, s2, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s s1=%-8s s2=%-16s | TODO 未实现%n", label, s1, s2);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s s1=%-8s s2=%-16s | 异常: %s%n", label, s1, s2, e);
        }
    }
}
