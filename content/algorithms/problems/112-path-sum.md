---
topics:
  - 二叉树
techniques:
  - 递归
---

# 112. 路径总和（Path Sum）

频次 ★★★★ · 难度 🟢 · 高频：字节/腾讯

## 题目

给定二叉树根节点和整数 `targetSum`，判断是否存在**从根到叶子**的路径，路径上节点值之和等于 `targetSum`。

**示例**：
```
输入: root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
输出: true  （5→4→11→2 和为 22）
```

## 思路

**递归减治法**：每向下走一层，从 `targetSum` 减去当前节点值，到达叶节点时判断剩余值是否等于叶节点值。

递归终止条件：空节点返回 false；叶节点判断 `root.val == targetSum`。

## 代码

```java
public boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    // 叶节点：判断是否满足
    if (root.left == null && root.right == null)
        return root.val == targetSum;
    // 递归左右子树，targetSum 减去当前值
    return hasPathSum(root.left, targetSum - root.val)
        || hasPathSum(root.right, targetSum - root.val);
}
```

## 复杂度

- **时间**：O(n) — 每个节点访问一次
- **空间**：O(height) — 最坏 O(n)（单支树），平均 O(log n)

## 边界条件

- 空树：返回 false
- 单节点且值等于 targetSum：返回 true
- 单节点值不等于 targetSum：返回 false
- 路径必须到叶子：如果一个节点有左子/右子但路径和已等于 targetSum，仍不算（还没到叶子）

## 变式

- **[113. 路径总和 II](113-path-sum-ii.md)** —— 不仅要判断是否存在，还要输出所有满足条件的路径
- **[437. 路径总和 III](https://leetcode.cn/problems/path-sum-iii/)** —— 路径不需要从根开始也不需要在叶子结束，前缀和 + 哈希表
- **[129. 求根节点到叶节点数字之和](129-sum-root-to-leaf-numbers.md)** —— 同框架，累加规则从求和改为拼接数字

## 易错点

- **必须到叶子节点**：不能中途提前返回 true（例如非叶节点值已等于 targetSum 但还没到叶子，不算路径）。递归终止条件必须同时满足 `root.left == null && root.right == null`。
- 空节点返回 false，但空树本身也返回 false——不需要单独处理。
- `targetSum - root.val` 可能为负，Java int 不会溢出，但面试中可以提一嘴。

## 面试追问

- **如果路径不需要到叶子，到任意节点即可？** 改动终止条件：去掉 `root.left == null && root.right == null` 的判断，改为检查 `targetSum - root.val == 0` 时即返回 true。
- **如果节点值可能为负呢？** 算法不受影响，因为不能提前剪枝（负值可能让和重新回到 targetSum）。唯一的变化是不能在 `targetSum - root.val < 0` 时提前返回 false。
- **如何记录路径？** 见 [113. 路径总和 II](113-path-sum-ii.md)，回溯法记录当前路径。

## 关联题

- 进阶：[113. 路径总和 II](113-path-sum-ii.md) —— 输出所有满足条件的路径
- 易混：[129. 求根节点到叶节点数字之和](129-sum-root-to-leaf-numbers.md) —— 同样框架，计算规则变为数字拼接
- 知识点：二叉树递归模板见[二叉树](二叉树.md)
