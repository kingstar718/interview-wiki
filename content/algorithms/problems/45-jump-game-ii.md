---
topics:
  - 动态规划与贪心
techniques:
  - 贪心证明
---

# 45. 跳跃游戏 II（Jump Game II）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

保证能跳到终点，求最少跳跃次数。

**示例**：
```
输入: nums = [2,3,1,1,4]
输出: 2  （下标 0→1→4）
```

## 思路

**贪心 BFS 视角**：记录当前步能到达的最远边界 `end`，在边界内不断更新下一步的 `maxReach`。走到边界时步数 +1，更新边界为 `maxReach`。

## 代码

```java
public int jump(int[] nums) {
    int jumps = 0, end = 0, maxReach = 0;
    for (int i = 0; i < nums.length - 1; i++) {
        maxReach = Math.max(maxReach, i + nums[i]);
        if (i == end) {           // 到达当前步的边界
            jumps++;
            end = maxReach;       // 更新为下一步的边界
        }
    }
    return jumps;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 长度为 1：已在终点，返回 0

## 变式

- **[55. 跳跃游戏](55-jump-game.md)**：只判能否到达
- 输出跳跃路径：记录每个位置最优的前置节点

## 易错点

- 循环遍历到 `n-2`（无需从终点跳跃）
- `i == end` 时更新边界：保持"每步最大范围"的 BFS 语义

## 面试追问

- **为什么不 DP？** `dp[i] = min(dp[j]+1 for j < i if j+nums[j] >= i)`，O(n²)。贪心 O(n) 是基于"保证能到达"的题目条件

## 关联题

- 同套路：[55. 跳跃游戏](55-jump-game.md) —— 判断版
- 进阶：[134. 加油站](134-gas-station.md) —— 另一种遍历+贪心决策
- 知识点：贪心 BFS 步数模板见[贪心](动态规划与贪心.md)

