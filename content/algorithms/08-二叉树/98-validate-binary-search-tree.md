# 98. 验证二叉搜索树（Validate Binary Search Tree）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

判断一棵树是否是二叉搜索树（BST）：左子树所有节点 < 根，右子树所有节点 > 根，且递归成立。

**示例**：
```
输入:      2         输出: true
         / \
        1   3

输入:      5         输出: false（4 在右子树但 < 5）
         / \
        1   4
           / \
          3   6
```

## 思路

**两种主流解法**：

1. **递归传范围**：DFS 时给每个节点传入允许的 `(min, max)` 区间，节点值必须在区间内，然后递归收紧区间
2. **中序遍历**：BST 的中序遍历是严格升序的，递归中序时检查当前值是否大于上一个值

推荐解法 1，因为更直接、不依赖全局变量。

## 代码

```java
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}

private boolean validate(TreeNode node, Integer min, Integer max) {
    if (node == null) return true;
    if (min != null && node.val <= min) return false;
    if (max != null && node.val >= max) return false;
    return validate(node.left, min, node.val)
        && validate(node.right, node.val, max);
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(height)

## 边界条件

- 空树：true（空树被视为 BST）
- 单节点：true
- 值取 Integer.MAX_VALUE / MIN_VALUE：用 Integer 包装类（可为 null）避免 int 边界歧义
- 含相等值的树：BST 定义不等（≥或≤即非法）

## 变式

- **[501. BST 中的众数](https://leetcode.cn/problems/find-mode-in-binary-search-tree/)**：中序遍历统计，利用 BST 顺序性质避免哈希表
- 验证平衡二叉树 + BST（1373）：综合两道题
- 将有序数组/链表转换为 BST（108/109）：反过程——用单调性建树

## 易错点

- **`<` 和 `<=` 的区别**：BST 要求严格小于/大于，相等即非法
- **不能只检查 `node.left.val < node.val < node.right.val`**：必须保证整棵子树都在范围内——反例: `[10,5,15,null,null,6,20]`，6 在 10 的右子树但 < 10，只检查父节点会漏掉
- 用 `Integer` 而非 `int` 做参数：`null` 表示没有边界，`int` 初始值 0 会误判含 `Integer.MIN_VALUE` 的树

## 面试追问

- **中序遍历解法怎么写？** 递归中序维护 `prev`，每次 `node.val <= prev` 即返回 false。展示第二种解法说明"BST ↔ 中序升序"的双向理解

## 关联题

- 同套路：[230. BST 中第 K 小的元素](230-kth-smallest-element-in-a-bst.md) —— 利用中序遍历模版
- 进阶：[236. 二叉树的最近公共祖先](236-lowest-common-ancestor-of-a-binary-tree.md) —— BST 版 LCA 可利用值范围直接判断方向，比普通二叉树更简单
- 知识点：BST 的递归区间性质见[二叉树](algorithms/08-二叉树/README.md)；中序有序、以及不平衡时退化成链表，见[树](树.md#二叉搜索树)

---

[← 返回训练计划](社招算法训练计划.md)
