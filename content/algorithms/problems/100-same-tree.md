---
topics:
  - 二叉树
techniques:
  - 递归
---

# 100. 相同的树（Same Tree）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

给定两棵二叉树 `p` 和 `q`，判断它们是否完全相同（结构和节点值都相同）。

**示例**：
```
输入: p = [1,2,3], q = [1,2,3]
输出: true

输入: p = [1,2], q = [1,null,2]
输出: false
```

## 思路

**递归比较**：两棵树相同当且仅当根节点值相等，且左右子树分别相同。递归终止条件：两个都为空 → true；一个为空 → false；值不等 → false。

也可以**迭代**使用双队列同时层序遍历两棵树，每轮取出两个节点比较。

## 代码

```java
public boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    if (p.val != q.val) return false;
    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
}
```

## 复杂度

- **时间**：O(n) — 每个节点访问一次
- **空间**：O(h) — 递归调用栈深度 h（最坏 O(n)，平均 O(log n)）

## 边界条件

- 两棵树都为空：`null == null`，返回 true。
- 一棵为空一棵非空：`p == null || q == null` 返回 false。
- 值相等但结构不同：递归到某一层会发现一个节点有子节点而另一个没有。

## 变式

- **对称树**（[101. 对称二叉树](101-symmetric-tree.md)）：改一下递归方向，`isSameTree(p.left, q.right) && isSameTree(p.right, q.left)`。
- **子树判断**（572. Subtree of Another Tree）：在 `isSameTree` 基础上，遍历大树每个节点作为根与子树比较。
- **迭代版**：用双端队列同步存放两棵树的待比较节点，BFS 层序比较。

## 易错点

- 递归条件顺序很重要：先判断 `p == null && q == null`，再判断 `p == null || q == null`，顺序颠倒会导致空指针（先判断 `p == null || q == null` 时，如果都为空直接就返回 true 了其实也没问题，但整体逻辑要清晰）。
- `p.val != q.val` 是短路条件：值不等直接返回 false，不需要继续递归。
- 结构比较比值比较更重要：即使值相等，结构不同也要返回 false。

## 面试追问

- **递归和迭代各有什么优劣？** 递归代码简洁直观，但可能栈溢出（树很深时）；迭代用显式队列没有栈溢出风险，但代码更长。面试时优先给出递归，再说明可用队列实现迭代。
- **如果节点数量上亿，内存放不下怎么办？** 大概率面试不会涉及这种极端情况；如果真的需要，可以用分布式比较（分片 + MapReduce），或利用树的某种序列化哈希来比较。

## 关联题

- 同套路：[101. 对称二叉树](101-symmetric-tree.md)、[104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md)、[110. 平衡二叉树](110-balanced-binary-tree.md) —— 递归判定树的性质
- 进阶：[226. 翻转二叉树](226-invert-binary-tree.md)（翻转后比较就是对称判断）
- 易混：[572. 另一棵树的子树](https://leetcode.cn/problems/subtree-of-another-tree/)（在大树中找子树）
- 知识点：二叉树递归遍历模板见[二叉树](二叉树.md)
