---
topics:
  - 排序与堆
techniques:
  - 排序算法实现
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

## 面试追问

- **为什么比较 `a+b` 和 `b+a` 就够了**：拼接结果的长度固定为 `len(a)+len(b)`，长度相同时字典序就等于数值大小。所以「谁在前更小」这个局部决策，等价于比较两种拼法的数值。
- **凭什么局部两两比较能得到全局最优**：这是贪心，需要比较器满足**全序**（尤其是传递性）。传递性成立后，排序结果唯一且最优——这是贪心正确性的前提，不是随便定义个比较器就行。
- **比较器不满足全序会怎样**：Java 的 `Arrays.sort(T[], Comparator)` 用 TimSort，检测到比较器不自洽时直接抛 `IllegalArgumentException: Comparison method violates its general contract!`。这是生产里真实会踩的坑，不是理论问题。
- **为什么不能 `Integer.parseInt(a+b)`**：拼接后长度翻倍，`int` 甚至 `long` 都会溢出。字符串的 `compareTo` 天然是字典序比较，长度一致时就是数值比较，绕开了溢出。
- **`[0,0]` 为什么要特判**：拼出来是 `"00"`，而题目要求的是数值 `0` 的字符串形式 `"0"`。前导零是这题唯一的输出格式陷阱。

## 关联题

- 同套路：[912. 排序数组](912-sort-an-array.md) —— 自定义排序
- 进阶：[56. 合并区间](56-merge-intervals.md) —— 区间排序
- 知识点：自定义比较器的全序性质见[排序](排序与堆.md)

