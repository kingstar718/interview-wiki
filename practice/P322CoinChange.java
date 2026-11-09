import java.util.Arrays;

/**
 * LeetCode 322: 零钱兑换 (Coin Change)
 * https://leetcode.cn/problems/coin-change/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面】（背包组 5/8——最值型登场：组合计数 → 最少枚数）
 *
 * <p>■ 题意
 * <p>　给面额数组 coins（**每种无限枚**）和金额 amount。
 * <p>　求：**凑出 amount 所需的最少硬币数**。
 * <p>　**无法凑出**（任何组合都到不了 amount）返回 **-1**。
 *
 * <p>■ 与 518 的对照（同一张表换"减法算子"）
 * <pre>
 *   518 组合数: dp[j] += dp[j−coin]       （"几种"）
 *   322 最值:   dp[j] = min(dp[j], dp[j−coin] + 1)   （"最少几枚"）
 *   共同点：外层容量自增递推（377 式骨架）、内层枚举"最后一步"、dp[0]=0 地基
 * </pre>
 * <p>■ "无穷大"公约（计数→最值的地基迁移）
 * <p>　计数表的地基是 1/true；**最值表需要"还凑不出的格子"**——用
 * <p>　**`amount + 1`** 充当"伪 ∞"（概率上任何 dp[i] ≤ amount——最多用 amount 枚 1 元。
 * <p>　所以 `dp[i] > amount` ⟺ "凑不出" → 返回 -1）。
 * <p>　**别用 Integer.MAX_VALUE**：52*10 之类会溢出（`dp[i−coin]+1` 相加时越过 int.max）——
 * <p>　（amount+1 的妙处：+1 后仍 ≤ amount+1+1 ≤ int 一切安全；判"不可达"只需 > amount）。
 *
 * <p>■ 输入输出
 * <p>　输入：int[] coins（1 ≤ length ≤ 12；1 ≤ coins[i] ≤ 2^31−1，可很大）、int amount（0 ≤ amount ≤ 10^4）
 * <p>　输出：int（最少枚数，凑不出 -1）
 *
 * <p>■ 示例逐条
 * <pre>
 * coins=[1,2,5], amount=11 → 3   （5+5+1）
 * coins=[2], amount=3 → -1       （偶数面额凑不出 3——"无法凑出"）
 * coins=[1], amount=0 → 0        （约束 0 ≤ amount；0 元用 0 枚——不花）
 * coins=[1,2147483647], amount=5 → 5（超大面额 if 挡下，靠 1 元走 5 枚）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★ 最高频）
 * <p>【标签】动态规划(DP) · 完全背包（最值型）
 * <p>【题型】"最少几枚"：dp[j]=min(dp[j], dp[j−coin]+1)，外层容量自增、内层物品
 * <p>【考点】"伪 ∞ = amount+1"约定（不用 MAX_VALUE 防溢出）；dp[0]=0；返回判 `> amount → -1`；贪心不成立（大面额贪心 rounds 差值，例 [1,3,4] 凑 6 贪 4+1+1=3 枚 vs 正确 3+3=2 枚）
 * <p>【关联】P518（组合数 → 最值）、P377（排列数 → 最值同骨架）、P494（三者加一起：01 计数 = 本章完整学长）
 *
 * <p>LCS 签名：coinChange(int[] coins, int amount)。给面额数组（无限枚）与金额，返回最少枚数，凑不出 -1。
 */
public class P322CoinChange {

    /** 你的实现（伪 ∞ fill + dp[0]=0 地基 + min 递推 + > amount 判 -1）。 */
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 :dp[amount];
    }

    public static void main(String[] args) {
        P322CoinChange solver = new P322CoinChange();

        run(solver, new int[]{1, 2, 5}, 11, "官方示例 → 3（5+5+1）");
        run(solver, new int[]{2}, 3, "偶数凑奇 → -1");
        run(solver, new int[]{1}, 0, "0 元 0 枚（边界）");
        run(solver, new int[]{1, 2147483647}, 5, "超大面额被 if 挡，靠 1 元走 5 枚");
        run(solver, new int[]{1, 3, 4}, 6, "贪心反例（贪 4+1+1=3 vs 正确 3+3=2 → 参考定）");
        run(solver, new int[]{186, 419, 83, 408}, 6249, "LC 大压力用例（参考定）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：记忆化 DFS"从剩余金额出发枚举每一步拿谁"——最值型的递归物理形式，
     * 机制不同（自顶向下、枚举所有分支取最小），与自底向上 min 递推互验。
     */
    private static long dfsMin(int remain, int[] coins, java.util.Map<Integer, Long> memo) {
        if (remain == 0) return 0L;
        if (remain < 0) return -1L;
        Long cached = memo.get(remain);
        if (cached != null) return cached;
        long best = -2;                                // -2 = "凑不出"哨兵
        for (int c : coins) {
            long sub = dfsMin(remain - c, coins, memo);
            if (sub >= 0) {                            // 子问题成功的才参与竞争
                long cand = sub + 1;
                if (best == -2 || cand < best) best = cand;
            }
        }
        memo.put(remain, best);
        return best;
    }

    private static int bruteForce(int[] coins, int amount) {
        long r = dfsMin(amount, coins, new java.util.HashMap<>());
        return r == -2 ? -1 : (int) r;
    }

    private static void run(P322CoinChange solver, int[] coins, int amount, String label) {
        try {
            int got = solver.coinChange(Arrays.copyOf(coins, coins.length), amount);
            int want = bruteForce(Arrays.copyOf(coins, coins.length), amount);
            boolean ok = got == want;

            System.out.printf("[%s] %-40s coins=%-28s amount=%-6d | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(coins), amount, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-40s coins=%-28s amount=%-6d | TODO 未实现%n", label, Arrays.toString(coins), amount);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-40s coins=%-28s amount=%-6d | 异常: %s%n", label, Arrays.toString(coins), amount, e);
        }
    }
}
