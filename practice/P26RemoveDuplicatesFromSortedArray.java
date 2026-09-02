import java.util.Arrays;

/**
 * LeetCode 26: 删除有序数组中的重复项 (Remove Duplicates from Sorted Array)
 * https://leetcode.cn/problems/remove-duplicates-from-sorted-array/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 原地读写(In-place)
 * <p>【题型】原地读写指针：慢指针负责「写下一个该保留的位置」，快指针负责「读」，一趟把去重结果压到数组前段
 * <p>【考点】能否不开新数组、O(1) 额外空间原地去重；理解返回值是「新长度 k」而非新数组，且只保证前 k 个有效
 * <p>【关联】{@link P27RemoveElement} —— 同为原地读写指针，慢指针写「该留下的」，只是保留条件不同
 * <p>{@link P283MoveZeroes} —— 同构：慢指针放该留的元素，末尾再补默认值
 *
 * <p>给定一个**非严格递增**排列的数组 nums，请原地删除重复元素，使每个元素只出现一次，
 * 返回删除后数组的新长度 k。元素的相对顺序应保持一致，且需把前 k 个位置填成去重后的结果。
 * 要求：空间复杂度 O(1)，不要使用额外数组。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：用 Set 去重再写回，但要 O(n) 额外空间，且丢了「数组已排序」这个关键信息。
 *
 * <p>2. 关键观察：数组已排序，所以**重复元素必然相邻**。判断一个元素是不是新值，
 *    只需和「上一个已保留的值」比一比，不必回看全部。
 *
 * <p>3. 核心技巧 —— 快慢双指针：
 *    慢指针 slow 指向「下一个应写入的位置」（也即已保留区的末端），快指针 fast 从前往后扫。
 *    当 nums[fast] 与已保留的最后一个值不同，就是遇到新值，写到 slow 处并让 slow 前进。
 *    扫完后 slow 即新长度 k，前 k 个就是去重结果。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)。
 *    边界：空数组返回 0；单元素返回 1；全部相同返回 1。
 */
public class P26RemoveDuplicatesFromSortedArray {

    public int removeDuplicates(int[] nums) {
        // 边界：空数组 / 单元素本身即去重结果，直接返回长度
        int length = nums.length;
        if (length <= 1) {
            return length;
        }
        // Step 2：数组已排序 → 重复元素必相邻，只需和「已保留区的最后一个值」比较
        // Step 3：slow 指向下一个应写入位置；nums[0] 必保留，故从 1 起，lastNum 记录已保留的最后值
        int slow = 1;
        int lastNum = nums[0];
        // Step 3：fast 扫描，遇到与 lastNum 不同的新值就写到 slow 并让 slow 前进
        for (int fast = 1; fast < length; fast++) {
            if (nums[fast] != lastNum) {
                nums[slow] = nums[fast];
                lastNum = nums[fast];
                slow++;
            }
        }
        // 扫完后 slow 即去重后的新长度 k，前 k 个就是结果
        return slow;
    }

    public static void main(String[] args) {
        P26RemoveDuplicatesFromSortedArray p26 = new P26RemoveDuplicatesFromSortedArray();

        // 示例 1
        assertRemoveDuplicates(new int[]{1, 2}, new int[]{1, 1, 2}, p26, "Example 1");

        // 示例 2
        assertRemoveDuplicates(new int[]{0, 1, 2, 3, 4},
            new int[]{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}, p26, "Example 2");

        // 无重复：原样返回
        assertRemoveDuplicates(new int[]{1, 2, 3}, new int[]{1, 2, 3}, p26, "No duplicates");

        // 全部相同：只剩一个
        assertRemoveDuplicates(new int[]{2}, new int[]{2, 2, 2}, p26, "All same");

        // 单元素
        assertRemoveDuplicates(new int[]{5}, new int[]{5}, p26, "Single element");

        // 空数组
        assertRemoveDuplicates(new int[]{}, new int[]{}, p26, "Empty array");

        // 含负数
        assertRemoveDuplicates(new int[]{-3, -1, 0, 2},
            new int[]{-3, -3, -1, 0, 0, 2}, p26, "Negative numbers");

        System.out.println("All tests passed!");
    }

    /** 校验返回的新长度 k 与去重后前 k 个元素都符合预期。 */
    private static void assertRemoveDuplicates(int[] expectedPrefix, int[] input,
                                               P26RemoveDuplicatesFromSortedArray solver, String testName) {
        int k = solver.removeDuplicates(input);
        int[] actualPrefix = Arrays.copyOf(input, k);
        if (k != expectedPrefix.length || !Arrays.equals(expectedPrefix, actualPrefix)) {
            throw new AssertionError(String.format("[FAIL] %s: expected len=%d %s, but got len=%d %s",
                testName, expectedPrefix.length, Arrays.toString(expectedPrefix),
                k, Arrays.toString(actualPrefix)));
        }
        System.out.println("[PASS] " + testName);
    }
}
