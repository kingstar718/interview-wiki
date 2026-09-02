import java.util.Arrays;

/**
 * LeetCode 66: 加一 (Plus One)
 * https://leetcode.cn/problems/plus-one/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 数学(Math) · 模拟(Simulation)
 * <p>【题型】模拟进位：从最低位开始 +1 并处理进位
 * <p>【考点】全 9 进位导致结果「长度 +1」的情形（如 999 → 1000）
 * <p>【关联】{@link P415AddStrings} —— 同为逐位进位模拟，本题是「+1」的特例
 *
 * <p>给定一个由整数组成的非空数组表示的非负整数（高位在前，每个元素是一位数字），对它加一，
 * 返回表示结果的数组。
 *
 * <p>── 思路引导 ──
 * <p>1. 从最低位（末尾）开始：若该位 &lt; 9，则 +1 后可直接返回（不产生进位）。
 *
 * <p>2. 关键观察：若该位是 9，+1 后变 0 并向前进位；一路进位直到遇到某个 &lt; 9 的位为止。
 *
 * <p>3. 核心技巧：从后往前扫，遇 9 置 0 继续，遇非 9 则 +1 并返回；
 *    若整趟都是 9（循环结束仍未返回），说明结果多一位——新建长度 +1 的数组，最高位为 1、其余为 0。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)（不含全 9 时新建的数组）。
 *    边界：999 → 1000；末位非 9 时一步返回。
 */
public class P66PlusOne {

    public int[] plusOne(int[] digits) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P66PlusOne p66 = new P66PlusOne();

        assertArrayEqual(new int[]{1, 2, 4}, p66.plusOne(new int[]{1, 2, 3}), "Example 1");
        assertArrayEqual(new int[]{4, 3, 2, 2}, p66.plusOne(new int[]{4, 3, 2, 1}), "Example 2");
        assertArrayEqual(new int[]{1, 0}, p66.plusOne(new int[]{9}), "Single 9 → 10");
        assertArrayEqual(new int[]{1, 0, 0, 0}, p66.plusOne(new int[]{9, 9, 9}), "All nines → 1000");
        assertArrayEqual(new int[]{2, 0, 0}, p66.plusOne(new int[]{1, 9, 9}), "Carry stops mid-way");
        assertArrayEqual(new int[]{1}, p66.plusOne(new int[]{0}), "Zero → 1");

        System.out.println("All tests passed!");
    }

    private static void assertArrayEqual(int[] expected, int[] actual, String testName) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, Arrays.toString(expected), Arrays.toString(actual)));
        }
        System.out.println("[PASS] " + testName);
    }
}
