---
topics:
  - 动态规划与贪心
techniques:
  - 背包
---

# 494. 目标和（Target Sum）

频次 ★★★ · 难度 🟡 · 高频：腾讯

## 题目

给定非负整数数组 `nums` 和目标 `target`，在每个数前添加 `+` 或 `-`，求有多少种表达式计算结果等于 `target`。

**示例**：
```
输入: nums = [1,1,1,1,1], target = 3
输出: 5
解释: -1+1+1+1+1 = 3, +1-1+1+1+1 = 3 ... 共 5 种
```

## 思路

**0-1 背包计数**：设总和为 `sum`，正数和为 `P`，负数和为 `N`，有 `P - N = target` 且 `P + N = sum` → `P = (sum + target) / 2`。问题转化为：选出若干数使其和为 `(sum + target) / 2`，求方案数。

`dp[j]` 表示凑出和 `j` 的方案数。`dp[0] = 1`。对每个 `num`，`dp[j] += dp[j - num]`（逆序 0-1 背包）。

## 代码

```java
public int findTargetSumWays(int[] nums, int target) {
    int sum = 0;
    for (int n : nums) sum += n;
    // (sum + target) 必须非负且为偶数
    if (sum < Math.abs(target) || (sum + target) % 2 != 0) return 0;
    int cap = (sum + target) / 2;   // 正数和
    int[] dp = new int[cap + 1];
    dp[0] = 1;
    for (int num : nums) {
        for (int j = cap; j >= num; j--) {
            dp[j] += dp[j - num];
        }
    }
    return dp[cap];
}
```

## 复杂度

- **时间**：O(n × cap) — cap = (sum + target) / 2
- **空间**：O(cap)

## 边界条件

- `sum < target`：无法达到，返回 0
- `(sum + target) % 2 != 0`：P 不是整数，返回 0
- 数组长度为 0：只有一个空表达式，若 target == 0 返回 1 否则 0
- 包含 0：0 不影响和，但 `+0` 和 `-0` 算两种不同方案，DP 中 0 的处理是内层循环 j 从 cap 到 0（含 0）

## 变式

- **[416. 分割等和子集](416-partition-equal-subset-sum.md)** —— 0-1 背包判定问题，目标为 sum/2
- **[518. 零钱兑换 II](518-coin-change-2.md)** —— 完全背包计数
- **[322. 零钱兑换](322-coin-change.md)** —— 完全背包最值

## 易错点

- **转换条件 `(sum + target) % 2 != 0` 漏了则结果错误**：P 必须是整数且非负。
- `dp[0] = 1` 表示空集可以凑出 0——这个初始值容易忘。
- 内层循环**必须逆序**（0-1 背包特性），否则每个数会被重复选择。
- `sum < Math.abs(target)` 也是提前返回 0 的条件，容易漏。

## 面试追问

- **为什么是 0-1 背包而不是完全背包？** 每个数只能用一次（加 + 或 -），对应 0-1 背包的"每个物品选或不选"。完全背包对应无限次使用。
- **如果数组中有 0 会怎样？** 0 的特殊性在于 `+0` 和 `-0` 是两种不同方案，但 dp 转移时 0 不会改变 cap 值。处理方式：内层循环 j 从 cap 到 0（含 0），让 `dp[0]` 也能被更新——每个 0 出现时 `dp[0]` 乘以 2（因为 0 可以 + 或 -，带来两倍方案数）。
- **DFS 回溯能解吗？** 能，每个数枚举 +/-，O(2ⁿ) 指数级。加记忆化（memo）可优化到 O(n × cap)，和 DP 本质等价。

## 关联题

- 同套路：[416. 分割等和子集](416-partition-equal-subset-sum.md) —— 0-1 背包判定
- 进阶：[322. 零钱兑换](322-coin-change.md) —— 完全背包最值
- 知识点：0-1 背包计数模板见[动态规划与贪心](动态规划与贪心.md)
