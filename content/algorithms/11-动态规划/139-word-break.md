# 139. 单词拆分（Word Break）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

字符串 s 和一个单词列表 wordDict，判断 s 是否能由 wordDict 中的单词拼接而成（单词可重复用）。

**示例**：
```
输入: s = "leetcode", wordDict = ["leet","code"]
输出: true
```

## 思路

**一维 DP**：`dp[i]` 表示 s 前 i 个字符是否能被拆分。`dp[0] = true`。

对每个 i，找 j < i 使 `dp[j] && s[j..i-1] ∈ wordDict`。用 HashSet 实现 O(1) 单词查询。

## 代码

```java
public boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;
    for (int i = 1; i <= s.length(); i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    return dp[s.length()];
}
```

## 复杂度

- **时间**：O(n² × L) —— L 是 substring 拷贝长度，最坏 O(n³)
- **空间**：O(n + dict)

优化方向：先算最长单词长度限制内层循环 + 从 i 向 j 反向搜（遇到匹配就 break）。

## 边界条件

- wordDict 为空：当 s 也为空时返回 true
- s 为空：返回 true

## 变式

- **[140. 单词拆分 II](https://leetcode.cn/problems/word-break-ii/)**：输出所有拆分方案，DFS + 记忆化搜索
- 带权重的单词拆分：DP 变 min/max 问题

## 易错点

- `dp[0] = true` 表示空串可拆分
- substring 是 `[j, i)` 左闭右开
- 每次用 `break` 提前结束，避免无用计算

## 面试追问

- **怎么进一步优化？** 用 i 从短到长 + 限制 j 从 max(0, i-maxLen) 到 i

## 关联题

- 同套路：[140. 单词拆分 II](https://leetcode.cn/problems/word-break-ii/) —— 输出方案
- 进阶：[300. 最长递增子序列](300-longest-increasing-subsequence.md) —— 另一类子序列 DP
- 知识点：一维 DP + 哈希集加速见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
