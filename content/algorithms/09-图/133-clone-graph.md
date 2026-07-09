# 133. 克隆图（Clone Graph）

频次 ★★★ · 难度 🟡 · 高频：腾讯

## 题目

给你无向连通图的一个节点引用，返回**深拷贝**该图。每个节点包含值 val 和邻居列表。

## 思路

**DFS + HashMap**：用一个 Map 记录"原节点 → 克隆节点"，遍历原图时：

- 如果节点已克隆，直接返回克隆引用
- 否则创建克隆节点，注册到 Map，然后递归克隆它的所有邻居

## 代码

```java
private Map<Node, Node> map = new HashMap<>();

public Node cloneGraph(Node node) {
    if (node == null) return null;
    if (map.containsKey(node)) return map.get(node);

    Node clone = new Node(node.val);
    map.put(node, clone);                    // 先注册，避免循环引用死循环

    for (Node neighbor : node.neighbors) {
        clone.neighbors.add(cloneGraph(neighbor));
    }
    return clone;
}
```

## 复杂度

- **时间**：O(n + m) —— n 个节点、m 条边
- **空间**：O(n) —— HashMap + 递归栈

## 边界条件

- 空节点：返回 null
- 单节点无邻居：创建 val 相同的单节点返回
- 大图（递归深度可能很高）：考虑 BFS 版防栈溢出

## 变式

- **[138. 复制带随机指针的链表](138-copy-list-with-random-pointer.md)**：链表版的深拷贝，思路完全相同（Map + 递归），邻居换成 next 和 random
- **BFS 版克隆**：用队列层序遍历原图，逐步克隆邻居

## 易错点

- **必须先注册克隆节点再递归邻居**：否则循环引用（A→B→A）会导致无限递归栈溢出。先 `map.put(node, clone)` 再遍历 neighbors
- 无向图的邻居会在相邻节点中互相引用，所以必须防环
- val 可能重复（题目说 val 唯一但即使不唯一也不能拿 val 当 key），必须用 Node 引用做 key

## 面试追问

- **BFS 版怎么写？** 队列 + Map，出队时克隆邻居并注册。和 DFS 时空复杂度一样，无栈溢出风险
- **为什么这和链表复制是同一道题？** 都是"遍历一个带引用的结构，用 Map 记录原始→副本的映射"，递归处理嵌套引用。链表是图的特例（每个节点度 ≤ 2）

## 关联题

- 同套路：[138. 复制带随机指针的链表](138-copy-list-with-random-pointer.md) —— Map + 递归/迭代的深拷贝模式
- 进阶：[200. 岛屿数量](200-number-of-islands.md) —— 图的另一类遍历（网格图）
- 知识点：图的 DFS 遍历 + 哈希映射技巧见[图](algorithms/09-图/README.md)

