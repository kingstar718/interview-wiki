---
topics:
  - 双指针与滑动窗口
---

# 567. 字符串的排列（Permutation in String）

频次 ★★★ · 难度 🟡 · 高频：快手

## 题目

判断 s2 是否包含 s1 的**某个排列**作为子串。

**示例**：
```
输入: s1 = "ab", s2 = "eidbaooo"
输出: true    （"ba"）
```

## 思路

"包含 s1 的排列" 等价于 "存在长度为 |s1| 的子串与 s1 互为字母异位词"——与 [438](438-find-all-anagrams-in-a-string.md) 完全同构：定长窗口 + 计数比较，找到即返回。

## 代码

```java
public boolean checkInclusion(String s1, String s2) {
    if (s2.length() < s1.length()) return false;
    int[] need = new int[26], win = new int[26];
    for (char c : s1.toCharArray()) need[c - 'a']++;
    int k = s1.length();
    for (int i = 0; i < s2.length(); i++) {
        win[s2.charAt(i) - 'a']++;
        if (i >= k) win[s2.charAt(i - k) - 'a']--;
        if (i >= k - 1 && Arrays.equals(win, need)) return true;
    }
    return false;
}
```

## 复杂度

- **时间**：O(n × 26)
- **空间**：O(26)

## 边界条件

- s1 比 s2 长：false
- s1 单字符：退化为 contains 判断
- s1 == s2 的排列：窗口滑到尾部命中

## 变式

- 改问"出现几次/所有位置"→ 即 [438. 找到字符串中所有字母异位词](438-find-all-anagrams-in-a-string.md)
- 允许 s2 中隔着别的字符（子序列版）→ 不是滑窗，变成计数贪心

## 易错点

- "排列"两个字唬人——本质就是异位词判断，别真去枚举全排列（阶乘爆炸）
- 判定与出窗的下标条件同 438，`i >= k - 1` 才能判
- 提前返回 true 后别忘了循环外返回 false

## 面试追问

- **为什么不用生成 s1 的全排列再逐个 contains？** k! 复杂度，k=10 就 360 万；"排列"的判定性质（计数相等）比枚举便宜得多——把"生成问题"转化为"判定问题"是常见的降维手法
- **和 438 的关系？** 同一份窗口代码，只是"收集全部"和"存在即真"的差别；面试常先出本题再追问 438 或反过来

## 关联题

- 同套路：[438. 找到字符串中所有字母异位词](438-find-all-anagrams-in-a-string.md) —— 同构题
- 进阶：[76. 最小覆盖子串](76-minimum-window-substring.md) —— 放宽为"包含"后窗口变长
- 知识点：[3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md)是变长窗口起点,本题是定长窗口起点

