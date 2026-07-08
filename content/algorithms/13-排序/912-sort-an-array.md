# 912. 排序数组（Sort an Array）

频次 ★★★★★ · 难度 🟡 · 高频：字节/阿里/美团

## 题目

升序排序整数数组，用**快排**实现（面试中最常要求写的就是快排）。

## 思路

**快速排序（三路切分优化）**：选 pivot，将数组分为 < pivot、== pivot、> pivot 三部分，递归处理前后两部分。三路切分对大量重复元素友好（退化为 O(n) 而非 O(n²)）。

## 代码

```java
public int[] sortArray(int[] nums) {
    quickSort(nums, 0, nums.length - 1);
    return nums;
}

private void quickSort(int[] nums, int l, int r) {
    if (l >= r) return;
    // 随机选 pivot，避免最坏 O(n²)
    int idx = l + (int)(Math.random() * (r - l + 1));
    swap(nums, idx, r);                           // 放到最右边
    int[] p = partition(nums, l, r);              // 三路切分
    quickSort(nums, l, p[0] - 1);
    quickSort(nums, p[1] + 1, r);
}

// 返回 < pivot 的右边界和 > pivot 的左边界
private int[] partition(int[] nums, int l, int r) {
    int pivot = nums[r];
    int i = l, lt = l, gt = r;
    while (i <= gt) {
        if (nums[i] < pivot) swap(nums, i++, lt++);
        else if (nums[i] > pivot) swap(nums, i, gt--);
        else i++;
    }
    return new int[]{lt, gt};
}

private void swap(int[] nums, int i, int j) {
    int t = nums[i]; nums[i] = nums[j]; nums[j] = t;
}
```

## 复杂度

- **时间**：平均 O(n log n)，最坏 O(n²)
- **空间**：O(log n) —— 递归栈

## 边界条件

- 空数组/单元素：直接返回
- 全相同元素：三路切分一次完成，O(n)

## 变式

- **归并排序**：稳定的 O(n log n)，需 O(n) 辅助空间
- **堆排序**：O(n log n) 原地排序
- **[215. 数组中的第K大](215-kth-largest-element-in-an-array.md)**：快排的 partition 思想

## 易错点

- 随机选 pivot 不能省略——有序数组每次选最右是最坏 O(n²)
- 三路切分中 `i` 只在 `nums[i] < pivot` 时前进，`nums[i] > pivot` 时只交换不前进（因为换过来的数还没比较过）
- 桶排序/计数排序只适合小范围整数

## 面试追问

- **快排最坏情况怎么避免？** 随机选 pivot、三路切分、或切换到插入排序（小数组）
- **快排和归并排序的应用场景？** 快排原地排序适合数组；归并稳定、适合链表

## 关联题

- 同套路：215. 数组中的第K大 —— partition 选择
- 进阶：[75. 颜色分类](75-sort-colors.md) —— 三路切分的简化版（只有三类）
- 知识点：排序算法体系对比见[排序](algorithms/13-排序/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
