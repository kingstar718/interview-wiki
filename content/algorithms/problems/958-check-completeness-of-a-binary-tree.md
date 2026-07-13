---
topics:
  - 二叉树
techniques:
  - BFS层序
---

# 958. 二叉树的完全性检验（Check Completeness of a Binary Tree）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

判断一棵二叉树是否是完全二叉树。完全二叉树：除了最后一层外，每一层都被填满，且最后一层的节点都靠左排列。

**示例**：
```
输入:
    1
   / \
  2   3
 / \  /
4  5 6
输出: true

输入:
    1
   / \
  2   3
 / \   \
4  5   7
输出: false  （第三层最右侧节点有左子节点，但最左侧缺少右子节点）
```

## 思路

**BFS 层序 + 空节点标记**：正常层序遍历，遇到 `null` 节点后标记「空节点出现」。如果之后再遇到非空节点，说明不是完全二叉树。

关键洞察：完全二叉树在层序序列中，所有非空节点之后才能出现空节点，一旦出现空节点就不能再出现非空节点。

## 代码

```java
public boolean isCompleteTree(TreeNode root) {
    Queue<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    boolean seenNull = false;
    while (!q.isEmpty()) {
        TreeNode node = q.poll();
        if (node == null) {
            seenNull = true;
        } else {
            if (seenNull) return false;   // 空节点之后又出现非空节点
            q.offer(node.left);
            q.offer(node.right);
        }
    }
    return true;
}
```

## 复杂度

- **时间**：O(n) —— 每个节点入队出队一次
- **空间**：O(n) —— 队列最大宽度

## 边界条件

- 空树：根据定义空树是完全二叉树（通常返回 true）
- 单节点：层序序列 [node, null, null]，遇到 node 后入队 left(null) 和 right(null)，看到第一个 null 标记 seenNull，后面再 poll 到的 null 不触发 false，返回 true
- 满二叉树：所有节点都有两个子节点，层序中不会出现 null 夹在非空节点之间的情况

## 变式

- **[919. 完全二叉树插入器](919-complete-binary-tree-inserter.md)**：用队列记录可插入位置，在完全二叉树中插入新节点
- **[222. 完全二叉树的节点个数](https://leetcode.cn/problems/count-complete-tree-nodes/)**：利用完全二叉树性质 O(log²n) 计算节点总数
- **判断满二叉树**：要求所有节点要么是叶子要么有两个子节点，条件不同

## 易错点

- **必须把空节点也入队**：否则无法区分「左子为空但右子非空」的非法情况（如节点只有右子没有左子）
- `seenNull` 在遇到 null 后设为 true，且之后不再恢复——因为完全二叉树要求所有空节点集中在末尾
- ArrayDeque 允许 null 入队（Java 的 ArrayDeque 不允许 null，但 LinkedList 允许；上面代码用 ArrayDeque 需要改为 LinkedList 或做 null 检查）。实际代码中常用 `LinkedList<>()` 实现可包含 null 的队列

## 面试追问

- **为什么层序中看到 null 后再看到非空节点就说明不是完全二叉树？** 完全二叉树要求节点靠左排列，所有空位只能出现在最后。层序遍历把空位也展开到序列中，一旦某个位置为空，其右侧的所有位置（在序列中表现为后续节点）也必须为空。这是完全二叉树的充要条件
- **如果不用 BFS，有没有递归方式？** 可以：DFS 检查每个节点的索引是否符合完全二叉树的性质——对根节点索引 1，左子 2i，右子 2i+1，如果某个节点的索引 > 节点总数，则不是完全二叉树。需要先算节点总数（O(n)），再 DFS 验证（O(n)）
- **如果二叉树节点可能有 null 值节点（占位节点而不是 null 指针）？** 这种情况需要转换思路：占位节点本身就是一个值，和 "没有子节点" 的 null 指针不同，此时用 BFS 空标记法仍然正确——占位节点作为普通节点处理即可

## 关联题

- 基础：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md) —— BFS 层序模板
- 进阶：[919. 完全二叉树插入器](919-complete-binary-tree-inserter.md) —— 利用完全二叉树性质的插入操作
- 同套路：[199. 二叉树的右视图](199-binary-tree-right-side-view.md) —— 层序变种
- 知识点：完全二叉树在堆排序和数组表示中的广泛应用见[二叉树](二叉树.md)
