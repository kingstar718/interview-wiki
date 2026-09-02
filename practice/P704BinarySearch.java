import java.util.Arrays;

/**
 * 704. 二分查找（Binary Search）
 * 给定升序整型数组 nums 和目标值 target，返回 target 的下标；不存在返回 -1。
 */
public class P704BinarySearch {

    // TODO: 实现二分查找（左闭右闭模板）
    public int search(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                l = mid + 1;
            } else {
                r = mid - 1;
            }
        }
        return -1; // 占位，请替换为你的实现
    }

    public static void main(String[] args) {
        P704BinarySearch s = new P704BinarySearch();

        run(s, new int[]{-1, 0, 3, 5, 9, 12}, 9, 4);   // 示例1：命中
        run(s, new int[]{-1, 0, 3, 5, 9, 12}, 2, -1);  // 示例2：未命中
        run(s, new int[]{}, 0, -1);                    // 边界：空数组
        run(s, new int[]{5}, 5, 0);                    // 边界：单元素命中
        run(s, new int[]{5}, 3, -1);                   // 边界：单元素未命中
        run(s, new int[]{1, 3, 5}, 1, 0);              // 边界：目标在最左
        run(s, new int[]{1, 3, 5}, 5, 2);              // 边界：目标在最右
        run(s, new int[]{1, 2, 2, 2, 3}, 2, -2);       // 重复元素：只校验 nums[idx] == target
    }

    private static void run(P704BinarySearch s, int[] nums, int target, int expected) {
        int got = s.search(nums, target);
        boolean pass = expected == -2
                ? (got >= 0 && got < nums.length && nums[got] == target)
                : got == expected;
        System.out.println((pass ? "PASS" : "FAIL") + " | nums=" + Arrays.toString(nums)
                + " target=" + target + " got=" + got + " expected=" + expected);
    }
}