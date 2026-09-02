import java.util.Arrays;

/**
 * LeetCode 27: 移除元素 (Remove Element)
 *
 * <p>给定数组 nums 和值 val，原地移除所有等于 val 的元素，返回移除后数组的新长度 k。
 * 元素顺序可以改变，且只需保证前 k 个元素为不等于 val 的元素（多余位置无所谓）。
 * 要求：空间复杂度 O(1)。
 */
public class P27RemoveElement {

    // TODO: 实现 removeElement，返回移除后新长度 k
    public int removeElement(int[] nums, int val) {
        int slow = 0, fast = 0;
        while (fast < nums.length) {
            if (nums[fast] != val) {
                nums[slow] = nums[fast];
                slow++;
            }
            fast++;
        }
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