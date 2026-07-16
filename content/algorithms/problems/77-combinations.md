---
topics:
  - 回溯
techniques:
  - 回溯框架
---

# 77. 组合（Combinations）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。

**示例**：
```
输入: n = 4, k = 2
输出: [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
```

## 思路

**回溯（组合模板）**：经典回溯，`start` 参数控制只从当前下标往后选，避免重复组合（如 [1,2] 和 [2,1]）。

剪枝优化：剩余可选数字不够时提前终止 —— 当 `n - i + 1 < k - path.size()` 时，即使把后面所有数都选上也不够 k 个，直接 break。

## 代码

```java
public List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(n, k, 1, new ArrayList<>(), res);
    return res;
}

private void backtrack(int n, int k, int start, List<Integer> path, List<List<Integer>> res) {
    if (path.size() == k) {
        res.add(new ArrayList<>(path));
        return;
    }
    // 剪枝：剩余数字不够凑满 k 个
    for (int i = start; i <= n - (k - path.size()) + 1; i++) {
        path.add(i);
        backtrack(n, k, i + 1, path, res);
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(C(n,k) × k) —— 组合数 × 每次复制 path 的开销
- **空间**：O(k) —— 递归栈深度

## 边界条件

- k = 0：返回 `[[]]`
- k = n：返回 `[[1,2,...,n]]`，只有一种组合
- k > n：返回空列表

## 变式

- **[39. 组合总和](39-combination-sum.md)**：可重复选 + 目标和约束
- **[216. 组合总和 III](216-combination-sum-iii.md)**：固定长度 k + 1~9 范围 + 目标和 n
- **[40. 组合总和 II](40-combination-sum-ii.md)**：不可重复选 + 去重 + 目标和

## 易错点

- 循环终止条件 `i <= n - (k - path.size()) + 1` 的推导：还需 `k - path.size()` 个数字，从 i 开始最多能取 `n - i + 1` 个，所以 `n - i + 1 >= k - path.size()` → `i <= n - (k - path.size()) + 1`
- 递归传 `i + 1` 而非 `i`：每个数字只能用一次，这是组合与"可重复组合"的唯一区别

## 面试追问

- **剪枝条件怎么推导的？** 剩余还需要 `k - path.size()` 个数，从 i 到 n 共有 `n - i + 1` 个数可选，必须满足 `n - i + 1 >= k - path.size()`，否则直接终止循环
- **组合和排列的代码区别？** 组合用 `start` 参数 + `i+1` 向下传，排列用 `used[]` 数组或交换法。组合避免 [1,2] 和 [2,1] 重复，排列需要所有顺序

## 关联题

- 同套路：[39. 组合总和](39-combination-sum.md) —— 可重复选版
- 进阶：[216. 组合总和 III](216-combination-sum-iii.md) —— 加目标和约束
- 知识点：回溯组合模板见[回溯](回溯.md)
