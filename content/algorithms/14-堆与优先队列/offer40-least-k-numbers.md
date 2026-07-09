# 剑指 Offer 40. 最小的 K 个数

频次 ★★★★ · 难度 🟢 · 高频：全厂

## 题目

数组 nums 和整数 k，返回最小的 k 个数（任意顺序）。

## 思路

**最大堆**：维护一个大小为 k 的**最大堆**（比堆顶大就弹出再入堆），遍历结束后堆中就是最小的 k 个。

也可以用快速选择（partition），和 215 对称（第 k 小 vs 第 k 大）。

## 代码

```java
public int[] getLeastNumbers(int[] arr, int k) {
    if (k == 0) return new int[0];
    PriorityQueue<Integer> pq = new PriorityQueue<>((a, b) -> b - a); // 最大堆
    for (int n : arr) {
        if (pq.size() < k) {
            pq.offer(n);
        } else if (n < pq.peek()) {
            pq.poll();
            pq.offer(n);
        }
    }
    int[] res = new int[k];
    for (int i = 0; i < k; i++) res[i] = pq.poll();
    return res;
}
```

## 复杂度

- **时间**：O(n log k)
- **空间**：O(k)

## 边界条件

- k = 0：返回空数组
- k = n：返回全部
- k > n：理论上不会出现，但返回全部

## 变式

- **[215. 数组中的第 K 大](215-kth-largest-element-in-an-array.md)**：相反方向——第 k 大用最小堆
- 快速选择版：O(n) 平均，但会修改原数组

## 易错点

- 求最小 k 个用**最大堆**（堆顶是堆中最大元素，比它小的才入堆），不要和 215 的最小堆搞混

## 关联题

- 同套路：[215. 数组中的第 K 大](215-kth-largest-element-in-an-array.md) —— 相反方向
- 进阶：[347. 前 K 个高频元素](347-top-k-frequent-elements.md) —— 按频率维度
- 知识点：Top K 的"最大堆/最小堆"选择见[堆与优先队列](algorithms/14-堆与优先队列/README.md)

