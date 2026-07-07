# 88. Merge Sorted Array (Easy)

## 题目

给定两个有序数组 `nums1` 和 `nums2`，将 `nums2` 合并到 `nums1` 中。`nums1` 初始长度为 `m`，但有足够空间（总长度 `m+n`）容纳 `nums2` 的 `n` 个元素。

**示例**：
```
输入: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
输出: [1,2,2,3,5,6]
```

## 思路

**从后往前**合并，避免覆盖 `nums1` 的原始数据：
- 三个指针：`p1` 指向 `nums1` 末尾有效元素，`p2` 指向 `nums2` 末尾，`p` 指向合并后末尾位置
- 比较 `nums1[p1]` 和 `nums2[p2]`，较大者放到 `nums1[p]`
- 如果 `nums2` 还有剩余，直接拷贝（`nums1` 已空时无需处理，元素已在正确位置）

## 代码

```java
public void merge(int[] nums1, int m, int[] nums2, int n) {
    int p1 = m - 1, p2 = n - 1, p = m + n - 1;
    while (p1 >= 0 && p2 >= 0) {
        if (nums1[p1] > nums2[p2]) {
            nums1[p--] = nums1[p1--];
        } else {
            nums1[p--] = nums2[p2--];
        }
    }
    // nums2 剩余元素拷贝（nums1 剩余时已在正确位置）
    while (p2 >= 0) {
        nums1[p--] = nums2[p2--];
    }
}
```

## 复杂度

- **时间**：O(m + n) — 每个元素最多访问一次
- **空间**：O(1) — 只用指针变量

---

[← 返回训练计划](../社招算法训练计划.md)
