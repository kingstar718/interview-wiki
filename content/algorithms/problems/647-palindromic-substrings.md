# 647. 回文子串（Palindromic Substrings）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯/美团

## 题目

统计字符串中回文子串的个数（相同子串在不同位置算多次）。

**示例**：
```
输入: s = "abc"
输出: 3  （"a", "b", "c"）
输入: s = "aaa"
输出: 6  （"a"×3, "aa"×2, "aaa"×1）
```

## 思路

**解法 1：中心扩展法** — 每个字符和每对相邻字符作为回文中心，向两边扩展，每扩展一次就计数 +1。

**解法 2：DP** — `dp[i][j]` 表示 s[i..j] 是否为回文。`dp[i][j] = s[i]==s[j] && (j-i<2 || dp[i+1][j-1])`。

中心扩展法更优（O(1) 空间），DP 更直观。

## 代码

```java
// 解法 1：中心扩展法（推荐）
public int countSubstrings(String s) {
    int count = 0;
    for (int i = 0; i < s.length(); i++) {
        count += expand(s, i, i);       // 奇数长度中心
        count += expand(s, i, i + 1);   // 偶数长度中心
    }
    return count;
}

private int expand(String s, int l, int r) {
    int cnt = 0;
    while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
        cnt++;
        l--;
        r++;
    }
    return cnt;
}

// 解法 2：DP
public int countSubstrings(String s) {
    int n = s.length(), count = 0;
    boolean[][] dp = new boolean[n][n];
    for (int j = 0; j < n; j++) {
        for (int i = 0; i <= j; i++) {
            if (s.charAt(i) == s.charAt(j) && (j - i < 2 || dp[i + 1][j - 1])) {
                dp[i][j] = true;
                count++;
            }
        }
    }
    return count;
}
```

## 复杂度

- **中心扩展**：时间 O(n²)，空间 O(1)
- **DP**：时间 O(n²)，空间 O(n²)

## 边界条件

- 空串：返回 0
- 单字符：返回 1
- 全相同字符（"aaa"）：6 个

## 变式

- **[5. 最长回文子串](5-longest-palindromic-substring.md)**：找最长的那个（同款中心扩展，记录 max）
- **[516. 最长回文子序列](516-longest-palindromic-subsequence.md)**：子序列不连续，DP

## 易错点

- 中心扩展法要分别处理奇数长度和偶数长度中心（i,i 和 i,i+1）
- 扩展函数中每匹配一次就计数 +1，不是扩展完才计数
- DP 的遍历顺序：`j` 在外层（右边界），`i` 在内层（左边界），因为 `dp[i][j]` 依赖 `dp[i+1][j-1]`

## 面试追问

- **DP 的遍历顺序为什么是 j 外层？** `dp[i][j]` 依赖 `dp[i+1][j-1]`（左下角），先算小的 j 保证依赖项已计算
- **Manacher 算法？** O(n) 时间，面试一般不要求，提一句证明见识

## 关联题

- 同套路：[5. 最长回文子串](5-longest-palindromic-substring.md) —— 找最长回文子串
- 进阶：[516. 最长回文子序列](516-longest-palindromic-subsequence.md) —— 子序列版
- 知识点：中心扩展法 + 回文 DP 见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)