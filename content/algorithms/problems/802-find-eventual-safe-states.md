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

- **自环节点**：出度永远 ≥ 1，减不到 0，永不安全。天然被排除
- **孤立节点**（出度为 0）：本身就是终点，安全，直接入队
- **整张图是一个大环**：没有出度为 0 的节点，队列初始为空，返回空列表
- **答案必须升序**：`safe[]` 按下标扫一遍收集即可，不需要额外排序
- **重边**：`rev[j].add(i)` 会加两次，`outdeg[i]` 也加两次，两边配平，结果正确

## 变式

- **[207. 课程表](207-course-schedule.md)**：判断有向图是否有环，Kahn 用**入度**
- **[210. 课程表 II](210-course-schedule-ii.md)**：输出拓扑序
- **DFS + 三色标记**：白（未访问）/灰（在当前递归栈上）/黑（已确认安全）。走到灰色节点说明成环，整条路径都不安全
- **找出所有「必然进入环」的节点**：即本题的补集

## 易错点

- **要建反向图，且减的是原图的出度**。正着做拓扑排序（减入度）解决的是「谁能被走到」，而本题问的是「谁能走到终点」——**方向反了，问题就不是同一个问题**
- `rev[j].add(i)` 与 `outdeg[i]++` 必须在同一层循环里配对写，漏掉任何一个都会让入队条件永远不成立
- 安全节点的判定是「**所有**出边都通向安全节点」，不是「存在一条」。Kahn 的 `--outdeg[prev] == 0` 恰好表达了「所有出边都已被确认安全」
- 别用普通 DFS + visited 直接判「能否到达终点」——环上的节点会被误判，必须区分「在当前路径上」和「已访问过」

## 面试追问

- **为什么反向拓扑排序恰好等价于「安全」的定义**：安全的定义是递归的——「一个节点安全 ⇔ 它的所有后继都安全」，基例是出度为 0 的终点。Kahn 从基例出发，每确认一个安全节点，就把它从所有前驱的出度里减掉；**前驱的出度归零，意味着它的后继全部被确认安全**，正好命中递归定义。
- **和判环有什么关系**：不安全的节点 = 能走到环里的节点。所以本题也可以先找出所有环、再反向标记能到达环的节点。**但 Kahn 版本一次遍历就同时完成了这两件事**，因为环上节点的出度永远减不到 0。
- **DFS 三色标记怎么写**：`color[i] = 0/1/2`。进入时染灰，递归所有出边；遇到灰色 → 有环，返回 false；全部返回 true 则染黑（安全）。**灰色 = 在当前递归栈上**，这是它与普通 visited 的本质区别。
- **两种做法怎么选**：Kahn 是迭代的，不会栈溢出，且天然给出安全节点集合；DFS 递归更短，但深图可能爆栈。见[图论](图论.md)。

## 关联题

- 同套路：[207. 课程表](207-course-schedule.md)、[210. 课程表 II](210-course-schedule-ii.md)

