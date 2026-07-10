---
topics:
  - 图论
techniques:
  - DFS
---

# 695. 岛屿的最大面积（Max Area of Island）

频次 ★★★★ · 难度 🟡 · 高频：字节

## 题目

m×n 网格，`1` 为陆地、`0` 为水，四联通，求最大岛屿面积（即连通 `1` 的数量）。

**示例**：
```
输入:
[[0,0,1,0,0,0,0,1,0,0,0,0,0],
 [0,0,0,0,0,0,0,1,1,1,0,0,0],
 [0,1,1,0,1,0,0,0,0,0,0,0,0],
 [0,1,0,0,1,1,0,0,1,0,1,0,0],
 [0,1,0,0,1,1,0,0,1,1,1,0,0],
 [0,0,0,0,0,0,0,0,0,0,1,0,0],
 [0,0,0,0,0,0,0,1,1,1,0,0,0],
 [0,0,0,0,0,0,0,1,1,0,0,0,0]]
输出: 6
```

## 思路

**DFS 沉没法**：遍历网格，遇到 `1` 就开始 DFS，递归地访问四联通相邻的 `1`，同时将访问过的格子标记为 `0`（沉没），递归过程累加面积。取所有岛屿面积的最大值。

## 代码

```java
public int maxAreaOfIsland(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    int max = 0;
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == 1) {
                max = Math.max(max, dfs(grid, i, j));
            }
        }
    }
    return max;
}

private int dfs(int[][] grid, int i, int j) {
    if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] == 0)
        return 0;
    grid[i][j] = 0;          // 沉没
    int area = 1;
    area += dfs(grid, i + 1, j);
    area += dfs(grid, i - 1, j);
    area += dfs(grid, i, j + 1);
    area += dfs(grid, i, j - 1);
    return area;
}
```

## 复杂度

- **时间**：O(m × n) — 每个格子至多访问一次
- **空间**：O(m × n) 最坏 — 全 `1` 时递归栈深度 m×n

## 边界条件

- 空网格：`m == 0` 直接返回 0
- 全 0：max 保持 0
- 全 1：递归栈深可能溢出（大网格建议 BFS 或显式栈）
- 单行/单列：正常 DFS

## 变式

- **[200. 岛屿数量](200-number-of-islands.md)** —— 只计数不计数面积
- **[130. 被围绕的区域](https://leetcode.cn/problems/surrounded-regions/)** —— 从边界 DFS 标记
- **[463. 岛屿的周长](https://leetcode.cn/problems/island-perimeter/)** —— 遍历时判断四边

## 易错点

- **输入是 `int[][]`**：比较用 `== 1` 而不是 `== '1'`（和 [200. 岛屿数量](200-number-of-islands.md) 的 `char[][]` 区分）。
- DFS 沉没后不需要恢复现场（和回溯不同），因为"访问过"是永久标记。
- 四联通不要写成八联通（即不加对角线方向）。
- 面积累加：`area = 1 + 四个方向递归返回的和`，初始的 1 表示当前格子。

## 面试追问

- **BFS 怎么写？** 队列存坐标，每遇到 `1` 就 BFS 扩散，同时记录面积。好处是防栈溢出，适合大网格。
- **怎么改成求最大岛屿的周长？** 遍历每个 `1`，检查四边是否为边界或水，累加。
- **如果要求"对角联通也算"呢？** 八联通 DFS，方向数组增加 4 个对角线方向

## 关联题

- 同套路：[200. 岛屿数量](200-number-of-islands.md) —— 网格 DFS 遍历模板
- 进阶：[130. 被围绕的区域](https://leetcode.cn/problems/surrounded-regions/) —— 边界 DFS 标记法
- 知识点：网格图 DFS 模板见[图论](图论.md)
