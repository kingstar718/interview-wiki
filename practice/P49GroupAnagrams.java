import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 49: 字母异位词分组 (Group Anagrams)
 * https://leetcode.cn/problems/group-anagrams/
 *
 * <p>【难度】Medium
 * <p>【标签】哈希表(Hash Map) · 字符串(String) · 排序(Sorting)
 * <p>【题型】**设计哈希键**做分组；把「判断两两是否等价」转成「计算同一个代表元」
 * <p>【考点】能否想到给每组构造唯一「指纹」作为 key，以及能否分析出计数法优于排序法
 * <p>【关联】{@link P1TwoSum} —— 哈希表基础：那题用哈希「查值」，本题用哈希「分组」
 *
 * <p>给定一个字符串数组 strs，把「字母异位词」分到同一组，返回分组结果（组的顺序不限）。
 * 字母异位词：由相同字母以不同顺序重排而成，如 "eat" / "tea" / "ate"。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：两两比较是否互为异位词，把同组的并起来。
 *    比较一对是 O(k)，两两比较是 O(n²k)，还要额外处理「已归组」标记，很笨重。
 *
 * <p>2. 关键观察：分组问题不该用「两两比较」，而该用「**给每个元素算一个代表元(指纹)**」——
 *    只要指纹相同就是一组，一次遍历用哈希表按指纹归堆即可，从 O(n²) 降到 O(n)。
 *    那么异位词的指纹是什么？它们的共性是「字母组成完全相同，只是顺序不同」。
 *
 * <p>3. 核心技巧 —— 构造哈希键（两种经典做法）：
 *    a) 排序法：把字符串内部字符排序，"eat"/"tea"/"ate" 都变成 "aet"，用它当 key。
 *       直观好写，单次排序 O(k log k)。
 *    b) 计数法（本文实现，进阶最优）：统计 26 个字母出现次数，拼成 "#1#0#1…" 这样的 key，
 *       单次 O(k)，总复杂度 O(nk)，在 k 很大时更优。面试说出这条能加分。
 *       注意计数间要加分隔符（本文用 '#'），否则 [1,11] 与 [11,1] 会拼成同一个 key 而撞键。
 *    随后按 key 把同组字符串塞进同一个 List，即完成分组。
 *
 * <p>4. 复杂度（计数法）：时间 O(n·k)，空间 O(n·k)；n 为字符串个数，k 为平均长度。
 *    边界：空数组返回空列表；空串 "" 自成一组（其计数键就是 26 个 "#0"）。
 */
public class P49GroupAnagrams {

    public List<List<String>> groupAnagrams(String[] strs) {
        // 指纹 → 同组字符串
        Map<String, List<String>> groups = new HashMap<>();
        for (String s : strs) {
            // Step 3：给每个字符串算指纹（异位词指纹必相同）
            String key = build(s);
            // Step 2：按指纹归堆 —— 同 key 塞进同一个 List，无则新建
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }
        return new ArrayList<>(groups.values());
    }

    /** 计算字符串的「计数指纹」：字母组成相同则指纹相同（Step 3b 计数法）。 */
    public String build(String s) {
        // Step 3b-1：统计 26 个字母各自出现次数（异位词的计数完全一致）
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }
        // Step 3b-2：把计数数组拼成 key，用 '#' 分隔避免多位数相邻撞键
        StringBuilder sb = new StringBuilder();
        for (int c : count) {
            sb.append('#').append(c);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        P49GroupAnagrams p49 = new P49GroupAnagrams();

        // 示例 1
        assertGroupsEqual("[[ate,eat,tea],[bat],[nat,tan]]",
            p49.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}),
            "Example 1");

        // 空串输入
        assertGroupsEqual("[[]]", p49.groupAnagrams(new String[]{""}), "Empty string");

        // 单个字符串
        assertGroupsEqual("[[a]]", p49.groupAnagrams(new String[]{"a"}), "Single string");

        // 空数组
        assertGroupsEqual("[]", p49.groupAnagrams(new String[]{}), "Empty array");

        // 全部互为异位词
        assertGroupsEqual("[[abc,bca,cab]]",
            p49.groupAnagrams(new String[]{"abc", "bca", "cab"}), "All anagrams");

        // 无任何异位词
        assertGroupsEqual("[[a],[b],[c]]",
            p49.groupAnagrams(new String[]{"a", "b", "c"}), "No anagrams");

        // 完全重复的字符串应留在同一组内
        assertGroupsEqual("[[ab,ab]]",
            p49.groupAnagrams(new String[]{"ab", "ab"}), "Identical strings");

        // 长度相同但字母不同，不能混为一组
        assertGroupsEqual("[[aab],[abb]]",
            p49.groupAnagrams(new String[]{"aab", "abb"}), "Same length, different letters");

        System.out.println("All tests passed!");
    }

    /**
     * 分组结果的组间顺序、组内顺序都不确定，
     * 故先归一化成「组内排序 + 组间排序」的字符串再比较。
     */
    private static void assertGroupsEqual(String expected, List<List<String>> actual, String testName) {
        List<String> normalized = new ArrayList<>();
        for (List<String> group : actual) {
            List<String> sortedGroup = new ArrayList<>(group);
            java.util.Collections.sort(sortedGroup);
            normalized.add("[" + String.join(",", sortedGroup) + "]");
        }
        java.util.Collections.sort(normalized);
        String actualStr = "[" + String.join(",", normalized) + "]";

        if (!expected.equals(actualStr)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, expected, actualStr));
        }
        System.out.println("[PASS] " + testName);
    }
}
