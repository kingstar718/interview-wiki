# 621. 任务调度器（Task Scheduler）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

任务用大写字母表示，每个任务执行需 1 单位时间，同类任务间必须间隔 n 单位。求最小总执行时间。

**示例**：
```
输入: tasks = ["A","A","A","B","B","B"], n = 2
输出: 8  （A→B→idle→A→B→idle→A→B）
```

## 思路

**贪心 + 数学推导**：找出出现次数最多的任务 maxCount，有 maxCount 个任务的总空闲 = `(maxCount-1) × (n+1) + 同最多出现次数的任务种类数`。结果取 max(推导值, 总任务数)。

## 代码

```java
public int leastInterval(char[] tasks, int n) {
    int[] freq = new int[26];
    for (char c : tasks) freq[c - 'A']++;
    Arrays.sort(freq);
    int maxCount = freq[25];                            // 最高频次
    int sameMax = 0;
    for (int f : freq) {
        if (f == maxCount) sameMax++;
    }
    return Math.max(tasks.length,
        (maxCount - 1) * (n + 1) + sameMax);
}
```

## 复杂度

- **时间**：O(n) —— n 为任务数
- **空间**：O(1)

## 边界条件

- n = 0：总时间 = 任务数
- 只有一种任务：`(maxCount-1) × (n+1) + 1`

## 变式

- 具体调度序列：模拟优先队列（PQ），每轮取 n+1 个任务执行
- 冷却时间不同：每个任务独立冷却，PQ + 冷却队列

## 易错点

- 推导公式中的 `+1` 代表第一轮的最后一次执行后排完；`+ sameMax` 因为如果有多个频次最高的任务，最后一个时间片需要包含它们
- 结果不能小于 `tasks.length`

## 面试追问

- **推导公式的思路？** 把最高频任务看作骨架，其他任务填充间隔

## 关联题

- 同套路：[45. 跳跃游戏 II](45-jump-game-ii.md) —— 贪心 + 边界计算
- 进阶：[406. 根据身高重建队列](406-queue-reconstruction-by-height.md) —— 贪心排序
- 知识点：贪心 + 数学推导模式见[贪心](algorithms/12-贪心/README.md)

