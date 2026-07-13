# 450. 删除二叉搜索树中的节点（Delete Node in a BST）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

在 BST 中删除值为 key 的节点，返回删除后的 BST 根节点。若 key 不存在，返回原树。

**示例**：
```
输入: root = [5,3,6,2,4,null,7], key = 3
输出: [5,4,6,2,null,null,7] 或 [5,2,6,null,4,null,7]
（删除 3 后，左子树最大节点 2 或右子树最小节点 4 都可以接替）
```

## 思路

**分两步：搜索 + 删除逻辑**。

搜索：利用 BST 性质找到目标节点。

删除分三种情况：

1. **无子节点（叶子）**：直接返回 null
2. **有一个子节点**：返回该子节点（子节点接替位置）
3. **有两个子节点**：找右子树的最小节点（后继），将其值赋给当前节点，然后递归删除右子树中的后继节点

## 代码

```java
public TreeNode deleteNode(TreeNode root, int key) {
    if (root == null) return null;
    if (key < root.val) {
        root.left = deleteNode(root.left, key);
    } else if (key > root.val) {
        root.right = deleteNode(root.right, key);
    } else {
        // 找到目标节点，执行删除
        // 情况 1 & 2：无子节点或只有一个子节点
        if (root.left == null) return root.right;
        if (root.right == null) return root.left;
        // 情况 3：有两个子节点，找后继
        TreeNode successor = findMin(root.right);
        root.val = successor.val;
        root.right = deleteNode(root.right, successor.val);
    }
    return root;
}

private TreeNode findMin(TreeNode node) {
    while (node.left != null) node = node.left;
    return node;
}
```

## 复杂度

- **时间**：O(h) —— 搜索 + 找后继都是 O(h)，最坏 O(n)
- **空间**：O(h) —— 递归栈深度

## 边界条件

- key 不存在于树中：递归到 null 返回 null，树不变
- 删除根节点：同样走三种情况逻辑
- 删除叶子节点：返回 null，父节点指向 null
- 后继节点本身有右子树：`deleteNode(root.right, successor.val)` 会递归处理后继节点（后继一定没有左子树，可能只有右子树）

## 变式

- **用前驱（左子树最大值）替换**：对称操作，找左子树最右节点
- **不修改节点值，通过指针操作删除**：需要维护 parent 指针，更复杂但更贴近实际工程
- **批量删除多个 key**：依次删除，或先排序后一次遍历处理

## 易错点

- 情况 3 中，后继节点的值赋给当前节点后，**必须递归删除后继节点**，不能只改值不删原节点
- 后继节点可能不是叶子（可能有右子节点），递归删除能正确处理此情况
- `root.left = deleteNode(root.left, key)` 和 `root.right = deleteNode(...)` 的返回值必须赋值回去——因为删除后子树结构可能改变

## 面试追问

- **为什么找后继而不是前驱？** 都可以，两者对称。后继是右子树最小值，前驱是左子树最大值。两种方式都能保持 BST 性质
- **如果频繁删除，BST 会退化吗？** 会——删除操作不会维持平衡。如果要求平衡，需要 AVL 树或红黑树，在删除后做旋转调整
- **迭代版怎么写？** 需要维护 parent 指针和当前节点，找到目标后分情况处理 parent 的子节点引用。代码量比递归大很多，但空间 O(1)

## 关联题

- 同套路：[701. 二叉搜索树中的插入操作](701-insert-into-a-binary-search-tree.md) —— 插入比删除简单（总是插入到叶子位置）
- 进阶：[669. 修剪二叉搜索树](669-trim-a-binary-search-tree.md) —— 批量删除区间外的节点
- 知识点：BST 的增删改查操作体系见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)