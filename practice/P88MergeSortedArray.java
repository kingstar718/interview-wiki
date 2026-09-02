import java.util.Arrays;

/**
 * LeetCode 88: 合并两个有序数组 (Merge Sorted Array)
 * https://leetcode.cn/problems/merge-sorted-array/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 原地读写(In-place)
 * <p>【题型】原地读写指针（逆向）：从后往前写，用 nums1 尾部的空位当归并的落点
 * <p>【考点】能否想到「从大到小、从后往前填」避免覆盖 nums1 未处理的元素，从而做到 O(1) 额外空间
 * <p>【关联】{@link P283MoveZeroes} —— 同为原地读写指针，但本题的关键是**逆向**写以规避覆盖
 * <p>{@link P26RemoveDuplicatesFromSortedArray} —— 都吃「已排序」这个前提，用指针一趟完成
 *
 * <p>给定两个非递减数组 nums1、nums2，长度分别记为 m、n。
 * nums1 的实际长度为 m+n，后 n 个位置为 0（预留空位）。请把 nums2 合并进 nums1，
 * 使 nums1 成为一个非递减数组。要求原地完成、空间 O(1)（合并结果就地写在 nums1 里，方法无返回值）。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：把 nums2 拷到 nums1 尾部再整体排序，O((m+n)log(m+n))，且没利用「两者已有序」。
 *
 * <p>2. 关键观察：若**从前往后**归并写入 nums1，写指针会覆盖掉 nums1 中还没比较的元素。
 *    但 nums1 的**尾部是空的**——从后往前写就永远写在空位上，不会覆盖有效数据。
 *
 * <p>3. 核心技巧 —— 逆向三指针：
 *    i 指向 nums1 的有效末尾 (m-1)，j 指向 nums2 末尾 (n-1)，写指针 k 指向 nums1 总末尾 (m+n-1)。
 *    每次把 nums1[i] 与 nums2[j] 中**较大者**写到 k 处并前移对应指针；
 *    最后若 nums2 还有剩余需继续搬（nums1 剩余本就在正确位置，无需管）。
 *
 * <p>4. 复杂度：时间 O(m+n)，空间 O(1)。
 *    边界：m=0（nums1 全为占位）→ 结果即 nums2；n=0 → nums1 不变。
 */
public class P88MergeSortedArray {

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        // Step 3：i 指 nums1 有效末尾，j 指 nums2 末尾，k 指 nums1 总末尾（从空位往前写）
        int i = m - 1;
        int j = n - 1;
        int k = m + n - 1;
        // Step 3：两者都有剩余时把较大者写到 k（Step 2：从后往前写，永远落在空位、不覆盖未处理元素）
        while (i >= 0 && j >= 0) {
            if (nums1[i] >= nums2[j]) {
                nums1[k] = nums1[i];
                k--;
                i--;
            } else {
                nums1[k] = nums2[j];
                k--;
                j--;
            }
        }
        // 只需补 nums2 剩余；nums1 剩余的 nums1[0..i] 本就在正确位置，无需搬动
        while (j >= 0) {
            nums1[k] = nums2[j];
            j--;
            k--;
        }
    }

    public static void main(String[] args) {
        P88MergeSortedArray p88 = new P88MergeSortedArray();

        // 示例 1
        assertMerge(new int[]{1, 2, 2, 3, 5, 6},
            new int[]{1, 2, 3, 0, 0, 0}, 3, new int[]{2, 5, 6}, 3, p88, "Example 1");

        // nums2 为空：nums1 不变
        assertMerge(new int[]{1}, new int[]{1}, 1, new int[]{}, 0, p88, "nums2 empty");

        // nums1 有效部分为空：结果即 nums2
        assertMerge(new int[]{1}, new int[]{0}, 0, new int[]{1}, 1, p88, "nums1 empty");

        // nums2 全部更小，需插到最前
        assertMerge(new int[]{1, 2, 3, 4, 5, 6},
            new int[]{4, 5, 6, 0, 0, 0}, 3, new int[]{1, 2, 3}, 3, p88, "nums2 all smaller");

        // 交错 + 含负数
        assertMerge(new int[]{-5, -2, 0, 1, 3, 7},
            new int[]{-2, 1, 7, 0, 0, 0}, 3, new int[]{-5, 0, 3}, 3, p88, "Interleaved with negatives");

        System.out.println("All tests passed!");
    }

    /** 调用 merge 后，按 nums1 的最终内容与期望比较。 */
    private static void assertMerge(int[] expected, int[] nums1, int m, int[] nums2, int n,
                                    P88MergeSortedArray solver, String testName) {
        solver.merge(nums1, m, nums2, n);
        if (!Arrays.equals(expected, nums1)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, Arrays.toString(expected), Arrays.toString(nums1)));
        }
        System.out.println("[PASS] " + testName);
    }
}
