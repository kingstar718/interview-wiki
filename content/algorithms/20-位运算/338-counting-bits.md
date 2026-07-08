# 338. 比特位计数（Counting Bits）

频次 ★★★ · 难度 🟢 · 高频：美团

## 题目

返回 0~n 每个数的二进制表示中 1 的个数。

## 思路

**DP**：`count[i] = count[i >> 1] + (i & 1)`。i 右移一位的 1 的个数 + 最低位是否为 1。

## 代码

```java
public int[] countBits(int n) {
    int[] res = new int[n + 1];
    for (int i = 1; i <= n; i++) {
        res[i] = res[i >> 1] + (i & 1);
    }
    return res;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(n)

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[136. 只出现一次的数字](136-single-number.md)

---

[← 返回训练计划](社招算法训练计划.md)
