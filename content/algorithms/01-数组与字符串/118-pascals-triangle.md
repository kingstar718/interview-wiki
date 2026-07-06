# 118. Pascal's Triangle (Easy)

## 题目

给定一个非负整数 `numRows`，生成杨辉三角的前 `numRows` 行。每行第一个和最后一个元素为 1，中间元素等于上一行相邻两个元素之和。

**示例**：
```
输入: numRows = 5
输出: [
  [1],
  [1,1],
  [1,2,1],
  [1,3,3,1],
  [1,4,6,4,1]
]
```

## 思路

逐行构造：
- 第 `i` 行有 `i+1` 个元素
- 首尾固定为 1
- 中间元素 `row[j] = prevRow[j-1] + prevRow[j]`

## 代码

```java
public List<List<Integer>> generate(int numRows) {
    List<List<Integer>> result = new ArrayList<>();
    for (int i = 0; i < numRows; i++) {
        List<Integer> row = new ArrayList<>();
        for (int j = 0; j <= i; j++) {
            if (j == 0 || j == i) {
                row.add(1);
            } else {
                row.add(result.get(i - 1).get(j - 1) + result.get(i - 1).get(j));
            }
        }
        result.add(row);
    }
    return result;
}
```

## 复杂度

- **时间**：O(numRows²) — 第 i 行有 i 个元素，总计 1+2+...+n = O(n²)
- **空间**：O(numRows²) — 输出结果占用，不计入则为 O(1) 额外空间

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
