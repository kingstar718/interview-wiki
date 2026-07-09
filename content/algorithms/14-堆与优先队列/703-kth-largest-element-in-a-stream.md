# 703. 数据流中的第 K 大元素（Kth Largest Element in a Stream）

频次 ★★★ · 难度 🟢 · 高频：美团

## 题目

设计类，支持添加新值并返回当前第 k 大元素。

**示例**：
```
KthLargest kth = new KthLargest(3, [4,5,8,2]);
kth.add(3) → 4
kth.add(5) → 5
```

## 思路

**大小为 k 的最小堆**：初始化时将数组入堆，只保留最大的 k 个。每次 add 时如果堆未满直接入堆，否则比堆顶大则弹出堆顶后入堆。堆顶即为第 k 大。

## 代码

```java
class KthLargest {
    private PriorityQueue<Integer> pq;
    private int k;

    public KthLargest(int k, int[] nums) {
        this.k = k;
        pq = new PriorityQueue<>();            // 最小堆
        for (int n : nums) add(n);             // 复用 add 逻辑
    }

    public int add(int val) {
        pq.offer(val);
        if (pq.size() > k) pq.poll();
        return pq.peek();
    }
}
```

## 复杂度

- **时间**：O(n log k) 初始化，O(log k) add
- **空间**：O(k)

## 边界条件

- 输入数组长度 < k：初始化时堆不满，add 时会逐步填满
- 添加的元素小于堆顶：不入堆（弹出后又进），堆顶不变

## 变式

- **[215. 数组中的第 K 大](215-kth-largest-element-in-an-array.md)**：静态数组版
- **[295. 数据流的中位数](295-find-median-from-data-stream.md)**：双堆求中位数

## 易错点

- 初始化时复用 `add` 方法——代码更简洁
- 应对 `nums.length < k` 的情况：堆不满时直接放堆顶

## 面试追问

- **如果频繁调用 add，怎么优化？** 当前已经是 O(log k)，用最小堆是最优解。如果 k 非常小或数据有界，可以用数组 + 插入排序

## 关联题

- 同套路：[215. 数组中的第 K 大](215-kth-largest-element-in-an-array.md) —— 静态版
- 进阶：[295. 数据流的中位数](295-find-median-from-data-stream.md) —— 双堆
- 知识点：堆的数据流 Top K 模板见[堆与优先队列](algorithms/14-堆与优先队列/README.md)

