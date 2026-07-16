---
topics:
  - 二叉树
techniques:
  - 递归返回值设计
---

# 337. 打家劫舍 III（House Robber III）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

二叉树，相邻节点不能同时偷（直接相连的父子），求最大金额。

**示例**：
```
输入: [3,2,3,null,3,null,1]
     3
    / \
   2   3
    \   \
     3   1
输出: 7  （偷 3(root)+3(右孙子)+1(右孙子) = 7）
```

## 思路

**树形 DP**：每个节点返回一个 `int[2]`，`res[0]` = 不偷当前节点的最大收益，`res[1]` = 偷当前节点的最大收益。

后序遍历：
- 偷当前节点：`res[1] = node.val + left[0] + right[0]`（偷了当前就不能偷左右孩子）
- 不偷当前节点：`res[0] = max(left[0], left[1]) + max(right[0], right[1])`（左右孩子可选偷或不偷）

## 代码

```java
public int rob(TreeNode root) {
    int[] res = dfs(root);
    return Math.max(res[0], res[1]);
}

private int[] dfs(TreeNode node) {
    if (node == null) return new int[]{0, 0};
    int[] left = dfs(node.left);
    int[] right = dfs(node.right);
    int[] res = new int[2];
    res[0] = Math.max(left[0], left[1]) + Math.max(right[0], right[1]); // 不偷当前
    res[1] = node.val + left[0] + right[0];                              // 偷当前
    return res;
}
```

## 复杂度

- **时间**：O(n)，每个节点访问一次
- **空间**：O(h)，递归栈深度

## 边界条件

- 空树：返回 0
- 单节点：返回 node.val
- 全负值：LeetCode 值非负，不涉及

## 变式

- **[198. 打家劫舍](198-house-robber.md)**：数组版，`dp[i] = max(dp[i-1], dp[i-2]+nums[i])`
- **[213. 打家劫舍 II](213-house-robber-ii.md)**：环形数组版，拆成两个区间

## 易错点

- 后序遍历：必须先拿到左右子树的返回结果，才能计算当前节点
- `res[0]`（不偷当前）时，左右可以偷也可以不偷，取 max
- `res[1]`（偷当前）时，左右只能取"不偷"的值

## 面试追问

- **如果加记忆化？** 用 HashMap 存 `(node, canRob)` 的值，递归时查表——但不如树形 DP 优雅
- **和 198/213 的关系？** 从一维数组 → 环形数组 → 树，都是"相邻不能同时选"的约束下求最大和

## 关联题

- 同套路：[198. 打家劫舍](198-house-robber.md) —— 一维数组版
- 进阶：[213. 打家劫舍 II](213-house-robber-ii.md) —— 环形数组版
- 知识点：树形 DP 模板见[动态规划](动态规划与贪心.md)
