# 26. Remove Duplicates from Sorted Array (Easy)

## 题目

给定一个**有序**数组，原地删除重复元素，使每个元素只出现一次，返回新长度。不能使用额外空间，必须 O(1) 额外内存修改输入数组。

**示例**：
```
输入: nums = [1, 1, 2]
输出: length = 2, nums 前两位为 [1, 2]
```

## 思路

数组已排序，重复元素必然相邻。用快慢双指针：
- `slow` 指向下一个不重复元素应放的位置
- `fast` 遍历数组
- 当 `nums[fast] != nums[fast-1]` 时，说明遇到新元素，复制到 `slow` 位置

## 代码

```java
public int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    int slow = 1;
    for (int fast = 1; fast < nums.length; fast++) {
        if (nums[fast] != nums[fast - 1]) {
            nums[slow] = nums[fast];
            slow++;
        }
    }
    return slow;
}
```

## 复杂度

- **时间**：O(n) — 快指针遍历一次
- **空间**：O(1) — 只用了两个指针

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
