# 1143. 最长公共子序列（Longest Common Subsequence）

频次 ★★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

两个字符串 text1、text2，找最长公共子序列（LCS，不要求连续）的长度。

**示例**：
```
输入: text1 = "abcde", text2 = "ace"
输出: 3  （"ace"）
```

## 思路

**二维 DP**：`dp[i][j]` 表示 text1 前 i 个字符和 text2 前 j 个字符的 LCS 长度。

- `text1[i-1] == text2[j-1]` → `dp[i][j] = dp[i-1][j-1] + 1`
- 否则 → `dp[i][j] = max(dp[i-1][j], dp[i][j-1])`

空间优化：滚动数组（当前行只依赖上一行和当前行左侧），降到 O(min(m,n))。

## 代码

```java
public int longestCommonSubsequence(String text1, String text2) {
    int m = text1.length(), n = text2.length();
    int[] dp = new int[n + 1];                // 滚动数组
    for (int i = 1; i <= m; i++) {
        int prev = 0;                          // dp[i-1][j-1]
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];                  // 保存旧值（下一列是 dp[i-1][j]）
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[j] = prev + 1;
            } else {
                dp[j] = Math.max(dp[j], dp[j - 1]);
            }
            prev = temp;                       // 用于下一列的 dp[i-1][j-1]
        }
    }
    return dp[n];
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(n)

## 边界条件

- 一个为空串：返回 0

## 变式

- **[72. 编辑距离](72-edit-distance.md)**：DP 转移增加一个维度（替换/插入/删除）
- **[300. 最长递增子序列](300-longest-increasing-subsequence.md)**：LIS 可转为 LCS 问题

## 易错点

- dp 数组下标对应的是**长度**（1-indexed），取字符用 `charAt(i-1)`
- 滚动数组的 `prev` 记录了 `dp[i-1][j-1]`，方向是 i 内层正向遍历

## 面试追问

- **打印 LCS 之一？** 用二维 dp 记录转移方向，从 (m,n) 回溯。但不要求滚动数组了

## 关联题

- 同套路：[72. 编辑距离](72-edit-distance.md) —— 同是二维 DP，多一个替换的选择
- 进阶：[300. 最长递增子序列](300-longest-increasing-subsequence.md) —— 一维 DP 可转为 LIS
- 知识点：二维 DP + 滚动数组优化见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
