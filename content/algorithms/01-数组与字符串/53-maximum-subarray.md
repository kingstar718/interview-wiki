# 53. Maximum Subarray (Easy)

## 题目

给定一个整数数组 `nums`，找到一个具有最大和的连续子数组（至少包含一个元素），返回其最大和。

**示例**：
```
输入: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
输出: 6
解释: 连续子数组 [4,-1,2,1] 的和最大为 6
```

## 思路

**Kadane 算法**：
- 遍历数组，维护当前连续子数组和 `currentSum`
- 如果 `currentSum < 0`，说明前面的和对后续是拖累，从当前元素重新开始
- 用 `maxSum` 记录历史最大值

核心思想：负的累加和只会拖累后面的子数组，所以一旦变成负数就舍弃。

## 代码

```java
public int maxSubArray(int[] nums) {
    int maxSum = nums[0], currentSum = nums[0];
    for (int i = 1; i < nums.length; i++) {
        currentSum = Math.max(nums[i], currentSum + nums[i]);
        maxSum = Math.max(maxSum, currentSum);
    }
    return maxSum;
}
```

## 复杂度

- **时间**：O(n) — 一次遍历
- **空间**：O(1) — 两个变量

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
