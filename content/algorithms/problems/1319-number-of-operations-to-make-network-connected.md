---
topics:
  - 图论
techniques:
  - 并查集
---

# 1319. 连通网络的操作次数（Number of Operations to Make Network Connected）

频次 ★★★ · 难度 🟡 · 高频：快手

## 题目

n 台电脑，用 connections 连接，可移动缆线到未连接的电脑。求连通所有电脑的最少移动次数，无法连通返回 -1。

## 思路

**并查集**：连通 n 个节点至少需要 n-1 条边；如果 connections 数量 < n-1 直接返回 -1。

用并查集统计多余的边（已经连通的组件间再连边 = 多余），以及连通分量数量。需要移动的次数 = 连通分量数 - 1（只要有足够的多余边）。

## 代码

```java
public int makeConnected(int n, int[][] connections) {
    if (connections.length < n - 1) return -1;     // 边不够

    int[] parent = new int[n];
    for (int i = 0; i < n; i++) parent[i] = i;
    int components = n;                             // 初始 n 个孤立节点

    for (int[] c : connections) {
        int ra = find(parent, c[0]), rb = find(parent, c[1]);
        if (ra != rb) {
            parent[ra] = rb;
            components--;                           // 合并一个连通分量
        }
    }
    return components - 1;                          // 连通的边数
}

private int find(int[] parent, int x) {
    if (parent[x] != x) parent[x] = find(parent, parent[x]);
    return parent[x];
}
```

## 复杂度

- **时间**：O(n α(n))
- **空间**：O(n)

## 边界条件

- 边数 < n-1：返回 -1

## 变式

- **[547. 省份数量](547-number-of-provinces.md)**：数连通分量
- **[684. 冗余连接](684-redundant-connection.md)**：找具体哪条边多余

## 易错点

- 先判边数不足，再判需要移动次数（有多余边才能移动）
- `components` 初始为 n，union 成功时减一

## 面试追问

- **为什么最少移动次数 = components - 1？** 每个连通分量看做一个节点，需要 components - 1 条边才能连成树

## 关联题

- 同套路：[547. 省份数量](547-number-of-provinces.md) —— 并查集计数
- 进阶：[684. 冗余连接](684-redundant-connection.md) —— 环检测
- 知识点：并查集统计连通分量见[并查集](图论.md)

