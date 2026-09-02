import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LeetCode 15: 三数之和 (3Sum)
 * https://leetcode.cn/problems/3sum/
 *
 * <p>【难度】Medium
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 排序(Sorting)
 * <p>【题型】排序后「固定一端 + 双指针夹逼」求和；三元组在生成时即去重，而非事后用 Set 去重
 * <p>【考点】能否把三数之和化归成「固定一个数后求两数之和」，以及去重的正确写法（排序 + 跳过相邻重复值）
 * <p>【关联】{@link P1TwoSum} —— 同构前置：固定 nums[i] 后，剩下两数之和 = -nums[i]，即 P1 的「补数」思想
 *
 * <p>给定整数数组 nums，找出所有满足 i≠j、i≠k、j≠k 且和为 0 的三元组 [nums[i], nums[j], nums[k]]，
 * 返回**不重复**的三元组列表（三元组之间、组内顺序均不计）。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：三重循环枚举所有三元组，O(n³)；且去重麻烦——
 *    要先把每个三元组排序、再丢进 Set 去重，既慢又丑。
 *
 * <p>2. 关键观察：和为 0 = 固定一个数 a 后，找另两数 b+c = -a。
 *    而「有序数组上找两数之和等于某值」正是双指针的强项（同 {@link P1TwoSum} 的补数思想）：
 *    排序后，left 指向小端、right 指向大端，按和的大小收窄即可，O(n)。
 *    于是先排序，把三数之和降成「外层固定一个 + 内层双指针」。
 *
 * <p>3. 核心技巧 —— 排序 + 双指针 + 生成时去重：
 *    ① 先 Arrays.sort(nums)；
 *    ② 外层固定 nums[i]（i 从 0 到 n-3），内层 left=i+1、right=n-1 夹逼找 b+c = -nums[i]；
 *    ③ 去重（关键，否则结果含重复三元组）：
 *       - 外层：若 nums[i] == nums[i-1] 则 continue，同一固定值只处理一次；
 *       - 内层：命中一组后，left 右移到「与当前不同值」、right 左移到「与当前不同值」，再继续夹逼。
 *    ④ 剪枝（可选）：若 nums[i] > 0 则直接 break，三个正数不可能和为 0。
 *
 * <p>⚠️ 易错点：去重不是「事后用 Set 去重」，而是「生成时就跳过相邻重复值」——
 *    因为已排序，重复值必相邻，比较 nums[i] 与 nums[i-1] 即可。
 *    注意外层去重比的是 i-1（已处理过的），不是 i+1（未处理），
 *    否则会漏解（例如 [-2,-2,0,2]，第二个 -2 若被跳过会漏掉 [-2,0,2]）。
 *
 * <p>4. 复杂度：时间 O(n²)（排序 O(n log n) + 双指针 O(n²)），空间 O(1)（不计输出与排序栈）。
 *    边界：长度 < 3 返回空；全 0 输入应只返回一个 [0,0,0]，而非多个。
 */
public class P15ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P15ThreeSum p15 = new P15ThreeSum();

        // 示例 1
        assertTripletsEqual(new int[][]{{-1, -1, 2}, {-1, 0, 1}},
            p15.threeSum(new int[]{-1, 0, 1, 2, -1, -4}), "Example 1");

        // 示例 2：无解
        assertTripletsEqual(new int[][]{},
            p15.threeSum(new int[]{0, 1, 1}), "Example 2 (no triple)");

        // 示例 3：全零
        assertTripletsEqual(new int[][]{{0, 0, 0}},
            p15.threeSum(new int[]{0, 0, 0}), "Example 3 (all zeros)");

        // 多个零只产生一个三元组（验证去重）
        assertTripletsEqual(new int[][]{{0, 0, 0}},
            p15.threeSum(new int[]{0, 0, 0, 0}), "Multiple zeros dedup");

        // 重复值去重：[-2,0,2] 只出现一次
        assertTripletsEqual(new int[][]{{-2, 0, 2}},
            p15.threeSum(new int[]{-2, 0, 0, 2, 2}), "Dedup pairs");

        // 长度恰好 3 且成立
        assertTripletsEqual(new int[][]{{-1, 0, 1}},
            p15.threeSum(new int[]{-1, 0, 1}), "Length 3 valid");

        // 长度不足 3
        assertTripletsEqual(new int[][]{},
            p15.threeSum(new int[]{1, 2}), "Length < 3");

        // 全正：不可能和为 0
        assertTripletsEqual(new int[][]{},
            p15.threeSum(new int[]{1, 2, 3, 4}), "All positive");

        // 含负含零含正
        assertTripletsEqual(new int[][]{{-2, 0, 2}},
            p15.threeSum(new int[]{-2, 0, 2}), "Neg-zero-pos");

        System.out.println("All tests passed!");
    }

    /** 比较三元组集合（顺序无关）：把双方都规范化为「升序三元组组成的集合」再比。 */
    private static void assertTripletsEqual(int[][] expected, List<List<Integer>> actual, String testName) {
        Set<List<Integer>> expectedSet = new HashSet<>();
        for (int[] t : expected) {
            List<Integer> list = new ArrayList<>();
            for (int x : t) {
                list.add(x);
            }
            expectedSet.add(list);
        }
        Set<List<Integer>> actualSet = new HashSet<>(actual);
        if (!expectedSet.equals(actualSet)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, expectedSet, actualSet));
        }
        System.out.println("[PASS] " + testName);
    }
}
