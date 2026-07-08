# 503. 下一个更大元素 II（Next Greater Element II）

频次 ★★★ · 难度 🟡 · 高频：美团

## 题目

循环数组，找每个元素的下一个更大元素（不存在则 -1）。

**示例**：
```
输入: nums = [1,2,1]
输出: [2,-1,2]
```

## 思路

**单调栈 + 循环数组**：遍历下标 `2 × n - 1` 次，取模映射到循环数组。单调递减栈存下标，在第一次遍历时确定答案。

## 代码

```java
public int[] nextGreaterElements(int[] nums) {
    int n = nums.length;
    int[] res = new int[n];
    Arrays.fill(res, -1);
    Deque<Integer> stack = new ArrayDeque<>();   // 存下标
    for (int i = 0; i < 2 * n; i++) {
        while (!stack.isEmpty() && nums[stack.peek()] < nums[i % n]) {
            res[stack.pop()] = nums[i % n];
        }
        if (i < n) stack.push(i);                // 只在第一轮入栈
    }
    return res;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(n)

## 边界条件

- 全递减：res 全部保持 -1
- 单元素：栈空，输出 [-1]

## 变式

- **[496. 下一个更大元素 I](496-next-greater-element-i.md)**：非循环 + 子集
- **[739. 每日温度](739-daily-temperatures.md)**：下一个更大元素的下标距离

## 易错点

- 用 `2 × n` 遍历模拟循环数组的"循环效果"
- 入栈仅在第一次遍历时（`i < n`），第二次只是为了弹栈——如果第二次也入栈会导致结果被覆盖
- res 初值 -1，没有被弹出的栈元素保持 -1

## 面试追问

- **循环数组的标准处理方式？** 两种：遍历 `2n` 长度取模，或数组拼接后处理。`2n` 遍历更省空间

## 关联题

- 同套路：[496. 下一个更大元素 I](496-next-greater-element-i.md) —— 非循环版
- 进阶：[84. 柱状图中最大矩形](84-largest-rectangle-in-histogram.md) —— 单调栈扩展应用
- 知识点：循环数组单调栈模板见[单调栈与单调队列](algorithms/15-单调栈与单调队列/README.md)

---

[← 返回训练计划](社招算法训练计划.md)
