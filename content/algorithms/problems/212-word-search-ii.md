---
topics:
  - 字典树
techniques:
  - Trie前缀树
---

# 212. 单词搜索 II（Word Search II）

频次 ★★★ · 难度 🔴 · 高频：阿里

## 题目

m×n 网格和一组单词，返回所有在网格中出现的单词（相邻四方向）。

## 思路

**Trie + 回溯**：将单词列表构建 Trie，DFS 网格时同时匹配 Trie 节点。匹配到一个单词就移除（防重复）。

## 代码

```java
public List<String> findWords(char[][] board, String[] words) {
    Trie root = new Trie();
    for (String w : words) root.insert(w);

    List<String> res = new ArrayList<>();
    for (int i = 0; i < board.length; i++)
        for (int j = 0; j < board[0].length; j++)
            dfs(board, i, j, root, res);
    return res;
}

private void dfs(char[][] b, int i, int j, Trie node, List<String> res) {
    char c = b[i][j];
    int idx = c - 'a';
    if (c == '#' || node.next[idx] == null) return;

    node = node.next[idx];
    if (node.word != null) {
        res.add(node.word);
        node.word = null;          // 去重
    }

    b[i][j] = '#';
    if (i > 0) dfs(b, i - 1, j, node, res);
    if (i < b.length - 1) dfs(b, i + 1, j, node, res);
    if (j > 0) dfs(b, i, j - 1, node, res);
    if (j < b[0].length - 1) dfs(b, i, j + 1, node, res);
    b[i][j] = c;
}

class Trie {
    Trie[] next = new Trie[26];
    String word;
    void insert(String w) {
        Trie node = this;
        for (char c : w.toCharArray()) {
            int idx = c - 'a';
            if (node.next[idx] == null) node.next[idx] = new Trie();
            node = node.next[idx];
        }
        node.word = w;
    }
}
```

## 复杂度

- **时间**：O(m×n × 3^(L-1)) + 建树
- **空间**：O(单词总长度)

## 边界条件

- **同一个单词在网格中出现多次**：靠 `node.word = null` 只收一次。这既是去重，也是剪枝
- **单词是另一个单词的前缀**（`["oa", "oaa"]`）：匹配到 `"oa"` 时不能 `return`，必须继续往下走才能找到 `"oaa"`
- **空网格或空单词表**：直接返回空列表
- **`c == '#'` 的短路判断必须在前**：`'#' - 'a'` 是负数，`node.next[idx]` 会数组越界。Java 的 `||` 短路保证了安全，但两个条件的顺序不能换

## 变式

- **[79. 单词搜索](79-word-search.md)**：只找一个单词，不需要 Trie，直接对着字符串回溯
- **单词数量很大**：本题的全部意义所在——79 的做法是 O(单词数 × 网格 × 4^L)，Trie 把「单词数」这个因子消掉了
- **返回每个单词出现的次数**：不能把 `word` 置 null，改成计数并在回溯时保证不重复计同一条路径
- **八方向 / 允许重复使用格子**：改 DFS 的邻居枚举与 visited 策略

## 易错点

- **匹配到单词后不能立刻 return**，因为可能存在更长的单词以它为前缀。要继续 DFS
- **`node.word = null` 的位置**：置空的是「已收录」标记，不是 Trie 节点本身。节点还得留着给更长的单词走
- **回溯要恢复 `b[i][j] = c`**，否则后续起点的搜索会看到一堆 `#`
- `dfs` 的第一件事是取 `b[i][j]` 并沿 Trie 下探一层——**Trie 节点和网格位置是同步推进的**，写成先下探再判格子会错位
- 忘了 `if (c == '#')` 就直接算 `idx`，会在访问过的格子上抛越界

## 面试追问

- **为什么 Trie 能把复杂度从「单词数 × 搜索」降到「一次搜索」**：79 的做法对每个单词独立搜一遍网格；Trie 把所有单词的公共前缀合并了，网格上走一步就等于同时推进了所有以该前缀开头的单词。**共享前缀 = 共享搜索路径。**
- **还能怎么剪枝**：匹配完一个单词后，若该 Trie 节点已无子节点，可以把它从父节点上摘掉。随着单词被逐个找到，Trie 会不断收缩，后续 DFS 提前失败。这是本题从 AC 到「跑得快」的关键优化。
- **`3^(L-1)` 里的 3 是怎么来的**：每一步有 4 个方向，但其中一个是来路（已被标记为 `#`），所以实际分支是 3。
- **为什么它是回溯而不是普通 DFS**：因为要**撤销**格子的占用标记（`b[i][j] = c`）。同一个格子在不同的搜索路径里可以被重复使用，只是在一条路径内不行——这正是[回溯](回溯.md)框架「选择、递归、撤销」的定义。

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)、[79. 单词搜索](79-word-search.md)

