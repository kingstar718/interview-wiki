---
topics:
  - 字典树
---

# 677. 键值映射（Map Sum Pairs）

频次 ★★★ · 难度 🟡 · 高频：百度

## 题目

实现类，`insert(key, val)` 和 `sum(prefix)` 返回所有以 prefix 开头的键的 val 之和。

## 思路

**Trie**：每个节点存该前缀对应的所有键的和。插入时每层更新和值。

## 代码

```java
class MapSum {
    private MapSum[] next = new MapSum[26];
    private int val;

    public void insert(String key, int val) {
        MapSum node = this;
        for (char c : key.toCharArray()) {
            int idx = c - 'a';
            if (node.next[idx] == null) node.next[idx] = new MapSum();
            node = node.next[idx];
            node.val += val;
        }
    }

    public int sum(String prefix) {
        MapSum node = this;
        for (char c : prefix.toCharArray()) {
            node = node.next[c - 'a'];
            if (node == null) return 0;
        }
        return node.val;
    }
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[208. 实现 Trie](208-implement-trie.md)

