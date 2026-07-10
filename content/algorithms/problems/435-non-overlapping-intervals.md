---
topics:
  - 动态规划与贪心
techniques:
  - 贪心证明
---

# 435. 无重叠区间（Non-overlapping Intervals）

频次 ★★★★ · 难度 🟡 · 高频：字节

## 题目

区间集合 `[start, end]`，移除最少数量的区间使剩余区间互不重叠。

**示例**：
```
输入: [[1,2],[2,3],[3,4],[1,3]]
输出: 1  （移除 [1,3]）
```

## 思路

**贪心（选最早结束的）**：按 end 排序，每次选 end 最小且与上一区间不重叠的区间。总数 - 可选区间数 = 最少移除数。

## 代码

```java
public int eraseOverlapIntervals(int[][] intervals) {
    if (intervals.length == 0) return 0;
    Arrays.sort(intervals, (a, b) -> a[1] - b[1]);   // 按 end 升序
    int count = 1;                                    // 至少选一个
    int end = intervals[0][1];
    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] >= end) {                 // 不重叠
            count++;
            end = intervals[i][1];
        }
    }
    return intervals.length - count;
}
```

## 复杂度

- **时间**：O(n log n) —— 排序
- **空间**：O(1)

## 边界条件

- 空集合：返回 0
- 一个区间：不移除，返回 0

## 变式

- **[56. 合并区间](https://leetcode.cn/problems/merge-intervals/)**：合并重叠区间
- **[252. 会议室](https://leetcode.cn/problems/meeting-rooms/)**：判断能否参加所有会议
- **[253. 会议室 II](https://leetcode.cn/problems/meeting-rooms-ii/)**：最少会议室数量，一维扫描线

## 易错点

- 按 end 排序不是 start，因为"最早结束"给后面留最大空间——这是区间贪心的经典结论
- `intervals[i][0] >= end` 可以相等（[1,2] 和 [2,3] 不算重叠）

## 面试追问

- **为什么按 end 排序最优？** 局部最优（选当前最早结束的）给剩余区间留最大空间，进而达到全局最优

## 关联题

- 同套路：[56. 合并区间](https://leetcode.cn/problems/merge-intervals/) —— 区间排序类
- 进阶：[763. 划分字母区间](763-partition-labels.md) —— 同为区间贪心
- 知识点：区间贪心的"最早结束优先"模板见[贪心](动态规划与贪心.md)

