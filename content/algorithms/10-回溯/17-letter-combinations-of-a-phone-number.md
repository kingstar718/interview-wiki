# 17. 电话号码的字母组合（Letter Combinations of a Phone Number）

频次 ★★★★ · 难度 🟡 · 高频：美团/字节

## 题目

数字 2~9 映射到字母（同电话键盘），给定数字串，输出所有可能的字母组合。

**示例**：
```
输入: digits = "23"
输出: ["ad","ae","af","bd","be","bf","cd","ce","cf"]
```

## 思路

**回溯**：每个数字对应多个字母，递归每层从当前数字对应的字母集中选一个，到数字串末尾时收集。

可以看作是"多叉树"的遍历——每个节点的分支数取决于当前数字对应的字母数。

## 代码

```java
private static final String[] MAPPING = {
    "", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"
};

public List<String> letterCombinations(String digits) {
    List<String> res = new ArrayList<>();
    if (digits.isEmpty()) return res;
    backtrack(digits, 0, new StringBuilder(), res);
    return res;
}

private void backtrack(String digits, int idx, StringBuilder sb, List<String> res) {
    if (idx == digits.length()) {
        res.add(sb.toString());
        return;
    }
    String letters = MAPPING[digits.charAt(idx) - '0'];
    for (char c : letters.toCharArray()) {
        sb.append(c);
        backtrack(digits, idx + 1, sb, res);
        sb.deleteCharAt(sb.length() - 1);
    }
}
```

## 复杂度

- **时间**：O(4^n × n) —— 每个数字最多对应 4 个字母，n 是数字串长度
- **空间**：O(n) —— 递归栈 + StringBuilder

## 边界条件

- 空串：返回空列表（不是 `[""]`！）
- 含 0/1：不映射到字母，backtrack 自动跳过（MAPPING 对应为空串）

## 变式

- **[22. 括号生成](22-generate-parentheses.md)**：同级的多选一回溯，但分支有条件约束
- **[93. 复原 IP 地址](93-restore-ip-addresses.md)**：分支数动态（1~3 位数字），多了合法性判断

## 易错点

- `digits.isEmpty()` 返回空列表而不是 `[""]`——需要特判。`new StringBuilder()` 直接递归会返回 `[""]` 不正确
- 数字转对应字符串：`digits.charAt(idx) - '0'` 获得 int 值
- `sb.deleteCharAt(sb.length() - 1)` 回溯的典型写法；如果用 `String +` 拼接则不需要回溯但性能差

## 面试追问

- **这题是排列还是组合？** 不是组合也不是排列——它是"每个位置有若干选择，选一个走到底"的**选排列**。每层的选择列表独立且互不干扰，是最简单的回溯场景
- **如果输入含 1 和 0？** 1 和 0 不映射字母，可以选择跳过（对应分支数为 1，即什么都不加）

## 关联题

- 同套路：[22. 括号生成](22-generate-parentheses.md)、[93. 复原 IP 地址](93-restore-ip-addresses.md) —— 按位选择 + 回溯
- 进阶：[46. 全排列](46-permutations.md) —— 选择列表随路径动态变化的更复杂回溯
- 知识点：回溯的"多叉树遍历"视角见[回溯](algorithms/10-回溯/README.md)

