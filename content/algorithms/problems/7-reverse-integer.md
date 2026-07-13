---
topics:
  - 数学
techniques:
  - 模拟构造
---

# 7. 整数反转（Reverse Integer）

频次 ★★★ · 难度 🟡 · 高频：字节/腾讯/美团

## 题目

给出一个 32 位有符号整数，将其数字部分反转。如果反转后溢出 32 位有符号整数范围 `[−2^31, 2^31−1]`，返回 0。

**示例**：
```
输入: 123
输出: 321

输入: -123
输出: -321

输入: 120
输出: 21

输入: 1534236469
输出: 0  （反转后溢出）
```

## 思路

**逐位弹出再组装**：取 `x % 10` 得到末位，`x /= 10` 去掉末位。用 `result = result * 10 + digit` 逐位构建反转数。每次操作前检查是否会导致溢出：`result > Integer.MAX_VALUE / 10` 或 `result < Integer.MIN_VALUE / 10` 时直接返回 0。

## 代码

```java
public int reverse(int x) {
    int result = 0;
    while (x != 0) {
        int digit = x % 10;
        x /= 10;

        // 正数溢出检查
        if (result > Integer.MAX_VALUE / 10
            || (result == Integer.MAX_VALUE / 10 && digit > 7)) {
            return 0;
        }
        // 负数溢出检查
        if (result < Integer.MIN_VALUE / 10
            || (result == Integer.MIN_VALUE / 10 && digit < -8)) {
            return 0;
        }

        result = result * 10 + digit;
    }
    return result;
}
```

## 复杂度

- **时间**：O(log₁₀ n) — 数字的位数
- **空间**：O(1)

## 边界条件

- 负数反转：`-123 → -321`，取模运算在 Java 中保留符号。
- 末位是 0（如 `120 → 21`）：通过 `digit = x % 10` 和 `x /= 10` 自然处理，无需额外逻辑。
- 溢出情况：`1534236469` 反转后超出 int 范围，溢出检查拦截。
- `x = 0`：循环不执行，直接返回 0。
- `x = Integer.MIN_VALUE`（-2147483648）：反转后 -8463847412 明显溢出，返回 0。

## 变式

- **64 位整数反转**：把溢出检查中的 `Integer.MAX_VALUE` 换成 `Long.MAX_VALUE` 即可。
- **字符串转整数**（[8. 字符串转换整数 (atoi)](8-string-to-integer-atoi.md)）：反转 + 溢出检查 + 空格/符号处理。

## 易错点

- 溢出检查必须在 `result * 10 + digit`**之前**，否则已经溢出了才检查就晚了（Java 整数溢出静默环绕）。
- `Integer.MAX_VALUE / 10 = 214748364`，最后一位是 7，所以判断 `digit > 7`。`Integer.MIN_VALUE / 10 = -214748364`，最后一位是 -8（因为 Java 负数取模结果是负数），所以判断 `digit < -8`。
- 正负号在反转后保持不变，不需要单独处理符号。

## 面试追问

- **为什么不用 long 类型来反转，最后强转 int 并判断是否相等？** 可以，代码更简洁：用 `long result` 累加，最后 `(int) result != result` 来判断是否溢出。但面试官可能会问这是不是"利用了语言特性"，有时希望看到纯 int 的溢出检查。两种方案都应该能给出。
- **怎么处理溢出问题，除了 int 溢出检查外还能怎么做？** 也可以用 `try-catch` 捕捉 Maths.addExact 或 `Math.multiplyExact` 抛出的 `ArithmeticException`。

## 关联题

- 同套路：[9. 回文数](9-palindrome-number.md)（整数版，反转一半比较避免溢出）
- 易混：[8. 字符串转换整数 (atoi)](8-string-to-integer-atoi.md)（溢出检查 + 空格/符号处理更复杂）
- 知识点：32 位有符号整数范围 [-2³¹, 2³¹-1] 见[位运算](位运算.md)
