---
topics:
  - 动态规划与贪心
techniques:
  - 贪心证明
---

# 122. 买卖股票的最佳时机 II（Best Time to Buy and Sell Stock II）

频次 ★★★★ · 难度 🟡 · 高频：美团

## 题目

数组 prices，第 i 天价格。不限制交易次数，但必须先卖再买，求最大利润。

**示例**：
```
输入: prices = [7,1,5,3,6,4]
输出: 7  （第 2 天买入第 3 天卖 4 + 第 4 天买入第 5 天卖 3）
```

## 思路

**贪心**：只要今天价格 > 昨天，就昨天买今天卖（把每天正的差额累加）。相当于平掉所有上升段。

`profit = sum(max(0, prices[i] - prices[i-1]))`

## 代码

```java
public int maxProfit(int[] prices) {
    int profit = 0;
    for (int i = 1; i < prices.length; i++) {
        if (prices[i] > prices[i - 1]) {
            profit += prices[i] - prices[i - 1];
        }
    }
    return profit;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 空数组/单元素：返回 0
- 全降序：profit = 0

## 变式

- **[121. 买卖股票 I](121-best-time-to-buy-and-sell-stock.md)**：限一次交易
- **[188. 买卖股票 IV](188-best-time-to-buy-and-sell-stock-iv.md)**：限 k 次
- **[309. 含冷冻期](309-best-time-to-buy-and-sell-stock-with-cooldown.md)**：卖后隔一天才能买

## 易错点

- 贪心的本质：把所有上升段**拆成一天天交易**，等价于不限次数的最优策略

## 面试追问

- **如果只能最多持有 1 股？** 本题已经假设了（先卖后买）。如果要同时持多股就是不同问题了

## 关联题

- 同套路：[121. 买卖股票 I](121-best-time-to-buy-and-sell-stock.md) —— 限一次
- 进阶：[309. 含冷冻期](309-best-time-to-buy-and-sell-stock-with-cooldown.md) —— 带冷却
- 知识点：股票系列 DP 状态机见[动态规划](动态规划与贪心.md)

