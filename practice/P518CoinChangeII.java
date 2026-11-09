import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 518: 零钱兑换 II (Coin Change II)
 * https://leetcode.cn/problems/coin-change-ii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面】（背包组 3/8——"完全背包"登场；与 494 一墙之隔）
 *
 * <p>■ 题意
 * <p>　给整数数组 coins（coins[i] 是一种**面额**，约束里 notes 面额互不相同）和整数 amount。
 * <p>　每种面额的硬币**都有无限枚**（**同一面额可用任意多次**）。
 * <p>　求：**凑出 amount 元的组合数**。
 * <p>　【"组合"的纪律】:顺序无所谓——{1,2} 和 {2,1} 算【同一种】。
 *
 * <p>■ 三种最易撞的"语义墙"（先立后拆）
 * <p>　① **无限供应**：494/"01 背包"里每个数字只能用一次；【518】每个面额可以重复用——
 * <p>　　　这正是"**完全背包**"（Unbounded Knapsack）的名字。
 * <p>　② **组合 ≠ 排列**：{1,2}={2,1}（同一种）；377 组合总和 IV 是"排列数"（{1,2}≠{2,1}）——
 * <p>　　　**内层正序 + 外层硬币** 数的是组合；**内外层互换** 数排列（同为完全背包，语义不同——本组 6/8 守着它）。
 * <p>　③ **正序登场**：01 背包（494/416）内层倒序以"每枚硬币只用一次"；**完全背包内层正序**（同面额可重复用，不再是"偷改自己"的 bug，而是**题意本身**）。
 *
 * <p>■ 输入输出
 * <p>　输入：int amount（0 ≤ amount ≤ 5000）、int[] coins（1 ≤ length ≤ 300；1 ≤ coins[i] ≤ 5000；coins 全不同）
 * <p>　输出：int（组合数）
 *
 * <p>■ 示例逐条
 * <pre>
 * coins=[1,2,5], amount=5 → 4
 * 　　{5}；{2,2,1}；{2,1,1,1}；{1,1,1,1,1}
 * 　　（注意{1,2,2},{2,2,1},{2,1,2} …顺序全算一种——只看每个面额几枚）
 * 　　手推：用面额 5 → 1 种；用面额 2 和 1 → 2 的数量 0..2 枚、1 的数量 5-2k 枚（k=0..2 →3 种）→ 共 1+3=4
 * coins=[2], amount=3 → 0   （2 的倍数永远凑不出 3）
 * coins=[10], amount=10 → 1 ({10})
 * coins=[], amount=0 → 1 （"不选"即凑 0——dp[0]=1 地基）
 * coins=[3,7,11,13], amount=500 → 7667（大额压力用例，参考核算）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium
 * <p>【标签】动态规划(DP) · 完全背包（组合数型）· 计数
 * <p>【题型】"凑 amount 的组合数"，dp[j] += dp[j-coin]，**内层正序**（完全背包）
 * <p>【考点】01 倒序 → 完全背包正序的转折；外层硬币/内层金额固定(组合数)；与 377 排列数对照
 * <p>【关联】P494（01 背包方案数——本题的"倒序"镜像）；P377（同 DP 但内外层颠倒=排列数）；P322（同一硬币的最少枚数——min 的完全背包）
 *
 * <p>给面额数组 coins（无限枚）和金额 amount，返回凑出 amount 的组合数。
 */
public class P518CoinChangeII {

    /** 你的实现（组合数型完全背包：外层面额、内层金额正序、dp[0]=1 地基）。 */
    public int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
        }
        return dp[amount];
    }

    public static void main(String[] args) {
        P518CoinChangeII solver = new P518CoinChangeII();

        run(solver, 5, new int[]{1, 2, 5}, "官方示例 → 4（面额 5/2/1 三种盘子）");
        run(solver, 3, new int[]{2}, "2 的倍数凑不出 3 → 0");
        run(solver, 10, new int[]{10}, "单面额整除 → 1");
        run(solver, 0, new int[]{}, "空数组凑 0 → 1（空集地基）");
        run(solver, 0, new int[]{7}, "amount=0 → 1（全不选）");
        run(solver, 6, new int[]{1, 2, 3}, "1/2/3 六元 → 参考定（手推 {1,1,1,1,1,1} 等）");
        run(solver, 4, new int[]{2, 4}, "偶数面额 → 参考定");
        run(solver, 500, new int[]{3, 7, 11, 13}, "大额压力（参考定）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：【递归+记忆化】直接枚举"用每种面额几枚"，2 层循环依面额子集消重，与滚动 1-D 表独立。
     */
    private static long memoRef(int amount, int[] coins, int idx, Map<String, Long> memo) {
        if (amount == 0) return 1L;
        String key = amount + "," + idx;
        Long cached = memo.get(key);
        if (cached != null) return cached;
        long ways = 0;
        for (int i = idx; i < coins.length; i++) {    // 按"从 idx 起"防止排列重复
            if (coins[i] <= amount) {
                ways += memoRef(amount - coins[i], coins, i, memo);   // 可以再取同一面额（i 不加 1）
            }
        }
        memo.put(key, ways);
        return ways;
    }

    private static int bruteForce(int amount, int[] coins) {
        Map<String, Long> memo = new HashMap<>();
        long r = memoRef(amount, coins, 0, memo);
        return (int) r;
    }

    private static void run(P518CoinChangeII solver, int amount, int[] coins, String label) {
        try {
            int got = solver.change(amount, Arrays.copyOf(coins, coins.length));
            int want = bruteForce(amount, Arrays.copyOf(coins, coins.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-28s amount=%-5d coins=%-24s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, amount, Arrays.toString(coins), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s amount=%-5d coins=%-24s | TODO 未实现%n", label, amount, Arrays.toString(coins));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s amount=%-5d coins=%-24s | 异常: %s%n", label, amount, Arrays.toString(coins), e);
        }
    }
}
