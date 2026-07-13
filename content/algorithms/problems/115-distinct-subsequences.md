# 115. 不同的子序列（Distinct Subsequences）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

给定字符串 s 和 t，求 s 的子序列中等于 t 的个数。

**示例**：
```
输入: s = "rabbbit", t = "rabbit"
输出: 3  （s 中有 3 个不同的子序列 "rabbit"）
```

## 思路

**二维 DP**：`dp[i][j]` 表示 s[0..i-1] 中出现 t[0..j-1] 的次数。

- `s[i-1] == t[j-1]`：`dp[i][j] = dp[i-1][j-1] + dp[i-1][j]`
  - `dp[i-1][j-1]`：用 s[i-1] 去匹配 t[j-1]
  - `dp[i-1][j]`：不用 s[i-1]，跳过它
- `s[i-1] != t[j-1]`：`dp[i][j] = dp[i-1][j]`（只能跳过 s[i-1]）

初始化：`dp[i][0] = 1`（空 t 是任何 s 的子序列，出现 1 次），`dp[0][j] = 0`（j > 0）。

## 代码

```java
public int numDistinct(String s, String t) {
    int m = s.length(), n = t.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = 1;   // 空 t 出现 1 次
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s.charAt(i - 1) == t.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + dp[i - 1][j];
            } else {
                dp[i][j] = dp[i - 1][j];
            }
        }
    }
    return dp[m][n];
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(m × n)，可优化到 O(n) 滚动数组

## 边界条件

- m < n：返回 0（s 比 t 短，不可能匹配）
- t 为空：返回 1
- 结果可能很大，LeetCode 保证在 int 范围内

## 变式

- **[392. 判断子序列](392-is-subsequence.md)**：只判断是否存在（boolean），双指针 O(n)
- **[940. 不同的子序列 II](https://leetcode.cn/problems/distinct-subsequences-ii/)**：统计 s 的所有不同子序列个数（不指定 t）

## 易错点

- `dp[i][0] = 1` 是基值：空字符串 t 在任何 s 中作为子序列出现恰好 1 次
- 匹配时是 `dp[i-1][j-1] + dp[i-1][j]`，不是 `dp[i-1][j-1] + 1`——计数 DP 不是求最值
- 不匹配时只需 `dp[i-1][j]`，不需要 `dp[i][j-1]`（t 的字符必须全部匹配，不能跳过）

## 面试追问

- **和 392 的区别？** 392 只判断是否存在（双指针 O(n)），115 求出现的次数（二维 DP O(mn)）
- **空间优化？** 滚动数组：`dp[j]` 倒序更新，`dp[0] = 1` 不变

## 关联题

- 同套路：[392. 判断子序列](392-is-subsequence.md) —— 判断版
- 进阶：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— 二维 DP 同款
- 知识点：计数型二维 DP 见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)