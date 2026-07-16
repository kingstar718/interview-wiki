---
topics:
  - 二叉树
techniques:
  - BFS层序
---

# 513. 找树左下角的值（Find Bottom Left Tree Value）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

找到二叉树最后一行最左边的节点值。假设根节点非空。

**示例**：
```
输入:    2
       /   \
      1     3
输出: 1

输入:      1
         /   \
        2     3
       /     / \
      4     5   6
            /
           7
输出: 7
```

## 思路

**两种解法**：

1. **BFS 层序遍历**：从右到左入队，最后一个出队的节点就是最深最左的节点。或者正常层序，每层记录第一个节点，最后一层取到。

2. **DFS 深度优先**：维护全局 `maxDepth` 和 `result`，前序遍历——若当前深度 > maxDepth，更新 result（因为前序先访问左，所以同一深度第一个遇到的就是最左的）。

推荐 BFS，代码更简洁。

## 代码

```java
// BFS 解法：从右到左入队，最后出队的就是左下角
public int findBottomLeftValue(TreeNode root) {
    Queue<TreeNode> q = new LinkedList<>();
    q.offer(root);
    TreeNode node = null;
    while (!q.isEmpty()) {
        node = q.poll();
        // 先右后左，这样最后一个出队的就是最左的
        if (node.right != null) q.offer(node.right);
        if (node.left != null) q.offer(node.left);
    }
    return node.val;
}
```

```java
// DFS 解法：记录最大深度，前序遍历优先更新
private int maxDepth = -1, result = 0;

public int findBottomLeftValue(TreeNode root) {
    dfs(root, 0);
    return result;
}

private void dfs(TreeNode node, int depth) {
    if (node == null) return;
    if (depth > maxDepth) {
        maxDepth = depth;
        result = node.val;
    }
    dfs(node.left, depth + 1);
    dfs(node.right, depth + 1);
}
```

## 复杂度

- **BFS**：时间 O(n)，空间 O(n)
- **DFS**：时间 O(n)，空间 O(height)

## 边界条件

- 单节点：返回该节点值
- 只有右子树：最后一层最左也就是右子树的某个节点
- 最后一层只有一个节点：可能在任何位置，BFS/DFS 都能正确处理

## 变式

- **找右下角的值**：BFS 改为先左后右入队（最后出队的是最右），或 DFS 改为中序/后序并优先右子树
- **找每层最左节点**：层序遍历时收集每层第一个节点
- **找树的最左叶子**：任意遍历，找到第一个叶子节点即可（404 的变体）

## 易错点

- BFS 从右到左入队时，**先入右再入左**，这样最后出队才是左下角。反过来（先左后右）最后出队的是右下角
- DFS 前序遍历时，`depth > maxDepth`（严格大于）才能保证同一深度第一个遇到的是最左——因为前序先走左边
- 不能用中序或后序代替前序——中序可能先访问右子树的左节点，导致深度相同时覆盖了左上的结果

## 面试追问

- **DFS 为什么用前序遍历？** 因为前序遍历保证同一深度先访问左边节点，`depth > maxDepth`（严格大于）确保第一个被记录的就是最左的。中序和后序不能保证这一点
- **BFS 和 DFS 怎么选？** BFS 代码更直观，但空间 O(n)；DFS 空间 O(height)，但在极端单支树上可能栈溢出。面试中两种都展示更好

## 关联题

- 同套路：[199. 二叉树的右视图](199-binary-tree-right-side-view.md) —— 同样是层序遍历取特定位置，但取每层最后一个
- 进阶：[404. 左叶子之和](404-sum-of-left-leaves.md) —— 关注"左"侧，但统计的是叶子节点和非叶子节点
- 知识点：BFS 层序遍历 + 方向入队技巧见[二叉树](二叉树.md)
