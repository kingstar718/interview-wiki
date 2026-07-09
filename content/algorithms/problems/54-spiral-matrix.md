---
topics:
  - 数组与字符串
---

# 54. 螺旋矩阵（Spiral Matrix）

频次 ★★★ · 难度 🟡 · 高频：字节

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

## 边界条件

- 只有一行（`m == 1`）：走完"上边"后 `top++` 使 `top > bottom`，后续"右边""下边""左边"的循环因边界条件不满足而跳过（"下边"和"左边"额外有 `if` 保护，避免重复遍历同一行）。
- 只有一列（`n == 1`）：类似地，走完"上边"（其实只有一个元素）后 `right--` 使 `left > right`，同样靠 `if` 保护避免"左边"重复遍历。
- 空矩阵：开头 `matrix.length == 0` 直接返回空列表。

## 变式

- **逆时针螺旋**：遍历顺序换成"左边→下边→右边→上边"，边界收缩逻辑对称地调整即可。
- **按螺旋顺序填数**（LeetCode 59 题，生成螺旋矩阵）：用同样的边界收缩框架，把"读取 `matrix[i][j]`"换成"写入递增的数字"。

## 易错点

- "下边"和"左边"这两步必须加 `if (top <= bottom)` / `if (left <= right)` 判断，否则在矩阵只有一行或一列时会把同一行/列重复添加一次——这是本题最容易出错、且不容易在简单用例里被发现的地方（必须用只有一行或一列的用例专门测试）。
- 四个边界变量的收缩顺序（先走完一条边再收缩对应边界）不能打乱，收缩早了会漏掉本该访问的最后一个元素。

## 面试追问

- **为什么"下边"和"左边"需要额外判断，而"上边""右边"不需要？** 因为遍历顺序是"上→右→下→左"，走到"下边""左边"时，`top`/`right` 已经在前两步被收缩过，如果矩阵退化成一行或一列，此时 `top > bottom` 或 `left > right` 可能已经成立，不加判断会重复遍历第一步已经访问过的行/列。
- **除了边界收缩法，还有别的实现思路吗？** 可以用"方向数组 + 碰壁转向"的模拟法：维护当前方向（右→下→左→上循环切换），每走一步检查下一步是否越界或已访问过（需要额外的 visited 矩阵），越界或已访问就顺时针转向；这种写法更通用（比如螺旋起点不在角上的变式），但需要 O(m×n) 的 visited 空间，不如边界收缩法简洁。

## 关联题

- 同套路：[48. 旋转图像](48-rotate-image.md) —— 按层收缩边界
- 进阶：59. 螺旋矩阵 II —— 生成版，同一套四边界收缩模板
- 知识点：上下左右四边界 + 收缩顺序固定，是所有"绕圈"矩阵题的通用骨架

