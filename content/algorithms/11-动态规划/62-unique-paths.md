# 62. 不同路径（Unique Paths）

频次 ★★★ · 难度 🟡 · 高频：全厂

## 题目

m×n 网格，从左上到右下每次只能右或下，求不同路径总数。

**示例**：
```
输入: m = 3, n = 7
输出: 28
```

## 思路

**组合数学**：总共需要走 m-1 步向下 + n-1 步向右，共 m+n-2 步，选其中 m-1 步向下：`C(m+n-2, m-1)`。

**DP**：`dp[i][j] = dp[i-1][j] + dp[i][j-1]`，与最小路径和模板一致。

## 代码

```java
// 组合数学版 O(min(m,n))
public int uniquePaths(int m, int n) {
    long ans = 1;                     // long 防中间溢出
    int k = Math.min(m - 1, n - 1);   // 取较小的，减少循环
    for (int i = 1; i <= k; i++) {
        ans = ans * (m + n - 2 - i + 1) / i;
    }
    return (int) ans;
}
```

```java
// DP 版
public int uniquePaths(int m, int n) {
    int[] dp = new int[n];
    Arrays.fill(dp, 1);
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[j] += dp[j - 1];
        }
    }
    return dp[n - 1];
}
```

## 复杂度

- **组合数学**：O(min(m,n)) 时间，O(1) 空间
- **DP**：O(m×n) 时间，O(n) 空间

## 边界条件

- m=1 或 n=1：只有一条路径

## 变式

- **[63. 不同路径 II](63-unique-paths-ii.md)**：含障碍物
- **[64. 最小路径和](64-minimum-path-sum.md)**：加权的同款模板

## 易错点

- 组合数计算时用 `long` 防中间溢出，边乘边除
- DP 版 dp 数组初始化为 1（第一行只有一条路径）

## 面试追问

- **组合数学法的推导？** 一共 m+n-2 步，选 m-1 步向下，等价于 n-1 步向右。答案 C(m+n-2, m-1)

## 关联题

- 同套路：[63. 不同路径 II](63-unique-paths-ii.md) —— 含障碍物
- 进阶：[64. 最小路径和](64-minimum-path-sum.md) —— 加权版
- 知识点：组合数学 vs DP 的适用场景见[动态规划](algorithms/11-动态规划/README.md)

