# 538. 把二叉搜索树转换为累加树（Convert BST to Greater Tree）

频次 ★★★ · 难度 🟡 · 高频：阿里/字节

## 题目

将 BST 的每个节点值更新为**原树中所有大于等于该节点值的节点值之和**。即：新值 = 自身值 + 所有比它大的节点值之和。

**示例**：
```
输入:      4            输出:      30
         /   \                  /   \
        1     6               36    21
       / \   / \             / \   /  \
      0   2 5   7          36  35 26  15
            \   \               \   \
             3   8              33   8
```

## 思路

**反序中序遍历（右 → 根 → 左）**：BST 的中序遍历是升序，反序中序遍历就是降序。遍历过程中维护一个累加和 `sum`，每访问一个节点：
- `sum += node.val`（sum 是"所有大于等于当前节点的值之和"）
- `node.val = sum`（更新当前节点为新值）

由于反序中序遍历先访问最大值，`sum` 一直累加，完美满足"当前节点值 + 所有更大节点值"的要求。

## 代码

```java
// 递归版
private int sum = 0;

public TreeNode convertBST(TreeNode root) {
    if (root == null) return null;
    convertBST(root.right);   // 先处理右子树（更大的值）
    sum += root.val;          // 累加当前值
    root.val = sum;           // 更新为累加和
    convertBST(root.left);    // 再处理左子树（更小的值）
    return root;
}
```

```java
// 迭代版（显式栈模拟反序中序）
public TreeNode convertBST(TreeNode root) {
    int sum = 0;
    TreeNode cur = root;
    Deque<TreeNode> stack = new ArrayDeque<>();
    while (cur != null || !stack.isEmpty()) {
        while (cur != null) {
            stack.push(cur);
            cur = cur.right;   // 先走右边
        }
        cur = stack.pop();
        sum += cur.val;
        cur.val = sum;
        cur = cur.left;        // 再走左边
    }
    return root;
}
```

## 复杂度

- **时间**：O(n) —— 每个节点访问一次
- **空间**：递归 O(h)，迭代 O(h)（栈深度）

## 边界条件

- 空树：返回 null
- 单节点：更新为自身值（因为没有更大的节点）
- 最右节点（最大值）：更新为自身值（没有更大的节点，sum 从 0 开始累加）
- 最左节点（最小值）：更新为所有节点值之和

## 变式

- **累加为前缀和（不包含自身）**：先更新 `root.val`，再加 `sum += root.val`——交换顺序即可
- **把 BST 转换为递减树（每个节点值 = 所有大于它的节点数）**：改为 `sum += 1` 计数
- **[1038. 从 BST 到更大和树](https://leetcode.cn/problems/binary-search-tree-to-greater-sum-tree/)**：与本题完全相同

## 易错点

- 遍历顺序必须是**右 → 根 → 左**，不能是常规中序的左 → 根 → 右（那样会变成累加小值）
- 更新顺序：先 `sum += root.val` 再 `root.val = sum`——如果反过来，sum 会丢失当前节点的原值
- 递归版 sum 是全局变量或成员变量，不能作为参数传递（因为参数传递是值传递，不会回溯更新）

## 面试追问

- **为什么用反序中序遍历？** BST 中序遍历是升序，反序就是降序。累加和需要"比当前节点大的所有节点"，降序遍历天然满足——先访问大的，sum 自然就是"所有大于等于当前节点的值之和"
- **迭代版怎么写？** 显式栈模拟中序遍历，走右子树时压栈，弹出时处理，再走左子树。展示对中序遍历迭代模板的掌握

## 关联题

- 同套路：[230. 二叉搜索树中第K小的元素](230-kth-smallest-element-in-a-bst.md) —— 同样的中序遍历框架，但本题是反序
- 进阶：[98. 验证二叉搜索树](98-validate-binary-search-tree.md) —— 中序遍历的升序性质是本题的基础
- 知识点：BST 中序遍历变体（正序/反序）见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)