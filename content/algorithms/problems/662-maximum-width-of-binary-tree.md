---
topics:
  - 二叉树
techniques:
  - BFS层序
---

# 662. 二叉树最大宽度（Maximum Width of Binary Tree）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

给定二叉树，求最大宽度。宽度定义为：**每一层的最左和最右非空节点之间的长度**（中间空节点也计入长度）。从根节点编号为 1，左子 `2 * i`，右子 `2 * i + 1`。

**示例**：
```
输入:
        1
       / \
      3   2
     /     \
    5       9
   /         \
  6           7
输出: 8  （最下层左端 6 编号 13，右端 7 编号 20，宽度 20-13+1=8）
```

## 思路

**BFS + 下标编号**：层序遍历时，每个节点用其在完全二叉树中的编号标记。每层第一个和最后一个节点的编号差 +1 即为该层宽度。根编号为 0（或 1），左子 `2 * idx`，右子 `2 * idx + 1`。

为防止编号溢出（树深可达 3000），可对每层第一个节点的编号做归一化——每层都减去 `minIdx`（该层最小编号）。

## 代码

```java
public int widthOfBinaryTree(TreeNode root) {
    if (root == null) return 0;
    Queue<Pair<TreeNode, Integer>> q = new ArrayDeque<>();
    q.offer(new Pair<>(root, 0));
    int max = 0;
    while (!q.isEmpty()) {
        int size = q.size();
        int first = q.peek().getValue();   // 该层最左编号
        int last = first;
        for (int i = 0; i < size; i++) {
            Pair<TreeNode, Integer> p = q.poll();
            TreeNode node = p.getKey();
            int idx = p.getValue();
            last = idx;
            if (node.left != null)
                q.offer(new Pair<>(node.left, (idx - first) * 2));
            if (node.right != null)
                q.offer(new Pair<>(node.right, (idx - first) * 2 + 1));
        }
        max = Math.max(max, last - first + 1);
    }
    return max;
}
```

## 复杂度

- **时间**：O(n) — 每个节点入队出队一次
- **空间**：O(n) — 队列存储最宽层的节点

## 边界条件

- 空树：返回 0
- 单节点：一层，宽度 1
- 满二叉树：最下层宽度最大
- 斜树：每层一个节点，宽度恒为 1

## 变式

- **[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)** —— BFS 模板，本题的基础
- **[958. 二叉树的完全性检验](958-check-completeness-of-a-binary-tree.md)** —— 也是用下标编号，判断节点下标是否连续
- **[543. 二叉树的直径](543-diameter-of-binary-tree.md)** —— 宽度 vs 直径：宽度是层维度的跨度，直径是任意两节点的最远距离

## 易错点

- **编号可能溢出**：树深度 3000 时 `2³⁰⁰⁰` 远超 long。解决方法是每层归一化——子节点编号减去当前层第一个节点的编号。
- 队列存储 `Pair<TreeNode, Integer>`，如果 LeetCode 环境没有 `Pair`，可以用 `Object[]` 或自定义类。
- 宽度是 `last - first + 1`，不是 `last - first`（节点数 = 两端编号差 + 1）。
- 空节点不计入层序遍历，但宽度计算时"中间的空节点"算长度——这正是编号方案的优势：空节点虽然不入队，但编号差已经隐含了它们的位置。

## 面试追问

- **为什么编号会溢出？** 指数增长：第 k 层第一个节点编号约为 `2^(k-1)`，树深度 100 时已远超 `long` 范围。归一化通过减去当前层最小编号，确保子节点编号相对值始终在本层宽度范围内，不会溢出。
- **DFS 能做吗？** 能——用哈希表 `Map<depth, firstIdx>` 记录每层第一个节点的编号，DFS 时更新 `max = max(depth, idx - firstIdx + 1)`。DFS 优势是不需要额外队列，但需要全局 map。
- **完全二叉树的宽度和最大节点数的关系？** 完全二叉树第 k 层最多 `2^(k-1)` 个节点，但本题宽度计算包含中间空位，所以可能大于该层实际节点数。

## 关联题

- 同套路：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md) —— BFS 层序模板
- 进阶：[958. 二叉树的完全性检验](958-check-completeness-of-a-binary-tree.md) —— 同类下标编号的应用
- 知识点：BFS 层序框架见[二叉树](二叉树.md)
