# 63. 不同路径 II（Unique Paths II）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

m×n 网格，含障碍物（1 = 障碍），从左上到右下每次只能右或下，求路径总数。

**示例**：
```
输入: obstacleGrid = [[0,0,0],[0,1,0],[0,0,0]]
输出: 2
```

## 思路

**DP**：在 62 基础上加障碍判断。`dp[j] += dp[j-1]` 仅当该格不是障碍。障碍物处 `dp[j] = 0`。

## 代码

```java
public int uniquePathsWithObstacles(int[][] obstacleGrid) {
    int n = obstacleGrid[0].length;
    int[] dp = new int[n];
    dp[0] = 1;                               // 起点无障则初始为 1
    for (int[] row : obstacleGrid) {
        for (int j = 0; j < n; j++) {
            if (row[j] == 1) {
                dp[j] = 0;                   // 障碍物处路径数为 0
            } else if (j > 0) {
                dp[j] += dp[j - 1];
            }
        }
    }
    return dp[n - 1];
}
```

## 复杂度

- **时间**：O(m×n)
- **空间**：O(n)

## 边界条件

- 起点或终点是障碍：返回 0
- 全无障碍：退化为 62

## 变式

- **[62. 不同路径](62-unique-paths.md)**：无障碍版
- **[64. 最小路径和](64-minimum-path-sum.md)**：加权路径

## 易错点

- 起点 dp[0] 初始化为 `1` 只有第一格非障碍时才有意义。如果起点就是障碍应该在遍历前返回 0
- 障碍物处 `dp[j] = 0` 而不是 `dp[j] = dp[j-1]`

## 面试追问

- **如果障碍物动态变化怎么办？** 每步重新计算？需要不同结构。一般不会问到这个程度

## 关联题

- 同套路：[62. 不同路径](62-unique-paths.md) —— 无障版
- 进阶：[64. 最小路径和](64-minimum-path-sum.md) —— 加权路径
- 知识点：障碍物 DP 的初始化技巧见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
