# 213. 打家劫舍 II（House Robber II）

频次 ★★★★ · 难度 🟡 · 高频：美团

## 题目

198 进阶版——房屋首尾相连成环。

**示例**：
```
输入: nums = [2,3,2]
输出: 3  （偷第一家或第三家，不能同时偷因为首尾相邻）
```

## 思路

**环形拆成两个线性问题**：因为首尾互斥，只需要分别求"去掉尾"和"去掉头"的两个线性打家劫舍，取最大值。

- `robRange(nums, 0, n-2)`：偷第一家，不偷最后一家
- `robRange(nums, 1, n-1)`：不偷第一家，偷最后一家
- 取两趟的 max

## 代码

```java
public int rob(int[] nums) {
    int n = nums.length;
    if (n == 1) return nums[0];
    return Math.max(robRange(nums, 0, n - 2), robRange(nums, 1, n - 1));
}

private int robRange(int[] nums, int l, int r) {
    int prev2 = 0, prev1 = 0;
    for (int i = l; i <= r; i++) {
        int cur = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- n = 1：直接返回（环只有一间房屋）
- n = 2：取 max（两间只能偷一间）

## 变式

- **[198. 打家劫舍](198-house-robber.md)**：线性版
- **[337. 打家劫舍 III](https://leetcode.cn/problems/house-robber-iii/)**：树形版

## 易错点

- `n == 1` 需要**特判**——如果直接进 `robRange` 两趟会返回错误结果
- `robRange` 是**左闭右闭**区间，包含 r

## 面试追问

- **环形约束的本质？** 就是让首尾不能同时选。分两趟是最简单的思路，也可以用状态机 DP 解决（多一个"是否偷第一家"的维度）

## 关联题

- 同套路：[198. 打家劫舍](198-house-robber.md) —— 线性版
- 进阶：[300. 最长递增子序列](300-longest-increasing-subsequence.md) —— 另一种递推模式
- 知识点："环形转线性"的通用技巧见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
