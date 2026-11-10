import java.util.Arrays;

/**
 * LeetCode 198: 打家劫舍 (House Robber)
 * https://leetcode.cn/problems/house-robber/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面】（打家劫舍 1/3——线性 DP 状态定义的典范）
 *
 * <p>■ 题意
 * <p>　沿街一排房屋 nums[i] = 第 i 栋的现金。
 * <p>　规则：**相邻两栋不能都偷**（报警装置连着邻居）。
 * <p>　求：能偷到的**最大总金额**。
 *
 * <p>■ 输入输出
 * <p>　输入：int[] nums（1 ≤ length ≤ 100；0 ≤ nums[i] ≤ 400）【注意**数组非空**】
 * <p>　输出：int（最大金额）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[1,2,3,1]   → 4　（偷 0 号和 2 号：1+3=4；偷 1/3=3 差 1；0/1 相邻不行）
 * nums=[2,7,9,3,1] → 12（偷 0/2/4：2+9+1=12）
 * nums=[2]         → 2 　（仅一栋，偷它）★ 边界
 * nums=[2,1]       → 2 　（相邻，两栋只取大）★ 边界
 * nums=[0]         → 0
 * nums=[1,2,3,1] 复看 → 第一间 1 vs 第二间 2 —— 偷 1/3 幢 optimal 是 4
 * nums=[2,1,1,2]   → 4（0+3 号：2+2=4；中间两间才 2——不贪中段）
 * nums=[5,5,10,100,10,5] → 110（偷 3 号 100 + 0/1 任一 5 + 5 号 5 = 110？手推：5+5+100+5=?)
 *   手推：idx0=5, idx1=5, idx2=10 (5+10), idx3=100 (5+100), idx4=10+5+... 实际最优 5+100+5=110 ✓
 * nums=[3,6,1,3,6] → 12（1 号+4 号 6+6=12 vs 0+2+4=7——跳中间）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy 标注但页频 ★★★★★（面试小白问；DP 线性的典范）
 * <p>【标签】动态规划(DP) · 数组
 * <p>【题型】线性 DP：dp[i]=max(dp[i-1], dp[i-2]+nums[i])——"最后一步=偷/不偷第 i 栋"两箱取 max
 * <p>【考点】相邻禁令 ⇒ 接 dp[i-2]；地基 dp[0]=nums[0]、dp[1]=max(nums[0],nums[1])；
 * <p>　　　　B 流派（dp 数组长 n、返回 dp[n-1]——70 锁流派公约在此继续）；滚动双变量优化是加分口答
 * <p>【关联】P213（环形版——拆两段线性各跑）；P337（树形版——返回 rob/skip 二元组——"两态分开"流派的正式登场）；P121（股票——同"偷不偷/买卖"二态胚）
 *
 * <p>LCS 签名：rob(int[] nums)。给一排房屋现金，相邻不能都偷，返回最大金额。
 */
public class P198HouseRobber {

    /** 你的实现（前缀世界扩大链：dp[0]=nums[0] 真地基、dp[1]=max、dp[i]=max(dp[i-2]+nums[i], dp[i-1])）。 */
    public int rob(int[] nums) {
        int length = nums.length;
        if (length == 1) {
            return nums[0];
        }
        if (length == 2) {
            return Math.max(nums[0], nums[1]);
        }
        int[] dp =  new int[length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < length; i++) {
            dp[i] = Math.max(dp[i - 2] + nums[i], dp[i - 1]);
        }
        return dp[length - 1];
    }

    public static void main(String[] args) {
        P198HouseRobber solver = new P198HouseRobber();

        run(solver, new int[]{1, 2, 3, 1}, "官方示例1 → 4（1+3）");
        run(solver, new int[]{2, 7, 9, 3, 1}, "官方示例2 → 12（2+9+1）");
        run(solver, new int[]{2}, "单栋 → 2");
        run(solver, new int[]{2, 1}, "两栋相邻 → max=2");
        run(solver, new int[]{0}, "现金 0 → 0");
        run(solver, new int[]{2, 1, 1, 2}, "两端赢过中段 → 4");
        run(solver, new int[]{5, 5, 10, 100, 10, 5}, "中段大头 + 两端小头 → 110");
        run(solver, new int[]{3, 6, 1, 3, 6}, "隔位取大 → 12");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：回溯"每栋 偷/不偷"二叉树，收账于"恰好在叶子（扫完全街道）"——
     * 本题 n ≤ 100 可暴力 2^n 过爆（memo 版）：记忆化 DFS"从第 idx 栋开始抢的最大值"
     * （机制：自顶向下决策树 vs 用户自底向上线性表）。
     */
    private static long dfsRob(int[] nums, int idx, java.util.Map<Integer, Long> memo) {
        if (idx >= nums.length) return 0L;
        Long cached = memo.get(idx);
        if (cached != null) return cached;
        long best = Math.max(
                nums[idx] + dfsRob(nums, idx + 2, memo),   // 偷 idx → 必跳 idx+1
                dfsRob(nums, idx + 1, memo));               // 不偷 idx
        memo.put(idx, best);
        return best;
    }

    private static int bruteForce(int[] nums) {
        return (int) dfsRob(nums, 0, new java.util.HashMap<>());
    }

    private static void run(P198HouseRobber solver, int[] nums, String label) {
        try {
            int got = solver.rob(Arrays.copyOf(nums, nums.length));
            int want = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-36s nums=%-24s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-36s nums=%-24s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-36s nums=%-24s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
