# 1047. 删除字符串中的所有相邻重复项（Remove All Adjacent Duplicates In String）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

给出由小写字母组成的字符串 `s`，重复项删除操作会选择两个相邻且相同的字母，并删除它们。在 `s` 上反复执行重复项删除操作，直到无法继续删除。返回最终的字符串。

**示例**：
```
输入: s = "abbaca"
输出: "ca"
解释: 删除 "bb" 得到 "aaca"，再删除 "aa" 得到 "ca"
```

## 思路

像消消乐一样，每次删除相邻重复项后可能产生新的相邻重复项，需要"回头看"——这正是**栈**的典型应用：

遍历字符串，栈顶元素与当前字符相同时弹出（消除），否则压入。最后栈中剩余字符即为结果。

用 `StringBuilder` 模拟栈（比 `Deque<Character>` 更省空间，且直接转字符串），`StringBuilder` 的最后一个字符即"栈顶"。

## 代码

```java
// StringBuilder 模拟栈
public String removeDuplicates(String s) {
    StringBuilder sb = new StringBuilder();
    for (char c : s.toCharArray()) {
        int len = sb.length();
        if (len > 0 && sb.charAt(len - 1) == c) {
            sb.deleteCharAt(len - 1); // 弹出（消除）
        } else {
            sb.append(c); // 压入
        }
    }
    return sb.toString();
}
```

```java
// Deque 栈（更直观）
public String removeDuplicates(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
        if (!stack.isEmpty() && stack.peek() == c) {
            stack.pop();
        } else {
            stack.push(c);
        }
    }
    StringBuilder sb = new StringBuilder();
    while (!stack.isEmpty()) {
        sb.append(stack.pollLast()); // 从栈底取，保持原顺序
    }
    return sb.toString();
}
```

## 复杂度

- **时间**：O(n) — 每个字符入栈/出栈最多一次
- **空间**：O(n) — 栈（或 StringBuilder）最多存储所有不重复的字符

## 边界条件

- 空字符串：返回空字符串
- 无相邻重复字符（如 `"abc"`）：所有字符压入栈，返回原字符串
- 全部可消除（如 `"aabb"`）：栈为空，返回空字符串

## 变式

- **双指针原地修改**：用 `slow` 指针表示"栈顶"，`fast` 指针遍历，`chars[slow] == chars[fast]` 则 `slow--`，否则 `chars[++slow] = chars[fast]`。空间 O(1)（除输入数组外），类似 [27. 移除元素](27-remove-element.md) 的快慢指针套路。
- **递归删除**：每次扫描并删除所有相邻重复项，递归直到没有变化，但时间复杂度最坏 O(n²)，不推荐。

## 易错点

- `StringBuilder` 删除最后一个字符用 `deleteCharAt(len - 1)`，不是 `deleteCharAt(len)`——索引从 0 开始
- Deque 版最后拼字符串时，栈顶存的是最后入栈的字符，需要从栈底取（`pollLast()` 或逆序）才能保持原顺序
- 注意 `sb.length()` 在 `deleteCharAt` 后减小，不要用旧的 `len` 变量

## 面试追问

- **为什么这题必须用栈？** 因为删除后可能产生新的相邻重复项，需要"回头看"上一个没有被删除的字符——这正是栈 LIFO 特性（每次只关心最近一个未删除的字符）。用双指针遍历做不到，因为删除后字符位置前移，需要不断回退检查。
- **如果要求原地修改（输入是 char[]）？** 用快慢指针：`slow` 初始为 -1，`fast` 从 0 遍历，匹配时 `slow--`，否则 `chars[++slow] = chars[fast]`。这本质上是把 char[] 的 `[0..slow]` 当作栈来用。

## 关联题

- 同套路：[20. 有效的括号](20-valid-parentheses.md) —— 栈处理"配对消除"的经典模板
- 进阶：1209. 删除字符串中的所有相邻重复项 II —— 每次删除 k 个重复项，栈需要记录字符和连续出现次数
- 知识点：栈的"回看上一个"特性在括号匹配、表达式求值、路径简化等场景中反复出现

---

[← 返回训练计划](社招算法训练计划.md)