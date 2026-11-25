---
topics:
  - 回溯
techniques:
  - 回溯框架
  - 剪枝
---

# 22. 括号生成（Generate Parentheses）

频次 ★★★★★ · 难度 🟡 · 高频：字节/阿里/腾讯

## 题目

n 对括号，生成所有有效的括号组合。

**示例**：
```
输入: n = 3
输出: ["((()))","(()())","(())()","()(())","()()()"]
```

## 思路

**回溯 + 合法性裁剪**：递归维护两个计数器：已用左括号数和已用右括号数。

- 左括号数 < n → 可以加左括号
- 右括号数 < 左括号数 → 可以加右括号（保证任何时候右括号不超过左括号）

## 代码

```java
public List<String> generateParenthesis(int n) {
    List<String> res = new ArrayList<>();
    backtrack(n, 0, 0, new StringBuilder(), res);
    return res;
}

private void backtrack(int n, int left, int right, StringBuilder sb, List<String> res) {
    if (sb.length() == n * 2) {
        res.add(sb.toString());
        return;
    }
    if (left < n) {
        sb.append('(');
        backtrack(n, left + 1, right, sb, res);
        sb.deleteCharAt(sb.length() - 1);
    }
    if (right < left) {
        sb.append(')');
        backtrack(n, left, right + 1, sb, res);
        sb.deleteCharAt(sb.length() - 1);
    }
}
```

## 两计数器剪枝 + 复杂度锚=答案个数（回溯家族收官）

```
46 家族→22 的三处换装（其余骨架一字不变）：
  for+used → 两个 if 各管一支（每位置恰好两种选择，非互斥可双开）
  used/排序去重墙 → 两计数器（open<n 放'('；close<open 放')'——严格小于，
    close<n 砍不出"前缀合法"；close<=open 会在 (0,0) 放出非法 ')' 开头）
  撤销 → deleteCharAt（或 String 拼接=不可变对象天然快照免撤销）
出口/收账/return 纪律原样（216：外层管停内层管收 return 无条件）
复杂度锚=答案个数（用户"这题复杂度会高一点"）：输出本身=Catalan 数
  ≈4^n/n^1.5 指数——剪枝只砍非法枚举（2^(2n)→Catalan 条路径），
  砍不动合法解；回溯题复杂度对齐答案数，不是对齐 n
Catalan 对照：1,2,5,14,42（n=1..5）
```

## 复杂度

- **时间**：O(4^n / √n) —— 第 n 个卡特兰数
- **空间**：O(n) —— 递归栈 + StringBuilder

## 边界条件

- n = 0：返回 `[""]`（一个空串）
- n = 1：返回 `["()"]`

## 变式

- **[17. 电话号码的字母组合](17-letter-combinations-of-a-phone-number.md)**：同样是"选排列"，但选择分支来自 map，不涉及合法性判断
- **判断括号有效性（20）**：栈匹配，和本题的"生成"是对偶问题
- **不同括号类型（{} []）**：需要栈辅助匹配

## 易错点

- **右括号条件 `right < left` 而不是 `right < n`**：保证不产生 `")("` 这种无效串
- StringBuilder 是可变对象，回溯后要 `deleteCharAt`；如果用 String 拼接可以不用回溯（但 n 大时性能差）
- 卡特兰数的直觉：有效的括号组合数 = 卡特兰数 C_n = 1/(n+1)C(2n,n)

## 面试追问

- **DFS 先加左括号和先加右括号有区别吗？** 先加左括号天然合法（因为 left < n 的分支先走），先加右括号需要检查更多条件。实际没有区别，只是代码写法不同
- **卡特兰数的其他应用？** 出栈序列数、N 个节点二叉树形态数、凸多边形三角剖分数——答出两三个说明数学基础好

## 关联题

- 同套路：[17. 电话号码的字母组合](17-letter-combinations-of-a-phone-number.md) —— 回溯分支选择
- 进阶：[46. 全排列](46-permutations.md) —— 另一方向的回溯模板（排列 vs 组合 vs 构造）
- 知识点：卡特兰数、回溯合法性剪枝见[回溯](../回溯.md)

