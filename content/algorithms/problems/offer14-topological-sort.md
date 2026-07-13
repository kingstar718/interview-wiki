---
topics:
  - 图论
techniques:
  - 拓扑排序
---

# 剑指 Offer II 14. 拓扑排序模板

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

拓扑排序模板题，给定 n 个节点和依赖关系，输出一个拓扑序。

## 思路

**Kahn 模板**：计算入度，入度为 0 入队，出队时减少相邻节点的入度。

## 代码

```java
public int[] topologicalSort(int n, int[][] edges) {
    List<Integer>[] graph = new List[n];
    int[] indeg = new int[n];
    for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
    for (int[] e : edges) {
        graph[e[0]].add(e[1]);
        indeg[e[1]]++;
    }

    Queue<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < n; i++) if (indeg[i] == 0) q.offer(i);

    int[] res = new int[n];
    int idx = 0;
    while (!q.isEmpty()) {
        int cur = q.poll();
        res[idx++] = cur;
        for (int next : graph[cur])
            if (--indeg[next] == 0) q.offer(next);
    }
    return idx == n ? res : new int[0];
}
```

## 复杂度

- **时间**：O(n + e)
- **空间**：O(n + e)

## 边界条件

- **图中有环**：环上节点入度永远减不到 0，出队总数 `idx < n`，返回空数组——这也是 Kahn 算法顺带完成的环检测
- **无边**：所有节点入度为 0，一次性全部入队，输出即任意顺序
- **重复边**：`(u, v)` 出现两次会让 `indeg[v]` 多加一次，若题目允许重边需先去重，否则 v 永远出不了队
- **自环** `(u, u)`：`indeg[u]` 自增，u 永远无法入队，等价于有环

## 变式

- **[210. 课程表 II](210-course-schedule-ii.md)**：输出任意一个拓扑序，本题的直接应用
- **字典序最小的拓扑序**：把 `ArrayDeque` 换成 `PriorityQueue`，每次取编号最小的零入度节点，复杂度升到 O(n log n + e)
- **判断拓扑序是否唯一**：队列中任一时刻元素个数都为 1 时唯一；一旦出现 ≥2 个零入度节点，就存在多个合法拓扑序
- **DFS 版**：后序遍历逆序即拓扑序，靠三色标记（未访问/在栈上/已完成）检测环

## 易错点

- **必须 `--indeg[next] == 0` 之后才入队**，不能一遇到相邻节点就入队——否则它的其他前驱还没处理完，拓扑序就错了
- 判环靠 `idx == n`，不要另写一套访问标记；Kahn 算法的出队计数天然就是环检测
- 入度数组统计的是**入边**，`edges[i] = [u, v]` 表示 u 先于 v，加的是 `indeg[v]++` 而不是 `indeg[u]++`，方向搞反会得到反向拓扑序

## 面试追问

- **Kahn 和 DFS 哪个好**：Kahn 更直观、天然检测环、能顺便求字典序最小；DFS 版代码短但要用三色标记区分「在当前递归栈上」（成环）和「已完成」（安全），二者复杂度相同。
- **为什么拓扑排序只对 DAG 成立**：拓扑序要求所有边都从前指向后，环上的节点互为前驱后继，无法排出这样的线性顺序。
- **入度为 0 的节点有多个时怎么选**：任选都合法，说明拓扑序不唯一。要确定性输出就换优先队列。
- **能不能用它做课程表的最短学期数**：能，按「层」处理（每轮把当前所有零入度节点一次性出队），轮数就是关键路径长度——这就是 BFS 分层的思想。

## 关联题

- 同套路：[207. 课程表](207-course-schedule.md) —— 只判断能否完成，即只做环检测
- 同套路：[210. 课程表 II](210-course-schedule-ii.md) —— 输出具体拓扑序，本模板的直接应用
- 知识点：Kahn 与 DFS 两种实现的取舍见[图论](图论.md)

