# 102. 二叉树的层序遍历（Binary Tree Level Order Traversal）

频次 ★★★★★ · 难度 🟡 · 高频：全厂

## 题目

逐层从左到右输出二叉树节点值。

**示例**：
```
输入: [3,9,20,null,null,15,7]
输出: [[3],[9,20],[15,7]]
```

## 思路

**BFS 逐层收集**：用队列存当前层节点，处理前先记录队列大小（即当前层节点数），然后一次性弹出该层所有节点，收集值的同时将子节点入队。

## 代码

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();              // 当前层的节点数
        List<Integer> level = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            level.add(node.val);
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
        res.add(level);
    }
    return res;
}
```

## 复杂度

- **时间**：O(n) —— 每个节点入队出队各一次
- **空间**：O(n) —— 队列最坏存储最宽一层的节点数（完全二叉树最后一层约 n/2）

## 边界条件

- 空树：返回空列表
- 单节点：返回 `[[val]]`
- 左/右单支树：每层一个节点，正常遍历

## 变式

- **[107. 二叉树的层序遍历 II](https://leetcode.cn/problems/binary-tree-level-order-traversal-ii/)**：自底向上，结果列表 `Collections.reverse(res)` 即可
- **[199. 二叉树的右视图](199-binary-tree-right-side-view.md)**：每层只取最后一个节点
- **[637. 二叉树的层平均值](https://leetcode.cn/problems/average-of-levels-in-binary-tree/)**：每层计算平均值
- **锯齿形层序遍历（103）**：加 flag 控制每层是否翻转

## 易错点

- `q.size()` 必须在**循环前**记录，不能在 for 条件里写 `q.size()`——队列大小在弹入子节点过程中不断变化
- `ArrayDeque` 不允许 null，不要 `q.offer(null)`；用 `LinkedList` 可以但性能不如 `ArrayDeque`
- 层序输出的经典应用是序列化、右视图、填充 next 指针等，熟练掌握这题的 for 循环模板

## 面试追问

- **不用队列用 DFS（递归）能做吗？** 能：按层号 depth 在结果中索引对应的 List，`res.get(depth).add(val)`。面试中提一句展示对 DFS/BFS 的切换能力
- **如何判断一层是否结束？** 用 size 快照法；或者用哨兵 null 分隔（入队时在当前层末尾加 null，碰到 null 就是换层标记）

## 关联题

- 同套路：[199. 二叉树的右视图](199-binary-tree-right-side-view.md)、[104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md)（BFS 版本）
- 进阶：[297. 二叉树的序列化与反序列化](297-serialize-and-deserialize-binary-tree.md) —— 层序输出作为序列化格式
- 知识点：BFS 通用模板、队列选型（ArrayDeque vs LinkedList）见[集合框架](集合框架.md)

