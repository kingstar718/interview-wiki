import java.util.Arrays;

/**
 * LeetCode 283: 移动零 (Move Zeroes)
 * https://leetcode.cn/problems/move-zeroes/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 15/50——快慢指针热身）
 *
 * <p>■ 题目描述
 *   给数组 nums，把所有 **0 移动到末尾**，同时**保持非零元素的相对顺序**。
 *   必须**原地**操作（不复制数组），尽量减少操作次数。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ length ≤ 10^4；-2^31 ≤ nums[i] ≤ 2^31-1——**负数也是非零**）
 *   输出：无（原地修改）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[0,1,0,3,12] → [1,3,12,0,0]
 * nums=[0] → [0]
 * nums=[1,2,3] → [1,2,3]（无零不动）
 * nums=[0,0,0] → [0,0,0]（全零不动）
 * nums=[-1,0,2] → [-1,2,0]（负数非零哨兵）
 * nums=[0,0,1] → [1,0,0]
 * nums=[4,0,5,0,6] → [4,5,6,0,0]
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★·美团/快手）
 * <p>【标签】数组 · 双指针（快慢）
 * <p>【题型】快慢指针原地重排：slow=下一个非零应放位，fast 扫描，非零就放过来；尾部补 0
 * <p>【考点】**稳定序**（非零元素相对顺序不变——不能用两分区交换）；两流派：
 * <p>　　　　补零版（两趟：先搬非零再补 0）vs 交换版（一趟：非零与 slow 交换）；
 * <p>　　　　slow 语义 = "已处理前缀的边界"（[0,slow) 全非零且保序）
 * <p>【关联】P27 移除元素（同模板换判据）；P26 删除有序重复项（同族）；P75（三指针重排——进阶）
 *
 * <p>LCS 签名：moveZeroes(int[] nums)。
 */
public class P283MoveZeroes {

    /** 你的实现（交换版一趟：非零与 slow 交换——slow==fast 自换无害；需 swap 助手）。 */
    public void moveZeroes(int[] nums) {
        int slow = 0, fast = 0;
        while (fast < nums.length) {
            if (nums[fast] != 0) {
                swap(nums, slow, fast);
                slow++;
            }
            fast++;
        }
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i];
        nums[i] = nums[j];
        nums[j] = t;
    }

    public static void main(String[] args) {
        P283MoveZeroes solver = new P283MoveZeroes();

        run(solver, new int[]{0, 1, 0, 3, 12}, "官方例 → [1,3,12,0,0]");
        run(solver, new int[]{0}, "单零 → [0]");
        run(solver, new int[]{1, 2, 3}, "无零 → 不变");
        run(solver, new int[]{0, 0, 0}, "全零 → 不变");
        run(solver, new int[]{-1, 0, 2}, "负数非零 → [-1,2,0] ★ 哨兵");
        run(solver, new int[]{0, 0, 1}, "零在前 → [1,0,0]");
        run(solver, new int[]{4, 0, 5, 0, 6}, "散布 → [4,5,6,0,0]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：收集非零到新数组再按原序回填+补零——机制与原地指针完全不同。 */
    private static int[] bruteForce(int[] nums) {
        int[] out = new int[nums.length];
        int k = 0;
        for (int n : nums) {
            if (n != 0) out[k++] = n;
        }
        return out;   // 其余位默认 0 = 补零
    }

    private static void run(P283MoveZeroes solver, int[] nums, String label) {
        try {
            int[] got = nums.clone();
            solver.moveZeroes(got);
            int[] want = bruteForce(nums);
            boolean ok = Arrays.equals(got, want);

            System.out.printf("[%s] %-28s nums=%-24s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums),
                    Arrays.toString(want), Arrays.toString(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s nums=%-24s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s nums=%-24s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
