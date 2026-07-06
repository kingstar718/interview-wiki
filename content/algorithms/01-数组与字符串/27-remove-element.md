# 27. Remove Element (Easy)

## 题目

给定一个数组和一个值 `val`，原地移除所有等于 `val` 的元素，返回新长度。

**示例**：
```
输入: nums = [3, 2, 2, 3], val = 3
输出: length = 2, nums 前两位为 [2, 2]
```

## 思路

快慢双指针：
- `fast` 遍历数组
- `slow` 指向下一个非 `val` 元素应放的位置
- 当 `nums[fast] != val` 时，复制到 `slow` 位置

## 代码

```java
public int removeElement(int[] nums, int val) {
    int slow = 0;
    for (int fast = 0; fast < nums.length; fast++) {
        if (nums[fast] != val) {
            nums[slow] = nums[fast];
            slow++;
        }
    }
    return slow;
}
```

## 复杂度

- **时间**：O(n) — 遍历一次
- **空间**：O(1) — 只用两个指针

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
