---
topics:
  - 哈希表
techniques:
  - 哈希查表
---

# 242. 有效的字母异位词（Valid Anagram）

频次 ★★★ · 难度 🟢 · 高频：字节/腾讯/美团

## 题目

给定两个字符串 `s` 和 `t`，判断 `t` 是否是 `s` 的字母异位词（字母和出现次数相同，顺序可以不同）。

**示例**：
```
输入: s = "anagram", t = "nagaram"
输出: true

输入: s = "rat", t = "car"
输出: false
```

## 思路

**计数法**：用长度为 26 的 int 数组（或 HashMap）统计 `s` 中每个字母出现次数，然后遍历 `t` 逐个减计数，如果出现负数则不是异位词。

也可以用**排序法**：将两个字符串排序后比较是否相等，但排序 O(n log n) 比计数法 O(n) 慢。

## 代码

```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;

    int[] count = new int[26];
    for (char c : s.toCharArray()) {
        count[c - 'a']++;
    }
    for (char c : t.toCharArray()) {
        count[c - 'a']--;
        if (count[c - 'a'] < 0) {
            return false;
        }
    }
    return true;
}
```

## 复杂度

- **时间**：O(n) — 两个字符串各遍历一次
- **空间**：O(1) — 固定大小 26 的数组（如果字符集扩大为 Unicode 则用 HashMap，空间 O(k)）

## 边界条件

- 长度不同：一定不是异位词，直接返回 `false`。
- 空串：两个空串互为异位词，返回 `true`。
- 包含大写字母：题目假设只有小写字母；如果包含大写，需要先统一 `toLowerCase` 或使用更大范围的计数数组 / HashMap。

## 变式

- **包含 Unicode 字符**：用 `HashMap<Character, Integer>` 而不是固定数组。
- **字母异位词分组**（[49. 字母异位词分组](49-group-anagrams.md)）：同样的异位词判定思路扩展为批量聚类。
- **字符串排列**（[567. 字符串的排列](567-permutation-in-string.md)）：异位词 + 滑动窗口，判断 s2 是否包含 s1 的某个排列。

## 易错点

- 字符转索引时用 `c - 'a'` 而不是 `(int) c`（后者是 ASCII 码）。
- 长度不等时提前返回 false，避免数组越界。
- 用计数数组时，`t` 遍历中一旦出现负数应立即返回 false，不必等到全部减完（因为长度相等，如果最后有负数说明某字母出现次数不一致）。

## 面试追问

- **计数法和排序法各自的优劣是什么？** 计数法 O(n) 时间 O(1) 空间（有限字符集），更优；排序法 O(n log n) 时间 O(1) 空间（原地排序，如快排）或 O(n) 空间（归并排序）。在字符集有限时优先给计数法。
- **如果字符集非常大（如 Unicode 全量），计数法怎么调整？** 用 `HashMap<Character, Integer>` 按需统计，空间 O(k)（k 为不同字符数），时间仍为 O(n)。

## 关联题

- 同套路：[49. 字母异位词分组](49-group-anagrams.md)（批量版）
- 进阶：[438. 找到字符串中所有字母异位词](438-find-all-anagrams-in-a-string.md)（异位词 + 滑动窗口）
- 易混：[567. 字符串的排列](567-permutation-in-string.md)（判断子串包含性）
- 知识点：哈希计数模板见[哈希表](哈希表.md)
