# 295. 数据流的中位数（Find Median from Data Stream）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

设计数据结构，支持添加整数和查询当前中位数。

## 思路

**双堆法**：一个**最大堆**存较小的一半，一个**最小堆**存较大的一半，保证两个堆的大小差距 ≤ 1。

加数时统一先加到最大堆，然后把最大堆堆顶弹出到最小堆，再平衡两个堆的大小。这样最小堆堆顶和最大堆堆顶始终是中间的两个候选。

## 代码

```java
class MedianFinder {
    private PriorityQueue<Integer> left;   // 最大堆（存小的一半）
    private PriorityQueue<Integer> right;  // 最小堆（存大的一半）

    public MedianFinder() {
        left = new PriorityQueue<>((a, b) -> b - a);
        right = new PriorityQueue<>();
    }

    public void addNum(int num) {
        left.offer(num);
        right.offer(left.poll());          // 最大堆的最大值 → 最小堆
        if (left.size() < right.size()) {
            left.offer(right.poll());      // 保持 left 长度 ≥ right
        }
    }

    public double findMedian() {
        if (left.size() > right.size())
            return left.peek();
        return (left.peek() + right.peek()) / 2.0;
    }
}
```

## 复杂度

- **addNum**：O(log n)
- **findMedian**：O(1)
- **空间**：O(n)

## 边界条件

- 只有一个元素：返回该元素
- 已加大量数后交替奇偶：平衡逻辑保证正确

## 变式

- **[480. 滑动窗口中位数](https://leetcode.cn/problems/sliding-window-median/)**：滑动窗口 + 双堆 + 懒删除
- 用有序集合（TreeMap）也能做，但双堆是面试标准答案
- 数据流第 k 大（百分位数）：扩展双堆为 k-堆

## 易错点

- **最大堆用 `(a, b) -> b - a`**：PriorityQueue 默认最小堆，需要反转比较器
- `addNum` 中的平衡顺序：先左→右，再右→左，保证左永远≥右且差值≤1
- 中位数是 `double`，不能整除当偶数长度时丢失小数

## 面试追问

- **为什么用两个堆而不是有序列表 + 二分插入？** 有序列表插入 O(n)，双堆 O(log n)
- **数据量巨大（内存不够存放全部数据）怎么办？** 分桶统计（直方图）近似中位数，或外部排序

## 关联题

- 同套路：[215. 数组中的第 K 大](215-kth-largest-element-in-an-array.md) —— 单堆 Top K
- 进阶：[347. 前 K 个高频元素](347-top-k-frequent-elements.md) —— 堆 + 哈希
- 知识点：堆 + 平衡模板见[堆与优先队列](algorithms/14-堆与优先队列/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
