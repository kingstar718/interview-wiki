# 560. Subarray Sum Equals K (Medium)

## 题目

给定一个整数数组和一个整数 `k`，找到和为 `k` 的连续子数组的个数。

**示例**：
```
输入: nums = [1, 1, 1], k = 2
输出: 2
解释: [1,1] 出现两次（索引 [0,1] 和 [1,2]）
```

## 思路

**前缀和 + HashMap**：
- `prefixSum[i]` 表示 `nums[0..i-1]` 的和
- 子数组 `nums[j..i]` 的和 = `prefixSum[i+1] - prefixSum[j]`
- 要找 `prefixSum[i+1] - prefixSum[j] = k`，即 `prefixSum[j] = prefixSum[i+1] - k`
- 用 HashMap 记录每个前缀和出现的次数

初始化 `map.put(0, 1)` 处理从索引 0 开始的子数组。

## 代码

```java
public int subarraySum(int[] nums, int k) {
    int count = 0, prefixSum = 0;
    Map<Integer, Integer> map = new HashMap<>();
    map.put(0, 1);
    for (int num : nums) {
        prefixSum += num;
        count += map.getOrDefault(prefixSum - k, 0);
        map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1);
    }
    return count;
}
```

## 复杂度

- **时间**：O(n) — 一次遍历
- **空间**：O(n) — HashMap 最多存 n 个前缀和

---

[[社招算法训练计划#第 1 周数组基础|← 返回训练计划]]
