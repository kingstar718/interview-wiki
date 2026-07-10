---
topics:
  - 动态规划与贪心
techniques:
  - 二维DP
---

# 64. 最小路径和（Minimum Path Sum）

频次 ★★★★ · 难度 🟡 · 高频：美团

## 题目

m×n 网格，从左上到右下每次只能下或右，求路径数字之和的最小值。

**示例**：
```
输入: grid = [[1,3,1],[1,5,1],[4,2,1]]
输出: 7  （1→3→1→1→1）
```

## 思路

**二维 DP（原地修改）**：`grid[i][j] += min(上边, 左边)`。

第一行只能从左来，第一列只能从上面来。

## 代码

```java
public int minPathSum(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    for (int i = 1; i < m; i++) grid[i][0] += grid[i - 1][0];
    for (int j = 1; j < n; j++) grid[0][j] += grid[0][j - 1];
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            grid[i][j] += Math.min(grid[i - 1][j], grid[i][j - 1]);
        }
    }
    return grid[m - 1][n - 1];
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(1) —— 原地修改

## 边界条件

- 1×1：返回 grid[0][0]
- 单行/单列：只有一条路径

## 变式

- **[62. 不同路径](62-unique-paths.md)**：方案数而不是和
- **[63. 不同路径 II](63-unique-paths-ii.md)**：含障碍物
- **[120. 三角形最小路径和](https://leetcode.cn/problems/triangle/)**：三角形网格版

## 易错点

- 带下标 `i-1` 或 `j-1` 时要先处理第一行/列边界
- 原地修改会改变原数组，如果面试官不希望改原数组就新开 dp 数组

## 面试追问

- **如果只能走右或下，为什么贪心（每步选较小的）不行？** 因为局部最优不保证全局最优

## 关联题

- 同套路：[62. 不同路径](62-unique-paths.md) —— 同模板但求和
- 进阶：[72. 编辑距离](72-edit-distance.md) —— 二维 DP 的三方向转移
- 知识点：网格 DP 模板见[动态规划](动态规划与贪心.md)

