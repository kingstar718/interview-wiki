# 41. First Missing Positive (Hard)

## 题目

给定一个未排序的整数数组，找出其中没有出现的最小正整数。要求时间复杂度 O(n)，空间复杂度 O(1)。

**示例**：
```
输入: nums = [3, 4, -1, 1]
输出: 2
```

## 思路

**原地哈希**（索引映射）：
- 长度为 n 的数组，缺失的最小正整数一定在 [1, n+1] 范围内
- 目标：让 `nums[i] = i + 1`（即值为 1 的放索引 0，值为 2 的放索引 1...）
- 遍历数组，将每个在 [1, n] 范围内的数放到正确位置
- 再遍历一次，第一个 `nums[i] != i + 1` 的位置就是答案

注意：重复元素和超出范围的数可以忽略。

## 代码

```java
public int firstMissingPositive(int[] nums) {
    int n = nums.length;
    for (int i = 0; i < n; i++) {
        while (nums[i] > 0 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
            int temp = nums[nums[i] - 1];
            nums[nums[i] - 1] = nums[i];
            nums[i] = temp;
        }
    }
    for (int i = 0; i < n; i++) {
        if (nums[i] != i + 1) return i + 1;
    }
    return n + 1;
}
```

## 复杂度

- **时间**：O(n) — 每个元素最多被交换一次到正确位置
- **空间**：O(1) — 原地操作

---

[← 返回训练计划](../社招算法训练计划.md)
