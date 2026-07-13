# 130. 被围绕的区域（Surrounded Regions）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里/腾讯

## 题目

m×n 矩阵，把被 'X' 包围的 'O' 翻转为 'X'。边界上的 'O' 和与边界相连的 'O' 不被翻转。

**示例**：
```
输入:
X X X X
X O O X
X X O X
X O X X
输出:
X X X X
X X X X
X X X X
X O X X
```

## 思路

**逆向思维**：从边界上的 'O' 出发做 DFS/BFS，标记为 '#'（不可翻转）。最后遍历矩阵：'O' → 'X'（被包围），'#' → 'O'（恢复）。

核心：边界上的 'O' 及其连通区域是安全的，其余 'O' 都要翻转。

## 代码

```java
public void solve(char[][] board) {
    if (board.length == 0) return;
    int m = board.length, n = board[0].length;
    // 从边界 'O' 出发 DFS 标记
    for (int i = 0; i < m; i++) {
        if (board[i][0] == 'O') dfs(board, i, 0);
        if (board[i][n - 1] == 'O') dfs(board, i, n - 1);
    }
    for (int j = 0; j < n; j++) {
        if (board[0][j] == 'O') dfs(board, 0, j);
        if (board[m - 1][j] == 'O') dfs(board, m - 1, j);
    }
    // 翻转
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (board[i][j] == 'O') board[i][j] = 'X';
            else if (board[i][j] == '#') board[i][j] = 'O';
        }
    }
}

private void dfs(char[][] board, int i, int j) {
    if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || board[i][j] != 'O')
        return;
    board[i][j] = '#';                            // 标记为安全
    dfs(board, i + 1, j);
    dfs(board, i - 1, j);
    dfs(board, i, j + 1);
    dfs(board, i, j - 1);
}
```

## 复杂度

- **时间**：O(m × n)
- **空间**：O(m × n) 递归栈

## 边界条件

- 空矩阵：直接返回
- 全 'X'：不变
- 全 'O'：全部是边界连通，不变

## 变式

- **[200. 岛屿数量](200-number-of-islands.md)**：沉没岛屿
- **[1020. 飞地的数量](1020-number-of-enclaves.md)**：统计被包围的陆地数量（不修改原数组）
- **[417. 太平洋大西洋水流](417-pacific-atlantic-water-flow.md)**：同款"从边界出发"思路

## 易错点

- 逆向思维：从边界出发找安全区域，而不是从内部找被包围区域——后者难以判断
- 标记用 '#' 或其他临时字符，最后恢复——不要直接改 'O' 为 'X'，否则会切断连通性
- 边界遍历时注意四边都检查，不要遗漏

## 面试追问

- **为什么从边界出发？** 因为只有边界上的 'O' 和与边界连通的 'O' 是安全的。逆向思维：找出不安全的（被包围的）不如找出安全的
- **BFS 怎么做？** 同样从边界出发，用队列代替递归

## 关联题

- 同套路：[200. 岛屿数量](200-number-of-islands.md) —— 沉没岛屿
- 进阶：[1020. 飞地的数量](1020-number-of-enclaves.md) —— 统计被包围数量
- 知识点：逆向思维 + 边界出发 DFS 见[图](algorithms/09-图/README.md)

---

[← 返回训练计划](社招算法训练计划.md)