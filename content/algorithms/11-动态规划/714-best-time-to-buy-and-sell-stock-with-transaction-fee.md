# 714. 买卖股票的最佳时机含手续费（Best Time to Buy and Sell Stock with Transaction Fee）

频次 ★★★ · 难度 🟡 · 高频：快手

## 题目

不限交易次数，但每次买卖收固定 fee。求最大利润。

## 思路

**两状态 DP**：和 122 类似，多一个 fee 在卖出时扣。

- `hold`：持股最大利润
- `cash`：空仓最大利润

## 代码

```java
public int maxProfit(int[] prices, int fee) {
    int hold = -prices[0] - fee, cash = 0;   // 买入即扣手续费
    for (int i = 1; i < prices.length; i++) {
        cash = Math.max(cash, hold + prices[i]);  // 卖出 + 现金
        hold = Math.max(hold, cash - prices[i] - fee);
    }
    return cash;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- fee > 最大可能利润：hold 一直低于 0，cash = 0

## 变式

- **[122. 买卖股票 II](122-best-time-to-buy-and-sell-stock-ii.md)**：无手续费
- **[309. 冷冻期](309-best-time-to-buy-and-sell-stock-with-cooldown.md)**：三状态

## 易错点

- 手续费扣在**买入时**还是**卖出时**两种写法都可以，只要统一。这里选择买入时扣，卖出时就不扣了
- 初始化 `hold` 时已经减去 fee，后面的买入同理

## 面试追问

- **手续费在买入还是卖出扣有区别吗？** 数值一样，只是初始化和转移时一个符号的差异

## 关联题

- 同套路：[122. 买卖股票 II](122-best-time-to-buy-and-sell-stock-ii.md) —— 无手续费
- 进阶：[188. 买卖股票 IV](188-best-time-to-buy-and-sell-stock-iv.md) —— 限次数
- 知识点：状态机 DP 模板见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
