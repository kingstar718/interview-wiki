---
topics:
  - 哈希表
  - 数组与字符串
techniques:
  - 哈希查表
---

# 1. 两数之和（Two Sum）

频次 ★★★★★ · 难度 🟢 · 高频：全厂

## 题目

给定一个整数数组 `nums` 和一个目标值 `target`，找出两个数之和等于 `target` 的索引。假设每组只有唯一解，不能重复使用同一个元素。

**示例**：
```
输入: nums = [2, 7, 11, 15], target = 9
输出: [0, 1]
解释: nums[0] + nums[1] == 9
```

## 思路

用 HashMap 记录 `{值 -> 索引}`，遍历时检查 `target - nums[i]` 是否已存在。一次遍历完成。

## 代码

```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }
        map.put(nums[i], i);
    }
    throw new IllegalArgumentException("no solution");
}
```

## 复杂度

- **时间**：O(n) — 每个元素访问一次，HashMap 查找和插入均为 O(1)
- **空间**：O(n) — HashMap 最多存储 n 个元素

## 边界条件

- 数组长度 < 2：题目保证有唯一解，工程实现中若要防御非法输入，应提前判空/判长度。
- 存在重复值，如 `nums = [3,3], target = 6`：先查 `complement` 再 `put` 当前值，天然支持"用两个不同下标的相同值配对"。
- 负数、0：HashMap 按值查找，不受正负影响，逻辑不需要特殊处理。

## 变式

- **有序数组**（[167. 两数之和II-输入有序](167-two-sum-ii.md) 类型）：可以用左右双指针，O(1) 额外空间，不需要 HashMap。
- **三数之和**（15 题）：固定一个数，转化为对剩余数组做"两数之和"，通常排序后用双指针（因为要去重，HashMap 不方便）。
- 要求返回**所有**满足条件的下标对而不是任意一组：不能找到第一组就提前 return，需要遍历完并收集所有结果。

## 易错点

- 不能把同一个元素用两次：先判断 `map.containsKey(complement)` 再 `put(nums[i], i)`，顺序反了会出现"自己和自己匹配"的错误（如 `nums=[3], target=6`）。
- HashMap 存的是**值到下标**的映射，如果值重复，后出现的下标会覆盖先出现的——这正是我们想要的（保证匹配到的是"之前出现过的"下标）。

## 面试追问

- **为什么用 HashMap 而不是先排序再双指针？** 排序会打乱原始下标，需要额外数组记录“值→原下标”才能排序后找回，且排序本身是 O(n log n)，比 HashMap 一次遍历的 O(n) 慢；只有当题目不要求返回下标（只要值）时，排序双指针才更省空间。
- **如果数组元素有几十亿个，内存放不下 HashMap 怎么办？** 如果数据本身有序或允许排序，改用双指针可以把空间降到 O(1)；如果必须无序且找不到排序的场景，可以考虑分块处理 + 外部存储，但这已经超出该题本身范畴。

## 关联题

- 同套路：[560. 和为 K 的子数组](560-subarray-sum-equals-k.md) —— "边遍历边查哈希补数"的区间和版本
- 进阶：[167. 两数之和II-输入有序](167-two-sum-ii.md)（有序 → 首尾双指针）→ [15. 三数之和](15-3sum.md)（排序 + 固定一位 + 双指针）
- 知识点：HashMap O(1) 查找的原理见[集合框架](集合框架.md)

