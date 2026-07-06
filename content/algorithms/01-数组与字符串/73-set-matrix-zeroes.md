# 73. Set Matrix Zeroes (Medium)

## 题目

给定一个 m × n 的矩阵，如果某个元素为 0，则将其所在的行和列的所有元素都设为 0。必须**原地**修改。

**示例**：
```
输入: [[1,1,1],[1,0,1],[1,1,1]]
输出: [[1,0,1],[0,0,0],[1,0,1]]
```

## 思路

**利用第一行和第一列作为标记**：
- 先用两个变量记录第一行和第一列是否需要置零
- 遍历除第一行第一列外的区域，如果 `matrix[i][j] == 0`，标记 `matrix[i][0] = 0` 和 `matrix[0][j] = 0`
- 根据第一行和第一列的标记来置零内部区域
- 最后处理第一行和第一列本身

这样空间复杂度从 O(m+n) 降到 O(1)。

## 代码

```java
public void setZeroes(int[][] matrix) {
    int m = matrix.length, n = matrix[0].length;
    boolean firstRowZero = false, firstColZero = false;
    
    // 检查第一行和第一列
    for (int j = 0; j < n; j++) if (matrix[0][j] == 0) firstRowZero = true;
    for (int i = 0; i < m; i++) if (matrix[i][0] == 0) firstColZero = true;
    
    // 用第一行和第一列做标记
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            if (matrix[i][j] == 0) {
                matrix[i][0] = 0;
                matrix[0][j] = 0;
            }
        }
    }
    
    // 根据标记置零
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                matrix[i][j] = 0;
            }
        }
    }
    
    // 处理第一行和第一列
    if (firstRowZero) for (int j = 0; j < n; j++) matrix[0][j] = 0;
    if (firstColZero) for (int i = 0; i < m; i++) matrix[i][0] = 0;
}
```

## 复杂度

- **时间**：O(m × n) — 遍历矩阵
- **空间**：O(1) — 只用两个布尔变量

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
