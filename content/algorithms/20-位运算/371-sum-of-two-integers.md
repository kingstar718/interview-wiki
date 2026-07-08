# 371. 两整数之和（Sum of Two Integers）

频次 ★★★ · 难度 🟡 · 高频：阿里

## 题目

不用 +、-，求两整数之和。

## 思路

**位运算**：`a ^ b` 是无进位和，`(a & b) << 1` 是进位。迭代直到进位为 0。

## 代码

```java
public int getSum(int a, int b) {
    while (b != 0) {
        int carry = (a & b) << 1;   // 进位
        a = a ^ b;                  // 无进位和
        b = carry;
    }
    return a;
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- [136. 只出现一次的数字](136-single-number.md)（异或的拓展应用）

---

[← 返回训练计划](社招算法训练计划.md)
