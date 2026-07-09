# 720. 词典中最长的单词（Longest Word in Dictionary）

频次 ★★ · 难度 🟢 · 高频：快手

## 题目

字典中找最长单词，该单词可由字典中其他单词**每次加一个字母**构成。

## 思路

**Trie + BFS/DFS**：构建 Trie，DFS 找最深的、每步都是完整单词的路径。

**排序 + 哈希集**更简单：单词按长度排序，用 Set 存见过的前缀。

## 代码

```java
public String longestWord(String[] words) {
    Arrays.sort(words);
    Set<String> set = new HashSet<>();
    String res = "";
    for (String w : words) {
        if (w.length() == 1 || set.contains(w.substring(0, w.length() - 1))) {
            set.add(w);
            if (w.length() > res.length() || (w.length() == res.length() && w.compareTo(res) < 0))
                res = w;
        }
    }
    return res;
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)

