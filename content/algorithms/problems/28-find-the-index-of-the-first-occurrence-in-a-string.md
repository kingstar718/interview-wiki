---
topics:
  - 数组与字符串
techniques:
  - 模拟构造
---

# 28. 找出字符串中第一个匹配项的下标（Find the Index of the First Occurrence in a String）

频次 ★★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

给定两个字符串 `haystack` 和 `needle`，在 `haystack` 中找出 `needle` 的第一个匹配项的下标（从 0 开始）。如果 `needle` 不是 `haystack` 的一部分，返回 -1。

**示例**：
```
输入: haystack = "sadbutsad", needle = "sad"
输出: 0
解释: "sad" 在下标 0 和 6 处匹配，第一个匹配项的下标是 0

输入: haystack = "leetcode", needle = "leeto"
输出: -1
```

## 思路

KMP 算法的核心是**利用已匹配的部分信息避免回退**——当匹配失败时，主串指针 `i` 不回溯，模式串指针 `j` 根据 `next` 数组（前缀表）跳到已匹配前缀的下一个位置继续匹配。

- **暴力法**：O(n*m)，每次失配主串回退 1 个位置，模式串从头开始
- **KMP**：O(n+m)，预处理模式串的 `next` 数组（最长相等前后缀），匹配时 `i` 不回溯

面试中建议先讲暴力法，再引出 KMP 优化。

## 代码

```java
// KMP 算法
public int strStr(String haystack, String needle) {
    int n = haystack.length(), m = needle.length();
    if (m == 0) {
        return 0;
    }
    // 构建 next 数组（前缀表）
    int[] next = buildNext(needle);
    // 匹配
    int j = 0;
    for (int i = 0; i < n; i++) {
        while (j > 0 && haystack.charAt(i) != needle.charAt(j)) {
            j = next[j - 1]; // 回退到前缀的下一个位置
        }
        if (haystack.charAt(i) == needle.charAt(j)) {
            j++;
        }
        if (j == m) {
            return i - m + 1; // 找到匹配
        }
    }
    return -1;
}

private int[] buildNext(String pattern) {
    int m = pattern.length();
    int[] next = new int[m];
    int j = 0; // j 表示当前已匹配的前缀长度
    for (int i = 1; i < m; i++) {
        while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
            j = next[j - 1];
        }
        if (pattern.charAt(i) == pattern.charAt(j)) {
            j++;
        }
        next[i] = j;
    }
    return next;
}
```

```java
// 暴力法（面试先讲这个）
public int strStr(String haystack, String needle) {
    int n = haystack.length(), m = needle.length();
    for (int i = 0; i <= n - m; i++) {
        int j = 0;
        while (j < m && haystack.charAt(i + j) == needle.charAt(j)) {
            j++;
        }
        if (j == m) {
            return i;
        }
    }
    return -1;
}
```

## 复杂度

- **时间**：KMP O(n + m)；暴力 O(n * m)
- **空间**：KMP O(m)（next 数组）；暴力 O(1)

## 边界条件

- `needle` 为空字符串：根据题目约定返回 0
- `haystack` 长度 < `needle` 长度：不可能匹配，暴力法 `i <= n - m` 为负值，循环不执行，返回 -1
- `needle` 与 `haystack` 完全相同：返回 0

## 变式

- **Boyer-Moore 算法**：从右向左匹配，利用坏字符和好后缀规则跳转，平均性能优于 KMP，但实现更复杂
- **Rabin-Karp 算法**：用滑动哈希（Rolling Hash）在 O(n) 时间内匹配，适合多模式串匹配场景

## 易错点

- `buildNext` 中 `i` 从 1 开始（`next[0]` 默认为 0），不是从 0 开始
- 匹配失败时 `j = next[j - 1]` 而不是 `j = next[j]`——`next[j-1]` 表示 `j-1` 位置的最长相等前后缀长度，即回退后应该对齐的位置
- 暴力法循环条件 `i <= n - m`，注意是 `<=` 不是 `<`

## 面试追问

- **KMP 的 next 数组为什么叫"前缀表"？** next[i] 表示模式串 `[0..i]` 这个子串中，最长相等前后缀的长度。当在 `j` 处失配时，`next[j-1]` 告诉我们"已匹配的前缀中，后缀与前缀相等的长度"，也就是模式串可以跳过多少字符。
- **KMP 和 BM、RK 怎么选？** KMP 理论上 O(n+m) 稳定，BM 实际中常更快（跳跃更大），RK 适合多模式串。面试中 KMP 考得最多，因为它是"利用已匹配信息"这一思想的经典体现。

## 关联题

- 同套路：[459. 重复的子字符串](459-repeated-substring-pattern.md) —— KMP 的 next 数组应用，判断字符串是否由子串重复构成
- 知识点：字符串匹配算法族——KMP（前缀表）/ BM（右向左）/ RK（哈希滚动）各自的核心思想
