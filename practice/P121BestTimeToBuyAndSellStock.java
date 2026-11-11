import java.util.Arrays;

/**
 * LeetCode 121: 买卖股票的最佳时机 (Best Time to Buy and Sell Stock)
 * https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（股票组 1/3——状态机 DP 开门）
 *
 * <p>■ 题目描述
 *   给一个数组 prices，prices[i] 表示股票第 i 天的价格。
 *   你可以完成【一笔交易】：
 *     1) 在某一天买入（持有，账户扣 prices[i]）；
 *     2) 在【之后的某一天】卖出（清仓，账户得 prices[j]，j > i）；
 *     3) 只能买一次卖一次（买入当天不能又卖，同日买卖需隔日——LeetCode 本题
 *        定义为"先买入后卖出"，i < j）。
 * ■ 求
 *   所能获得的最大利润。若无法获利（一路下跌等），返回 0（不交易也是合法选择）。
 *
 * <p>■ 输入输出
 *   输入：int[] prices（1 ≤ length ≤ 10^5；0 ≤ prices[i] ≤ 10^4）
 *   输出：int（最大利润，≥0）
 *
 * <p>■ 示例逐条
 * <pre>
 * prices=[7,1,5,3,6,4] → 5（第 2 天 1 买入、第 5 天 6 卖出）
 * prices=[7,6,4,3,1]   → 0（一路跌——任何买卖对都亏，不交易）
 * prices=[5]           → 0（单日无对手日，卖不出）
 * prices=[2,4]         → 2（唯一可行对）
 * prices=[2,1,4]       → 3（1 买 4 卖；别被 2 干扰）
 * prices=[3,3,3]       → 0（平盘，利润 0）
 * prices=[1,2,4,2,5,7,2,4,9,0] → 9（9−0 后段更低不代表最优——参考定）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★★——面试最高频股票题）
 * <p>【标签】动态规划(DP) · 状态机 · 贪心（双流派）
 * <p>【题型】一笔交易最大利润：①贪心流（历史最低价滚动）②状态机流（每天 hold/free 两态）
 * <p>【考点】"i < j"次序约束的表达；不交易返 0；两流派的等价性（状态机是贪心的"正装"）
 * <p>【关联】P122（贪心已做：多笔全吃上涨——本题为单笔特例）；P123（≤2 笔：五状态）；P188（k 笔：状态数组化）
 *
 * <p>LCS 签名：maxProfit(int[] prices)。只能一笔交易（先买后卖），求最大利润，无法获利返 0。
 */
public class P121BestTimeToBuyAndSellStock {

    /** 你的实现【状态机流】：hold/free 两态现金流滚动（贪心版见 maxProfitGreedy 对照；
     *  O(n²) 暴力首版由 LC 规模核算判死——n≤10⁵ 必 TLE，教训入错题本）。 */
    public int maxProfit(int[] prices) {
        int hold = -prices[0];
        int free = 0;
        for (int i = 1; i < prices.length; i++) {
            // 继续持有 or 今天买（0 现金起步——单笔锁）
            int newHold = Math.max(hold, -prices[i]);
            // 继续空仓 or 卖（引用旧 hold——先算两个 new 再统一提交）
            int newFree = Math.max(free, hold + prices[i]);
            hold = newHold;
            free = newFree;
        }
        return free;
    }

    /** 你的实现【贪心流】（历史最低价滚动——与状态机逐日对表的等价版，对照存档）。 */
    public int maxProfitGreedy(int[] prices) {
        int miniPrice = prices[0];
        int best = 0;
        for (int i = 1; i < prices.length; i++) {
            if (miniPrice > prices[i]) {
                miniPrice = prices[i];
            } else {
                best = Math.max(best, prices[i] - miniPrice);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        P121BestTimeToBuyAndSellStock solver = new P121BestTimeToBuyAndSellStock();

        run(solver, new int[]{7, 1, 5, 3, 6, 4}, "官方例1 → 5（1买6卖）");
        run(solver, new int[]{7, 6, 4, 3, 1}, "一路跌 → 0");
        run(solver, new int[]{5}, "单日 → 0（i<j 约束）");
        run(solver, new int[]{2, 4}, "两天上行 → 2");
        run(solver, new int[]{2, 1, 4}, "坑后涨 → 3");
        run(solver, new int[]{3, 3, 3}, "平盘 → 0");
        run(solver, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, "多峰压力 → 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举所有 (i, j) 买卖对，i<j 约束下取 max——O(n²)，机制与单扫完全不同。 */
    private static int bruteForce(int[] prices) {
        int best = 0;
        for (int i = 0; i < prices.length; i++) {
            for (int j = i + 1; j < prices.length; j++) {
                best = Math.max(best, prices[j] - prices[i]);
            }
        }
        return best;
    }

    /** 参考二（状态机流，对照用）：每天两态 hold/free——贪心的"正装"表达。 */
    private static int refStateMachine(int[] prices) {
        int hold = -prices[0];          // 第 0 天买入（持有态最优）
        int free = 0;                   // 空仓态最优
        for (int i = 1; i < prices.length; i++) {
            int newHold = Math.max(hold, -prices[i]);         // 继续持有 or 今天买（0 现金起步——单笔！）
            int newFree = Math.max(free, hold + prices[i]);   // 继续空仓 or 今天卖
            hold = newHold;
            free = newFree;
        }
        return free;                    // 结束时空仓（已卖出/从未买）为最优
    }

    private static void run(P121BestTimeToBuyAndSellStock solver, int[] prices, String label) {
        try {
            int got = solver.maxProfit(Arrays.copyOf(prices, prices.length));
            int want = bruteForce(Arrays.copyOf(prices, prices.length));
            int ref2 = solver.maxProfitGreedy(Arrays.copyOf(prices, prices.length));
            boolean ok = got == want && got == ref2;
            String extra = (got != ref2) ? "（状态机参考不一致！）" : "";

            System.out.printf("[%s] %-30s prices=%-30s | 参考=%d 状态机=%d 实得=%d %s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(prices), want, ref2, got, extra);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s prices=%-30s | TODO 未实现%n", label, Arrays.toString(prices));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s prices=%-30s | 异常: %s%n", label, Arrays.toString(prices), e);
        }
    }
}
