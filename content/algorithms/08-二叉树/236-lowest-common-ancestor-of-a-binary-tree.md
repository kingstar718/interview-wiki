# 236. 二叉树的最近公共祖先（Lowest Common Ancestor of a Binary Tree）

频次 ★★★★★ · 难度 🟡 · 高频：字节/腾讯/美团

## 题目

给定二叉树和两个节点 p、q，找到它们的最近公共祖先（LCA）。

**示例**：
```
输入: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
输出: 3
```

## 思路

**递归后序遍历**：从叶子向上返回 p/q 的"发现信息"。

- 当前节点 = null → 返回 null
- 当前节点 = p 或 q → 返回当前节点（找到了）
- 否则递归查左右子树：左右都非 null（p、q 各在一侧）→ 当前节点就是 LCA；只有一侧非 null → 返回该侧

## 代码

```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);
    if (left != null && right != null) return root;  // p、q 在两侧，root 是 LCA
    return left != null ? left : right;               // 全在一侧，返回找到的那一侧
}
```

## 复杂度

- **时间**：O(n) —— 每个节点至多访问一次
- **空间**：O(height) —— 递归栈深度

## 边界条件

- p == q：LCA 就是 p（或 q）
- p 是 q 的祖先：递归到 p 时直接返回 p，q 在 p 的子树中，函数自然返回 p
- 根节点是 LCA：左右各找到一个
- p 或 q 不存在于树中：题目保证都存在

## 变式

- **[235. BST 的最近公共祖先](https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-search-tree/)**：利用 BST 值范围直接判断方向，无需递归整棵树
- **多次查询 LCA**：用 Tarjan 离线算法（并查集 + DFS）或树上二进制倍增（需要预处理 parent、depth 表）
- **N 叉树 LCA**：遍历所有子节点，统计返回非 null 的子节点个数

## 易错点

- **不能简单判断 `root.val` 是否在 p 和 q 之间**——这是 BST 的解法；普通二叉树的值没有顺序，只能用引用判断
- 递归结束条件：包含 `root == p || root == q`，因为一旦找到其中一个就不用往下走了（如果另一个在它的子树里，它本身就是 LCA）
- 函数返回的是 TreeNode 而不是 boolean：左右同时非 null 才确认 LCA

## 面试追问

- **如果树很大且多次查询 LCA，怎么优化？** 二进制倍增：O(n log n) 预处理、O(log n) 每次查询。或者 Tarjan 离线方法。场景：树的深度大且查询量大
- **和 BST 版的 LCA 有什么区别？** BST 版利用值的区间直接判断方向，不需要递归整棵树；普通二叉树只能全量后序。这题是经典的分治 + 回溯

## 关联题

- 同套路：[235. BST 的最近公共祖先](https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-search-tree/) —— 利用顺序性质简化
- 进阶：[98. 验证二叉搜索树](98-validate-binary-search-tree.md) —— 另一种"递归 + 返回值"的树形判断模式
- 知识点：二叉树后序遍历的"回溯"模式见[二叉树](algorithms/08-二叉树/README.md)

