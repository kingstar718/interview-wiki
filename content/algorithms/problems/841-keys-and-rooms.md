---
topics:
  - 图论
techniques:
  - 图遍历
---

# 841. 钥匙和房间（Keys and Rooms）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

有 N 个房间，从 0 号开始。每个房间有若干钥匙，每把钥匙可以打开对应房间。判断是否能进入所有房间。

**示例**：
```
输入: rooms = [[1],[2],[3],[]]
输出: true  （0→1→2→3）
输入: rooms = [[1,3],[3,0,1],[2],[0]]
输出: false  （无法进入 2）
```

## 思路

**DFS/BFS 遍历**：从房间 0 出发，用 visited 数组记录已访问房间。每次进入一个房间，拿到钥匙后去开对应房间。DFS 递归或 BFS 队列均可。

本质是图的遍历：房间是节点，钥匙是有向边。

## 代码

```java
// DFS
public boolean canVisitAllRooms(List<List<Integer>> rooms) {
    boolean[] visited = new boolean[rooms.size()];
    dfs(rooms, 0, visited);
    for (boolean v : visited) {
        if (!v) return false;
    }
    return true;
}

private void dfs(List<List<Integer>> rooms, int room, boolean[] visited) {
    visited[room] = true;
    for (int key : rooms.get(room)) {
        if (!visited[key]) {
            dfs(rooms, key, visited);
        }
    }
}

// BFS
public boolean canVisitAllRooms(List<List<Integer>> rooms) {
    int n = rooms.size();
    boolean[] visited = new boolean[n];
    Queue<Integer> q = new LinkedList<>();
    q.offer(0);
    visited[0] = true;
    int count = 1;
    while (!q.isEmpty()) {
        int room = q.poll();
        for (int key : rooms.get(room)) {
            if (!visited[key]) {
                visited[key] = true;
                q.offer(key);
                count++;
            }
        }
    }
    return count == n;
}
```

## 复杂度

- **时间**：O(N + E)，N = 房间数，E = 钥匙总数
- **空间**：O(N)

## 边界条件

- 只有一个房间：返回 true
- 房间 0 无钥匙但其他房间不可达：返回 false
- 有环（A→B, B→A）：visited 防止重复访问

## 变式

- **[200. 岛屿数量](200-number-of-islands.md)**：网格图遍历
- **[207. 课程表](207-course-schedule.md)**：有向图找环

## 易错点

- visited 必须在入队/递归前标记，避免重复入队
- 本质是有向图的连通性判断——DFS/BFS 均可
- 不需要显式建图，`rooms.get(room)` 就是邻接表

## 面试追问

- **BFS 和 DFS 哪个好？** 都行，时间复杂度相同。DFS 写起来更短，BFS 可以统计访问计数提前判断
- **如果房间数量很大？** 迭代 BFS 比递归 DFS 更安全（避免栈溢出）

## 关联题

- 同套路：[200. 岛屿数量](200-number-of-islands.md) —— 网格图连通性
- 进阶：[207. 课程表](207-course-schedule.md) —— 有向图拓扑排序
- 知识点：有向图连通性 DFS/BFS 见[图](图论.md)
