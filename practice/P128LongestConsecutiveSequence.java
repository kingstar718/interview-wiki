import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 128: 最长连续序列 (Longest Consecutive Sequence)
 * https://leetcode.cn/problems/longest-consecutive-sequence/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 2/50——哈希换排序思维样本）
 *
 * <p>■ 题目描述
 *   给一个**未排序**的整数数组 nums，找出**数字连续**（值连续，与数组位置无关）
 *   的最长序列长度。如 [1,3,2] 里 1,2,3 连续 → 长度 3。
 *   要求 **O(n) 时间**（禁止排序解法的 O(n log n)——面试官的真正考点）。
 *
 * <p>■ 输入输出
 *   输入：int[] nums（0 ≤ length ≤ 10^5；-10^9 ≤ nums[i] ≤ 10^9，**可负可重复**）
 *   输出：int（最长连续序列长度；空数组 → 0）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[100,4,200,1,3,2] → 4（1,2,3,4）
 * nums=[0,3,7,2,5,8,4,6,0,1] → 9（0~8，含重复 0——Set 去重后不虚增）
 * nums=[] → 0（空数组）
 * nums=[5] → 1（单元素）
 * nums=[1,2,0,1] → 3（0,1,2 + 重复 1）
 * nums=[9,1,4,7,3,-1,0,5,8,-1,6] → 7（-1,0,1 和 3~9 两段取长）
 * nums=[0,-1] → 2（负数跨界）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★· Hot100）
 * <p>【标签】哈希表 · 并查集 · 数组
 * <p>【题型】O(n) 判"值连续段最长"：Set 全存 + 只从序列头（x-1 不在集合）向右扩
 * <p>【考点】"序列头判据"（x-1 不在 Set）；去重防虚增；每数被扫两遍 → O(n) 论证；
 * <p>　　　　**空数组返回 0**（int[] 空与 set 空的关系）
 * <p>【关联】P49（分组哈希首题——key 设计同族）；P1 TwoSum（Set 查表原型）
 *
 * <p>LCS 签名：longestConsecutive(int[] nums)。
 */
public class P128LongestConsecutiveSequence {

    /** 你的实现（Set 序列头扩展：x-1 不在=头 → 向右数到断；非头跳过保 O(n)）。 */
    public int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) {
            set.add(n);
        }
        int best = 0;
        for (int n : set) {
            if (set.contains(n - 1)) {
                continue;
            }
            int len = 1;
            while (set.contains(n + len)) {
                len++;
            }
            best = Math.max(len, best);
        }
        return best;
    }

    public static void main(String[] args) {
        P128LongestConsecutiveSequence solver = new P128LongestConsecutiveSequence();

        run(solver, new int[]{100, 4, 200, 1, 3, 2}, "官方例1 → 4");
        run(solver, new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}, "官方例2 含重复 → 9");
        run(solver, new int[]{}, "空数组 → 0");
        run(solver, new int[]{5}, "单元素 → 1");
        run(solver, new int[]{1, 2, 0, 1}, "重复干扰 → 3");
        run(solver, new int[]{9, 1, 4, 7, 3, -1, 0, 5, 8, -1, 6}, "两段取长 → 7");
        run(solver, new int[]{0, -1}, "负数跨界 → 2");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：排序去重后线性扫段——O(n log n)，机制与"Set 序列头扩展"完全不同。 */
    private static int bruteForce(int[] nums) {
        if (nums.length == 0) return 0;
        int[] sorted = nums.clone();
        java.util.Arrays.sort(sorted);
        int best = 1, cur = 1;
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] == sorted[i - 1]) continue;      // 去重
            if (sorted[i] == sorted[i - 1] + 1) cur++;
            else cur = 1;
            best = Math.max(best, cur);
        }
        return best;
    }

    private static void run(P128LongestConsecutiveSequence solver, int[] nums, String label) {
        try {
            int got = solver.longestConsecutive(Arrays.copyOf(nums, nums.length));
            int want = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-24s nums=%-44s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s nums=%-44s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s nums=%-44s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
