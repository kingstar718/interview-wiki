import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LeetCode 15: 三数之和 (3Sum)
 * https://leetcode.cn/problems/3sum/
 *
 * <p>【难度】Medium
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 排序(Sorting) · 去重
 * <p>【题型】排序后「固定一端 + 双指针夹逼」求和；三元组在生成时即去重，而非事后用 Set 去重
 * <p>【考点】把三数之和化归成「固定一个数后求两数之和」；三处去重的正确写法与时机
 * <p>【关联】P1TwoSum（内层就是有序版两数之和）；P18FourSum（再套一层）
 *
 * <p>给你一个整数数组 nums，判断是否存在三元组 [nums[i], nums[j], nums[k]] 满足
 * i != j、i != k 且 j != k，同时还满足 nums[i] + nums[j] + nums[k] == 0。
 * 请你返回所有和为 0 且不重复的三元组。答案中不可以包含重复的三元组。
 *
 * <p>约束：3 &lt;= nums.length &lt;= 3000；-10^5 &lt;= nums[i] &lt;= 10^5。
 *
 * <p>── 思路要点（自己先想，卡住再看）──
 * <p>1. 固定一个数 a 后，问题变成「在剩余部分找两数之和 = -a」——有序数组上这是双指针的活。
 * <p>2. 排序是核心预处理：既让双指针有单调性，又让重复值必然相邻（去重才好写）。
 * <p>3. 去重有三处：外层 i、命中后 l、命中后 r。想想每一处该和「前一个」还是「后一个」比。
 * <p>4. 剪枝：排序后 nums[i] &gt; 0 就可以停了（三个正数凑不出 0）。
 *
 * <p>⚠️ 常见错误：命中后只移动一侧指针（会死循环/重复解）；外层去重写反方向（会漏解）；
 *    剪枝写成 nums[i] &gt;= 0（漏掉 [0,0,0]）。
 */
public class P15ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        // 先排序
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (nums[i] > 0) {
                break;
            }
            if (i > 0 && nums[i] == nums[i-1]) {
                continue;
            }
            int l = i + 1, r = nums.length - 1;
            int target = -nums[i];
            while (l < r) {
                int sum = nums[l] + nums[r];
                if (sum < target) {
                    l++;
                } else if (sum > target) {
                    r--;
                } else {
                    result.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l+1]) {
                        l++;
                    }
                    while (l < r && nums[r] == nums[r-1]) {
                        r--;
                    }
                    l++;
                    r--;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        P15ThreeSum p15 = new P15ThreeSum();

        run(p15, new int[]{-1, 0, 1, 2, -1, -4}, new int[][]{{-1, -1, 2}, {-1, 0, 1}}, "示例1");
        run(p15, new int[]{0, 1, 1}, new int[][]{}, "示例2：无解");
        run(p15, new int[]{0, 0, 0}, new int[][]{{0, 0, 0}}, "示例3：全零");
        run(p15, new int[]{0, 0, 0, 0}, new int[][]{{0, 0, 0}}, "多个零只输出一个三元组");
        run(p15, new int[]{-2, 0, 0, 2, 2}, new int[][]{{-2, 0, 2}}, "重复值去重");
        run(p15, new int[]{-1, 0, 1}, new int[][]{{-1, 0, 1}}, "长度恰好 3");
        run(p15, new int[]{1, 2}, new int[][]{}, "长度不足 3");
        run(p15, new int[]{1, 2, 3, 4}, new int[][]{}, "全正：不可能和为 0");
        run(p15, new int[]{-2, 0, 2}, new int[][]{{-2, 0, 2}}, "含负含零含正");
    }

    private static void run(P15ThreeSum solver, int[] nums, int[][] expected, String name) {
        String label = name + " nums=" + java.util.Arrays.toString(nums);
        try {
            List<List<Integer>> got = solver.threeSum(nums);
            Set<List<Integer>> expSet = new HashSet<>();
            for (int[] t : expected) {
                List<Integer> list = new ArrayList<>();
                for (int x : t) list.add(x);
                expSet.add(list);
            }
            // 校验结果：每组和必须为 0，且集合与期望一致（顺序无关）
            boolean sumOk = true;
            for (List<Integer> t : got) {
                int sum = 0;
                for (int x : t) sum += x;
                if (sum != 0 || t.size() != 3) sumOk = false;
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