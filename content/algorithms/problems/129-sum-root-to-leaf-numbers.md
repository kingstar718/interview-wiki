---
topics:
  - 二叉树
techniques:
  - DFS
---

# 129. 求根节点到叶节点数字之和（Sum Root to Leaf Numbers）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

给定二叉树，每个节点值 0-9。每条根到叶的路径代表一个数字（路径上节点值依次拼接），求所有路径数字之和。

**示例**：
```
输入: [1,2,3]
    1
   / \
  2   3
输出: 12 + 13 = 25

输入: [4,9,0,5,1]
    4
   / \
  9   0
 / \
5   1
输出: 495 + 491 + 40 = 1026
```

## 思路

**DFS 前序遍历**：递归时传递从根到当前节点的前缀数字 `prev = prev * 10 + root.val`。到达叶节点时累加到全局和。

也可以不用全局变量，递归返回"左子树和 + 右子树和"。

## 代码

```java
public int sumNumbers(TreeNode root) {
    return dfs(root, 0);
}

private int dfs(TreeNode node, int prev) {
    if (node == null) return 0;
    int cur = prev * 10 + node.val;
    if (node.left == null && node.right == null)
        return cur;                     // 叶节点，返回路径数字
    return dfs(node.left, cur) + dfs(node.right, cur);
}
```

## 复杂度

- **时间**：O(n) — 每个节点访问一次
- **空间**：O(height) — 递归栈深度

## 边界条件

- 空树：dfs 返回 0
- 单节点：`prev * 10 + val` 即为节点值本身
- 节点值为 0：如根为 0，左子为 5，路径数字为 05 = 5（前导 0 不影响数值）

## 变式

- **[112. 路径总和](112-path-sum.md)** —— 判断是否存在路径和为 target，减治法
- **[113. 路径总和 II](113-path-sum-ii.md)** —— 输出所有和为 target 的路径
- **[1022. 从根到叶的二进制数之和](https://leetcode.cn/problems/sum-of-root-to-leaf-binary-numbers/)** —— 同样框架，进制从 10 换为 2

## 易错点

- **数字拼接用 `prev * 10 + val`**，不是字符串拼接再 parseInt（后者效率低且违背面试希望看到的数学精度感）。
- 返回值的累加方式：`dfs(left) + dfs(right)`，不能只返回一边。
- 非叶节点不参与求和（题目要求"根到叶"才算一个数字），所以求和只在叶节点进行。

## 面试追问

- **如果节点值不限于 0-9 呢？** 需要大数处理，用字符串拼接或 BigInteger。但面试中通常限定 0-9。
- **BFS 层序能做吗？** 能——队列同时存节点和当前路径数字，层序遍历时更新。空间 O(n) 和 DFS 的 O(height) 各有优劣。
- **如果溢出怎么办？** LeetCode 原题保证结果在 int 范围内。如果题目改成不限位数，用 `long` 或 `BigInteger`。

## 关联题

- 同套路：[112. 路径总和](112-path-sum.md) —— 路径和问题的 DFS 框架
- 进阶：[113. 路径总和 II](113-path-sum-ii.md) —— 记录路径所有节点
- 知识点：DFS 前序遍历模板见[二叉树](二叉树.md)
