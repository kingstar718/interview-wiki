# 48. Rotate Image (Medium)

## 题目

给定一个 n × n 的二维矩阵表示图像，将图像顺时针旋转 90 度。必须**原地**旋转，不能使用另一个二维数组。

**示例**：
```
输入: matrix = [[1,2,3],[4,5,6],[7,8,9]]
输出: [[7,4,1],[8,5,2],[9,6,3]]
```

## 思路

两步法：
1. **转置**（沿主对角线翻转）：`matrix[i][j]` ↔ `matrix[j][i]`
2. **水平翻转**：每行左右对调

例如：
```
原矩阵:    转置后:     翻转后:
1 2 3     1 4 7       7 4 1
4 5 6  →  2 5 8  →    8 5 2
7 8 9     3 6 9       9 6 3
```

## 代码

```java
public void rotate(int[][] matrix) {
    int n = matrix.length;
    // 转置
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            int temp = matrix[i][j];
            matrix[i][j] = matrix[j][i];
            matrix[j][i] = temp;
        }
    }
    // 水平翻转
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < n / 2; j++) {
            int temp = matrix[i][j];
            matrix[i][j] = matrix[i][n - 1 - j];
            matrix[i][n - 1 - j] = temp;
        }
    }
}
```

## 复杂度

- **时间**：O(n²) — 转置和翻转各遍历矩阵一次
- **空间**：O(1) — 原地操作

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
