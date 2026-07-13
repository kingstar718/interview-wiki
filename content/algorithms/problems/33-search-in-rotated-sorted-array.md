---
topics:
  - 二分查找
techniques:
  - 二分边界
---

# 33. 搜索旋转排序数组（Search in Rotated Sorted Array）

频次 ★★★★★ · 难度 🟡 · 高频：字节/阿里/美团

## 题目

升序数组在某个未知点旋转，给定 target，找到返回下标，否则返回 -1。

**示例**：
```
输入: nums = [4,5,6,7,0,1,2], target = 0
输出: 4
```

要求 **O(log n)**——提示只能用二分。

## 思路

**一次二分 + 有序半区判定**：旋转数组在任何 mid 处，**至少有一半是有序的**。判断 target 是否在有序半区里，在则缩小到该半区，否则去另一半。

1. `nums[l] <= nums[mid]` → 左半有序：若 `target` 在 `[nums[l], nums[mid])` 之间则 `r = mid - 1`，否则 `l = mid + 1`
2. 否则 → 右半有序：若 `target` 在 `(nums[mid], nums[r]]` 之间则 `l = mid + 1`，否则 `r = mid - 1`

关键条件：`<=` 的比较必须带等号（处理 `l == mid` 的场景）。

## 代码

```java
public int search(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l <= r) {
        int mid = l + (r - l) / 2;
        if (nums[mid] == target) return mid;

        if (nums[l] <= nums[mid]) {                 // 左半有序
            if (nums[l] <= target && target < nums[mid])
                r = mid - 1;                        // target 在左半
            else
                l = mid + 1;                        // target 在右半
        } else {                                    // 右半有序
            if (nums[mid] < target && target <= nums[r])
                l = mid + 1;                        // target 在右半
            else
                r = mid - 1;                        // target 在左半
        }
    }
    return -1;
}
```

## 复杂度

- **时间**：O(log n)
- **空间**：O(1)

## 边界条件

- 无旋转（全局有序）：条件 `nums[l] <= nums[mid]` 始终成立，退化为普通二分
- 长度 1：`l == r == mid`，比较相等即返回
- target 是旋转点本身：nums[mid] == target 在入口拦截

## 变式

- **[81. 搜索旋转排序数组 II](https://leetcode.cn/problems/search-in-rotated-sorted-array-ii/)**：含重复值，`nums[l] == nums[mid] == nums[r]` 时无法判断有序半区，需 `l++` 去重，最坏退化为 O(n)
- **[153. 寻找旋转排序数组中的最小值](153-find-minimum-in-rotated-sorted-array.md)**：只找最小值不做匹配，判断条件不同

## 易错点

- `nums[l] <= nums[mid]` 的**等号不能丢**：当 `l == mid`（区间长 2）时，左半只有一个元素也被视为有序，丢掉等号二分会漏掉
- 判 target 在有序半区时，**区间端点注意边界**：`<=` 和 `<` 的方向
- 旋转数组二分的核心不是找旋转点，而是**利用部分有序信息**缩小范围

## 面试追问

- **有重复值会怎样？** `nums[l] == nums[mid] == nums[r]` 无法区分哪半有序，只能 `l++` 逐步收缩，最坏 O(n)。答出退化条件再加一句"面试中先确认数组是否含重复值"，体现沟通习惯
- **和普通二分的关系？** 普通二分靠**值的大小**决定方向，旋转数组二分靠**有序半区的位置**决定方向；共同前提是**区间内索引单调性**（数组随机访问）

## 关联题

- 同套路：[153. 寻找旋转排序数组中的最小值](153-find-minimum-in-rotated-sorted-array.md) —— 同样"至少一半有序"的二分思路
- 进阶：[81. 搜索旋转排序数组 II](https://leetcode.cn/problems/search-in-rotated-sorted-array-ii/) —— 含重复值的退化版
- 知识点：二分查找模板见[二分查找](二分查找.md)

