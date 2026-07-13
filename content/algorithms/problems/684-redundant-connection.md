---
topics:
  - 图论
techniques:
  - 并查集
---

# 684. 冗余连接（Redundant Connection）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

n 个节点（1~n），`[u, v]` 边，删去一条边使树成立（无环）。有多个解时返回最后出现的边。

## 思路

**并查集检测环**：按顺序遍历边，如果 u 和 v 已经在同一集合中，说明当前边会导致环，它就是冗余连接。

## 代码

```java
public int[] findRedundantConnection(int[][] edges) {
    int n = edges.length;
    int[] parent = new int[n + 1];          // 节点从 1 开始
    for (int i = 1; i <= n; i++) parent[i] = i;

    for (int[] e : edges) {
        if (find(parent, e[0]) == find(parent, e[1]))
            return e;                        // 已连通 → 冗余
        union(parent, e[0], e[1]);
    }
    return new int[0];
}

private int find(int[] parent, int x) {
    if (parent[x] != x) parent[x] = find(parent, parent[x]);
    return parent[x];
}

private void union(int[] parent, int a, int b) {
    parent[find(parent, a)] = find(parent, b);
}
```

## 复杂度

- **时间**：O(n α(n))
- **空间**：O(n)

## 边界条件

- 题目保证至少有一条冗余边

## 变式

- **[685. 冗余连接 II](https://leetcode.cn/problems/redundant-connection-ii/)**：有向图版，需要分情况（入度为 2 或成环）

## 易错点

- 节点从 1 开始编号，parent 数组大小 = n + 1
- 返回的是**最后出现**的冗余边——因为按顺序遍历，第一条检测到的就是"输入顺序中最后那条导致环的边"

## 面试追问

- **有向图版的冗余连接？** 需要额外处理入度为 2 的情况，比无向图复杂

## 关联题

- 同套路：[547. 省份数量](547-number-of-provinces.md) —— 基础并查集
- 进阶：[990. 等式方程的可满足性](990-satisfiability-of-equality-equations.md) —— 带约束的并查集
- 知识点：并查集检测环见[并查集](图论.md)

