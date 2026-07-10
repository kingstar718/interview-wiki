---
topics:
  - 二分查找
techniques:
  - 二分边界
---

# 704. 二分查找（Binary Search）

频次 ★★★★ · 难度 🟢 · 高频：字节/腾讯/美团

## 题目

给定升序整型数组 `nums` 和目标值 `target`，返回 `target` 的下标；不存在返回 `-1`。

**示例**：
```
输入: nums = [-1,0,3,5,9,12], target = 9
输出: 4

输入: nums = [-1,0,3,5,9,12], target = 2
输出: -1
```

## 思路

经典的**左闭右闭**二分：`l = 0, r = n-1`，循环条件 `l <= r`，`mid = l + (r - l) / 2`。与 target 比较后，排除 `mid` 本身收缩区间。

## 代码

```java
public int search(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l <= r) {
        int mid = l + (r - l) / 2;
        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            l = mid + 1;
        } else {
            r = mid - 1;
        }
    }
    return -1;
}
```

## 复杂度

- **时间**：O(log n)
- **空间**：O(1)

## 边界条件

- 空数组：`l = 0, r = -1`，`l <= r` 不成立，直接返回 -1
- 单元素：比较后返回 0 或 -1
- target 不存在：循环结束返回 -1
- 重复值：返回任意一个匹配下标（题目不要求最左/最右）

## 变式

- **[34. 在排序数组中查找元素的第一个和最后一个位置](34-find-first-and-last-position-of-element-in-sorted-array.md)**：二分找左边界 + 右边界，区间划分条件改为 `>=` 和 `<=`
- **[33. 搜索旋转排序数组](33-search-in-rotated-sorted-array.md)**：旋转数组上二分，先判断哪半有序
- **[69. x 的平方根](69-sqrtx.md)**：二分答案空间而非数组
- **[74. 搜索二维矩阵](74-search-a-2d-matrix.md)**：二维矩阵拍平成一维二分

## 易错点

- **循环条件 `l <= r`（左闭右闭）与 `l < r`（左闭右开）不能混用**：选定一种写法后，`r` 初始化、收缩时是 `mid` 还是 `mid ± 1` 必须自洽。左闭右闭时 `r = n - 1`、收缩用 `mid ± 1`；左闭右开时 `r = n`、收缩用 `mid`。
- `mid = l + (r - l) / 2` 比 `(l + r) / 2` 安全——后者在 l + r 极大时可能溢出。
- 升序数组前提，面试时先确认输入是否有序。

## 面试追问

- **为什么不用 `(l + r) / 2`？** Java int 相加可能溢出，`l + (r - l) / 2` 避免溢出，效果相同。
- **左闭右闭 vs 左闭右开的区别？** 左闭右闭：区间 `[l, r]` 两端都包含，初始 `r = n-1`，循环 `l <= r`，收缩 `l = mid+1` / `r = mid-1`。左闭右开：区间 `[l, r)`，初始 `r = n`，循环 `l < r`，收缩 `l = mid+1` / `r = mid`。两者等价但写法固定不可混用。
- **二分查找的前提？** 数组有序，或者答案空间存在单调判定条件——见[二分查找](二分查找.md)。

## 关联题

- 同套路：[33. 搜索旋转排序数组](33-search-in-rotated-sorted-array.md) —— 旋转数组变体
- 进阶：[34. 在排序数组中查找元素的第一个和最后一个位置](34-find-first-and-last-position-of-element-in-sorted-array.md) —— 边界二分
- 知识点：二分查找模板与边界选择见[二分查找](二分查找.md)
