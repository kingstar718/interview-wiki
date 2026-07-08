# 93. 复原 IP 地址（Restore IP Addresses）

频次 ★★★ · 难度 🟡 · 高频：快手

## 题目

数字字符串，通过加 `.` 分割成有效的 IP 地址（四段，每段 0~255，不允许前导零），输出所有可能。

**示例**：
```
输入: "25525511135"
输出: ["255.255.11.135","255.255.111.35"]
```

## 思路

**回溯（分割类）**：每层取 1~3 位数字，判断是否合法（无前导零、≤255），递归处理剩余部分。已取满 4 段且用完字符串则收集。

## 代码

```java
public List<String> restoreIpAddresses(String s) {
    List<String> res = new ArrayList<>();
    backtrack(s, 0, new ArrayList<>(), res);
    return res;
}

private void backtrack(String s, int start, List<String> segments, List<String> res) {
    if (segments.size() == 4 && start == s.length()) {
        res.add(String.join(".", segments));
        return;
    }
    if (segments.size() == 4 || start == s.length()) return;

    for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
        String seg = s.substring(start, start + len);
        if (isValid(seg)) {
            segments.add(seg);
            backtrack(s, start + len, segments, res);
            segments.remove(segments.size() - 1);
        }
    }
}

private boolean isValid(String seg) {
    if (seg.length() > 1 && seg.charAt(0) == '0') return false;  // 前导零
    int val = Integer.parseInt(seg);
    return val >= 0 && val <= 255;
}
```

## 复杂度

- **时间**：O(3^4) = O(1) —— 最多 3^4 种分割，常数
- **空间**：O(1) —— 递归栈深度 ≤ 4

## 边界条件

- 长度 < 4 或 > 12：没有合法 IP，返回空
- "0000"：唯一合法分割 "0.0.0.0"
- "010010"：返回 ["0.10.0.10","0.100.1.0"]

## 变式

- **[131. 分割回文串](131-palindrome-partitioning.md)**：类似的分割类回溯，只是合法性判定从"回文"换成"IP 段"
- **IPv6**：8 段 16 进制，本质相同

## 易错点

- **前导零检查**：`"01"` 不是合法 IP 段（除非段就是 "0" 本身），`seg.length() > 1 && seg.charAt(0) == '0'` 是核心判断
- 字符串长度约束：len 范围 1~3 且 `start + len <= s.length()`
- 递归终止条件同时满足"4 段"和"用完字符串"才收集——否则要么 IP 不完整要么字符串没消费完

## 面试追问

- **合法的 IP 段还有哪些约束？** 0~255、无前导零。面试中可以说"按 IPv4 规范，不允许前导零但允许单独一个 0"

## 关联题

- 同套路：[131. 分割回文串](131-palindrome-partitioning.md) —— 分割类回溯
- 进阶：[17. 电话号码的字母组合](17-letter-combinations-of-a-phone-number.md) —— 另一种字符串组合回溯
- 知识点：字符串分割 + 合法性剪枝见[回溯](algorithms/10-回溯/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
