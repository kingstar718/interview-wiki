import java.util.Arrays;

/**
 * LeetCode 136: 只出现一次的数字 (Single Number)
 * https://leetcode.cn/problems/single-number/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 5/50——异或自反）
 *
 * <p>■ 题目描述
 *   给一个**非空**整数数组，除某个元素只出现一次外，**其余每个元素都恰出现两次**。
 *   找出那个只出现一次的元素。要求线性 O(n) 时间、**不使用额外空间**（进阶）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ length ≤ 3×10^4；每个元素出现 1 次或 2 次；-3×10^4 ≤ nums[i] ≤ 3×10^4）
 *   输出：int（只出现一次的那个数）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[2,2,1] → 1
 * nums=[4,1,2,1,2] → 4（1 和 2 各两次，4 单飞）
 * nums=[1] → 1（单元素）
 * nums=[-1,-1,-2] → -2（负数照样配对——异或按位操作与符号无关）
 * nums=[0,0,5] → 5（0 与 0 配对归零）
 * nums=[7,3,7,5,5] → 3（乱序——异或满足交换律，顺序不影响）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★· Hot100）
 * <p>【标签】位运算 · 哈希表
 * <p>【题型】异或自反：a⊕a=0、a⊕0=a、交换律结合律 → 全员异或一遍，成对的抵消，单飞者留底
 * <p>【考点】异或三律（自反/恒等/交换结合）；为什么 Hashset 计数解"额外空间"输给位运算；
 * <p>　　　　负数与 0 的配对同样成立（按位运算无符号概念）
 * <p>【关联】P169（摩尔投票——同族"找多数/独苗"）；P260 只出现一次III（两个单飞的进阶——未收）
 *
 * <p>LCS 签名：singleNumber(int[] nums)。
 */
public class P136SingleNumber {

    /** 你的实现（异或自反：全员异或，配对归零，单飞留底）。 */
    public int singleNumber(int[] nums) {
        int res = 0;
        for (int i : nums) {
            res ^= i;
        }
        return res;
    }

    public static void main(String[] args) {
        P136SingleNumber solver = new P136SingleNumber();

        run(solver, new int[]{2, 2, 1}, "官方例1 → 1");
        run(solver, new int[]{4, 1, 2, 1, 2}, "官方例2 → 4");
        run(solver, new int[]{1}, "单元素 → 1");
        run(solver, new int[]{-1, -1, -2}, "负数 → -2");
        run(solver, new int[]{0, 0, 5}, "0 配对 → 5");
        run(solver, new int[]{7, 3, 7, 5, 5}, "乱序 → 3");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：Map 计数后取 count==1 的键——机制与异或位运算完全不同。 */
    private static int bruteForce(int[] nums) {
        java.util.Map<Integer, Integer> cnt = new java.util.HashMap<>();
        for (int n : nums) cnt.merge(n, 1, Integer::sum);
        for (java.util.Map.Entry<Integer, Integer> e : cnt.entrySet()) {
            if (e.getValue() == 1) return e.getKey();
        }
        throw new IllegalStateException("无单飞元素");
    }

    private static void run(P136SingleNumber solver, int[] nums, String label) {
        try {
            int got = solver.singleNumber(Arrays.copyOf(nums, nums.length));
            int want = bruteForce(nums);
            boolean ok = got == want;

            System.out.printf("[%s] %-20s nums=%-30s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-20s nums=%-30s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-20s nums=%-30s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
