---
topics:
  - 字典树
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

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)、[79. 单词搜索](79-word-search.md)

