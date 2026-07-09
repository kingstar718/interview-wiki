# 131. 分割回文串（Palindrome Partitioning）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

将字符串分割成若干回文子串，输出所有分割方案。

**示例**：
```
输入: "aab"
输出: [["a","a","b"],["aa","b"]]
```

## 思路

**回溯**：从左到右枚举当前子串的结束位置，判断 `[start, end]` 这一段是否是回文串，是则加入路径并递归处理剩余部分。

回文判断可预处理（动态规划/双指针中心扩展）避免重复计算。

## 代码

```java
public List<List<String>> partition(String s) {
    List<List<String>> res = new ArrayList<>();
    backtrack(s, 0, new ArrayList<>(), res);
    return res;
}

private void backtrack(String s, int start, List<String> path, List<List<String>> res) {
    if (start == s.length()) {
        res.add(new ArrayList<>(path));
        return;
    }
    for (int end = start; end < s.length(); end++) {
        if (!isPalindrome(s, start, end)) continue;
        path.add(s.substring(start, end + 1));
        backtrack(s, end + 1, path, res);
        path.remove(path.size() - 1);
    }
}

private boolean isPalindrome(String s, int l, int r) {
    while (l < r) {
        if (s.charAt(l) != s.charAt(r)) return false;
        l++; r--;
    }
    return true;
}
```

## 复杂度

- **时间**：O(n × 2^n) —— 最坏每种分割都有可能（全相同字符时）
- **空间**：O(n) —— 递归栈 + path

## 边界条件

- 空串：返回 `[[]]`
- 单字符：返回 `[[s]]`
- 全相同字符：所有分割都是回文，输出全部分割方案

## 变式

- **[93. 复原 IP 地址](93-restore-ip-addresses.md)**：类似的分割类回溯，但加了"IP 段合法性"约束
- **回文串预计算**：用 DP 或中心扩展法先算出所有 `isPal[i][j]`，O(n²) 时间换 O(1) 查询

## 易错点

- 子串范围 `[start, end]` 不能写成 `[start, end)` 或 `s.substring(start, end)`——substring 是左闭右开，注意参数
- 回文判断的双指针条件 `l < r`（不是 `l <= r`）
- 递归参数 start 走到 s.length() 时收集——注意 substring 最后一次调用时 end = n-1，然后下一层 start = n

## 面试追问

- **预计算回文信息有几种方式？** DP：`isPal[i][j] = (s[i]==s[j] && (j-i<=2 || isPal[i+1][j-1]))`；中心扩展：每个位置向两边扩。DP 好写好理解

## 关联题

- 同套路：[93. 复原 IP 地址](93-restore-ip-addresses.md) —— 字符串分割类回溯
- 进阶：[5. 最长回文子串](5-longest-palindromic-substring.md) —— 回文的动态规划解法
- 知识点：字符串分割类回溯模板见[回溯](algorithms/10-回溯/README.md)

