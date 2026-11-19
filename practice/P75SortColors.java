import java.util.Arrays;

/**
 * LeetCode 75: 颜色分类 (Sort Colors)
 * https://leetcode.cn/problems/sort-colors/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 7/50——三路 partition·荷兰国旗）
 *
 * <p>■ 题目描述
 *   给一个含 **0、1、2** 各若干个的数组（红/白/蓝），**原地**将它们排序成
 *   相同颜色相邻、按 0→1→2 顺序排列。**不要用库排序**（本题的排序是考题本身）。
 *   进阶：**一遍扫描** O(n) + O(1) 空间（计数两遍版不够"进阶"）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ length ≤ 300；nums[i] ∈ {0, 1, 2}）——**原地修改无返回值**
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[2,0,2,1,1,0] → [0,0,1,1,2,2]
 * nums=[2,0,1] → [0,1,2]
 * nums=[0] → [0]
 * nums=[1] → [1]（无 0 无 2）
 * nums=[2,2,2] → [2,2,2]（全 2——mid 每步都换 high）
 * nums=[1,0] → [0,1]
 * nums=[1,2,0] → [0,1,2]（换回来的可能是 2——mid 不前进哨兵）★
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★· Hot100）
 * <p>【标签】数组 · 双指针 · 排序 · 三路快排 partition
 * <p>【题型】三指针一遍扫描（荷兰国旗）：low(0 区右界)/mid(扫描位)/high(2 区左界)
 * <p>【考点】mid 遇 2 换后【不前进】（换来的可能还是 2）；遇 0 换后可前进（low 区里只有已处理的 0/1）；
 * <p>　　　　循环条件 mid ≤ high；区间不变式（[0,low)=0 / [low,mid)=1 / (high,n)=2）
 * <p>【关联】P88 合并有序数组（逆向填充同族）；P215 快选（partition 家族）
 *
 * <p>LCS 签名：sortColors(int[] nums)。
 */
public class P75SortColors {

    /** 你的实现（荷兰国旗三指针：mid 扫描遇 0 双前进/遇 1 单前进/遇 2 换后重审）。 */
    public void sortColors(int[] nums) {
        int l = 0, mid = 0, r = nums.length - 1;
        while (mid <= r) {
            if (nums[mid] == 0) {
                swap(nums, l, mid);
                l++;
                mid++;
            } else if (nums[mid] == 1) {
                mid++;
            } else if (nums[mid] == 2) {
                swap(nums, mid, r);
                r--;
            }
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        P75SortColors solver = new P75SortColors();

        run(solver, new int[]{2, 0, 2, 1, 1, 0}, "官方例1 → [0,0,1,1,2,2]");
        run(solver, new int[]{2, 0, 1}, "官方例2 → [0,1,2]");
        run(solver, new int[]{0}, "单 0 → 不变");
        run(solver, new int[]{1}, "单 1 → 不变");
        run(solver, new int[]{2, 2, 2}, "全 2 → 不变");
        run(solver, new int[]{1, 0}, "两元素 → [0,1]");
        run(solver, new int[]{1, 2, 0}, "换回 2 重审 → [0,1,2] ★ 哨兵");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：计数两遍（数 0/1/2 个数再覆写）——机制与单遍三指针完全不同。 */
    private static int[] bruteForce(int[] nums) {
        int[] out = nums.clone();
        int c0 = 0, c1 = 0, c2 = 0;
        for (int n : out) {
            if (n == 0) c0++;
            else if (n == 1) c1++;
            else c2++;
        }
        int i = 0;
        while (c0-- > 0) out[i++] = 0;
        while (c1-- > 0) out[i++] = 1;
        while (c2-- > 0) out[i++] = 2;
        return out;
    }

    private static void run(P75SortColors solver, int[] nums, String label) {
        try {
            int[] got = nums.clone();
            solver.sortColors(got);
            int[] want = bruteForce(nums);
            boolean ok = Arrays.equals(got, want);

            System.out.printf("[%s] %-26s nums=%-24s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums),
                    Arrays.toString(want), Arrays.toString(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s nums=%-24s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s nums=%-24s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
