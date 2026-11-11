import java.util.Arrays;

/**
 * LeetCode 188: 买卖股票的最佳时机 IV (Best Time to Buy and Sell Stock IV)
 * https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-iv/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（股票 3/3 收官——"k 笔"状态数组化）
 *
 * <p>■ 题目描述
 *   给一个整数 k 和数组 prices（prices[i] 是第 i 天股价）。
 *   你最多可以完成【k 笔交易】：
 *     1) 一笔 = 一次买入 + 一次卖出（先买后卖，i < j）；
 *     2) 卖清仓后才能再买（同刻最多持一股）；
 *     3) 多笔交易【不能重叠】；
 *     4) 也可以只做更少笔数（k 是上限）。
 * ■ 求
 *   所能获得的最大利润。
 *
 * <p>■ 输入输出
 *   输入：int k（0 ≤ k ≤ 100）、int[] prices（1 ≤ length ≤ 1000；0 ≤ prices[i] ≤ 1000）
 *   输出：int（最大利润，≥0）
 *
 * <p>■ 示例逐条
 * <pre>
 * k=2, prices=[2,4,1]           → 2（1 买 4 卖；k=2 上限但一笔最优）
 * k=2, prices=[3,2,6,5,0,3]     → 7（2 买 5 卖=3 + 0 买 3 卖=3 → 合计 6? 参考定：2买6卖=4+0买3卖=3=7）
 * k=0, prices=[1,2,4]           → 0（0 笔上限=不许交易）
 * k=1, prices=[7,6,4,3,1]       → 0（一路跌）
 * k=100, prices=[1,2,4,2,5,7,2,4,9,0] → 15（k≥n/2 退化为 122 全吃：2+2+2+5+... 参考定）
 * k=1, prices=[1,2,4,2,5,7,2,4,9,0]   → 8（121 同款退化——单笔最优 9−1=8）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Hard（页频 ★★★）
 * <p>【标签】动态规划(DP) · 状态机 · 状态数组化
 * <p>【题型】≤k 笔：buy[j]/sell[j] 数组化（123 四态的 k 泛化）+ k≥n/2 退化剪枝
 * <p>【考点】k>n/2 剪枝（一笔至少两天——超出的 k 全是"死上限"，直接 122 贪心全吃）；
 * <p>　　　　k=0 边界（不许交易 → 0）；j 循环方向（升序=同日接力合法，与 123 拓扑序同理）；
 * <p>　　　　123 的四态 = 本题 k=2 的展开（"数组化"不是新思想，是同一思想的参数化）
 * <p>【关联】P121（k=1 退化）；P123（k=2 展开）；P122（k=∞ 贪心全吃）
 *
 * <p>LCS 签名：maxProfit(int k, int[] prices)——注意参数顺序（k 在前）。
 */
public class P188BestTimeToBuyAndSellStockIV {

    /** 你的实现终版——三击修齐 + 剪枝分支 122 贪心（0 起算累加正差）归位。 */
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if (k > n / 2) {
            int profix = 0;
            for (int i = 1; i < n; i++) {
                if (prices[i] > prices[i-1]) {
                    profix += prices[i] - prices[i - 1];
                }
            }
            return profix;
        }
        int[] buy = new int[k + 1];
        Arrays.fill(buy, Integer.MIN_VALUE);
        int[] sell = new int[k + 1];

        for (int price : prices) {
            for (int i = 1; i <= k; i++) {
                buy[i] = Math.max(buy[i], sell[i - 1] - price);
                sell[i] = Math.max(sell[i], buy[i] + price);
            }
        }
        return sell[k];
    }

    public static void main(String[] args) {
        P188BestTimeToBuyAndSellStockIV solver = new P188BestTimeToBuyAndSellStockIV();

        run(solver, 2, new int[]{2, 4, 1}, "官方例1 → 2（一笔最优）");
        run(solver, 2, new int[]{3, 2, 6, 5, 0, 3}, "官方例2 → 参考定");
        run(solver, 0, new int[]{1, 2, 4}, "k=0 不许交易 → 0 ★ 边界");
        run(solver, 1, new int[]{7, 6, 4, 3, 1}, "k=1 一路跌 → 0");
        run(solver, 100, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, "k≥n/2 退化 → 参考定（122 全吃）");
        run(solver, 1, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, "k=1 退化 121 → 参考定（8）");
        run(solver, 3, new int[]{6, 1, 3, 2, 4, 7}, "k=3 两段接力 → 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：递归记忆化"第 i 天、剩余 k 笔、持股与否"三态 DFS——
     * 与状态数组化 dp 表机制不同（自顶向下 vs 自底向上）。
     */
    private static int dfsK(int[] prices, int i, int kLeft, boolean holding, java.util.Map<String, Integer> memo) {
        if (i >= prices.length) return 0;
        String key = i + "," + kLeft + "," + holding;
        Integer cached = memo.get(key);
        if (cached != null) return cached;
        int best;
        if (holding) {
            // 卖（用掉一笔） or 拿着
            best = Math.max(prices[i] + dfsK(prices, i + 1, kLeft - 1, false, memo),
                            dfsK(prices, i + 1, kLeft, true, memo));
        } else {
            // 买（需 kLeft>0——卖出时结算笔数） or 空仓
            int buyBranch = kLeft > 0
                    ? -prices[i] + dfsK(prices, i + 1, kLeft, true, memo)
                    : Integer.MIN_VALUE;
            best = Math.max(buyBranch, dfsK(prices, i + 1, kLeft, false, memo));
        }
        memo.put(key, best);
        return best;
    }

    private static int bruteForce(int k, int[] prices) {
        return dfsK(prices, 0, k, false, new java.util.HashMap<>());
    }

    private static void run(P188BestTimeToBuyAndSellStockIV solver, int k, int[] prices, String label) {
        try {
            int got = solver.maxProfit(k, Arrays.copyOf(prices, prices.length));
            int want = bruteForce(k, Arrays.copyOf(prices, prices.length));
            boolean ok = got == want;

            System.out.printf("[%s] k=%-4d prices=%-30s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", k, Arrays.toString(prices), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] k=%-4d prices=%-30s | TODO 未实现%n", k, Arrays.toString(prices));
        } catch (Exception e) {
            System.out.printf("[FAIL] k=%-4d prices=%-30s | 异常: %s%n", k, Arrays.toString(prices), e);
        }
    }
}
