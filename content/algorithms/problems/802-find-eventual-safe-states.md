---
topics:
  - 图论
techniques:
  - 拓扑排序
---

# 802. 找到最终的安全状态（Find Eventual Safe States）

频次 ★★★ · 难度 🟡 · 高频：阿里

## 题目

有向图，节点安全 = 从它出发的任何路径能走到终点（出度为 0 的节点），返回所有安全节点。

## 思路

**反向图 + 拓扑排序**：建反向图，从出度为 0 的节点开始 BFS。安全的节点 = 能从出度为 0 节点反向到达的所有节点。

## 代码

```java
public List<Integer> eventualSafeNodes(int[][] graph) {
    int n = graph.length;
    List<Integer>[] rev = new List[n];
    int[] outdeg = new int[n];
    for (int i = 0; i < n; i++) rev[i] = new ArrayList<>();
    for (int i = 0; i < n; i++) {
        for (int j : graph[i]) {
            rev[j].add(i);          // 反向边
            outdeg[i]++;            // 原出度
        }
    }

    Queue<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < n; i++)
        if (outdeg[i] == 0) q.offer(i);

    boolean[] safe = new boolean[n];
    while (!q.isEmpty()) {
        int cur = q.poll();
        safe[cur] = true;
        for (int prev : rev[cur]) {
            if (--outdeg[prev] == 0) q.offer(prev);
        }
    }

    List<Integer> res = new ArrayList<>();
    for (int i = 0; i < n; i++) if (safe[i]) res.add(i);
    return res;
}
```

## 复杂度

- **时间**：O(n + e)
- **空间**：O(n + e)

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 同套路：[207. 课程表](207-course-schedule.md)、[210. 课程表 II](210-course-schedule-ii.md)

