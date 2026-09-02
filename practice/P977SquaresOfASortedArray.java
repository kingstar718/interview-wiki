import java.util.Arrays;

/**
 * LeetCode 977: 有序数组的平方 (Squares of a Sorted Array)
 * https://leetcode.cn/problems/squares-of-a-sorted-array/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 对撞指针
 * <p>【题型】对撞指针从两端取大值，结果数组倒着填；「平方后最值在两端」的经典套路
 * <p>【考点】能否想到平方后最大值只可能出现在两端，用 O(n) 双指针替代「先平方再排序」的 O(n log n)
 * <p>【关联】P88MergeSortedArray —— 同为「从后往前填充」的归并思路
 *
 * <p>给定一个按非递减顺序排序的整数数组 nums，返回每个数字的平方组成的新数组，
 * 要求也按非递减顺序排序。
 * 约束：1 &lt;= nums.length &lt;= 10^4；-10^4 &lt;= nums[i] &lt;= 10^4。
 */
public class P977SquaresOfASortedArray {

    public int[] sortedSquares(int[] nums) {
        int left = 0, right = nums.length - 1;
        int[] res = new int[nums.length];
        int cur = nums.length - 1;
        while (left <= right) {
            int lPow = nums[left] * nums[left];
            int rPow = nums[right] * nums[right];
            if (lPow < rPow) {
                res[cur--] = rPow;
                right--;
            } else {
                res[cur--] = lPow;
                left++;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P977SquaresOfASortedArray s = new P977SquaresOfASortedArray();

        run(s, new int[]{-4, -1, 0, 3, 10}, new int[]{0, 1, 9, 16, 100});    // 示例1
        run(s, new int[]{-7, -3, 2, 3, 11}, new int[]{4, 9, 9, 49, 121});     // 示例2
        run(s, new int[]{1, 2, 3}, new int[]{1, 4, 9});                       // 边界：全正数
        run(s, new int[]{-5, -3, -2}, new int[]{4, 9, 25});                   // 边界：全负数
        run(s, new int[]{-2, 0, 2}, new int[]{0, 4, 4});                      // 边界：对称含 0
        run(s, new int[]{0}, new int[]{0});                                   // 边界：单元素 0
        run(s, new int[]{-2}, new int[]{4});                                  // 边界：单元素负数
        run(s, new int[]{-10000, -1, 10000}, new int[]{1, 100000000, 100000000}); // 边界：最值，注意 int 是否溢出
    }

    private static void run(P977SquaresOfASortedArray s, int[] nums, int[] expected) {
        String label = "nums=" + Arrays.toString(nums);
        try {
            int[] got = s.sortedSquares(nums);
            boolean pass = Arrays.equals(got, expected);
            System.out.println((pass ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(got)
                    + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
