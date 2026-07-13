# 463. 岛屿的周长（Island Perimeter）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

m×n 网格，1 是陆地、0 是水，只有一个岛屿（无湖），求岛屿周长。

**示例**：
```
输入:
[[0,1,0,0],
 [1,1,1,0],
 [0,1,0,0],
 [1,1,0,0]]
输出: 16
```

## 思路

**解法 1：遍历计数** — 每个陆地格子贡献 4 条边，但相邻陆地会共享边。遍历每个陆地格子：周长 += 4 - 相邻陆地数（上下左右）。

**解法 2：DFS** — 遇到陆地格子时，边界和水边贡献 1 条边，相邻陆地递归继续。

解法 1（数学）最简单。

## 代码

```java
// 解法 1：遍历计数
public int islandPerimeter(int[][] grid) {
    int perimeter = 0;
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == 1) {
                perimeter += 4;
                if (i > 0 && grid[i - 1][j] == 1) perimeter -= 2;  // 上邻
                if (j > 0 && grid[i][j - 1] == 1) perimeter -= 2;  // 左邻
            }
        }
    }
    return perimeter;
}

// 解法 2：DFS
public int islandPerimeter(int[][] grid) {
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == 1) {
                return dfs(grid, i, j);
            }
        }
    }
    return 0;
}

private int dfs(int[][] grid, int i, int j) {
    // 边界或水边：贡献 1 条边
    if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] == 0)
        return 1;
    if (grid[i][j] == -1) return 0;           // 已访问
    grid[i][j] = -1;
    return dfs(grid, i + 1, j) + dfs(grid, i - 1, j)
         + dfs(grid, i, j + 1) + dfs(grid, i, j - 1);
}
```

## 复杂度

- **遍历法**：时间 O(m×n)，空间 O(1)
- **DFS**：时间 O(m×n)，空间 O(m×n) 递归栈

## 边界条件

- 空网格：返回 0
- 单格子：返回 4
- 题目保证只有一个岛屿，无湖

## 变式

- **[200. 岛屿数量](200-number-of-islands.md)**：计数岛屿个数
- **[695. 岛屿的最大面积](695-max-area-of-island.md)**：求最大岛屿面积

## 易错点

- 遍历法中，每个相邻边被两个陆地格子共享，所以每发现一个相邻就减 2（不是减 1）
- 只检查上邻和左邻（避免重复减），下邻和右邻会在后续遍历时处理
- DFS 法中，边界和水的判断返回 1（贡献一条边），已访问返回 0

## 面试追问

- **为什么遍历法每相邻减 2？** 相邻意味着两个格子共享一条边，这条边对两个格子都不贡献周长，所以从总数中减去 2
- **DFS 法和遍历法哪个好？** 遍历法 O(1) 空间更优，DFS 展示图论思维

## 关联题

- 同套路：[200. 岛屿数量](200-number-of-islands.md) —— 沉没岛屿
- 进阶：[695. 岛屿的最大面积](695-max-area-of-island.md) —— 求面积
- 知识点：网格遍历（数学法 vs DFS）见[图](algorithms/09-图/README.md)

---

[← 返回训练计划](社招算法训练计划.md)