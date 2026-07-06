# 121. Best Time to Buy and Sell Stock (Easy)

## 题目

给定一个数组 `prices`，其中 `prices[i]` 表示某支股票第 `i` 天的价格。你最多只能完成一笔交易（买入一次 + 卖出一次），求最大利润。注意：不能在同一天买入和卖出，且必须先买入再卖出。

**示例**：
```
输入: prices = [7,1,5,3,6,4]
输出: 5
解释: 第 2 天买入(价格=1)，第 5 天卖出(价格=6)，利润 = 5
```

## 思路

一次遍历，维护两个变量：
- `minPrice`：到当前位置的最低价格（最佳买入点）
- `maxProfit`：到当前位置的最大利润

遍历时更新最低价，并用当前价格减去最低价更新最大利润。本质上是在找 `prices[j] - prices[i]` 的最大值（j > i）。

## 代码

```java
public int maxProfit(int[] prices) {
    int minPrice = Integer.MAX_VALUE;
    int maxProfit = 0;
    for (int price : prices) {
        if (price < minPrice) {
            minPrice = price;
        } else if (price - minPrice > maxProfit) {
            maxProfit = price - minPrice;
        }
    }
    return maxProfit;
}
```

## 复杂度

- **时间**：O(n) — 遍历一次
- **空间**：O(1) — 两个变量

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
