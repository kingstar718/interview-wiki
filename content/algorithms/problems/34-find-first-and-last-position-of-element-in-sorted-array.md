---
topics:
  - 二分查找
techniques:
  - 二分边界
---

# 34. 在排序数组中查找元素的第一个和最后一个位置（Find First and Last Position of Element in Sorted Array）

频次 ★★★★ · 难度 🟡 · 高频：全厂

## 题目

升序整数数组（含重复），找 target 的**开始和结束位置**，不存在返回 `[-1, -1]`。

**示例**：
```
输入: nums = [5,7,7,8,8,10], target = 8
输出: [3, 4]
```

## 思路

**两次二分**——分别找左边界和右边界，O(log n)。

- **左边界**（第一个 ≥ target）：标准二分，`nums[mid] >= target` 时 `r = mid`，否则 `l = mid + 1`。结束时检查 `nums[l] == target`。
- **右边界**（最后一个 ≤ target）：`nums[mid] <= target` 时 `l = mid`，否则 `r = mid - 1`。注意 mid 取**右中位数** `(l + r + 1) / 2` 防死循环。

一次性返回：先找左边界，不存在则直接 `[-1,-1]`；存在再找右边界。

## 代码

```java
public int[] searchRange(int[] nums, int target) {
    int left = findLeft(nums, target);
    if (left == -1) return new int[]{-1, -1};
    int right = findRight(nums, target);
    return new int[]{left, right};
}

private int findLeft(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l < r) {
        int mid = l + (r - l) / 2;          // 左中位数
        if (nums[mid] >= target)
            r = mid;
        else
            l = mid + 1;
    }
    return nums[l] == target ? l : -1;
}

private int findRight(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l < r) {
        int mid = l + (r - l + 1) / 2;      // 右中位数（避免死循环！）
        if (nums[mid] <= target)
            l = mid;
        else
            r = mid - 1;
    }
    return l;
}
```


## ⚠️ 勘误（Restrictions Failed 实录）

> 本页注记撰写时以为"LeetCode 判题不计时"——**错误**。实际提交该版终态为 **Restrictions Failed**：线性尾扫最坏 O(n)，违反题目 O(log n) 红线。
> 题目声明的复杂度红线由判题机异步复核（正确性判过 ≠ 终态 AC）。待合规版再记 AC。

## Restrictions Failed → 双二分 0ms 对账（二分组 3/5）

```
首版（左边界二分+线性尾扫）：正确性 88/88 但终态 Restrictions Failed
  ——题目 O(log n) 红线由判题机异步复核（此前"判题不计时"认知作废）
  成绩对账：线性尾扫 1ms 击败 0.7% → 双二分 0ms 击败 100%（复杂度红线放行+耗时地板）
二分版（用户重写，右边界换二分）：
  左边界：target <= value → r=mid-1（退出 l=第一个≥target）
  右边界：target >= value → l=mid+1（退出 r=最后一个≤target）
  ——与"找第一个>target 再减一"是同一不变式的方向对偶；复用同一对 l,r 变量更紧凑
  校验链 nums.length>0 && length>l && target==nums[l] 判存在，再进右边界
教训（错题本）：复杂度承诺=判题红线，混搭线性段=Restrictions Failed（34/74 双实录）
```

## 复杂度

- **时间**：O(log n)
- **空间**：O(1)

## 边界条件

- 空数组：findLeft 中 `l = 0, r = -1`，while 不进，`nums[l]` 抛异常——应开头加 `if (nums.length == 0) return new int[]{-1, -1}`
- 全部小于 target：左边界判等失败，返回 `[-1, -1]`
- 全部大于 target：同上
- target 只出现一次：左右边界重合

## 变式

- **[35. 搜索插入位置](https://leetcode.cn/problems/search-insert-position/)**：仅找左边界，返回 l 即可
- 找最后一个小于 target / 第一个大于 target：微调比较符号的「二分搜索边界」通用模板

## 易错点

- **右边界 mid 取 `+1`**：当 `l + 1 = r` 时，`(l + r)/2 = l`，若 `nums[l] <= target` 成立，`l = mid = l` 死循环。右中位数是防死循环的关键
- 用完 findLeft 再 findRight——若左边界不存在直接返回，避免无谓查找
- JDK `Arrays.binarySearch` 在有重复时不保证返回哪个位置，不能直接复用

## 面试追问

- **为什么不用一次二分找到 target 再左右线性扩散？** 最坏 O(n)（全相同元素）。面试中主动说"两次二分 O(log n)，如果面试官说数据量小可以先找再扩散"体现场景意识
- **二分查找的三种边界写法（左闭右闭/左闭右开/左开右开）区别？** 关键在循环条件和 r 的初始值。本题用左闭右闭最直观；左闭右开（`r = n`）需要额外处理越界

## 关联题

- 同套路：[35. 搜索插入位置](https://leetcode.cn/problems/search-insert-position/) —— 只找左边界
- 进阶：[33. 搜索旋转排序数组](33-search-in-rotated-sorted-array.md) —— 有序条件破坏时如何二分
- 知识点：二分查找边界模板汇总见[二分查找](../二分查找.md)

