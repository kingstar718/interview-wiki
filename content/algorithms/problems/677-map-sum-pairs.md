---
topics:
  - 字典树
techniques:
  - Trie前缀树
---

# 677. 键值映射（Map Sum Pairs）

频次 ★★★ · 难度 🟡 · 高频：百度

## 题目

实现类，`insert(key, val)` 和 `sum(prefix)` 返回所有以 prefix 开头的键的 val 之和。

## 思路

**Trie，但节点上存的不是「是不是单词结尾」，而是「以这条路径为前缀的所有 key 的 val 之和」。** 于是 `sum(prefix)` 只需沿着 prefix 走到底，读那个节点的累计和，O(|prefix|)。

关键在 `insert` 的**覆盖语义**：题目规定重复插入同一个 key 要**覆盖**旧值，而不是累加。所以沿路更新时加的必须是 `新值 - 旧值` 这个**差值**，旧值用一个 `HashMap` 单独记着。直接 `node.sum += val` 会让 `insert("apple",3)` 后再 `insert("apple",2)` 得到 5，而正确答案是 2。

## 代码

```java
class MapSum {
    private static class Node {
        Node[] next = new Node[26];
        int sum;                       // 以本节点路径为前缀的所有 key 的 val 之和
    }

    private final Node root = new Node();
    private final Map<String, Integer> vals = new HashMap<>();   // key -> 当前值,用来算差值

    public void insert(String key, int val) {
        int delta = val - vals.getOrDefault(key, 0);   // 覆盖语义 => 沿路加差值,不是加 val
        vals.put(key, val);
        Node node = root;
        for (char c : key.toCharArray()) {
            int i = c - 'a';
            if (node.next[i] == null) node.next[i] = new Node();
            node = node.next[i];
            node.sum += delta;
        }
    }

    public int sum(String prefix) {
        Node node = root;
        for (char c : prefix.toCharArray()) {
            node = node.next[c - 'a'];
            if (node == null) return 0;
        }
        return node.sum;
    }
}
```

## 复杂度

设 key 长度为 L，前缀长度为 P，插入次数为 n。

- **时间**：`insert` O(L)（哈希查旧值 O(L) 摊还 + 沿路更新 O(L)）；`sum` O(P)——**与 key 的数量无关**，这是 Trie 相对哈希表的全部优势
- **空间**：O(总字符数 × 26)

对比：哈希表存全部 key，`sum(prefix)` 要遍历所有 key 逐个判前缀，O(n × P)。

## 边界条件

- **重复插入同一个 key**：必须覆盖。差值可以是负数（`insert("a",5)` 后 `insert("a",2)`，delta = -3），沿路的 `sum` 会正确减小
- **`prefix` 不存在**：中途 `node` 为 `null`，返回 0
- **`prefix` 是空串**：返回所有 val 之和——本实现里 `root.sum` 恒为 0，若题目允许空前缀，要让 root 也参与累加
- **`sum` 的中断判断顺序**：先赋值 `node = node.next[...]` 再判 `null`，否则会对 `null` 解引用

## 变式

- **`sum` 要返回 key 的个数而非 val 之和**：节点上把 `sum` 换成 `count`，插入时新 key 才 `+1`
- **支持删除 key**：`insert(key, 0)` 即可（差值会把沿路的和减回去），但节点不会被回收；要真正回收得给节点加引用计数
- **[208. 实现 Trie](208-implement-trie.md)**：只判存在性，节点上存 `isEnd` 而非聚合值
- **前缀求 max 而非 sum**：max 不可增量维护（删除时无法回退），要么每次重算子树，要么节点上挂一个多重集合

## 易错点

- **`node.sum += val` 是错的，必须是 `+= delta`**。这是本题唯一的核心考点，也是 LeetCode 上这题最高频的 WA。题面里「will be overridden」那半句就是为它准备的
- 沿路更新时**不要更新 root**：`node = node.next[i]` 之后才 `node.sum += delta`，顺序反了会把 root 也算进去
- `sum` 里循环内先移动再判空；写成先判 `node.next[c-'a'] == null` 再移动也对，但两句都要有
- 节点上存的是**前缀和**不是**该节点结尾的 key 的值**——所以中间节点也有非零的 `sum`

## 面试追问

- **为什么这题非用 Trie 不可**：哈希表能存 key→val，但 `sum(prefix)` 要扫全表。**Trie 把「前缀」这个关系编码进了树的结构里**，前缀查询退化成一次路径下探。这正是[字典树](字典树.md)存在的理由——哈希表的键是原子的，Trie 的键是有内部结构的。
- **为什么要在节点上做聚合，而不是查询时遍历子树**：查询时遍历子树是 O(子树大小)，节点上预聚合是 O(1)。**这是又一次「预处理换查询」**，和前缀和是同一个交易：插入变慢（沿路 O(L) 更新），查询变快。
- **delta 这个技巧还能用在哪**：任何「可逆聚合」都能这么增量维护——sum、count、xor 都可以（有逆元）；max、min 不行（删除时无法回退）。**这是可撤销聚合与不可撤销聚合的分界线**，线段树的懒标记、数据库的物化视图增量刷新都受同一条限制。
- **并发下怎么办**：沿路更新不是原子的，`sum` 可能读到半更新状态。要么整棵树一把锁，要么按路径加锁（自顶向下的 hand-over-hand locking），代价是每个节点一把锁。

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)

