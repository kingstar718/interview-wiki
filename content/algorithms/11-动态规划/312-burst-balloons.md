# 312. 戳气球（Burst Balloons）

频次 ★★★ · 难度 🔴 · 高频：阿里

## 题目

每个气球有分数，戳破后获得 `nums[left] × nums[i] × nums[right]`（左右为相邻未戳破的气球）。求最大得分。

**示例**：
```
输入: nums = [3,1,5,8]
输出: 167
```

## 思路

**区间 DP（反向思考）**：不戳气球，改为"向区间中添加气球"。

定义 `dp[i][j]` 为戳破 (i, j) 开区间内所有气球的最大得分。枚举最后一个被戳破的气球 k：`dp[i][j] = max(dp[i][k] + dp[k][j] + nums[i]×nums[k]×nums[j])`。

为了处理边界，在原数组左右各加一个 1。

## 代码

```java
public int maxCoins(int[] nums) {
    int n = nums.length;
    int[] val = new int[n + 2];
    val[0] = val[n + 1] = 1;              // 虚拟边界
    for (int i = 0; i < n; i++) val[i + 1] = nums[i];

    int[][] dp = new int[n + 2][n + 2];
    for (int len = 2; len <= n + 1; len++) {           // 区间长度
        for (int i = 0; i + len <= n + 1; i++) {
            int j = i + len;
            for (int k = i + 1; k < j; k++) {
                dp[i][j] = Math.max(dp[i][j],
                    dp[i][k] + dp[k][j] + val[i] * val[k] * val[j]);
            }
        }
    }
    return dp[0][n + 1];
}
```

## 复杂度

- **时间**：O(n³) —— 三层循环
- **空间**：O(n²)

## 边界条件

- 空数组：返回 0
- 单气球：得分就是 nums[0]

## 变式

- 石子合并问题 —— 同样是区间 DP，但合并时加的是区间和

## 易错点

- 区间是**开区间** `(i, j)`，虚拟边界 `val[0] = val[n+1] = 1`，乘起来不影响结果
- 枚举长度从 2 开始（长度 1 的区间没有可戳的气球）
- 最后戳破的气球 k 的枚举范围是 `(i+1) 到 (j-1)`

## 面试追问

- **为什么反着做（添加气球）更方便？** 戳破气球会改变相邻关系，正向很难处理。反着在区间内添加气球，相邻关系确定，DP 就可行了——"逆向思维"在面试中说出来加分

## 关联题

- 同套路：[32. 最长有效括号](32-longest-valid-parentheses.md) —— 同是区间类 DP/栈
- 进阶：区间 DP 的通用模板
- 知识点：区间 DP 的"反向思考"技巧见[动态规划](algorithms/11-动态规划/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
