import java.util.Arrays;

/**
 * LeetCode 33: 搜索旋转排序数组 (Search in Rotated Sorted Array)
 * https://leetcode.cn/problems/search-in-rotated-sorted-array/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二分组 5/5 决战——一次二分+有序半区判定）
 *
 * <p>■ 题目描述
 *   升序数组在某个未知下标处旋转（如 [0,1,2,4,5,6,7] → [4,5,6,7,0,1,2]）。
 *   找 target，找到返回**下标**，否则返回 -1。要求 **O(log n)**。
 *   元素**互异**（无重复；81 有重复是进阶）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ n ≤ 5000；互异；原升序旋转而来）、int target
 *   输出：int（下标或 -1）
 *
 * <p>■ 示例逐条
 * <pre>
 * [4,5,6,7,0,1,2], 0 → 4（官方例）
 * [4,5,6,7,0,1,2], 3 → -1（不存在）
 * [1], 0 → -1；[1], 1 → 0
 * [5,1,3], 5 → 0（断崖贴近头部）
 * [3,1], 1 → 1（两元素 ★ 边界重灾区）
 * [1,3], 3 → 1（未旋转）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★·字节/阿里/美团——二分组最硬一题）
 * <p>【标签】数组 · 二分查找
 * <p>【题型】**一次二分 + 有序半区判定**：任何 mid 处至少一半是有序的；
 * <p>　　　　判明哪半有序 → 查 target 是否落在该半区的值域内 → 在则进该半、否则进另半
 * <p>【考点】**判有序用 nums[l] <= nums[mid]**（等号必须带——l==mid 场景，两元素数组专杀）；
 * <p>　　　　值域判定的开闭端（target 与 mid 本身相等已在第一步 return 掉）；
 * <p>　　　　l<=r 压空流（判定+找边界族——mid 只是路标，全程踢）
 * <p>【关联】P153（同图热身：断崖+右端点比较）；P81（有重复进阶）；P34/74（压空流同门）
 *
 * <p>LCS 签名：search(int[] nums, int target)。
 */
public class P33SearchInRotatedSortedArray {

    /** 你的实现（一次二分+有序半区判定：判哪半干净升序，target 落其值域则进，否则进另半）。 */
    public int search(int[] nums, int target) {
        int l = 0;
        int r = nums.length - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            int value = nums[mid];
            if (target == value) {
                return mid;
            }
            if (nums[l] <= nums[mid]) {
                // l...mid 干净升序
                if (nums[l] <= target && target < nums[mid]) {
                    r = mid -1;
                } else {
                    l = mid + 1;
                }
            } else {
                // mid...r 升序
                if (nums[mid] < target && target <= nums[r]) {
                    l = mid + 1;
                } else {
                    r = mid - 1;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        P33SearchInRotatedSortedArray solver = new P33SearchInRotatedSortedArray();

        run(solver, new int[]{4, 5, 6, 7, 0, 1, 2}, 0, "官方例 → 4");
        run(solver, new int[]{4, 5, 6, 7, 0, 1, 2}, 3, "不存在 → -1");
        run(solver, new int[]{4, 5, 6, 7, 0, 1, 2}, 4, "打头元素 → 0 ★");
        run(solver, new int[]{1}, 0, "单元素未中 → -1");
        run(solver, new int[]{1}, 1, "单元素命中 → 0");
        run(solver, new int[]{5, 1, 3}, 5, "断崖贴头 → 0");
        run(solver, new int[]{3, 1}, 1, "两元素 ★ → 1");
        run(solver, new int[]{1, 3}, 3, "未旋转 → 1");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：线性扫——机制与二分完全不同。 */
    private static int bruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) return i;
        }
        return -1;
    }

    private static void run(P33SearchInRotatedSortedArray solver, int[] nums, int target, String label) {
        try {
            int got = solver.search(nums, target);
            int want = bruteForce(nums, target);
            boolean ok = got == want;

            System.out.printf("[%s] %-14s target=%-2d | 参考=%d 实得=%d nums=%s%n",
                    ok ? "PASS" : "FAIL", label, target, want, got, Arrays.toString(nums));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-14s target=%-2d | TODO 未实现%n", label, target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-14s target=%-2d | 异常: %s%n", label, target, e);
        }
    }
}
