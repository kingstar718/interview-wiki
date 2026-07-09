# 78. 子集（Subsets）

频次 ★★★★★ · 难度 🟡 · 高频：全厂

## 题目

数组元素互异，返回所有子集（含空集）。

**示例**：
```
输入: nums = [1,2,3]
输出: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
```

## 思路

**回溯（选或不选）**：每层决定是否取当前元素，递归到数组末尾时收集。关键在于每层都有一个起始 start，避免回头（组合数，不是排列）。

## 代码

```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), res);
    return res;
}

private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
    res.add(new ArrayList<>(path));           // 每个节点都是合法的子集
    for (int i = start; i < nums.length; i++) {
        path.add(nums[i]);
        backtrack(nums, i + 1, path, res);    // 选了之后只能从后面选（避免重复组合）
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(n × 2^n) —— 2^n 个子集，每个拷贝 O(n)
- **空间**：O(n) —— 递归栈 + path

## 边界条件

- 空数组：返回 `[[]]`
- 单元素：返回 `[[],[1]]`

## 变式

- **[90. 子集 II](90-subsets-ii.md)**：含重复元素，排序 + 剪枝 `i > start && nums[i] == nums[i-1]`
- **[46. 全排列](46-permutations.md)**：每次从 0 开始遍历（排列），本题从 start 开始（组合）
- **迭代增量法**：初始 `[[]]`，每遍历一个元素，给当前所有子集加上该元素——比回溯更省代码但不是面试预期解法

## 易错点

- `res.add(new ArrayList<>(path))` 放在递归开始而不是 base case——**每个节点的路径都是合法子集**，不是只有叶子节点才是
- 组合类回溯用 `start` 防止回头，保证 `[1,2]` 和 `[2,1]` 不会被同时收集
- 不用 visited 数组，因为 start 已经限制了选择范围

## 面试追问

- **子集问题的本质？** 每个元素选或不选，2^n 种可能。可用位运算表示：二进制 0~2^n-1 的每一位对应一个元素的取舍
- **什么时候用 start 什么时候用 visited？** 组合/子集用 start 防止回头，排列用 visited 保证全部可选——判断标准是"顺序重不重要"

## 关联题

- 同套路：[90. 子集 II](90-subsets-ii.md) —— 含重复的子集，多一步去重剪枝
- 进阶：[46. 全排列](46-permutations.md) —— 排列 vs 组合的对比
- 知识点：回溯"组合"模板见[回溯](algorithms/10-回溯/README.md)

