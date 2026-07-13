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
- 知识点：0-1 背包的三种变体（判断/最值/计数）见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)