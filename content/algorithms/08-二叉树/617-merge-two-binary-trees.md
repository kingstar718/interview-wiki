# 617. 合并二叉树（Merge Two Binary Trees）

频次 ★★ · 难度 🟢 · 高频：全厂

## 题目

合并两棵二叉树：对应节点值相加，空节点视为 0。合并从根节点开始。

**示例**：
```
Tree1:     1        Tree2:     2
         /   \               /   \
        3     2             1     3
       /                     \     \
      5                       4     7
输出:      3
         /   \
        4     5
       / \     \
      5   4     7
```

## 思路

**递归同时遍历两棵树**：以其中一棵树为基准（通常是 t1），在 t1 上原地修改：

- 若 t1 为空，返回 t2（t2 整个子树直接接上去）
- 若 t2 为空，返回 t1（t1 保留不动）
- 若都不为空，节点值相加，递归合并左右子树

也可以新建节点，不修改原树。

## 代码

```java
public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
    if (root1 == null) return root2;
    if (root2 == null) return root1;
    // 原地修改 root1
    root1.val += root2.val;
    root1.left = mergeTrees(root1.left, root2.left);
    root1.right = mergeTrees(root1.right, root2.right);
    return root1;
}
```

## 复杂度

- **时间**：O(min(n, m)) —— 只遍历两棵树重叠的部分
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 两树都为空：返回 null
- 一棵为空：返回另一棵（整个子树嫁接）
- 结构不对称：t1 有左子树但 t2 没有，t1 左子树保留；t2 有右子树但 t1 没有，t2 右子树接过来
- 重叠节点值很大：int 可能溢出，但实际题目不会出

## 变式

- **新建节点不修改原树**：`new TreeNode(root1.val + root2.val)`，分别递归左右
- **合并 N 棵二叉树**：依次两两合并
- **对称合并（只合并结构相同的节点）**：不嫁接，结构不对称的节点直接丢弃

## 易错点

- `root1 == null` 时返回 `root2` 而不是 `null`——t2 的整个子树直接嫁接过来，不能丢失
- 递归返回值要正确赋值给 `root1.left` 和 `root1.right`——不能只调用不赋值
- 如果要求不修改原树，注意 `new TreeNode(val)` 创建新节点，不要直接修改 t1 或 t2

## 面试追问

- **如果要求不修改原树怎么写？** 每次 `new TreeNode(root1.val + root2.val)`，然后递归设置左右。空间复杂度升到 O(重叠节点数)
- **迭代版怎么写？** 用栈同时压入 (t1节点, t2节点) 对，每次弹出合并，注意处理子节点为空的情况

## 关联题

- 同套路：[226. 翻转二叉树](226-invert-binary-tree.md) —— 同样是对树的结构变换操作，递归模板一致
- 进阶：[101. 对称二叉树](101-symmetric-tree.md) —— 同时遍历两棵树的递归模式，但比较逻辑不同
- 知识点：同时遍历两棵树的递归模板见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)