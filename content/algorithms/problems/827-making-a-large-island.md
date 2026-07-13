# 827. 最大人工岛（Making A Large Island）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

m×n 网格，1 是陆地、0 是水。最多可以将一个 0 变成 1，求改变后最大岛屿面积。

**示例**：
```
输入: grid = [[1,0],[0,1]]
输出: 3  （将 (0,1) 或 (1,0) 的 0 变 1，连接两个 1 到新岛屿面积 3）
```

## 思路

**两遍遍历**：

1. **第一遍**：给每个岛屿编号（从 2 开始），用 DFS/BFS 计算每个岛屿的面积，存入 `area[编号]`。
2. **第二遍**：遍历每个 0 格子，检查它四联通方向的岛屿编号（去重），累加这些岛屿的面积 + 1（填海的格子），更新 max。

注意：如果全 1（没有 0），直接返回 m×n。

## 代码

```java
public int largestIsland(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    int[] area = new int[m * n + 2];             // 岛屿编号 → 面积
    int index = 2;                                // 岛屿编号从 2 开始
    int max = 0;
    // 第一遍：给岛屿编号并计算面积
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == 1) {
                area[index] = dfs(grid, i, j, index);
                max = Math.max(max, area[index]);
                index++;
            }
        }
    }
    // 第二遍：遍历每个 0，计算合并周围岛屿的面积
    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == 0) {
                Set<Integer> seen = new HashSet<>();
                int cur = 1;                       // 填海格子
                for (int[] d : dirs) {
                    int ni = i + d[0], nj = j + d[1];
                    if (ni >= 0 && ni < m && nj >= 0 && nj < n && grid[ni][nj] > 1) {
                        int idx = grid[ni][nj];
                        if (seen.add(idx)) cur += area[idx];
                    }
                }
                max = Math.max(max, cur);
            }
        }
    }
    return max == 0 ? m * n : max;                  // 全 1 的情况
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(m × n)

## 边界条件

- 全 1：返回 m×n（没有 0 可填）
- 全 0：返回 1（填一个 0）
- 多个 0 连接同一个岛屿：用 HashSet 去重

## 变式

- **[695. 岛屿的最大面积](695-max-area-of-island.md)**：不填海，直接求最大岛屿面积
- **[200. 岛屿数量](200-number-of-islands.md)**：计数岛屿个数

## 易错点

- 岛屿编号从 2 开始（避免和 1 混淆），面积数组大小为 `m×n + 2`
- 第二遍遍历 0 时，四方向的岛屿编号要去重（可能多个方向属于同一个岛屿）
- 全 1 的特殊处理：`max == 0` 时返回 `m×n`

## 面试追问

- **为什么给岛屿编号？** 编号后可以快速通过 `area[编号]` 获取面积，避免重复 DFS
- **如果允许多次填海？** 变成连通分量合并问题，用并查集更合适

## 关联题

- 同套路：[695. 岛屿的最大面积](695-max-area-of-island.md) —— 基础版
- 进阶：[200. 岛屿数量](200-number-of-islands.md) —— 计数版
- 知识点：岛屿编号 + 连通分量合并见[图](algorithms/09-图/README.md)

---

[← 返回训练计划](社招算法训练计划.md)