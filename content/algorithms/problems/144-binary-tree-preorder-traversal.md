---
topics:
  - 二叉树
techniques:
  - 栈模拟递归
---

# 144. 二叉树的前序遍历（Binary Tree Preorder Traversal）

频次 ★★★★ · 难度 🟢 · 高频：腾讯/美团/字节

## 题目

给定二叉树根节点，返回前序遍历的节点值序列（根 → 左 → 右），分别用递归和迭代实现。

**示例**：
```
输入: [1,null,2,3]
    1
     \
      2
     /
    3
输出: [1,2,3]
```

## 思路

**递归**：根 → 左 → 右，最直接。

**迭代（栈）**：用栈模拟递归。根先入栈，每次弹出栈顶访问，然后**先右后左**入栈（栈 LIFO，右后入则左先出，达到根→左→右的顺序）。

## 代码

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}

// 递归版本
public List<Integer> preorderTraversal(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    preorder(root, res);
    return res;
}

private void preorder(TreeNode node, List<Integer> res) {
    if (node == null) return;
    res.add(node.val);
    preorder(node.left, res);
    preorder(node.right, res);
}
```

```java
// 迭代版本（栈）
public List<Integer> preorderTraversal(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        res.add(node.val);
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    return res;
}
```

## 复杂度

- **时间**：O(n) — 每个节点访问一次
- **空间**：递归 O(n)（最坏单支树）；迭代 O(n)（栈最多存 n 个节点）

## 边界条件

- 空树：返回空列表
- 单节点：直接输出该节点
- 左/右单支树：迭代时入栈顺序保证访问顺序正确

## 变式

- **[94. 二叉树的中序遍历](94-binary-tree-inorder-traversal.md)**（迭代）：栈模拟思路类似，但先一路入栈左子节点，弹出时访问，再处理右子树
- **[145. 二叉树的后序遍历](https://leetcode.cn/problems/binary-tree-postorder-traversal/)**（迭代）：前序变体（根 → 右 → 左）反转结果，或双栈法
- **Morris 前序遍历**：O(1) 空间前序遍历，利用线索二叉树

## 易错点

- **迭代时必须先右后左入栈**：因为栈是后进先出，`左子树` 要先访问就必须最后入栈。写成先左后右就变成了"根→右→左"。
- 递归版本注意 `res` 作为参数传递，不要每次递归都创建新列表。
- `ArrayDeque` 优于 `Stack`：`Stack` 是同步遗留类，性能差。

## 面试追问

- **前序遍历迭代实现有什么变体？** 除了"根先入栈→弹出→右左入栈"之外，还有一种更接近中序遍历模板的写法：`while (cur != null || !stack.isEmpty()) { while (cur != null) { visit cur; stack.push(cur); cur = cur.left; } cur = stack.pop(); cur = cur.right; }`——区别在于前序在入栈时访问（根→左），中序在出栈时访问（左→根）。
- **三种迭代遍历有没有统一框架？** 有，用"标记法"：每个节点入栈两次，第一次标记为"未处理"，第二次才访问。但这种写法太 hack，面试中分别记忆三种迭代写法的区别即可。

## 关联题

- 同套路：[94. 二叉树的中序遍历](94-binary-tree-inorder-traversal.md) —— 栈模拟递归的兄弟实现
- 进阶：[145. 二叉树的后序遍历](https://leetcode.cn/problems/binary-tree-postorder-traversal/) —— 迭代实现的不同入栈顺序
- 知识点：栈模拟递归的原理见[二叉树](二叉树.md)
