import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 1: 两数之和 (Two Sum)
 * https://leetcode.cn/problems/two-sum/
 *
 * <p>【难度】Easy
 * <p>【标签】哈希表(Hash Map) · 数组(Array)
 * <p>【题型】「以值找下标」查找表；哈希表最基础的入门套路，几乎所有哈希题的思维起点
 * <p>【考点】能否把「找另一个数」从线性扫描换成 O(1) 查表，以及「边遍历边建表」的先查后存顺序
 * <p>【关联】{@link P560SubarraySumEqualsK} —— 同构进阶：前缀和之差把区间和问题化归成本题
 * <p>{@link P49GroupAnagrams} —— 哈希表的另一类用法：分组
 * <p>{@link P15ThreeSum} —— 三数之和是两数之和的「固定一个再两数之和」扩展（固定 nums[i] 后找 b+c = -nums[i]）
 *
 * <p>给定一个整数数组 nums 和一个目标值 target，
 * 找出数组中和为 target 的两个数，返回它们的下标。
 * 假设每种输入只对应一个答案，且同一个元素不能重复使用。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：双重循环枚举所有数对，O(n²)。
 *    瓶颈不在「枚举 x」，而在「为了配对 x 又要把数组扫一遍」。
 *
 * <p>2. 关键观察：一旦固定了 x，要找的另一个数是**唯一确定**的 —— 就是 target - x（称它「补数」）。
 *    于是问题从「找一对数」退化成「查某个值在不在数组里」，
 *    而查值正是哈希表的强项：O(1)。
 *
 * <p>3. 核心技巧 —— 边遍历边建表，且「先查后存」：
 *    遍历到 x 时，先查补数 target-x 是否已在表中：
 *      - 在  → 它一定来自 x 之前的某个下标，直接返回这两个下标；
 *      - 不在 → 再把 x 自己存进表（值 → 下标），供后面的数来匹配。
 *    「先查后存」这个顺序很关键：它天然保证了不会把 x 自己和自己配成一对
 *    （例如 nums=[3,3], target=6 能正确返回 [0,1]，而不是 [0,0]）。
 *    只需一趟遍历，无需预先建表。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(n)。
 *    注意表里存的是「值 → 下标」而非「下标 → 值」，因为我们要按值来查询。
 *
 * <p>── 无解时的返回契约 ──
 * 题目约束「只会存在一个有效答案」，故该分支理论上不可达，写它只是为了让编译器通过。
 * 本文件沿用 LeetCode 官方题解的写法：
 *     throw new IllegalArgumentException("No two sum solution");
 * 为什么用异常而不是返回哨兵值：
 *   - 抛异常表达的是「输入违反了题目约定」，语义上不是一个正常的查询结果；
 *   - 返回 {0,0} 是错的 —— 它看起来像一个合法下标对，调用方无法区分「真找到了」还是「没找到」；
 *     顺带一提，Java 里 new int[2] 的元素默认值就是 0，即 {0,0}，绝非 {-1,-1}。
 *   - 若确实要返回值而非抛异常，须选一个**落在合法值域之外**的哨兵，如 {-1,-1}（下标不可能为负）。
 */
public class P1TwoSum {

    public int[] twoSum(int[] nums, int target) {
        // 表里存「值 → 下标」，因为 Step 2 要按值来查（不是按下标）
        Map<Integer, Integer> indexByValue = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            // Step 2：固定 nums[i]，要配的另一个数唯一确定 —— 补数 complement
            int complement = target - nums[i];
            // Step 3（先查）：补数已在表中，必来自更早的下标，直接成对返回
            if (indexByValue.containsKey(complement)) {
                return new int[]{indexByValue.get(complement), i};
            }
            // Step 3（后存）：补数不在，再把当前值存入供后面匹配 —— 先查后存保证不拿自己配自己
            indexByValue.put(nums[i], i);
        }
        // 「无解契约」：题目保证有唯一解，走到这里即输入违反约定（详见上方 Javadoc）
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) {
        P1TwoSum p1 = new P1TwoSum();

        // 示例 1
        assertArrayEqual(new int[]{0, 1}, p1.twoSum(new int[]{2, 7, 11, 15}, 9), "Example 1");

        // 示例 2：答案不在开头
        assertArrayEqual(new int[]{1, 2}, p1.twoSum(new int[]{3, 2, 4}, 6), "Example 2");

        // 重复元素：验证「先查后存」，不能把同一个元素用两次
        assertArrayEqual(new int[]{0, 1}, p1.twoSum(new int[]{3, 3}, 6), "Duplicate values");

        // 负数与负目标值
        assertArrayEqual(new int[]{2, 4}, p1.twoSum(new int[]{-1, -2, -3, -4, -5}, -8), "Negative numbers");

        // 含 0
        assertArrayEqual(new int[]{0, 2}, p1.twoSum(new int[]{0, 4, 0}, 0), "Zeros");

        // 以下三个用例违反题目「必有唯一解」的约定，应抛 IllegalArgumentException
        // 无解
        assertThrows(() -> p1.twoSum(new int[]{1, 2}, 100), "No solution");

        // 空数组
        assertThrows(() -> p1.twoSum(new int[]{}, 0), "Empty array");

        // 单元素（不可能成对）
        assertThrows(() -> p1.twoSum(new int[]{5}, 5), "Single element");

        System.out.println("All tests passed!");
    }

    /** 断言调用会抛出 IllegalArgumentException（无解分支的契约）。 */
    private static void assertThrows(Runnable action, String testName) {
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            System.out.println("[PASS] " + testName);
            return;
        }
        throw new AssertionError(String.format(
            "[FAIL] %s: expected IllegalArgumentException, but nothing was thrown", testName));
    }

    private static void assertArrayEqual(int[] expected, int[] actual, String testName) {
        if (!Arrays.equals(expected, actual)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, Arrays.toString(expected), Arrays.toString(actual)));
        }
        System.out.println("[PASS] " + testName);
    }
}
