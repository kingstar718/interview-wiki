# 994. 腐烂的橘子（Rotting Oranges）

频次 ★★★★ · 难度 🟡 · 高频：美团/字节

## 题目

m×n 网格，0=空、1=鲜橘、2=烂橘。每分钟烂橘会四方向感染鲜橘。求全部腐烂的最短分钟数，不可能则返回 -1。

**示例**：
```
输入: [[2,1,1],[1,1,0],[0,1,1]]
输出: 4
```

## 思路

**多源 BFS**：所有初始烂橘同时作为起点入队，一层层扩散。每层（每分钟）将当前层所有烂橘的邻居感染，已经腐烂的标记为 2。

最后检查是否还有鲜橘，有则返回 -1，否则返回 BFS 的层数。

## 代码

```java
private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

public int orangesRotting(int[][] grid) {
    int m = grid.length, n = grid[0].length;
    Queue<int[]> q = new ArrayDeque<>();
    int fresh = 0;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == 2) q.offer(new int[]{i, j});
            else if (grid[i][j] == 1) fresh++;
        }
    }

    if (fresh == 0) return 0;     // 没有鲜橘
    int minutes = 0;
    while (!q.isEmpty() && fresh > 0) {
        int size = q.size();
        minutes++;
        for (int k = 0; k < size; k++) {
            int[] cur = q.poll();
            for (int[] d : DIRS) {
                int ni = cur[0] + d[0], nj = cur[1] + d[1];
                if (ni >= 0 && ni < m && nj >= 0 && nj < n && grid[ni][nj] == 1) {
                    grid[ni][nj] = 2;               // 感染
                    fresh--;
                    q.offer(new int[]{ni, nj});
                }
            }
        }
    }
    return fresh == 0 ? minutes : -1;
}
```

## 复杂度

- **时间**：O(m×n)
- **空间**：O(m×n) —— 队列最坏存储所有烂橘

## 边界条件

- 无鲜橘：立即返回 0
- 无烂橘但有鲜橘：fresh > 0 且队列为空 → 返回 -1
- 有鲜橘永远够不着（被 0 包围的 1）：fresh 最后不为 0 → -1

## 变式

- **[200. 岛屿数量](200-number-of-islands.md)**：DFS 直接标记，不用计时
- **[542. 01 矩阵](https://leetcode.cn/problems/01-matrix/)**：多源 BFS 求每个格子到 0 的距离
- **[1162. 地图分析](https://leetcode.cn/problems/as-far-from-land-as-possible/)**：多源 BFS + 取最大距离

## 易错点

- **分钟计数时机**：BFS 每处理完完整的一层才加一分钟（在当前层感染的结果是下一分钟才生效）。`minutes++` 在每层开始时或结束时都可以，但要和"初始状态"对齐——初始烂橘在第 0 分钟已烂
- `minutes++` 放在 `while` 里每层加一次，而不是每 pop 一个节点加一次
- `fresh == 0` 时才退出 while 循环，或者 BFS 扩散完毕但 fresh 还有剩 → -1
- 入队时立即标记为烂（2），防止同一分钟重复入队

## 面试追问

- **为什么用 BFS 不用 DFS？** 因为是"最短时间"问题，BFS 天然按层扩散，第一次到达就是最短时间。DFS 需要算所有路径再取 min
- **如果每分钟腐烂时间不同（有的橘子烂得快）？** 变优先级队列（Dijkstra），距离权重不为 1

## 关联题

- 同套路：[200. 岛屿数量](200-number-of-islands.md) —— 网格图遍历，DFS 版本
- 进阶：[207. 课程表](207-course-schedule.md) —— 从网格图换到邻接表的图遍历
- 知识点：多源 BFS 模板见[图](algorithms/09-图/README.md)

