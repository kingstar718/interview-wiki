---
topics:
  - 二叉树
techniques:
  - 递归返回值设计
---

# 124. 二叉树中的最大路径和（Binary Tree Maximum Path Sum）

频次 ★★★★ · 难度 🔴 · 高频：阿里/字节

## 题目

二叉树中每个节点有权值（可负），找**任意两个节点间路径的最大和**（路径至少含一个节点，不一定过根）。

**示例**：
```
输入: [-10,9,20,null,null,15,7]
输出: 42   （15 + 20 + 7 = 42）
```

## 思路

**后序遍历 + 分治**（类似 543 直径的权值版本）：

每个节点计算两种值：
1. **以该节点为路径端点**的"单边最大贡献" = `node.val + max(0, leftGain, rightGain)`
2. **经过该节点**的路径和 = `node.val + max(0, leftGain) + max(0, rightGain)`，用全局变量取最大

与直径的差别：直径关心边数，本题关心权值和；且负值子树应被截断（取 `max(0, gain)`）。

## 代码

```java
private int maxSum = Integer.MIN_VALUE;

public int maxPathSum(TreeNode root) {
    maxGain(root);
    return maxSum;
}

private int maxGain(TreeNode node) {
    if (node == null) return 0;

    int left = Math.max(0, maxGain(node.left));   // 负贡献直接丢弃
    int right = Math.max(0, maxGain(node.right));

    // 经过当前节点的路径和（左→当前→右）
    int throughNode = node.val + left + right;
    maxSum = Math.max(maxSum, throughNode);

    // 返回以当前节点为端点的最大贡献（供父节点使用）
    return node.val + Math.max(left, right);
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(height)

## 边界条件

- 全负数节点：取最大值（最小的负数），`maxSum` 初始值必须设 `Integer.MIN_VALUE`
- 单节点：`maxGain` 返回节点值，`maxSum` 更新为该值
- 根在路径中：整条路径从最左叶到最右叶经过根

## 变式

- **[543. 二叉树的直径](543-diameter-of-binary-tree.md)**：边数版本，没有负数截断
- **[687. 最长同值路径](https://leetcode.cn/problems/longest-univalue-path/)**：限制路径上值必须相同
- **N 叉树版**：取子节点中最大/次大贡献的类似框架

## 易错点

- **`maxSum` 初始值必须用 `Integer.MIN_VALUE`**，不能为 0（全负数场景会返回 0）
- 递归返回值是"单边最大贡献"（只能选左或右），不是"经过当前节点的路径和"——两者容易混淆
- 负子树用 `max(0, gain)` 截断，也意味着允许路径不从叶子开始（从任意正节点开始）
- 与 543 直径的区分：543 用深度计数（边数），本题用值求和

## 面试追问

- **如果路径必须从根到叶子？** 那是 112/113 路径总和，用 DFS + 回溯，不需要考虑负值截断
- **为什么能取到全局最大？** 后序遍历保证每个节点都作为"最高点"被计算一次——任何路径都有唯一的最高节点（最近公共祖先），所以全局枚举不漏

## 关联题

- 同套路：[543. 二叉树的直径](543-diameter-of-binary-tree.md) —— 同结构（后序 + 全局变量），路径长度改路径和
- 进阶：[105. 从前序与中序遍历构造二叉树](105-construct-binary-tree-from-preorder-and-inorder-traversal.md) —— 需要另一维度的树形分治
- 知识点：树形 DP 框架见[二叉树](二叉树.md)

