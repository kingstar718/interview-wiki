---
topics:
  - 二叉树
techniques:
  - BFS层序
---

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

- **时间**：构造 O(n)（一次层序遍历）；`insert` **均摊 O(1)**；`get_root` O(1)
- **空间**：队列里只存「有空位的候选节点」，最多 O(宽度) = O(n)

`insert` 之所以是 O(1) 而不是 O(log n)：队头永远是**下一个该被填充的节点**，不需要任何查找。

## 边界条件

- **初始树只有根节点**：构造时队列只放 root，root 的左右都为空
- **队头节点左右都被填满**：必须 `poll()` 出队，让下一个节点成为新队头
- **左空右非空**：完全二叉树里不可能出现，构造函数的 `else break` 依赖这个前提
- **构造函数的 BFS 何时停**：遇到第一个「不满」的节点就停——它和它之后的所有节点都是候选，但只有它现在需要被填

## 变式

- **[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)**：本题构造函数用的就是它
- **完全二叉树的节点个数**（LeetCode 222）：利用完全性，用两次二分做到 O(log²n)，而不是 O(n) 遍历
- **用数组表示完全二叉树**：下标 i 的孩子是 `2i+1`、`2i+2`。插入就是往数组末尾追加，`insert` 天然 O(1)——**这就是堆的存储方式**
- **删除最后一个节点**：需要定位「最后一个」，队列维护不了，得靠下标或二分

## 易错点

- **队列里存的不是「所有节点」，而是「可能还有空位的节点」**。构造时把已经满员的节点 `poll()` 掉，是这个不变量的维护
- **`insert` 后要把新节点入队**：它将来也要接收孩子
- **只有填了右孩子才 `poll()`**：填左孩子后该节点还有右位可用，不能出队
- 构造函数里 `q.peek()` 而非 `q.poll()`——判断完才决定要不要出队
- 别每次 insert 都重新 BFS 找空位，那是 O(n)，本题的全部意义就是把它降到 O(1)

## 面试追问

- **为什么队列的队头永远是下一个该填的节点**：完全二叉树的填充顺序**就是层序顺序**。队列按层序推进，把满员节点弹出后，队头必然是层序中第一个不满的节点。**「完全二叉树」和「BFS 队列」在结构上是同一个顺序**，这是本题的全部洞察。
- **和堆的 `offer` 是什么关系**：堆用数组存完全二叉树，插入就是 `array[size++]`，然后向上调整。**本题是它的指针版本**——因为要求返回 `TreeNode`，不能用数组。如果允许数组，`insert` 会更简单，见[排序与堆](排序与堆.md)。
- **均摊 O(1) 怎么论证**：每个节点入队恰好一次、出队至多一次，n 次插入的总队列操作是 O(n)，见摊还。单次 `insert` 不做任何循环。
- **完全二叉树的性质还能用来做什么**：节点数 n 时高度必为 `⌊log₂n⌋`；可以用数组紧凑存储（无指针开销）；能在 O(log²n) 内数出节点个数。**这些优化全都依赖「没有空洞」这个约束**，一旦允许空洞就全部失效。

## 关联题

- 基础：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)（BFS 模板）

