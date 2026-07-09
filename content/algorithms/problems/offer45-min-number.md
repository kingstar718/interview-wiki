---
topics:
  - 排序与堆
---

# 剑指 Offer 45. 把数组排成最小的数

频次 ★★★ · 难度 🟡 · 高频：阿里

## 题目

非负整数数组，将它们拼接起来排成最小的数，以**字符串**形式返回。

**示例**：
```
输入: [3,30,34,5,9]
输出: "3033459"
```

## 思路

**自定义排序**：对字符串 `a` 和 `b`，比较 `a+b` 和 `b+a` 的字典序，小的在前。

## 代码

```java
public String minNumber(int[] nums) {
    String[] strs = new String[nums.length];
    for (int i = 0; i < nums.length; i++) strs[i] = String.valueOf(nums[i]);
    Arrays.sort(strs, (a, b) -> (a + b).compareTo(b + a));
    return String.join("", strs);
}
```

## 复杂度

- **时间**：O(n log n × L) —— L 为字符串平均长度
- **空间**：O(n)

## 边界条件

- 含 0：`[0,0]` → "00" → 转为 "0"
- 单元素：直接返回字符串

## 变式

- 最大数：`(b+a).compareTo(a+b)` 即可

## 易错点

- 排序规则需要证明传递性——`a+b < b+a 且 b+c < c+b ⇒ a+c < c+a`。面试中说"可以通过数学归纳证明该比较器满足全序"即可
- 不要用数值比较（`Integer.parseInt(a+b)` 会溢出）

## 关联题

- 同套路：[912. 排序数组](912-sort-an-array.md) —— 自定义排序
- 进阶：[56. 合并区间](56-merge-intervals.md) —— 区间排序
- 知识点：自定义比较器的全序性质见[排序](排序与堆.md)

