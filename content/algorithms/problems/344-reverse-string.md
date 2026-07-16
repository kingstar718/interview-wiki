---
topics:
  - 数组与字符串
techniques:
  - 对撞指针
---

# 344. 反转字符串（Reverse String）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

编写一个函数，将输入的字符数组 `s` 原地反转。要求使用 O(1) 额外空间。

**示例**：
```
输入: s = ['h','e','l','l','o']
输出: ['o','l','l','e','h']
```

## 思路

双指针从两端向中间逼近：`left` 指向开头，`right` 指向结尾，每次交换 `s[left]` 和 `s[right]`，然后 `left++`、`right--`，直到 `left >= right`。

## 代码

```java
public void reverseString(char[] s) {
    int left = 0, right = s.length - 1;
    while (left < right) {
        char temp = s[left];
        s[left] = s[right];
        s[right] = temp;
        left++;
        right--;
    }
}
```

## 复杂度

- **时间**：O(n) — 每个字符访问一次，共交换 n/2 次
- **空间**：O(1) — 只用一个临时变量

## 边界条件

- 空数组：`left=0, right=-1`，`left < right` 不成立，循环不执行，直接返回
- 单个字符：`left=0, right=0`，`left < right` 不成立，循环不执行，结果不变
- 奇数长度：中间字符不需要交换，循环在 `left == right` 时自然停止

## 变式

- **递归版**：递归交换两端字符，基准条件 `left >= right`，但递归需要 O(n) 栈空间，不如迭代 O(1) 空间
- **用异或交换**：`a ^= b; b ^= a; a ^= b;` 省去临时变量，但可读性差，且当 `left == right` 时异或会清零，需额外判断

## 易错点

- 循环条件是 `left < right` 而不是 `left <= right`——`<=` 会在中间字符处多交换一次（自己和自己交换，虽然结果正确但多余）
- 交换时注意不要写成 `s[left] = s[right]` 后丢失 `s[left]` 的原始值——必须先存到临时变量

## 面试追问

- **如果输入是 String 类型（不可变）而不是 char[]，怎么反转？** String 不可变，只能返回新字符串。可以用 `new StringBuilder(s).reverse().toString()`，或者转成 `char[]` 手动反转后再 `new String(chars)`。
- **递归和迭代各有什么优缺点？** 迭代 O(1) 空间，递归 O(n) 栈空间。本题用递归属于"杀鸡用牛刀"，面试中迭代是更优解。

## 关联题

- 同套路：[541. 反转字符串 II](541-reverse-string-ii.md) —— 每 2k 个字符反转前 k 个，双指针的变体
- 进阶：[151. 反转字符串中的单词](151-reverse-words-in-a-string.md) —— 反转整个字符串 + 反转每个单词
- 知识点：双指针是处理数组/字符串反转的标准写法
