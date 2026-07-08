# 260. 只出现一次的数字 III（Single Number III）

频次 ★★★ · 难度 🟡 · 高频：字节

## 题目

两个元素只出现一次，其余各出现两次。找出这两个元素。

## 思路

先全部异或得到 `xor = a ^ b`。取 xor 最低位的 1（`xor & -xor`）把数组分成两组，每组各自异或得到两个数。

## 代码

```java
public int[] singleNumber(int[] nums) {
    int xor = 0;
    for (int n : nums) xor ^= n;

    int mask = xor & -xor;          // 最低位的 1
    int a = 0, b = 0;
    for (int n : nums) {
        if ((n & mask) == 0) a ^= n;
        else b ^= n;
    }
    return new int[]{a, b};
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[136. 只出现一次的数字](136-single-number.md)

---

[← 返回训练计划](社招算法训练计划.md)
