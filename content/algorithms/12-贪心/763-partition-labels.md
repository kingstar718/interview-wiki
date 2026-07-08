# 763. 划分字母区间（Partition Labels）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

字符串 s，划分成尽可能多的段，使每段中的字符只在该段出现。输出每段长度。

**示例**：
```
输入: s = "ababcbacadefegdehijhklij"
输出: [9,7,8]
```

## 思路

**贪心**：先遍历一遍记录每个字符的**最后出现下标**。再遍历，维护当前段的 `start` 和 `end`，`end = max(end, last[c])`，当 `i == end` 时收割一段。

## 代码

```java
public List<Integer> partitionLabels(String s) {
    int[] last = new int[26];
    for (int i = 0; i < s.length(); i++) {
        last[s.charAt(i) - 'a'] = i;
    }
    List<Integer> res = new ArrayList<>();
    int start = 0, end = 0;
    for (int i = 0; i < s.length(); i++) {
        end = Math.max(end, last[s.charAt(i) - 'a']);
        if (i == end) {
            res.add(end - start + 1);
            start = end + 1;
        }
    }
    return res;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(26) = O(1)

## 边界条件

- 空串：返回空列表
- 全不同字符：每个字符一段，输出 n 个 1
- 全相同字符：一段，输出 [n]

## 变式

- **[56. 合并区间](https://leetcode.cn/problems/merge-intervals/)**：同样是合并重叠范围，但区间是显式给出的
- 字符只出现一次：必然自成一段

## 易错点

- `last` 数组存储的是**最后出现的位置**，不是出现次数
- 收割条件 `i == end` 说明当前段内的所有字符都不会在后面出现
- 收割后更新 start

## 面试追问

- **为什么这是贪心？** 每步取"当前字符的最后出现位置"来扩展 end，对每个段取最大范围——局部最优（贪心最远）构成全局最优（分段）

## 关联题

- 同套路：[435. 无重叠区间](435-non-overlapping-intervals.md) —— 区间问题贪心
- 进阶：[134. 加油站](134-gas-station.md) —— 另一种"遍历+更新边界"的贪心
- 知识点：字符串区间合并贪心见[贪心](algorithms/12-贪心/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
