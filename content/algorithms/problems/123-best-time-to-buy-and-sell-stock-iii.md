# 123. 买卖股票的最佳时机 III（Best Time to Buy and Sell Stock III）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里/腾讯

## 题目

最多完成 2 笔交易（一次买入 + 一次卖出 = 一笔交易），求最大利润。不能同时参与多笔交易（买之前必须卖掉）。

**示例**：
```
输入: prices = [3,3,5,0,0,3,1,4]
输出: 6  （第 4 天买 0，第 6 天卖 3，利润 3；第 7 天买 1，第 8 天卖 4，利润 3）
```

## 思路

**状态机 DP**：定义 5 个状态（或 `dp[i][k][0/1]` 三维）。5 个状态：
- `buy1`：第一次买入后的最大收益
- `sell1`：第一次卖出后的最大收益
- `buy2`：第二次买入后的最大收益
- `sell2`：第二次卖出后的最大收益

转移：
- `buy1 = max(buy1, -prices[i])`（第一次买入）
- `sell1 = max(sell1, buy1 + prices[i])`（第一次卖出）
- `buy2 = max(buy2, sell1 - prices[i])`（第二次买入）
- `sell2 = max(sell2, buy2 + prices[i])`（第二次卖出）

## 代码

```java
public int maxProfit(int[] prices) {
    int buy1 = Integer.MIN_VALUE, sell1 = 0;
    int buy2 = Integer.MIN_VALUE, sell2 = 0;
    for (int price : prices) {
        buy1 = Math.max(buy1, -price);           // 第一次买入
        sell1 = Math.max(sell1, buy1 + price);    // 第一次卖出
        buy2 = Math.max(buy2, sell1 - price);     // 第二次买入
        sell2 = Math.max(sell2, buy2 + price);    // 第二次卖出
    }
    return sell2;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 价格递减：不交易，返回 0
- 数组长度 < 2：返回 0
- 可以完成不足 2 笔交易：sell2 最终可能等于 sell1

## 变式

- **[121. 买卖股票 I](121-best-time-to-buy-and-sell-stock.md)**：只能交易 1 次，维护最低买入价
- **[122. 买卖股票 II](122-best-time-to-buy-and-sell-stock-ii.md)**：不限交易次数，贪心累加所有正差价
- **[188. 买卖股票 IV](188-best-time-to-buy-and-sell-stock-iv.md)**：最多 k 次交易，通用状态机 DP
- **[309. 含冷冻期](309-best-time-to-buy-and-sell-stock-with-cooldown.md)**：卖出后隔一天才能买
- **[714. 含手续费](714-best-time-to-buy-and-sell-stock-with-transaction-fee.md)**：每次卖出扣手续费

## 易错点

- buy1/buy2 初始化为 `Integer.MIN_VALUE`（或 `-prices[0]`），不能用 0
- 状态转移顺序不能错——必须是 buy1 → sell1 → buy2 → sell2 的顺序
- `sell1` 和 `sell2` 初始化为 0（不交易的最低收益）

## 面试追问

- **如果最多 k 次交易（188 题）？** 需要 `dp[i][k][0/1]` 三维 DP，或 2k+1 个状态
- **为什么用 5 个状态而不是 `dp[i][2][2]`？** 5 个状态是三维 DP 的展开，更直观且空间 O(1)

## 关联题

- 同套路：[188. 买卖股票 IV](188-best-time-to-buy-and-sell-stock-iv.md) —— 通用 k 次交易
- 入门：[121. 买卖股票 I](121-best-time-to-buy-and-sell-stock.md) —— 只交易 1 次
- 知识点：股票系列状态机 DP 见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)