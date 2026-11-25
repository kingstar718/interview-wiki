---
topics:
  - 二叉树
techniques:
  - BST中序
---

# 230. 二叉搜索树中第 K 小的元素（Kth Smallest Element in a BST）

频次 ★★★ · 难度 🟡 · 高频：阿里

## 题目

给定 BST 和整数 k，返回第 k 小的元素（1-indexed）。

**示例**：
```
输入: root = [3,1,4,null,2], k = 1
输出: 1
```

## 思路

**中序遍历到第 k 个**：BST 的中序遍历结果是升序序列，遍历时计数，到 k 即返回。

## 代码

```java
private int count = 0, result = 0;

public int kthSmallest(TreeNode root, int k) {
    inorder(root, k);
    return result;
}

private void inorder(TreeNode node, int k) {
    if (node == null) return;
    inorder(node.left, k);
    count++;
    if (count == k) {
        result = node.val;
        return;
    }
    inorder(node.right, k);
}
```

## 三形态全家福与"第 k 个退出"的双卫兵（二叉树组 3/6）

```
BST 中序=升序（性质不改变遍历代码，只让"第 k 个被访问=第 k 小"成立）
用户三形态全亲手写：List 收集版 → 双 int 计数器递归版 → 迭代栈版（主入口）
用户进化实录：v1 只在入口设卫兵——左子树装满名额后回到根会【无条件 add】（res 可超 k，
  答案对是 get(k-1) 的巧合不是逻辑对）→ v2 在 add 前补中卫兵（访问时刻判），结构严密
递归版退出天然要在【两个时刻】设卡：入口（拦新调用）+ 访问前（拦回溯路上的 add）；
  迭代版只要一个（弹栈访问是唯一时刻）——这是"递归退出比迭代难写"的根因
正数到 k（cnt++ 判 ==k）vs 倒数到 0（--cnt 判 ==0）等价；正数版更贴直觉，倒数版少带 k 参数
迭代版弹栈 --k==0 直接 return：栈内剩余欠账直接丢 → O(h+k)；
  递归版名额满后回溯残余 O(n) 最坏——"直接 return 丢弃欠账"是迭代版独有的收益
末尾 return 0 = 约束保证不可达的语法兜底（k∈[1,节点数]）
BST wiki 已补：中序两版代码（递归/迭代栈）+ 访问时刻对照（二叉树.md「BST：中序即升序」节）
```

## 复杂度

- **时间**：O(n) 最坏（k = n 时需要遍历全部），平均 O(k)
- **空间**：O(height)

## 边界条件

- k = 1：最小元素（最左叶子）
- k = 节点总数：最大元素（最右叶子）
- BST 为空：不会出现（k 一定有效）

## 变式

- **第 K 大**：右 → 根 → 左的逆中序遍历，或转化为求第 `(n - k + 1)` 小（知道节点总数时）
- **[285. BST 中序后继](https://leetcode.cn/problems/inorder-successor-in-bst/)**：找 p 节点在中序序列中的下一个

## 易错点

- 递归找到第 k 个后要**提前返回**，否则 count 会继续增加。这里用全局变量 + return 后递归继续但没有副作用；更严谨的做法是递归函数返回 boolean 并短路
- 不要和[215. 数组中的第K大](215-kth-largest-element-in-an-array.md)搞混——数组第 K 大是 Partition/堆，BST 第 K 小是中序遍历

## 面试追问

- **如果 BST 频繁被修改（插入/删除）且高频查第 k 小，怎么优化？** 每个节点加 leftCount 字段，二分查找。插入/删除时 O(log n) 更新 leftCount，查询 O(log n)——答出"树状数组/平衡树"思路即算过关
- **为什么不用堆？** 因为 BST 本身就有顺序，堆需要全量入堆再取 k 次，浪费了 BST 的天然有序性

## 关联题

- 同套路：[98. 验证二叉搜索树](98-validate-binary-search-tree.md) —— 中序遍历的升序性质
- 进阶：[236. 二叉树的最近公共祖先](236-lowest-common-ancestor-of-a-binary-tree.md) —— BST 顺序性质在另一类问题的应用
- 知识点：BST 中序遍历模板见[二叉树](../二叉树.md)；中序有序是这条模板的根据，见树

