---
topics:
  - 动态规划与贪心
techniques:
  - 背包
---

# 279. 完全平方数（Perfect Squares）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

给定正整数 n，找出最少个数的完全平方数（1, 4, 9, 16, ...）使其和为 n。

**示例**：
```
输入: n = 12
输出: 3  （12 = 4+4+4）
输入: n = 13
输出: 2  （13 = 4+9）
```

## 思路

**解法 1：完全背包 DP** — `dp[i]` 表示 i 的最少完全平方数个数。`dp[i] = min(dp[i], dp[i - j*j] + 1)`，j*j ≤ i。

**解法 2：BFS** — 把 n 看作起点，每一步减去一个完全平方数，BFS 层数即为最少个数（求最短路径）。等价于在图中找从 n 到 0 的最短路径。

**解法 3：四平方和定理** — 数学方法 O(√n)，但面试不要求。

## 代码

```java
// 解法 1：完全背包 DP
public int numSquares(int n) {
    int[] dp = new int[n + 1];
    Arrays.fill(dp, n + 1);              // INF
    dp[0] = 0;
    for (int i = 1; i <= n; i++) {
        for (int j = 1; j * j <= i; j++) {
            dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
        }
    }
    return dp[n];
}

// 解法 2：BFS
public int numSquares(int n) {
    Queue<Integer> q = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    q.offer(n);
    visited.add(n);
    int level = 0;
    while (!q.isEmpty()) {
        int size = q.size();
        level++;
        for (int i = 0; i < size; i++) {
            int cur = q.poll();
            for (int j = 1; j * j <= cur; j++) {
                int next = cur - j * j;
                if (next == 0) return level;
                if (visited.add(next)) q.offer(next);
            }
        }
    }
    return level;
}
```

## 复杂度

- **DP**：时间 O(n√n)，空间 O(n)
- **BFS**：时间 O(n√n) 最坏，空间 O(n)

## 边界条件

- n = 1：返回 1
- n 是完全平方数：返回 1
- dp[0] = 0（基值）

## 变式

- **[322. 零钱兑换](322-coin-change.md)**：完全背包求最值，硬币面额任意
- **[343. 整数拆分](343-integer-break.md)**：DP 拆分求最大乘积

## 易错点

- 完全平方数是 j*j 不是 j^2（Java 中 ^ 是异或）
- dp 初始化 INF 为 n+1（最坏情况全用 1，共 n 个）
- BFS 解法中 visited 剪枝很重要，否则会超时

## 面试追问

- **四平方和定理是什么？** 每个正整数至多由 4 个完全平方数构成。先判断答案是否为 1/2/4，否则为 3。O(√n) 时间，但面试一般不要求
- **DP 和 BFS 哪个更好？** DP 更直观，BFS 展示图论思维。面试中先 DP 再提 BFS 加分

## 关联题

- 同套路：[322. 零钱兑换](322-coin-change.md) —— 完全背包求最少硬币数
- 进阶：[343. 整数拆分](343-integer-break.md) —— DP 拆分求最大乘积
- 知识点：完全背包 + BFS 最短路径见[动态规划](动态规划与贪心.md)
