# 108. 将有序数组转换为二叉搜索树（Convert Sorted Array to Binary Search Tree）

频次 ★★★★ · 难度 🟢 · 高频：字节/美团

## 题目

将一个升序数组转换为一棵**高度平衡**的 BST：任意节点左右子树高度差不超过 1。

**示例**：
```
输入: [-10, -3, 0, 5, 9]
输出:     0
        /   \
      -3     9
      /     /
    -10    5
（或 [0,-3,9,-10,null,5] 等其他高度平衡 BST）
```

## 思路

**二分递归**：升序数组天然满足 BST 中序遍历结果。取中间元素为根，左边子数组递归构造左子树，右边子数组递归构造右子树。

取中间元素时，`mid = left + (right - left) / 2`（偏左）或 `mid = (left + right + 1) / 2`（偏右）都能得到高度平衡的 BST，答案不唯一。

## 代码

```java
public TreeNode sortedArrayToBST(int[] nums) {
    return build(nums, 0, nums.length - 1);
}

private TreeNode build(int[] nums, int left, int right) {
    if (left > right) return null;
    int mid = left + (right - left) / 2;  // 取中间偏左
    TreeNode root = new TreeNode(nums[mid]);
    root.left = build(nums, left, mid - 1);
    root.right = build(nums, mid + 1, right);
    return root;
}
```

## 复杂度

- **时间**：O(n) —— 每个元素创建一个节点
- **空间**：O(log n) —— 平衡树的递归深度

## 边界条件

- 空数组：返回 null
- 单元素数组：返回单节点
- 偶数个元素：取 `left + (right - left) / 2` 会偏左，取 `(left + right + 1) / 2` 会偏右，都合法
- 数组长度很大：递归深度 O(log n) 不会栈溢出

## 变式

- **[109. 有序链表转换 BST](https://leetcode.cn/problems/convert-sorted-list-to-binary-search-tree/)**：链表无法 O(1) 取中间元素，需要快慢指针找中点，或者中序遍历 + 全局指针
- 要求"最平衡"（左右节点数差 ≤ 1）：`mid = (left + right) / 2` 即可保证
- 转换为任意 BST（不要求平衡）：取第一个元素为根，其余递归到右子树

## 易错点

- 递归终止条件 `left > right` 而非 `left == right`——`left == right` 时还要创建单个节点
- mid 计算用 `left + (right - left) / 2` 防溢出，不要用 `(left + right) / 2`
- 左子树范围 `[left, mid - 1]`，右子树范围 `[mid + 1, right]`——mid 作为根不参与子树递归

## 面试追问

- **为什么取中间元素能保证高度平衡？** 二分后左右子数组长度差 ≤ 1，递归构造出的子树高度差也 ≤ 1。数学归纳法可证
- **链表版（109）怎么做？** 快慢指针找中点，O(n log n)；或者中序遍历 + 全局指针模拟，O(n) 且不需要找中点

## 关联题

- 同套路：[109. 有序链表转换二叉搜索树](https://leetcode.cn/problems/convert-sorted-list-to-binary-search-tree/) —— 链表版，找中点方式不同
- 进阶：[105. 从前序与中序遍历构造二叉树](105-construct-binary-tree-from-preorder-and-inorder-traversal.md) —— 同样通过二分递归构造树，但输入是两种遍历序列
- 知识点：二分递归构造树的模式见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)