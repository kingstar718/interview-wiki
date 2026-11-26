---
topics:
  - 图论
techniques:
  - 拓扑排序
---

# 210. 课程表 II（Course Schedule II）

频次 ★★★★ · 难度 🟡 · 高频：字节

## 题目

和 207 相同，但不仅判断是否有环，还要求返回**一个拓扑排序结果**。

**示例**：
```
输入: n = 4, prerequisites = [[1,0],[2,0],[3,1],[3,2]]
输出: [0,1,2,3] 或 [0,2,1,3]
```

## 思路

**Kahn 算法**：在 BFS 拓扑排序的基础上，每次出队时将节点加入结果数组。如果最终结果长度 == n 则返回结果，否则说明有环返回空数组。

## 代码

```java
public int[] findOrder(int numCourses, int[][] prerequisites) {
    List<Integer>[] graph = new List[numCourses];
    int[] indegree = new int[numCourses];
    for (int i = 0; i < numCourses; i++) graph[i] = new ArrayList<>();
    for (int[] p : prerequisites) {
        graph[p[1]].add(p[0]);
        indegree[p[0]]++;
    }

    Queue<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) {
        if (indegree[i] == 0) q.offer(i);
    }

    int[] res = new int[numCourses];
    int idx = 0;
    while (!q.isEmpty()) {
        int cur = q.poll();
        res[idx++] = cur;
        for (int next : graph[cur]) {
            if (--indegree[next] == 0) q.offer(next);
        }
    }
    return idx == numCourses ? res : new int[0];
}
```

## 207→210 三处改 + 收集时刻教训（挂账第三笔）

```
207→210 三处：返回 boolean→int[]；弹队时 order[count++]（收集与 count++ 同位）；
  有环返 new int[0]（三元方向别反——用户 v1 成功返空/失败返序颠倒）
用户 v1 收集时刻错位：res.add(next) 放在"邻居减到 0 入队"处——
  初始入度 0 的课（无前置、直接可修）全部漏收（[1,0] 返回 [1] 少元素）
  ⟹ 弹队才是"修完"时刻（207 的 count++ 就是弹队收的，收集跟它站同一行）
List<Integer>→int[] 无 toArray 捷径（装箱类型无 toArray(int[])——直接 int[]+idx 填）
多答案对账口径：任意合法序都对——want 空时 got 也空即过，非空则验合法性+全覆盖
```

## 复杂度

- **时间**：O(n + m)
- **空间**：O(n + m)

## 边界条件

- 空依赖：返回任意顺序（0,1,2,...,n-1 即可）
- 有环：返回空数组 `new int[0]`
- 单节点：返回 `[0]`

## 变式

- **[207. 课程表](207-course-schedule.md)**：只判环不输出
- **[802. 安全状态](https://leetcode.cn/problems/find-eventual-safe-states/)**：反向图 + 拓扑排序，输出安全节点

## 易错点

- 顺序不唯一：拓扑排序的结果不是唯一的，只要满足依赖关系即可。面试时说"任意一种拓扑序"
- 返回 `new int[0]` 而不是 `null`——题目要求空数组
- 和 207 的代码几乎一致，只需额外加一个数组记出队顺序

## 面试追问

- **要输出字典序最小的拓扑序？** BFS 的队列改为优先队列，每次取编号最小的入度为 0 节点
- **DFS 后序遍历的逆序 = 拓扑序？** 是的，DFS 在节点标记完成后入栈，最后逆序输出。对比 Kahn 算法的"正序"思路，展示两种方法

## 关联题

- 同套路：[207. 课程表](207-course-schedule.md) —— 判环版
- 进阶：[787. K 站中转内最便宜的航班](787-cheapest-flights-within-k-stops.md) —— 带约束的最短路径
- 知识点：拓扑排序输出模板见[图](../图论.md)

