---
topics:
  - 二分查找
techniques:
  - 二分边界
---

# 35. 搜索插入位置（Search Insert Position）

频次 ★★★ · 难度 🟢 · 高频：字节/美团

## 题目

给定升序**无重复**整型数组 `nums` 和目标值 `target`，若 target 存在返回其下标；不存在则返回它**按顺序插入的位置**。要求 O(log n)。

**示例**：
```
输入: nums = [1,3,5,6], target = 5   输出: 2
输入: nums = [1,3,5,6], target = 2   输出: 1
输入: nums = [1,3,5,6], target = 7   输出: 4
```

## 思路

跟 `704. 二分查找` 完全同构，唯一区别是**没找到时返回什么**：704 返回 -1，本题返回插入位置。

关键结论：**左闭右闭二分循环结束时，`l` 就是插入位置**。因为循环退出时必然 `l == r + 1`，此时 `[0, r]` 全都 `< target`、`[l, n-1]` 全都 `> target`，`l` 正好是第一个大于 target 的下标 —— 也就是 target 该插进去的地方。

这条结论是二分类题的通用心法：**二分退出时 `l` 指向"第一个满足条件的位置"**，很多"查找左边界""插入位置""大于等于 target 的最小值"其实是同一道题。

## 代码

```java
public int searchInsert(int[] nums, int target) {
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
    return l;                 // 没找到:l 即插入位置(此时 l == r + 1)
}
```

也可以写成统一的**找左边界**形式，连 `== target` 的分支都不用特判：

```java
public int searchInsert(int[] nums, int target) {
    int l = 0, r = nums.length;          // 左闭右开,注意 r = n
    while (l < r) {
        int mid = l + (r - l) / 2;
        if (nums[mid] < target) l = mid + 1;
        else r = mid;                    // nums[mid] >= target,答案可能就是 mid
    }
    return l;                            // 第一个 >= target 的位置
}
```

第二种写法能直接推广到有重复元素的场景（返回第一个 `>= target` 的下标），面试里更好用。

## 复杂度

- **时间**：O(log n)
- **空间**：O(1)

## 边界条件

- 空数组：`l = 0, r = -1`，循环不进入，返回 0（插到最前面）
- target 比所有元素都小：`r` 一路减到 -1，`l` 停在 0
- target 比所有元素都大：`l` 一路加到 n，返回 n（插到末尾，**这是唯一会返回 n 的情况，也是最容易越界的分支**）
- target 恰好存在：命中 `return mid`
- 数组有重复值（题目保证无重复，但变式常问）：第一种写法返回任意匹配下标，第二种写法返回最左匹配

## 变式

- **[34. 在排序数组中查找元素的第一个和最后一个位置](34-find-first-and-last-position-of-element-in-sorted-array.md)**：把"第一个 >= target"和"第一个 > target"各做一次二分
- **[704. 二分查找](704-binary-search.md)**：同一模板，找不到时返回 -1
- **[74. 搜索二维矩阵](74-search-a-2d-matrix.md)**：把二维拍平成一维后就是本题
- **求"最后一个 <= target"**：等价于本题结果减一，注意结果为 0 时表示不存在

## 易错点

- **返回 `l` 而不是 `r`**：循环结束时 `l == r + 1`，`r` 指向最后一个小于 target 的位置，差一位就是经典的 off-by-one
- 两种写法的 `r` 初值不同：左闭右闭 `r = n - 1`、循环 `l <= r`、收缩 `r = mid - 1`；左闭右开 `r = n`、循环 `l < r`、收缩 `r = mid`。**混用必错**
- `mid = l + (r - l) / 2` 而非 `(l + r) / 2`，避免 int 溢出
- 返回值范围是 `[0, n]` 而不是 `[0, n-1]` —— 拿它当下标去访问数组前要先判越界

## 面试追问

- **循环结束时为什么 `l` 就是答案？** 二分维持的不变量是"`[0, l)` 全部 `< target`、`(r, n)` 全部 `>= target`"，退出时 `l == r+1`，两段刚好拼满整个数组，`l` 就是分界点。用不变量论证比背模板可靠。
- **有重复元素怎么办？** 第一种写法会随机命中某个相等元素，要改用左闭右开的"找左边界"版本，去掉 `== target` 的提前返回。
- **和 Java 标准库的关系？** `Arrays.binarySearch` 找不到时返回 `-(插入点) - 1`，取反减一即可得到本题答案；`TreeMap.ceilingKey` 也是同一语义。
- **能不能用来二分答案？** 能 —— 把 `nums[mid] < target` 换成任意单调判定条件，返回的就是"条件由假变真的第一个位置"，见[二分查找](二分查找.md)。

## 关联题

- 同套路：[704. 二分查找](704-binary-search.md) —— 同模板，只差返回值
- 进阶：[34. 在排序数组中查找元素的第一个和最后一个位置](34-find-first-and-last-position-of-element-in-sorted-array.md) —— 左右边界各二分一次
- 知识点：二分不变量与两种区间写法见[二分查找](二分查找.md)
