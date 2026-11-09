---
topics:
  - 动态规划与贪心
techniques:
  - 背包
---

# 494. 目标和（Target Sum）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

给每个数前加 `+` 或 `-`，使表达式结果等于 target，求不同表达式的数目。

**示例**：
```
输入: nums = [1,1,1,1,1], target = 3
输出: 5  （+1+1+1+1-1, +1+1+1-1+1, +1+1-1+1+1, +1-1+1+1+1, -1+1+1+1+1）
```

## 思路

**转化为 0-1 背包**：设正数组和为 P，负数组绝对值和为 N，则 P - N = target，又 P + N = sum。

解得 `P = (sum + target) / 2`。问题变成：从 nums 中选若干个数，和为 P 的方案数。

`dp[j]` 表示装满容量 j 的方案数。`dp[j] += dp[j - num]`，内层逆序（0-1 背包）。

剪枝：sum + target 必须是偶数且 ≥ 0。

## 代码

```java
public int findTargetSumWays(int[] nums, int target) {
    int sum = 0;
    for (int n : nums) sum += n;
    // 剪枝：sum+target 必须是非负偶数
    if (sum + target < 0 || ((sum + target) & 1) == 1) return 0;
    int P = (sum + target) / 2;
    int[] dp = new int[P + 1];
    dp[0] = 1;                             // 空集和为 0，方案数 1
    for (int num : nums) {
        for (int j = P; j >= num; j--) {    // 逆序：0-1 背包
            dp[j] += dp[j - num];
        }
    }
    return dp[P];
}
```

## 原理友讲版（"放球+班车"双模型——实测一次讲通）

```
① 代数约化（小学两式相加）：p−q=target、p+q=sum ⟹ p=(sum+target)/2
   → "选子集和为 p 的方案数"
② 逐个放球滚表：dp[j] = "桌上的球能凑出和 j 的组合数"
   新球 x 入场：dp[j] = "不装新球的方案" + "先凑 j−x 再装新球"  j 从大到小逐格 +=
③ 杨辉三角涌现：nums 全 1、target=3 时逐轮表 [1,1,0,0,0]→[1,2,1,0,0]→…→[1,5,10,10,5]
   轮末 dp[k]=C(k,r) 完美对拍——方案数型 01 背包的直觉验表法
④ 倒序救场（与 416 同由）：引用必须指向"班车未通车前"的世界
```

## 特殊现象与剪枝

- **0 的膨胀**：num=0 时内层下限 `j>=0`，`dp[j] += dp[j]`（自己+自己）→ 每个 0 独立翻倍（[0,0,1] target=1 → 4；[1,0,0,0,0] target=1 → 16）——**倒序循环天然覆盖，无需特判**
- **三个剪枝**（镜像 416）：sum+target<0（P 为负）；(sum+target) 奇（P 非整数）；|target|>sum（隐含，可选上）——answer bound 为 int 但 n≤20 故 dp 不溢出

## 复杂度

- **时间**：O(n × P)，P = (sum + target) / 2
- **空间**：O(P)

## 边界条件

- sum + target 为奇数：无解，返回 0
- sum + target < 0：target 太小，无解
- 全 0 数组 + target = 0：每个 0 可选 + 或 -，方案数 2^n

## 变式

- **[416. 分割等和子集](416-partition-equal-subset-sum.md)**：判断能否平分（boolean DP）
- **[1049. 最后一块石头的重量 II](1049-last-stone-weight-ii.md)**：求最接近 target 的重量（max 值 DP）

## 易错点

- **剪枝必须做**：`sum + target < 0` 或奇数时直接返回 0，否则 dp 数组下标越界
- 背包容量是 P = (sum + target) / 2，不是 target
- dp[0] = 1（空集一种方案），dp 其余为 0
- 内层逆序：每个数只能选一次（0-1 背包）

## 面试追问

- **为什么内层要逆序？** 正序会导致每个 num 被重复使用（变成完全背包），逆序保证每个 num 只用一次
- **DFS 回溯能做吗？** 能，O(2^n)，仅适合 n 很小或加记忆化

## 关联题

- 同套路：[416. 分割等和子集](416-partition-equal-subset-sum.md) —— 0-1 背包判断
- 进阶：[1049. 最后一块石头的重量 II](1049-last-stone-weight-ii.md) —— 0-1 背包最值
- 知识点：0-1 背包计数模板见[动态规划](../动态规划与贪心.md)
