---
topics:
  - 字典树
techniques:
  - Trie前缀树
---

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

- **全是 `.` 的查询**（如 `"...."`）：退化成「有没有长度为 4 的单词」，会把该层所有分支都走一遍，是最坏情况
- **`.` 在首位**：从 root 的 26 个孩子逐个试，没有任何剪枝空间
- **搜索空串**：`i == word.length()` 立即成立，返回 root 的 `isEnd`（为 false，因为空串没被 add 过）
- **`.` 匹配的是「任意一个字符」，不是「任意多个字符」**：长度必须严格相等，所以 `i == word.length()` 时不能提前返回 true，得看 `isEnd`

## 变式

- **[208. 实现 Trie](208-implement-trie.md)**：无通配符版本，`search` 退化成一条路径下探
- **`*` 匹配任意多个字符**：`.` 只吃一个字符所以长度确定；`*` 会让长度不确定，退化成 [10. 正则表达式匹配](10-regular-expression-matching.md) 那种二维 DP
- **限制通配符个数 ≤ k**：可以在 DFS 里带上剩余通配符预算做剪枝
- **大小写/数字**：26 叉数组换成 `HashMap<Character, Node>`

## 易错点

- **`i == word.length()` 时返回的是 `isEnd` 而不是 `true`**。走到路径末尾不代表这是个完整单词——`add("apple")` 之后 `search("app")` 必须返回 false
- **遇到 `.` 时只枚举非空孩子**，`child != null` 的判断不能少；且一旦有一个分支返回 true 就立即返回，不要继续枚举
- 非通配符分支要先判 `next[idx] != null` 再递归，否则空指针
- `.` 的递归里传的是 `i + 1`，不是 `i`——它消耗掉一个字符

## 面试追问

- **最坏复杂度是多少**：查询全是 `.` 时，每一层都要展开 26 个分支，`O(26^L)`。实际上受限于树中真实存在的节点数，上界是 O(树的节点总数)。**所以真正的界是 `min(26^L, 节点数)`。**
- **怎么优化通配符查询**：按单词长度分桶——`Map<Integer, Trie>`，长度为 L 的单词进第 L 棵树。查询 `"...."` 时只在长度为 4 的那棵树里搜，直接砍掉大量分支。
- **和正则引擎有什么关系**：这就是一个只支持 `.` 的极简正则匹配器，且模式串是查询、文本是 Trie 里的所有单词。**Trie 上做 DFS ≈ 把 NFA 的状态集合走一遍**。完整的正则要处理 `*` 的不确定长度，那就得上 DP 或 NFA 模拟。
- **为什么不用哈希表 + 逐个正则匹配**：那是 O(单词数 × L)。Trie 的优势是**共享前缀**——一次分支失败就同时排除了该前缀下的所有单词，见[字典树](字典树.md)。

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)、进阶：[212. 单词搜索 II](212-word-search-ii.md)

