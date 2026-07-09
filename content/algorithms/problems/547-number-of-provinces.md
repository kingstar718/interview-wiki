---
topics:
  - 图论
---

# 547. 省份数量（Number of Provinces）

频次 ★★★★ · 难度 🟡 · 高频：字节

## 题目

n × n 矩阵 isConnected，isConnected[i][j] = 1 表示 i 和 j 直接相连（无向），求省份（连通分量）数量。

**示例**：
```
输入: [[1,1,0],[1,1,0],[0,0,1]]
输出: 2
```

## 思路

**并查集**：遍历矩阵上半三角，连通的城市 union，最后统计不同根的数量。

也可以用 DFS/BFS 数连通分量，但本题是并查集专题的入门题。

## 代码

```java
public int findCircleNum(int[][] isConnected) {
    int n = isConnected.length;
    int[] parent = new int[n];
    for (int i = 0; i < n; i++) parent[i] = i;

    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            if (isConnected[i][j] == 1) union(parent, i, j);
        }
    }

    int count = 0;
    for (int i = 0; i < n; i++) {
        if (parent[i] == i) count++;
    }
    return count;
}

private int find(int[] parent, int x) {
    if (parent[x] != x) parent[x] = find(parent, parent[x]); // 路径压缩
    return parent[x];
}

private void union(int[] parent, int a, int b) {
    int ra = find(parent, a), rb = find(parent, b);
    if (ra != rb) parent[ra] = rb;
}
```

## 复杂度

- **时间**：O(n² α(n)) —— 近 O(n²)
- **空间**：O(n)

## 边界条件

- n = 1：返回 1
- 全连通：返回 1
- 全不连通：返回 n

## 变式

- **[200. 岛屿数量](200-number-of-islands.md)**：网格图并查集版本
- **[1319. 连通网络的操作次数](1319-number-of-operations-to-make-network-connected.md)**：统计多余边

## 易错点

- 遍历上半三角即可（`j = i + 1`），无向图对称
- 路径压缩用递归写法，大 n 时考虑迭代版防栈溢出

## 面试追问

- **并查集和 DFS 各有什么优缺点？** 并查集支持动态合并（边逐渐接入），DFS 需要全量图

## 关联题

- 同套路：[200. 岛屿数量](200-number-of-islands.md) —— 网格并查集
- 进阶：[684. 冗余连接](684-redundant-connection.md) —— 并查集检测环
- 知识点：并查集模板 + 路径压缩见[并查集](图论.md)

