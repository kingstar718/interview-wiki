---
topics:
  - 动态规划与贪心
techniques:
  - 线性DP
---

# 746. 使用最小花费爬楼梯（Min Cost Climbing Stairs）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

数组 cost[i] 表示从第 i 阶向上爬的花费，可从第 0 或第 1 阶开始，每次爬 1 或 2 阶，求到达楼顶的最小总花费。

**示例**：
```
输入: cost = [10,15,20]
输出: 15  （从第 1 阶开始，爬 2 阶到顶，花费 15）
```

## 思路

**DP**：`dp[i]` 表示到达第 i 阶的最小花费。
`dp[i] = min(dp[i-1] + cost[i-1], dp[i-2] + cost[i-2])`

楼顶在数组末尾的下一个位置，即 `dp[n]` 是答案。可优化到 O(1) 空间（两个变量滚动）。

## 代码

```java
public int minCostClimbingStairs(int[] cost) {
    int n = cost.length;
    int a = 0, b = 0;              // dp[0], dp[1]（从 0 或 1 开始，花费为 0）
    for (int i = 2; i <= n; i++) {
        int c = Math.min(a + cost[i - 2], b + cost[i - 1]);
        a = b;
        b = c;
    }
    return b;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- cost 长度 2：直接取 min(cost[0], cost[1])
- 从 0 或 1 开始不花钱——dp[0]=dp[1]=0

## 变式

- **[70. 爬楼梯](70-climbing-stairs.md)**：不带 cost 的版本，dp[i]=dp[i-1]+dp[i-2]
- **三步问题**：每次可爬 1~3 阶，dp[i]=dp[i-1]+dp[i-2]+dp[i-3]

## 易错点

- 楼顶在 `cost.length` 位置（不是 `cost.length - 1`），dp 数组长度为 n+1
- dp[0] 和 dp[1] 初始化为 0（站在起点不花钱，花费用在"离开"时）

## 面试追问

- **如果花费在"到达"而不是"离开"？** 转移方程会变，讨论清楚题意再写

## 关联题

- 同套路：[70. 爬楼梯](70-climbing-stairs.md) —— 无 cost 基础版
- 进阶：[509. 斐波那契数](509-fibonacci-number.md) —— DP 入门
- 知识点：带权 DP 的滚动优化见[动态规划](动态规划与贪心.md)
