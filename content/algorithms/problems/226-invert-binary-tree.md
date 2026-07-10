---
topics:
  - 二叉树
techniques:
  - 递归返回值设计
---

# 226. 翻转二叉树（Invert Binary Tree）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

翻转二叉树，即交换所有节点的左右子树。

**示例**：
```
输入:      4         输出:      4
         /   \              /   \
        2     7            7     2
       / \   / \          / \   / \
      1   3 6   9        9   6 3   1
```

## 思路

**递归**：每个节点交换左右子树，然后递归处理左右子节点。先交换再递归或先递归再交换结果一样。

## 代码

```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    // 交换左右子树
    TreeNode tmp = root.left;
    root.left = root.right;
    root.right = tmp;
    // 递归处理
    invertTree(root.left);
    invertTree(root.right);
    return root;
}
```

## 复杂度

- **时间**：O(n) —— 每个节点访问一次
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：返回 null
- 单节点：没有子树可交换，返回自身
- 只有左/右子树的节点：交换后单边变对边

## 变式

- **迭代版**：用栈/队列辅助，前序/层序模拟递归
- **对称二叉树（101）**：翻转后与原树比较，等价于判断左右子树是否镜像相等

## 易错点

- 不能写成 `root.left = invertTree(root.right); root.right = invertTree(root.left);`——第一行把左子树覆盖后，第二行用的左子树已经被新值覆盖了
- 返回 `root` 而不是 `root.left`（容易惯性写成递归调用的返回值）

## 面试追问

- **为什么这道题有名？** 因为 homebrew 作者 Max Howell 因没写出这题被 Google 拒了，引发"面试考翻转二叉树是否合理"的讨论。在面经里提这个故事算加分项
- **迭代版怎么写？** 层序或前序，每次交换弹出节点的左右子。展示一下对递归转迭代的掌握

## 关联题

- 同套路：[101. 对称二叉树](https://leetcode.cn/problems/symmetric-tree/) —— 镜像相等判定的本质就是翻转后自比
- 进阶：[105. 从前序与中序遍历构造二叉树](105-construct-binary-tree-from-preorder-and-inorder-traversal.md) —— 翻转概念的逆向：通过遍历顺序重建树结构
- 知识点：二叉树递归遍历模板见[二叉树](二叉树.md)

