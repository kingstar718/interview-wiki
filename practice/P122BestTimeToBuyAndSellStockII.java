import java.util.Arrays;

/**
 * LeetCode 122: 买卖股票的最佳时机 II (Best Time to Buy and Sell Stock II)
 * https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-ii/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · 数组 · DP
 * <p>【题型】不限制交易次数：所有"上涨段"全吃掉——贪心收集正差价
 * <p>【考点】profit += max(0, prices[i] - prices[i-1])；拆/合等价（连续涨 = 一次长交易利润不变）；与 121 的规则差
 * <p>【关联】P121（只能一笔：收集器不适用，要一次峰谷 max）；P123（至多两笔，DP 味）
 *
 * <p>给 prices（第 i 天股价）。可完成多笔交易（必须先卖再买；同时最多持一股），
 * 返回最大利润。
 *
 * <p>示例：[7,1,5,3,6,4] → 7（1 买 5 卖 + 3 买 6 卖）；[1,2,3,4,5] → 4；[7,6,4,3,1] → 0
 * <p>约束：1 &lt;= prices.length &lt;= 3*10^4；0 &lt;= prices[i] &lt;= 10^4
 */
public class P122BestTimeToBuyAndSellStockII {

    /** 你的实现（正差收集器 + length<1 防御）。 */
    public int maxProfit(int[] prices) {
        int profit = 0;
        if (prices.length < 1) {
            return 0;
        }
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    public static void main(String[] args) {
        P122BestTimeToBuyAndSellStockII solver = new P122BestTimeToBuyAndSellStockII();

        run(solver, new int[]{7, 1, 5, 3, 6, 4}, "示例1 官方 → 7");
        run(solver, new int[]{1, 2, 3, 4, 5}, "示例2 全升 → 4（连续涨=一次长交易）");
        run(solver, new int[]{7, 6, 4, 3, 1}, "示例3 全降 → 0（一个正差都没有）");
        run(solver, new int[]{5}, "单日 → 0");
        run(solver, new int[]{2, 4}, "两日涨 → 2");
        run(solver, new int[]{2, 1, 4}, "先降后升 → 3");
        run(solver, new int[]{3, 3, 3}, "全平 → 0");
        run(solver, new int[]{1, 5, 2, 6}, "两段独立上涨 → 4+4=8");
        run(solver, new int[]{5, 1, 6, 0, 2}, "两谷两峰 → 5+2=7");
        run(solver, new int[]{6, 1, 3, 2, 4, 7}, "交替起伏 → (3-1)+(7-2)=7");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举每个交易日"持/不持"状态机（持=可卖出或继续持有），穷举利润路径。 */
    private static int bruteForce(int[] prices) {
        // dp：未持有/持有 两种状态，逐日转移（这也是本题 DP 正装，留作 DP 章伏笔）
        int no = 0, own = -prices[0];
        for (int i = 1; i < prices.length; i++) {
            int newNo = Math.max(no, own + prices[i]);      // 今天不持：昨天不持 or 今天卖出
            int newOwn = Math.max(own, no - prices[i]);     // 今天持有：昨天持有 or 今天买入
            no = newNo;
            own = newOwn;
        }
        return no;
    }

    private static void run(P122BestTimeToBuyAndSellStockII solver, int[] prices, String label) {
        try {
            int got = solver.maxProfit(Arrays.copyOf(prices, prices.length));
            int want = bruteForce(Arrays.copyOf(prices, prices.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-28s prices=%-26s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(prices), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s prices=%-26s | TODO 未实现%n", label, Arrays.toString(prices));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s prices=%-26s | 异常: %s%n", label, Arrays.toString(prices), e);
        }
    }
}
