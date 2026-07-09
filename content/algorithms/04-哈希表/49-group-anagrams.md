# 49. 字母异位词分组（Group Anagrams）

频次 ★★★★ · 难度 🟡 · 高频：美团/字节

## 题目

给定字符串数组，把互为字母异位词（字母相同、顺序不同）的字符串分到一组。

**示例**：
```
输入: ["eat","tea","tan","ate","nat","bat"]
输出: [["eat","tea","ate"],["tan","nat"],["bat"]]
```

## 思路

异位词分组的核心是给每个字符串算一个**规范化 key**——互为异位词的字符串 key 必须相同、否则必须不同，然后用 HashMap 按 key 聚桶：

- **排序键**：把字符排序后的字符串当 key（`"eat" → "aet"`），实现最简单
- **计数键**：26 位字母计数拼成字符串当 key（`a1e1t1`），单串 O(k) 优于排序的 O(k log k)

## 代码

```java
public List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> groups = new HashMap<>();
    for (String s : strs) {
        char[] cs = s.toCharArray();
        Arrays.sort(cs);                       // 规范化:异位词排序后相同
        String key = new String(cs);
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(groups.values());
}
```

## 复杂度

- **时间**：O(n·k log k)，n 为字符串个数，k 为最大长度；计数键版为 O(n·k)
- **空间**：O(n·k)，哈希表存全部字符串

## 边界条件

- 空字符串 `""`：排序键还是 `""`，正常聚为一组
- 单个字符串：返回一个单元素组
- 全部互为异位词：只有一个桶

## 变式

- **计数键版**：`int[26]` 统计后拼接 `"1#0#2#..."`，注意必须带分隔符
- 字符集不限小写字母（Unicode）时：排序键仍然通用；计数键要换成 `Map<Character,Integer>`，得不偿失
- [438. 找到字符串中所有字母异位词](438-find-all-anagrams-in-a-string.md)：把"分组"换成"在长串里找异位词子串"，解法变滑动窗口

## 易错点

- 计数键拼接**不带分隔符会撞 key**：`(1,12)` 和 `(11,2)` 都拼成 `"112"`
- 不能直接拿 `int[]` 当 HashMap 的 key——数组的 hashCode/equals 是**身份语义**（比地址），两个内容相同的数组不相等
- `computeIfAbsent` 比先 `containsKey` 再 `put` 少一次查找，也更不容易漏初始化

## 面试追问

- **为什么数组不能当 HashMap 的 key？** 数组没有重写 hashCode/equals，用的是 Object 的身份实现；要么转 String，要么用 `List<Integer>`（重写过）。契约细节见[集合框架](集合框架.md)
- **排序键和计数键怎么选？** k 小两者无差；k 大且字符集固定选计数键 O(k)；字符集开放选排序键。先说清楚 key 的本质是"异位词等价类的规范型"再谈优化

## 关联题

- 同套路：242. 有效的字母异位词 —— 单对判断版，本题是它的批量聚合
- 进阶：[438. 找到字符串中所有字母异位词](438-find-all-anagrams-in-a-string.md) —— 规范化思想 + 滑动窗口
- 知识点：HashMap 的 hashCode/equals 契约与 O(1) 查找，见[集合框架](集合框架.md)

