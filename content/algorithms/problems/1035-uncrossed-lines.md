---
topics:
  - 动态规划与贪心
techniques:
  - 二维DP
---

# 1035. 不相交的线（Uncrossed Lines）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

两个整数数组 nums1 和 nums2，在它们之间画线（nums1[i] == nums2[j] 时连线），要求线不相交。求最大连线数。

**示例**：
```
输入: nums1 = [1,4,2], nums2 = [1,2,4]
输出: 2  （连线 1-1 和 4-4 或 2-2，最多 2 条不相交线）
```

## 思路

**本质就是 LCS（最长公共子序列）**：不相交的线意味着连接的索引必须都是递增的。即求 nums1 和 nums2 的最长公共子序列长度。

`dp[i][j]` = nums1[i-1]==nums2[j-1] ? dp[i-1][j-1]+1 : max(dp[i-1][j], dp[i][j-1])。

## 代码

```java
public int maxUncrossedLines(int[] nums1, int[] nums2) {
    int m = nums1.length, n = nums2.length;
    int[] dp = new int[n + 1];                // 滚动数组
    for (int i = 1; i <= m; i++) {
        int prev = 0;                          // dp[i-1][j-1]
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];
            if (nums1[i - 1] == nums2[j - 1]) {
                dp[j] = prev + 1;
            } else {
                dp[j] = Math.max(dp[j], dp[j - 1]);
            }
            prev = temp;
        }
    }
    return dp[n];
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(n) 滚动数组

## 边界条件

- 任一数组为空：返回 0
- 无相等元素：返回 0

## 变式

- **[1143. 最长公共子序列](1143-longest-common-subsequence.md)**：同款 LCS DP，只是输入是字符串
- **[718. 最长重复子数组](718-maximum-length-of-repeated-subarray.md)**：连续子数组匹配（子数组 vs 子序列）

## 易错点

- 识别出"不相交的线 = LCS"是这道题的核心——面试中如果没看出来，会卡很久
- 滚动数组的 prev 更新：先保存 `temp = dp[j]`，再更新 `dp[j]`，最后 `prev = temp`
- 和 718 的区别：718 要求连续（匹配时 dp[j]=dp[j-1]+1，不匹配时 dp[j]=0）；1035 不要求连续

## 面试追问

- **为什么等于 LCS？** 画线不相交 → 连接的索引单调递增 → 就是找公共子序列
- **如果线可以相交呢？** 那就不是 LCS 了，变成了二分图最大匹配问题

## 关联题

- 同套路：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— 字符串版 LCS
- 进阶：[718. 最长重复子数组](718-maximum-length-of-repeated-subarray.md) —— 连续版
- 知识点：LCS 问题建模 + 识别见[动态规划](动态规划与贪心.md)
