# 54. Spiral Matrix (Medium)

## 题目

给定一个 m × n 的矩阵，按照螺旋顺序返回所有元素。

**示例**：
```
输入: matrix = [[1,2,3],[4,5,6],[7,8,9]]
输出: [1,2,3,6,9,8,7,4,5]
```

## 思路

**边界收缩法**：
- 维护四个边界：`top`, `bottom`, `left`, `right`
- 按顺序遍历：上边（左→右）→ 右边（上→下）→ 下边（右→左）→ 左边（下→上）
- 每遍历完一条边，收缩对应边界
- 直到边界交叉

## 代码

```java
public List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> result = new ArrayList<>();
    if (matrix == null || matrix.length == 0) return result;
    int top = 0, bottom = matrix.length - 1, left = 0, right = matrix[0].length - 1;
    
    while (top <= bottom && left <= right) {
        // 上边
        for (int j = left; j <= right; j++) result.add(matrix[top][j]);
        top++;
        // 右边
        for (int i = top; i <= bottom; i++) result.add(matrix[i][right]);
        right--;
        // 下边
        if (top <= bottom) {
            for (int j = right; j >= left; j--) result.add(matrix[bottom][j]);
            bottom--;
        }
        // 左边
        if (left <= right) {
            for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
            left++;
        }
    }
    return result;
}
```

## 复杂度

- **时间**：O(m × n) — 每个元素访问一次
- **空间**：O(1) — 不计输出数组

---

[← 返回训练计划](../社招算法训练计划.md)
