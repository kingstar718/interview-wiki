---
topics:
  - 排序与堆
---

# 215. 数组中的第 K 个最大元素（Kth Largest Element in an Array）

频次 ★★★★★ · 难度 🟡 · 高频：全厂

## 题目

整数数组 nums 和整数 k，返回数组中第 k 个**最大**元素（非第 k 个不同元素）。

## 思路

**两种主流解法**：

**解法 1：快速选择（Quick Select）** — 快排的 partition 思想，期望 O(n)。随机选 pivot 后比它大的放左边，比它小的放右边，判断第 k 个最大在哪边递归。

**解法 2：大小为 k 的最小堆** — 维护一个大小为 k 的最小堆，遍历数组时如果堆大小 < k 直接入堆，否则如果当前元素 > 堆顶，弹出堆顶后入堆。遍历结束堆顶就是第 k 大。O(n log k)。

面试中推荐先答堆解法（稳定 O(n log k)），再答快速选择（平均 O(n) 但最坏 O(n²)）。

## 代码

```java
// 堆解法
public int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> pq = new PriorityQueue<>();  // 最小堆
    for (int n : nums) {
        pq.offer(n);
        if (pq.size() > k) pq.poll();
    }
    return pq.peek();
}
```

```java
// 快速选择
public int findKthLargest(int[] nums, int k) {
    return quickSelect(nums, 0, nums.length - 1, k);
}

private int quickSelect(int[] nums, int l, int r, int k) {
    int idx = partition(nums, l, r);
    int rank = idx - l + 1;                    // nums[idx] 是第 rank 大
    if (rank == k) return nums[idx];
    if (rank < k) return quickSelect(nums, idx + 1, r, k - rank);
    return quickSelect(nums, l, idx - 1, k);
}

private int partition(int[] nums, int l, int r) {
    int pivot = nums[r];                       // 简化版选最右
    int i = l;
    for (int j = l; j < r; j++) {
        if (nums[j] >= pivot) swap(nums, i++, j);
    }
    swap(nums, i, r);
    return i;
}

private void swap(int[] nums, int i, int j) {
    int t = nums[i]; nums[i] = nums[j]; nums[j] = t;
}
```

## 复杂度

- **堆**：时间 O(n log k)，空间 O(k)
- **快速选择**：平均 O(n)，最坏 O(n²)，空间 O(log n)

## 边界条件

- k = 1：最大值
- k = n：最小值
- 含重复值：正常处理（不计"不同"）

## 变式

- **[347. 前 K 个高频元素](347-top-k-frequent-elements.md)**：频率维度，先用哈希计数再堆
- **[295. 数据流的中位数](295-find-median-from-data-stream.md)**：两个堆（最大堆 + 最小堆）
- **[703. 数据流中的第 K 大](703-kth-largest-element-in-a-stream.md)**：动态数据流，持续维护一个堆

## 易错点

- 第 k **大**不是第 k 个不同元素，重复值分别计数
- 堆解法用**最小堆**：堆顶是堆中最小元素，堆中保留最大的 k 个，堆顶就是第 k 大
- 快速选择 partition 的边界条件小心递归偏移（`k - rank`）

## 面试追问

- **为什么堆解法不直接用最大堆？** 最大堆需要保留全部 n 个元素，O(n log n)。最小堆只保留 k 个，O(n log k)，k 远小于 n 时优势明显

## 关联题

- 同套路：[347. 前 K 个高频元素](347-top-k-frequent-elements.md) —— 堆 + 哈希
- 进阶：[295. 数据流的中位数](295-find-median-from-data-stream.md) —— 双堆
- 知识点：堆的 Top K 模式见[堆与优先队列](排序与堆.md)

