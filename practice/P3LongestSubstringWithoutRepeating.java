import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 3: 无重复字符的最长子串 (Longest Substring Without Repeating Characters)
 * https://leetcode.cn/problems/longest-substring-without-repeating-characters/
 *
 * <p>【难度】Medium
 * <p>【标签】哈希表(Hash Map) · 滑动窗口(Sliding Window) · 字符串(String)
 * <p>【题型】**滑动窗口 + 哈希记录位置**；「最长满足某约束的连续区间」的通用范式
 * <p>【考点】能否用双指针维护合法窗口做到 O(n)，以及左指针「只进不退」这个易错细节
 * <p>【关联】{@link P128LongestConsecutiveSequence} —— 同为「避免重复计算」把 O(n²) 压回 O(n)
 * <p>{@link P560SubarraySumEqualsK} —— 对比：那题含负数，滑动窗口失效只能用前缀和
 *
 * <p>给定一个字符串 s，请找出其中不含有重复字符的**最长子串**的长度。
 * 注意子串要求连续，子序列才不要求连续。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：枚举所有子串再逐个检查是否有重复字符，O(n³)（或用 Set 优化到 O(n²)）。
 *    瓶颈在于：每换一个起点就把后面重新扫一遍，前一轮的信息全丢了。
 *
 * <p>2. 关键观察：答案是**连续区间**，且具有单调性 ——
 *    若区间 [left, right] 无重复，它的任意子区间也无重复。
 *    所以不必枚举所有区间，只需维护一个「当前合法窗口」，右边界一路向右扩，
 *    一旦出现重复就把左边界收缩到重新合法为止。每个字符进出窗口各一次 → O(n)。
 *
 * <p>3. 核心技巧 —— 哈希表存「字符 → 它最后一次出现的下标」，实现左指针**一步跳到位**：
 *    right 前进遇到字符 c 时，若 c 上次出现的位置 lastIndex[c] 落在当前窗口内，
 *    说明窗口里已有一个 c，左边界必须跳到 lastIndex[c] + 1 把旧的那个排除掉。
 *
 * <p>⚠️ 易错点：必须判断 lastIndex[c] >= left，即「上次出现是否还在窗口内」。
 *    否则遇到 "abba" 时，处理最后的 'a' 会用它在下标 0 的旧记录，
 *    把 left 从 2 拉回到 1 —— **左指针绝不能回退**，否则窗口失效、答案偏大。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(min(n, m))，m 为字符集大小。
 *    边界：空串返回 0；全同字符返回 1；字符集固定时可用 int[128] 替代 HashMap 提速。
 */
public class P3LongestSubstringWithoutRepeating {

    public int lengthOfLongestSubstring(String s) {
        // TODO: 在此实现（返回最长无重复字符子串的长度）
        //       实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P3LongestSubstringWithoutRepeating p3 = new P3LongestSubstringWithoutRepeating();

        // 示例 1："abc"
        assertEqual(3, p3.lengthOfLongestSubstring("abcabcbb"), "Example 1");

        // 示例 2："b"
        assertEqual(1, p3.lengthOfLongestSubstring("bbbbb"), "Example 2");

        // 示例 3："wke"（注意 "pwke" 是子序列不是子串）
        assertEqual(3, p3.lengthOfLongestSubstring("pwwkew"), "Example 3");

        // 空串
        assertEqual(0, p3.lengthOfLongestSubstring(""), "Empty string");

        // 单字符
        assertEqual(1, p3.lengthOfLongestSubstring("a"), "Single character");

        // 关键用例：验证左指针不回退，答案是 "ab"/"ba" 而非 3
        assertEqual(2, p3.lengthOfLongestSubstring("abba"), "Left pointer must not move back");

        // 同类易错用例："vdf"
        assertEqual(3, p3.lengthOfLongestSubstring("dvdf"), "Tricky: dvdf");

        // 全不重复
        assertEqual(5, p3.lengthOfLongestSubstring("abcde"), "All unique");

        // 空格也是字符
        assertEqual(1, p3.lengthOfLongestSubstring(" "), "Space character");

        // 含数字与符号
        assertEqual(4, p3.lengthOfLongestSubstring("a1b2b2"), "Digits and repeats");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(
                String.format("[FAIL] %s: expected %d, but got %d", testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
