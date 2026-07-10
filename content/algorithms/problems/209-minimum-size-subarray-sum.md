---
topics:
  - 数组与字符串
techniques:
  - 滑动窗口
---

# 209. 长度最小的子数组（Minimum Size Subarray Sum）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

正整数数组 `nums` 和目标值 `target`，求和 ≥ `target` 的**长度最小**的连续子数组的长度。不存在则返回 0。

**示例**：
```
输入: target = 7, nums = [2,3,1,2,4,3]
输出: 2  （子数组 [4,3] 长度 2）
```

## 思路

**滑动窗口**：窗口 `[l, r]` 内元素和 ≥ target 时收缩左边界，记录最小长度。因为数组元素都是正数，右指针右移窗口和单调增、左指针右移单调减，窗口具备单调性，滑动窗口成立。

## 代码

```java
public int minSubArrayLen(int target, int[] nums) {
    int l = 0, sum = 0, min = Integer.MAX_VALUE;
    for (int r = 0; r < nums.length; r++) {
        sum += nums[r];
        while (sum >= target) {
            min = Math.min(min, r - l + 1);
            sum -= nums[l];
            l++;
        }
    }
    return min == Integer.MAX_VALUE ? 0 : min;
}
```

## 复杂度

- **时间**：O(n) — l、r 各移动至多 n 次
- **空间**：O(1)

## 边界条件

- 空数组：返回 0
- 全局和 < target：`min` 保持 `Integer.MAX_VALUE`，返回 0
- 单元素 ≥ target：窗口长度 1
- 全数组和正好等于 target：最终窗口覆盖整个数组

## 变式

- **[3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md)** —— 从求最短变成求最长，收缩条件从"窗口和 ≥ target"变为"窗口内出现重复"
- **[76. 最小覆盖子串](76-minimum-window-substring.md)** —— 从单目标和变为多字符覆盖，窗口状态从整数和变成字符频次数组
- **[862. 和至少为 K 的最短子数组](https://leetcode.cn/problems/shortest-subarray-with-sum-at-least-k/)** —— 允许负数，滑动窗口失效，需要用单调双端队列

## 易错点

- **收缩窗口时先更新 min 再移除左端元素**：顺序是 `sum >= target` → 更新 min → 移除左端 → l++。如果把移除放在更新之前，min 会漏掉当前窗口。
- `min` 初始化为 `Integer.MAX_VALUE`，不要初始化为 `0` 或 `n + 1`，否则后面的 `Math.min` 逻辑出错。
- 窗口内元素全是正数是用滑动窗口的前提——如果有负数，窗口扩展时和不一定增大，滑动窗口不成立。

## 面试追问

- **为什么滑动窗口能保证不漏解？** 由于数组元素都是正整数，r 右移窗口和只增不减，l 右移只减不增（单调性）。固定 r 时，最左的满足条件的 l 就是该 r 下的最优解；r 遍历所有位置，全局最优解必然被覆盖。
- **如果数组包含负数怎么办？** 滑动窗口的单调性前提不成立，需要用前缀和 + 单调双端队列（`862. 和至少为 K 的最短子数组`），复杂度 O(n) 但原因不同。
- **O(n log n) 的解法？** 前缀和 + 二分查找：对每个 r，在 `prefix[0..r]` 中二分查找 `≤ prefix[r+1] - target` 的位置。当滑动窗口不适用时（如负数数组），这是替代方案。

## 关联题

- 同套路：[3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md) —— 滑动窗口标准模板
- 进阶：[76. 最小覆盖子串](76-minimum-window-substring.md) —— 多约束的滑动窗口
- 知识点：滑动窗口的单调性前提与模板见[双指针与滑动窗口](双指针与滑动窗口.md)
