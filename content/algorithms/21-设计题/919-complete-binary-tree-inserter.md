# 919. 完全二叉树插入器（Complete Binary Tree Inserter）

频次 ★★ · 难度 🟡 · 高频：百度

## 题目

设计类 CBTInserter，支持插入节点保持完全二叉树性质，以及获取根节点。

## 思路

**BFS 队列**：初始化时层序遍历，将左右子树不完整的节点入队。插入时操作队头节点。

## 代码

```java
class CBTInserter {
    private TreeNode root;
    private Queue<TreeNode> q = new ArrayDeque<>();

    public CBTInserter(TreeNode root) {
        this.root = root;
        q.offer(root);
        while (true) {
            TreeNode cur = q.peek();
            if (cur.left != null) q.offer(cur.left);
            else break;
            if (cur.right != null) { q.offer(cur.right); q.poll(); }
            else break;
        }
    }

    public int insert(int val) {
        TreeNode cur = q.peek();
        if (cur.left == null) {
            cur.left = new TreeNode(val);
            q.offer(cur.left);
        } else {
            cur.right = new TreeNode(val);
            q.offer(cur.right);
            q.poll();
        }
        return cur.val;
    }

    public TreeNode get_root() { return root; }
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- [102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)（BFS 模板）

