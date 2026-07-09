---
topics:
  - 动态规划与贪心
---

# 309. 买卖股票的最佳时机含冷冻期（Best Time to Buy and Sell Stock with Cooldown）

频次 ★★★ · 难度 🟡 · 高频：阿里

## 题目

不限交易次数，卖出后第二天不能买入（冷冻期 1 天）。

## 思路

**状态机 DP**：三个状态

- `hold`：持股最大利润
- `sold`：刚卖出（第二天冷却）最大利润
- `rest`：空仓可买（冷冻期已过）最大利润

## 代码

```java
public int maxProfit(int[] prices) {
    int hold = Integer.MIN_VALUE, sold = 0, rest = 0;
    for (int p : prices) {
        int prevSold = sold;
        sold = hold + p;                    // 卖出
        hold = Math.max(hold, rest - p);    // 买入或继续持有
        rest = Math.max(rest, prevSold);    // 冷冻或继续空仓
    }
    return Math.max(sold, rest);
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 空数组/单元素：返回 0

## 变式

- **[122. 买卖股票 II](122-best-time-to-buy-and-sell-stock-ii.md)**：无冷冻期
- **[714. 含手续费](714-best-time-to-buy-and-sell-stock-with-transaction-fee.md)**：每次交易扣手续费

## 易错点

- `rest` 取 `max(rest, prevSold)`：冷冻期结束后从 sold 转移过来，同时保持自身（空仓继续空仓）
- 初始化 `hold = MIN_VALUE`：还没买入时不能卖出

## 面试追问

- **状态机的好处？** 每个状态只依赖前一步，空间 O(1)，天然适合流式处理

## 关联题

- 同套路：[122. 买卖股票 II](122-best-time-to-buy-and-sell-stock-ii.md) —— 两状态
- 进阶：[714. 含手续费](714-best-time-to-buy-and-sell-stock-with-transaction-fee.md) —— 三状态变体
- 知识点：状态机 DP 见[动态规划](动态规划与贪心.md)

