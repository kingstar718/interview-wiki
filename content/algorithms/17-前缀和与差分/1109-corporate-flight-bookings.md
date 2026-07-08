# 1109. 航班预订统计（Corporate Flight Bookings）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

`[first, last, seats]` 表示预定的区间，返回每个航班的预定总数。

## 思路

**差分数组**：`diff[first] += seats; diff[last+1] -= seats`，前缀和恢复。

## 代码

```java
public int[] corpFlightBookings(int[][] bookings, int n) {
    int[] diff = new int[n + 2];
    for (int[] b : bookings) {
        diff[b[0]] += b[2];
        diff[b[1] + 1] -= b[2];
    }
    int[] res = new int[n];
    int cur = 0;
    for (int i = 0; i < n; i++) {
        cur += diff[i + 1];
        res[i] = cur;
    }
    return res;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(n)

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 同套路：[1094. 拼车](1094-car-pooling.md)

---

[← 返回训练计划](社招算法训练计划.md)
