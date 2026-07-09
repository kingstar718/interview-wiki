---
topics:
  - 图论
---

# 787. K 站中转内最便宜的航班（Cheapest Flights Within K Stops）

频次 ★★★★ · 难度 🟡 · 高频：字节

## 题目

n 个城市（0~n-1），`[from, to, price]` 表示单向航班，最多经过 k 站中转（即 k+1 次飞行），求 src 到 dst 的最低价格。

**示例**：
```
输入: n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]],
      src = 0, dst = 3, k = 1
输出: 700  （0→1→3）
```

## 思路

**Bellman-Ford 的变体（DP）**：限制边数的单源最短路径。

定义 `dist[t]` 为从 src 到 t 的最小花费，初始 dist[src] = 0，其余 ∞。每轮迭代模拟一次飞行（即步进一层图），用上一轮的距离更新当前轮的距离。迭代 k+1 轮后检查 dist[dst]。

技巧：必须用**上一轮**的 dist 快照来更新，否则一轮内可能会连飞多步，违反中转次数限制。

## 代码

```java
public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;

    for (int i = 0; i <= k; i++) {
        int[] prev = dist.clone();          // 快照上一轮的 dist
        for (int[] f : flights) {
            if (prev[f[0]] != Integer.MAX_VALUE) {
                dist[f[1]] = Math.min(dist[f[1]], prev[f[0]] + f[2]);
            }
        }
    }
    return dist[dst] == Integer.MAX_VALUE ? -1 : dist[dst];
}
```

## 复杂度

- **时间**：O(k × E) —— k+1 轮遍历所有边
- **空间**：O(n)

## 边界条件

- src == dst：返回 0，无需飞行
- k = 0：只允许直达，检查 src→dst 的边
- 不可达：返回 -1

## 变式

- **[743. 网络延迟时间](https://leetcode.cn/problems/network-delay-time/)**：Dijkstra 无限制步数的单源最短路径
- **Dijkstra 限制步数版**：存 `(price, city, stops)` 到优先队列，但 Bellman-Ford 在本题更简洁

## 易错点

- **不能用普通 Dijkstra**：Dijkstra 不限制步数，可能因绕路导致步数超限但被提前弹出队列丢弃
- 必须 `dist.clone()` 快照：不克隆的话，同一轮内 `dist[f[0]]` 可能已被本轮更新，造成"一轮飞了多步"，违反 k 限制
- `k` 是中转数，循环范围是 `<= k`（即 k+1 次飞行）

## 面试追问

- **和标准 Bellman-Ford 的区别？** 标准 BF 迭代 V-1 轮确保收敛，本题限制轮数为 k+1 且每轮用快照——相当于带步数限制的 BF。答出来说明对 BF 底层的"松弛轮数 = 最长路径边数"理解到位
- **Dijkstra 能改吗？** 可以，节点状态变成 `(city, stops)` 二元组，但队列中可能存大量冗余状态，空间开销大

## 关联题

- 同套路：[743. 网络延迟时间](https://leetcode.cn/problems/network-delay-time/) —— 标准 Dijkstra
- 进阶：[207. 课程表](207-course-schedule.md) —— 图的拓扑遍历
- 知识点：Bellman-Ford DP 模板见[图](图论.md)；INF 判断防溢出见[Java基础](Java基础.md)

