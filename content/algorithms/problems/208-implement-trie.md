---
topics:
  - 字典树
techniques:
  - Trie前缀树
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

## 26 叉的由来 + search/startsWith 一行之差（字典树组 2/2 收官）

```
用户零提示直落（灵神同款确认）：Node[26] 槽位 + isEnd；三方法共用 walk，差异收敛最后一句
  insert：缺路铺路→走完立 isEnd；search：路在+isEnd 双条件；startsWith：路在单条件
26 的含义（用户追问）：小写字母集大小=分支因子；c-'a' 把字母换槽位下标；
  只有走过的槽位才建节点（banana 的 'b' 槽 null → startsWith 一步返回 false）
数组 vs Map 孩子的判据：字符集小且固定用数组（O(1) 常数小，稀疏浪费可忍）；
  Unicode/稀疏集用 Map（省内存换速度）——面试口头加分句
isEnd 的灵魂：路径存在≠完整单词存在（app/apple 案例：存 apple 时 app 路在但 isEnd 假）
骨架口径：操作序列型骨架 lint 适配（RefBrute 参考类+步数校验+逐步对账输出）——
  146/155 手工适配先例的通用化（本次改 skeleton_lint.py 本体）
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

