# 39. 组合总和（Combination Sum）

频次 ★★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

无重复元素数组 candidates 和一个目标值 target，找出所有和为 target 的组合（**每个元素可无限重复使用**）。

**示例**：
```
输入: candidates = [2,3,6,7], target = 7
输出: [[2,2,3],[7]]
```

## 思路

**回溯（组合 + 可重复选）**：与 78 子集的组合模板相同，但每个元素可以重复选，递归时参数从 `i+1` 改为 `i`。

剪枝：对 candidates 排序，一旦当前元素 > remain，之后的更大元素更不可能满足，直接 break。

## 代码

```java
public List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> res = new ArrayList<>();
    Arrays.sort(candidates);                  // 排序后方便剪枝
    backtrack(candidates, target, 0, new ArrayList<>(), res);
    return res;
}

private void backtrack(int[] c, int remain, int start, List<Integer> path, List<List<Integer>> res) {
    if (remain == 0) {
        res.add(new ArrayList<>(path));
        return;
    }
    for (int i = start; i < c.length; i++) {
        if (c[i] > remain) break;             // 剪枝（因为已排序）
        path.add(c[i]);
        backtrack(c, remain - c[i], i, path, res);   // 仍传 i 而非 i+1：可重复选
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(n^(target/min(c))) 指数级 —— 实际受剪枝大幅优化
- **空间**：O(target/min(c))

## 边界条件

- target = 0：返回 `[[]]`
- 无 candidate ≤ target：没有解，返回空列表
- candidates 含 1：路径非常多（组合爆炸）

## 变式

- **[40. 组合总和 II](40-combination-sum-ii.md)**：每个元素只能用一次 + 排序去重剪枝
- **[216. 组合总和 III](https://leetcode.cn/problems/combination-sum-iii/)**：固定长度 + 1~9 范围
- **[78. 子集](78-subsets.md)**：每个元素只能用一次 + 不要求和

## 易错点

- **递归参数传 `i` 不是 `i+1`**：这样才能重复选同一元素。这是和 40 题的唯一代码区别，面试常考这点
- 排序剪枝 `break` 依赖于数组已排序，如果没有排序应改为 `continue`（多走几步但功能不变）
- `remain - c[i]` 作为参数而不是 `remain -= c[i]` 后再恢复——更简洁，不需要在循环里做加减

## 面试追问

- **如果不排序怎么剪枝？** 无法用 `break` 提前退出，只能用 `continue` 跳过当前过大的元素
- **可重复选的本质？** 它在元素树上允许"自环"——每个节点都能选自己。这是组合问题的特殊变体，理解这个自环就理解了两道组合总和题的区别

## 关联题

- 同套路：[40. 组合总和 II](40-combination-sum-ii.md) —— 不可重复选 + 去重
- 进阶：[46. 全排列](46-permutations.md) —— 同"回溯搜索"但排列 vs 组合
- 知识点：回溯"可重复组合"模板见[回溯](algorithms/10-回溯/README.md)

