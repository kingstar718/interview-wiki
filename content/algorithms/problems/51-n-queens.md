---
topics:
  - 回溯
techniques:
  - 回溯框架
  - 剪枝
---

# 51. N 皇后（N-Queens）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

N×N 棋盘放置 N 个皇后，使它们互不攻击（不同行/列/对角线）。返回所有合法布局。

**示例**：
```
输入: n = 4
输出: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
```

## 思路

**逐行放置 + 列/对角线冲突判重**：从第 0 行开始，每行尝试放皇后到各列，用三个集合记录"被占用的列、主对角线、副对角线"。

- 主对角线特征：`row - col` 为常数（左上→右下）
- 副对角线特征：`row + col` 为常数（右上→左下）

## 代码

```java
public List<List<String>> solveNQueens(int n) {
    List<List<String>> res = new ArrayList<>();
    boolean[] cols = new boolean[n];
    boolean[] diag1 = new boolean[2 * n - 1];   // row - col + n - 1
    boolean[] diag2 = new boolean[2 * n - 1];   // row + col
    char[][] board = new char[n][n];
    for (char[] row : board) Arrays.fill(row, '.');
    backtrack(n, 0, cols, diag1, diag2, board, res);
    return res;
}

private void backtrack(int n, int row, boolean[] cols, boolean[] d1, boolean[] d2,
                       char[][] board, List<List<String>> res) {
    if (row == n) {
        List<String> snapshot = new ArrayList<>();
        for (char[] r : board) snapshot.add(new String(r));
        res.add(snapshot);
        return;
    }
    for (int col = 0; col < n; col++) {
        int idx1 = row - col + n - 1;
        int idx2 = row + col;
        if (cols[col] || d1[idx1] || d2[idx2]) continue;
        board[row][col] = 'Q';
        cols[col] = d1[idx1] = d2[idx2] = true;
        backtrack(n, row + 1, cols, d1, d2, board, res);
        cols[col] = d1[idx1] = d2[idx2] = false;
        board[row][col] = '.';
    }
}
```

## 复杂度

- **时间**：O(n!) —— 第一行 n 种选择，第二行最多 n-1 种……最坏 n!
- **空间**：O(n) —— 三个标记数组 + board

## 边界条件

- n = 1：返回 `[["Q"]]`
- n = 2/3：无解，返回空列表

## 变式

- **[52. N 皇后 II](https://leetcode.cn/problems/n-queens-ii/)**：只返回解的数量，不输出具体布局。可以用位运算优化（用 int 的位代替 boolean 数组）
- **N 皇后（位运算版）**：用三个 int 表示列/对角线占用，每层通过位运算枚举可放位置，常数级优化
- 打印/统计所有解：本题是搜集输出，52 题只计数

## 易错点

- **对角线的长度**：2n-1，不是 n。`row - col` 范围 `[-(n-1), n-1]`，平移 `n-1` 后映射到 `[0, 2n-2]`；`row + col` 范围 `[0, 2n-2]`
- 每行只放一个皇后（逐行遍历），所以不需要行冲突标记。列和对角线需要
- `board` 每次回溯后要恢复 `.`

## 面试追问

- **怎么用位运算优化？** 三个整数 `cols`、`d1`、`d2`，可放位置 = `~(cols | d1 | d2) & ((1<<n)-1)`，然后枚举最低位的 1——答出来加分，但不必在代码里写，因为可读性差
- **N 皇后的解的数量公式？** 没有闭式解，当 n=8 时有 92 个解。用位运算可以快速验算

## 关联题

- 同套路：[37. 解数独](37-sudoku-solver.md) —— 更复杂的棋盘约束回溯
- 进阶：[46. 全排列](46-permutations.md) —— 另一种"每层可选列表"的回溯
- 知识点：对角线公式、棋盘回溯模板见[回溯](回溯.md)

