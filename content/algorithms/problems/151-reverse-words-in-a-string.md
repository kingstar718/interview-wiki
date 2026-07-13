# 151. 反转字符串中的单词（Reverse Words in a String）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

给定一个字符串 `s`，反转字符串中单词的顺序。单词由非空格字符组成，单词之间可能有多个空格。返回的字符串中单词间只能有一个空格，且首尾不能有空格。

**示例**：
```
输入: s = "the sky is blue"
输出: "blue is sky the"

输入: s = "  hello world  "
输出: "world hello"
解释: 需要去除首尾空格和单词间多余空格
```

## 思路

三步法，不使用 split + 倒序拼接（虽然简单但面试不够硬核）：

1. **去除多余空格**：用双指针原地去除首尾空格和单词间多余空格（类似 [27. 移除元素](27-remove-element.md)），每个单词间保留一个空格
2. **反转整个字符串**：将去空格后的 `char[]` 整体反转（套用 [344. 反转字符串](344-reverse-string.md)）
3. **反转每个单词**：遍历 `char[]`，以空格为分隔，反转每个单词

## 代码

```java
public String reverseWords(String s) {
    char[] chars = s.toCharArray();
    int n = chars.length;

    // 1. 去除多余空格（双指针）
    int slow = 0;
    for (int fast = 0; fast < n; fast++) {
        if (chars[fast] != ' ') {
            // 单词之间补一个空格（非首单词）
            if (slow > 0) {
                chars[slow++] = ' ';
            }
            // 填入整个单词
            while (fast < n && chars[fast] != ' ') {
                chars[slow++] = chars[fast++];
            }
        }
    }
    int newLen = slow; // 去空格后的有效长度

    // 2. 反转整个字符串
    reverse(chars, 0, newLen - 1);

    // 3. 反转每个单词
    int start = 0;
    for (int i = 0; i <= newLen; i++) {
        if (i == newLen || chars[i] == ' ') {
            reverse(chars, start, i - 1);
            start = i + 1;
        }
    }

    return new String(chars, 0, newLen);
}

private void reverse(char[] chars, int left, int right) {
    while (left < right) {
        char temp = chars[left];
        chars[left] = chars[right];
        chars[right] = temp;
        left++;
        right--;
    }
}
```

## 复杂度

- **时间**：O(n) — 去空格 O(n) + 整体反转 O(n) + 每个单词反转 O(n) = O(n)
- **空间**：O(n) — `char[]` 数组（String 不可变），若输入已是 `char[]` 则 O(1) 额外空间

## 边界条件

- 首尾有空格：去空格步骤自动跳过首尾空格
- 单词间有多个空格：去空格步骤中 `chars[fast] != ' '` 的条件保证跳过连续空格，只在单词间补一个空格
- 全空格字符串：`slow` 保持为 0，`newLen = 0`，返回空字符串

## 变式

- **使用 split + 倒序拼接**：`s.trim().split("\\s+")` 然后从后往前拼接，代码短但需要 O(n) 额外空间创建多个字符串对象，且 split 是正则匹配，面试中不够底层
- **单词顺序不变，反转每个单词内的字符**：去掉第二步"整体反转"即可，即只保留去空格 + 反转每个单词（如 `"hello world" → "olleh dlrow"`）

## 易错点

- 去空格时，`while` 循环中 `fast` 会自增，外层 `for` 循环也会自增——注意 `fast` 在 `while` 结束时已经指向空格，外层的 `fast++` 会让它跳过空格，逻辑正确
- 反转每个单词时，循环到 `i == newLen` 也要触发反转——最后一个单词后面没有空格，靠 `i == newLen` 触发
- 返回时用 `new String(chars, 0, newLen)` 而不是 `new String(chars)`——后者会包含去空格后尾部的无效字符

## 面试追问

- **为什么不用 split？** split 创建了多个字符串对象，额外空间开销大；三步法原地操作 char[]，空间更优且展示了对数组操作的熟练度。且 split 的正则匹配在极端情况（超长字符串）下性能不如手动遍历。
- **如果允许 O(n) 额外空间，还有更简单的方法吗？** 可以用 `Collections.reverse(Arrays.asList(s.trim().split("\\s+")))` 然后 `String.join`，但面试中写了这个会被追问"不用 split 怎么做"。

## 关联题

- 前置：[344. 反转字符串](344-reverse-string.md) —— 反转字符数组的基础操作
- 同套路：[27. 移除元素](27-remove-element.md) —— 双指针原地移除（去空格步骤的核心思想）
- 进阶：186. 翻转字符串里的单词 II —— 输入是 `char[]`，可以直接 O(1) 额外空间

---

[← 返回训练计划](社招算法训练计划.md)