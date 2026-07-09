---
topics:
  - 动态规划与贪心
---

# 32. 最长有效括号（Longest Valid Parentheses）

频次 ★★★★ · 难度 🔴 · 高频：字节

## 题目

只含 `(` 和 `)` 的字符串，求最长有效（正确匹配）括号子串的长度。

**示例**：
```
输入: ")()())"
输出: 4  （"()()"）
```

## 思路

**栈 / DP 两种解法**。栈更直观，DP 更稳定。

**栈**：栈底存最后一个未匹配的 `)` 的 index（初始 -1）。遍历，遇到 `(` 入栈，遇到 `)` 出栈，栈空时把当前 i 压入（新边界），栈不空时 `i - stack.peek()` 就是有效长度。

**DP**：`dp[i]` 表示以 i 结尾的最长有效括号长度。`s[i] == ')'` 时：
- `s[i-1] == '('`：`dp[i] = dp[i-2] + 2`
- `s[i-1] == ')'` 且 `s[i-dp[i-1]-1] == '('`：`dp[i] = dp[i-1] + dp[i-dp[i-1]-2] + 2`

## 代码

```java
// 栈解法
public int longestValidParentheses(String s) {
    Stack<Integer> stack = new Stack<>();
    stack.push(-1);                          // 哨兵
    int max = 0;
    for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == '(') {
            stack.push(i);
        } else {
            stack.pop();
            if (stack.isEmpty()) {
                stack.push(i);              // 新边界（上一个未匹配的 ')'）
            } else {
                max = Math.max(max, i - stack.peek());
            }
        }
    }
    return max;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(n)

## 边界条件

- 空串：返回 0
- 全左/全右：返回 0

## 变式

- **[22. 括号生成](22-generate-parentheses.md)**：生成版
- **[20. 有效的括号](20-valid-parentheses.md)**：判合法

## 易错点

- 哨兵 `-1` 的初始值——遇到右括号出栈后栈空则 push 新边界
- 栈存的是下标不是字符
- DP 的转移公式较复杂，面试时栈写法就够了

## 面试追问

- **空间 O(1) 的解法？** 两趟遍历，从左到右数左右括号数，相等时更新 max，右 > 左时归零；从右到左对称。答出来证明对问题理解深

## 关联题

- 同套路：[22. 括号生成](22-generate-parentheses.md) —— 生成 vs 匹配
- 进阶：[312. 戳气球](312-burst-balloons.md) —— 区间 DP 另一题
- 知识点：括号匹配的栈/DP 双解法见[动态规划](动态规划与贪心.md)

