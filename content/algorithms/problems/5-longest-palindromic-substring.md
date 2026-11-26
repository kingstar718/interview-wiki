---
topics:
  - 动态规划与贪心
techniques:
  - 中心扩展
---

# 5. 最长回文子串（Longest Palindromic Substring）

频次 ★★★★ · 难度 🟡 · 高频：全厂

## 题目

找字符串中最长的回文子串。

**示例**：
```
输入: "babad"
输出: "bab"  （"aba" 也可）
```

## 思路

**中心扩展法**：每个字符和每对相邻字符作为回文中心，向两边扩展。回文长度可能是奇数（单字符中心）或偶数（双字符中心）。

DP 也能做但 O(n²) 空间，中心扩展 O(1) 空间更优。

## 代码

```java
private int maxLen = 0, start = 0;

public String longestPalindrome(String s) {
    for (int i = 0; i < s.length(); i++) {
        expand(s, i, i);       // 奇数长度中心
        expand(s, i, i + 1);   // 偶数长度中心
    }
    return s.substring(start, start + maxLen);
}

private void expand(String s, int l, int r) {
    while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
        l--; r++;
    }
    int len = r - l - 1;       // 循环结束后 l,r 已越界
    if (len > maxLen) {
        maxLen = len;
        start = l + 1;
    }
}
```

## 中心扩展 vs 区间 DP + start 公式整除通吃（回文双流派）

```
中心扩展（主入口 AC 145/145）：2n-1 中心（n 奇字符 + n-1 缝隙）共用 expand(l,r)
  expand 返回长度 r-l-1（退出时指针各多走一步）；起点公式 start=i-(len-1)/2
  整除通吃奇偶：奇中心 L=2k+1 左伸 k 格；偶中心 L=2k 左伸 k-1 格（floor 抹掉多的1）
  用户卡点"start 计算方式"——双中心分例推 + expand 返回真实边界 [l+1,r-1] 的免换算替代
区间 DP（对照入口）：dp[i][j]=s[i..j] 是否回文；转移 s[i]==s[j] && (L<=3 || dp[i+1][j-1])
  外层按区间长度 L（不是按 i——短区间先算好才能查 dp[i+1][j-1]）
  L<=3 短路（内部为空/单字符恒真）；收账沿途记最长
用户 L=0 实录：j=i-1 空区间 → charAt(-1) 必炸 + 倒置区间垃圾状态——
  边界条件是定义域本身（区间必须非空），L 从 1 起
两版对照：扩展 O(1) 空间首选；DP O(n²) 表为"子问题复用"打底（516 挂账题直接复用）
多答案题对账口径：只比长度+合法性（骨架 lint 适配 len=%d 输出）
```

## 复杂度

- **时间**：O(n²) —— 2n 个中心，每个最多扩展 O(n)
- **空间**：O(1)

## 边界条件

- 空串/单字符：返回原串
- 全相同字符（"aaaa"）：中心扩展全程命中

## 变式

- **[647. 回文子串](https://leetcode.cn/problems/palindromic-substrings/)**：统计回文子串数量，同款中心扩展
- **[516. 最长回文子序列](https://leetcode.cn/problems/longest-palindromic-subsequence/)**：子序列（不连续），二维 DP

## 易错点

- 扩展结束时 l、r 已越界，长度 = `r - l - 1`（不是 `r - l + 1`）
- `start` 需要用 `l + 1` 恢复
- 两种中心都要枚举（奇偶）

## 面试追问

- **Manacher 算法？** O(n) 时间，面试中一般不要求，提一句"还有线性解法 Manacher"证明见识广即可

## 关联题

- 同套路：[647. 回文子串](https://leetcode.cn/problems/palindromic-substrings/) —— 计数版
- 进阶：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— 子序列类 DP
- 知识点：中心扩展法 vs DP 见[动态规划](../动态规划与贪心.md)

