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

设词根总长度为 D，句子总长度为 S。

- **时间**：建 Trie O(D) + 逐词匹配 O(S)，合计 O(D + S)。**与词根的数量无关**
- **空间**：O(D × 26)

哈希表做法要枚举每个单词的所有前缀去查表，是 O(S × L)（L 为单词平均长度），且要把所有前缀都造成字符串。

## 边界条件

- **单词没有任何词根匹配**：`node.next[idx] == null` 时 `break`，`words[i]` 保持原样
- **词根就是单词本身**：走到最后一个字符时 `isEnd` 为 true，替换成它自己，结果不变
- **多个词根都匹配**（`["a", "aa"]` 对 `"aaa"`）：题目要**最短**的，所以一遇到 `isEnd` 就立即 `break`——这是本题唯一的贪心点
- **空词根表**：Trie 只有 root，所有单词原样返回
- **句子只有一个空格分隔**：题目保证，否则 `split(" ")` 会产生空串

## 变式

- **要最长的词根**：不能一遇到 `isEnd` 就停，得一直走到底并记录最后一个 `isEnd` 的位置
- **[720. 词典中最长的单词](720-longest-word-in-dictionary.md)**：同样在 Trie 上找 `isEnd`，但要求路径上每一步都是 `isEnd`
- **词根带通配符**：退化成 [211. 添加与搜索单词](211-design-add-and-search-words-data-structure.md) 的 DFS
- **大小写不敏感 / 支持标点**：预处理归一化，或把 26 叉数组换成 `HashMap`

## 易错点

- **找到 `isEnd` 必须立刻 `break`**，否则会继续往下匹配到更长的词根，违反「最短」要求
- `sb.append(c)` 要在 `node = node.next[idx]` **之后**、判 `isEnd` **之前**——顺序错了会漏掉或多带一个字符
- `node.next[idx] == null` 时 break，此时 `words[i]` 不能被 `sb` 覆盖（`sb` 是不完整的前缀）
- 别用 `sentence.replace(词根, ...)` 做字符串替换：会把单词中间的子串也换掉

## 面试追问

- **为什么是「最短前缀」而不是「最长前缀」**：题目要求如此，但更有意思的是**两者在 Trie 上的代价不同**。最短前缀一遇到 `isEnd` 就停，平均只走几层；最长前缀必须走到失配为止。前者是「短路求值」，后者不是。
- **和路由表的最长前缀匹配（LPM）什么关系**：IP 路由查表就是在前缀树上找**最长**匹配（`10.0.0.0/8` 和 `10.1.0.0/16` 同时命中时选后者）。硬件路由器用的 TCAM、软件里的 LC-Trie，都是这题的工业版本，只不过基数是 2 而不是 26。
- **词根表非常大、句子很短，怎么办**：Trie 的建树成本 O(D) 就成了瓶颈。若词根表固定、句子流式到来，Trie 建一次复用；若反过来，直接枚举句子每个单词的前缀去哈希表里查更划算——**O(D) vs O(S×L)，看哪个大。**
- **能不能用 AC 自动机**：能，但杀鸡用牛刀。AC 自动机解决的是「在长文本里找所有模式串出现位置」，这题的模式串只需要在**单词开头**匹配，不需要 fail 指针跳转。见[字典树](字典树.md)。

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)

