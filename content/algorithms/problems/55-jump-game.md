---
topics:
  - 动态规划与贪心
---

# 55. 跳跃游戏（Jump Game）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

非负整数数组，每个元素代表从该位置最大可跳长度。从下标 0 出发，能否跳到最后一个下标。

**示例**：
```
输入: nums = [2,3,1,1,4]
输出: true
输入: nums = [3,2,1,0,4]
输出: false
```

## 思路

**贪心**：维护当前能到达的最远位置 `maxReach`。每走一步更新 `maxReach = max(maxReach, i + nums[i])`。如果 `i > maxReach` 说明到不了当前位置，返回 false。

## 代码

```java
public boolean canJump(int[] nums) {
    int maxReach = 0;
    for (int i = 0; i < nums.length; i++) {
        if (i > maxReach) return false;         // 到不了 i
        maxReach = Math.max(maxReach, i + nums[i]);
        if (maxReach >= nums.length - 1) return true;
    }
    return true;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 长度为 1：已在终点，true
- 含 0 且跳不过去：false

## 变式

- **[45. 跳跃游戏 II](45-jump-game-ii.md)**：保证能跳到终点，求最少步数
- **[1306. 跳跃游戏 III](https://leetcode.cn/problems/jump-game-iii/)**：跳转到 i±nums[i]，BFS

## 易错点

- `maxReach` 是"当前可达最远下标"，不是剩余步数
- 循环中一旦 `maxReach >= n-1` 可提前返回

## 面试追问

- **如果数组很大（百万级）还是 O(n) 吗？** 是，贪心一次遍历，常数空间

## 关联题

- 同套路：[45. 跳跃游戏 II](45-jump-game-ii.md) —— 最少步数版
- 进阶：[134. 加油站](134-gas-station.md) —— 贪心另一类"能否跑完一圈"问题
- 知识点：贪心 vs DP 的适用边界见[贪心](动态规划与贪心.md)

