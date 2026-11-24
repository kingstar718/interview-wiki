import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * LeetCode 215: 数组中的第 K 个最大元素 (Kth Largest Element in an Array)
 * https://leetcode.cn/problems/kth-largest-element-in-an-array/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·栈与堆组 3/3 压轴——堆/快速选择）
 *
 * <p>■ 题目描述
 *   给整数数组 nums 和整数 k，返回第 k 个**最大**元素。
 *   注意是"第 k 大"不是"第 k 个不同"——重复值各算一次。
 *   进阶：不排序，O(n) 期望时间（快速选择）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ n ≤ 10^5）、int k（1 ≤ k ≤ n）
 *   输出：int
 *
 * <p>■ 示例逐条
 * <pre>
 * [3,2,1,5,6,4], k=2 → 5
 * [3,2,3,1,2,4,5,5,6], k=4 → 4（重复值示例：排好序 1,2,2,3,3,4,5,5,6 → 第4大=4）
 * [1], k=1 → 1
 * [2,1], k=2 → 1
 * [7,6,5,4,3,2,1], k=5 → 3（逆序最坏——快选无随机时退化为 O(n²) 风险区）
 * [3,3,3,3], k=2 → 3（全等）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★·全厂）
 * <p>【标签】数组 · 分治 · 堆 · 快速选择
 * <p>【题型】两大解法：
 * <p>　　① 大小为 k 的最小堆：offer 入堆、超 k 弹顶——遍历完堆顶即第 k 大（O(n log k)，稳定）
 * <p>　　② 快速选择：partition 定位 pivot 终位，按 rank 只递归一边（期望 O(n)，最坏 O(n²)）
 * <p>【考点】"第 k 大"的降维（排序下标 n-k / partition 里 rank 的换算）；
 * <p>　　　　快选只走一边（和快排全递归的区别）；随机 pivot 防退化（逆序哨兵）
 * <p>【关联】P912 排序数组（快排全量版）；P347 前 K 个高频（堆的同族应用）；P703 数据流第 K 大
 *
 * <p>LCS 签名：findKthLargest(int[] nums, int k)。
 */
public class P215KthLargestElementInArray {

    /** 你的实现（快速选择：随机pivot+Lomuto划分，等号归右；只递归目标侧）。 */
    public int findKthLargest(int[] nums, int k) {
        return quickSelect(nums, 0, nums.length - 1, nums.length - k);
    }

    private int quickSelect(int[] nums, int l, int r, int target) {
        int p = randPartition(nums, l , r);
        if (p == target) return nums[p];
        if (p < target) {
            return quickSelect(nums, p + 1, r, target);
        } else {
            return quickSelect(nums, l, p - 1, target);
        }
    }

    private int randPartition(int[] nums, int l, int r) {
        int p = l + new Random().nextInt(r - l + 1);
        swap(nums, p, r);
        int pivot = nums[r];
        int i = l;
        for (int j = l; j < r; j++) {
            if (nums[j] < pivot) {
                swap(nums, i, j);
                i++;
            }
        }
        swap(nums, i, r);
        return i;
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i];
        nums[i] = nums[j];
        nums[j] = t;
    }

    public static void main(String[] args) {
        P215KthLargestElementInArray solver = new P215KthLargestElementInArray();

        run(solver, new int[]{3, 2, 1, 5, 6, 4}, 2, "官方例 → 5");
        run(solver, new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4, "官方例2（重复值）→ 4");
        run(solver, new int[]{1}, 1, "单元素 → 1");
        run(solver, new int[]{2, 1}, 2, "两元素取次大 → 1");
        run(solver, new int[]{7, 6, 5, 4, 3, 2, 1}, 5, "逆序最坏 → 3 ★");
        run(solver, new int[]{3, 3, 3, 3}, 2, "全等 → 3");
        run(solver, new int[]{-1, -5, -3}, 1, "全负数取最大 → -1");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：排序取 n-k 下标——机制与堆/快选不同。 */
    private static int bruteForce(int[] nums, int k) {
        int[] c = nums.clone();
        Arrays.sort(c);
        return c[c.length - k];
    }

    private static void run(P215KthLargestElementInArray solver, int[] nums, int k, String label) {
        try {
            int want = bruteForce(nums, k);   // 参考先算（快选会打乱原数组）
            int got = solver.findKthLargest(nums, k);
            boolean ok = got == want;

            System.out.printf("[%s] %-22s k=%d | 参考=%d 实得=%d nums=%s%n",
                    ok ? "PASS" : "FAIL", label, k, want, got, Arrays.toString(nums));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-22s k=%d | TODO 未实现%n", label, k);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-22s k=%d | 异常: %s%n", label, k, e);
        }
    }
}
