# 31. Next Permutation (Medium)

## 题目

给定一个整数数组，表示一个排列，原地修改为字典序中的下一个更大的排列。如果不存在更大的排列，则修改为最小的排列（升序）。

**示例**：
```
输入: [1, 2, 3]
输出: [1, 3, 2]

输入: [3, 2, 1]
输出: [1, 2, 3]
```

## 思路

三步法：
1. **从右向左**找第一个下降的位置 `i`（即 `nums[i] < nums[i+1]`）
2. **从右向左**找第一个大于 `nums[i]` 的元素 `j`，交换 `nums[i]` 和 `nums[j]`
3. **翻转** `i+1` 到末尾的部分（使其变为最小排列）

如果找不到下降位置，说明当前是最大排列，直接翻转整个数组。

例如 `[1,3,5,4,2]`：
- 找到 i=1（3<5）
- 找到 j=3（4 是从右第一个大于 3 的）
- 交换 → `[1,4,5,3,2]`
- 翻转后面 → `[1,4,2,3,5]`

## 代码

```java
public void nextPermutation(int[] nums) {
    int n = nums.length;
    // 1. 找第一个下降位置
    int i = n - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) i--;
    
    if (i >= 0) {
        // 2. 找第一个大于 nums[i] 的元素并交换
        int j = n - 1;
        while (nums[j] <= nums[i]) j--;
        swap(nums, i, j);
    }
    // 3. 翻转 i+1 到末尾
    reverse(nums, i + 1, n - 1);
}

private void swap(int[] nums, int i, int j) {
    int temp = nums[i]; nums[i] = nums[j]; nums[j] = temp;
}

private void reverse(int[] nums, int start, int end) {
    while (start < end) swap(nums, start++, end--);
}
```

## 复杂度

- **时间**：O(n) — 最多三次线性扫描
- **空间**：O(1) — 原地操作

---

[← 返回训练计划](../社招算法训练计划.md)
