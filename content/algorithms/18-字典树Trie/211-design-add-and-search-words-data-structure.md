# 211. 添加与搜索单词（Design Add and Search Words Data Structure）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

支持添加单词和搜索单词，搜索时 `.` 可匹配任意字符。

## 思路

**Trie + DFS**：遇到 `.` 时枚举所有非空子节点递归搜索。

## 代码

```java
class WordDictionary {
    private WordDictionary[] next = new WordDictionary[26];
    private boolean isEnd;

    public void addWord(String word) {
        WordDictionary node = this;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.next[idx] == null) node.next[idx] = new WordDictionary();
            node = node.next[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        return search(word, 0);
    }

    private boolean search(String word, int i) {
        if (i == word.length()) return isEnd;
        char c = word.charAt(i);
        if (c != '.') {
            int idx = c - 'a';
            return next[idx] != null && next[idx].search(word, i + 1);
        }
        for (WordDictionary child : next) {
            if (child != null && child.search(word, i + 1)) return true;
        }
        return false;
    }
}
```

## 复杂度

- 时间：add O(L)，search 最坏 O(26^L)
- 空间：O(总字符数 × 26)

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)、进阶：[212. 单词搜索 II](212-word-search-ii.md)

