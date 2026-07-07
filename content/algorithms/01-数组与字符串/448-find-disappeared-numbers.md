# 448. Find All Numbers Disappeared in an Array (Easy)

## 题目

给定一个整数数组，其中 `1 <= nums[i] <= n`（n 为数组长度），有些元素出现两次，有些出现一次。找出所有在 [1, n] 范围内没有出现在数组中的数字。

**示例**：
```
输入: nums = [4, 3, 2, 7, 8, 2, 3, 1]
输出: [5, 6]
```

## 思路

**原地标记法**：
- 对于每个数 `nums[i]`，将其对应索引位置 `nums[i]-1` 处的值标记为负数
- 第二次遍历时，正值所在的位置就是缺失的数字

因为所有值都在 [1, n] 范围内，所以可以安全地用负号来标记"已出现"。

## 代码

```java
public List<Integer> findDisappearedNumbers(int[] nums) {
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < nums.length; i++) {
        int index = Math.abs(nums[i]) - 1;
        if (nums[index] > 0) {
            nums[index] = -nums[index];
        }
    }
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] > 0) {
            result.add(i + 1);
        }
    }
    return result;
}
```

## 复杂度

- **时间**：O(n) — 两趟遍历
- **空间**：O(1) — 不计输出数组

---

[← 返回训练计划](../社招算法训练计划.md)
