# 303. 区域和检索 - 数组不可变（Range Sum Query - Immutable）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

数组不可变，多次查询 `sumRange(l, r)`。

## 思路

**一维前缀和**：预处理 `pre[i] = sum(nums[0..i-1])`，`sumRange(l, r) = pre[r+1] - pre[l]`。

## 代码

```java
class NumArray {
    private int[] pre;
    public NumArray(int[] nums) {
        pre = new int[nums.length + 1];
        for (int i = 0; i < nums.length; i++) pre[i + 1] = pre[i] + nums[i];
    }
    public int sumRange(int l, int r) {
        return pre[r + 1] - pre[l];
    }
}
```

## 复杂度

- **时间**：初始化 O(n)，查询 O(1)
- **空间**：O(n)

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 进阶：[304. 二维区域和检索](304-range-sum-query-2d-immutable.md)、[560. 和为 K 的子数组](560-subarray-sum-equals-k.md)

---

[← 返回训练计划](社招算法训练计划.md)
