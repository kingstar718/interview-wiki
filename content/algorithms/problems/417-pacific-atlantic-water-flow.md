---
topics:
  - 图论
techniques:
  - 图遍历
---

# 417. 太平洋大西洋水流（Pacific Atlantic Water Flow）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

m×n 矩阵，每个格子是海拔。左上/上边是太平洋，右下/右边是大西洋。水流从高往低（或等高）流，问哪些格子能同时流向两大洋。

**示例**：
```
输入: heights = [[1,2,2,3,5],
                 [3,2,3,4,4],
                 [2,4,5,3,1],
                 [6,7,1,4,5],
                 [5,1,1,2,4]]
输出: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]
```

## 思路

**反向 DFS**：从边界出发向内地逆流（`next >= cur` 时能逆流而上），记录"能流到太平洋"和"能流到大西洋"的格子，取交集。

为什么反向？正向流要枚举所有格子做 DFS 到边界，反向从边界出发一次 DFS 覆盖所有可达格子，效率更高。

## 代码

```java
private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

public List<List<Integer>> pacificAtlantic(int[][] heights) {
    int m = heights.length, n = heights[0].length;
    boolean[][] pac = new boolean[m][n];
    boolean[][] atl = new boolean[m][n];

    for (int i = 0; i < m; i++) {
        dfs(heights, pac, i, 0);        // 左边界（太平洋）
        dfs(heights, atl, i, n - 1);    // 右边界（大西洋）
    }
    for (int j = 0; j < n; j++) {
        dfs(heights, pac, 0, j);        // 上边界（太平洋）
        dfs(heights, atl, m - 1, j);    // 下边界（大西洋）
    }

    List<List<Integer>> res = new ArrayList<>();
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (pac[i][j] && atl[i][j]) {
                res.add(List.of(i, j));
            }
        }
    }
    return res;
}

private void dfs(int[][] h, boolean[][] reach, int i, int j) {
    reach[i][j] = true;
    for (int[] d : DIRS) {
        int ni = i + d[0], nj = j + d[1];
        if (ni >= 0 && ni < h.length && nj >= 0 && nj < h[0].length
                && !reach[ni][nj] && h[ni][nj] >= h[i][j]) {
            dfs(h, reach, ni, nj);
        }
    }
}
```

## 复杂度

- **时间**：O(m×n) —— 每个格子至多被两个方向的 DFS 各访问一次
- **空间**：O(m×n) —— 两个 boolean 矩阵

## 边界条件

- 单行/单列：边界 DFS 互相重叠，正常取交集
- 一格：同时是太平洋和大西洋边界，返回 `[[0,0]]`
- 全等海拔：全量连通，所有格子都满足

## 变式

- **[200. 岛屿数量](200-number-of-islands.md)**：同网格 DFS 但改为单向沉没
- **[130. 被围绕的区域](https://leetcode.cn/problems/surrounded-regions/)**：也是从边界反向 DFS

## 易错点

- **反向流的比较条件 `h[ni][nj] >= h[i][j]`**：因为是从边界逆流往高处走，所以相邻格子不低于当前格子才能流过去。方向感容易搞反
- 两个 boolean 数组分开标记，不要混用
- 方向数组复用第 200 题的 DIRS 四方向

## 面试追问

- **为什么反向 DFS 比正向快？** 正向每个格子做一次 DFS 到边界，重复计算多。反向从边界只做 2×(m+n) 次 DFS，且能"记忆化"所有可达格子
- **BFS 能写吗？** 能，用队列代替递归，思路完全一样。DFS 代码更简洁

## 关联题

- 同套路：[200. 岛屿数量](200-number-of-islands.md) —— 网格 DFS
- 进阶：[787. K 站中转内最便宜的航班](787-cheapest-flights-within-k-stops.md) —— 从网格图到加权图
- 知识点：反向思维在算法中的应用见[图](图论.md)

