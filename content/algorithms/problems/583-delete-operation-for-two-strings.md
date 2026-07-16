---
topics:
  - 动态规划与贪心
techniques:
  - 二维DP
---

# 583. 两个字符串的删除操作（Delete Operation for Two Strings）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

给定两个单词 word1 和 word2，每次删除任意一个单词中的一个字符，求使两个单词相同的最小删除步数。

**示例**：
```
输入: word1 = "sea", word2 = "eat"
输出: 2  （删除 's' 和 't'，剩下 "ea"）
```

## 思路

**LCS 变式**：最终相同的部分就是 word1 和 word2 的 LCS（最长公共子序列）。删除步数 = `len1 + len2 - 2 × LCS`。

**直接 DP**：`dp[i][j]` 表示 word1[0..i] 和 word2[0..j] 的最小删除次数。
- `word1[i-1] == word2[j-1]`：`dp[i][j] = dp[i-1][j-1]`（保留）
- 否则：`dp[i][j] = min(dp[i-1][j], dp[i][j-1]) + 1`（删除一个）

## 代码

```java
// 解法 1：LCS 变式
public int minDistance(String word1, String word2) {
    int lcs = longestCommonSubsequence(word1, word2);
    return word1.length() + word2.length() - 2 * lcs;
}

// 解法 2：直接 DP
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = i;   // 删除全部 word1
    for (int j = 0; j <= n; j++) dp[0][j] = j;   // 删除全部 word2
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + 1;
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

- 一个为空：返回另一个的长度
- 两字符串相同：返回 0
- 无公共字符：返回 len1 + len2

## 变式

- **[1143. 最长公共子序列](1143-longest-common-subsequence.md)**：求 LCS 长度（不删除）
- **[72. 编辑距离](72-edit-distance.md)**：允许插入、删除、替换三种操作

## 易错点

- 与 72. 编辑距离的区别：本题只允许删除，编辑距离允许插入/删除/替换
- 初始化：`dp[i][0] = i`（删除 word1 全部），`dp[0][j] = j`（删除 word2 全部）
- LCS 变式更简洁：`len1 + len2 - 2*LCS`，展示对 LCS 的理解

## 面试追问

- **和 72. 编辑距离的区别？** 72 允许插入、删除、替换；583 只允许删除。583 是 72 的特例，也是 LCS 的变式
- **如果允许替换？** 就是 72. 编辑距离，转移方程多一个 `dp[i-1][j-1] + 1`

## 关联题

- 同套路：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— LCS 核心
- 进阶：[72. 编辑距离](72-edit-distance.md) —— 允许插入/删除/替换
- 知识点：LCS 变式 + 删除操作 DP 见[动态规划](动态规划与贪心.md)
