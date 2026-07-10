---
topics:
  - 字典树
techniques:
  - Trie前缀树
  - 哈希查表
---

# 720. 词典中最长的单词（Longest Word in Dictionary）

频次 ★★ · 难度 🟢 · 高频：快手

## 题目

字典中找最长单词，该单词可由字典中其他单词**每次加一个字母**构成。若有多个答案，返回**字典序最小**的那个。

**示例**：
```
输入: ["w","wo","wor","worl","world"]
输出: "world"

输入: ["a","banana","app","appl","ap","apply","apple"]
输出: "apple"   // "apply" 同样合法，但 "apple" 字典序更小
```

## 思路

题目的约束是**每一个前缀都必须是词典里的完整单词**。所以真正要判断的不是「这个词在不在词典里」，而是「从它的第一个字母开始，每一步截断都在词典里」。

**解法一：Trie + DFS**。把所有单词插入 Trie，然后从根出发 DFS，**只沿着 `isEnd == true` 的孩子往下走**——这条限制直接把「每步都是完整单词」编码进了遍历规则里。走得最深的那条路径就是答案。按 `a..z` 顺序遍历孩子，天然保证同深度时先到达的是字典序最小的。

**解法二：排序 + 哈希集**。把单词按字典序排序后从前往后扫，此时任一单词的前缀（若存在）必然已被处理过。用 `Set` 存已确认合法的单词，检查 `w` 去掉末位字符后是否在 `Set` 里即可。

两种解法的分工很典型：Trie 把「前缀」这个概念做成了数据结构本身，而排序 + 哈希集是用「排序保证前缀先到」这个性质，绕开了显式的前缀结构。**如果题目继续追加前缀相关的查询（有多少个词以 x 开头），Trie 能扩展，哈希集不能。**

## 代码

**Trie + DFS**：

```java
class Solution {
    private static class Node {
        Node[] next = new Node[26];
        boolean isEnd;
    }

    private String res = "";

    public String longestWord(String[] words) {
        Node root = new Node();
        for (String w : words) {          // 建 Trie
            Node cur = root;
            for (char c : w.toCharArray()) {
                int i = c - 'a';
                if (cur.next[i] == null) cur.next[i] = new Node();
                cur = cur.next[i];
            }
            cur.isEnd = true;
        }
        dfs(root, new StringBuilder());
        return res;
    }

    private void dfs(Node node, StringBuilder path) {
        if (path.length() > res.length()) res = path.toString();  // 同长时不覆盖 => 字典序最小胜出
        for (int i = 0; i < 26; i++) {                            // a..z 顺序 => 先到者字典序最小
            Node child = node.next[i];
            if (child == null || !child.isEnd) continue;          // 只走「本身也是完整单词」的孩子
            path.append((char) ('a' + i));
            dfs(child, path);
            path.deleteCharAt(path.length() - 1);
        }
    }
}
```

**排序 + 哈希集**：

```java
public String longestWord(String[] words) {
    Arrays.sort(words);                   // 字典序排序 => 前缀必在本词之前出现
    Set<String> set = new HashSet<>();
    String res = "";
    for (String w : words) {
        if (w.length() == 1 || set.contains(w.substring(0, w.length() - 1))) {
            set.add(w);
            if (w.length() > res.length()) res = w;   // 排序后同长必是先到者更小
        }
    }
    return res;
}
```

## 复杂度

设 n 为单词数，L 为单词平均长度，总字符数 `S = n × L`。

| 解法 | 时间 | 空间 |
|---|---|---|
| Trie + DFS | O(S) 建树 + O(S) 遍历 | O(S × 26) 最坏，节点数最多 S |
| 排序 + 哈希集 | O(n L log n) 排序 + O(S) 扫描 | O(S) |

Trie 版**不需要排序**，所以时间上更优；代价是 26 叉数组带来的常数级空间浪费（可换成 `HashMap<Character, Node>` 省空间但常数变大）。

## 边界条件

- **空数组**：返回 `""`
- **没有任何单词满足条件**（如 `["abc"]`，因为 `"ab"`、`"a"` 都不在词典里）：返回 `""`。Trie 版靠「只走 `isEnd` 的孩子」自然处理——根的孩子 `a` 不存在或 `isEnd == false`，DFS 直接不下探
- **多个等长答案**：返回字典序最小。Trie 版靠 `a..z` 的遍历顺序 + 严格 `>` 比较；排序版靠 `Arrays.sort` 后先到先得
- **单字母单词**：长度为 1 时前缀为空串，直接视为合法起点（排序版的 `w.length() == 1` 分支）

## 变式

- **[648. 单词替换](648-replace-words.md)**：给句子里每个词找词典中最短的前缀替换——Trie 上走到第一个 `isEnd` 就返回
- **[208. 实现 Trie](208-implement-trie.md)**：本题的基础设施
- **返回所有满足条件的最长单词**：DFS 时用列表收集所有等长最深路径
- **词典可动态增删**：排序 + 哈希集的方案失效（每次插入都要重排），Trie 天然支持增量插入

## 易错点

- **`res` 的更新用严格 `>` 而不是 `>=`**。Trie 版按 `a..z` 遍历，等长时先到的字典序更小，用 `>=` 会被后来的更大者覆盖。这是本题唯一的坑
- **DFS 时不能走 `isEnd == false` 的孩子**。漏掉这个判断会把 `["a","banana"]` 里的 `"banana"` 当成合法答案，而 `"b"`、`"ba"` 根本不在词典里
- **根节点不做 `isEnd` 检查**。根代表空串，它不是词典里的单词，但每个单字母单词的父节点就是它——所以是「检查孩子的 `isEnd`」而不是「检查自己的 `isEnd`」
- 排序版里 `w.substring(0, w.length() - 1)` 每次都建新字符串，L 很大时开销可观；Trie 版没有这个问题

## 面试追问

- **为什么排序能替代 Trie**：字典序排序保证了任一单词的所有前缀都排在它前面（`"app" < "appl" < "apple"`）。这是**用排序换掉显式前缀结构**，和「前缀和预处理换查询」是同一类思想——把某个性质提前固化，后续查询就能 O(1) 判断。
- **什么时候排序法就不行了**：一旦词典要动态增删，或者追加「有多少个词以 x 开头」这类前缀聚合查询，排序法就崩了——每次变更都要重排。Trie 支持 O(L) 增量插入，且每个节点挂个计数就能回答前缀聚合。**这就是为什么这题归在字典树套路下，尽管排序法代码更短。**
- **Trie 的 26 叉数组会不会太浪费**：会。稀疏词典下大量 `null` 指针，可以换 `HashMap<Character, Node>`（省空间、常数变大）或双数组 Trie / 三向单词查找树（工业级方案）。Redis 的 `rax`、Elasticsearch 的 FST 都是压缩前缀树的变体，见 [Elasticsearch](Elasticsearch.md)。
- **和哈希表比，Trie 到底赢在哪**：哈希表的键是「整体」，查 `"apple"` 就只能得到 `"apple"`；Trie 的路径把「前缀」显式化了，任何前缀语义的查询都是一次下探。**代价是空间——Trie 用结构换查询能力**，见[字典树](字典树.md)。

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md) —— 本题的数据结构基础
- 同套路：[648. 单词替换](648-replace-words.md) —— 同样是 Trie 上找 `isEnd` 节点
- 知识点：Trie 为什么能解决哈希表解决不了的前缀查询，见[字典树](字典树.md)
