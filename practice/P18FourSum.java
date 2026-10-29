import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LeetCode 18: 四数之和 (4Sum)
 * https://leetcode.cn/problems/4sum/
 *
 * <p>【难度】Medium
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 排序(Sorting) · 去重
 * <p>【题型】三数之和外面再套一层：排序 + 两层固定 + 双指针；每层都要去重
 * <p>【考点】四层去重（i/j/left/right）；剪枝用 break 还是 continue；用 long 防 int 溢出
 * <p>【关联】P15ThreeSum（直接基础）；P1TwoSum（哈希版对比）
 *
 * <p>给你一个由 n 个整数组成的数组 nums，和一个目标值 target。
 * 请你找出并返回满足下述全部条件且不重复的四元组 [nums[a], nums[b], nums[c], nums[d]]
 * （若两个四元组元素一一对应，则认为两个四元组重复）：
 * 0 &lt;= a, b, c, d &lt; n；a、b、c、d 互不相同；nums[a] + nums[b] + nums[c] + nums[d] == target。
 * 可以按任意顺序返回答案。
 *
 * <p>约束：1 &lt;= nums.length &lt;= 200；-10^9 &lt;= nums[i] &lt;= 10^9；-10^9 &lt;= target &lt;= 10^9。
 *
 * <p>── 思路要点 ──
 * <p>1. 排序后固定 i、j 两层，剩下 [j+1, n-1] 区间内双指针找两数，目标和 = target - nums[i] - nums[j]。
 * <p>2. 去重有四处：i、j、命中后的 left、right。想想每处和"前一个"还是"后一个"比。
 * <p>3. 剪枝：四数之和可能超 int —— 用 long 计算；且剪枝条件里"最小四数之和 &gt; target 才 break"
 *    要注意 target 可能是负数，不能像 15 那样只看 nums[i] &gt; 0。
 */
public class P18FourSum {

    public List<List<Integer>> fourSum(int[] nums, int target) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < nums.length - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            for (int j = i + 1; j < nums.length - 2; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) {
                    continue;
                }
                int l = j + 1, r = nums.length - 1;
                while (l < r) {
                    long sum = (long) nums[i] + nums[j] + nums[l] + nums[r];
                    if (sum < target) {
                        l++;
                    } else if (sum > target) {
                        r--;
                    } else {
                        res.add(new ArrayList<>(Arrays.asList(nums[i], nums[j], nums[l], nums[r])));
                        while (l < r && nums[l] == nums[l + 1]) {
                            l++;
                        }
                        while (l < r && nums[r] == nums[r - 1]) {
                            r--;
                        }
                        l++;
                        r--;
                    }
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P18FourSum solver = new P18FourSum();

        run(solver, new int[]{1, 0, -1, 0, -2, 2}, 0,
                new int[][]{{-2, -1, 1, 2}, {-2, 0, 0, 2}, {-1, 0, 0, 1}}, "示例1");
        run(solver, new int[]{2, 2, 2, 2, 2}, 8, new int[][]{{2, 2, 2, 2}}, "重复元素去重");
        run(solver, new int[]{1, 2, 3}, 0, new int[][]{}, "长度不足 4");
        run(solver, new int[]{0, 0, 0, 0}, 0, new int[][]{{0, 0, 0, 0}}, "全零");
        run(solver, new int[]{1, 2, 3, 4}, 10, new int[][]{{1, 2, 3, 4}}, "长度恰好 4");
        run(solver, new int[]{-3, -1, 0, 2, 4, 5}, 2, new int[][]{{-3, -1, 2, 4}}, "含负数");
        // 溢出陷阱：4 * 10^9 用 int 算会溢出成 -294967296，恰好等于 target → 误判！
        run(solver, new int[]{1000000000, 1000000000, 1000000000, 1000000000}, -294967296,
                new int[][]{}, "int 溢出陷阱：必须用 long");
        run(solver, new int[]{1000000000, 1000000000, 1000000000, 1000000000}, 0,
                new int[][]{}, "大数无解");
    }

    private static void run(P18FourSum solver, int[] nums, int target, int[][] expected, String name) {
        String label = name + " nums=" + Arrays.toString(nums) + " target=" + target;
        try {
            List<List<Integer>> got = solver.fourSum(nums, target);
            Set<List<Integer>> expSet = new HashSet<>();
            for (int[] t : expected) {
                List<Integer> list = new ArrayList<>();
                for (int x : t) list.add(x);
                expSet.add(list);
            }
            // 校验：每组必须是 4 个数且和等于 target
            boolean sumOk = true;
            long targetL = target;
            for (List<Integer> t : got) {
                long sum = 0;
                for (int x : t) sum += x;
                if (sum != targetL || t.size() != 4) sumOk = false;
            }
            Set<List<Integer>> gotSet = new HashSet<>(got);
            boolean pass = sumOk && expSet.equals(gotSet);
            System.out.println((pass ? "[PASS] " : "[FAIL] ") + label + " | got=" + got + " expected=" + expSet);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}