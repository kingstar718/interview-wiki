---
topics:
  - 图论
techniques:
  - DFS
---

# 1020. 飞地的数量（Number of Enclaves）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

m×n 网格，1 是陆地、0 是水。飞地是那些无法通过四联通走到边界的陆地格子。求飞地数量。

**示例**：
```
输入: grid = [[0,0,0,0],[1,0,1,0],[0,1,1,0],[0,0,0,0]]
输出: 3  （中间 3 个 1 被包围）
```

## 思路

**逆向思维**：从边界上的 1 出发 DFS/BFS，标记为 0（沉没）。最后统计剩余 1 的数量。

与 130. 被围绕区域的区别：130 要翻转被包围的 O，1020 只统计被包围的陆地数量。

## 代码

```java
public int numEnclaves(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    // 从边界 1 出发沉没所有可达陆地的边界
    for (int i = 0; i < m; i++) {
        if (grid[i][0] == 1) dfs(grid, i, 0);
        if (grid[i][n - 1] == 1) dfs(grid, i, n - 1);
    }
    for (int j = 0; j < n; j++) {
        if (grid[0][j] == 1) dfs(grid, 0, j);
        if (grid[m - 1][j] == 1) dfs(grid, m - 1, j);
    }
    // 统计剩余未被沉没的 1
    int count = 0;
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == 1) count++;
        }
    }
    return count;
}

private void dfs(int[][] grid, int i, int j) {
    if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] == 0)
        return;
    grid[i][j] = 0;                              // 沉没
    dfs(grid, i + 1, j);
    dfs(grid, i - 1, j);
    dfs(grid, i, j + 1);
    dfs(grid, i, j - 1);
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(m × n) 递归栈

## 边界条件

- 空网格：返回 0
- 全 0：返回 0
- 全 1：边界 1 全沉没，返回 0

## 变式

- **[130. 被围绕的区域](130-surrounded-regions.md)**：翻转被包围区域（需要临时标记再恢复）
- **[200. 岛屿数量](200-number-of-islands.md)**：计数岛屿个数
- **[695. 岛屿的最大面积](695-max-area-of-island.md)**：求最大面积

## 易错点

- 与 130 的区别：130 需要保留边界连通区域（用 '#' 标记后恢复），1020 可以直接沉没边界连通区域（因为只需要统计剩余数）
- 沉没后直接统计剩余 1 的数量，不需要恢复
- 四边边界都要检查

## 面试追问

- **和 130 的区别？** 130 需要修改矩阵（翻转），1020 只需要统计。1020 可以直接沉没（因为不需要恢复），130 需要临时标记再恢复
- **BFS 怎么做？** 边界入队，扩散沉没，最后统计

## 关联题

- 同套路：[130. 被围绕的区域](130-surrounded-regions.md) —— 翻转版
- 进阶：[695. 岛屿的最大面积](695-max-area-of-island.md) —— 求面积
- 知识点：逆向思维 + 边界出发见[图](图论.md)
