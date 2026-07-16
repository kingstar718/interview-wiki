---
topics:
  - 动态规划与贪心
techniques:
  - 背包
---

# 474. 一和零（Ones and Zeroes）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

二进制字符串数组 strs，找最大子集，使其中最多有 m 个 0 和 n 个 1。

**示例**：
```
输入: strs = ["10","0001","111001","1","0"], m = 5, n = 3
输出: 4  （{"10","0001","1","0"} 共 5 个 0、3 个 1）
```

## 思路

**二维 0-1 背包**：每个字符串是要么选要么不选的物品，消耗 cost0 个 0 和 cost1 个 1，价值为 1（选一个字符串）。

`dp[i][j]` 表示最多 i 个 0 和 j 个 1 时的最大子集长度。对每个字符串 `s`：
`dp[i][j] = max(dp[i][j], dp[i-cnt0][j-cnt1] + 1)`

内层 i、j 均逆序（0-1 背包）。

## 代码

```java
public int findMaxForm(String[] strs, int m, int n) {
    int[][] dp = new int[m + 1][n + 1];
    for (String s : strs) {
        int cnt0 = 0, cnt1 = 0;
        for (char c : s.toCharArray()) {
            if (c == '0') cnt0++;
            else cnt1++;
        }
        // 二维 0-1 背包：两维都逆序
        for (int i = m; i >= cnt0; i--) {
            for (int j = n; j >= cnt1; j--) {
                dp[i][j] = Math.max(dp[i][j], dp[i - cnt0][j - cnt1] + 1);
            }
        }
    }
    return dp[m][n];
}
```

## 复杂度

- **时间**：O(L × m × n)，L = 所有字符串长度之和
- **空间**：O(m × n)

## 边界条件

- 空 strs：返回 0
- 某字符串 cnt0 > m 或 cnt1 > n：跳过（装不下）
- m 或 n 为 0：只统计另一维

## 变式

- **[416. 分割等和子集](416-partition-equal-subset-sum.md)**：一维 0-1 背包判断
- **[494. 目标和](494-target-sum.md)**：一维 0-1 背包计数

## 易错点

- 两维都要逆序——这是 0-1 背包的关键，保证每个字符串只选一次
- 统计 cnt0/cnt1 时注意 `toCharArray()` 每次新建数组，面试中可优化为直接 `charAt()`
- 内层循环范围：`i >= cnt0` 和 `j >= cnt1`，不是 `>= 0`

## 面试追问

- **如果物品数量巨大但 m/n 很小？** 复杂度 O(L×m×n)，m/n 小时可行
- **和普通 0-1 背包的关系？** 普通 0-1 背包是二维背包的一维特例

## 关联题

- 同套路：[416. 分割等和子集](416-partition-equal-subset-sum.md) —— 一维 0-1 背包
- 进阶：[494. 目标和](494-target-sum.md) —— 0-1 背包计数
- 知识点：多维 0-1 背包见[动态规划](动态规划与贪心.md)
