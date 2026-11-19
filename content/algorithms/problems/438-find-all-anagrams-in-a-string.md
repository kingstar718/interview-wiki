---
topics:
  - 双指针与滑动窗口
techniques:
  - 滑动窗口
---

# 438. 找到字符串中所有字母异位词（Find All Anagrams in a String）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

给定字符串 s 和 p，找出 s 中所有 p 的**字母异位词子串**的起始下标。

**示例**：
```
输入: s = "cbaebabacd", p = "abc"
输出: [0, 6]    （"cba"、"bac"）
```

## 思路

**定长滑动窗口 + 计数匹配**：窗口长度固定为 `p.length()`，维护窗口内 26 字母计数；每右移一步"进一个字符、出一个字符"，比较窗口计数与 p 的计数。

优化比较：维护 `valid`（计数完全匹配的字母种数），进出字符时增量更新，避免每步 O(26) 全比（全比也能过，胜在好写）。

## 代码

```java
public List<Integer> findAnagrams(String s, String p) {
    List<Integer> res = new ArrayList<>();
    if (s.length() < p.length()) return res;
    int[] need = new int[26], win = new int[26];
    for (char c : p.toCharArray()) need[c - 'a']++;
    int k = p.length();
    for (int i = 0; i < s.length(); i++) {
        win[s.charAt(i) - 'a']++;              // 进
        if (i >= k) win[s.charAt(i - k) - 'a']--; // 出
        if (i >= k - 1 && Arrays.equals(win, need)) res.add(i - k + 1);
    }
    return res;
}
```

## 灵神定长滑窗模板（外部参考规矩首题·二阶段 11/50 实测）

```
按新规矩参考灵茶山艾府（0x3f）题解：单数组差值画像 cnt = p的字母 − 窗口字母
进：cnt[c]--；cnt[c]<0 → while 收缩左端（超配字母自我纠正）
等长判定 i-left+1 == m → 必是异位词（差值画像归零——无 equals 全表比对）
滑走：res.add(left); cnt[s[left++]]++
亮点：p>s 短路被不变式吃掉（窗口长 ≤ s长 < p长，等长判定天然不触发）
每步 O(1)、无 26 全比——比 need/win 双数组全比版（O(26)/步）高半档
国际区对照：anton4 #92015（负数 hash 区分 p 字母与路人 + count 计缺——同"增量家族"）
本库先例同族：560 有负数不能滑窗（438 无负数滑窗正好上场——姊妹题对仗）
```

## 复杂度

- **时间**：O(n × 26) —— 每步一次数组比较；valid 计数版可到 O(n)
- **空间**：O(26)

## 边界条件

- `s` 比 `p` 短：直接返回空
- `s == p`：输出 [0]
- p 含重复字母：计数天然处理，无需特判

## 变式

- [567. 字符串的排列](567-permutation-in-string.md)：同一份代码把"收集所有下标"改成"找到一个就返回 true"
- 字符集扩大到 Unicode：`int[26]` 换 HashMap + valid 计数

## 易错点

- 出窗时机：`i >= k` 时移出 `i - k`；判定时机：`i >= k - 1`——两个条件容易混
- 用排序做每个窗口的规范化是 O(n·k log k)，退化解法，面试要主动说计数版
- `Arrays.equals` 比较的是内容——这里数组反而比 HashMap 好用（对比 [49. 字母异位词分组](49-group-anagrams.md) 里"数组不能当 key"）

## 面试追问

- **为什么这题窗口定长，而最长子串那题变长？** 约束不同：异位词长度必须等于 p，合法窗口大小唯一；求最长/最短才需要伸缩。先判断窗口是定长还是变长，是滑窗题的第一步
- **怎么把每步比较从 O(26) 降到 O(1)？** 维护"已匹配字母种数 valid"：进出字符只影响一个字母桶，桶从不匹配变匹配 valid++，反向 valid--，判定 `valid == 26`（或出现过的种数）

## 关联题

- 同套路：[567. 字符串的排列](567-permutation-in-string.md) —— 同题不同问法；[3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md) —— 变长窗口
- 进阶：[76. 最小覆盖子串](76-minimum-window-substring.md) —— 从"恰好等于"放宽为"包含即可"，窗口变长
- 知识点：与 [49. 字母异位词分组](49-group-anagrams.md) 同用"计数规范化"，一个横向滑动、一个全局聚桶

