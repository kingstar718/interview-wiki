import java.util.Arrays;

/**
 * LeetCode 153: 寻找旋转排序数组中的最小值 (Find Minimum in Rotated Sorted Array)
 * https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二分组 4/5——旋转数组二分·33 热身）
 *
 * <p>■ 题目描述
 *   长为 n 的升序数组**在某个未知下标处旋转**（如 [0,1,2,4,5,6,7] 旋转 →
 *   [4,5,6,7,0,1,2]）。找其中的**最小值**。要求 O(log n)。
 *   **元素互异**（无重复——153 的前提；154 有重复是进阶）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ n ≤ 5000；互异；原升序旋转而来）
 *   输出：int（最小值本身）
 *
 * <p>■ 示例逐条
 * <pre>
 * [3,4,5,1,2] → 1（官方例：最小值在旋转点）
 * [4,5,6,7,0,1,2] → 0（官方例2）
 * [11,13,15,17] → 11（没旋转/旋转点=0 → 最小值就在头上 ★）
 * [1] → 1
 * [2,1] → 1
 * [5,1,2,3,4] → 1（旋转点在 index 1）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·字节）
 * <p>【标签】数组 · 二分查找
 * <p>【题型】**与右端点比较的二分**：mid > r → 最小值在右半（l=mid+1）；mid < r → 在左半含 mid（r=mid）
 * <p>【考点】**为什么和 nums[r] 比**（右端点=已知边界：比它小 ⟹ 落在旋转点后的短段）；
 * <p>　　　　l==r 收敛语义（153 是"收缩到单点"流——l<r 循环的正当用例，与 34 的 l<=r 流对照）；
 * <p>　　　　未旋转的数组也算"旋转"（最小值在头部，mid<r 分支自动处理）
 * <p>【关联】P33 搜索旋转排序数组（★★★★★ 决战）；P154（有重复进阶）；P34/74（l<=r 流）
 *
 * <p>LCS 签名：findMin(int[] nums)。
 */
public class P153FindMinimumInRotatedSortedArray {

    /** 你的实现（与右端点比较的二分——纯收缩流 l<r + r=mid，退出 l==r 即答案）。 */
    public int findMin(int[] nums) {
        int l = 0, r = nums.length - 1;
        while (l < r) {
            int mid = l + (r - l) / 2;
            if (nums[r] > nums[mid]) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        return nums[l];
    }

    public static void main(String[] args) {
        P153FindMinimumInRotatedSortedArray solver = new P153FindMinimumInRotatedSortedArray();

        run(solver, new int[]{3, 4, 5, 1, 2}, "官方例 → 1");
        run(solver, new int[]{4, 5, 6, 7, 0, 1, 2}, "官方例2 → 0");
        run(solver, new int[]{11, 13, 15, 17}, "未旋转 → 11 ★ 最小值在头");
        run(solver, new int[]{1}, "单元素 → 1");
        run(solver, new int[]{2, 1}, "两元素 → 1");
        run(solver, new int[]{5, 1, 2, 3, 4}, "旋转点=1 → 1");
        run(solver, new int[]{2, 3, 4, 5, 1}, "旋转点=末段 → 1");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：线性扫最小值——机制与二分不同。 */
    private static int bruteForce(int[] nums) {
        int min = nums[0];
        for (int v : nums) {
            if (v < min) min = v;
        }
        return min;
    }

    private static void run(P153FindMinimumInRotatedSortedArray solver, int[] nums, String label) {
        try {
            int got = solver.findMin(nums);
            int want = bruteForce(nums);
            boolean ok = got == want;

            System.out.printf("[%s] %-18s | 参考=%d 实得=%d nums=%s%n",
                    ok ? "PASS" : "FAIL", label, want, got, Arrays.toString(nums));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-18s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-18s | 异常: %s%n", label, e);
        }
    }
}
