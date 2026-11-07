---
topics:
  - 排序与堆
  - 数组与字符串
techniques:
  - 区间排序
---

# 56. 合并区间（Merge Intervals）

频次 ★★★★ · 难度 🟡 · 高频：全厂

## 题目

区间集合，重叠的合并。

**示例**：
```
输入: intervals = [[1,3],[2,6],[8,10],[15,18]]
输出: [[1,6],[8,10],[15,18]]
```

## 思路

**排序 + 贪心合并**：按 start 排序，遍历时比较当前 start 与上一区间 end，重叠则合并（取最大 end），否则新增区间。

## 代码

```java
public int[][] merge(int[][] intervals) {
    if (intervals.length == 0) return new int[0][];
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    List<int[]> res = new ArrayList<>();
    int start = intervals[0][0], end = intervals[0][1];
    for (int i = 1; i < intervals.length; i++) {
        if (intervals[i][0] <= end) {            // 重叠
            end = Math.max(end, intervals[i][1]);
        } else {
            res.add(new int[]{start, end});
            start = intervals[i][0];
            end = intervals[i][1];
        }
    }
    res.add(new int[]{start, end});
    return res.toArray(new int[0][]);
}
```

## 区间线三题对照（435 / 763 / 56）

| | 435 无重叠 | 763 划分字母 | 56 合并 |
|---|---|---|---|
| 排序键 | **右端点** end | 不排（扫描本身天然升序，last[] 预处理） | **左端点** start |
| 相接判定 | `start >= keepEnd` → 保留（相接**不算**重叠） | `i == end` 收割（相接无定义——墙=末现并集） | `start <= curEnd` → 合并（相接**算**重叠） |
| 输出 | 一个数（最少删除 = n - keep） | 段长列表 | 不重叠区间列表 |
| 几何画面 | 先手最短优先：墙挡手，肥的被丢 | 墙=字符末现并集，墙落回脚下就切 | 后手覆盖：墙吸收，重叠拼成最长 |

三题共享一个骨架：**排序（或天然序）+ 参照物 end/curEnd/last[c] + 单次扫描**——排序键决定"谁先手"（end 小先手 = 留余地；start 小先手 = 最早可能开始），相接规则由题设明示（435 不算、56 算，代码只差 `>=` 与 `<=` 一个字符）。

## 复杂度

- **时间**：O(n log n) —— 排序
- **空间**：O(log n) —— 排序栈

## 边界条件

- 空集合：返回空
- 全重叠：合并为一个区间
- 全不重叠：原样输出

## 变式

- **[435. 无重叠区间](435-non-overlapping-intervals.md)**：移除最少区间使无重叠
- **[252. 会议室](252-meeting-rooms.md)**：判断能否参加所有会议
- **[253. 会议室 II](253-meeting-rooms-ii.md)**：最少会议室数量

## 易错点

- 排序用 `a[0] - b[0]`（按 start），不是 end
- 合并时 end 取两个区间的 max，不是直接取第二个的 end
- 最后一个区间在循环外加入结果

## 面试追问

- **如果区间达到百万级？** 外部排序（外存归并）+ 流式合并

## 关联题

- 同套路：[252. 会议室](252-meeting-rooms.md) —— 区间不重叠判断
- 进阶：[253. 会议室 II](253-meeting-rooms-ii.md) —— 扫描线
- 知识点：区间排序 + 贪心合并模板见[排序](../排序与堆.md)

