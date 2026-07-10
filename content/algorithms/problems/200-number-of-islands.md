---
topics:
  - 图论
techniques:
  - 图遍历
---

# 200. 岛屿数量（Number of Islands）

频次 ★★★★★ · 难度 🟡 · 高频：全厂

## 题目

m×n 网格，'1' 是陆地、'0' 是水，四联通，求岛屿数量。

**示例**：
```
输入:
11110
11010
11000
00000
输出: 1
```

## 思路

**DFS 沉没法**：遍历网格，每遇到一个 '1' 就计数 +1，然后递归把和它四联通的 '1' 全改成 '0'（"沉没"该岛）。

BFS 或并查集也行，但 DFS 最简洁。

## 代码

```java
public int numIslands(char[][] grid) {
    if (grid.length == 0) return 0;
    int m = grid.length, n = grid[0].length;
    int count = 0;
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == '1') {
                count++;
                dfs(grid, i, j);
            }
        }
    }
    return count;
}

private void dfs(char[][] grid, int i, int j) {
    if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] == '0')
        return;
    grid[i][j] = '0';                     // 沉没
    dfs(grid, i + 1, j);
    dfs(grid, i - 1, j);
    dfs(grid, i, j + 1);
    dfs(grid, i, j - 1);
}
```

## 复杂度

- **时间**：O(m×n) —— 每个格子至多访问一次
- **空间**：O(m×n) 最坏 —— 全 '1' 时递归栈深度 m×n

## 边界条件

- 空网格：返回 0
- 全 0 / 全 1：返回 0 / 1
- 单行/单列：正常 DFS
- 网格很大（1000×1000）：递归可能栈溢出，改 BFS 或显式栈

## 变式

- **[695. 岛屿的最大面积](https://leetcode.cn/problems/max-area-of-island/)**：沉没时记录面积，取 max
- **[130. 被围绕的区域](https://leetcode.cn/problems/surrounded-regions/)**：从边界 DFS 标记 'O'，然后翻转内部
- **[463. 岛屿的周长](https://leetcode.cn/problems/island-perimeter/)**：遍历 '1' 时判断四边是否为边界或水

## 易错点

- **输入是 char[][] 不是 int[][]**：比较时用 `'1'` 和 `'0'` 别写数字
- DFS 沉没后不需要恢复（修改原数组），和回溯的"恢复现场"不同——因为"访问过"就是永久标记
- 四联通（上下左右）不要写成八联通（加对角线）

## 面试追问

- **BFS 怎么写？** 用队列存坐标，每遇到 '1' 就 BFS 四方向扩散并标记。递达复杂度相同，适合大网格防栈溢出
- **并查集怎么做？** 每个 '1' 格子看做一个节点，与四联通方向的 '1' union，最后统计根的个数。虽然代码长，但展示了对并查集的理解

## 关联题

- 同套路：[994. 腐烂的橘子](994-rotting-oranges.md) —— 多源 BFS 版"感染"问题
- 进阶：[207. 课程表](207-course-schedule.md) —— 图的遍历从网格换到邻接表
- 知识点：网格图的 DFS 遍历模板见[图](图论.md)

