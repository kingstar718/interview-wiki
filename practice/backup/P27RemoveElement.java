import java.util.Arrays;

/**
 * LeetCode 27: 移除元素 (Remove Element)
 * https://leetcode.cn/problems/remove-element/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 原地读写(In-place)
 * <p>【题型】原地读写指针：慢指针只写「要保留（≠val）」的元素，把它们压到数组前段
 * <p>【考点】能否原地 O(1) 移除；理解「元素顺序可打乱、只看前 k 个」的宽松约定，据此想到更省写操作的对撞写法
 * <p>【关联】{@link P26RemoveDuplicatesFromSortedArray} —— 同为原地读写指针，慢指针写该留的，只是保留条件不同
 * <p>{@link P283MoveZeroes} —— 几乎同构：把「≠val」换成「≠0」即为本题，只是那题还要求保持相对顺序
 *
 * <p>给定数组 nums 和值 val，原地移除所有等于 val 的元素，返回移除后数组的新长度 k。
 * 元素顺序可以改变，且只需保证前 k 个元素为不等于 val 的元素（多余位置无所谓）。
 * 要求：空间复杂度 O(1)。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：遇到 val 就把后面元素整体前移一位，单次移动 O(n)，最坏 O(n²)。
 *
 * <p>2. 关键观察：题目**不要求保持相对顺序**，也不要求清空尾部。
 *    于是「删除」可以退化成「把要保留的元素挑出来写到前面」，无需真正搬移。
 *
 * <p>3. 核心技巧 —— 快慢双指针（同向）：
 *    慢指针 slow 指向下一个写入位置，快指针 fast 扫描；
 *    只要 nums[fast] != val，就写到 slow 并让 slow 前进，等于 val 则直接跳过。
 *    进阶：因顺序无所谓，还可用「对撞指针」把末尾元素搬来覆盖 val，进一步减少写操作。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)。
 *    边界：空数组返回 0；无一个等于 val 原样返回；全部等于 val 返回 0。
 */
public class P27RemoveElement {

    public int removeElement(int[] nums, int val) {
        int length = nums.length;
        // 边界：空数组无元素可留，返回 0（主循环对空数组也天然正确，此处显式短路）
        if (length == 0) {
            return 0;
        }
        // Step 3：slow 指向下一个写入位置（已保留区末端），fast 同向扫描
        int slow = 0;
        for (int fast = 0; fast < length; fast++) {
            // Step 3：只把要保留的 (≠ val) 写到 slow 并前移，等于 val 直接跳过（Step 2：顺序无所谓）
            if (nums[fast] != val) {
                nums[slow] = nums[fast];
                slow++;
            }
        }
        // slow 即移除后新长度 k
        return slow;
    }

    public static void main(String[] args) {
        P27RemoveElement p27 = new P27RemoveElement();

        // 示例 1：顺序无所谓，用多重集比较
        assertRemoveElement(new int[]{2, 2}, new int[]{3, 2, 2, 3}, 3, p27, "Example 1");

        // 示例 2：删掉全部 2 后剩 {0,1,3,0,4} —— 有两个 0，k=5
        assertRemoveElement(new int[]{0, 0, 1, 3, 4}, new int[]{0, 1, 2, 2, 3, 0, 4, 2}, 2, p27, "Example 2");

        // 无一个等于 val
        assertRemoveElement(new int[]{1, 2, 3}, new int[]{1, 2, 3}, 9, p27, "None equals val");

        // 全部等于 val
        assertRemoveElement(new int[]{}, new int[]{4, 4, 4}, 4, p27, "All equal val");

        // 空数组
        assertRemoveElement(new int[]{}, new int[]{}, 1, p27, "Empty array");

        // val 在首尾
        assertRemoveElement(new int[]{2, 3}, new int[]{1, 2, 3, 1}, 1, p27, "Val at both ends");

        System.out.println("All tests passed!");
    }

    /**
     * 校验新长度 k 与前 k 个元素。因本题「顺序可改变」，
     * 故把期望与实际前缀都排序后按多重集比较。
     */
    private static void assertRemoveElement(int[] expectedRemaining, int[] input, int val,
                                            P27RemoveElement solver, String testName) {
        int k = solver.removeElement(input, val);
        int[] actualPrefix = Arrays.copyOf(input, k);
        int[] expectedSorted = expectedRemaining.clone();
        Arrays.sort(expectedSorted);
        Arrays.sort(actualPrefix);
        if (k != expectedRemaining.length || !Arrays.equals(expectedSorted, actualPrefix)) {
            throw new AssertionError(String.format("[FAIL] %s: expected len=%d %s(乱序), but got len=%d %s",
                testName, expectedRemaining.length, Arrays.toString(expectedSorted),
                k, Arrays.toString(actualPrefix)));
        }
        System.out.println("[PASS] " + testName);
    }
}
