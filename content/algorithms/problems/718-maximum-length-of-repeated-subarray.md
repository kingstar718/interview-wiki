---
topics:
  - 动态规划与贪心
techniques:
  - 二维DP
---

# 718. 最长重复子数组（Maximum Length of Repeated Subarray）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

两个整数数组 A 和 B，找最长公共子数组（**必须连续**）的长度。

**示例**：
```
输入: A = [1,2,3,2,1], B = [3,2,1,4,7]
输出: 3  （[3,2,1]）
```

## 思路

**二维 DP**：`dp[i][j]` 表示以 A[i-1] 和 B[j-1] 结尾的最长公共子数组长度。

`A[i-1] == B[j-1]` 时 `dp[i][j] = dp[i-1][j-1] + 1`，否则 `dp[i][j] = 0`。全程记录 max。

**空间优化**：一维滚动数组（倒序），因为 dp[i][j] 只依赖 dp[i-1][j-1]。

与 1143. LCS（最长公共子序列）的区别：这道题要求**连续**，LCS 不要求连续。

## 代码

```java
// 空间优化：一维滚动数组
public int findLength(int[] A, int[] B) {
    int m = A.length, n = B.length;
    int[] dp = new int[n + 1];
    int max = 0;
    for (int i = 1; i <= m; i++) {
        for (int j = n; j >= 1; j--) {         // 倒序：依赖上一行的左上角
            if (A[i - 1] == B[j - 1]) {
                dp[j] = dp[j - 1] + 1;
                max = Math.max(max, dp[j]);
            } else {
                dp[j] = 0;
            }
        }
    }
    return max;
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(n)

## 边界条件

- 任一数组为空：返回 0
- 无公共元素：返回 0

## 变式

- **[1143. 最长公共子序列](1143-longest-common-subsequence.md)**：不要求连续，dp[i][j] 不匹配时取 max(dp[i-1][j], dp[i][j-1])
- **[300. 最长递增子序列](300-longest-increasing-subsequence.md)**：单数组不连续

## 易错点

- 不匹配时 dp[j] = 0（重置），而不是取 max——这是"连续"子数组的关键
- 一维倒序：dp[j] 依赖 dp[j-1]（上一行的左上角），正序会覆盖未使用的值
- 结果不一定在 dp[n] 位置，需要全程维护 max

## 面试追问

- **和 LCS 的区别？** 本题要求连续（子数组），不匹配时重置为 0；LCS 不要求连续，不匹配时取 max
- **滑动窗口能做吗？** 能：将两个数组"对齐"后逐位比较，O((m+n) × min(m,n))，空间 O(1)，但写法复杂

## 关联题

- 同套路：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— 不连续版
- 进阶：[674. 最长连续递增序列](674-longest-continuous-increasing-subsequence.md) —— 单数组连续递增
- 知识点：子数组(连续) vs 子序列(不连续) DP 转移见[动态规划](动态规划与贪心.md)
