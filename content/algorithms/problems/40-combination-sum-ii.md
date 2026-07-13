---
topics:
  - 回溯
techniques:
  - 回溯框架
  - 剪枝
---

# 40. 组合总和 II（Combination Sum II）

频次 ★★★ · 难度 🟡 · 高频：阿里

## 题目

candidates 有**重复元素**，每个元素**只能用一次**，找出所有和为 target 的不重复组合。

**示例**：
```
输入: candidates = [10,1,2,7,6,1,5], target = 8
输出: [[1,1,6],[1,2,5],[1,7],[2,6]]
```

## 思路

**回溯（组合 + 去重 + 不可重复选）**：在 39 组合总和基础上做两处改动：

1. 递归参数 `i` 改为 `i + 1`（不可重复选）
2. 排序 + 剪枝 `i > start && candidates[i] == candidates[i-1]`（同层去重）

## 代码

```java
public List<List<Integer>> combinationSum2(int[] candidates, int target) {
    List<List<Integer>> res = new ArrayList<>();
    Arrays.sort(candidates);
    backtrack(candidates, target, 0, new ArrayList<>(), res);
    return res;
}

private void backtrack(int[] c, int remain, int start, List<Integer> path, List<List<Integer>> res) {
    if (remain == 0) {
        res.add(new ArrayList<>(path));
        return;
    }
    for (int i = start; i < c.length; i++) {
        if (c[i] > remain) break;                              // 排序后剪枝
        if (i > start && c[i] == c[i - 1]) continue;           // 同层去重
        path.add(c[i]);
        backtrack(c, remain - c[i], i + 1, path, res);         // i+1：不可重复选
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(2^n) 最坏（所有组合）
- **空间**：O(n)

## 边界条件

- target = 0：返回 `[[]]`
- 无组合：返回空列表
- 全相同元素：同层去重保证只有一个组合

## 变式

- **[39. 组合总和](39-combination-sum.md)**：可重复选 + 无重复元素
- **[216. 组合总和 III](https://leetcode.cn/problems/combination-sum-iii/)**：固定长度 + 1~9
- **[90. 子集 II](90-subsets-ii.md)**：同"排序 + 同层去重"的思路

## 易错点

- **`i > start` 不是 `i > 0`**：排列的去重条件是 `!used[i-1]`，组合的去重条件是 `i > start`——因为组合用 start 控制选择范围，同层重复的定义不同
- 先排序是去重的前提
- 39 和 40 的差异只有两点：`i` vs `i+1`、去重剪枝。面试常让先写 39 再改 40

## 面试追问

- **39 和 40 的两处改动分别对应什么语义变化？** `i → i+1` 表示"不可重复取"，去重剪枝表示"值相同视为同一选择"——两个维度独立的约束可以组合出四种变体

## 关联题

- 同套路：[39. 组合总和](39-combination-sum.md) —— 可重复版对照
- 进阶：[47. 全排列 II](47-permutations-ii.md) —— 排列版去重对比
- 知识点：组合类回溯的"同层去重"模板见[回溯](回溯.md)

