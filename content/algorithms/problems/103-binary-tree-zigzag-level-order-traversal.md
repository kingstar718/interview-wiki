---
topics:
  - 二叉树
techniques:
  - BFS层序
---

# 103. 二叉树的锯齿形层序遍历（Binary Tree Zigzag Level Order Traversal）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯/美团

## 题目

给定二叉树，按"锯齿形"顺序返回节点值：第一层左→右，第二层右→左，第三层左→右……交替进行。

**示例**：
```
输入: [3,9,20,null,null,15,7]
    3
   / \
  9  20
    /  \
   15   7
输出: [[3],[20,9],[15,7]]
```

## 思路

在标准层序 BFS 模板基础上加一个方向标志 `leftToRight`。每层收集完节点值后，如果当前方向是右→左，就把该层列表反转再加入结果（或用双端队列头插避免显式反转）。

## 代码

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode(int val) { this.val = val; }
}

public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;

    Queue<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    boolean leftToRight = true;

    while (!q.isEmpty()) {
        int size = q.size();
        List<Integer> level = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            level.add(node.val);
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        if (!leftToRight) {
            Collections.reverse(level);
        }
        res.add(level);
        leftToRight = !leftToRight;
    }
    return res;
}
```

不使用 `Collections.reverse` 的优化版本（用 `LinkedList` 头插）：

```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    boolean leftToRight = true;
    while (!q.isEmpty()) {
        int size = q.size();
        LinkedList<Integer> level = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (leftToRight) {
                level.addLast(node.val);
            } else {
                level.addFirst(node.val);
            }
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        res.add(level);
        leftToRight = !leftToRight;
    }
    return res;
}
```

## 复杂度

- **时间**：O(n) — 每个节点访问一次，反转操作每层最多 O(n) 总 O(n)
- **空间**：O(n) — 队列最大存一层的节点数

## 边界条件

- 空树：直接返回空列表。
- 单节点：只有一个节点，`leftToRight` 为 true，直接返回 `[[val]]`。
- 满二叉树：每层满节点，锯齿形交替方向正常工作。

## 变式

- 普通层序遍历：见 [102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)（没有翻转逻辑）。
- 自底向上层序遍历：结果列表 `Collections.reverse(res)` 即可。
- 二叉树的右视图：[199. 二叉树的右视图](199-binary-tree-right-side-view.md)（每层只取最后一个节点）。

## 易错点

- 方向标志在**每层处理后**翻转，不是每处理一个节点就翻转一次。
- `Collections.reverse(level)` 会修改原列表，不能用于需要保留原顺序的场景；用双端队列头插法可以避免显式反转，但要注意 `LinkedList` 在大量数据下性能不如 `ArrayList`。
- BFS 模板中 `q.size()` 必须在循环前记录，否则子节点入队会使队列大小持续变化。

## 面试追问

- **每层用一个 LinkedList 头插代替 Collections.reverse 的优缺点？** 优点：省去一次 O(k) 的显式反转；缺点：LinkedList 的节点内存开销大（每个元素一个 Node 对象），且头插在数据量大时有更好的理论常数。面试中两种写法都可以，提到 tradeoff 更好。
- **能不能用 DFS（递归）实现锯齿形遍历？** 可以：按 depth 在结果中索引对应层，根据 depth 奇偶性决定在当前层的 List 头插还是尾插。但 DFS 不保证从左到右的访问顺序时，需要在遍历时先左后右严格控制。
- **如果要求不用额外数据结构（队列），只用递归怎么实现？** 按层递归 + depth 参数，用 `res.get(depth).add(val)` 或头插，但需要预先知道树的高度或使用动态扩容的 List。

## 关联题

- 同套路：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)（去掉翻转就是本题）、[199. 二叉树的右视图](199-binary-tree-right-side-view.md)
- 进阶：[107. 二叉树的层序遍历 II](https://leetcode.cn/problems/binary-tree-level-order-traversal-ii/)（自底向上）
- 知识点：BFS 层序模板 + 方向标志位的控制，是很多"交替方向"类问题的通用思路
