import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 560: 和为 K 的子数组 (Subarray Sum Equals K)
 * https://leetcode.cn/problems/subarray-sum-equals-k/
 *
 * <p>【难度】Medium
 * <p>【标签】哈希表(Hash Map) · 前缀和(Prefix Sum) · 数组(Array)
 * <p>【题型】**前缀和 + 哈希计数**；「区间和」问题转「两点之差」的经典范式
 * <p>【考点】能否把区间和改写成前缀和之差，从而复用两数之和的查表思路；能否想清为何必须预置 prefixCount[0]=1
 * <p>【关联】{@link P1TwoSum} —— 本题的思维原型，建议先掌握它再做本题
 * <p>{@link P3LongestSubstringWithoutRepeating} —— 对比：本题含负数故不能用滑动窗口
 *
 * <p>给定整数数组 nums 和整数 k，统计并返回该数组中**和为 k 的连续子数组的个数**。
 * 注意：数组可能含负数，因此不能用滑动窗口。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：枚举左右端点求和，O(n³)；用前缀和优化求和步骤后是 O(n²)。
 *    瓶颈在于：固定右端点后，仍要向左线性扫描去找所有合法的左端点。
 *
 * <p>2. 关键观察：设 prefix[i] 为前 i 个元素之和，则子数组 (j, i] 的和 = prefix[i] - prefix[j]。
 *    要求它等于 k，即   prefix[i] - prefix[j] == k
 *                 →   prefix[j] == prefix[i] - k
 *    固定右端点 i 后，需要找的左端点前缀和是**唯一确定的值**！
 *    这和「两数之和」找补数是同一个结构 —— 于是又可以用哈希表 O(1) 查询。
 *
 * <p>3. 核心技巧 —— 哈希表存「前缀和 → 出现次数」，边遍历边查边存：
 *    因为问的是**个数**而非下标，同一个前缀和可能出现多次，所以存的是次数而不是下标。
 *    遍历到 i 时：先累加 prefixSum，再把 prefixCount[prefixSum - k] 计入答案，最后把
 *    prefixSum 自己的次数 +1（依旧是「先查后存」，保证 j < i，子数组非空）。
 *
 * <p>⚠️ 易错点：必须预置 prefixCount.put(0, 1)。
 *    它表示「空前缀」，用于处理「从下标 0 开始的子数组」——
 *    例如 nums=[3], k=3 时 prefixSum=3，需要查 prefixCount[0]，若没预置就会漏掉这个答案。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(n)。
 *    数组含负数照样成立（这正是不能用滑动窗口、必须用前缀和的原因）；
 *    元素为 0 或重复前缀和的情形由「计数」自然处理。
 */
public class P560SubarraySumEqualsK {

    public int subarraySum(int[] nums, int k) {
        // TODO: 在此实现（返回和为 k 的连续子数组个数）
        //       实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释

        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P560SubarraySumEqualsK p560 = new P560SubarraySumEqualsK();

        // 示例 1：[1,1] 与 [1,1]
        assertEqual(2, p560.subarraySum(new int[]{1, 1, 1}, 2), "Example 1");

        // 示例 2：[1,2] 与 [3]
        assertEqual(2, p560.subarraySum(new int[]{1, 2, 3}, 3), "Example 2");

        // 从下标 0 开始的子数组：验证 prefixCount[0]=1 的预置
        assertEqual(1, p560.subarraySum(new int[]{3}, 3), "Starts at index 0");

        // 含负数：[1,-1]、[1,-1,0]、[0] 三个
        assertEqual(3, p560.subarraySum(new int[]{1, -1, 0}, 0), "With negative numbers");

        // 全零求和为 0：C(3,1)+C(3,2)... 共 6 个连续子数组
        assertEqual(6, p560.subarraySum(new int[]{0, 0, 0}, 0), "All zeros");

        // 无解
        assertEqual(0, p560.subarraySum(new int[]{1, 2, 3}, 100), "No solution");

        // 空数组
        assertEqual(0, p560.subarraySum(new int[]{}, 0), "Empty array");

        // 单元素不匹配
        assertEqual(0, p560.subarraySum(new int[]{1}, 0), "Single element, no match");

        // 负目标值
        assertEqual(1, p560.subarraySum(new int[]{-1, -1, 1}, -2), "Negative k");

        // 整个数组恰好是唯一答案
        assertEqual(1, p560.subarraySum(new int[]{1, 2, 3, 4}, 10), "Whole array");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(
                String.format("[FAIL] %s: expected %d, but got %d", testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
