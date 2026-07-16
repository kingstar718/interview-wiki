---
topics:
  - 二叉树
techniques:
  - 递归
---

# 111. 二叉树的最小深度（Minimum Depth of Binary Tree）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

求二叉树的最小深度：从根到最近叶子节点的**路径上的节点数**。

**示例**：
```
输入: [3,9,20,null,null,15,7]     输出: 2（根→9）
输入: [2,null,3,null,4,null,5]   输出: 5（单支树，叶子是最后一个节点）
```

## 思路

**两种解法**：

1. **BFS 层序遍历**：逐层扫描，遇到第一个叶子节点（左右都为空）时返回当前层数。BFS 天然适合"最短路径"问题，因为第一次遇到叶子就是最小深度。

2. **递归 DFS**：注意和最大深度的区别——当左子树为空时，最小深度 = 1 + 右子树最小深度，不能直接取 min。因为叶子节点定义为"左右子节点都为空"，空子树不算叶子。

## 代码

```java
// BFS 解法（推荐，遇到第一个叶子即返回）
public int minDepth(TreeNode root) {
    if (root == null) return 0;
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    int depth = 1;
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (node.left == null && node.right == null) return depth;
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        depth++;
    }
    return depth;
}
```

```java
// DFS 递归解法
public int minDepth(TreeNode root) {
    if (root == null) return 0;
    if (root.left == null) return 1 + minDepth(root.right);
    if (root.right == null) return 1 + minDepth(root.left);
    return 1 + Math.min(minDepth(root.left), minDepth(root.right));
}
```

## 复杂度

- **BFS**：时间 O(n)，空间 O(n)（队列最坏存一层节点）
- **DFS**：时间 O(n)，空间 O(height)

## 边界条件

- 空树：返回 0
- 单节点：返回 1
- 单支树（只有左或只有右）：深度 = 节点数，不能返回 1（因为另一侧为空不算叶子深度）
- 根节点紧邻叶子（如 `[1,2]`）：返回 2

## 变式

- **N 叉树最小深度**：BFS 同样适用，遇到第一个无子节点的节点即返回
- **求最小深度对应的路径**：BFS 时记录 parent 指针，找到叶子后回溯

## 易错点

- **不能直接 `1 + Math.min(minDepth(left), minDepth(right))`**：当一侧子树为空时，空子树返回 0，min 会取 0，导致深度为 1——但空子树没有叶子，不能算深度
- DFS 版本的判断顺序：先判空子树，再正常取 min。如果在 `if (left == null)` 之前就取 min，会出错

## 面试追问

- **BFS 和 DFS 哪个更好？** BFS 在最好情况下 O(1) 个节点就找到叶子（根紧邻叶子），DFS 总要遍历到叶子。但 BFS 空间 O(n)（队列），DFS 空间 O(height)。追问"极端单支树"——BFS 队列存 1 个节点，DFS 递归栈深度 n，BFS 反而更好
- **为什么最小深度用 BFS，最大深度用 DFS？** 因为 BFS 天然适合"最短路径"问题——第一次遇到叶子就一定是最短；而最大深度无论哪种遍历都要访问全部节点，DFS 代码更简洁

## 关联题

- 同套路：[104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md) —— 逻辑几乎相同，但最大深度不用特殊处理空子树
- 进阶：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md) —— BFS 层序遍历模板
- 知识点：BFS vs DFS 的选择权衡见[二叉树](二叉树.md)
