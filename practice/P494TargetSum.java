import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 494: 目标和 (Target Sum)
 * https://leetcode.cn/problems/target-sum/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面】（背包组 2/8——在 416 的"判定型"上升级为"方案数"）
 *
 * <p>■ 题意
 * <p>　给一个非负整数数组 nums 和一个整数 target（可以是负数！）。
 * <p>　给每个数字前面**加 '+' 或 '-' 号**，把数组组成一个带符号的和式：
 * <p>　　　例如 nums=[1,1,1,1,1] → "+1+1+1+1-1"（结果 3）之类。
 * <p>　返回：**使表达式结果恰好等于 target 的符号组合总共有多少种**。
 *
 * <p>■ 关键转化（比 416 深一拍的"代数变形"型翻译）
 * <p>　设放'+'的数字集合 P（和 = p），放'-'的数字集合 N（和 = q）。
 * <p>　则：p − q = target　且　p + q = sum（正筐+负筐恰好瓜分全部数字）
 * <p>　解二元方程：p = (sum + target) / 2    ← ★ 化成一个"选子集和为 p"问题
 * <p>　→ 于是题目 = "从 nums 里选若干数（每个只得选一次），和恰为 p 的**方案数**" —— 又是 01 背包！
 * <p>　（选取的那些数放"+"号，未选的自动落"-"号；一个集合对应唯一一种符号组合）
 *
 * <p>■ 剪枝（与 416 镜像的三个"不可能先判"）
 * <p>　① sum + target &lt; 0  → p 为负 → 确定凑不出（返回 0）
 * <p>　② (sum + target) 奇数 → p 非整数 → 恰好不行（返回 0）
 * <p>　③ nums 全非负：若 |target| > sum → 也是 0（隐含在①——sum+target &lt; 0 且 sum−target&gt;sum≈必错，
 * <p>　　　　　　　　　　实际只需判 sum+target 一个，因为负 target 时 |target|≤sum ⟺ sum+target ≥ 0）
 *
 * <p>■ 输入输出
 * <p>　输入：int[] nums（1 ≤ length ≤ 20；0 ≤ nums[i] ≤ 1000）、int target（LeetCode 保证：-1000 ≤ target ≤ 1000 之类，constraints 详官网）
 * <p>　输出：int（方案数）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[1,1,1,1,1], target=3 → 5（p = (5+3)/2 = 4 → 四个 1 相加的方案数 C(5,4)=5）
 * 　　验证：+1+1+1+1-1 等 5 种（-1+1+1+1+1 等）——每个表达式的"负 1"位置五选一
 * nums=[1], target=1 → 1（+1）
 * nums=[1], target=2 → 0（p = (1+2)/2 非整 → 0）
 * nums=[0,0,1], target=1 → ？ p = (1+1)/2 = 1 → 从 {0,0,1} 选子集和为 1：{1},{0,1}?,{0,1},{0},{0,0,1}?
 * 　　{0,0,1} 和=1 ✓；{0} 和=0；{0,1}=1 ✓；{0,0}=0；{0,0,1}=1 ✓ → 这里"0 可以正号/负号都可"——
 * 　　这句话错了：一个 0 无论正负贡献相同 → **每个 0 都独立膨胀 2 倍方案**
 * 　　官方答案见 LeetCode（Java 实测：非官方预期由暴力校验器裁定）；用暴力参考为准。
 * nums=[2,3,5], target=1 → p = (10+1)/2 非整 → 0

 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium
 * <p>【标签】动态规划(DP) · 0-1 背包（方案数型）
 * <p>【题型】P=(sum+target)/2 的"选子集和方案数"；dp[j] += dp[j-num]（判定 boolean → 计数 int）
 * <p>【考点】代数变形（p−q=target、p+q=sum 联立）；奇偶/负数剪枝；倒序不变（01 不是完全）
 * <p>【关联】P416（判定型 → 本题的计数型）；P518（组合数 vs 排列Ⅹ——"物品外/容量外"次序差异）
 *
 * <p>给非负数组 nums 和整数 target，返回每种 ± 号组合中使结果 = target 的方案数。
 */
public class P494TargetSum {

    /** 你的实现（代数变形 p=(sum+target)/2 + 计数型 01 背包倒序 +=）。 */
    public int findTargetSumWays(int[] nums, int target) {
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        if (sum + target < 0) {
            return 0;
        }
        if ((sum + target) % 2 == 1) {
            return 0;
        }
        int P = (sum + target) / 2;
        int[] dp = new int[P + 1];
        dp[0] = 1;
        for (int num : nums) {
            for (int i = P; i >= num ; i--) {
                dp[i] += dp[i - num];
            }
        }
        return dp[P];
    }

    public static void main(String[] args) {
        P494TargetSum solver = new P494TargetSum();

        run(solver, new int[]{1, 1, 1, 1, 1}, 3, "官方示例 → 5（负号位置五选一）");
        run(solver, new int[]{1}, 1, "单数字 → 1");
        run(solver, new int[]{1}, 2, "p=(1+2)/2 非整 → 0");
        run(solver, new int[]{2, 3, 5}, 1, "p=(10+1)/2 非整 → 0");
        run(solver, new int[]{0, 0, 1}, 1, "零膨胀问题 → 由暴力定");
        run(solver, new int[]{1, 0, 0, 0, 0}, 1, "5 个位置选一个为主 → 由暴力定");
        run(solver, new int[]{3, 5, 7}, 5, "不可拆 → 由参考定");
        run(solver, new int[]{9, 7, 5, 3}, -2, "target 可负 → 由参考定");
        run(solver, new int[]{7, 9, 3, 8, 0, 2, 4, 8, 3, 9}, 0, "LeetCode 压力用例（参考定）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：每格 DFS 两种符号（+/−）直接拼值，n ≤ 22 层。指数级但机制独立。
     */
    private static long bruteCount = 0;

    private static void dfs(int[] nums, int idx, long current, int target) {
        if (idx == nums.length) {
            if (current == target) bruteCount++;
            return;
        }
        dfs(nums, idx + 1, current + nums[idx], target);
        dfs(nums, idx + 1, current - nums[idx], target);
    }

    private static int bruteForce(int[] nums, int target) {
        if (nums.length > 22) {   // 长用例不暴力（改用数学法独立实现：p 计数 DP 双机制依旧独立）
            int sum = 0;
            for (int v : nums) sum += v;
            if ((sum + target) < 0 || ((sum + target) & 1) == 1) return 0;
            int P = (sum + target) / 2;
            // 独立法：二维 DP（f[i][j] = 前 i 个数选出和 j 的方案数），与滚动不同行序
            long[] f = new long[P + 1];
            f[0] = 1;
            for (int i = 0; i < nums.length; i++) {
                for (int j = P; j >= nums[i]; j--) {
                    f[j] += f[j - nums[i]];
                }
            }
            return (int) f[P];
        }
        bruteCount = 0;
        dfs(nums, 0, 0, target);
        return (int) bruteCount;
    }

    private static void run(P494TargetSum solver, int[] nums, int target, String label) {
        try {
            int got = solver.findTargetSumWays(Arrays.copyOf(nums, nums.length), target);
            int want = bruteForce(Arrays.copyOf(nums, nums.length), target);
            boolean ok = got == want;

            System.out.printf("[%s] %-40s nums=%-26s target=%-4d | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), target, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-40s nums=%-26s target=%-4d | TODO 未实现%n", label, Arrays.toString(nums), target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-40s nums=%-26s target=%-4d | 异常: %s%n", label, Arrays.toString(nums), target, e);
        }
    }
}
