---
topics:
  - 二叉树
techniques:
  - 递归
---

# 101. 对称二叉树（Symmetric Tree）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

判断一棵二叉树是否轴对称（镜像对称）。

**示例**：
```
输入:      1         输出: true
         /   \
        2     2
       / \   / \
      3   4 4   3

输入:      1         输出: false
         /   \
        2     2
         \     \
         3      3
```

## 思路

**递归比较**：两棵树镜像对称的条件是——根节点值相等，且左树的左子树与右树的右子树镜像对称，左树的右子树与右树的左子树镜像对称。

将"判断一棵树是否对称"转化为"判断左右两棵子树是否镜像"，递归函数接收两个节点 `left` 和 `right`，比较 `left.left` vs `right.right` 且 `left.right` vs `right.left`。

也可用队列迭代：每次入队两个节点（`left.left` 与 `right.right`、`left.right` 与 `right.left`），成对出队比较。

## 代码

```java
public boolean isSymmetric(TreeNode root) {
    if (root == null) return true;
    return isMirror(root.left, root.right);
}

private boolean isMirror(TreeNode left, TreeNode right) {
    if (left == null && right == null) return true;
    if (left == null || right == null) return false;
    if (left.val != right.val) return false;
    return isMirror(left.left, right.right)
        && isMirror(left.right, right.left);
}
```

## 复杂度

- **时间**：O(n) —— 每个节点访问一次
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：返回 true
- 单节点：返回 true（左右子树都为空，镜像成立）
- 左右子树结构不对称（一个为空一个非空）：返回 false
- 值相等但结构不对称（如反例中左右两个 2 的子树结构不同）：返回 false

## 变式

- **迭代版**：用队列成对入队比较，避免递归栈溢出
- **N 叉树的对称**：将子节点列表反转后比较
- 如果要求判断"翻转后是否相等"：等价于本题——翻转后自比就是对称性判定

## 易错点

- 不能只比较 `root.left.val == root.right.val` 就递归——必须比较"左的左 vs 右的右"和"左的右 vs 右的左"交叉匹配
- 空节点处理：`left == null && right == null` 返回 true 要先判断，否则后续 `left.val` 会 NPE

## 面试追问

- **迭代怎么写？** 用队列，初始入队 `(root.left, root.right)`，每次出队两个节点比较，再按交叉顺序入队子节点。展示对递归转迭代的掌握
- **和翻转二叉树的关系？** 翻转二叉树后与原树相等等价于原树是对称二叉树。两者本质相同：226 是操作，101 是判定

## 关联题

- 同套路：[226. 翻转二叉树](226-invert-binary-tree.md) —— 翻转后自比等价于对称判定
- 进阶：[100. 相同的树](https://leetcode.cn/problems/same-tree/) —— 判断两棵树是否完全相同，递归逻辑更简单（直接比较而非交叉）
- 知识点：二叉树递归模板见[二叉树](二叉树.md)
