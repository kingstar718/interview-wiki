/**
 * LeetCode 14: 最长公共前缀 (Longest Common Prefix)
 * https://leetcode.cn/problems/longest-common-prefix/
 *
 * <p>【难度】Easy
 * <p>【标签】字符串(String) · 字典树(Trie，进阶)
 * <p>【题型】模拟 / 纵向扫描：逐列比较所有字符串同一位置的字符
 * <p>【考点】边界处理（空数组、含空串）；纵向扫描 vs 横向逐个求公共前缀两种写法
 * <p>【关联】{@link P415AddStrings} —— 同为按位/按列逐字符处理字符串的模拟题
 *
 * <p>编写一个函数查找字符串数组 strs 中的最长公共前缀；若不存在公共前缀，返回空串 ""。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素 / 横向：先拿 strs[0] 当前缀，逐个字符串求它与前缀的公共部分并不断缩短，直到扫完。
 *
 * <p>2. 关键观察（纵向）：也可按「列」比较——固定列 j，检查所有字符串第 j 个字符是否都相同；
 *    一旦某串到头或出现不同，公共前缀就到 j 为止。
 *
 * <p>3. 核心技巧：纵向扫描——外层遍历 strs[0] 的每个字符位置 j，内层比较其余串的第 j 位，
 *    发现越界或不等就返回 strs[0] 的前 j 个字符。
 *
 * <p>4. 复杂度：时间 O(∑len)，空间 O(1)。
 *    边界：空数组返回 ""；任一字符串为空串时公共前缀必为 ""。
 */
public class P14LongestCommonPrefix {

    public String longestCommonPrefix(String[] strs) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P14LongestCommonPrefix p14 = new P14LongestCommonPrefix();

        assertEqual("fl", p14.longestCommonPrefix(new String[]{"flower", "flow", "flight"}), "Example 1");
        assertEqual("", p14.longestCommonPrefix(new String[]{"dog", "racecar", "car"}), "Example 2 (no common)");
        assertEqual("a", p14.longestCommonPrefix(new String[]{"a"}), "Single string");
        assertEqual("", p14.longestCommonPrefix(new String[]{}), "Empty array");
        assertEqual("", p14.longestCommonPrefix(new String[]{"", "abc"}), "Contains empty string");
        assertEqual("abc", p14.longestCommonPrefix(new String[]{"abc", "abc", "abc"}), "All identical");
        assertEqual("ab", p14.longestCommonPrefix(new String[]{"ab", "abc"}), "One is prefix of another");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(String expected, String actual, String testName) {
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format("[FAIL] %s: expected \"%s\", but got \"%s\"",
                testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
