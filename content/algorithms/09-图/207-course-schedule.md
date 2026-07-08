# 207. 课程表（Course Schedule）

频次 ★★★★★ · 难度 🟡 · 高频：字节/阿里/美团

## 题目

n 门课编号 0~n-1，给定选课依赖关系 `[a, b]` 表示"选 a 前必须先选 b"，判断是否能学完（是否有环）。

**示例**：
```
输入: n = 2, prerequisites = [[1,0]]
输出: true

输入: n = 2, prerequisites = [[1,0],[0,1]]
输出: false
```

## 思路

**拓扑排序**——判断有向图是否有环。两种方法：

**Kahn 算法（BFS）**：统计每个节点的入度，将入度为 0 的节点入队；依次弹出并减少相邻节点入度，入度变 0 的入队。最后如果所有节点都出队，说明无环。

**DFS 版**：三色标记（0=未访, 1=访问中, 2=完成），遍历中发现再访访问中的节点即检测到环。

## 代码

```java
// Kahn 算法（BFS）
public boolean canFinish(int numCourses, int[][] prerequisites) {
    List<Integer>[] graph = new List[numCourses];
    int[] indegree = new int[numCourses];
    for (int i = 0; i < numCourses; i++) graph[i] = new ArrayList<>();
    for (int[] p : prerequisites) {
        graph[p[1]].add(p[0]);              // b → a
        indegree[p[0]]++;
    }

    Queue<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) {
        if (indegree[i] == 0) q.offer(i);
    }

    int count = 0;
    while (!q.isEmpty()) {
        int cur = q.poll();
        count++;
        for (int next : graph[cur]) {
            if (--indegree[next] == 0) q.offer(next);
        }
    }
    return count == numCourses;
}
```

## 复杂度

- **时间**：O(n + m) —— n 个节点、m 条边，建图 + 遍历
- **空间**：O(n + m) —— 邻接表

## 边界条件

- 空依赖：全部无环，返回 true
- 自环 `[0, 0]`：入度计算导致永远无法入队，count < n → false
- 多个连通分量：正常拓扑排序处理

## 变式

- **[210. 课程表 II](210-course-schedule-ii.md)**：不仅判断环，还要输出一种拓扑序（BFS 出队顺序即是结果）
- **[802. 找到最终的安全状态](https://leetcode.cn/problems/find-eventual-safe-states/)**：反向图 + 拓扑排序
- **[269. 火星词典](https://leetcode.cn/problems/alien-dictionary/)**：字符比较构造依赖关系，然后拓扑排序

## 易错点

- **输入 `[a, b]` 表示 a 依赖 b（学 a 前先学 b），即 b → a**——经常搞反方向。建图时统一成 `graph[p[1]].add(p[0])`
- 入度数组和邻接表要正确初始化，节点数 = numCourses
- BFS 最后用 `count == numCourses` 判断，而不是队列是否为空——队列为空时可能还有环残留

## 面试追问

- **DFS 三色标记怎么检测环？** DFS 遍历中遇到"访问中"（灰色）的节点即有环。会用两种写法是图论扎实的表现
- **如果要输出任意一种拓扑序？** Kahn 算法的出队顺序本身就是一种拓扑序，存入数组返回即可（见 210 题）

## 关联题

- 同套路：[210. 课程表 II](210-course-schedule-ii.md) —— 输出拓扑序
- 进阶：[787. K 站中转内最便宜的航班](787-cheapest-flights-within-k-stops.md) —— 带权图的最短路径
- 知识点：拓扑排序（Kahn / DFS 两色法）见[图](algorithms/09-图/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
