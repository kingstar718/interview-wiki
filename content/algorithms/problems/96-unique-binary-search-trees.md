---
topics:
  - 动态规划与贪心
techniques:
  - 线性DP
---

# 96. 不同的二叉搜索树（Unique Binary Search Trees）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

给定整数 n，求由 1..n 为节点组成的、结构不同的二叉搜索树有多少种。

**示例**：
```
输入: n = 3
输出: 5
```

## 思路

**卡特兰数（Catalan Number）DP**：以 i 为根，左子树节点为 1..i-1（共 i-1 个），右子树节点为 i+1..n（共 n-i 个）。

`dp[n] = sum(dp[i-1] × dp[n-i]) for i = 1..n`

这是卡特兰数：`C_n = C(2n,n) / (n+1)`。

## 代码

```java
public int numTrees(int n) {
    int[] dp = new int[n + 1];
    dp[0] = 1;                          // 空树算 1 种
    dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        for (int j = 1; j <= i; j++) {
            dp[i] += dp[j - 1] * dp[i - j];
        }
    }
    return dp[n];
}
```

## 复杂度

- **时间**：O(n²)
- **空间**：O(n)

## 边界条件

- n = 0：返回 1（空树）
- n = 1：返回 1
- dp[0] = 1 是关键——空子树的组合数为 1

## 变式

- **[95. 不同的二叉搜索树 II](https://leetcode.cn/problems/unique-binary-search-trees-ii/)**：要求返回所有可能的树结构（递归构造）
- **卡特兰数通项公式**：`C_n = C(2n,n) / (n+1)`，直接计算 O(n)

## 易错点

- dp[0] 必须初始化为 1（空子树只有一种形状），否则乘积为 0
- 二重循环中 j 表示以 j 为根，左子树 j-1 个节点，右子树 i-j 个节点
- 注意跟 95 的区别：96 只计数，95 要返回 `List<TreeNode>`，需要递归构造

## 面试追问

- **卡特兰数还出现在哪些问题？** 括号生成（n 对括号的合法组合数）、出栈序列数、凸多边形三角划分
- **95 题（返回所有树）怎么写？** 递归：选根 i，左子树列表和右子树列表做笛卡尔积

## 关联题

- 同套路：[95. 不同的二叉搜索树 II](https://leetcode.cn/problems/unique-binary-search-trees-ii/) —— 构造所有树
- 进阶：[343. 整数拆分](343-integer-break.md) —— 同类 DP 枚举分割点
- 知识点：卡特兰数 DP + 二叉树计数见[动态规划](动态规划与贪心.md)
