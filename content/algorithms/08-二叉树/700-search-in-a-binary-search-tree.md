# 700. 二叉搜索树中的搜索（Search in a Binary Search Tree）

频次 ★★ · 难度 🟢 · 高频：全厂

## 题目

在 BST 中搜索值为 val 的节点，返回以该节点为根的子树。如果不存在，返回 null。

**示例**：
```
输入: root = [4,2,7,1,3], val = 2
输出: 子树 [2,1,3]
```

## 思路

**利用 BST 性质二分搜索**：val < root.val 走左子树，val > root.val 走右子树，val == root.val 返回当前节点。

递归和迭代都能写，推荐在面试中展示迭代版（更高效，无递归开销）。

## 代码

```java
// 迭代版（推荐）
public TreeNode searchBST(TreeNode root, int val) {
    while (root != null) {
        if (root.val == val) return root;
        root = val < root.val ? root.left : root.right;
    }
    return null;
}
```

```java
// 递归版
public TreeNode searchBST(TreeNode root, int val) {
    if (root == null || root.val == val) return root;
    return val < root.val
        ? searchBST(root.left, val)
        : searchBST(root.right, val);
}
```

## 复杂度

- **时间**：O(h) —— 最坏 O(n)（退化为链表），平均 O(log n)
- **空间**：迭代 O(1)，递归 O(h)

## 边界条件

- 空树：返回 null
- val 不存在于树中：走到 null 返回 null
- val 等于根节点：直接返回根
- val 在叶子节点：正常搜索到叶子返回

## 变式

- **搜索并返回父节点**：迭代时维护 parent 指针
- **搜索并返回路径**：用列表记录搜索路径
- **在普通二叉树中搜索**：只能用 DFS/BFS 全遍历，无 BST 性质可用

## 易错点

- 不要忘记利用 BST 性质——用全遍历搜索 O(n) 虽然能过，但面试官会问"BST 的性质体现在哪里"
- 迭代版循环条件 `while (root != null)`，不能写成 `while (root != null && root.val != val)` 然后在循环外 return root——这样退出循环时无法区分"找到"和"到 null"

## 面试追问

- **BST 搜索和二分查找本质相同吗？** 相同——都利用有序性每次排除一半。但 BST 的实现方式是树形指针，二分查找是数组索引。当 BST 平衡时，搜索性能等价于二分查找的 O(log n)；退化为链表时退化为 O(n)
- **迭代 vs 递归哪个更好？** 迭代空间 O(1)，递归 O(h)，迭代更优。但递归更简洁。面试中两种都写出来说明对 BST 的深刻理解

## 关联题

- 同套路：[98. 验证二叉搜索树](98-validate-binary-search-tree.md) —— 同样利用 BST 的区间性质
- 进阶：[701. 二叉搜索树中的插入操作](701-insert-into-a-binary-search-tree.md) —— 搜索到空位置后插入
- 知识点：BST 的搜索性质见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)