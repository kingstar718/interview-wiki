---
topics:
  - 二叉树
---

# 543. 二叉树的直径（Diameter of Binary Tree）

频次 ★★★★ · 难度 🟢 · 高频：美团/字节

## 题目

二叉树中任意两个节点间**最长路径的长度**（路径 = 边数，不一定经过根）。

**示例**：
```
输入:         1
             / \
            2   3
           / \
          4   5
输出: 3    （路径 4→2→1→3 或 5→2→1→3，共 3 条边）
```

## 思路

**DFS 后序遍历**：每个节点计算"经过该节点的最长路径" = 左子树深度 + 右子树深度（左右各往下走最大深度），全局取 max。

递归函数返回该节点的深度（`1 + max(left, right)`），同时用全局变量更新经过当前节点的路径长度。

## 代码

```java
private int max = 0;

public int diameterOfBinaryTree(TreeNode root) {
    depth(root);
    return max;
}

private int depth(TreeNode node) {
    if (node == null) return 0;
    int left = depth(node.left);
    int right = depth(node.right);
    max = Math.max(max, left + right);     // 经过当前节点的最长路径（边数）
    return 1 + Math.max(left, right);      // 返回子树深度（节点数）
}
```

## 复杂度

- **时间**：O(n) —— 每个节点一次后序遍历
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：max = 0，返回 0
- 单节点：left + right = 0，max = 0
- 完全二叉树：直径不一定经过根，可能在子树内

## 变式

- **[124. 二叉树中的最大路径和](124-binary-tree-maximum-path-sum.md)**：把边数求和换成节点值求和，需要在递归中处理负数
- **N 叉树直径**：取深度最大的两个子节点做和
- **求直径的路径本身（不限于长度）**：DFS 时同时记录路径

## 易错点

- **直径是边数不是节点数**：`left + right` 直接相加就是边数（每条边对应一次子树深度贡献），不需要 `+1`
- `max` 在递归过程中更新，不是最后才算——所以不能省全局变量
- 直径不一定过根，比如左子树的内部路径可能更长——这也是为什么不能在根节点直接返回 `leftDepth + rightDepth`

## 面试追问

- **直径的"边数"和"节点数"怎么区分？** 本题定义是边数（两个节点间 edges 数量），如果要求节点数则返回 `left + right + 1`。面试中主动确认定义
- **如果直径必须经过根？** 那就是 `leftDepth + rightDepth`，退化为一行的计算，不用全局变量

## 关联题

- 同套路：[104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md) —— 本题的深度计算是子过程
- 进阶：[124. 二叉树中的最大路径和](124-binary-tree-maximum-path-sum.md) —— 从边数求和升级到权值求和，需要考虑负数截断
- 知识点：树形 DP 的"后序遍历 + 全局变量"模式见[二叉树](二叉树.md)

