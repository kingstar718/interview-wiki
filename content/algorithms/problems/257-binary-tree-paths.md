---
topics:
  - 二叉树
techniques:
  - DFS
---

# 257. 二叉树的所有路径（Binary Tree Paths）

频次 ★★★ · 难度 🟢 · 高频：美团

## 题目

找出所有从根到叶子的路径，以 `"1->2->5"` 格式字符串返回。

**示例**：
```
输入:    1
       /   \
      2     3
       \
        5
输出: ["1->2->5", "1->3"]
```

## 思路

**DFS + 回溯**：从根出发，维护当前路径字符串（或 StringBuilder 配合 path 列表）。到达叶子节点时，将路径字符串加入结果列表。

**字符串拼接**：每次递归传入 `当前路径 + "->" + 节点值`，免去回溯操作（字符串不可变，天然快照）。但字符串拼接会产生大量临时对象。更好的做法是用 `StringBuilder`：追加后递归，递归返回后恢复长度。

## 代码

```java
public List<String> binaryTreePaths(TreeNode root) {
    List<String> res = new ArrayList<>();
    if (root != null) dfs(root, new StringBuilder(), res);
    return res;
}

private void dfs(TreeNode node, StringBuilder sb, List<String> res) {
    int len = sb.length();
    sb.append(node.val);
    if (node.left == null && node.right == null) {
        // 到达叶子，记录当前路径
        res.add(sb.toString());
    } else {
        sb.append("->");
        if (node.left != null) dfs(node.left, sb, res);
        if (node.right != null) dfs(node.right, sb, res);
    }
    sb.setLength(len);  // 回溯：恢复 StringBuilder 长度
}
```

## 复杂度

- **时间**：O(n²) —— 每条路径都要生成字符串，路径 O(h) 条，每条 O(h) 拼接
- **空间**：O(height) —— 递归栈 + StringBuilder

## 边界条件

- 空树：返回空列表
- 单节点：返回 `["1"]`（只有数字，没有箭头）
- 只有一个子节点：`->` 箭头只在非叶子节点后添加

## 变式

- **返回路径节点值列表**（不用字符串）：改为 `List<List<Integer>>`，回溯时操作 List
- **只输出最长路径**：DFS 时维护最长路径
- **输出所有路径和**：在叶子节点时累加 `node.val` 到路径和

## 易错点

- StringBuilder 回溯时**必须恢复长度**，否则后续其他路径会带上之前路径的残留内容
- 叶子节点拼接 `sb.append(node.val)` 后不要加 `"->"`——箭头只在非叶子节点后添加
- 不要用 `sb.append("->").append(node.val)` 后再 `sb.delete(...)` 删除——应该用 `setLength(len)` 恢复，最干净

## 面试追问

- **用 String 拼接 vs StringBuilder 回溯有什么区别？** String 拼接不可变，每次生成新对象，不需要显式回溯，但 O(n²) 时间且 O(n²) 空间。StringBuilder 可复用，空间 O(h)，但需要手动回溯。推荐展示 StringBuilder 版本来说明"理解回溯的本质"
- **如果树很大，路径字符串很长，怎么优化？** 用 `List<Integer>` 存路径，最后统一 `String.join("->", list)` 生成字符串——减少中间字符串对象

## 关联题

- 同套路：[113. 路径总和 II](113-path-sum-ii.md) —— 同样是收集所有根到叶子的路径，但按和过滤
- 进阶：[112. 路径总和](112-path-sum.md) —— 只判断是否存在，不需要收集路径
- 知识点：回溯模板（选择 → 递归 → 撤销）见[二叉树](二叉树.md)
