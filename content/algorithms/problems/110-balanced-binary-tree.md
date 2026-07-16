---
topics:
  - 二叉树
techniques:
  - 递归返回值设计
---

# 110. 平衡二叉树（Balanced Binary Tree）

频次 ★★★★ · 难度 🟢 · 高频：字节/美团

## 题目

判断一棵二叉树是否是平衡二叉树：任意节点的左右子树高度差不超过 1。

**示例**：
```
输入: [3,9,20,null,null,15,7]     输出: true
输入: [1,2,2,3,3,null,null,4,4]   输出: false
```

## 思路

**后序遍历求高度 + 剪枝**：自底向上计算每个节点的高度，若 `|leftHeight - rightHeight| > 1` 则返回 -1 标记"不平衡"，上层发现 -1 后直接返回 -1 不再继续计算。

关键优化：不在求高度之外另起一个平衡判断函数——**一次递归同时完成高度计算和平衡判断**，避免 O(n²) 的重复计算。

## 代码

```java
public boolean isBalanced(TreeNode root) {
    return height(root) != -1;
}

// 返回树的高度，若不平衡则返回 -1
private int height(TreeNode node) {
    if (node == null) return 0;
    int leftH = height(node.left);
    if (leftH == -1) return -1;   // 剪枝
    int rightH = height(node.right);
    if (rightH == -1) return -1;  // 剪枝
    if (Math.abs(leftH - rightH) > 1) return -1;
    return 1 + Math.max(leftH, rightH);
}
```

## 复杂度

- **时间**：O(n) —— 每个节点只访问一次
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：平衡，返回 true
- 单节点：平衡，高度为 1
- 单支树（链状）：高度差超过 1，返回 false
- 左右子树高度差恰好为 1：平衡（如示例 1）

## 变式

- **自顶向下 O(n²) 版**：先求左右子树高度，再递归判断左右子树是否平衡。面试中如果先写出这版，面试官会追问优化
- 要求返回"最小不平衡子树"：修改返回值为 `{int height, TreeNode unbalancedRoot}`

## 易错点

- 不能用"整棵树的最大深度 - 最小深度 ≤ 1"来判断平衡——平衡要求每个子树都满足，不是只看全局
- 高度的定义：叶子节点高度为 1，空节点高度为 0。本题用节点数定义高度，和 [104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md) 一致
- 递归返回 -1 后上层必须检查并传递，否则上层会继续计算导致错误

## 面试追问

- **自顶向下 vs 自底向上有什么区别？** 自顶向下每个节点都要算一遍子树高度，O(n²)；自底向上一次遍历即可，O(n)。追问"自底向上的本质是什么"——本质是后序遍历，先拿到子树信息再判断当前节点
- **如果树频繁增删节点，怎么高效维护平衡判断？** 引出 AVL 树 / 红黑树的结构化平衡方案，说明平衡二叉树是逻辑概念，AVL 是强制维护的实现

## 关联题

- 同套路：[104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md) —— 高度计算是本题的基础操作
- 进阶：[543. 二叉树的直径](543-diameter-of-binary-tree.md) —— 同样是后序遍历求高度，但合并逻辑从"比较差"变为"求和"
- 知识点：后序遍历自底向上模式见[二叉树](二叉树.md)
