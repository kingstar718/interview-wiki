---
topics:
  - 动态规划与贪心
techniques:
  - 贪心证明
---

# 135. 分发糖果（Candy）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里/腾讯

## 题目

n 个孩子站成一排，每个孩子至少分到 1 颗糖。评分高的孩子必须比相邻的孩子获得更多糖果。求最少需要多少颗糖果。

**示例**：
```
输入: ratings = [1,0,2]
输出: 5 （分配 [2,1,2]）

输入: ratings = [1,2,2]
输出: 4 （分配 [1,2,1]）
```

## 思路

**贪心 + 两遍扫描**：规则拆成两条独立约束：
1. **左 → 右**：若右边孩子评分更高，则右边糖果 = 左边糖果 + 1
2. **右 → 左**：若左边孩子评分更高，则左边糖果 = max(左边糖果, 右边糖果 + 1)

每遍扫描只保证一侧的局部约束，两遍扫描后取 max 同时满足两侧约束。这是"相邻比较"类贪心的经典范式。

## 代码

```java
public int candy(int[] ratings) {
    int n = ratings.length;
    int[] candies = new int[n];
    Arrays.fill(candies, 1);              // 每人至少 1 颗

    // 左 → 右：右边高分 > 左边
    for (int i = 1; i < n; i++) {
        if (ratings[i] > ratings[i - 1]) {
            candies[i] = candies[i - 1] + 1;
        }
    }

    // 右 → 左：左边高分 > 右边，同时取 max 保证不破坏左→右的结果
    for (int i = n - 2; i >= 0; i--) {
        if (ratings[i] > ratings[i + 1]) {
            candies[i] = Math.max(candies[i], candies[i + 1] + 1);
        }
    }

    int total = 0;
    for (int c : candies) total += c;
    return total;
}
```

## 复杂度

- **时间**：O(n) —— 两次遍历
- **空间**：O(n) —— candies 数组（可优化为 O(1) 但代码复杂，面试不推荐）

## 边界条件

- 只有一个孩子：返回 1
- 评分全部相同：每人 1 颗，返回 n
- 评分严格递增：糖果 1,2,3,...,n，返回 n(n+1)/2
- 评分严格递减：同理，返回 n(n+1)/2
- 评分相等时不必给更多糖果：相邻评分相等可以给不同数量的糖果

## 变式

- **[455. 分发饼干](455-assign-cookies.md)**：不同约束——饼干尺寸 vs 胃口，双指针
- **[42. 接雨水](https://leetcode.cn/problems/trapping-rain-water/)**：同为"两遍扫描"，左→右和右→左各算一次取 min/max

## 易错点

- **第二遍扫描必须取 max**：`candies[i] = Math.max(candies[i], candies[i+1] + 1)` 而不是直接赋值 `candies[i] = candies[i+1] + 1`，否则会覆盖左→右的结果
- **评分相同时糖果不必更多**：`ratings[i] == ratings[i-1]` 时不做任何操作，candies[i] 保持 1
- **两遍扫描不可合并为一遍**：因为右侧约束需要从右向左传播，必须先完成左→右再用右→左修正

## 面试追问

- **为什么两遍扫描能保证正确性？** 题目约束是"相邻比较"，一维传播。左→右保证所有"右边比左边高"的约束；右→左保证所有"左边比右边高"的约束。取 max 同时满足两边
- **空间能优化到 O(1) 吗？** 可以，用递增/递减序列的长度来算，但代码复杂容易出错，面试中 O(n) 空间版本足够

## 关联题

- 同套路：[455. 分发饼干](455-assign-cookies.md) —— 不同贪心策略
- 进阶：[42. 接雨水](https://leetcode.cn/problems/trapping-rain-water/) —— 两遍扫描思路
- 知识点：相邻约束贪心模板见[贪心](动态规划与贪心.md)
