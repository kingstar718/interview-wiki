# 283. Move Zeroes (Easy)

## 题目

给定一个数组 `nums`，将所有 0 移动到数组末尾，同时保持非零元素的相对顺序。必须原地操作。

**示例**：
```
输入: nums = [0, 1, 0, 3, 12]
输出: nums 变为 [1, 3, 12, 0, 0]
```

## 思路

**快慢指针**：
- `slow` 指向下一个非零元素应放的位置
- `fast` 遍历数组
- 当 `nums[fast] != 0` 时，放到 `slow` 位置
- 最后将 `slow` 之后的位置全部补 0

也可以交换法：非零元素和 slow 位置交换（当 slow != fast 时），这样只需一趟。

## 代码

```java
public void moveZeroes(int[] nums) {
    int slow = 0;
    for (int fast = 0; fast < nums.length; fast++) {
        if (nums[fast] != 0) {
            if (slow != fast) {
                int temp = nums[slow];
                nums[slow] = nums[fast];
                nums[fast] = temp;
            }
            slow++;
        }
    }
}
```

## 复杂度

- **时间**：O(n) — 一次遍历
- **空间**：O(1) — 原地操作

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
