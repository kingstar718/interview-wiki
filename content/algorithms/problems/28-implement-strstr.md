---
topics:
  - 数组与字符串
techniques:
  - 滑动窗口
---

# 28. 实现 strStr()（Implement strStr()）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

实现 `strStr(haystack, needle)`，返回 `needle` 在 `haystack` 中首次出现的索引，如果不存在则返回 -1。如果 `needle` 为空字符串，返回 0。

**示例**：
```
输入: haystack = "hello", needle = "ll"
输出: 2

输入: haystack = "aaaaa", needle = "bba"
输出: -1

输入: haystack = "", needle = ""
输出: 0
```

## 思路

**滑动窗口（逐位匹配）**：遍历 `haystack` 到 `haystack.length() - needle.length()`，对每个起始位置检查接下来的 `needle.length()` 个字符是否匹配。

也可以用 KMP 算法优化到 O(m+n)，但对应届生面试通常先给出滑动窗口再讨论优化。

## 代码

```java
public int strStr(String haystack, String needle) {
    if (needle == null || needle.isEmpty()) return 0;

    int n = haystack.length(), m = needle.length();
    for (int i = 0; i <= n - m; i++) {
        int j = 0;
        while (j < m && haystack.charAt(i + j) == needle.charAt(j)) {
            j++;
        }
        if (j == m) return i;
    }
    return -1;
}
```

## 复杂度

- **时间**：最坏 O(n × m) — 滑动窗口法；KMP 可优化到 O(n + m)
- **空间**：O(1)

## 边界条件

- `needle` 为空串：返回 0（按题目约定）。
- `needle` 比 `haystack` 长：循环条件 `i <= n - m` 为假，直接返回 -1。
- `haystack` 为空且 `needle` 非空：返回 -1。
- 完全匹配：`haystack = "abc", needle = "abc"`，`i = 0` 时匹配成功。

## 变式

- **KMP 算法**：通过预处理 `needle` 的 next 数组（最长相等前后缀），在主串匹配失败时跳过多余的比较。适合 `haystack` 很长且 `needle` 有重复模式时。
- **Rabin-Karp 算法**：用滚动哈希将字符串比较转为数值比较，期望时间 O(n + m)。
- **实现 `contains` / `indexOf`**：本题的返回值本质就是 `indexOf` 的语义。

## 易错点

- 循环上界是 `i <= n - m`，不是 `i < n - m`，等号要取到（当 `haystack` 和 `needle` 等长时）。
- `needle.isEmpty()` 要返回 0，但 `haystack` 为空时也返回 0（因为题目定义空串在任何位置都匹配）。
- 字符比较用 `charAt` 而不是 `substring` + `equals`（后者每次创建新字符串，O(m) 空间和时间）。

## 面试追问

- **KMP 的关键思想是什么？** 利用模式串自身的重复结构（最长公共前后缀），在匹配失败时让模式串跳到下一个可能匹配的位置，而不是从主串的下一个字符重新开始。虽然 KMP 很难在面试现场写准，但能清晰地说出它与滑动窗口的本质区别就足够。
- **除了 KMP，还有没有别的优化方法？** Rabin-Karp（滚动哈希）、Boyer-Moore（从右匹配，跳过更多字符）、Z 算法。一般指出 Rabin-Karp 的思路（哈希碰撞处理）即可体现知识广度。

## 关联题

- 同套路：KMP 的 next 数组构建是"自匹配"思想
- 进阶：[3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md)（滑动窗口的另一类应用）
- 易混：`contains` / `indexOf` 库函数的实现（各语言差异）
- 知识点：字符串匹配与 KMP 见[数组与字符串](数组与字符串.md)
