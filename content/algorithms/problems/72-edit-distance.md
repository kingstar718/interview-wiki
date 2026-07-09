---
topics:
  - 动态规划与贪心
---

# 72. 编辑距离（Edit Distance）

频次 ★★★★★ · 难度 🔴 · 高频：字节/阿里/腾讯

## 题目

将 word1 转成 word2，允许三种操作：插入、删除、替换一个字符。求最少操作次数。

**示例**：
```
输入: word1 = "horse", word2 = "ros"
输出: 3  （horse → rorse → rose → ros）
```

## 思路

**二维 DP**：`dp[i][j]` 表示 word1 前 i 个字符转成 word2 前 j 个字符的最少操作数。

- `word1[i-1] == word2[j-1]` → `dp[i][j] = dp[i-1][j-1]`（无需操作）
- 否则取三种操作的最小值 + 1：
  - 插入：`dp[i][j-1]`（word1 加一个字符匹配 word2[j]）
  - 删除：`dp[i-1][j]`（word1 删一个字符）
  - 替换：`dp[i-1][j-1]`（替换 word1[i] 为 word2[j]）

## 代码

```java
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;     // 删除全部
    for (int j = 0; j <= n; j++) dp[0][j] = j;     // 插入全部
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = Math.min(dp[i - 1][j - 1],         // 替换
                            Math.min(dp[i - 1][j], dp[i][j - 1]))  // 删除/插入
                            + 1;
            }
        }
    }
    return dp[m][n];
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(m × n) —— 可优化到 O(n) 滚动数组（但面试通常不要求）

## 边界条件

- 一个为空：结果为另一个串的长度

## 变式

- **[1143. 最长公共子序列](1143-longest-common-subsequence.md)**：只允许一种修改（匹配/不匹配）
- **[161. 相隔为 1 的编辑距离](https://leetcode.cn/problems/one-edit-distance/)**：判是否只差一次操作

## 易错点

- **三种操作的直觉**：插入 = word2 当前字符是在 word1 上插入的；删除 = word1 当前字符多余；替换 = 把 word1 的字符换成 word2 的。三种操作的对称性是理解编辑距离的关键
- 初始化：`dp[i][0]` 和 `dp[0][j]` 必须赋值为 i 和 j——空串转非空只能插入/删除

## 面试追问

- **为什么替换算一次操作而删除+插入算两次？** 题目定义如此。如果替换算两次，那问题变成"最长公共子序列"的变体

## 关联题

- 同套路：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— 同型二维 DP
- 进阶：[10. 正则表达式匹配](10-regular-expression-matching.md) —— 更复杂的字符串匹配 DP
- 知识点：编辑距离的三种操作语义见[动态规划](动态规划与贪心.md)

