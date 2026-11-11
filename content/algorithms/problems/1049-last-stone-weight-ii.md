---
topics:
  - 动态规划与贪心
techniques:
  - 背包
---

# 1049. 最后一块石头的重量 II（Last Stone Weight II）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

一堆石头，每次选两块粉碎：若 x == y 则都碎；若 x != y 则剩 |x-y|。求最后剩下的最小可能重量。

**示例**：
```
输入: stones = [2,7,4,1,8,1]
输出: 1  （组合 (2,4,1,1) 和 (7,8) → 8 vs 7 → 剩 1）
```

## 思路

**转化为 0-1 背包**：将石头分成两组使重量差最小。记总和为 sum，target = sum/2。问题变成：从 stones 中选若干石头，使总重量不超过 target 且尽可能大（即最接近 target）。

设选出的重量为 `maxWeight`，则另一组为 `sum - maxWeight`，差 = `sum - 2 × maxWeight`。

`dp[j]` 表示容量为 j 的背包能装的最大重量。0-1 背包：内层逆序。

## 代码

```java
public int lastStoneWeightII(int[] stones) {
    int sum = 0;
    for (int s : stones) sum += s;
    int target = sum / 2;
    int[] dp = new int[target + 1];
    for (int stone : stones) {
        for (int j = target; j >= stone; j--) {   // 逆序：0-1 背包
            dp[j] = Math.max(dp[j], dp[j - stone] + stone);
        }
    }
    return sum - 2 * dp[target];
}
```

## 撞石→分堆→背包换装链与四形态收官（实测）

```
撞石（过程视角）：每次 |x−y| = 两阵营总重差的累进 —— 石头从不消失，只"选边"
分堆（归属视角）：最终剩 = |ΣA − ΣB|，最优分堆 ≡ 最优撞石
背包（操作视角）：小堆 p ≤ sum/2 撑到最大（dp[j] = 容量 j 最多装多满）
代数（收尾视角）：answer = sum − 2p（大堆−小堆；−2p 的直觉=小堆被减两次）
```

**dp[j] 语义四形态收官**（一天内全型号过手）：

| 题 | dp[j] | 算子 | 方向 | dp[0] |
|---|---|---|---|---|
| 416 | 能不能（bool） | `\|\|` | 倒序 | true |
| 494 | 几种方案（int） | `+=` | 倒序 | 1 |
| 322/279 | 最少枚数（int 最值） | `min` | 外层容量自增 | 0 |
| **1049** | **最多装多满（int 最值）** | **`max(..., dp[j−s]+s)`** | **倒序** | **0（默认全 0，无需 fill）** |

- `[10]` 单块：dp[target]=0（10 装不进 5 容量）→ sum−0=10——"无处撞"的代数自然表达，无需特判
- `sum−2p` 对 sum 奇数自动为正（下取整无漏洞）；错写 `sum−p` 算出的是大堆不是答案（常见最后一脚）

## 复杂度

- **时间**：O(n × target)，target = sum/2
- **空间**：O(target)

## 边界条件

- 单块石头：返回 stones[0]（另一组为空）
- 总和为奇数：target 向下取整，最后差为 sum - 2×dp[target]

## 变式

- **[416. 分割等和子集](416-partition-equal-subset-sum.md)**：判断能否平分（dp[target] == target）
- **[494. 目标和](494-target-sum.md)**：求方案数（`dp[j] += dp[j-num]`）

## 易错点

- 跟 416 的区别：416 是判断能否（boolean DP），1049 是求最接近（max 值 DP）
- 跟 494 的区别：494 是计数（方案数），1049 是求最值
- 内层循环逆序（0-1 背包不能重复选）

## 面试追问

- **为什么能转化为 0-1 背包？** 每次粉碎等价于给每块石头分配 + 或 - 符号，问题变成划分
- **和 416 的区别在哪？** 416 是求是否恰好等于 target（boolean），1049 是求不超过 target 的最大值（int）

## 关联题

- 同套路：[416. 分割等和子集](416-partition-equal-subset-sum.md) —— 判断能否平分
- 进阶：[494. 目标和](494-target-sum.md) —— 计算方案数
- 知识点：0-1 背包的三种变体（判断/最值/计数）见[动态规划](../动态规划与贪心.md)
