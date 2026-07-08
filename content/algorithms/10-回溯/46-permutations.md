# 46. 全排列（Permutations）

频次 ★★★★★ · 难度 🟡 · 高频：全厂

## 题目

给定不含重复数字的数组，返回所有可能的全排列。

**示例**：
```
输入: nums = [1,2,3]
输出: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```

## 思路

**回溯 + visited 数组**：每层在剩余未选数字中选一个加入路径，递归到长度 == n 时收集结果。

选数时用一个 boolean 数组标记哪些已经被选过——这是排列和组合的最大区别：排列关心顺序，每次都要从所有剩余数字中选。

## 代码

```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    backtrack(nums, new ArrayList<>(), used, res);
    return res;
}

private void backtrack(int[] nums, List<Integer> path, boolean[] used, List<List<Integer>> res) {
    if (path.size() == nums.length) {
        res.add(new ArrayList<>(path));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        used[i] = true;
        path.add(nums[i]);
        backtrack(nums, path, used, res);
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```

## 复杂度

- **时间**：O(n × n!) —— n! 个排列，每个拷贝到结果 O(n)
- **空间**：O(n) —— 递归栈 + path

## 边界条件

- n = 0：返回 `[[]]`（一个空排列）
- n = 1：返回 `[[1]]`

## 变式

- **[47. 全排列 II](47-permutations-ii.md)**：含重复数字，需要排序 + 剪枝 `i > 0 && nums[i] == nums[i-1] && !used[i-1]`
- **[78. 子集](78-subsets.md)**：选或不选，不是全选
- **[39. 组合总和](39-combination-sum.md)**：组合 + 可重复选

## 易错点

- `used[i]` 回溯后必须恢复——这是回溯的核心契约
- 结果收集用 `new ArrayList<>(path)`（深拷贝），否则后续回溯会修改已加入结果的 path
- 排列和组合的模板是回溯体系的两大基础，本题是"排列版"——每层从 0 开始遍历，不是从 start 开始

## 面试追问

- **如果数组有重复？** 先排序，剪枝：`i > 0 && nums[i] == nums[i-1] && !used[i-1]`，见 47 题
- **什么是"排列"和"组合"的代码区别？** 排列每层从 0 开始（全量可选），组合从 start 开始（避免重复选择之前的位置）

## 关联题

- 同套路：[47. 全排列 II](47-permutations-ii.md) —— 含重复的排列
- 进阶：[78. 子集](78-subsets.md) —— 另一类回溯模板；[39. 组合总和](39-combination-sum.md) —— 可重复选
- 知识点：回溯三要素（路径/选择列表/结束条件）见[回溯](algorithms/10-回溯/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
