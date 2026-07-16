---
topics:
  - 回溯
techniques:
  - 回溯框架
---

# 216. 组合总和 III（Combination Sum III）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

找出所有相加之和为 n 的 k 个数的组合，只使用数字 1~9，每个数字最多使用一次。

**示例**：
```
输入: k = 3, n = 7
输出: [[1,2,4]]

输入: k = 3, n = 9
输出: [[1,2,6],[1,3,5],[2,3,4]]
```

## 思路

**回溯 + 双重剪枝**：在 77 组合模板的基础上增加目标和约束。剪枝有两处：
1. **剩余数字不够**：`9 - i + 1 < k - path.size()` 时 break（同 77 题）
2. **当前数字 > 剩余和**：`i > remain` 时 break（数字递增，后面的更大）

## 代码

```java
public List<List<Integer>> combinationSum3(int k, int n) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(k, n, 1, new ArrayList<>(), res);
    return res;
}

private void backtrack(int k, int remain, int start, List<Integer> path, List<List<Integer>> res) {
    if (path.size() == k) {
        if (remain == 0) res.add(new ArrayList<>(path));
        return;
    }
    // 剪枝：剩余数字不够凑满 k 个，或当前数字已大于剩余和
    for (int i = start; i <= 9 - (k - path.size()) + 1; i++) {
        if (i > remain) break;                // 正数递增，后面更大
        path.add(i);
        backtrack(k, remain - i, i + 1, path, res);
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(C(9,k) × k) —— 从 9 个数字中选 k 个的组合数
- **空间**：O(k) —— 递归栈深度

## 边界条件

- k > 9：无法满足，返回空列表
- n 过小（如 n < 1+2+...+k）：无解
- n 过大（如 n > 9+8+...+(9-k+1)）：无解
- 双剪枝条件缺一不可：数字不够 + 剩余和超限

## 变式

- **[39. 组合总和](39-combination-sum.md)**：无长度限制 + 可重复选
- **[40. 组合总和 II](40-combination-sum-ii.md)**：无长度限制 + 不可重复选 + 去重
- **[77. 组合](77-combinations.md)**：只限长度，无目标和

## 易错点

- 终止条件：path 长度 = k 时，remain 必须为 0 才收集结果，不是长度够了就收集
- 剪枝条件 `i > remain` 依赖 for 循环中 i 递增，正确性基于数字全为正且递增
- 递归传 `i + 1`：每个数字只能用一次，和 39 题（传 `i`）不同

## 面试追问

- **和 39 题的区别？** 39 题可重复选（递归传 i）、无长度限制；216 题不可重复（递归传 i+1）、有长度限制 k、数字范围固定为 1~9
- **为什么只从 1~9 选？** 题目限定，这恰好是数独的数字范围，也是"组合总和"系列中范围最小的，剪枝效果最明显

## 关联题

- 同套路：[39. 组合总和](39-combination-sum.md) —— 可重复选版
- 同套路：[77. 组合](77-combinations.md) —— 纯长度限制，无目标和
- 知识点：回溯组合+目标和模板见[回溯](回溯.md)
