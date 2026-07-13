---
topics:
  - 双指针与滑动窗口
techniques:
  - 双指针模拟
---

# 125. 验证回文串（Valid Palindrome）

频次 ★★★ · 难度 🟢 · 高频：字节/腾讯/美团

## 题目

给定一个字符串 `s`，只考虑字母和数字字符，忽略大小写，判断它是否是回文串。

**示例**：
```
输入: "A man, a plan, a canal: Panama"
输出: true

输入: "race a car"
输出: false
```

## 思路

**首尾双指针**：左指针从 0 开始，右指针从末尾开始，跳过非字母数字字符，比较 `Character.toLowerCase` 后的值，如果全部相等则是回文。

## 代码

```java
public boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
        while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;
        if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}
```

## 复杂度

- **时间**：O(n) — 每个字符最多访问一次
- **空间**：O(1)

## 边界条件

- 空串或全非字母数字：跳过所有字符后 `left >= right`，返回 `true`。
- 单字符：直接返回 `true`。
- 数字字符：数字参与回文比较，但不需要 `toLowerCase`（不影响结果）。
- Unicode 字符：`Character.isLetterOrDigit` 支持大多数 Unicode 字母。

## 变式

- **只考虑字母忽略数字**：把 `isLetterOrDigit` 换成 `isLetter` 即可。
- **不忽略大小写**：去掉 `toLowerCase` 比较即可。
- **回文链表**（[234. 回文链表](234-palindrome-linked-list.md)）：值比较 + 快慢指针找中点 + 反转后半段。
- **最多删除一个字符能否构成回文**：见 680. Valid Palindrome II，用双指针遇到不相等时尝试跳过左或右。

## 易错点

- 跳过非字母数字时，内层 `while` 也必须检查 `left < right`，否则可能越界。
- 比较时必须统一 `toLowerCase`（或 `toUpperCase`），且两边的字符都要转换。
- 不要在跳过前就把字符转小写——非字母数字字符跳过前没有转小写的必要。

## 面试追问

- **能不能先过滤字符再判断，和双指针比怎样？** 可以，先 `filter` + `toLowerCase` 得到一个纯净字符串，再双指针或反转比较，但需要 O(n) 额外空间；双指针只用了 O(1) 空间，推荐面试优先给出。
- **如何支持忽略空格和标点但不忽略数字？** 把 `isLetterOrDigit` 改为 `isLetter` 即可。

## 关联题

- 同套路：[9. 回文数](9-palindrome-number.md)（整数版，反转一半比较）
- 进阶：[5. 最长回文子串](5-longest-palindromic-substring.md)（中心扩展法）
- 易混：[234. 回文链表](234-palindrome-linked-list.md)（链表版，快慢指针 + 反转）
- 知识点：双指针模板见[双指针与滑动窗口](双指针与滑动窗口.md)
