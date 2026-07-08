# 304. 二维区域和检索 - 矩阵不可变（Range Sum Query 2D - Immutable）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

二维矩阵不可变，多次查询 `(r1,c1)-(r2,c2)` 子矩阵和。

## 思路

**二维前缀和**：`pre[i+1][j+1] = pre[i][j+1] + pre[i+1][j] - pre[i][j] + matrix[i][j]`。

查询：`sum = pre[r2+1][c2+1] - pre[r1][c2+1] - pre[r2+1][c1] + pre[r1][c1]`。

## 代码

```java
class NumMatrix {
    private int[][] pre;
    public NumMatrix(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        pre = new int[m + 1][n + 1];
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                pre[i+1][j+1] = pre[i][j+1] + pre[i+1][j] - pre[i][j] + matrix[i][j];
    }
    public int sumRegion(int r1, int c1, int r2, int c2) {
        return pre[r2+1][c2+1] - pre[r1][c2+1] - pre[r2+1][c1] + pre[r1][c1];
    }
}
```

## 复杂度

- **时间**：O(mn) 初始化，O(1) 查询
- **空间**：O(mn)

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[303. 区域和检索](303-range-sum-query-immutable.md)、[560. 和为 K 的子数组](560-subarray-sum-equals-k.md)

---

[← 返回训练计划](社招算法训练计划.md)
