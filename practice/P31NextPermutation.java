import java.util.Arrays;

/**
 * LeetCode 31: 下一个排列 (Next Permutation)
 * https://leetcode.cn/problems/next-permutation/
 *
 * <p>【难度】Medium
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 原地读写(In-place)
 * <p>【题型】原地读写指针：从右找「可增大的最靠右位置」，换一个尽量小的更大值，再把后缀翻成最小
 * <p>【考点】能否想清「下一个排列 = 在尽量靠右处做最小幅度的增大」，并用一次扫描 + 原地反转做到 O(n)/O(1)
 * <p>【关联】{@link P189RotateArray} —— 同为原地读写指针，都靠「区间反转」这一子操作在 O(1) 空间内重排
 *
 * <p>把数组 nums 重排成字典序中**下一个更大的排列**；若不存在（已是最大排列，即完全降序），
 * 则重排为最小排列（完全升序）。要求**原地**修改、只用常数额外空间；方法无返回值。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：生成所有排列排序后找当前的下一个，O(n!)，完全不可行。
 *
 * <p>2. 关键观察：要「变大且尽量小」，就应在**尽量靠右**的位置增大。
 *    从右往左看，若某段是降序，它已是该段能取到的最大排列，动它只会更小；
 *    所以先从右找到第一个「打破降序」的位置 i（即 nums[i] < nums[i+1]），i 就是要增大的那一位。
 *
 * <p>3. 核心技巧 —— 一次扫描 + 原地反转：
 *    a) 从右向左找第一个 i 使 nums[i] < nums[i+1]；
 *    b) 若找到：再从最右找第一个比 nums[i] 大的 nums[j]，交换 nums[i]、nums[j]
 *       （j 是「比 nums[i] 大的数里最小的那个」，换完 i 位恰好增大最少）；
 *    c) 此时 i 右边仍是降序，把 [i+1, 末尾] **反转**成升序，得到该前缀下的最小后缀；
 *    d) 若第 a 步没找到 i（整个数组降序，已是最大排列）：直接反转整个数组变成最小排列。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)。
 *    边界：单元素不变；完全降序→整体反转为升序；含重复元素时第 b 步用「严格大于」定位 j。
 */
public class P31NextPermutation {

    public void nextPermutation(int[] nums) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P31NextPermutation p31 = new P31NextPermutation();

        // 示例 1
        assertNextPermutation(new int[]{1, 3, 2}, new int[]{1, 2, 3}, p31, "Example 1");

        // 示例 2：已是最大排列 → 回到最小
        assertNextPermutation(new int[]{1, 2, 3}, new int[]{3, 2, 1}, p31, "Example 2 (wrap to smallest)");

        // 示例 3
        assertNextPermutation(new int[]{1, 5, 1}, new int[]{1, 1, 5}, p31, "Example 3");

        // 交换点在中间
        assertNextPermutation(new int[]{3, 1, 2}, new int[]{2, 3, 1}, p31, "Swap in the middle");

        // 需反转较长后缀
        assertNextPermutation(new int[]{1, 4, 2, 3}, new int[]{1, 3, 4, 2}, p31, "Reverse suffix");

        // 单元素：不变
        assertNextPermutation(new int[]{1}, new int[]{1}, p31, "Single element");

        // 两元素升序 → 降序
        assertNextPermutation(new int[]{2, 1}, new int[]{1, 2}, p31, "Two ascending");

        // 两元素降序 → 回到升序
        assertNextPermutation(new int[]{1, 2}, new int[]{2, 1}, p31, "Two descending (wrap)");

        // 含重复元素
        assertNextPermutation(new int[]{5, 1, 1}, new int[]{1, 5, 1}, p31, "With duplicates");

        System.out.println("All tests passed!");
    }

    /** 调用 nextPermutation 后，按 nums 的最终内容与期望比较。 */
    private static void assertNextPermutation(int[] expected, int[] input,
                                              P31NextPermutation solver, String testName) {
        solver.nextPermutation(input);
        if (!Arrays.equals(expected, input)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, Arrays.toString(expected), Arrays.toString(input)));
        }
        System.out.println("[PASS] " + testName);
    }
}
