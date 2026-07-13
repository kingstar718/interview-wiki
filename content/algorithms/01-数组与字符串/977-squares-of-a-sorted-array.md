# 977. 有序数组的平方（Squares of a Sorted Array）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

给定非递减排序的整数数组 `nums`，返回每个元素平方后的新数组，要求也按非递减排序。

## 思路

**双指针从两端向中间**：数组元素平方后，最大值一定在两端（负数的平方可能很大）。`left` 和 `right` 指针比较平方值，大的放入结果数组末尾，向中间移动。

## 代码

```java
public int[] sortedSquares(int[] nums) {
    int n = nums.length;
    int[] res = new int[n];
    int left = 0, right = n - 1, idx = n - 1;
    while (left <= right) {
        int lv = nums[left] * nums[left];
        int rv = nums[right] * nums[right];
        if (lv > rv) {
            res[idx--] = lv;
            left++;
        } else {
            res[idx--] = rv;
            right--;
        }
    }
    return res;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(n) — 结果数组

## 边界条件

- 全正数：右指针一直左移，左指针不动
- 全负数：左指针一直右移，右指针不动
- 单个元素：直接返回其平方

## 变式

- 如果不要求非递减而是非递增：从两端取绝对值大的放结果数组开头即可

## 易错点

- 不能先平方再排序 O(nlogn)（面试追问"能不能优化"）
- 比较时用的是 `nums[left]` 和 `nums[right]` 的绝对值，等价于比较平方值

## 面试追问

- **为什么从两端向中间而非从中间向两端？** 平方后最大值在两端，从两端向中间天然得到降序，结果数组从后往前填充即可得到升序

## 关联题

- 同套路：[88. 合并两个有序数组](88-merge-sorted-array.md)（也是从后往前填充）