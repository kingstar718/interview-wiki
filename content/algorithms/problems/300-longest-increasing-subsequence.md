---
topics:
  - 动态规划与贪心
techniques:
  - 线性DP
---

# 300. 最长递增子序列（Longest Increasing Subsequence）

频次 ★★★★★ · 难度 🟡 · 高频：字节/阿里/美团

## 题目

整数数组 nums，找最长严格递增子序列（可不连续）的长度。

**示例**：
```
输入: nums = [10,9,2,5,3,7,101,18]
输出: 4  （[2,3,7,101]）
```

## 思路

**两种解法**：

**解法 1：O(n²) DP** — `dp[i]` 为以 nums[i] 结尾的 LIS 长度。`dp[i] = max(dp[j] + 1 for j < i if nums[j] < nums[i])`。

**解法 2：O(n log n) 贪心 + 二分** — 维护一个数组 tails，tails[k] 表示长度为 k+1 的递增子序列的**最小末尾值**。对每个 nums[i]，二分查找它在 tails 中的位置并替换。

这道题 O(n log n) 解法是高频考点。

## 代码

```java
// O(n log n) 贪心 + 二分
public int lengthOfLIS(int[] nums) {
    int[] tails = new int[nums.length];
    int len = 0;                              // tails 当前长度
    for (int x : nums) {
        int i = Arrays.binarySearch(tails, 0, len, x);
        if (i < 0) i = -(i + 1);              // 二分插入点
        tails[i] = x;
        if (i == len) len++;
    }
    return len;
}
```

## 两种一维 dp 语义对照（子序列组开篇课，实测）

```
198 前缀扫描型：dp[i]="前 i 个的最优"——转移只有一个邻居（禁令锁死），answer=dp[n-1]
300 结尾锚定型：dp[i]="以 nums[i] 结尾"——转移是 max over 全部 j<i（可跳选），
                answer=全表 max（最优列结尾位置不定）
为何必须锚定：子序列可跳 → 第 i+1 元素要"接尾巴"，前缀型不知结尾是谁 → 接不上

O(n log n) 提一句（贪心+二分）：tails[k]=长度 k+1 的最小末尾；二分找位置替换/追加；
len 即答案（tails 内容是"幻觉序列"，非真实子序列——面试点破加分的细节）
```

## 复杂度

- **时间**：O(n log n) —— 每次二分 O(log n)
- **空间**：O(n)

## 边界条件

- 空数组：返回 0
- 全降序：每步 tails 都被覆盖在第 0 位，len = 1
- 全升序：tails 顺序增长，len = n

## 变式

- **[673. 最长递增子序列的个数](https://leetcode.cn/problems/number-of-longest-increasing-subsequence/)**：DP 基础上多维护一个 count 数组
- **[354. 俄罗斯套娃信封问题](https://leetcode.cn/problems/russian-doll-envelopes/)**：定宽排序后对高做 LIS
- **[674. 最长连续递增子序列](https://leetcode.cn/problems/longest-continuous-increasing-subsequence/)**：要求连续，一次遍历即可

## 易错点

- **tails 中的顺序不是正确的 LIS 序列**——它只维护了每个长度的最小末尾，不能用于还原子序列。面试追问如果问还原，需要回溯 parent 指针或另开一个 DP 数组记录
- `binarySearch` 没找到时返回 `-(insertionPoint) - 1`
- 二分条件是 `<`（严格递增），如果要求非递减需要改比较条件

## 面试追问

- **O(n²) DP 写法？** 双重循环，面试中作为"先给一个简单解"的铺垫，再优化到 O(n log n)

## 关联题

- 同套路：[354. 俄罗斯套娃信封问题](https://leetcode.cn/problems/russian-doll-envelopes/) —— 二维转一维 LIS
- 进阶：[1143. 最长公共子序列](1143-longest-common-subsequence.md) —— 二维 DP 的 LCS
- 知识点：DP 最优子结构 + 贪心二分优化见[动态规划](../动态规划与贪心.md)

