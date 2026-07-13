# 222. 完全二叉树的节点个数（Count Complete Tree Nodes）

频次 ★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

统计完全二叉树的节点个数。完全二叉树：除最后一层外每层都填满，最后一层节点从左到右连续排列。

**示例**：
```
输入:      1
         /   \
        2     3
       / \   /
      4   5 6
输出: 6
```

## 思路

**两级解法，面试先讲基础再讲优化**：

**基础解法（O(n)）**：任意遍历方式（DFS/BFS）数节点，适用于所有二叉树。

**优化解法（O((log n)²)）**：利用完全二叉树的性质——分别计算左子树的最左路径深度和右子树的最左路径深度：
- 若左右深度相等 → 左子树是满二叉树，节点数 = 2^leftH - 1 + 1（根） + 递归右子树
- 若左右深度不等 → 右子树是满二叉树（比左子树少一层），节点数 = 2^rightH - 1 + 1（根） + 递归左子树

满二叉树节点数 = 2^h - 1（h 为高度），用位运算 `1 << h` 计算。

## 代码

```java
// 优化解法 O((log n)^2)
public int countNodes(TreeNode root) {
    if (root == null) return 0;
    int leftH = getLeftHeight(root.left);
    int rightH = getLeftHeight(root.right);
    if (leftH == rightH) {
        // 左子树是满二叉树，层数为 leftH
        return (1 << leftH) + countNodes(root.right);
    } else {
        // 右子树是满二叉树，层数为 rightH
        return (1 << rightH) + countNodes(root.left);
    }
}

// 沿最左路径走到底，得到高度
private int getLeftHeight(TreeNode node) {
    int h = 0;
    while (node != null) {
        h++;
        node = node.left;
    }
    return h;
}
```

```java
// 基础解法 O(n) —— 面试先展示这个思路
public int countNodes(TreeNode root) {
    if (root == null) return 0;
    return 1 + countNodes(root.left) + countNodes(root.right);
}
```

## 复杂度

- **优化解法**：时间 O((log n)²) —— 每次递归计算一次高度 O(log n)，递归深度 O(log n)；空间 O(log n)
- **基础解法**：时间 O(n)，空间 O(log n)

## 边界条件

- 空树：返回 0
- 单节点：返回 1
- 满二叉树：左右深度始终相等，每次都是左子树满，最终 O(log n) 次位移运算完成
- 完全二叉树最后一层只有一个节点：极端情况，但优化解法仍然只需 O((log n)²)

## 变式

- **普通二叉树节点数**：直接用基础 O(n) 解法
- **判断是否是完全二叉树**：BFS 层序遍历，遇到第一个 null 后不能再有非 null 节点
- **完全二叉树插入器（919）**：用队列维护插入位置

## 易错点

- `1 << h` 是 2^h，不是 2^h - 1。满二叉树节点数需要减 1，但这里 `(1 << leftH)` 已经包含了根节点（因为递归返回的是"整个左子树 + 根 + 右子树递归"），不需要手动加根
- 计算高度时只沿最左路径走，不能用右路径——因为完全二叉树的最后一层从左到右填充，右路径可能为空
- 基础解法直接写 `1 + countNodes(left) + countNodes(right)` 即可，不要用 BFS 队列多此一举

## 面试追问

- **为什么是 O((log n)²)？** 每次递归调用 `getLeftHeight` 走 O(log n) 步，递归深度也是 O(log n)（因为每次排除一半节点），所以总体 O((log n)²)。追问"能不能 O(log n)？"——二分查找最后一层的分界点，但需要知道某个位置的节点是否存在，实现复杂
- **完全二叉树 vs 满二叉树 vs 完美二叉树的区别？** 完美二叉树（Perfect）：每层都填满，节点数 = 2^h - 1。满二叉树（Full）：每个节点有 0 或 2 个子节点。完全二叉树（Complete）：除最后一层外填满，最后一层从左到右连续

## 关联题

- 同套路：[104. 二叉树的最大深度](104-maximum-depth-of-binary-tree.md) —— 高度的计算是本题的基础
- 进阶：[110. 平衡二叉树](110-balanced-binary-tree.md) —— 同样利用高度信息做剪枝优化
- 知识点：完全二叉树的结构性质见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)