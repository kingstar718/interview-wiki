---
topics:
  - 动态规划与贪心
techniques:
  - 二维DP
---

# 221. 最大正方形（Maximal Square）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

m×n 二进制矩阵（'0' 和 '1'），找到只包含 '1' 的最大正方形，返回其面积。

**示例**：
```
输入:
[["1","0","1","0","0"],
 ["1","0","1","1","1"],
 ["1","1","1","1","1"],
 ["1","0","0","1","0"]]
输出: 4  （右下角 2×2 正方形）
```

## 思路

**二维 DP**：`dp[i][j]` 表示以 `(i,j)` 为右下角的最大全 '1' 正方形边长。

转移方程：`dp[i][j] = min(dp[i-1][j], dp[i][j-1], dp[i-1][j-1]) + 1`，当 `matrix[i][j] == '1'` 时。

直观理解：要形成一个更大的正方形，左上、上方、左方三个邻居都得有足够的边长。

空间优化：滚动数组，只保留上一行即可降到 O(n)。

## 代码

```java
public int maximalSquare(char[][] matrix) {
    if (matrix == null || matrix.length == 0 || matrix[0].length == 0)
        return 0;
    int m = matrix.length, n = matrix[0].length;
    int[] dp = new int[n + 1];         // 空间优化：一维滚动
    int max = 0, prev = 0;             // prev 记录 dp[j] 的旧值（即 dp[i-1][j-1]）
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            int temp = dp[j + 1];      // 保存旧值给下一轮作为左上角
            if (matrix[i][j] == '1') {
                dp[j + 1] = Math.min(Math.min(dp[j + 1], dp[j]), prev) + 1;
                max = Math.max(max, dp[j + 1]);
            } else {
                dp[j + 1] = 0;
            }
            prev = temp;
        }
    }
    return max * max;
}
```

## 复杂度

- **时间**：O(m × n) —— 遍历每个元素一次
- **空间**：O(n) —— 滚动数组

## 边界条件

- 空矩阵：返回 0
- 单行或单列：若有 '1' 返回 1，否则 0
- 全 '0' 矩阵：dp 全为 0，max = 0，返回 0

## 变式

- **[85. 最大矩形](https://leetcode.cn/problems/maximal-rectangle/)**：不要求正方形，求最大矩形，转化为每行「以当前行为底边的柱状图」+ 84 题「最大矩形面积」
- **[1277. 统计全为 1 的正方形子矩阵](https://leetcode.cn/problems/count-square-submatrices-with-all-ones/)**：统计所有大小的正方形个数，DP 方程相同，累加 dp[i][j] 即可

## 易错点

- **char 类型**：matrix[i][j] 是 '1' 或 '0'，不是整数，比较时用字符常量
- dp[j+1] 在更新前保存的 temp 作为下一轮的左上角（dp[i-1][j-1]），注意滚动数组的错位索引
- 返回 `max * max` 而非 max——题目要求面积（边长的平方）

## 面试追问

- **DP 方程"取 min 三个邻居"的直觉理解？** 以 (i,j) 为右下角的大正方形由三个方向的小正方形决定：上方柱子高度、左边宽度、左上方支撑——三者最短的那个就是新正方形的边长。画个 2×2 全 1 矩阵验证：dp[1][1] = min(1,1,1)+1 = 2
- **能不能用其他方法？** 可以：对每个 '1' 做 BFS 扩展（O(m²n²)），或用「每列连续 1 的高度 + 单调栈」求最大矩形（后者可改造成求正方形但不如 DP 直观）
- **如果矩阵很大（10⁴×10⁴）又很稀疏，有什么优化？** 可以用行/列的计数跳过全零区域，或用稀疏矩阵存储，但 DP 本身的 O(mn) 已经最优（至少要读一遍数据）

## 关联题

- 基础：[70. 爬楼梯](70-climbing-stairs.md) —— 一维 DP 热身
- 进阶：[85. 最大矩形](https://leetcode.cn/problems/maximal-rectangle/) —— 转化为柱状图最大面积
- 同套路：[64. 最小路径和](64-minimum-path-sum.md) —— 二维 DP 的另一种转移模式（求和而非取 min 扩展）
- 知识点：二维 DP 的空间压缩技巧「滚动数组」见[动态规划](动态规划与贪心.md)
