---
topics:
  - 二叉树
techniques:
  - 栈模拟递归
---

# 94. 二叉树的中序遍历（Binary Tree Inorder Traversal）

频次 ★★★★ · 难度 🟢 · 高频：字节/腾讯/美团/阿里

## 题目

给定二叉树根节点，返回中序遍历的节点值序列。要求分别用递归和迭代（栈）两种方式实现。

**示例**：
```
输入: [1,null,2,3]
    1
     \
      2
     /
    3
输出: [1,3,2]
```

## 思路

**递归**：左子树 → 根 → 右子树，天然符合中序定义。

**迭代（栈）**：用栈模拟递归调用栈。核心思想——尽可能往左走，把沿途节点入栈；无法再往左走时弹出栈顶节点访问，然后转向右子树继续这个过程。

## 代码

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}

// 递归版本
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    inorder(root, res);
    return res;
}

private void inorder(TreeNode node, List<Integer> res) {
    if (node == null) return;
    inorder(node.left, res);
    res.add(node.val);
    inorder(node.right, res);
}
```

```java
// 迭代版本（栈）
public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    Deque<TreeNode> stack = new ArrayDeque<>();
    TreeNode cur = root;

    while (cur != null || !stack.isEmpty()) {
        // 尽可能往左走
        while (cur != null) {
            stack.push(cur);
            cur = cur.left;
        }
        // 弹出栈顶访问
        cur = stack.pop();
        res.add(cur.val);
        // 转向右子树
        cur = cur.right;
    }
    return res;
}
```

## 复杂度

- **时间**：O(n) — 每个节点恰好访问一次
- **空间**：递归 O(n)（最坏调用栈深度 n，单支树）；迭代 O(n)（栈最多存 n 个节点）

## 边界条件

- 空树：递归直接返回空列表；迭代 `cur == null` 且栈空，循环不执行，返回空列表。
- 单节点：递归访问根后结束；迭代先把根入栈，弹出访问，`cur = cur.right` 为 null，栈空，结束。
- 左/右单支树：迭代时左单支树会先把所有左子节点入栈然后依次弹出；右单支树每轮左走到 null，弹出栈顶（当前节点），然后转向右子节点入栈。

## 变式

- 前序遍历（迭代）：见 [144. 二叉树的前序遍历](https://leetcode.cn/problems/binary-tree-preorder-traversal/)（入栈时先右后左）。
- 后序遍历（迭代）：见 [145. 二叉树的后序遍历](https://leetcode.cn/problems/binary-tree-postorder-traversal/)（前序变体 + 反转 或 双栈法）。
- Morris 中序遍历：O(1) 空间的中序遍历，利用线索化二叉树（叶子节点的空闲指针指向前驱/后继），面试中作为进阶了解即可。

## 易错点

- 迭代版本中 `cur = cur.right` 必须在弹出访问之后，不能放在 `while (cur != null)` 的内部——否则变成一直沿着左子树走完就结束了，永远不会访问右子树。
- `Deque` 推荐用 `ArrayDeque` 而不是 `Stack`（`Stack` 是线程安全的遗留类，性能差）。
- 把节点加入结果列表的位置决定了遍历顺序：中序是在弹出栈顶时加入。

## 面试追问

- **迭代中序遍历的 while 循环（外层 while + 内层 while）能否合并？** 不能完全合并，外层 while 控制"还有节点未处理"，内层 while 控制"往左走到尽头"——这两个逻辑层次不同。不过可以用另一种写法：先一路把左子节点入栈，然后弹出、访问、再处理右子节点，流程更直观。
- **Morris 遍历的原理是什么？** 利用叶子节点空闲的 right 指针指向中序遍历的后继节点，从而不需要栈就能回溯。优点是 O(1) 空间，缺点是会短暂修改树的结构（访问完后恢复）。面试中以知道存在 O(1) 空间方案为佳。
- **递归遍历和迭代遍历在实际生产中的选择？** 树的深度不确定时优先用迭代（避免栈溢出），数据量小或明确树平衡时递归更简洁。题目明确要求用迭代实现时通常考的是栈模拟递归的能力。

## 关联题

- 同套路：[144. 二叉树的前序遍历](https://leetcode.cn/problems/binary-tree-preorder-traversal/)（迭代）、[145. 二叉树的后序遍历](https://leetcode.cn/problems/binary-tree-postorder-traversal/)（迭代）—— 三种迭代遍历的统一框架
- 进阶：[98. 验证二叉搜索树](98-validate-binary-search-tree.md) —— 中序遍历序列是否递增是验证 BST 的经典方法
- 知识点：递归转迭代的本质是"用栈显式保存调用现场"，前/中/后序的压栈时机不同决定了访问顺序
