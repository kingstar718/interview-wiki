# 392. 判断子序列（Is Subsequence）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

判断字符串 s 是否为 t 的子序列（s 中字符在 t 中按顺序出现，不要求连续）。

**示例**：
```
输入: s = "abc", t = "ahbgdc"
输出: true
```

## 思路

**解法 1：双指针 O(n)** — i 指向 s，j 指向 t，匹配则 i++，j 始终前进。

**解法 2：DP 预处理（进阶）** — 当有大量 s 需要查询同一个 t 时，预处理 t 的字符位置索引。`dp[i][c]` 表示从 t 的第 i 个位置开始，字符 c 第一次出现的位置。查询时 O(|s|)。

## 代码

```java
// 解法 1：双指针
public boolean isSubsequence(String s, String t) {
    int i = 0, j = 0;
    while (i < s.length() && j < t.length()) {
        if (s.charAt(i) == t.charAt(j)) i++;
        j++;
    }
    return i == s.length();
}

// 解法 2：DP 预处理（大量 s 查询时）
public boolean isSubsequence(String s, String t) {
    int n = t.length();
    // dp[i][c]：从位置 i 开始，字符 c 第一次出现的位置
    int[][] dp = new int[n + 1][26];
    for (int c = 0; c < 26; c++) dp[n][c] = n;  // 越界标记
    for (int i = n - 1; i >= 0; i--) {
        for (int c = 0; c < 26; c++) {
            if (t.charAt(i) == 'a' + c) dp[i][c] = i;
            else dp[i][c] = dp[i + 1][c];
        }
    }
    int pos = 0;
    for (char c : s.toCharArray()) {
        if (dp[pos][c - 'a'] == n) return false;
        pos = dp[pos][c - 'a'] + 1;
    }
    return true;
}
```

## 复杂度

- **双指针**：时间 O(|t|)，空间 O(1)
- **DP 预处理**：时间 O(|t| × 26 + |s|)，空间 O(|t| × 26)

## 边界条件

- s 为空：返回 true（空串是任何字符串的子序列）
- t 为空，s 非空：返回 false
- s 长度 > t 长度：返回 false

## 变式

- **[115. 不同的子序列](115-distinct-subsequences.md)**：求 s 在 t 中作为子序列出现的次数
- **[792. 匹配子序列的单词数](https://leetcode.cn/problems/number-of-matching-subsequences/)**：多个 s 匹配同一个 t

## 易错点

- 双指针中 j 始终 +1，i 只在匹配时 +1——i 最终等于 s.length() 即成功
- DP 预处理中，dp[n][c] 初始化为 n（越界标记），表示从末尾开始没有该字符
- 进阶追问重点：**大量 s 查询时如何优化**——DP 预处理的场景

## 面试追问

- **如果有 10 亿个 s 要查询同一个 t，怎么做？** DP 预处理 t，每次查询 O(|s|)。双指针每次 O(|t|) 太慢
- **DP 预处理的空间能否优化？** 26 个小写字母是常数，`O(|t| × 26)` 已是最优

## 关联题

- 同套路：[115. 不同的子序列](115-distinct-subsequences.md) —— 计数版
- 进阶：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— 二维 DP 版
- 知识点：双指针 vs DP 预处理的场景取舍见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)