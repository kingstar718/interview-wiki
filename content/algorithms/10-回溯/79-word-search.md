# 79. 单词搜索（Word Search）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

m×n 网格找单词（相邻格子四方向连接，同一格子不能用两次）。

**示例**：
```
输入: board = [["A","B","C","E"],["S","F","C","S"],["A","D","E","E"]], word = "ABCCED"
输出: true
```

## 思路

**回溯 + visited 标记**：从每个格子出发做 DFS，匹配 word 的每个字符。走过的格子用临时标记（或 visited 数组）防重复走。

## 代码

```java
private static final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};

public boolean exist(char[][] board, String word) {
    int m = board.length, n = board[0].length;
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (dfs(board, word, 0, i, j)) return true;
        }
    }
    return false;
}

private boolean dfs(char[][] board, String word, int idx, int i, int j) {
    if (idx == word.length()) return true;
    if (i < 0 || i >= board.length || j < 0 || j >= board[0].length
            || board[i][j] != word.charAt(idx)) return false;

    char tmp = board[i][j];
    board[i][j] = '#';                             // 标记已访问
    for (int[] d : DIRS) {
        if (dfs(board, word, idx + 1, i + d[0], j + d[1])) return true;
    }
    board[i][j] = tmp;                             // 恢复
    return false;
}
```

## 复杂度

- **时间**：O(m × n × 3^L) —— L 是单词长度，除起点外最坏 3 条分支（来路已被标记）
- **空间**：O(L) —— 递归栈深度

## 边界条件

- 空单词：返回 true
- 空网格：返回 false
- 单词比网格大：递归中自然越界返回 false

## 变式

- **[212. 单词搜索 II](https://leetcode.cn/problems/word-search-ii/)**：多个单词，用 Trie 加速前缀匹配，避免重复搜索
- **N 皇后同款 visited**：本质上都是在网格上做"带约束的 DFS 回溯"

## 易错点

- **标记方式**：用 `#` 或 `visited[i][j] = true` 都可以。直接在原数组上修改省空间，但要记得恢复
- 从每个格子出发（双重 for 循环），不是只从 board[0][0] 出发
- 剪枝：`board[i][j] != word.charAt(idx)` 在 DFS 入口判断，也可以在 for 循环里先判——效果一样

## 面试追问

- **多单词搜索（212）怎么优化？** 构建单词 Trie，DFS 时同步匹配 Trie 节点，前缀不匹配时剪枝——避免了每个单词独立 DFS 的重复开销
- **时间复杂度怎么算？** 每个格子启动一次 DFS，每次 DFS 除第一步有 4 个方向，后续最多 3 个方向（来的路被标记），所以 O(m×n×3^L)

## 关联题

- 同套路：[51. N 皇后](51-n-queens.md) —— 网格回溯 + visited
- 进阶：[212. 单词搜索 II](https://leetcode.cn/problems/word-search-ii/) —— Trie + 回溯
- 知识点：网格回溯模板见[回溯](algorithms/10-回溯/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
