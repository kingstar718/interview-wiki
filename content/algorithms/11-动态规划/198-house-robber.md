# 198. 打家劫舍（House Robber）

频次 ★★★★★ · 难度 🟢 · 高频：全厂

## 题目

一排房屋，每个有现金。不能偷相邻的，求最大偷盗金额。

**示例**：
```
输入: nums = [2,7,9,3,1]
输出: 12  （偷 2+9+1）
```

## 思路

**一维 DP**：到第 i 间房时，最大金额 = max(偷这间 + i-2 的最佳，不偷这间 = i-1 的最佳)。

`dp[i] = max(dp[i-1], dp[i-2] + nums[i])`

空间优化：滚动两个变量代替数组。

## 代码

```java
public int rob(int[] nums) {
    int prev2 = 0, prev1 = 0;    // dp[i-2], dp[i-1]
    for (int n : nums) {
        int cur = Math.max(prev1, prev2 + n);
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 空数组：返回 0
- 单房屋：返回该房屋金额

## 变式

- **[213. 打家劫舍 II](213-house-robber-ii.md)**：首尾相连成环，分两趟（去头/去尾）取 max
- **[337. 打家劫舍 III](https://leetcode.cn/problems/house-robber-iii/)**：树形 DP

## 易错点

- `dp[i]` 初始化 **全部为 0** 而非其它值——因为金额可能为 0 但不会为负
- `prev2` 初始 0 是状态定义使然（没房屋时金额 = 0）

## 面试追问

- **如果房屋排成环？** 拆成两个子问题：偷第一家不偷最后一家 / 偷最后一家不偷第一家，见 213

## 关联题

- 同套路：[213. 打家劫舍 II](213-house-robber-ii.md) —— 环形版
- 进阶：[300. 最长递增子序列](300-longest-increasing-subsequence.md) —— 一维 DP 但转移需向前遍历
- 知识点：一维 DP 的"选 vs 不选"模式见[动态规划](algorithms/11-动态规划/README.md)

