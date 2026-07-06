# 152. Maximum Product Subarray (Medium)

## 题目

给定一个整数数组 `nums`，找到乘积最大的连续子数组（至少包含一个元素），返回其最大乘积。

**示例**：
```
输入: nums = [2, 3, -2, 4]
输出: 6
解释: 子数组 [2,3] 乘积最大为 6
```

## 思路

**双变量法**（处理负数）：
- 因为负数×负数=正数，所以要同时维护**最大值**和**最小值**
- 遇到负数时，最大值和最小值会互换
- 每一步更新 `maxSoFar` 和 `minSoFar`，并记录全局最大值

关键点：遇到 0 时，最大值和最小值都会被重置为 0（或与当前元素比较）。

## 代码

```java
public int maxProduct(int[] nums) {
    int maxSoFar = nums[0], minSoFar = nums[0], result = nums[0];
    for (int i = 1; i < nums.length; i++) {
        int n = nums[i];
        if (n < 0) {
            int temp = maxSoFar;
            maxSoFar = minSoFar;
            minSoFar = temp;
        }
        maxSoFar = Math.max(n, maxSoFar * n);
        minSoFar = Math.min(n, minSoFar * n);
        result = Math.max(result, maxSoFar);
    }
    return result;
}
```

## 复杂度

- **时间**：O(n) — 一次遍历
- **空间**：O(1) — 三个变量

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
