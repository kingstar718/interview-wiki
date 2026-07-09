---
topics:
  - 图论
---

# 797. 所有可能的路径（All Paths From Source to Target）

频次 ★★★ · 难度 🟡 · 高频：快手

## 题目

给出有向无环图（DAG），节点 0 到 n-1，求从 0 到 n-1 的所有路径。

**示例**：
```
输入: graph = [[1,2],[3],[3],[]]
输出: [[0,1,3],[0,2,3]]
```

## 思路

**DFS + 回溯**：从 0 出发，每步记录当前路径，到达 n-1 时收集；递归回溯 `path.removeLast()`。

因为是 DAG 所以不用 visited 标记（无环保证不会死循环）。

## 代码

```java
public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
    List<List<Integer>> res = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    path.add(0);
    dfs(graph, 0, path, res);
    return res;
}

private void dfs(int[][] graph, int cur, List<Integer> path, List<List<Integer>> res) {
    if (cur == graph.length - 1) {
        res.add(new ArrayList<>(path));
        return;
    }
    for (int next : graph[cur]) {
        path.add(next);
        dfs(graph, next, path, res);
        path.remove(path.size() - 1);
    }
}
```

## 复杂度

- **时间**：O(2^n × n) 最坏（完全 DAG 的路径数指数级）——但在 n ≤ 15 的限制下可行
- **空间**：O(n) —— 当前路径 + 递归栈

## 边界条件

- n = 2 直达图：`graph = [[1],[]]`，输出 `[[0,1]]`
- n = 1：0 既是起点也是终点，输出 `[[0]]`
- 路径不存在：返回空列表

## 变式

- **加权图版**：除了收集路径还要计算路径权值和
- **[79. 单词搜索](79-word-search.md)**：在网格中找路径，需 visited 标记

## 易错点

- 因为是 DAG 所以不用 visited 数组——但如果图可能有环就不能这么写了。确认题目条件后再决定加不加 visited
- 路径列表需要 `new ArrayList<>(path)` 深拷贝，否则后续回溯会修改已加入结果的路径
- 递归前 `path.add(next)`，递归后 `path.remove(path.size()-1)`——这是回溯的标准写法

## 面试追问

- **如果不是 DAG 呢？** 加 visited 数组在递归中标记，回溯时取消。但路径数会极大膨胀甚至无限（有环），通常需要限制路径长度或只判断可达性
- **输出所有路径 vs 判断是否存在？** 存在性用 BFS/DFS 直接判断，这题专门考"所有路径"所以必须回溯

## 关联题

- 同套路：[207. 课程表](207-course-schedule.md) —— DAG 上的拓扑遍历
- 进阶：[787. K 站中转内最便宜的航班](787-cheapest-flights-within-k-stops.md) —— 带权约束的最短路径
- 知识点：DFS + 回溯模板见[图](图论.md)

