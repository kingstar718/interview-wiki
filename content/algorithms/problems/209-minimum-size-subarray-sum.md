# 209. 长度最小的子数组（Minimum Size Subarray Sum）

频次 ★★★ · 难度 🟡 · 高频：字节/阿里

## 题目

给定正整数数组 `nums` 和正整数 `target`，找出满足和 ≥ target 的**长度最小的连续子数组**，返回其长度。不存在则返回 0。

## 思路

**滑动窗口**：`right` 扩展窗口累加和，当和 ≥ target 时，收缩 `left` 并更新最小长度。每个元素进一次出一，O(n)。

## 代码

```java
public int minSubArrayLen(int target, int[] nums) {
    int left = 0, sum = 0, minLen = Integer.MAX_VALUE;
    for (int right = 0; right < nums.length; right++) {
        sum += nums[right];
        while (sum >= target) {
            minLen = Math.min(minLen, right - left + 1);
            sum -= nums[left++];
        }
    }
    return minLen == Integer.MAX_VALUE ? 0 : minLen;
}
```

## 复杂度

- **时间**：O(n) — 每个元素最多入窗一次、出窗一次
- **空间**：O(1)

## 边界条件

- 所有元素和 < target：返回 0
- 单个元素 ≥ target：minLen = 1
- 空数组：直接返回 0

## 变式

- 要求返回子数组本身而非长度：多维护 `start`/`end` 记录最优窗口
- 数组含负数则滑动窗口失效（收缩时和不一定减小），改用前缀和 + 单调队列

## 易错点

- 内层用 `while` 而非 `if`：可能连续收缩多次
- `minLen` 初始化为 `MAX_VALUE` 而非 `nums.length`，避免恰好全数组和=target 时误判

## 面试追问

- **为什么不用前缀和+二分？** 也可以 O(nlogn)，但滑动窗口 O(n) 更优；前缀和+二分适用于数组含负数或需要多次查询的场景
- **与 3. 无重复字符的最长子串 的异同？** 都是滑动窗口，但 3 是"最长满足条件"，本题是"最短满足条件"——收缩时机恰好相反

## 关联题

- 同套路：[3. 无重复字符的最长子串](3-longest-substring-without-repeating-characters.md)、[76. 最小覆盖子串](76-minimum-window-substring.md)
- 知识点：滑动窗口模板见 [滑动窗口专题](algorithms/06-滑动窗口/README.md)