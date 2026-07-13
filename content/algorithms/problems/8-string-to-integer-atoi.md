---
topics:
  - 数组与字符串
techniques:
  - 模拟构造
---

# 8. 字符串转换整数（atoi）（String to Integer (atoi)）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯/美团

## 题目

实现 `myAtoi(string s)`：读入字符串并转换为 32 位有符号整数。规则——跳过前导空格；处理正负号；读入连续数字直到非数字字符；将数字序列转为整数；超出 `[−2³¹, 2³¹−1]` 时截断到边界。

**示例**：
```
输入: "   -42"
输出: -42

输入: "4193 with words"
输出: 4193

输入: "-91283472332"
输出: -2147483648（负溢出）
```

## 思路

模拟三步走：① 跳过空格；② 判断 `+`/`-` 记录符号；③ 遍历数字边拼边做溢出预检查。

溢出检查在乘法之前用 `INT_MAX / 10` 判断：若 `res > INT_MAX / 10`，则乘 10 必溢出；若 `res == INT_MAX / 10`，只有 `digit > 7`（正）或 `digit > 8`（负）才溢出。统一用正数范围判断，最后乘符号。

## 代码

```java
public int myAtoi(String s) {
    int i = 0, n = s.length();
    while (i < n && s.charAt(i) == ' ') i++;
    if (i == n) return 0;

    int sign = 1;
    if (s.charAt(i) == '+') {
        i++;
    } else if (s.charAt(i) == '-') {
        sign = -1;
        i++;
    }

    int res = 0;
    while (i < n && Character.isDigit(s.charAt(i))) {
        int d = s.charAt(i) - '0';
        if (res > (Integer.MAX_VALUE - d) / 10) {
            return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        }
        res = res * 10 + d;
        i++;
    }
    return sign * res;
}
```

## 复杂度

- **时间**：O(n) — 一次遍历
- **空间**：O(1)

## 边界条件

- 空串/全空格：跳过空格后 `i == n`，返回 0
- 只有正负号：符号后第一个字符非数字，while 不执行，返回 0
- 首个非空字符非法（如 "words 123"）：直接返回 0
- 正向溢出 `2147483648` → `Integer.MAX_VALUE`
- 负向溢出 `-2147483649` → `Integer.MIN_VALUE`
- 前导零：正常读入，不影响结果

## 变式

- **[65. 有效数字](https://leetcode.cn/problems/valid-number/)**：判断字符串是否为合法数值（含小数/指数），状态更多
- **[415. 字符串相加](415-add-strings.md)**：大数加法，模拟竖式
- 浮点数解析：atoi 扩展，需处理小数点与指数部分

## 易错点

- **溢出判断必须在乘法之前**：`res * 10 + d` 已在 Java 中回绕，结果不可控。必须在乘之前用 `MAX_VALUE / 10` 预判。
- 正负号用 `sign` 统一处理，不要分成两套逻辑，否则溢出分支容易遗漏。
- `+` 是合法前缀，不能只处理 `-`。
- 数字字符转 int 用 `c - '0'`，勿用 `Character.getNumericValue`（处理全角数字行为不同）。

## 面试追问

- **溢出判断的阈值为什么是 7？** `Integer.MAX_VALUE = 2147483647`，末位 7。负数最小值末位 8，但统一用正数范围判断后，`digit > 7` 就能覆盖正负两侧——若 `res == 214748364` 且 `digit >= 8`，正侧溢出返回 MAX_VALUE，负侧 `sign * (2147483640 + 8) = -2147483648` 恰好是 MIN_VALUE 不溢出，所以负侧阈值其实是 `digit > 8`。常见实现统一用 `digit > 7` 简化，对负侧多一次截断但不影响正确性。
- **能用 long 偷懒吗？** 能用 `long res` 最后强转，但面试官通常希望看到 int 溢出预判，体现对整数范围的理解。
- **跳过空格用 while 不用 trim()？** trim() 创建新字符串 O(n) 空间；手写 while O(1) 空间。面试手写应展示不额外分配的意识。

## 关联题

- 同套路：[415. 字符串相加](415-add-strings.md) —— 大数模拟，溢出处理思路一致
- 进阶：[65. 有效数字](https://leetcode.cn/problems/valid-number/) —— 有限状态机解析数值
- 知识点：32 位整数范围与溢出处理模式
