---
topics:
  - 动态规划与贪心
techniques:
  - 背包
---

# 416. 分割等和子集（Partition Equal Subset Sum）

频次 ★★★★ · 难度 🟡 · 高频：美团/字节

## 题目

非空正整数数组，能否分成两个子集使元素和相等。

**示例**：
```
输入: nums = [1,5,11,5]
输出: true  （[1,5,5] = 11, [11] = 11）
```

## 思路

**0-1 背包问题**：总和为 sum，目标和 target = sum/2。问题转化为：是否存在子集和为 target。

`dp[j]` 表示是否能凑出和为 j。`dp[0] = true`。对每个 num，`dp[j] = dp[j] || dp[j - num]`（一维逆序防重复用）。

## 代码

```java
public boolean canPartition(int[] nums) {
    int sum = 0;
    for (int n : nums) sum += n;
    if ((sum & 1) == 1) return false;         // 奇数不可能平分
    int target = sum / 2;
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;
    for (int num : nums) {
        for (int j = target; j >= num; j--) {  // 逆序：0-1 背包
            dp[j] = dp[j] || dp[j - num];
        }
    }
    return dp[target];
}
```

## 复杂度

- **时间**：O(n × target) —— target = sum/2
- **空间**：O(target)

## 边界条件

- 数组长度 < 2：false
- 总和为奇数：false
- 最大数 > target：false（可提前剪枝）

## 变式

- **[494. 目标和](https://leetcode.cn/problems/target-sum/)**：每个数前加 +/-，凑目标和——同样是 0-1 背包，`dp[j] += dp[j - num]` 计数
- **[322. 零钱兑换](322-coin-change.md)**：完全背包，求最值
- **1049. 最后一块石头的重量 II**：转化为 0-1 背包

## 易错点

- **内层循环必须逆序**：否则每个 num 会被重复用（变成完全背包）。0-1 背包 = 逆序，完全背包 = 正序
- dp[0] 初始化为 true（空集可以凑出 0）
- 奇数直接 false，节省时间

## 面试追问

- **0-1 背包和完全背包的代码区别？** 内层循环方向：逆序防重复 = 0-1，正序可重复 = 完全

## 关联题

- 同套路：[494. 目标和](https://leetcode.cn/problems/target-sum/) —— 0-1 背包计数
- 进阶：[322. 零钱兑换](322-coin-change.md) —— 完全背包最值
- 知识点：0-1 背包模板见[动态规划](动态规划与贪心.md)

