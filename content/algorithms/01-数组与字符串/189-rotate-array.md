# 189. Rotate Array (Medium)

## 题目

给定一个数组，将数组中的元素向右移动 `k` 个位置，其中 `k` 是非负数。

**示例**：
```
输入: nums = [1,2,3,4,5,6,7], k = 3
输出: [5,6,7,1,2,3,4]
解释: 右移 1 步: [7,1,2,3,4,5,6], 右移 2 步: [6,7,1,2,3,4,5], 右移 3 步: [5,6,7,1,2,3,4]
```

## 思路

**三次翻转法**（最优解）：
1. 翻转整个数组
2. 翻转前 `k` 个元素
3. 翻转剩余部分

例如 `[1,2,3,4,5,6,7]`, k=3：
- 翻转全部 → `[7,6,5,4,3,2,1]`
- 翻转前 3 个 → `[5,6,7,4,3,2,1]`
- 翻转后 4 个 → `[5,6,7,1,2,3,4]` ✓

注意 `k` 可能大于数组长度，需要先 `k %= n`。

## 代码

```java
public void rotate(int[] nums, int k) {
    k %= nums.length;
    reverse(nums, 0, nums.length - 1);
    reverse(nums, 0, k - 1);
    reverse(nums, k, nums.length - 1);
}

private void reverse(int[] nums, int start, int end) {
    while (start < end) {
        int temp = nums[start];
        nums[start] = nums[end];
        nums[end] = temp;
        start++;
        end--;
    }
}
```

## 复杂度

- **时间**：O(n) — 三次翻转，每个元素最多被交换 3 次
- **空间**：O(1) — 原地翻转

---

[← 返回训练计划](../社招算法训练计划.md)
