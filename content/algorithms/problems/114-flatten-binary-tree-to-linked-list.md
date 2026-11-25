---
topics:
  - 二叉树
techniques:
  - 递归
---

# 114. 二叉树展开为链表（Flatten Binary Tree to Linked List）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

展开二叉树为一条右斜的链表（顺序等同于前序遍历），每个节点的左子指针为 null，右子指针为 next 节点。原地修改（in-place），不创建新节点。

**示例**：
```
输入:
    1
   / \
  2   5
 / \   \
3   4   6

输出: 1 -> 2 -> 3 -> 4 -> 5 -> 6
```

## 思路

**后序遍历（递归）**：先递归展开左右子树，然后把展开后的右子树接到左子树的尾部，再把左子树整体移到右侧。

核心操作：
1. 保存 `right = root.right`
2. 把 `root.right = root.left`，`root.left = null`
3. 找到左子树展开后的最后一个节点
4. 把原右子树接到该节点右侧

## 代码

```java
public void flatten(TreeNode root) {
    if (root == null) return;
    flatten(root.left);
    flatten(root.right);
    // 左右子树都已展开为链表
    TreeNode right = root.right;          // 保存原右子树
    root.right = root.left;               // 左子树移到右侧
    root.left = null;                     // 左指针置空
    // 找到当前右链的末尾
    TreeNode cur = root;
    while (cur.right != null) cur = cur.right;
    cur.right = right;                    // 原右子树接到末尾
}
```

## 帧视角读法 + 双版对照（后序拼接 vs 灵神反前序头插）（二叉树组 5/6）

```
骨架已内置"参考先算"纪律：copy 喂参考、再算原地实现（234 教训固化）
用户卡点两连：①"root 已是最左的 3"——把【全局执行流】当成注释的视角；
  递归注释一律按【帧】读：每帧的 root 是本帧节点，注释"左子树已成链"
  在本帧指"子帧已返回之后"（后序定义），叶子帧退化为空操作
  ② "tail=root 会空指针"——赋值永不 NPE，NPE 只在解引用；
  该段解引用点=tail.right/root.left/right，tail null ⟺ root null ⟺ 卫兵丢失
双版对照：
  用户版（官方标准）：后序三改一找——孩子先各自展开成链，父亲拼接；
    每帧 while 找尾巴（左链尾）；存 l/r 时序=递归后、改指针前（防指针丢失）
  灵神版：反前序（右→左→根）头插——访问序=前序逆序，从链尾往回搭；
    每访问一个节点 O(1) 头插到已建链（root.right=head; head=root），无需找尾巴
  共同点：都原地、都在后序位改指针、都先存后改；第三种=找前驱 O(1) 空间（Morris 风格）
用户实踩坑：dfsRev 里递归误调 flatten（入口）——每帧 head=null 复位，链断；
  纪律：【入口和递归函数分开，递归永不调回入口】（head/maxDia 类实例字段的入口复位只该发生一次）
```

## 复杂度

- **时间**：O(n) —— 每个节点访问一次
- **空间**：O(h) —— 递归调用栈深度 h（最坏 O(n)）

## 边界条件

- 空树：直接返回
- 单节点：无左右子树，递归返回后不做任何改动
- 只有左子树：`right = null`，把左子树移到右侧后，while 循环找到末尾，`cur.right = null` 正确
- 只有右子树：`root.left = null`，`root.right` 已经是原右子树，while 循环到末尾，`cur.right = right`（即 null），不影响

## 变式

- **前序遍历迭代法**：用栈模拟前序，每次弹出节点后将右、左入栈，同时接上 prev 的 right 指针。空间 O(n)
- **Morris 遍历法**：O(1) 空间完成展开，利用前驱节点的空闲右指针建立连接
- **原地展开为双向链表**：如 [426. 将二叉搜索树转化为排序的双向链表](https://leetcode.cn/problems/convert-binary-search-tree-to-sorted-doubly-linked-list/)，中序而非前序

## 易错点

- **先展开左右子树再处理 root**：必须先确保左右子树内部已经拉直，否则接上来的仍然是二叉树结构
- 找到左子树末尾时不能简单用 `root.left`——左子树已经被递归展开成右斜链表了，沿着 right 走到 null 即可
- 记得把 `root.left = null`，否则左指针仍然指向原左子树，破坏了链表结构
- 如果只用前序遍历收集节点再重新连接，虽然简单但不是"原地"（额外用了列表）

## 面试追问

- **为什么用后序而不是前序？** 后序先把左右子树展开，再处理 root，避免了先改 root.right 后丢失右子树的引用。前序也能做但需要额外保存右子树引用，或者用栈模拟
- **怎么做到 O(1) 空间（不递归、不栈）？** Morris 遍历思想：对每个节点，如果左子树不为空，找到左子树的最右节点（前驱），将 root.right 接到前驱的 right，然后 root.right = root.left，root.left = null，然后 root 向右移动。不需要栈也不需要递归
- **如果要求按中序展开呢？** 中序展开就是中序遍历序列，递归交换左右指针的方向不同

## 关联题

- 同套路：[226. 翻转二叉树](226-invert-binary-tree.md) —— 也是递归操作左右子树
- 进阶：[109. 有序链表转换二叉搜索树](https://leetcode.cn/problems/convert-sorted-list-to-binary-search-tree/) —— 展开的逆过程
- 易混：[199. 二叉树的右视图](199-binary-tree-right-side-view.md) —— 只看每层最右节点，不修改结构
- 知识点：二叉树的递归遍历框架见[二叉树](../二叉树.md)
