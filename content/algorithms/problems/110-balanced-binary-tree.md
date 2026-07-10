---
topics:
  - 二叉树
techniques:
  - 递归
---

# 110. 平衡二叉树（Balanced Binary Tree）

频次 ★★★★ · 难度 🟢 · 高频：字节/腾讯/美团

## 题目

给定一个二叉树，判断它是否是高度平衡的。平衡二叉树定义：**每个**节点的左右子树高度差不超过 1。

**示例**：
```
输入:     3                   输出: true
        / \
       9  20
          / \
         15  7

输入:     1                   输出: false
        / \
       2   2
      / \
     3   3
    / \
   4   4
```

## 思路

**自底向上递归（提前阻断）**：求子树高度的同时判断是否平衡：
- 如果子树不平衡，返回 `-1` 作为标记
- 如果平衡，返回子树的真实高度
- 当前节点左右子树都平衡且高度差 ≤ 1 时才是平衡的

自顶向下（先判断当前节点再递归左右）会重复计算高度，时间复杂度 O(n²)，不推荐。

## 代码

```java
public boolean isBalanced(TreeNode root) {
    return height(root) != -1;
}

private int height(TreeNode node) {
    if (node == null) return 0;
    int left = height(node.left);
    if (left == -1) return -1;
    int right = height(node.right);
    if (right == -1) return -1;
    if (Math.abs(left - right) > 1) return -1;
    return Math.max(left, right) + 1;
}
```

## 复杂度

- **时间**：O(n) —— 每个节点只访问一次
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：`null` 高度 0，平衡
- 单节点：左右子树高度都是 0，平衡
- 完全二叉树：平衡
- 单支树：高度差随深度增大，必然不平衡

## 变式

- **自顶向下版本**：`isBalanced = abs(depth(left) - depth(right)) ≤ 1 && isBalanced(left) && isBalanced(right)`，O(n²) 但容易理解
- **[1373. 二叉搜索子树的最大键值和](https://leetcode.cn/problems/maximum-sum-bst-in-binary-tree/)**：综合判 BST + 判平衡 + 求和的难题

## 易错点

- 定义是**每个节点**的左右子树高度差 ≤ 1，不是只在根节点判断——必须递归检查每个子树
- 自底向上的返回值既是高度又是平衡标记，用 `-1` 作为非法值的前提是树高不可能为负（空树高 0），这是常见的"用非法值做标记"技巧，面试中要解释清楚
- 容易忘递归检查左右子树各自的平衡性——只检查当前节点的高度差是不够的（反例：根平衡但子树不平衡）

## 面试追问

- **自顶向下为什么慢？** `depth()` 每次都要遍历整棵子树，上层节点会重复计算下层所有节点的高度，有大量重叠子问题；自底向上一次 DFS 同时返回高度和平衡信息，避免了重复计算
- **如何用迭代实现？** 后序遍历 + 栈，记录每个节点的高度（类似于用哈希表缓存计算结果），但代码比递归复杂得多，面试中推荐递归
- **和 BST 的关联？** 平衡二叉树只关心高度，不关心值的大小关系；AVL 树是在此基础上增加了 BST 性质

## 关联题

- 同套路：[104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md) —— 本题的子过程：求深度 + 判断平衡
- 进阶：[98. 验证二叉搜索树](98-validate-binary-search-tree.md) —— 树的另一性质判定；1373 综合了判平衡 + 判 BST
- 知识点：树递归的"自底向上"模式见[二叉树](二叉树.md)
