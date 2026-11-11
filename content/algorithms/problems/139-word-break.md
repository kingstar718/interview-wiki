---
topics:
  - 动态规划与贪心
techniques:
  - 背包
---

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

## 切刀模型与 j 边界、五变体收官（实测）

```
dp[i] = "前 i 个字符能拆"；dp[0]=true（空串=0 词拼接）
转移 = "最后一刀切在 j"：dp[j] && dict.contains(s[j..i)) —— 一个 j 成立即 break
边界：j∈[0, i-1]——0=前缀词（dp[0] 地基托）、i-1=单字符；j=i 是空词非法（j<i）
```

**枚举"最后一 X"五变体收官**（一天过手）：

| 题 | 枚举对象 | 转移 |
|---|---|---|
| 377 | 最后一步放哪个数 | `dp[i−num]` |
| 322 | 最后用哪枚硬币 | `dp[i−coin]+1` |
| 1049 | 最后装哪块石头 | `dp[j−stone]+stone` |
| 198/213 | 偷/不偷这栋 | `dp[i−2]+nums[i]` |
| **139** | **最后一个词从哪切（j）** | `dp[j] && 词命中`（&& 双合法：左半+右段） |

- `goalspecial` 哨兵：贪最短前缀（go）会死在 alspecial，必须枚全部分割点走到 goals——**枚举不贪心**
- `catsandog`：dp[9] 所有"最后一刀"均失败 → false，无需特判
- substring(j,i) 左闭右开=绝对坐标（与 518 的"从 j 起取 i−j 长"容量语义区分）

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
- 知识点：一维 DP + 哈希集加速见[动态规划](../动态规划与贪心.md)

