# 235. 二叉搜索树的最近公共祖先（Lowest Common Ancestor of a Binary Search Tree）

频次 ★★★★ · 难度 🟢 · 高频：字节/阿里

## 题目

在 BST 中找两个节点 p、q 的最近公共祖先（LCA）。最近公共祖先：p 和 q 在树中深度最大的公共祖先节点。

**示例**：
```
输入: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
输出: 6（2 和 8 的 LCA 是 6）

输入: p = 2, q = 4
输出: 2（一个节点可以是自己的祖先）
```

## 思路

**利用 BST 有序性**：p、q 的值与 root.val 比较：

- 若 p、q 都小于 root.val → 二者都在左子树，递归左边
- 若 p、q 都大于 root.val → 二者都在右子树，递归右边
- 否则（一个在左一个在右，或其中一个等于 root）→ root 就是 LCA

相比 [236. 二叉树的最近公共祖先](236-lowest-common-ancestor-of-a-binary-tree.md)，本题不需要后序遍历找 p、q 位置——BST 的值大小直接告诉你方向。

## 代码

```java
// 迭代版（推荐，空间 O(1)）
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    int min = Math.min(p.val, q.val);
    int max = Math.max(p.val, q.val);
    while (root != null) {
        if (root.val > max) {
            root = root.left;        // 都在左子树
        } else if (root.val < min) {
            root = root.right;       // 都在右子树
        } else {
            return root;             // 分叉点就是 LCA
        }
    }
    return null;
}
```

```java
// 递归版
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root.val > p.val && root.val > q.val) {
        return lowestCommonAncestor(root.left, p, q);
    }
    if (root.val < p.val && root.val < q.val) {
        return lowestCommonAncestor(root.right, p, q);
    }
    return root;
}
```

## 复杂度

- **时间**：O(h) —— 最坏 O(n)（退化为链表），平均 O(log n)
- **空间**：迭代 O(1)，递归 O(h)

## 边界条件

- p 或 q 等于 root：root 自身就是 LCA（一个节点可以是自己的祖先）
- p 是 q 的祖先：直接返回 p（p 在 q 的路径上）
- 退化为链表：搜索路径可能需要走 O(n) 步，但不会走错方向
- p 和 q 不在树中：题目保证一定在

## 变式

- **[236. 二叉树的最近公共祖先](236-lowest-common-ancestor-of-a-binary-tree.md)**：普通二叉树版，没有 BST 性质，需要后序遍历
- **多个节点的 LCA**：对所有节点值取 min 和 max，同样用 BST 性质一次遍历
- **求 LCA 但不允许比较值**：退化到 236 的普通二叉树解法

## 易错点

- 不要和 236 混淆——236 是普通二叉树，必须用后序遍历找 p、q 位置；235 是 BST，直接用值大小判断方向
- 迭代版循环条件：`root.val > max` 和 `root.val < min`，注意 `max` 和 `min` 的用法——先确定 p、q 的 min/max 便于判断
- 分叉条件：`root.val >= min && root.val <= max` 时返回 root——包括 `root.val == p.val` 或 `root.val == q.val` 的情况

## 面试追问

- **为什么 235 比 236 简单？** 因为 BST 有序性提供了"方向信息"——不需要回溯，沿着一条路径就能找到分叉点。236 需要后序遍历汇总左右子树"是否包含 p/q"的信息
- **如果树不是 BST 但你知道 p、q 的值范围？** 没有 BST 性质就没有"整个子树都在某个区间"的保证，不能直接用方向搜索，必须用 236 的后序遍历

## 关联题

- 同套路：[236. 二叉树的最近公共祖先](236-lowest-common-ancestor-of-a-binary-tree.md) —— 普通二叉树版 LCA，比本题多一个难度等级
- 进阶：[700. 二叉搜索树中的搜索](700-search-in-a-binary-search-tree.md) —— BST 的方向搜索是本题的基础
- 知识点：BST 有序性在 LCA 场景下的应用见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)