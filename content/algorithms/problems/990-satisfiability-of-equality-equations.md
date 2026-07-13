---
topics:
  - 图论
techniques:
  - 并查集
---

# 990. 等式方程的可满足性（Satisfiability of Equality Equations）

频次 ★★★ · 难度 🟡 · 高频：阿里

## 题目

字符串数组 equations，如 `"a==b"`、`"a!=b"`，判断是否所有等式同时成立。

**示例**：
```
输入: ["a==b","b!=a"]
输出: false
输入: ["a==b","b==c","a==c"]
输出: true
```

## 思路

**并查集 + 两趟扫描**：

1. 第一趟处理所有 `==` 等式，将两边变量 union
2. 第二趟检查所有 `!=` 不等式，如果两边的变量在同一集合中则矛盾

## 代码

```java
public boolean equationsPossible(String[] equations) {
    int[] parent = new int[26];
    for (int i = 0; i < 26; i++) parent[i] = i;

    for (String s : equations) {
        if (s.charAt(1) == '=') {
            int a = s.charAt(0) - 'a', b = s.charAt(3) - 'a';
            union(parent, a, b);
        }
    }

    for (String s : equations) {
        if (s.charAt(1) == '!') {
            int a = s.charAt(0) - 'a', b = s.charAt(3) - 'a';
            if (find(parent, a) == find(parent, b)) return false;
        }
    }
    return true;
}

private int find(int[] parent, int x) {
    if (parent[x] != x) parent[x] = find(parent, parent[x]);
    return parent[x];
}

private void union(int[] parent, int a, int b) {
    parent[find(parent, a)] = find(parent, b);
}
```

## 复杂度

- **时间**：O(n α(26)) ≈ O(n)
- **空间**：O(26)

## 边界条件

- "a!=a"：必然 false（自反性矛盾）

## 变式

- **[684. 冗余连接](684-redundant-connection.md)**：图上并查集检测环
- 约束传播：并查集用于判断不等约束的可行性

## 易错点

- 不能在同一次循环中同时处理 == 和 !=（"=" 还没 union 完就判断 "!=" 会漏掉传递不等）
- 变量是小写字母，范围 26
- 字符取下标用 `s.charAt(0) - 'a'`

## 面试追问

- **如果变量不止 26 个字母呢？** 用 HashMap 做并查集，key 为变量名

## 关联题

- 同套路：[547. 省份数量](547-number-of-provinces.md) —— 基础并查集
- 进阶：[684. 冗余连接](684-redundant-connection.md) —— 图环检测
- 知识点：并查集解决约束可满足性问题见[并查集](图论.md)

