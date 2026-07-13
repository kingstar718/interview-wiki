# 127. 单词接龙（Word Ladder）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里/腾讯

## 题目

给定开始单词 beginWord 和结束单词 endWord，以及字典 wordList。每次只能改变一个字母，找从 beginWord 到 endWord 的最短转换序列长度（每步转换后的单词必须在字典中）。

**示例**：
```
输入: beginWord = "hit", endWord = "cog",
     wordList = ["hot","dot","dog","lot","log","cog"]
输出: 5  （hit→hot→dot→dog→cog）
```

## 思路

**BFS 求最短路径**：每个单词看作图中的节点，两个单词相差一个字母则有边。BFS 从 beginWord 出发，逐层扩展，遇到 endWord 时返回层数。

**优化**：用 HashSet 存词表，每层遍历时对当前单词的每个位置尝试替换为 a-z 各字母，看是否在词表中。找到后从词表中删除（相当于 visited）。

**双向 BFS**：从 beginWord 和 endWord 同时 BFS，直到两个搜索相遇，进一步减少搜索空间。

## 代码

```java
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    Set<String> wordSet = new HashSet<>(wordList);
    if (!wordSet.contains(endWord)) return 0;
    Queue<String> q = new LinkedList<>();
    q.offer(beginWord);
    int level = 1;
    while (!q.isEmpty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            String word = q.poll();
            char[] chars = word.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char old = chars[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    chars[j] = c;
                    String next = new String(chars);
                    if (next.equals(endWord)) return level + 1;
                    if (wordSet.contains(next)) {
                        q.offer(next);
                        wordSet.remove(next);        // 相当于 visited
                    }
                }
                chars[j] = old;                      // 恢复
            }
        }
        level++;
    }
    return 0;
}
```

## 复杂度

- **时间**：O(N × L × 26)，N = 词表大小，L = 单词长度
- **空间**：O(N)

## 边界条件

- endWord 不在 wordList：返回 0
- beginWord == endWord：返回 1（题目保证不等）
- 无转换路径：返回 0

## 变式

- **[126. 单词接龙 II](https://leetcode.cn/problems/word-ladder-ii/)**：要求返回所有最短路径（BFS + DFS 回溯）
- **[433. 最小基因变化](https://leetcode.cn/problems/minimum-genetic-mutation/)**：同款 BFS，词表是基因序列

## 易错点

- 每次改变字符后要恢复原字符（`chars[j] = old`），否则下一个位置的修改基于错误字符
- 从词表中删除（`wordSet.remove(next)`）代替 visited Set，更简洁
- 遇到 endWord 返回 `level + 1`（当前层是 word，下一层才是 endWord）
- 双向 BFS 是加分项，面试中先写单向 BFS 再提双向优化

## 面试追问

- **双向 BFS 怎么做？** 两个 Set 分别从 beginWord 和 endWord 出发，每次扩展较小一侧，直到两个 Set 有交集
- **为什么 BFS 而不是 DFS？** 求最短路径——BFS 天然保证最短，DFS 需要遍历所有路径再比较

## 关联题

- 同套路：[126. 单词接龙 II](https://leetcode.cn/problems/word-ladder-ii/) —— 求所有最短路径
- 进阶：[433. 最小基因变化](https://leetcode.cn/problems/minimum-genetic-mutation/) —— 同款 BFS
- 知识点：BFS 最短路径 + 图建模见[图](algorithms/09-图/README.md)

---

[← 返回训练计划](社招算法训练计划.md)