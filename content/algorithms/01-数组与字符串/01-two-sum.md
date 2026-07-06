# 1. Two Sum (Easy)

## 题目

给定一个整数数组 `nums` 和一个目标值 `target`，找出两个数之和等于 `target` 的索引。假设每组只有唯一解，不能重复使用同一个元素。

**示例**：
```
输入: nums = [2, 7, 11, 15], target = 9
输出: [0, 1]
解释: nums[0] + nums[1] == 9
```

## 思路

用 HashMap 记录 `{值 -> 索引}`，遍历时检查 `target - nums[i]` 是否已存在。一次遍历完成。

## 代码

```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (map.containsKey(complement)) {
            return new int[]{map.get(complement), i};
        }
        map.put(nums[i], i);
    }
    throw new IllegalArgumentException("no solution");
}
```

## 复杂度

- **时间**：O(n) — 每个元素访问一次，HashMap 查找和插入均为 O(1)
- **空间**：O(n) — HashMap 最多存储 n 个元素

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
