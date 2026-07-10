---
topics:
  - 二叉树
techniques:
  - 回溯框架
---

# 113. 路径总和 II（Path Sum II）

频次 ★★★ · 难度 🟡 · 高频：百度

## 题目

找出所有从根到叶子、节点值之和等于 target 的路径。

**示例**：
```
输入: root = [5,4,8,11,null,13,4,7,2,null,null,5,1], targetSum = 22
输出: [[5,4,11,2],[5,8,4,5]]
```

## 思路

**DFS + 回溯**：从根出发，维护当前路径列表和当前和，到达叶子时判断是否等于 target。

回溯的关键：离开节点时从路径列表中移除自身，`path.remove(path.size() - 1)`。

## 代码

```java
public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> res = new ArrayList<>();
    dfs(root, targetSum, new ArrayList<>(), res);
    return res;
}

private void dfs(TreeNode node, int remain, List<Integer> path, List<List<Integer>> res) {
    if (node == null) return;
    path.add(node.val);
    remain -= node.val;
    if (node.left == null && node.right == null && remain == 0) {
        res.add(new ArrayList<>(path));   // 深拷贝，存当前快照
    } else {
        dfs(node.left, remain, path, res);
        dfs(node.right, remain, path, res);
    }
    path.remove(path.size() - 1);          // 回溯
}
```

## 复杂度

- **时间**：O(n²) 最坏 —— 每条 path 都要拷贝到结果列表（每条路径 O(h)，路径数 O(n)）；平均 O(n log n)
- **空间**：O(height + path 数) —— 递归栈 + 结果集

## 边界条件

- 空树：返回空列表
- 根即叶子且值等于 target：返回 `[[root.val]]`
- 全负数 + target 为负：正常匹配
- 多条路径满足：全部收集

## 变式

- **[112. 路径总和](https://leetcode.cn/problems/path-sum/)**：只问是否存在，不需要找路径——`remain == 0` 时立即返回 true
- **[437. 路径总和 III](https://leetcode.cn/problems/path-sum-iii/)**：路径不限于根到叶子，可以从任意节点到任意节点（前缀和 + 哈希表）
- **输出最大/最小和路径**：DFS 时维护全局 max/min

## 易错点

- `res.add(path)` 是**引用拷贝**，后续回溯会修改同一个 path 对象——必须 `new ArrayList<>(path)` 做快照
- 不能在入参里直接修改同一个 `List` 而不复原——回溯的"撤销"必须在递归返回后执行
- `remain` 用减法比加法更优雅（省去目标值参数），但要小心负值节点

## 面试追问

- **如果树特别深（路径长），怎么优化空间？** 还是只能 O(h)，因为需要存路径。但如果只需要输出一条路径（不是全部），那找到后立即 return 并逐层返回，不需要 path 列表（112 的做法）

## 关联题

- 同套路：[112. 路径总和](https://leetcode.cn/problems/path-sum/) —— 只问是否存在，不需要回溯收集
- 进阶：[437. 路径总和 III](https://leetcode.cn/problems/path-sum-iii/) —— 任意起点终点，前缀和优化
- 知识点：DFS + 回溯模板见[二叉树](二叉树.md)

