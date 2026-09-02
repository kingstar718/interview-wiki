/**
 * LeetCode 415: 字符串相加 (Add Strings)
 * https://leetcode.cn/problems/add-strings/
 *
 * <p>【难度】Easy
 * <p>【标签】字符串(String) · 数学(Math) · 模拟(Simulation)
 * <p>【题型】模拟竖式加法：从末位对齐，逐位相加并处理进位
 * <p>【考点】不借助整数/大数类型；两串不等长时的对齐，以及最后一次进位的收尾
 * <p>【关联】{@link P66PlusOne} —— 同为逐位进位模拟
 * <p>{@link P14LongestCommonPrefix} —— 同为按位逐字符处理字符串
 *
 * <p>给定两个字符串形式的非负整数 num1、num2，返回它们的和（同样以字符串返回）。
 * 不能把它们直接转成整数，也不能使用大整数库。
 *
 * <p>── 思路引导 ──
 * <p>1. 模拟竖式：从两串的末位开始对齐，逐位相加，维护一个进位 carry。
 *
 * <p>2. 关键观察：某串先到头就把它当作 0 继续；循环条件要同时兼顾「两串还有位」和「carry 未清空」。
 *
 * <p>3. 核心技巧：双指针 i、j 各指两串末位，sum = d1 + d2 + carry，
 *    本位取 sum % 10、进位取 sum / 10；结果逆序生成，最后反转（或用头插）。
 *
 * <p>4. 复杂度：时间 O(max(len1, len2))，空间 O(1)（不含结果）。
 *    边界："0"+"0"="0"；两串不等长；最高位再进一位（如 "99"+"1"="100"）。
 */
public class P415AddStrings {

    public String addStrings(String num1, String num2) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P415AddStrings p415 = new P415AddStrings();

        assertEqual("134", p415.addStrings("11", "123"), "Example 1");
        assertEqual("533", p415.addStrings("456", "77"), "Different lengths");
        assertEqual("0", p415.addStrings("0", "0"), "Zero + zero");
        assertEqual("100", p415.addStrings("99", "1"), "Carry grows length");
        assertEqual("1000", p415.addStrings("1", "999"), "Carry from shorter operand");
        assertEqual("10", p415.addStrings("5", "5"), "Single digits carry");

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
