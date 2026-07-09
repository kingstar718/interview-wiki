---
topics:
  - 排序与堆
---

# 252. 会议室（Meeting Rooms）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

区间数组 `[start, end]`，判断能否参加所有会议（区间是否互不重叠）。

## 思路

**排序 + 邻接比较**：按 start 排序，遍历检查每个区间的 start 是否 ≥ 上一区间的 end。

## 代码

```java
public boolean canAttendMeetings(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] < intervals[i - 1][1]) return false;
    }
    return true;
}
```

## 复杂度

- **时间**：O(n log n)
- **空间**：O(1)

## 边界条件

- 0/1 个会议：true

## 变式

- **[56. 合并区间](56-merge-intervals.md)**：合并重叠区间
- **[253. 会议室 II](253-meeting-rooms-ii.md)**：最少会议室

## 易错点

- 比较条件 `intervals[i][0] < intervals[i-1][1]`，不是 `<=`（边界相接不算冲突）

## 面试追问

- **252 和 253 的关系？** 252 判能否（布尔值），253 算最少数量（数值）。一个排序 + 遍历，一个排序 + 扫描线

## 关联题

- 同套路：[56. 合并区间](56-merge-intervals.md)、[253. 会议室 II](253-meeting-rooms-ii.md)

