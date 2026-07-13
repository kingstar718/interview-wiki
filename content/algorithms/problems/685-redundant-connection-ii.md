# 685. 冗余连接 II（Redundant Connection II）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

有向图由 n 个节点和 n 条边组成（根为没有父节点的节点，除根外每个节点恰好有一个父节点）。添加一条额外边后形成有向图，找这条多余边（若有多个答案，返回最后出现的）。

**示例**：
```
输入: edges = [[1,2],[1,3],[2,3]]
输出: [2,3]  （节点 3 有两个父节点 1 和 2，删除 [2,3]）
```

## 思路

**两种情况**：

1. **存在入度为 2 的节点**：该节点有两条入边，答案必为其中一条。枚举删除哪条，检查剩余的边是否构成有向树（无环 + 单根）。
2. **存在有向环**：所有节点入度均为 1，说明多余边导致了一个有向环。用并查集检测环，返回最后一条构成环的边。

**处理顺序**：先检查入度为 2 的情况，如果不存在再检查环。

## 代码

```java
public int[] findRedundantDirectedConnection(int[][] edges) {
    int n = edges.length;
    int[] parent = new int[n + 1];       // 记录每个节点的直接父节点
    int[] inDegree = new int[n + 1];
    int nodeWithTwoParents = -1;
    for (int[] e : edges) {
        inDegree[e[1]]++;
        if (inDegree[e[1]] == 2) {
            nodeWithTwoParents = e[1];
        }
    }
    // 情况 1：存在入度为 2 的节点
    if (nodeWithTwoParents != -1) {
        int[] cand1 = null, cand2 = null;
        for (int[] e : edges) {
            if (e[1] == nodeWithTwoParents) {
                if (cand1 == null) cand1 = e;
                else cand2 = e;
            }
        }
        // 先尝试删除 cand2（最后出现的），看是否构成有向树
        if (isTree(edges, cand2, n)) return cand2;
        return cand1;
    }
    // 情况 2：存在有向环
    UnionFind uf = new UnionFind(n + 1);
    for (int[] e : edges) {
        if (uf.isConnected(e[0], e[1])) return e;
        uf.union(e[0], e[1]);
    }
    return new int[]{};
}

private boolean isTree(int[][] edges, int[] removed, int n) {
    UnionFind uf = new UnionFind(n + 1);
    for (int[] e : edges) {
        if (e == removed) continue;
        if (uf.isConnected(e[0], e[1])) return false;
        uf.union(e[0], e[1]);
    }
    return true;
}

class UnionFind {
    int[] parent;
    UnionFind(int n) {
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }
    void union(int x, int y) { parent[find(x)] = find(y); }
    boolean isConnected(int x, int y) { return find(x) == find(y); }
}
```

## 复杂度

- **时间**：O(n × α(n))，并查集操作近乎 O(1)
- **空间**：O(n)

## 边界条件

- 只有一条边：题意保证 n≥3
- 入度为 2 的节点：两条候选边，返回最后出现的那条（如果删除后合法）
- 环的情况：用并查集检测，返回最后构成环的边

## 变式

- **[684. 冗余连接](https://leetcode.cn/problems/redundant-connection/)**：无向图版，直接用并查集找环
- **有向树判定**：只有一个根（入度为 0）+ 无环 + 连通

## 易错点

- 与 684 的区别：684 是无向图，直接并查集；685 是有向图，需要分情况讨论
- 入度为 2 时，两条候选边中先尝试删除后出现的（题目要求返回最后出现的）
- `isTree` 判断时排除被删除的边，用并查集检查是否有环
- 并查集判断有向环：如果 `e[0]` 和 `e[1]` 已经连通，加入 `e` 会形成环

## 面试追问

- **为什么入度为 2 时要分情况？** 因为多余边可能造成某节点入度为 2，也可能造成有向环。两种情况的处理方式不同
- **和 684 的区别？** 684 是无向图，只有一种情况（环），直接用并查集；685 是有向图，两种情况都要考虑

## 关联题

- 同套路：[684. 冗余连接](https://leetcode.cn/problems/redundant-connection/) —— 无向图版
- 进阶：[207. 课程表](207-course-schedule.md) —— 有向图环检测
- 知识点：有向图环检测 + 并查集见[图](algorithms/09-图/README.md)

---

[← 返回训练计划](社招算法训练计划.md)