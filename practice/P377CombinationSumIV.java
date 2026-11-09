import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 377: 组合总和 IV (Combination Sum IV)
 * https://leetcode.cn/problems/combination-sum-iv/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面】（背包组 4/8——518 的对照题"内外层互换"= 排列数）
 *
 * <p>■ 题意
 * <p>　给**互不相同**的正整数数组 nums（每个数**无限次取用**——注意无次数上限）和整数 target。
 * <p>　求：**序列总数** —— 从 nums（可重复）中挑元素排成有顺序的序列，**顺序不同的序列算不同方案**。
 * <p>　（题干叫"组合"，但官方注释明确"顺序不同视为不同"——实际是 **排列数**。这正是它和 518 互为镜像的分水岭）
 *
 * <p>■ 对照（本章口诀"内外层 = 组合/排列开关"的定型题）
 * <pre>
 *   518（组合）: 外层 for coin（物品序）  内层 for j（正序）→ {1,3}={3,1} 一种
 *   377（排列）: 外层 for i（容量序）    内层 for num     → (1,3) ≠ (3,1)
 * </pre>
 * <p>　这个开关的全部物理含义：**"最后一步是谁"**——
 * <p>　外层金额 i 时，dp[i] 的更新按"**最后一步选哪个 num**"分类（7=…最后放 1 / 最后放 2 / 最后放 3）——不同顺序天然分开数。
 *
 * <p>■ 输入输出
 * <p>　输入：int target（1 ≤ target ≤ 200）, int[] nums（1 ≤ length ≤ 200；1 ≤ nums[i] ≤ 1000；互不相同）
 * <p>　输出：int（序列数，LeetCode 保证在 int 内——但**中途 dp 需防溢出么？官方保证最终答案 int 内，中间值也 ≤ 答案？dp 依 i 单调不减到 target（排列数不会倒退），最大值即答案 → int 安全**）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[1,2,3], target=4 → 7
 * 　　(1,1,1,1) (1,3) (3,1) (1,1,2) (1,2,1) (2,1,1) (2,2)   ← 7 种（官方 6+2=7？见 harness 参考）
 * nums=[1,2], target=3 → 3
 * 　　(1,1,1) (1,2) (2,1)   ← {1,1,2}、{1,2}、{2,1}三重身份
 * nums=[9], target=3 → 0    （9 &gt; 3，一步也放不下；dp 递推时 dp[1..3] 全空 → 0）
 * nums=[2,34], target=100 → ？（参考实现核算）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium
 * <p>【标签】动态规划(DP) · 完全背包（排列数型）· 计数
 * <p>【题型】"凑 target 的**排列**数"；外层容量、内层物品（与 518 倒置）
 * <p>【考点】内外层顺序 = 组合/排列的开关定型题；dp[0]=1 地基；无大数溢出（？见 constraints）
 * <p>【关联】P518（同一张表的方向反转——组合 vs 排列）；P494（01 背包计数——倒序；377 是无限次取）
 *
 * <p>LCS 签名：combinationSum4(int[] nums, int target)。给互不相同的正整数数组 nums 和 target，可无限次取用元素，返回按序拼成 target 的序列数。
 */
public class P377CombinationSumIV {

    /** 你的实现（修正 < → <= 后；O(target×n) 排列数型一维递推）。 */
    public int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1;
        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (i >= num) {
                    dp[i] += dp[i - num];
                }
            }
        }
        return dp[target];
    }

    public static void main(String[] args) {
        P377CombinationSumIV solver = new P377CombinationSumIV();

        run(solver, 4, new int[]{1, 2, 3}, "官方示例 → 7（(1,3)≠(3,1) 算两种）");
        run(solver, 3, new int[]{1, 2}, "最小镜像（(1,2)≠(2,1)）→ 参考定");
        run(solver, 3, new int[]{9}, "一步也放不下 → 0");
        run(solver, 0, new int[]{7}, "target=0（约束保证 ≥1，防御用）→ 参考定");
        run(solver, 10, new int[]{2, 34}, "大跨度（参考定）");
        run(solver, 35, new int[]{1, 5, 10}, "斐波那契式递推（参考定）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：【自底向上 DFS（带记忆）】"从空起，可以 Black'к's 挑元素放队尾"
     * 等价于外层容量转义（每格枚举"最后放谁"），与 1-D 滚动表、内外层互换都不同。
     */
    private static long dfsRef(int remain, int[] nums, Map<Integer, Long> memo) {
        if (remain == 0) return 1L;
        Long cached = memo.get(remain);
        if (cached != null) return cached;
        long ways = 0;
        for (int n : nums) {
            if (n <= remain) {
                ways += dfsRef(remain - n, nums, memo);
            }
        }
        memo.put(remain, ways);
        return ways;
    }

    private static int bruteForce(int target, int[] nums) {
        if (target == 0) return 1;
        long r = dfsRef(target, nums, new HashMap<>());
        return (int) r;
    }

    private static void run(P377CombinationSumIV solver, int target, int[] nums, String label) {
        try {
            int got = solver.combinationSum4(Arrays.copyOf(nums, nums.length), target);
            int want = bruteForce(target, Arrays.copyOf(nums, nums.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-32s target=%-5d nums=%-24s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, target, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-32s target=%-5d nums=%-24s | TODO 未实现%n", label, target, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-32s target=%-5d nums=%-24s | 异常: %s%n", label, target, Arrays.toString(nums), e);
        }
    }
}
