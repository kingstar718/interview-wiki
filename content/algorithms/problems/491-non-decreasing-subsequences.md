---
topics:
  - 回溯
techniques:
  - 回溯框架
---

# 491. 非递减子序列（Non-decreasing Subsequences）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

给定一个整数数组 nums，找出所有不同的递增子序列（长度至少为 2）。数组中可能含有重复元素，子序列中相邻元素非严格递增（即 `nums[i] <= nums[j]`）。

**注意**：不能对数组排序，因为求的是子序列（保持原顺序），不是子集。

**示例**：
```
输入: nums = [4,6,7,7]
输出: [[4,6],[4,6,7],[4,6,7,7],[4,7],[4,7,7],[6,7],[6,7,7],[7,7]]

输入: nums = [4,4,3,2,1]
输出: [[4,4]]
```

## 思路

**回溯 + 每层 HashSet 去重**：因为不能排序（子序列需保持原顺序），不能用排序 + `nums[i] == nums[i-1]` 的去重法。改为每层递归用一个 HashSet 记录本层已选过的数字，`nums[i]` 已在本层出现过则跳过。

剪枝：当前数字 < 路径最后一个数字时跳过（保证非递减）。

## 代码

```java
public List<List<Integer>> findSubsequences(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), res);
    return res;
}

private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
    if (path.size() >= 2) {
        res.add(new ArrayList<>(path));
        // 注意：这里不能 return，因为还要继续往后找更长的子序列
    }
    Set<Integer> used = new HashSet<>();       // 本层去重（不能排序，只能用 set）
    for (int i = start; i < nums.length; i++) {
        if (used.contains(nums[i])) continue;  // 本层已用过该值
        if (!path.isEmpty() && nums[i] < path.get(path.size() - 1)) continue; // 非递减
        used.add(nums[i]);
        path.add(nums[i]);
        backtrack(nums, i + 1, path, res);
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(2^n × n) —— 每个元素选或不选，最坏生成所有子序列
- **空间**：O(n) —— 递归栈深度 + path

## 边界条件

- 数组长度 < 2：无法形成长度 ≥ 2 的子序列，返回空列表
- 全部递减数组（如 [5,4,3,2,1]）：无递增子序列，返回空列表
- 全相同元素（如 [7,7,7,7]）：每层 set 去重 + 直接收集，子序列数量 = 2^n - n - 1（所有长度 ≥ 2 的组合）

## 变式

- **[90. 子集 II](90-subsets-ii.md)**：排序后去重，求子集而非子序列
- **[300. 最长递增子序列](300-longest-increasing-subsequence.md)**：动态规划求最长长度，而非枚举所有

## 易错点

- **不能排序**：题目求的是子序列（保持原顺序），排序后变成子集问题，会生成错误结果（如 [4,3,2,1] 排序后变成 [1,2,3,4]，会错误地生成 [1,2] 等）
- **收集后不能 return**：path.size() >= 2 时收集结果，但不能 return，因为后续还能接更多数字形成更长序列
- **去重用 HashSet 而非排序法**：`i > start && nums[i] == nums[i-1]` 依赖排序，本题不能排序，必须在每层用 set 去重

## 面试追问

- **为什么不能用排序去重法？** 排序去重法（`nums[i] == nums[i-1]`）的前提是数组已排序，但本题求的是子序列，排序会破坏原数组顺序，导致生成的结果不是原数组的子序列
- **每层 set 去重的原理？** 在同一层递归中，相同值的元素只需选第一个（最靠左的），后面的相同值会生成重复子序列，set 记录本层已选值来跳过

## 关联题

- 同套路：[90. 子集 II](90-subsets-ii.md) —— 排序去重法（对比学习）
- 进阶：[47. 全排列 II](47-permutations-ii.md) —— 排列去重（`used[]` 数组法）
- 知识点：回溯去重三种方法见[回溯](回溯.md)
