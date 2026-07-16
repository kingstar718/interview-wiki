---
topics:
  - 数组与字符串
techniques:
  - 模拟构造
---

# 459. 重复的子字符串（Repeated Substring Pattern）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

给定一个非空字符串 `s`，检查是否可以通过由它的一个子串重复多次构成。

**示例**：
```
输入: s = "abab"
输出: true
解释: 可由子串 "ab" 重复两次构成

输入: s = "aba"
输出: false
```

## 思路

三种解法，从易到难：

1. **枚举法**：枚举子串长度 `len`（`len` 必须是 `n` 的约数），判断 `s` 是否由长度为 `len` 的子串重复构成。O(n√n)。

2. **拼接法（巧妙）**：将两个 `s` 拼接成 `s + s`，去掉首尾字符后，如果 `s` 仍作为子串出现在其中，说明 `s` 可由子串重复构成。原理：如果 `s` 由子串 `p` 重复 k 次构成，则 `s + s` 去掉首尾后一定包含 `s`（从第 2 个 `p` 的第 1 个字符开始，到第 k 个 `p` 的最后一个字符结束）。

3. **KMP 的 next 数组**：若 `s` 由子串重复构成，则 `next[n-1]`（最长相等前后缀）满足 `n % (n - next[n-1]) == 0`，且 `next[n-1] > 0`。原理：重复子串的长度 = `n - next[n-1]`。

## 代码

```java
// KMP next 数组法（最优）
public boolean repeatedSubstringPattern(String s) {
    int n = s.length();
    int[] next = new int[n];
    // 构建 next 数组
    int j = 0;
    for (int i = 1; i < n; i++) {
        while (j > 0 && s.charAt(i) != s.charAt(j)) {
            j = next[j - 1];
        }
        if (s.charAt(i) == s.charAt(j)) {
            j++;
        }
        next[i] = j;
    }
    // 判断：next[n-1] > 0 且 n % (n - next[n-1]) == 0
    int len = n - next[n - 1];
    return next[n - 1] > 0 && n % len == 0;
}
```

```java
// 拼接法（最巧妙）
public boolean repeatedSubstringPattern(String s) {
    String doubled = s + s;
    return doubled.substring(1, doubled.length() - 1).contains(s);
}
```

## 复杂度

- **KMP 法**：时间 O(n) — 构建 next 数组一次遍历；空间 O(n) — next 数组
- **拼接法**：时间 O(n) — `contains` 内部实现通常是 KMP 或 Two-way；空间 O(n) — 拼接字符串
- **枚举法**：时间 O(n√n) — 枚举约数，每次比较 O(n)；空间 O(1)

## 边界条件

- `n = 1`：`next[0] = 0`，`next[n-1] > 0` 不成立，返回 `false`（单个字符不能由更短的子串重复构成）
- `s = "aaaaa"`：`next[n-1] = 4`，`len = 5 - 4 = 1`，`5 % 1 == 0`，返回 `true`
- `s = "abcab"`：`next[n-1] = 2`，`len = 5 - 2 = 3`，`5 % 3 != 0`，返回 `false`

## 变式

- **枚举子串长度**：从 `n/2` 向下枚举到 1，若 `n % len == 0` 且 `s` 的前 `len` 个字符重复 `n/len` 次等于 `s`，则返回 `true`
- **拼接法不含 KMP**：`contains` 依赖 JDK 实现（通常为 O(n)），面试中可以作为"巧妙解"提出，但 KMP 法更能展示对算法的理解

## 易错点

- 判断条件需要两个：`next[n-1] > 0`（排除没有公共前后缀的情况）AND `n % (n - next[n-1]) == 0`（长度能整除）。只判断 `n % (n - next[n-1]) == 0` 在 `s = "a"` 时 `next[0] = 0`，`len = 1 - 0 = 1`，`1 % 1 == 0` 会误判为 `true`
- 拼接法中 `substring` 去掉首尾字符的索引是 `[1, 2n-1)`，注意是去掉第一个和最后一个字符

## 面试追问

- **为什么 `n - next[n-1]` 就是最小重复子串的长度？** 如果 `s` 由子串重复构成，`next[n-1]` 是去掉最后一个重复子串后剩余部分的最长相等前后缀。重复子串长度 = `s` 总长度 - 单个重复子串被"吃掉"的最大前缀长度 = `n - next[n-1]`。
- **拼接法的原理是什么？** 如果 `s` 由子串 `p` 重复 k 次构成，则 `s = p^k`。`s + s = p^(2k)`，去掉首尾字符后变成 `p^(k-1) + p^(k-1) = p^(2k-2)`，其中必然包含 `p^k = s`（当 k ≥ 2 时）。如果 `s` 不能由子串重复构成，则 `s + s` 去掉首尾后不会包含 `s`。

## 关联题

- 基础：[28. 找出字符串中第一个匹配项的下标](28-find-the-index-of-the-first-occurrence-in-a-string.md) —— KMP 算法及 next 数组的构建过程
- 知识点：KMP 的 next 数组不仅用于字符串匹配，还能解决"重复子串"类问题，是模式匹配的经典数据结构
