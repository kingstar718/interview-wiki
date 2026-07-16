---
topics:
  - 二叉树
techniques:
  - BST中序
---

# 701. 二叉搜索树中的插入操作（Insert into a Binary Search Tree）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

向 BST 中插入值为 val 的新节点，插入后保持 BST 性质。假设新值在原树中不存在。返回插入后的根节点。

**示例**：
```
输入: root = [4,2,7,1,3], val = 5
输出: [4,2,7,1,3,5]
```

## 思路

**利用 BST 性质搜索到空位置插入**：从根开始，val < root.val 走左，val > root.val 走右，走到空位置时创建新节点，返回给父节点连接。

关键：递归的返回值是"插入后的子树根"，父节点通过 `root.left = insertIntoBST(root.left, val)` 来接住新节点。

## 代码

```java
// 递归版
public TreeNode insertIntoBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    if (val < root.val) {
        root.left = insertIntoBST(root.left, val);
    } else {
        root.right = insertIntoBST(root.right, val);
    }
    return root;
}
```

```java
// 迭代版
public TreeNode insertIntoBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    TreeNode cur = root;
    while (true) {
        if (val < cur.val) {
            if (cur.left == null) {
                cur.left = new TreeNode(val);
                break;
            }
            cur = cur.left;
        } else {
            if (cur.right == null) {
                cur.right = new TreeNode(val);
                break;
            }
            cur = cur.right;
        }
    }
    return root;
}
```

## 复杂度

- **时间**：O(h) —— 最坏 O(n)，平均 O(log n)
- **空间**：递归 O(h)，迭代 O(1)

## 边界条件

- 空树：直接返回新建节点
- val 比所有节点都小/大：插入到最左/最右叶子
- 新值在树中已存在：题目保证不会发生。如果可能重复，需要在插入时判断（通常 BST 不允许重复值）

## 变式

- **插入并返回插入节点的父节点**：迭代时维护 parent 指针
- **插入多个值**：依次插入，但注意插入顺序会影响树的形状
- **插入后自动平衡**：AVL 树插入——需要在插入后检测平衡因子并旋转

## 易错点

- 递归版必须用 `root.left = insertIntoBST(root.left, val)` 接住返回值，不能只调用不赋值——否则新节点创建了但没接上树
- 迭代版要先检查空树（`if (root == null) return new TreeNode(val)`），否则无法进入循环
- 迭代版循环中 `break` 后要返回 `root`（存原始根），而不是 `cur`

## 面试追问

- **插入操作有没有可能打破 BST 平衡？** 会——如果按升序或降序插入，BST 会退化为链表。工程中通常用 AVL 树或红黑树来保证自平衡
- **迭代版和递归版哪个更好？** 迭代空间 O(1)，递归空间 O(h)，迭代更优。但递归代码简洁，面试中两种都展示更好

## 关联题

- 同套路：[450. 删除二叉搜索树中的节点](450-delete-node-in-a-bst.md) —— 删除比插入复杂，需要处理三种情况
- 进阶：[700. 二叉搜索树中的搜索](700-search-in-a-binary-search-tree.md) —— 搜索是插入的前置操作
- 知识点：BST 增删改查模板见[二叉树](二叉树.md)
