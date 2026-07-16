---
topics:
  - 动态规划与贪心
techniques:
  - 线性DP
---

# 674. 最长连续递增序列（Longest Continuous Increasing Subsequence）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

未排序数组，找最长连续递增子序列（**必须连续**）的长度。

**示例**：
```
输入: nums = [1,3,5,4,7]
输出: 3  （[1,3,5]）
```

## 思路

**贪心/DP**：一次遍历，`dp[i] = nums[i] > nums[i-1] ? dp[i-1] + 1 : 1`。

可优化到 O(1) 空间：用一个变量记录当前连续递增长度，一个变量记录全局最大值。

与 300. LIS（最长递增子序列）的区别：这道题要求**连续**，300 不要求连续。

## 代码

```java
public int findLengthOfLCIS(int[] nums) {
    if (nums.length == 0) return 0;
    int cur = 1, max = 1;
    for (int i = 1; i < nums.length; i++) {
        if (nums[i] > nums[i - 1]) {
            cur++;
            max = Math.max(max, cur);
        } else {
            cur = 1;                         // 重置
        }
    }
    return max;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 空数组：返回 0
- 单元素：返回 1
- 全降序：每步都重置，max = 1

## 变式

- **[300. 最长递增子序列](300-longest-increasing-subsequence.md)**：不要求连续，O(n²) DP 或 O(n log n) 贪心 + 二分
- **[673. 最长递增子序列的个数](https://leetcode.cn/problems/number-of-longest-increasing-subsequence/)**：不连续 + 计数

## 易错点

- 跟 300 题的区别：这道题是连续的（LCIS），300 是不连续的（LIS）。看到"连续"就用贪心，看到"不连续"就用 DP
- 重置时 cur = 1（当前元素本身算一个），不是 0

## 面试追问

- **如果要求不连续呢？** 就是 300. LIS，O(n²) DP 或 O(n log n) 贪心 + 二分
- **连续递增子数组 vs 连续递增子序列？** 这道题就是连续递增子数组（subarray），但 LeetCode 标题叫"子序列"，注意区分

## 关联题

- 同套路：[300. 最长递增子序列](300-longest-increasing-subsequence.md) —— 不连续版
- 进阶：[718. 最长重复子数组](718-maximum-length-of-repeated-subarray.md) —— 两个数组的连续子数组匹配
- 知识点：连续 vs 不连续子序列的 DP 区别见[动态规划](动态规划与贪心.md)
