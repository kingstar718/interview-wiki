---
topics:
  - 动态规划与贪心
techniques:
  - 贪心证明
---

# 134. 加油站（Gas Station）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

环形加油站路线上有 gas[i] 和 cost[i]，从哪个站出发能跑完一圈（唯一解或 -1）。

**示例**：
```
输入: gas = [1,2,3,4,5], cost = [3,4,5,1,2]
输出: 3
```

## 思路

**贪心一次遍历**：

- 从 0 开始累计剩余油量 `totalTank` 和当前段的 `curTank`
- 如果 `curTank < 0`，说明起点不可能是 0~i 中的任何一个，起点设为 i+1，curTank 归零
- 最终如果 totalTank ≥ 0，起点可行；否则 -1

## 代码

```java
public int canCompleteCircuit(int[] gas, int[] cost) {
    int totalTank = 0, curTank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) {
        int diff = gas[i] - cost[i];
        totalTank += diff;
        curTank += diff;
        if (curTank < 0) {
            start = i + 1;       // 重置起点
            curTank = 0;
        }
    }
    return totalTank >= 0 ? start : -1;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 总 gas < 总 cost：返回 -1
- 所有站 gas >= cost：起点 0

## 变式

- 环形公交路线油耗：同模问题

## 易错点

- `curTank < 0` 时重置起点为 i+1 而不是 start+1——因为 0~i 中任何一个都不可能是起点（从起点到 i 的累积油量为负，说明起点到 i 之间有消耗缺口，换更早的起点也一样）
- 最终用 totalTank 判断是否存在解

## 面试追问

- **为什么从 i+1 开始而不是逐个试？** 如果从 start 到 i 的累积油量 < 0，那 start~i 之间任意节点作为起点也会在 i 处 fail

## 关联题

- 同套路：[55. 跳跃游戏](55-jump-game.md) —— 贪心累积
- 进阶：[45. 跳跃游戏 II](45-jump-game-ii.md) —— 另一种贪心边界
- 知识点：累积贪心的"负则重置"模式见[贪心](动态规划与贪心.md)

