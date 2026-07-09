# 3. 无重复字符的最长子串（Longest Substring Without Repeating Characters）

频次 ★★★★★ · 难度 🟡 · 高频：全厂

## 题目

求字符串中不含重复字符的**最长子串**长度。

**示例**：
```
输入: "abcabcbb"
输出: 3    （"abc"）
```

## 思路

滑动窗口 + 哈希：窗口 `[l, r]` 内保持无重复。r 右移纳入新字符；一旦重复，l 右移收缩直到无重复。每个字符最多进出窗口各一次，O(n)。

加速版：用 `map<字符, 最近下标>`，遇到重复直接把 l **跳**到 `map[c] + 1`，免去逐步收缩。

## 代码

```java
public int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> last = new HashMap<>();  // 字符 -> 最近出现下标
    int best = 0;
    for (int l = 0, r = 0; r < s.length(); r++) {
        char c = s.charAt(r);
        if (last.containsKey(c)) {
            l = Math.max(l, last.get(c) + 1);  // 只能前跳,不能回退!
        }
        last.put(c, r);
        best = Math.max(best, r - l + 1);
    }
    return best;
}
```

## 复杂度

- **时间**：O(n) —— r 单向走一遍，l 只前进
- **空间**：O(min(n, 字符集)) —— ASCII 场景可换 `int[128]`

## 边界条件

- 空串：返回 0
- 全相同字符（"bbbb"）：答案 1
- 无重复整串：答案为串长，best 在最后一步更新

## 变式

- 输出子串本身：更新 best 时记录 l
- 至多允许 k 个重复/至多 k 种字符（340）：哈希值改计数，收缩条件改为"种类 > k"
- 数据流场景：窗口思想不变，配合淘汰策略限制 map 大小

## 易错点

- `l = Math.max(l, last.get(c) + 1)` 的 max 不能少——重复字符的上次位置可能在窗口外（如 "abba" 处理第二个 a 时 b 的记录会把 l 拉回去）
- `best` 要在每轮都更新，不是只在遇到重复时
- 窗口长度是 `r - l + 1`，off-by-one 高发

## 面试追问

- **为什么是 O(n) 而不是 O(n²)？** 两个指针都只单向移动，各走至多 n 步；答题时点出"每个字符最多进出窗口一次"这个摊还视角
- **滑动窗口适用的前提？** 窗口的合法性随扩张单调变坏、随收缩单调变好（无重复性质满足）；若无此单调性（如带负数的和），窗口失效，见 [560. 和为 K 的子数组](560-subarray-sum-equals-k.md) 的对比

## 关联题

- 同套路：[438. 找到字符串中所有字母异位词](438-find-all-anagrams-in-a-string.md)、[567. 字符串的排列](567-permutation-in-string.md) —— 定长窗口版
- 进阶：[76. 最小覆盖子串](76-minimum-window-substring.md) —— 从"求最长"变"求最短"，收缩逻辑反转
- 知识点：滑动窗口 = 双指针 + 窗口状态维护，专题见[滑动窗口](algorithms/06-滑动窗口/README.md)

