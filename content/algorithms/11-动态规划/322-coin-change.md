# 322. 零钱兑换（Coin Change）

频次 ★★★★★ · 难度 🟡 · 高频：字节/美团/阿里

## 题目

不同面额硬币 coins（无限供应），凑目标金额 amount 所需的最少硬币数，无法凑出返回 -1。

**示例**：
```
输入: coins = [1,2,5], amount = 11
输出: 3  （5+5+1）
```

## 思路

**完全背包问题（DP）**：`dp[i]` 表示凑 i 元的最少硬币数。

`dp[i] = min(dp[i], dp[i - coin] + 1) for each coin ≤ i`

初始化 dp[0] = 0，其余为 INF（amount + 1 即可）。

## 代码

```java
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);          // 上界（不可能达到）
    dp[0] = 0;
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
```

## 复杂度

- **时间**：O(amount × n) —— n 为硬币种类数
- **空间**：O(amount)

## 边界条件

- amount = 0：返回 0
- 无解（如 [2], amount = 3）：dp[3] 保持 INF → 返回 -1
- 大面额硬币超过 amount：不影响

## 变式

- **[518. 零钱兑换 II](https://leetcode.cn/problems/coin-change-ii/)**：求凑法种数（完全背包计数），`dp[i] += dp[i-coin]`
- **[70. 爬楼梯](70-climbing-stairs.md)**：每步可走 coin 种步数，问方案数

## 易错点

- **INF 初始化**：`amount + 1` 是因为最多 amount 枚硬币（全部用 1 元），所以 amount+1 肯定不可达
- 内层循环顺序：零钱兑换**最少硬币数**（完全背包**求最值**）对顺序不敏感，外面 for coin 或外面 for amount 都行。但如果是**求组合数**（518），外面 for coin 防重复计数
- 返回前检查 `dp[amount] > amount`

## 面试追问

- **为什么 518 换顺序就会重复计数？** 外层 for coin 保证"先选定面额再遍历金额"，每种组合只被计数一次（coin 的选取有顺序）。最少硬币数是最值，无所谓顺序

## 关联题

- 同套路：[518. 零钱兑换 II](https://leetcode.cn/problems/coin-change-ii/) —— 计数版
- 进阶：[300. 最长递增子序列](300-longest-increasing-subsequence.md) —— 一维 DP 另一模式
- 知识点：完全背包问题（求最值 vs 计数）见[动态规划](algorithms/11-动态规划/README.md)

