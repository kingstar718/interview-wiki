---
topics:
  - 字典树
techniques:
  - Trie前缀树
---

# 648. 单词替换（Replace Words）

频次 ★★ · 难度 🟡 · 高频：美团

## 题目

字典 dictionary（词根），句子中每个单词替换为它的词根（最短匹配）。

## 思路

**Trie**：构建词根 Trie，对句子每个单词在 Trie 中找最短前缀。

## 代码

```java
public String replaceWords(List<String> dict, String sentence) {
    Trie root = new Trie();
    for (String w : dict) {
        Trie node = root;
        for (char c : w.toCharArray()) {
            int idx = c - 'a';
            if (node.next[idx] == null) node.next[idx] = new Trie();
            node = node.next[idx];
        }
        node.isEnd = true;
    }

    String[] words = sentence.split(" ");
    for (int i = 0; i < words.length; i++) {
        Trie node = root;
        StringBuilder sb = new StringBuilder();
        for (char c : words[i].toCharArray()) {
            int idx = c - 'a';
            if (node.next[idx] == null) break;
            sb.append(c);
            node = node.next[idx];
            if (node.isEnd) { words[i] = sb.toString(); break; }
        }
    }
    return String.join(" ", words);
}

class Trie {
    Trie[] next = new Trie[26];
    boolean isEnd;
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)

