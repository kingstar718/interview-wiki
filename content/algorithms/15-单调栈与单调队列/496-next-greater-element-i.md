# 496. 下一个更大元素 I（Next Greater Element I）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

nums1 是 nums2 的子集，对 nums1 每个元素，在 nums2 中找它右边第一个更大的值，不存在则 -1。

**示例**：
```
输入: nums1 = [4,1,2], nums2 = [1,3,4,2]
输出: [-1,3,-1]
```

## 思路

**单调栈 + 哈希**：先对 nums2 做单调递减栈，计算出每个元素的下一个更大元素并存入 HashMap，然后遍历 nums1 查表。

## 代码

```java
public int[] nextGreaterElement(int[] nums1, int[] nums2) {
    Map<Integer, Integer> map = new HashMap<>();
    Deque<Integer> stack = new ArrayDeque<>();
    for (int n : nums2) {
        while (!stack.isEmpty() && stack.peek() < n) {
            map.put(stack.pop(), n);       // 栈顶的下一个更大元素是 n
        }
        stack.push(n);
    }
    // 栈中剩余元素没有下一个更大元素（已经在 map 中缺省时返回 -1）

    int[] res = new int[nums1.length];
    for (int i = 0; i < nums1.length; i++) {
        res[i] = map.getOrDefault(nums1[i], -1);
    }
    return res;
}
```

## 复杂度

- **时间**：O(n1 + n2)
- **空间**：O(n2)

## 边界条件

- 空数组：返回空
- 递减序列：全部 -1

## 变式

- **[503. 下一个更大元素 II](503-next-greater-element-ii.md)**：循环数组，遍历两倍长度
- **[739. 每日温度](739-daily-temperatures.md)**：求距离而不是值本身

## 易错点

- 单调栈存的是值还是下标取决于题目：本题存值就行（只需知道值是多少）
- 哈希表只存有下一个更大的元素，不存在时用 getOrDefault 默认 -1

## 面试追问

- **单调栈存下标和存值的区别？** 存下标更通用（能推导位置和值），本题只关心值所以存值即可

## 关联题

- 同套路：[503. 下一个更大元素 II](503-next-greater-element-ii.md) —— 循环数组版
- 进阶：[739. 每日温度](739-daily-temperatures.md) —— 距离版
- 知识点：单调栈模板见[单调栈与单调队列](algorithms/15-单调栈与单调队列/README.md)

