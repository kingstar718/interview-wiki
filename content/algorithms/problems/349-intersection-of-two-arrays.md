---
topics:
  - 哈希表
techniques:
  - 哈希查表
---

# 349. 两个数组的交集（Intersection of Two Arrays）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

给定两个数组 `nums1` 和 `nums2`，返回它们的交集。结果中的每个元素必须唯一，且可以按任意顺序返回。

**示例**：
```
输入: nums1 = [1,2,2,1], nums2 = [2,2]
输出: [2]

输入: nums1 = [4,9,5], nums2 = [9,4,9,8,4]
输出: [9,4]  （或 [4,9]）
```

## 思路

用两个 HashSet：先遍历 `nums1` 把所有元素放入 `set1`（自动去重），再遍历 `nums2`，如果元素在 `set1` 中存在则加入结果 `resultSet`（自动去重），最后将 `resultSet` 转为数组返回。

两个 Set 的设计保证了 O(1) 的查找和结果自动去重，比暴力两层循环 O(n*m) 高效得多。

## 代码

```java
public int[] intersection(int[] nums1, int[] nums2) {
    Set<Integer> set1 = new HashSet<>();
    for (int num : nums1) {
        set1.add(num);
    }
    Set<Integer> resultSet = new HashSet<>();
    for (int num : nums2) {
        if (set1.contains(num)) {
            resultSet.add(num);
        }
    }
    int[] result = new int[resultSet.size()];
    int i = 0;
    for (int num : resultSet) {
        result[i++] = num;
    }
    return result;
}
```

## 复杂度

- **时间**：O(n + m) — 分别遍历两个数组各一次，HashSet 的 add/contains 为 O(1)
- **空间**：O(n + min(n, m)) — `set1` 存 `nums1` 的全部元素，`resultSet` 最多存交集元素数

## 边界条件

- 一个数组为空：交集为空，返回空数组
- 两个数组无交集：`resultSet` 为空，返回空数组
- 数组中有重复元素：两个 Set 自动去重，结果中每个元素只出现一次

## 变式

- **排序 + 双指针**：如果数组已排序或允许排序，可以先排序再用双指针扫描，空间 O(1)（不计排序空间），时间 O(n log n + m log m）。适合内存受限或数组已有序的场景。
- **`retainAll` 一行流**：`set1.retainAll(set2)` 直接求交集，但面试中建议手写逻辑展示对 HashSet 查找的理解。

## 易错点

- 结果需要转为 `int[]` 返回，不能直接返回 `Set` 对象
- 如果只需一个 Set：`set1` 已经存了 `nums1` 的元素，遍历 `nums2` 时找到交集元素后，如果从 `set1` 中移除该元素，可以省去 `resultSet`，但这种方法会修改 `set1`，且如果 `nums2` 有重复元素也不会重复输出（因为移除后 `set1` 不再包含该元素）；两种写法都正确，面试时说清楚取舍即可

## 面试追问

- **如果两个数组都很大（内存放不下），怎么求交集？** 可以外部排序后双指针归并，或者用布隆过滤器（允许一定误判）先过滤再验证。这就是 MapReduce 中求交集的基本思路。
- **如果要求结果保持有序？** 先求交集再排序，或者直接用排序 + 双指针法（天然有序输出）。

## 关联题

- 进阶：350. 两个数组的交集 II —— 不要求结果唯一，需要保留重复元素出现的次数
- 知识点：HashSet 的 O(1) 查找原理见[集合框架](集合框架.md)
