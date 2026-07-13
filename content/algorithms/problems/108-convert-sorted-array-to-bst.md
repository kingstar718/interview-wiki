---
topics:
  - 二叉树
techniques:
  - 递归
---

# 108. 将有序数组转换为二叉搜索树（Convert Sorted Array to Binary Search Tree）

频次 ★★★★ · 难度 🟢 · 高频：字节

## 题目

给定升序数组，将其转换为一棵**高度平衡**的二叉搜索树（左右子树高度差不超过 1）。

**示例**：
```
输入: [-10,-3,0,5,9]
输出:
     0
    / \
  -3   9
  /   /
-10  5
（多种答案均可）
```

## 思路

**递归分治**：取数组中间元素作为根节点，左半递归构造左子树，右半递归构造右子树。中间元素保证左右子树节点数相差不超过 1，即为高度平衡。

## 代码

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}

public TreeNode sortedArrayToBST(int[] nums) {
    return build(nums, 0, nums.length - 1);
}

private TreeNode build(int[] nums, int l, int r) {
    if (l > r) return null;
    int mid = l + (r - l) / 2;
    TreeNode root = new TreeNode(nums[mid]);
    root.left = build(nums, l, mid - 1);
    root.right = build(nums, mid + 1, r);
    return root;
}
```

## 复杂度

- **时间**：O(n) — 每个元素恰好访问一次
- **空间**：O(log n) — 递归栈深度（平衡树），最坏 O(n)（但实际上 BST 高度是 log n）

## 边界条件

- 空数组：`l > r`，返回 null
- 单元素：`mid = 0`，左右递归返回 null，返回单个节点
- 偶数个元素：`mid = (l + r) / 2` 取左中位数，构造的树可能偏向一侧但高度差不超过 1

## 变式

- **[109. 有序链表转换二叉搜索树](https://leetcode.cn/problems/convert-sorted-list-to-binary-search-tree/)** —— 链表不能随机访问，需要用快慢指针找中位数，或中序遍历模拟构造
- **[105. 从前序与中序遍历构造二叉树](105-construct-binary-tree-from-preorder-and-inorder-traversal.md)** —— 用两种遍历顺序重建树
- **[110. 平衡二叉树](110-balanced-binary-tree.md)** —— 判断一棵树是否平衡

## 易错点

- **取中间元素用 `l + (r - l) / 2` 防止溢出**：虽然 `nums.length` 通常在 int 范围内，但养成好习惯。也可以直接用 `(l + r) >>> 1`。
- 左右子树递归的范围不包含 `mid`：`[l, mid-1]` 和 `[mid+1, r]`。
- 递归终止条件为 `l > r` 时返回 null，不是 `l >= r`（否则会漏掉最后一个元素）。
- 题目要求高度平衡但不要求"唯一"——中间元素可以取左中位数或右中位数，都是正确答案。

## 面试追问

- **为什么选中间元素能保证平衡？** 左右子树节点数相差不超过 1，递归下去每层都如此，树高度为 O(log n)。
- **如果链表怎么处理？** 快慢指针找中位数，但每次找中位数需要遍历，复杂度 O(n log n)。优化方案：先计算链表长度，用中序遍历方式模拟构造（按顺序填值），复杂度 O(n)。
- **如果数组不是严格升序但有重复值呢？** BST 的定义决定左子树必须严格小于根，通常取左边第一个不等的值作为边界。但本题明确是升序数组，不需要考虑重复值。

## 关联题

- 同套路：[105. 从前序与中序遍历构造二叉树](105-construct-binary-tree-from-preorder-and-inorder-traversal.md) —— 递归构造二叉树
- 进阶：[109. 有序链表转换二叉搜索树](https://leetcode.cn/problems/convert-sorted-list-to-binary-search-tree/) —— 链表版本，中位数查找方式不同
- 知识点：递归分治构造树见[二叉树](二叉树.md)
