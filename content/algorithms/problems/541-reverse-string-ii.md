# 541. 反转字符串 II（Reverse String II）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

给定一个字符串 `s` 和一个整数 `k`，每计数到 `2k` 个字符时，反转这 `2k` 个字符中的前 `k` 个字符。如果剩余字符少于 `k` 个，则全部反转；如果剩余字符在 `k` 到 `2k` 之间，则反转前 `k` 个，其余不变。

**示例**：
```
输入: s = "abcdefg", k = 2
输出: "bacdfeg"
解释: 前 2k=4 个字符 "abcd"，反转前 k=2 个 → "bacd"；剩下 "efg" 少于 k=2，全部反转 → "fe"
```

## 思路

将字符串转为 `char[]`，以 `2k` 为步长遍历：每个区间 `[i, i+2k-1]` 中反转前 k 个字符 `[i, i+k-1]`。注意右边界取 `Math.min(i + k - 1, n - 1)`，处理剩余字符不足 k 个的情况。

## 代码

```java
public String reverseStr(String s, int k) {
    char[] chars = s.toCharArray();
    int n = chars.length;
    for (int i = 0; i < n; i += 2 * k) {
        int left = i;
        int right = Math.min(i + k - 1, n - 1);
        while (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            left++;
            right--;
        }
    }
    return new String(chars);
}
```

## 复杂度

- **时间**：O(n) — 每个字符最多被交换一次
- **空间**：O(n) — `char[]` 数组存储字符串（String 不可变，必须转为数组操作）

## 边界条件

- `k = 1`：每 2 个字符反转前 1 个，即每个区间的第一个字符和自己交换，结果不变
- `k >= n`：`i + k - 1` 超出数组长度，`Math.min` 取 `n-1`，整个字符串反转
- 剩余字符数量在 `k` 到 `2k` 之间：`Math.min` 保证只反转前 k 个，剩余不变

## 变式

- **不转 char[] 直接用 StringBuilder**：每次截取子串反转后拼接，但频繁创建子串对象，效率不如 char[] 原地操作
- **反转规则变化**：如每 3k 个字符反转前 k 个——只需把步长从 `2*k` 改为 `3*k`，反转逻辑不变

## 易错点

- 反转范围是 `[i, i+k-1]` 而不是 `[i, i+k]`——`i+k-1` 是第 k 个字符的下标
- 注意 `Math.min(i + k - 1, n - 1)` 处理尾部不足 k 个的情况，不能直接用 `i + k - 1` 否则会越界
- 步长是 `2k` 不是 `k`——题意是每 `2k` 个反转前 `k` 个

## 面试追问

- **如果要求原地修改（输入是 char[]）？** 去掉 `String` → `char[]` 的转换和最后的 `new String`，直接操作数组即可，空间降为 O(1)。
- **为什么用 `i += 2*k` 而不是 `i += k` 再判断？** `i += 2*k` 每次跳过一个完整区间，逻辑更清晰，避免重复处理同一段字符。

## 关联题

- 前置：[344. 反转字符串](344-reverse-string.md) —— 本题的反转子串操作是它的直接套用
- 进阶：[151. 反转字符串中的单词](151-reverse-words-in-a-string.md) —— 更复杂的反转场景（先整体反转再单词反转）

---

[← 返回训练计划](社招算法训练计划.md)