/**
 * LeetCode 121: 买卖股票的最佳时机 (Best Time to Buy and Sell Stock)
 * https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 动态规划(DP)
 * <p>【题型】线性扫描维护状态：边扫边记「到目前为止的最低买入价」，用它算今天卖出的利润
 * <p>【考点】只允许买卖一次；能否一趟 O(1) 状态求解，而非枚举买卖对 O(n²)
 * <p>【关联】{@link P53MaximumSubarray} —— 同为一趟扫描维护一个最优状态
 *
 * <p>给定数组 prices，prices[i] 是第 i 天的股价。你只能选某一天买入、之后某一天卖出各一次，
 * 求能获得的最大利润；若无法获利返回 0。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：枚举买入日 i、卖出日 j>i，求 max(prices[j]-prices[i])，O(n²)。
 *
 * <p>2. 关键观察：卖出日 j 的最大利润 = prices[j] - (j 之前出现过的最低价)。
 *    而「之前的最低价」可以随扫描一路维护，不必回头找。
 *
 * <p>3. 核心技巧：一趟扫描，minPrice = min(minPrice, prices[i])，
 *    best = max(best, prices[i] - minPrice)。只用两个变量。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)。
 *    边界：价格单调递减时无法获利，返回 0；单元素返回 0。
 */
public class P121BestTimeToBuyAndSellStock {

    public int maxProfit(int[] prices) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P121BestTimeToBuyAndSellStock p121 = new P121BestTimeToBuyAndSellStock();

        assertEqual(5, p121.maxProfit(new int[]{7, 1, 5, 3, 6, 4}), "Example 1");   // 买1卖6
        assertEqual(0, p121.maxProfit(new int[]{7, 6, 4, 3, 1}), "Monotone down → 0");
        assertEqual(0, p121.maxProfit(new int[]{5}), "Single day");
        assertEqual(2, p121.maxProfit(new int[]{2, 4}), "Two days up");
        assertEqual(3, p121.maxProfit(new int[]{2, 1, 4}), "Dip then rise");
        assertEqual(0, p121.maxProfit(new int[]{3, 3, 3}), "Flat");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(String.format("[FAIL] %s: expected %d, but got %d",
                testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
