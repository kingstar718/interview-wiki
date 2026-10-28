/**
 * LeetCode 242: 有效的字母异位词 (Valid Anagram)
 * https://leetcode.cn/problems/valid-anagram/
 *
 * <p>【难度】Easy
 * <p>【标签】哈希表(Hash Table) · 字符串(String) · 计数
 * <p>【题型】只含小写字母 → int[26] 当哈希表；s 遇到字符 +1，t 遇到字符 -1，最后全 0 即异位词
 * <p>【考点】长度不等先剪枝；不能只判断"字符存在"还要判断"次数相同"
 * <p>【关联】P49 字母异位词分组（批量版）；P438 找所有异位词（滑动窗口版）
 *
 * <p>给定两个字符串 s 和 t，编写一个函数来判断 t 是否是 s 的字母异位词。
 * 字母异位词：两个字符串中每个字符的出现次数完全相同。
 * 约束：1 &lt;= s.length, t.length &lt;= 5 * 10^4；s 和 t 仅包含小写字母。
 */
public class P242ValidAnagram {

    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        int[] count = new int[26];
        char[] sChars = s.toCharArray();
        char[] tChars = t.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            count[sChars[i] - 97]++;
            count[tChars[i] - 97]--;
        }
        for (int i : count) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        P242ValidAnagram solver = new P242ValidAnagram();

        run(solver, "anagram", "nagaram", true);   // 示例1
        run(solver, "rat", "car", false);          // 示例2
        run(solver, "", "", true);                 // 边界：两个空串
        run(solver, "a", "", false);               // 边界：长度不等
        run(solver, "aab", "abb", false);          // 陷阱：字符都存在但次数不同
        run(solver, "aacc", "ccac", false);        // 陷阱：同型
        run(solver, "abc", "cba", true);           // 顺序不同
        run(solver, "a", "a", true);               // 单字符相同
        run(solver, "abcdefghijklmnopqrstuvwxyz", "zyxwvutsrqponmlkjihgfedcba", true); // 26 个字母全用
        run(solver, "aaabbb", "bbbaaa", true);     // 重复字符次数相同
        run(solver, "aaabbb", "aabbbb", false);    // 重复字符次数不同
    }

    private static void run(P242ValidAnagram solver, String s, String t, boolean expected) {
        String label = "\"" + s + "\" vs \"" + t + "\"";
        try {
            boolean got = solver.isAnagram(s, t);
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " expected=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}