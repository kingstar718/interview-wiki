# 516. 最长回文子序列（Longest Palindromic Subsequence）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

找字符串中最长回文子序列（**不要求连续**）的长度。

**示例**：
```
输入: s = "bbbab"
输出: 4  （"bbbb"）
```

## 思路

**二维 DP**：`dp[i][j]` 表示 s[i..j] 中最长回文子序列的长度。

- `s[i] == s[j]`：`dp[i][j] = dp[i+1][j-1] + 2`（首尾两个字符都取）
- `s[i] != s[j]`：`dp[i][j] = max(dp[i+1][j], dp[i][j-1])`（两端至少一个不取）

初始化：`dp[i][i] = 1`（单字符）。遍历顺序：i 从后往前，j 从 i+1 往后。

与 5. 最长回文子串的区别：5 要求**连续**（用中心扩展），516 要求**子序列**（用 DP）。

## 代码

```java
public int longestPalindromeSubseq(String s) {
    int n = s.length();
    int[][] dp = new int[n][n];
    for (int i = n - 1; i >= 0; i--) {
        dp[i][i] = 1;                          // 单字符
        for (int j = i + 1; j < n; j++) {
            if (s.charAt(i) == s.charAt(j)) {
                dp[i][j] = dp[i + 1][j - 1] + 2;
            } else {
                dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
            }
        }
    }
    return dp[0][n - 1];
}
```

## 复杂度

- **时间**：O(n²)
- **空间**：O(n²)，可优化到 O(n) 滚动数组

## 边界条件

- 空串：返回 0
- 单字符：返回 1
- 全相同字符：返回 n

## 变式

- **[5. 最长回文子串](5-longest-palindromic-substring.md)**：连续回文子串，中心扩展 O(n²)
- **[647. 回文子串](647-palindromic-substrings.md)**：计数回文子串个数
- **[1312. 让字符串成为回文串的最少插入次数](https://leetcode.cn/problems/minimum-insertion-steps-to-make-a-string-palindrome/)**：n - LPS

## 易错点

- 遍历顺序：i 从 n-1 到 0（因为 `dp[i][j]` 依赖 `dp[i+1][j-1]`，即左下角）
- 和 5（最长回文子串）的区别：5 要求连续，516 不要求连续；5 用中心扩展，516 用 DP
- 初始化 `dp[i][i] = 1`，j 从 i+1 开始

## 面试追问

- **和 5 题的区别？** 5 是子串（连续），516 是子序列（不连续）。5 用中心扩展，516 用 DP。子序列的 DP 转移和 LCS 类似
- **如何输出最长回文子序列？** 需要记录转移方向，从 (0, n-1) 回溯构造

## 关联题

- 同套路：[5. 最长回文子串](5-longest-palindromic-substring.md) —— 连续版
- 进阶：[647. 回文子串](647-palindromic-substrings.md) —— 计数版
- 知识点：子序列 DP + 区间 DP 见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)