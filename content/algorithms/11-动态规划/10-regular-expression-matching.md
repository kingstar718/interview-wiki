# 10. 正则表达式匹配（Regular Expression Matching）

频次 ★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

实现支持 `'.'` 和 `'*'` 的正则匹配：`'.'` 匹配任意单字符，`'*'` 匹配零或多个前一字符。

**示例**：
```
输入: s = "aa", p = "a*"
输出: true
输入: s = "ab", p = ".*"
输出: true
```

## 思路

**二维 DP**：`dp[i][j]` 表示 s 前 i 个字符和 p 前 j 个字符是否匹配。

- 字符或 `.` 匹配：`dp[i][j] = dp[i-1][j-1]`
- `*` 匹配有两种情况：
  1. `*` 取 0 次：忽略 p[j-2] 和 `*` → `dp[i][j-2]`
  2. `*` 取 ≥1 次：需 p[j-2] 匹配 s[i-1] → `dp[i-1][j]`

## 代码

```java
public boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m + 1][n + 1];
    dp[0][0] = true;
    for (int j = 2; j <= n; j++) {
        if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];
    }
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            char sc = s.charAt(i - 1), pc = p.charAt(j - 1);
            if (pc == '.' || pc == sc) {
                dp[i][j] = dp[i - 1][j - 1];
            } else if (pc == '*') {
                dp[i][j] = dp[i][j - 2];                    // 取 0 次
                char prev = p.charAt(j - 2);
                if (prev == '.' || prev == sc) {
                    dp[i][j] = dp[i][j] || dp[i - 1][j];    // 取 ≥1 次
                }
            }
        }
    }
    return dp[m][n];
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(m × n)

## 边界条件

- s 为空，p 为 `"a*"`：匹配（`*` 取 0 次）
- p 为空，s 非空：false

## 变式

- **[44. 通配符匹配](https://leetcode.cn/problems/wildcard-matching/)**：`*` 匹配任意序列，`?` 匹配单字符

## 易错点

- **dp 下标**是长度（1-indexed），取字符用 `charAt(i-1)`
- `dp[0][j]` 的初始化只有 `*` 才可以抹掉字符，需要按 2 步长初始化
- `*` 取 ≥1 次时看 `dp[i-1][j]`——这是"吃掉一个 s 字符但保留 p 的 `x*` 状态"

## 面试追问

- **和通配符匹配的区别？** `*` 在正则中修饰前一字符，通配符中 `*` 独立匹配任意序列。后者 DP 更简单

## 关联题

- 同套路：[44. 通配符匹配](https://leetcode.cn/problems/wildcard-matching/) —— 更简单的匹配规则
- 进阶：[72. 编辑距离](72-edit-distance.md) —— 字符串编辑类 DP
- 知识点：字符串匹配 DP 模式见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
