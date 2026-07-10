---
topics:
  - 排序与堆
techniques:
  - 区间排序
---

# 253. 会议室 II（Meeting Rooms II）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

区间数组 `[start, end]`，求所需的最少会议室数量。

## 思路

**扫描线（上下车法）**：将所有时间点摊平，start = +1（入场），end = -1（离场）。排序后扫描累加，过程中的最大值即为最少会议室。

## 代码

```java
public int minMeetingRooms(int[][] intervals) {
    List<int[]> points = new ArrayList<>();
    for (int[] iv : intervals) {
        points.add(new int[]{iv[0], 1});    // 开始 +1
        points.add(new int[]{iv[1], -1});   // 结束 -1
    }
    points.sort((a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);
    int count = 0, max = 0;
    for (int[] p : points) {
        count += p[1];
        max = Math.max(max, count);
    }
    return max;
}
```

## 复杂度

- **时间**：O(n log n)
- **空间**：O(n)

## 边界条件

- 空：返回 0
- 一个会议：返回 1

## 变式

- **[56. 合并区间](56-merge-intervals.md)**：合并
- **[252. 会议室](252-meeting-rooms.md)**：判能否参加

## 易错点

- 排序时同时间点 `+1` 在前 `-1` 在后（先开始再结束），否则峰值会被低估
- 用数组比两个独立列表更简洁

## 面试追问

- **扫描线和差分数组的关系？** 本质相同——扫描线是差分在非整数点上的特例。二维平面扫描线见 [218. 天际线](https://leetcode.cn/problems/the-skyline-problem/)

## 关联题

- 同套路：[56. 合并区间](56-merge-intervals.md) —— 区间合并
- 进阶：[435. 无重叠区间](435-non-overlapping-intervals.md) —— 移除最少区间
- 知识点：扫描线模板见[排序](排序与堆.md)

