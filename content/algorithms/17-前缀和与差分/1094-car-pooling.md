# 1094. 拼车（Car Pooling）

频次 ★★★ · 难度 🟡 · 高频：快手

## 题目

`[num, from, to]` 表示上客数/上客点/下客点，容量 capacity，能否全程不超载。

## 思路

**差分数组**：`diff[i]` 表示 i 点的人数变化，遍历 trips 更新上下客；然后前缀和模拟看全程。

## 代码

```java
public boolean carPooling(int[][] trips, int capacity) {
    int[] diff = new int[1001];
    for (int[] t : trips) {
        diff[t[1]] += t[0];
        diff[t[2]] -= t[0];
    }
    int cur = 0;
    for (int d : diff) {
        cur += d;
        if (cur > capacity) return false;
    }
    return true;
}
```

## 复杂度

- **时间**：O(n + 1000)
- **空间**：O(1000)

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 同套路：[1109. 航班预订统计](1109-corporate-flight-bookings.md)

