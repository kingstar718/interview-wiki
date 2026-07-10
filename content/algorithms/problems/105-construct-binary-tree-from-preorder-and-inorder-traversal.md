---
topics:
  - 二叉树
techniques:
  - 树的构造与序列化
---

# 105. 从前序与中序遍历构造二叉树（Construct Binary Tree from Preorder and Inorder Traversal）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

根据前序和中序遍历结果重建二叉树（树中无重复值）。

**示例**：
```
前序: [3,9,20,15,7]
中序: [9,3,15,20,7]
输出: 二叉树 3 → 9 / 20 → 15 / 7
```

## 思路

**分治递归**：

- 前序第一个元素是根节点
- 在中序中找到根位置，左侧是左子树、右侧是右子树
- 前序中紧跟根之后的「左子树长度」个元素是左子树的前序序列，再之后是右子树前序
- 递归处理

优化：用 `Map<值, 中序下标>` 把根定位加速到 O(1)。

## 代码

```java
private Map<Integer, Integer> inMap;  // 值 → 中序索引

public TreeNode buildTree(int[] preorder, int[] inorder) {
    inMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) inMap.put(inorder[i], i);
    return build(preorder, 0, preorder.length - 1, 0);
}

// build 参数: 前序数组, 前序左边界, 前序右边界, 中序左边界
private TreeNode build(int[] pre, int preL, int preR, int inL) {
    if (preL > preR) return null;
    TreeNode root = new TreeNode(pre[preL]);
    int inIdx = inMap.get(root.val);       // 根在中序中的位置
    int leftSize = inIdx - inL;            // 左子树节点数
    root.left = build(pre, preL + 1, preL + leftSize, inL);
    root.right = build(pre, preL + leftSize + 1, preR, inIdx + 1);
    return root;
}
```

## 复杂度

- **时间**：O(n) —— HashMap 加速中序查找
- **空间**：O(n) —— HashMap + 递归栈

## 边界条件

- 长度为 0：返回 null
- 单节点：返回该节点
- 左/右单支树：leftSize = 0 或 rightSize = 0

## 变式

- **[106. 从中序与后序遍历构造二叉树](https://leetcode.cn/problems/construct-binary-tree-from-inorder-and-postorder-traversal/)**：后序最后一个元素是根，其余步骤对称
- **[889. 从前序与后序构造二叉树](https://leetcode.cn/problems/construct-binary-tree-from-preorder-and-postorder-traversal/)**：前序定根、后序定左子树大小，但结果可能不唯一
- **数组转 BST（108/109）**：有序数组/链表取中间做根，递归建树

## 易错点

- **前序中左子树范围的边界计算**：`preL + 1`（左子树前序起点）到 `preL + leftSize`（左子树前序终点），`preL + leftSize + 1`（右子树前序起点）。多一个少一个都错
- 中序的边界用 `inL` 和 `inIdx`，不要引入 `inR`，因为可以从 `leftSize` 反推
- 没有重复值这个条件很重要——有重复值时无法确定根在中序中的位置，需要其他约束

## 面试追问

- **为什么前序 + 中序能唯一确定一棵二叉树？** 前序提供根，中序提供左右划分——两序列各自贡献一部分信息。如果只给前序 + 后序，当树不是满二叉树时，结果不唯一
- **不用 HashMap 的优化思路？** 每次在中序中线性查找根，最坏 O(n²)，但可以先答"Hash 优化到 O(n)"再简述"如果面试官说不准用额外空间，可以用双指针推进中序"

## 关联题

- 同套路：[106. 从中序与后序构造二叉树](https://leetcode.cn/problems/construct-binary-tree-from-inorder-and-postorder-traversal/) —— 遍历顺序不同
- 进阶：[124. 二叉树中的最大路径和](124-binary-tree-maximum-path-sum.md) —— 建树后的另一类树形问题
- 知识点：二叉树遍历的性质（前序定根、中序定左右）见[二叉树](二叉树.md)

