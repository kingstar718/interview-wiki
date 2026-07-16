---
topics:
  - 二叉树
techniques:
  - BST中序
---

# 669. 修剪二叉搜索树（Trim a Binary Search Tree）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

修剪 BST，使所有节点的值都在 `[low, high]` 区间内，返回修剪后的根节点。修剪后仍需保持 BST 性质。

**示例**：
```
输入: root = [3,0,4,null,2,null,null,1], low = 1, high = 3
输出: [3,2,null,1]
（0 被修剪，因为 0 < 1；4 也被修剪，因为 4 > 3）
```

## 思路

**利用 BST 性质递归修剪**：

- `root.val < low`：当前节点及左子树全部小于 low，**全部丢弃**，递归修剪右子树并返回
- `root.val > high`：当前节点及右子树全部大于 high，**全部丢弃**，递归修剪左子树并返回
- `low <= root.val <= high`：当前节点保留，分别递归修剪左右子树，返回当前节点

关键：和 [450. 删除二叉搜索树中的节点](450-delete-node-in-a-bst.md) 不同，这里不是删除单个节点，而是利用 BST 性质**批量剪掉整棵子树**。

## 代码

```java
public TreeNode trimBST(TreeNode root, int low, int high) {
    if (root == null) return null;
    if (root.val < low) {
        // 当前节点及左子树全部丢弃，递归修剪右子树
        return trimBST(root.right, low, high);
    }
    if (root.val > high) {
        // 当前节点及右子树全部丢弃，递归修剪左子树
        return trimBST(root.left, low, high);
    }
    // 当前节点在区间内，保留并递归修剪左右
    root.left = trimBST(root.left, low, high);
    root.right = trimBST(root.right, low, high);
    return root;
}
```

## 复杂度

- **时间**：O(n) —— 最坏访问所有节点
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：返回 null
- 所有节点都在区间外：返回 null
- 所有节点都在区间内：原树不变
- low > high：题目保证不会出现
- 区间恰好覆盖部分子树：BST 性质保证在位值区间外的子树可以整棵丢弃

## 变式

- **修剪普通二叉树**：没有 BST 性质，只能遍历每个节点判断，不能整棵子树丢弃
- **修剪后返回被修剪的节点列表**：递归时收集被丢弃的节点
- **保留区间外的节点为新树**：同时返回修剪后的树和被修剪的子树

## 易错点

- `root.val < low` 时直接返回 `trimBST(root.right, low, high)`，**不需要再递归左子树**——BST 性质保证左子树所有节点都 < root.val < low，全部小于区间
- 同理 `root.val > high` 时直接返回 `trimBST(root.left, low, high)`，跳过右子树
- 在区间内时，必须用 `root.left = trimBST(...)` 和 `root.right = trimBST(...)` 接住返回值——因为修剪后子树可能为空

## 面试追问

- **为什么可以整棵子树丢弃？** 因为 BST 的性质：左子树所有节点 < root.val < 右子树所有节点。如果 root.val < low，那么左子树全部 < low，必然全部在区间外，无需逐个检查
- **和 450 删除 BST 节点的区别？** 450 是删除单个节点，需要考虑"接替"逻辑；669 是批量修剪，利用 BST 性质直接跳过整棵子树，不需要逐个处理

## 关联题

- 同套路：[450. 删除二叉搜索树中的节点](450-delete-node-in-a-bst.md) —— 单个节点删除，需要处理接替逻辑
- 进阶：[701. 二叉搜索树中的插入操作](701-insert-into-a-binary-search-tree.md) —— 插入也是利用 BST 性质搜索位置
- 知识点：BST 区间性质在批量操作中的应用见[二叉树](二叉树.md)
