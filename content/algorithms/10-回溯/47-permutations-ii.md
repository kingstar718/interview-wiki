# 47. 全排列 II（Permutations II）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

含重复数字的数组，返回所有不重复的全排列。

**示例**：
```
输入: nums = [1,1,2]
输出: [[1,1,2],[1,2,1],[2,1,1]]
```

## 思路

**回溯 + 排序去重**：在 46 全排列基础上，先对数组排序，然后添加剪枝条件：

`i > 0 && nums[i] == nums[i - 1] && !used[i - 1]` → 前一个相同元素在当前层还没用过（在同层递归中已被回溯掉），跳过。

这个剪枝确保"相同数字在同一层只选第一个"。

## 代码

```java
public List<List<Integer>> permuteUnique(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    Arrays.sort(nums);                          // 排序让相同元素相邻
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
        // 剪枝：相同值，且前一个相同值在同层已被回溯（used[i-1] == false）
        if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
        used[i] = true;
        path.add(nums[i]);
        backtrack(nums, path, used, res);
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```

## 复杂度

- **时间**：O(n × n!) 最坏（不重复时退化到 46），实际因剪枝减少
- **空间**：O(n)

## 边界条件

- 全相同元素 `[1,1,1]`：只有一个排列
- 无重复：退化为 46 题

## 变式

- **[46. 全排列](46-permutations.md)**：无重复，不需要剪枝
- **[90. 子集 II](90-subsets-ii.md)**：组合版去重（剪枝条件 `i > start` 而非 `i > 0`）

## 易错点

- 剪枝条件 `!used[i-1]` 是**关键**：说明前一个相同元素在当前层已经被回溯掉了（不在当前路径中），所以当前元素不能选——避免同层重复。如果 `used[i-1]` 为 true 说明它在更深层路径中，同层选它是合理的（比如 `[1,1,2]` 中两个 1 在不同位置）
- 排序是必须的前置操作——剪枝依赖重复元素相邻
- 剪枝条件放在排列和组合有区别：排列用 `!used[i-1]`，组合用 `i > start`

## 面试追问

- **`!used[i-1]` 和 `used[i-1]` 的区别？** `!used[i-1]` 是同层去重（保留第一个，跳过同层后续重复）；`used[i-1]` 是不同层去重（允许同层重复，禁止不同层用相同值）。面试官如果追问就答"这是树层去重 vs 树枝去重的区别"

## 关联题

- 同套路：[46. 全排列](46-permutations.md) —— 无重复版
- 进阶：[40. 组合总和 II](40-combination-sum-ii.md) —— 组合版去重，剪枝条件变为 `i > start`
- 知识点：回溯去重的"树层去重 vs 树枝去重"见[回溯](algorithms/10-回溯/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
