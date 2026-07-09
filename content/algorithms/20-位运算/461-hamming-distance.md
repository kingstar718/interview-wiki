# 461. 汉明距离（Hamming Distance）

频次 ★★★ · 难度 🟢 · 高频：字节

## 题目

两个整数二进制表示不同位的个数。

## 思路

先异或得到不同位为 1，然后数 1 的个数。

## 代码

```java
public int hammingDistance(int x, int y) {
    int xor = x ^ y;
    int count = 0;
    while (xor != 0) {
        count += xor & 1;
        xor >>= 1;
    }
    return count;
}
```

也可以用 `Integer.bitCount(x ^ y)` 一行。但面试需要手写。

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 同套路：[136. 只出现一次的数字](136-single-number.md)、[338. 比特位计数](338-counting-bits.md) —— 同为逐位统计/异或技巧

