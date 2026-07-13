---
topics:
  - 二叉树
techniques:
  - BFS层序
---

# 199. 二叉树的右视图（Binary Tree Right Side View）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

从二叉树右侧看过去，返回从上到下能看到的所有节点值。

**示例**：
```
输入: [1,2,3,null,5,null,4]
输出: [1,3,4]
```

## 思路

**层序遍历取每层最后一个**：BFS 逐层遍历，每层记录最后一个节点值加入结果。

**DFS 版（先右后左）**：根 → 右 → 左的顺序 DFS，depth 首次到达时记录该节点——等价于"先走右边，每层第一个访问到的就是右视图看到的"。

## 代码

```java
// BFS 版 — 直观但 O(n) 空间
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    if (root == null) return res;
    Queue<TreeNode> q = new ArrayDeque<>();
    q.offer(root);
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            TreeNode node = q.poll();
            if (i == size - 1) res.add(node.val);  // 每层最后一个
            if (node.left != null) q.offer(node.left);
            if (node.right != null) q.offer(node.right);
        }
    }
    return res;
}
```

```java
// DFS 版 — O(height) 空间（优先推荐）
public List<Integer> rightSideView(TreeNode root) {
    List<Integer> res = new ArrayList<>();
    dfs(root, 0, res);
    return res;
}

private void dfs(TreeNode node, int depth, List<Integer> res) {
    if (node == null) return;
    if (depth == res.size()) res.add(node.val);  // 每层第一个访问到的节点
    dfs(node.right, depth + 1, res);   // 先右后左
    dfs(node.left, depth + 1, res);
}
```

## 复杂度

- **BFS**：时间 O(n)，空间 O(n)
- **DFS**：时间 O(n)，空间 O(height)

## 边界条件

- 空树：返回空列表
- 单节点：返回 [val]
- 单支树：整条链都是右视图（DFS 先走右 = 一直向下，直到右到底才走左）

## 变式

- **左视图**：BFS 取每层第一个，或 DFS 先左后右
- **俯视图**：按 x 坐标分桶，层序保证覆盖
- **[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md)**：输出整层而不是最后一个

## 易错点

- DFS 版 `depth == res.size()` 的时机：第一次到达新的一层时才会触发，因为有 right 优先，所以每层第一个访问到的就是右视图
- BFS 版 `i == size - 1` 判定每层最后一个元素——不要在 for 循环外判断 queue 是否为空来取最后一个
- 不要混淆层数 depth 和结果列表的 index：res.size() 表示已经记录到的层数

## 面试追问

- **DFS 版的思路来源？** 本质是让右子树优先遍历，每层"第一个"访问到的节点就是该层最右的——层序遍历的"每层最后一个"在 DFS 里等价于"右优先的首个"。能答出两种解法说明遍历方式理解透彻

## 关联题

- 同套路：[102. 二叉树的层序遍历](102-binary-tree-level-order-traversal.md) —— 层序遍历的所有变式都是这个模板
- 进阶：[236. 二叉树的最近公共祖先](236-lowest-common-ancestor-of-a-binary-tree.md) —— 另一种"不需要处理整层"的递归解法
- 知识点：DFS 遍历顺序的灵活控制（先右后左）见[二叉树](二叉树.md)

