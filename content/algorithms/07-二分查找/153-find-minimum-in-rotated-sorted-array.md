# 153. 寻找旋转排序数组中的最小值（Find Minimum in Rotated Sorted Array）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

升序数组旋转后找最小值（值互异）。

**示例**：
```
输入: nums = [3,4,5,1,2]
输出: 1
```

## 思路

**与右端点比较的二分**：

- 取 `mid`，比较 `nums[mid]` 和 `nums[r]`
- `nums[mid] > nums[r]` → 最小值在右半（mid 及 mid 左边都不是最小值，因为右半更小），`l = mid + 1`
- `nums[mid] < nums[r]` → 最小值在左半（含 mid 本身），`r = mid`

循环结束时 `l == r`，即为最小值下标。

为什么与右端点比而不是左端点？因为**右端点是已知的最大值边界**：旋转后右半段一定 ≤ 全局最小值左边的值，选右端点能避免 `nums[mid] < nums[l]` 时无法确定方向的歧义。

## 代码

```java
public int findMin(int[] nums) {
    int l = 0, r = nums.length - 1;
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (nums[mid] > nums[r])
            l = mid + 1;          // 最小值在右半，跳过 mid
        else
            r = mid;              // 最小值在左半（含 mid）
    }
    return nums[l];
}
```

## 复杂度

- **时间**：O(log n)
- **空间**：O(1)

## 边界条件

- 无旋转（[1,2,3,4,5]）：nums[mid] 始终 < nums[r]，r 持续左移，最终 l = 0 ✅
- 长度 1：while 不执行，直接返回该元素 ✅
- 旋转点到尾部（[2,3,4,5,1]）：最后一步 r 锁定在 1 的位置 ✅

## 变式

- **[154. 寻找旋转排序数组中的最小值 II](https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array-ii/)**：含重复值，`nums[mid] == nums[r]` 时无法判断，需要 `r--` 收缩
- **找最大值？** 与左端点比较的对称做法：`nums[mid] >= nums[l]` 时 `l = mid`，否则 `r = mid - 1`

## 易错点

- 与 **33. 搜索旋转排序数组** 的条件区分：33 判断哪半**有序**来找 target；本题只判断最小值在**哪半**，判断逻辑不同
- 循环条件 `l < r`（不是 `l <= r`）：结束时机是区间缩为一个点，`<=` 会导致死循环
- `nums[mid] > nums[r]` 时不取等号——题目说值互异，但就算有重复也该用 `>=` 吗？实际上 `>` 足够，因为值互异

## 面试追问

- **为什么和右端点比不和左端点比？** 用左端点：`nums[mid] > nums[l]` 只能知道左半有序，不知道最小值在哪（可能最小值就在左半的有序段头）。右端点一定指向"最大值的候选"，比较更有区分力。答完举 [3,4,5,1,2] 且 mid=1 时两种比较的差异
- **有重复值怎么处理？** 加 `if (nums[mid] == nums[r]) r--`，最坏退化为 O(n)。面试题 154 直接考察这一点

## 关联题

- 同套路：[33. 搜索旋转排序数组](33-search-in-rotated-sorted-array.md) —— 同为"利用旋转数组局部有序"做二分
- 进阶：[154. 寻找旋转排序数组中的最小值 II](https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array-ii/) —— 含重复值的退化版
- 知识点：二分查找变体模板见[二分查找](algorithms/07-二分查找/README.md)

