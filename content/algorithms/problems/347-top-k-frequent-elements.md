---
topics:
  - 排序与堆
techniques:
  - 堆TopK
---

# 347. 前 K 个高频元素（Top K Frequent Elements）

频次 ★★★★ · 难度 🟡 · 高频：美团/字节

## 题目

整数数组 nums 和 k，返回出现频率最高的 k 个元素。

**示例**：
```
输入: nums = [1,1,1,2,2,3], k = 2
输出: [1,2]
```

## 思路

**哈希计数 + 最小堆**：

1. 先用 HashMap 统计每个元素出现频率
2. 维护一个大小为 k 的最小堆（按频率比较），遍历 map 条目保持堆中为频率最高的 k 个
3. 堆中的元素即为答案

## 代码

```java
public int[] topKFrequent(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int n : nums) freq.merge(n, 1, Integer::sum);

    PriorityQueue<Integer> pq = new PriorityQueue<>(
        Comparator.comparingInt(freq::get));           // 按频率比较

    for (int key : freq.keySet()) {
        pq.offer(key);
        if (pq.size() > k) pq.poll();
    }

    int[] res = new int[k];
    for (int i = k - 1; i >= 0; i--) res[i] = pq.poll();
    return res;
}
```

## 复杂度

- **时间**：O(n log k) —— 哈希 O(n)，堆 O(n log k)
- **空间**：O(n) —— HashMap

## 边界条件

- k = n：返回全部元素
- 全相同元素：返回该元素

## 变式

- **[692. 前 K 个高频单词](https://leetcode.cn/problems/top-k-frequent-words/)**：还要求字典序排序，优先级队列需要自定义比较器
- **[451. 根据字符出现频率排序](https://leetcode.cn/problems/sort-characters-by-frequency/)**：全量排序，不是只取前 k
- **[215. 数组中的第 K 大](215-kth-largest-element-in-an-array.md)**：无频次维度，直接比大小

## 易错点

- 堆按频率比较，但堆中存的是元素本身（不是频率值）
- 比较器 `Comparator.comparingInt(freq::get)` 需要用 map 辅助
- 结果数组从堆中弹出时是升序的（最小堆），注意是否需要逆序
- **求“前 K 大”用【小顶堆】**（堆顶是这 K 个里最小的，方便淘汰）；求“前 K 小”才用大顶堆——方向反了逻辑就崩
- **手写堆时 `swap` 三步别写错**：`int t = heap[i]; heap[i] = heap[j]; heap[j] = t;`。若写成 `heap[j] = heap[i]` 就是“复制而非交换”，原 `heap[j]` 丢失 → 结果出现重复值（典型症状：`[1,1,1,2,2,3] k=2` 输出 `[1,1]`）
- **纯 `int` 堆 + 编码**的写法：`code = 频率 * 10000 + (值 + 1000)`（频率放高位），解码 `值 = code % 10000 - 1000`。前提：`最大频率 × 值域宽度 < 2^31`；本题 `10^5 × 10^4 = 10^9` 安全。堆本身零依赖，算法层负责编码/解码

## 面试追问

- **如果 n 很大但 k 很小（比如 k=1）？** 直接一次遍历找最大频次，O(n)。堆的通用性不如特化解法——面试中体现适应性思维

## 关联题

- 同套路：[215. 数组中的第 K 大](215-kth-largest-element-in-an-array.md) —— Top K 模式
- 进阶：[295. 数据流的中位数](295-find-median-from-data-stream.md) —— 双堆变体
- 知识点：哈希 + 堆的"先计后堆"模式见[堆与优先队列](排序与堆.md)

