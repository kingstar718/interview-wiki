---
topics:
  - 回溯
techniques:
  - 回溯框架
---

# 332. 重新安排行程（Reconstruct Itinerary）

频次 ★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

给定一份航班列表 `tickets`（`[from, to]`），从 `"JFK"` 出发，用完所有机票且每张只能用一次，返回字典序最小的行程。

**示例**：
```
输入: tickets = [["MUC","LHR"],["JFK","MUC"],["SFO","SJC"],["LHR","SFO"]]
输出: ["JFK","MUC","LHR","SFO","SJC"]

输入: tickets = [["JFK","SFO"],["JFK","ATL"],["SFO","ATL"],["ATL","JFK"],["ATL","SFO"]]
输出: ["JFK","ATL","JFK","SFO","ATL","SFO"]
解释：另一条 ["JFK","SFO","ATL","JFK","ATL","SFO"] 字典序更大，不选
```

## 思路

**回溯 + TreeMap 排序邻接表**：将机票构建为 `Map<String, PriorityQueue<String>>`（TreeMap 保证目的地按字典序排列），然后回溯遍历。每次取当前机场的字典序最小目的地，用完一张票后递归，若死路则回溯换下一个目的地。

**更优解：Hierholzer 欧拉路径算法**：题目本质是求有向图的欧拉路径（一笔画，用完所有边）。由于题目保证至少存在一条有效路径，从 JFK 出发做后序遍历（DFS 后把节点加入结果），最后反转结果即为答案。注意：优先走字典序小的边，走完一条边就删除它。

## 代码

```java
// 方法一：回溯（通用，容易理解）
public List<String> findItinerary(List<List<String>> tickets) {
    Map<String, PriorityQueue<String>> graph = new HashMap<>();
    for (List<String> t : tickets) {
        graph.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).offer(t.get(1));
    }
    List<String> res = new ArrayList<>();
    res.add("JFK");
    backtrack(graph, "JFK", tickets.size(), res);
    return res;
}

private boolean backtrack(Map<String, PriorityQueue<String>> graph, String from,
                          int remain, List<String> res) {
    if (remain == 0) return true;                     // 所有机票用完
    PriorityQueue<String> pq = graph.get(from);
    if (pq == null || pq.isEmpty()) return false;      // 死路
    List<String> candidates = new ArrayList<>(pq);     // 快照，方便回溯恢复
    for (String to : candidates) {
        pq.remove(to);                                 // 用掉这张机票
        res.add(to);
        if (backtrack(graph, to, remain - 1, res)) return true;
        res.remove(res.size() - 1);                    // 回溯
        pq.offer(to);                                  // 恢复机票
    }
    return false;
}
```

```java
// 方法二：Hierholzer 欧拉路径（最优，O(n log n)）
public List<String> findItinerary(List<List<String>> tickets) {
    Map<String, PriorityQueue<String>> graph = new HashMap<>();
    for (List<String> t : tickets) {
        graph.computeIfAbsent(t.get(0), k -> new PriorityQueue<>()).offer(t.get(1));
    }
    List<String> res = new LinkedList<>();
    dfs("JFK", graph, res);
    return res;
}

private void dfs(String from, Map<String, PriorityQueue<String>> graph, List<String> res) {
    PriorityQueue<String> pq = graph.get(from);
    while (pq != null && !pq.isEmpty()) {
        String to = pq.poll();           // 取字典序最小的，用完即删
        dfs(to, graph, res);
    }
    res.add(0, from);                    // 后序遍历：走投无路时加入结果头部
}
```

## 复杂度

- **回溯法**：
  - **时间**：O(n!) 最坏 —— 需要回溯尝试所有路径
  - **空间**：O(n)
- **Hierholzer 法**：
  - **时间**：O(n log n) —— 建图时 PriorityQueue 插入 O(log n)，DFS 遍历每条边一次
  - **空间**：O(n)

## 边界条件

- 只有一张机票（如 `[["JFK","LAX"]]`）：返回 `["JFK","LAX"]`
- 有多个相同机票：需要正确处理重复边（PriorityQueue 天然支持）
- 存在死胡同：回溯法需要能回退；Hierholzer 法天然处理（后序遍历，死胡同先入结果）

## 变式

- **[753. 破解保险箱](https://leetcode.cn/problems/cracking-the-safe/)**：欧拉路径的另一种应用
- **[2097. 合法重新排列](https://leetcode.cn/problems/valid-arrangement-of-pairs/)**：欧拉路径在数对上的应用

## 易错点

- **字典序最小不是全局排序**：不能简单地把所有机票排序后拼接，必须在每一步选择字典序最小的合法下一步
- **回溯法需要恢复 PriorityQueue**：`remove` 和 `offer` 必须成对出现，否则状态污染
- **Hierholzer 法结果要反转或头插**：后序遍历的顺序是反的（死胡同先被记录），需要 `add(0, from)` 或最后 `Collections.reverse()`
- **PriorityQueue 的遍历顺序**：直接 for-each PriorityQueue 不保证顺序，回溯法中需要先转成 List 再排序，或每次 poll 最小的尝试

## 面试追问

- **为什么 Hierholzer 算法能保证正确性？** 欧拉路径定理：如果图中有且仅有一个出度比入度大 1 的起点和一个入度比出度大 1 的终点，则存在欧拉路径。后序遍历 DFS 等价于不断删除已走过的边，当某个节点无路可走时它就是路径的终点，加入结果后回溯
- **回溯法和 Hierholzer 法怎么选？** 面试中如果没想到 Hierholzer，用回溯法也能过（题目数据量小）；Hierholzer 是更优解，体现图论功底

## 关联题

- 同套路：[207. 课程表](207-course-schedule.md) —— 图论基础（拓扑排序）
- 进阶：[753. 破解保险箱](https://leetcode.cn/problems/cracking-the-safe/) —— 欧拉回路
- 知识点：欧拉路径模板见[回溯](回溯.md)
