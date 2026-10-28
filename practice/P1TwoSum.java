import java.util.Arrays;
import java.util.HashMap;

/**
 * LeetCode 1: 两数之和 (Two Sum)
 * https://leetcode.cn/problems/two-sum/
 *
 * <p>【难度】Easy
 * <p>【标签】哈希表(Hash Map) · 数组(Array) · 查找表
 * <p>【题型】「以值找下标」查找表；哈希表最基础的入门套路，几乎所有哈希题的思维起点
 * <p>【考点】能否把「找另一个数」从线性扫描换成 O(1) 查表；「先查后存」的顺序
 * <p>【关联】P15ThreeSum（固定一个再两数之和）；P560SubarraySumEqualsK（前缀和之差化归成本题）
 *
 * <p>给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值的那两个整数，
 * 并返回它们的数组下标。你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。
 * 你可以按任意顺序返回答案。
 *
 * <p>约束：2 &lt;= nums.length &lt;= 10^4；-10^9 &lt;= nums[i], target &lt;= 10^9。
 *
 * <p>── 无解分支（题面未规定）──
 * 题面提示明确「只会存在一个有效答案」，**没有规定无解时返回什么**，因为无解输入不存在。
 * 循环结束后的 return 只是为了让编译器通过：返回 new int[0] / null / 抛异常都不影响判题。
 * 唯一要避免的是返回 {0,0} —— 看起来像合法下标对，调用方无法区分「找到了」和「没找到」。
 */
public class P1TwoSum {

    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int r = target - nums[i];
            if (hashMap.containsKey(r)) {
                return new int[]{hashMap.get(r), i};
            } else {
                hashMap.put(nums[i], i);
            }
        }
        return new int[0];
    }

    public static void main(String[] args) {
        P1TwoSum p1 = new P1TwoSum();

        run(p1, new int[]{2, 7, 11, 15}, 9, new int[]{0, 1}, "示例1");
        run(p1, new int[]{3, 2, 4}, 6, new int[]{1, 2}, "示例2：答案不在开头");
        run(p1, new int[]{3, 3}, 6, new int[]{0, 1}, "重复元素：不能把同一个元素用两次");
        run(p1, new int[]{-1, -2, -3, -4, -5}, -8, new int[]{2, 4}, "负数与负目标");
        run(p1, new int[]{0, 4, 0}, 0, new int[]{0, 2}, "含 0");
        run(p1, new int[]{1000000000, -1000000000}, 0, new int[]{0, 1}, "边界：最大与最小");
    }

    private static void run(P1TwoSum solver, int[] nums, int target, int[] expected, String name) {
        String label = "nums=" + Arrays.toString(nums) + " target=" + target;
        try {
            int[] got = solver.twoSum(nums, target);
            if (got == null || got.length != 2) {
                System.out.println("[FAIL] " + name + " | " + label + " | 返回值非法: "
                        + (got == null ? "null" : Arrays.toString(got)));
                return;
            }
            int[] gotSorted = got.clone();
            Arrays.sort(gotSorted);
            int[] expSorted = expected.clone();
            Arrays.sort(expSorted);
            System.out.println((Arrays.equals(gotSorted, expSorted) ? "[PASS] " : "[FAIL] ") + name + " | " + label
                    + " | got=" + Arrays.toString(got) + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + name + " | " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + name + " | " + label + " | 抛异常: " + e);
        }
    }
}