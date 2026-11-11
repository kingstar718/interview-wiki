import java.util.Arrays;

/**
 * LeetCode 123: 买卖股票的最佳时机 III (Best Time to Buy and Sell Stock III)
 * https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-iii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（股票 2/3——"≤2 笔"状态机五状态）
 *
 * <p>■ 题目描述
 *   给一个数组 prices，prices[i] 是第 i 天股价。
 *   你最多可以完成【两笔交易】：
 *     1) 一笔交易 = 一次买入 + 一次卖出（先买后卖，i < j）；
 *     2) 卖清仓后才能再买（同一时刻最多持一股）；
 *     3) 两笔交易【不能重叠】（第二笔的买入日 > 第一笔的卖出日）；
 *     4) 也可以只做 0 笔或 1 笔（"最多"是上限不是必须——只用一笔时 121 退化成立）。
 * ■ 求
 *   所能获得的最大利润。
 *
 * <p>■ 输入输出
 *   输入：int[] prices（1 ≤ length ≤ 10^5；0 ≤ prices[i] ≤ 10^5）
 *   输出：int（最大利润，≥0）
 *
 * <p>■ 示例逐条
 * <pre>
 * prices=[3,3,5,0,0,3,1,4] → 6（0 买 3 卖=3，1 买 4 卖=3；合计 6——两笔接力）
 * prices=[1,2,3,4,5]       → 4（单调涨：一笔 4 已最优，两笔拆开会丢（4−1)+(5−2)... 实际一笔最优）
 * prices=[7,6,4,3,1]       → 0（一路跌）
 * prices=[1]               → 0（单日）
 * prices=[1,2,4,2,5,7,2,4,9,0] → 17？（参考定：2+5+7 或 8+9 组合的接力最优——由暴力切分定）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Hard（页频 ★★★★——面试 Hard 中最高频股票题）
 * <p>【标签】动态规划(DP) · 状态机 · 环形拆分（对照参考）
 * <p>【题型】≤2 笔最大利润：五状态链 hold1→sell1→buy2→sell2（buy 态从上一笔 sell 出发——121 禁止的那条边在此合法！）
 * <p>【考点】状态机"链"结构（与 121 两态一环的图结构分野）；当天买卖的次序表达（同日 sell1 再 buy2 的可行性——LeetCode 定义 sell 后可同日 buy，即链内转移允许"接力"）；
 * <p>　　　　Hard 之上必有 O(n) 状态机解（123 若用 O(n²) 切分点枚举会 TLE——但暴力参考可用它做对拍）
 * <p>【关联】P121（≤1 笔——两态退化式）；P188（k 笔——状态数组化）；P122（无限笔——贪心全吃）
 *
 * <p>LCS 签名：maxProfit(int[] prices)。最多两笔交易（先买后卖、不重叠），求最大利润。
 */
public class P123BestTimeToBuyAndSellStockIII {

    /** 你的实现（五状态链：buy1→sell1→buy2→sell2，拓扑序滚动——地基 MIN/0 哨兵四件套）。 */
    public int maxProfit(int[] prices) {
        int buy1 = Integer.MIN_VALUE;
        int sell1 = 0;
        int buy2 = Integer.MIN_VALUE;
        int sell2 = 0;
        for (int price : prices) {
            buy1 = Math.max(buy1, -price);
            sell1 = Math.max(sell1, buy1 + price);
            buy2 = Math.max(buy2, sell1 - price);
            sell2 = Math.max(sell2, buy2 + price);
        }
        return sell2;
    }

    public static void main(String[] args) {
        P123BestTimeToBuyAndSellStockIII solver = new P123BestTimeToBuyAndSellStockIII();

        run(solver, new int[]{3, 3, 5, 0, 0, 3, 1, 4}, "官方例1 → 6（两笔接力）");
        run(solver, new int[]{1, 2, 3, 4, 5}, "官方例2 单调涨 → 4（一笔最优）");
        run(solver, new int[]{7, 6, 4, 3, 1}, "一路跌 → 0");
        run(solver, new int[]{1}, "单日 → 0");
        run(solver, new int[]{1, 2, 4, 2, 5, 7, 2, 4, 9, 0}, "多峰压力（121 用例的 123 版）→ 参考定");
        run(solver, new int[]{6, 1, 3, 2, 4, 7}, "两段涨幅接力 → 参考定（1-3 + 2-7）");
        run(solver, new int[]{2, 1, 2, 1, 2, 1, 2}, "锯齿 → 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：【切分点枚举】把两笔交易理解为"在前缀 [0..m) 做一笔 + 在后缀 [m..n) 做一笔"，
     * 对每个切分点 m 调用 121 的贪心求两半各自的单笔最优——O(n²) 但机制独立（图结构 vs 切分）。
     */
    private static int ref121One(int[] prices, int l, int r) {
        if (r - l < 1) return 0;             // 需要 ≥2 天才能一笔
        int minPrice = prices[l];
        int best = 0;
        for (int i = l + 1; i <= r; i++) {
            if (prices[i] < minPrice) minPrice = prices[i];
            else best = Math.max(best, prices[i] - minPrice);
        }
        return best;
    }

    private static int bruteForce(int[] prices) {
        int n = prices.length;
        int best = 0;
        for (int m = 0; m <= n; m++) {       // 切分：第一笔在 [0, m)，第二笔在 [m, n)
            best = Math.max(best, ref121One(prices, 0, m - 1) + ref121One(prices, m, n - 1));
        }
        return best;
    }

    private static void run(P123BestTimeToBuyAndSellStockIII solver, int[] prices, String label) {
        try {
            int got = solver.maxProfit(Arrays.copyOf(prices, prices.length));
            int want = bruteForce(Arrays.copyOf(prices, prices.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-30s prices=%-30s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(prices), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s prices=%-30s | TODO 未实现%n", label, Arrays.toString(prices));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s prices=%-30s | 异常: %s%n", label, Arrays.toString(prices), e);
        }
    }
}
