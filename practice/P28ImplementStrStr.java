/**
 * LeetCode 28: 找出字符串中第一个匹配项的下标 (Implement strStr())
 * https://leetcode.cn/problems/implement-strstr/
 *
 * <p>【难度】Easy
 * <p>【标签】字符串(String) · 双指针(Two Pointers) · 字符串匹配(KMP，进阶)
 * <p>【题型】字符串匹配：在 haystack 中找 needle 第一次出现的起始下标
 * <p>【考点】朴素逐位比对 O(nm) 能否写对边界；能否讲清 KMP 如何优化到 O(n+m)
 * <p>【关联】{@link P3LongestSubstringWithoutRepeating} —— 同为在字符串上移动起点 / 滑动窗口的扫描
 *
 * <p>给定 haystack 和 needle，返回 needle 在 haystack 中第一次出现的下标；不存在返回 -1。
 * 约定 needle 为空串时返回 0。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素：枚举 haystack 每个可能起点 i（0..n-m），从该处逐字符比对 needle，全等则返回 i。O(n·m)。
 *
 * <p>2. 关键观察：起点最多到 n-m，再往后放不下 needle，可提前停止，避免无谓比较。
 *
 * <p>3. 核心技巧（进阶）：朴素法每次失配都把主串指针退回重来；
 *    KMP 用 next 数组记录 needle 自身的最长相同前后缀，失配时让模式串「跳」而主串不回退，做到 O(n+m)。
 *    面试能写对朴素法 + 讲清 KMP 思路即可。
 *
 * <p>4. 复杂度：朴素 O(n·m)，KMP O(n+m)；空间 O(1) / O(m)。
 *    边界：needle 为空返回 0；needle 比 haystack 长返回 -1。
 */
public class P28ImplementStrStr {

    public int strStr(String haystack, String needle) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P28ImplementStrStr p28 = new P28ImplementStrStr();

        assertEqual(0, p28.strStr("sadbutsad", "sad"), "Example 1 (match at 0)");
        assertEqual(-1, p28.strStr("leetcode", "leeto"), "Example 2 (no match)");
        assertEqual(2, p28.strStr("hello", "ll"), "Match in middle");
        assertEqual(4, p28.strStr("mississippi", "issip"), "Match after false start");
        assertEqual(-1, p28.strStr("aaa", "aaaa"), "needle longer than haystack");
        assertEqual(0, p28.strStr("abc", ""), "Empty needle → 0");
        assertEqual(0, p28.strStr("a", "a"), "Single char match");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(String.format("[FAIL] %s: expected %d, but got %d",
                testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
