---
topics:
  - 数组与字符串
techniques:
  - 双指针模拟
---

# 151. 反转字符串中的单词（Reverse Words in a String）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯/美团

## 题目

给定字符串，逐单词反转顺序，单词间由空格分隔，结果中多余空格（前导/尾随/连续）需去除，单词间只保留一个空格。

**示例**：
```
输入: "  hello   world  "
输出: "world hello"

输入: "the sky is blue"
输出: "blue is sky the"
```

## 思路

**三步反转法**（O(1) 额外空间）：① 去除多余空格（双指针原地压缩）；② 整个字符串反转；③ 逐个单词反转回来（遇到空格或末尾触发反转）。

也可用**双端队列**：分割单词后从队头入队实现逆序，更直观但需要 O(n) 额外空间。

## 代码

```java
public String reverseWords(String s) {
    char[] a = s.toCharArray();
    int n = a.length;

    // 1. 去除多余空格（原地压缩）
    int w = 0;
    for (int i = 0; i < n; i++) {
        if (a[i] != ' ') {
            if (w != 0) a[w++] = ' ';   // 单词间加一个空格
            while (i < n && a[i] != ' ')
                a[w++] = a[i++];
        }
    }
    // 此时 [0, w) 是压缩后的字符串

    // 2. 整个反转
    reverse(a, 0, w - 1);

    // 3. 逐个单词反转
    int start = 0;
    for (int i = 0; i <= w; i++) {
        if (i == w || a[i] == ' ') {
            reverse(a, start, i - 1);
            start = i + 1;
        }
    }
    return new String(a, 0, w);
}

private void reverse(char[] a, int l, int r) {
    while (l < r) {
        char tmp = a[l];
        a[l++] = a[r];
        a[r--] = tmp;
    }
}
```

## 复杂度

- **时间**：O(n) — 三步各遍历一次
- **空间**：O(n) — Java 中 toCharArray 创建新数组；若语言支持原地修改则是 O(1)

## 边界条件

- 空串 / 全空格：压缩后 `w == 0`，返回空串
- 单单词无空格：压缩后不变，反转整个再反转回来等于不变
- 连续多个空格：压缩成单个空格
- 前导/尾随空格：压缩时跳过

## 变式

- **[189. 轮转数组](189-rotate-array.md)** —— 三步反转法（整体反转 → 部分反转）的同一套路
- **[186. 反转字符串中的单词 II](https://leetcode.cn/problems/reverse-words-in-a-string-ii/)** —— 字符数组原地修改版，无多余空格，只需两步反转
- 字符串循环左移/右移：也属于三步反转法的变体

## 易错点

- **原地压缩时单词间加空格的时机**：`if (w != 0) a[w++] = ' '` 确保第一个单词前不加空格，后续单词前加一个空格——这个 `if` 容易漏。
- 整个反转 vs 单词反转的顺序不能颠倒：必须先整个反转再逐个单词反转（或者先单词反转再整个反转，两种顺序对应结果相反）。
- 单词反转的边界：遇到空格或走到末尾时触发反转，注意 `start` 的更新。

## 面试追问

- **能不能用 split + join 一行搞定？** Java 可以用 `String.join(" ", Arrays.asList(s.trim().split("\\s+")))` 一行，Collections.reverse 反转列表。面试中先给这个版本，再问 O(1) 空间的双指针原地版加分。
- **三步反转法还能用在哪些题？** 轮转数组（189）、字符串循环左移。核心套路："整体反转 → 部分反转"可以实现任意区间内的顺序调整。

## 关联题

- 同套路：[189. 轮转数组](189-rotate-array.md) —— 三步反转法相同模板
- 进阶：[186. 反转字符串中的单词 II](https://leetcode.cn/problems/reverse-words-in-a-string-ii/) —— 原地无多余空格版
- 知识点：双指针原地技巧见[数组与字符串](数组与字符串.md)
