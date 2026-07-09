# 90. 子集 II（Subsets II）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

数组含有重复元素，返回所有不重复子集。

**示例**：
```
输入: nums = [1,2,2]
输出: [[],[1],[1,2],[1,2,2],[2],[2,2]]
```

## 思路

**回溯（组合 + 去重）**：在 78 子集的基础上排序 + 同层去重。和 40 组合总和的去重思路完全一致：`i > start && nums[i] == nums[i-1]` 时跳过。

## 代码

```java
public List<List<Integer>> subsetsWithDup(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    Arrays.sort(nums);
    backtrack(nums, 0, new ArrayList<>(), res);
    return res;
}

private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
    res.add(new ArrayList<>(path));
    for (int i = start; i < nums.length; i++) {
        if (i > start && nums[i] == nums[i - 1]) continue;   // 同层去重
        path.add(nums[i]);
        backtrack(nums, i + 1, path, res);
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(n × 2^n)
- **空间**：O(n)

## 边界条件

- 空数组：返回 `[[]]`
- 全相同元素：子集数量 = n+1（空集、一个元素、两个元素……n 个元素）

## 变式

- **[78. 子集](78-subsets.md)**：无重复版
- **[40. 组合总和 II](40-combination-sum-ii.md)**：同款去重，但加了目标和约束

## 易错点

- 和 78 唯一的区别就是排序 + 剪枝——理解这一点就能举一反三
- 去重条件 `i > start`（组合类）不是 `i > 0`（排列类），详见 47 题的笔记

## 面试追问

- **子集 II 和 组合总和 II 的去重写法一样吗？** 完全一样——都是组合类去重（`i > start`）。区别在于子集 II 在递归入口收集结果（所有节点），组合总和 II 在 base case 收集（满足 target 的叶子）

## 关联题

- 同套路：[78. 子集](78-subsets.md) —— 无重复版
- 进阶：[47. 全排列 II](47-permutations-ii.md) —— 排列版去重对比
- 知识点：回溯去重模板见[回溯](algorithms/10-回溯/README.md)

