import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 15: 三数之和 (3Sum)
 * https://leetcode.cn/problems/3sum/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 17/50——对撞套娃·全厂手撕王）
 *
 * <p>■ 题目描述
 *   给整数数组 nums，找出所有**不重复**的三元组 [nums[i], nums[j], nums[k]]
 *   满足 i≠j≠k 且三数之和为 0。**答案中不可含重复三元组**。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（3 ≤ length ≤ 3000；-10^5 ≤ nums[i] ≤ 10^5）
 *   输出：List<List<Integer>>（所有不重复三元组，顺序不限）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[-1,0,1,2,-1,-4] → [[-1,-1,2],[-1,0,1]]（官方例）
 * nums=[0,0,0,0] → [[0,0,0]]（全零——三层去重哨兵）★
 * nums=[0,1,1] → []（无解）
 * nums=[-2,0,1,1,2] → [[-2,0,2],[-2,1,1]]
 * nums=[-4,-2,-2,-2,0,1,2,2,2,3,3,4,4,6,6] → 参考定（重灾区去重）
 * nums=[1,2,-2,-1] → []
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★·字节/美团/阿里——手撕频率之王）
 * <p>【标签】数组 · 双指针（对撞）· 排序
 * <p>【题型】排序 + 固定一位 + 有序两数之和（对撞）：和小 l++、和大 r--、命中后双侧跳重
 * <p>【考点】**三层去重**（i 跳重复 / 命中后 l 跳 / r 跳）；nums[i]>0 剪枝；
 * <p>　　　　对撞单调性（排序保证两数之和的单调方向）；去重边界（l<r 守卫防越界）
 * <p>【关联】P1 两数之和（有序版内核）；P167 两数之和II（对撞原型）；P18 四数之和（套娃加一层）
 *
 * <p>LCS 签名：threeSum(int[] nums)。
 */
public class P15ThreeSum {

    /** 你的实现（排序+固定 i+对撞；i 跳重 + 命中后双侧跳重——三层去重对账）。
     *  注：nums[i]>0 剪枝可选（纯性能，正确性靠去重层）。 */
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int l = i + 1;
            int r = nums.length - 1;
            int target = -nums[i];
            while (l < r) {
                int sum = nums[l] + nums[r];
                if (sum == target) {
                    res.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    while (l < r && nums[l] == nums[l+1]) l++;
                    while (l < r && nums[r] == nums[r-1]) r--;
                    l++;
                    r--;
                } else if (sum < target) {
                    l++;
                } else {
                    r--;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P15ThreeSum solver = new P15ThreeSum();

        run(solver, new int[]{-1, 0, 1, 2, -1, -4}, "官方例 → 2 组");
        run(solver, new int[]{0, 0, 0, 0}, "全零 → [[0,0,0]] ★ 三层去重哨兵");
        run(solver, new int[]{0, 1, 1}, "无解 → []");
        run(solver, new int[]{-2, 0, 1, 1, 2}, "标准 → 2 组");
        run(solver, new int[]{-4, -2, -2, -2, 0, 1, 2, 2, 2, 3, 3, 4, 4, 6, 6}, "重灾区 → 参考定");
        run(solver, new int[]{1, 2, -2, -1}, "无解 → []");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举全部三元组，排序规范后进 TreeSet 去重——O(n³)，机制完全不同。 */
    private static List<List<Integer>> bruteForce(int[] nums) {
        java.util.TreeSet<String> seen = new java.util.TreeSet<>();
        List<List<Integer>> out = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        int[] t = {nums[i], nums[j], nums[k]};
                        Arrays.sort(t);
                        List<Integer> key = Arrays.asList(t[0], t[1], t[2]);
                        if (seen.add(key.toString())) out.add(key);
                    }
                }
            }
        }
        return out;
    }

    private static void run(P15ThreeSum solver, int[] nums, String label) {
        try {
            List<List<Integer>> got = canon(solver.threeSum(Arrays.copyOf(nums, nums.length)));
            List<List<Integer>> want = canon(bruteForce(nums));
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-26s nums=%-46s | 参考=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), fmt(want));
            if (!ok) System.out.printf("       实得=%s%n", fmt(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s nums=%-46s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s nums=%-46s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }

    private static List<List<Integer>> canon(List<List<Integer>> groups) {
        List<List<Integer>> out = new ArrayList<>();
        for (List<Integer> g : groups) {
            List<Integer> c = new ArrayList<>(g);
            java.util.Collections.sort(c);
            out.add(c);
        }
        out.sort((x, y) -> {
            for (int i = 0; i < 3; i++) {
                int cmp = x.get(i) - y.get(i);
                if (cmp != 0) return cmp;
            }
            return 0;
        });
        return out;
    }

    private static String fmt(List<List<Integer>> groups) {
        StringBuilder sb = new StringBuilder();
        for (List<Integer> g : groups) sb.append(g);
        return sb.toString();
    }
}
