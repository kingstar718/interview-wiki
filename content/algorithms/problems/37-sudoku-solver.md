---
topics:
  - 回溯
---

# 37. 解数独（Sudoku Solver）

频次 ★★★ · 难度 🔴 · 高频：阿里

## 题目

填充 9×9 数独板，使每行/每列/每个 3×3 宫格填入 1~9 不重复。题目保证有唯一解。

## 思路

**回溯 + 约束传播**：用三个 boolean 数组记录每行/每列/每宫格中 1~9 的使用情况。遍历空格，尝试填入 1~9 的有效数字并递归；找到第一个可行解即返回（题目保证唯一解）。

## 代码

```java
public void solveSudoku(char[][] board) {
    boolean[][] rows = new boolean[9][10];
    boolean[][] cols = new boolean[9][10];
    boolean[][] boxes = new boolean[9][10];

    for (int i = 0; i < 9; i++) {
        for (int j = 0; j < 9; j++) {
            if (board[i][j] != '.') {
                int n = board[i][j] - '0';
                int b = (i / 3) * 3 + j / 3;
                rows[i][n] = cols[j][n] = boxes[b][n] = true;
            }
        }
    }
    backtrack(board, 0, 0, rows, cols, boxes);
}

private boolean backtrack(char[][] board, int i, int j,
                          boolean[][] rows, boolean[][] cols, boolean[][] boxes) {
    if (j == 9) { i++; j = 0; }      // 换行
    if (i == 9) return true;          // 所有格子填完

    if (board[i][j] != '.') return backtrack(board, i, j + 1, rows, cols, boxes);

    int b = (i / 3) * 3 + j / 3;
    for (int n = 1; n <= 9; n++) {
        if (rows[i][n] || cols[j][n] || boxes[b][n]) continue;
        board[i][j] = (char) ('0' + n);
        rows[i][n] = cols[j][n] = boxes[b][n] = true;
        if (backtrack(board, i, j + 1, rows, cols, boxes)) return true;
        rows[i][n] = cols[j][n] = boxes[b][n] = false;
        board[i][j] = '.';
    }
    return false;
}
```

## 复杂度

- **时间**：O(9^m) —— m 为空格数量，实际受约束传播大幅优化
- **空间**：O(9×9) —— 三个标记数组 + 递归栈

## 边界条件

- 已填满的棋盘：直接返回
- 无解：题目保证有解（实际中返回 false 表示无解）

## 变式

- **[36. 有效的数独](https://leetcode.cn/problems/valid-sudoku/)**：只判合法性不求解，同样用三个 boolean 数组
- **舞蹈链（Dancing Links）**：精确覆盖问题的算法解数独，比回溯快但面试不要求

## 易错点

- **`(i / 3) * 3 + j / 3` 是宫格索引**：i/3 取行组（0~2），乘以 3 后 + j/3（列组），得到 0~8 的宫格编号。很多人临场推不出的公式，最好记牢
- 递归返回 boolean 有利于"找到就返回"——N 皇后是收集所有解所以 void，本题只需一个解所以返回 boolean
- 数字转字符：`(char)('0' + n)`，反向 `board[i][j] - '0'`

## 面试追问

- **如何进一步优化？** 每次选"可选数字最少"的空格先填（MRV 启发式），大幅减少回溯分支。展示对约束传播的理解

## 关联题

- 同套路：[51. N 皇后](51-n-queens.md) —— 棋盘约束回溯
- 进阶：[36. 有效的数独](https://leetcode.cn/problems/valid-sudoku/) —— 前置合法性判断
- 知识点：回溯 + 约束传播、宫格索引公式见[回溯](回溯.md)

