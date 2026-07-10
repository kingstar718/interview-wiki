---
topics:
  - 二叉树
techniques:
  - 递归
---

# 101. 对称二叉树（Symmetric Tree）

频次 ★★★★ · 难度 🟢 · 高频：字节/腾讯/美团

## 题目

给定一个二叉树，检查它是否是镜像对称的。

**示例**：
```
输入:     1                   输出: true
        / \
       2   2
      / \ / \
     3  4 4  3

输入:     1                   输出: false
        / \
       2   2
        \   \
         3    3
```

## 思路

**递归**：一棵树对称等价于左右子树互为镜像。定义递归函数 `check(p, q)` 判断两棵树是否镜像：
- 两棵空树镜像
- 一棵空一棵非空则不是
- 根值相等且 `p.left` 与 `q.right` 镜像且 `p.right` 与 `q.left` 镜像

也可以用**迭代**：用队列成对入队，每次取两个节点比较并反向入队子节点。

## 代码

```java
public boolean isSymmetric(TreeNode root) {
    return check(root, root);
}

private boolean check(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    return p.val == q.val
        && check(p.left, q.right)
        && check(p.right, q.left);
}
```

## 复杂度

- **时间**：O(n) —— 每个节点访问一次
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：`check(null, null)` 返回 true
- 单节点：左右子树都是 null，对称
- 不对称的形状（如一侧有子树一侧没有）：`check` 第二行拦截

## 变式

- **迭代版**：用双端队列或两个队列同步存入左右子树的待比较节点
- **[100. 相同的树](100-same-tree.md)**：判两棵树是否相同，改一下递归方向就是对称判定
- 判断树是否是自己的镜像：本题即是

## 易错点

- 比较方向容易弄反：是 `p.left` 对 `q.right`，不是 `p.left` 对 `q.left`
- 递归退出条件必须同时检查 `p == null && q == null` 和 `p == null || q == null`，顺序不能颠倒——先判断都为空返回 true，再判断一个为空返回 false
- 不要用 `p.val == q.val` 作为短路条件放在前面——值相等不能跳过递归，值不等才可直接返回 false

## 面试追问

- **迭代版本怎么实现？** 用队列的 `offer`/`poll`，每次先取两个节点比较，然后按 `p.left, q.right, p.right, q.left` 顺序入队。如果队列中连续两个 null 可以跳过，但如果只有一个 null 说明不对称。答出迭代版说明对递归转迭代有掌握
- **和"翻转"的关系？** 翻转二叉树后再与原树比较，等价于判断镜像对称——所以 `invertTree + isSameTree = isSymmetric`

## 关联题

- 同套路：[226. 翻转二叉树](226-invert-binary-tree.md) —— 翻转后自比＝判对称
- 进阶：[110. 平衡二叉树](110-balanced-binary-tree.md) —— 同样用递归自底向上判断树的性质
- 知识点：二叉树递归遍历模板见[二叉树](二叉树.md)
