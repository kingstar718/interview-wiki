---
topics:
  - 双指针与滑动窗口
---

# 76. 最小覆盖子串（Minimum Window Substring）

频次 ★★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

在 s 中找出**覆盖** t 所有字符（含重数）的最短子串，不存在返回 ""。

**示例**：
```
输入: s = "ADOBECODEBANC", t = "ABC"
输出: "BANC"
```

## 思路

变长滑动窗口的"最短"模板：

1. r 右移**扩张**，直到窗口覆盖 t（`valid == 需要的字母种数`）
2. 一旦覆盖，l 右移**收缩**榨出最短，每步更新答案
3. 收缩到不再覆盖，回到扩张

与"最长"模板的差别：最长题在**合法时**扩张记答案；最短题在**合法时**收缩记答案。

## 代码

```java
public String minWindow(String s, String t) {
    Map<Character, Integer> need = new HashMap<>(), win = new HashMap<>();
    for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);
    int l = 0, valid = 0, start = 0, len = Integer.MAX_VALUE;
    for (int r = 0; r < s.length(); r++) {
        char c = s.charAt(r);
        if (need.containsKey(c)) {
            win.merge(c, 1, Integer::sum);
            if (win.get(c).equals(need.get(c))) valid++;   // 该字母凑够了
        }
        while (valid == need.size()) {          // 覆盖成立,收缩榨最短
            if (r - l + 1 < len) { start = l; len = r - l + 1; }
            char d = s.charAt(l++);
            if (need.containsKey(d)) {
                if (win.get(d).equals(need.get(d))) valid--; // 破坏覆盖的临界点
                win.merge(d, -1, Integer::sum);
            }
        }
    }
    return len == Integer.MAX_VALUE ? "" : s.substring(start, start + len);
}
```

## 复杂度

- **时间**：O(n) —— l、r 各走一遍；哈希操作 O(1)
- **空间**：O(字符集大小)

## 边界条件

- t 比 s 长：valid 永远不满，返回 ""
- t 含重复字符（"AABC"）：need 计数处理，valid 按"凑够重数"才加
- s == t：整串即答案

## 变式

- t 只需被子串的**字符集**覆盖（不管重数）：need 计数全设 1
- 输出所有最短解：len 相等时收集 start
- 数据流版本：无法回头收缩，需要不同结构（不再是滑窗）

## 易错点

- `valid` 统计的是**凑够重数的字母种数**，不是匹配的字符总数——`win.get(c).equals(need.get(c))` 只在恰好达到时 ++ 一次
- Integer 比较必须 `equals`，`==` 在 128 以上失效（Integer 缓存池，经典 Java 坑）
- 收缩时先判临界再减计数，顺序反了 valid 就漂了
- 记录答案用 `start + len`，别在循环里做 substring（O(n) 拷贝）

## 面试追问

- **"最长"和"最短"两套模板的本质区别？** 合法性方向相反：最长题窗口合法时尽量伸、越界才缩；最短题一旦合法立即缩。先答出"覆盖性质随扩张单调变好"这一前提
- **为什么是 O(n) 而内层还有 while？** 摊还：l 总共只前进 n 次，内层循环的总次数被 l 的行程限制——和 [3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md) 同一论证

## 关联题

- 同套路：[3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md) —— 最长模板对照
- 进阶：[438. 找到字符串中所有字母异位词](438-find-all-anagrams-in-a-string.md)、[567. 字符串的排列](567-permutation-in-string.md) —— 把"覆盖"收紧成"恰好相等"，窗口退化为定长
- 知识点：Integer 缓存与 equals 比较的坑，见[Java基础](Java基础.md) int 与 Integer 一节；「内层有 while 却仍是 O(n)」的依据是[摊还](摊还.md)——左指针总共只前进 n 次

