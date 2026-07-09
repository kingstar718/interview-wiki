---
topics:
  - 动态规划与贪心
---

# 188. 买卖股票的最佳时机 IV（Best Time to Buy and Sell Stock IV）

频次 ★★★ · 难度 🔴 · 高频：字节

## 题目

最多 k 次交易，求最大利润。

## 思路

**三维 DP 降二维**：`dp[j][0/1]` 表示已完成 j 笔交易后、当前空仓/持股的最大利润。

- `dp[j][0] = max(dp[j][0], dp[j][1] + prices[i])`（卖出完成一笔交易）
- `dp[j][1] = max(dp[j][1], dp[j-1][0] - prices[i])`（买入开启新交易）

当 k > n/2 时退化为不限次数（122 的贪心），否则 DP。

## 代码

```java
public int maxProfit(int k, int[] prices) {
    int n = prices.length;
    if (k > n / 2) {                          // 退化为不限次数
        int profit = 0;
        for (int i = 1; i < n; i++) {
            if (prices[i] > prices[i - 1]) profit += prices[i] - prices[i - 1];
        }
        return profit;
    }
    int[][] dp = new int[k + 1][2];
    for (int j = 0; j <= k; j++) dp[j][1] = Integer.MIN_VALUE;
    for (int price : prices) {
        for (int j = 1; j <= k; j++) {
            dp[j][0] = Math.max(dp[j][0], dp[j][1] + price);
            dp[j][1] = Math.max(dp[j][1], dp[j - 1][0] - price);
        }
    }
    return dp[k][0];
}
```

## 复杂度

- **时间**：O(k × n)
- **空间**：O(k)

## 边界条件

- k = 0：不允许交易，返回 0
- 退化为无限次（k > n/2）

## 变式

- **[123. 买卖股票 III](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-iii/)**：k=2 的限定版

## 易错点

- k > n/2 时退化为无限次，否则 DP 会超时
- dp 初始化 dp[0][1] = MIN_VALUE（不可能完成 0 笔却持股）

## 面试追问

- **为什么 k > n/2 等价于无限次？** 因为 n 天内最多 n/2 次完整交易（买-卖），k 超过上限时就无限制了

## 关联题

- 同套路：[122. 买卖股票 II](122-best-time-to-buy-and-sell-stock-ii.md) —— 不限次数
- 进阶：[309. 冷冻期](309-best-time-to-buy-and-sell-stock-with-cooldown.md) —— 状态机加一维
- 知识点：股票 DP 状态机模板见[动态规划](动态规划与贪心.md)

