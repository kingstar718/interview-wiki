# 4. 寻找两个正序数组的中位数（Median of Two Sorted Arrays）

频次 ★★★★★ · 难度 🔴 · 高频：阿里/字节/腾讯

## 题目

两个有序数组 `nums1`、`nums2`，求它们合并后的中位数，要求 **O(log(m+n))**。

**示例**：
```
输入: nums1 = [1,3], nums2 = [2]
输出: 2.0

输入: nums1 = [1,2], nums2 = [3,4]
输出: 2.5
```

## 思路

O(log(m+n)) 提示只能用**二分**。核心技巧：**不合并，用二分找到分割线**。

把两个数组各切一刀分成左右两半，保证：
1. 左半元素个数 = 右半元素个数（或左比右多 1）
2. 左半所有元素 ≤ 右半所有元素

此时中位数 = 左半最大值（奇数长度）或 (左半 max + 右半 min) / 2（偶数长度）。

在**较短数组**上二分确定分割位置 i，在较长数组上对应分割位置 j 由"左半总个数"约束推出：
```
j = (m + n + 1) / 2 - i
```
检查 `nums1[i-1] ≤ nums2[j]` 且 `nums2[j-1] ≤ nums1[i]` 即找到合法分割。

## 代码

```java
public double findMedianSortedArrays(int[] nums1, int[] nums2) {
    // 保证 nums1 是较短的数组
    if (nums1.length > nums2.length) {
        int[] tmp = nums1; nums1 = nums2; nums2 = tmp;
    }
    int m = nums1.length, n = nums2.length;
    int totalLeft = (m + n + 1) / 2;        // 左半需要多少个元素

    int l = 0, r = m;                       // 在 nums1 上二分
    while (l < r) {
        int i = l + (r - l + 1) / 2;        // 右中位数（nums1 左半长度）
        int j = totalLeft - i;              // nums2 左半长度
        if (nums1[i - 1] > nums2[j])        // i 太大了，需要左移
            r = i - 1;
        else
            l = i;
    }

    int i = l, j = totalLeft - i;

    int leftMax = Math.max(
        i == 0 ? Integer.MIN_VALUE : nums1[i - 1],
        j == 0 ? Integer.MIN_VALUE : nums2[j - 1]
    );
    if ((m + n) % 2 == 1) return leftMax;   // 奇数长度，中位数在左半

    int rightMin = Math.min(
        i == m ? Integer.MAX_VALUE : nums1[i],
        j == n ? Integer.MAX_VALUE : nums2[j]
    );
    return (leftMax + rightMin) / 2.0;
}
```

## 复杂度

- **时间**：O(log(min(m, n))) —— 在较短数组上二分
- **空间**：O(1)

## 边界条件

- 一数组为空：退化为在单个有序数组上找中位数
- 分割线在数组端点（i=0 / i=m / j=0 / j=n）：对应侧用 ±∞ 代替，不影响大小比较
- 总长度为奇数/偶数：奇数取左半最大，偶数取左右平均

## 变式

- 找第 k 小的数（k 任意）：递归淘汰法，每次淘汰 k/2 个，O(log k)。中位数 = 第 (m+n+1)/2 小
- 数据流中位数：两个堆（大顶堆 + 小顶堆），O(log n) 插入、O(1) 查询

## 易错点

- **数组长度奇偶统一处理**：`(m + n + 1) / 2` 这个公式在奇偶下同时有效（整数除法截断），无须分支
- **i 从 0 到 m**（含两端）：i 表示 nums1 左半元素个数，可以为 0（nums1 全在右半）或 m（nums1 全在左半）
- **始终在短数组上二分**：在长数组上二分会导致 j 可能越界（负数或超过 n）；且 O(log(min)) 更优
- 取 `(r - l + 1) / 2` 右中位数：防止 `l = i` 后的死循环

## 面试追问

- **为什么要在短数组上二分？** 因为 j = totalLeft - i，i 确定后 j 自然确定；在长数组上二分时短数组的下标可能越界。答出"保证 j 不越界"再补一句"复杂度从 O(log(max)) 降到 O(log(min))"
- **中位数的本质定义？** 把有序集合平分，左半 ≤ 右半。这题的答案不是"找中间下标"而是"找合法分割"——二分的是分割条件而不是位置

## 关联题

- 同套路：[33. 搜索旋转排序数组](33-search-in-rotated-sorted-array.md) —— 二分在复杂有序场景的泛化
- 进阶：[295. 数据流的中位数](295-find-median-from-data-stream.md) —— 动态数据的中位数，堆解法
- 知识点：[二分查找](algorithms/07-二分查找/README.md)边界模板；算法笔记：第 k 小元素通用解法

