# 238. Product of Array Except Self (Medium)

## 题目

给定一个整数数组 `nums`，返回数组 `answer`，其中 `answer[i]` 等于 `nums` 中除 `nums[i]` 之外其余各元素的乘积。不使用除法，时间复杂度 O(n)。

**示例**：
```
输入: nums = [1, 2, 3, 4]
输出: [24, 12, 8, 6]
```

## 思路

**左右乘积法**：
- 每个位置的结果 = 左边所有数的乘积 × 右边所有数的乘积
- 第一遍从左到右累积左侧乘积
- 第二遍从右到左累积右侧乘积，直接乘到结果数组

这样只用一个输出数组，额外空间 O(1)（不计输出数组）。

## 代码

```java
public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    result[0] = 1;
    // 左侧累积
    for (int i = 1; i < n; i++) {
        result[i] = result[i - 1] * nums[i - 1];
    }
    // 右侧累积
    int right = 1;
    for (int i = n - 1; i >= 0; i--) {
        result[i] *= right;
        right *= nums[i];
    }
    return result;
}
```

## 复杂度

- **时间**：O(n) — 两趟遍历
- **空间**：O(1) — 不计输出数组

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
