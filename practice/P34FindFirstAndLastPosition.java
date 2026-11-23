import java.util.Arrays;

/**
 * LeetCode 34: 在排序数组中查找元素的第一个和最后一个位置
 * (Find First and Last Position of Element in Sorted Array)
 * https://leetcode.cn/problems/find-first-and-last-position-of-element-in-sorted-array/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二分组 3/5——左边界二分心法变奏）
 *
 * <p>■ 题目描述
 *   升序整数数组（**含重复**），找 target 的**第一个位置和最后一个位置**。
 *   不存在返回 [-1, -1]。要求 O(log n)。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（0 ≤ n ≤ 10^5；升序可重复）、int target（0 ≤ x ≤ 10^9）
 *   输出：int[]{first, last}（不存在 → [-1, -1]；注意**空数组**）
 *
 * <p>■ 示例逐条
 * <pre>
 * [5,7,7,8,8,10], 8 → [3,4]（官方例）
 * [5,7,7,8,8,10], 6 → [-1,-1]（缝隙未中）
 * [], 0 → [-1,-1]（空数组 ★）
 * [1], 1 → [0,0]
 * [2,2,2,2], 2 → [0,3]（全重复 ★ 左右边界=两端）
 * [1,3], 1 → [0,0]；[1,3], 4 → [-1,-1]（大于全阵）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·全厂）
 * <p>【标签】数组 · 二分查找
 * <p>【题型】两次二分找左右边界（O(log n)）——"第一个 ≥ target"和"最后一个 ≤ target"
 * <p>【考点】35 心法的变奏：有重复 ⟹ 不能 == 直接 return（要继续收缩找第一个/最后一个）；
 * <p>　　　　收尾校验（退出 l==r+1 后确认 nums[l] 是否真是 target——找到≠存在）；
 * <p>　　　　空数组提前挡（l>r 初态）
 * <p>【关联】P35（插入位=左边界本尊）；P74（判定题不用边界）；P153（旋转数组变奏）
 *
 * <p>LCS 签名：searchRange(int[] nums, int target)。
 */
public class P34FindFirstAndLastPosition {

    /** 你的实现（左边界二分 + 从左边界线性右扫找末位——判定层二分+尾段线性）。 */
    public int[] searchRange(int[] nums, int target) {
        int l = 0, r = nums.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            int value = nums[mid];
            if (target <= value) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        int[] res = new int[]{-1, -1};
        if (nums.length > 0 && nums.length > l && target == nums[l]) {
            int max = l;
            for (int i = l + 1; i < nums.length; i++) {
                if (nums[i] == nums[l]) {
                    max = i;
                }
            }
            res[0] = l;
            res[1] = max;
        }
        return res;
    }

    public static void main(String[] args) {
        P34FindFirstAndLastPosition solver = new P34FindFirstAndLastPosition();

        run(solver, new int[]{5, 7, 7, 8, 8, 10}, 8, "官方例 → [3,4]");
        run(solver, new int[]{5, 7, 7, 8, 8, 10}, 6, "缝隙未中 → [-1,-1]");
        run(solver, new int[]{}, 0, "空数组 → [-1,-1] ★");
        run(solver, new int[]{1}, 1, "单格命中 → [0,0]");
        run(solver, new int[]{2, 2, 2, 2}, 2, "全重复 → [0,3] ★");
        run(solver, new int[]{1, 3}, 1, "两格头 → [0,0]");
        run(solver, new int[]{1, 3}, 4, "大于全阵 → [-1,-1]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：线性扫第一个和最后一个——机制与二分不同。 */
    private static int[] bruteForce(int[] nums, int target) {
        int first = -1, last = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                if (first == -1) first = i;
                last = i;
            }
        }
        return new int[]{first, last};
    }

    private static void run(P34FindFirstAndLastPosition solver, int[] nums, int target, String label) {
        try {
            int[] got = solver.searchRange(nums, target);
            int[] want = bruteForce(nums, target);
            boolean ok = Arrays.equals(got, want);

            System.out.printf("[%s] %-18s target=%-2d | 参考=%s 实得=%s nums=%s%n",
                    ok ? "PASS" : "FAIL", label, target, Arrays.toString(want),
                    Arrays.toString(got), Arrays.toString(nums));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-18s target=%-2d | TODO 未实现%n", label, target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-18s target=%-2d | 异常: %s%n", label, target, e);
        }
    }
}
