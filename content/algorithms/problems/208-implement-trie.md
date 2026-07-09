---
topics:
  - 字典树
---

# 208. 实现 Trie（Implement Trie）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

实现 Trie（前缀树），支持 insert、search、startsWith。

## 思路

每个节点有 26 个子节点指针和一个 isEnd 标记。插入/搜索按字符逐层。

## 代码

```java
class Trie {
    private Trie[] next = new Trie[26];
    private boolean isEnd;

    public void insert(String word) {
        Trie node = this;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.next[idx] == null) node.next[idx] = new Trie();
            node = node.next[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        Trie node = searchPrefix(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private Trie searchPrefix(String s) {
        Trie node = this;
        for (char c : s.toCharArray()) {
            node = node.next[c - 'a'];
            if (node == null) return null;
        }
        return node;
    }
}
```

## 复杂度

- **时间**：O(L) —— 单词长度
- **空间**：O(总字符数 × 26）

## 边界条件

- 插入空串：isEnd 标记在根节点上即可，不需要额外处理

## 变式

- **[211. 添加与搜索单词](211-design-add-and-search-words-data-structure.md) —— 支持 `.` 通配符
- **[212. 单词搜索 II](212-word-search-ii.md) —— Trie + 回溯

## 易错点

- 节点数组初始化为 null，访问前必须判空
- `search` 和 `startsWith` 的区别：前者要求 isEnd，后者不要求

## 面试追问

- **Trie 和哈希表做前缀匹配的优劣？** Trie 空间换时间，支持前缀匹配 O(L)；哈希表只能全词匹配

## 关联题

- 同套路：[211. 添加与搜索单词](211-design-add-and-search-words-data-structure.md)、[677. 键值映射](677-map-sum-pairs.md)

