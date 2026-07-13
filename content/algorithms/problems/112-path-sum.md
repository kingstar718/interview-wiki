# 112. 路径总和（Path Sum）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

判断二叉树中是否存在一条从根到叶子的路径，路径上所有节点值之和等于 targetSum。

**示例**：
```
输入: root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
输出: true（路径 5→4→11→2）
```

## 思路

**递归减法**：每进入一个节点，将 targetSum 减去当前节点值。到达叶子节点时，判断剩余值是否减到了 0。

核心洞察：不需要记录路径，只需要知道"是否存在"——所以递归返回 boolean，左子树或右子树任意一条满足即可。

## 代码

```java
public boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    // 到达叶子节点，判断是否刚好减到 0
    if (root.left == null && root.right == null) {
        return root.val == targetSum;
    }
    // 递归左右子树，targetSum 减去当前节点值
    return hasPathSum(root.left, targetSum - root.val)
        || hasPathSum(root.right, targetSum - root.val);
}
```

## 复杂度

- **时间**：O(n) —— 最坏遍历所有节点
- **空间**：O(height) —— 递归栈深度

## 边界条件

- 空树：返回 false（没有路径）
- 根即叶子且值等于 target：返回 true
- 根即叶子但值不等于 target：返回 false
- 全负数节点 + target 为负：正常匹配
- 多条路径满足：找到一条即返回 true（短路）

## 变式

- **[113. 路径总和 II](113-path-sum-ii.md)**：返回所有满足条件的路径，需要回溯 + 路径列表
- **[437. 路径总和 III](https://leetcode.cn/problems/path-sum-iii/)**：路径不限于根到叶子，任意节点到任意节点，前缀和 + 哈希表
- 返回"最大和路径"：DFS 时维护全局最大值

## 易错点

- 必须在**叶子节点**判断 `targetSum == 0`，不能在中间节点就判断——中间节点不算路径终点
- 空树返回 false 而非 `targetSum == 0`——空树没有路径，即使 targetSum 为 0 也不应返回 true
- 减法的 targetSum 是值传递，不需要回溯恢复

## 面试追问

- **如果要求返回路径本身而不是布尔值？** 升级为 [113. 路径总和 II](113-path-sum-ii.md)，DFS + 回溯，到达叶子时 `new ArrayList<>(path)` 深拷贝
- **如果路径可以从任意节点开始？** 升级为 [437. 路径总和 III](https://leetcode.cn/problems/path-sum-iii/)，用前缀和 + HashMap 将 O(n²) 降到 O(n)

## 关联题

- 同套路：[113. 路径总和 II](113-path-sum-ii.md) —— 回溯收集所有路径，本题是它的简化版
- 进阶：[437. 路径总和 III](https://leetcode.cn/problems/path-sum-iii/) —— 任意起点终点，前缀和优化
- 知识点：DFS 路径搜索模板见[二叉树](algorithms/08-二叉树/README.md)

---

[← 返回训练计划](社招算法训练计划.md)