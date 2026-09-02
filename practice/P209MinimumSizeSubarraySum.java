import java.util.Arrays;

/**
 * LeetCode 209: 长度最小的子数组 (Minimum Size Subarray Sum)
 * https://leetcode.cn/problems/minimum-size-subarray-sum/
 *
 * <p>【难度】Medium
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 滑动窗口(Sliding Window)
 * <p>【题型】滑动窗口「右扩左缩」的原型：右指针扩展窗口直到满足条件，左指针收缩找最优
 * <p>【考点】能否想到窗口条件满足后要「收缩左边界」找最短；内层用 while 而非 if（可能连续收缩多次）
 * <p>【关联】P3LongestSubstringWithoutRepeating —— 同为滑动窗口，但 3 是「最长满足条件」而本题是「最短满足条件」，收缩时机恰好相反
 *
 * <p>给定一个含有 n 个正整数的数组 nums 和一个正整数 target。
 * 找出该数组中满足其和 ≥ target 的**长度最小的连续子数组**，并返回其长度；
 * 如果不存在符合条件的子数组，返回 0。
 * 约束：1 &lt;= target &lt;= 10^9；1 &lt;= nums.length &lt;= 10^5；1 &lt;= nums[i] &lt;= 10^4。
 */
public class P209MinimumSizeSubarraySum {

    public int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int sum = 0;
        int minLen = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left];
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    public static void main(String[] args) {
        P209MinimumSizeSubarraySum s = new P209MinimumSizeSubarraySum();

        run(s, 7, new int[]{2, 3, 1, 2, 4, 3}, 2);        // 示例1：[4,3]
        run(s, 4, new int[]{1, 4, 4}, 1);                 // 示例2：[4]
        run(s, 11, new int[]{1, 1, 1, 1, 1, 1, 1, 1}, 0); // 示例3：总和 8 < 11，不存在
        run(s, 5, new int[]{5}, 1);                       // 边界：单元素恰好 = target
        run(s, 6, new int[]{5}, 0);                       // 边界：单元素 < target
        run(s, 10, new int[]{1, 2, 3, 4}, 4);             // 边界：恰好需要整个数组（minLen 初始化别踩坑）
        run(s, 4, new int[]{1, 2, 3, 2}, 2);              // 边界：窗口落在末尾 [2,3] 或 [3,2]
        run(s, 15, new int[]{5, 1, 3, 5, 10, 7, 4, 9, 2, 8}, 2); // 边界：中间某段 [10,7]

        // 大 target 边界：10^5 个 10000，全数组和恰好 = 10^9 = target → 需全数组，len=100000
        // 同时验证 int 求和上限（10^9 不溢出）与”恰好全数组“时 minLen 初始化的坑
        int[] big = new int[100000];
        Arrays.fill(big, 10000);
        run(s, 1000000000, big, 100000);
    }

    private static String fmt(int[] nums) {
        if (nums.length <= 12) return Arrays.toString(nums);
        return "[" + nums.length + " 个元素 " + nums[0] + ",...," + nums[nums.length - 1] + "]";
    }

    private static void run(P209MinimumSizeSubarraySum s, int target, int[] nums, int expected) {
        String label = "target=" + target + " nums=" + fmt(nums);
        try {
            int got = s.minSubArrayLen(target, nums);
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " expected=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}