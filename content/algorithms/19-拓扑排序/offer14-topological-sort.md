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

## 关联题

- [207. 课程表](207-course-schedule.md)、[210. 课程表 II](210-course-schedule-ii.md)

---

[← 返回训练计划](社招算法训练计划.md)
