# 42. 接雨水（Trapping Rain Water）

频次 ★★★★★ · 难度 🔴 · 高频：字节/阿里/美团

## 题目

给定柱状高度数组，求下雨后能接多少水。

**示例**：
```
输入: [0,1,0,2,1,0,1,3,2,1,2,1]
输出: 6
```

## 思路

每个位置能接的水 = `min(左侧最高, 右侧最高) − 自身高度`（木桶短板）。三个层次的解法：

1. **按列暴力**：每列向两边扫最高，O(n²)
2. **DP 预处理**：`leftMax[i]`、`rightMax[i]` 两个数组，O(n) 时间 O(n) 空间
3. **双指针**（主推）：l/r 向中间收，维护 `leftMax/rightMax` 两个变量——**哪边的 max 小，哪边的水位就已确定**（矮的一侧不用等对面信息：`min` 已经被自己这边锁死），当场结算并前进

## 代码

```java
public int trap(int[] height) {
    int l = 0, r = height.length - 1;
    int leftMax = 0, rightMax = 0, water = 0;
    while (l < r) {
        leftMax = Math.max(leftMax, height[l]);
        rightMax = Math.max(rightMax, height[r]);
        if (leftMax < rightMax) {          // 左侧水位已确定
            water += leftMax - height[l];
            l++;
        } else {                            // 右侧水位已确定
            water += rightMax - height[r];
            r--;
        }
    }
    return water;
}
```

## 复杂度

- **时间**：O(n) —— 每步结算一列
- **空间**：O(1) —— 相比 DP 版省掉两个数组

## 边界条件

- 长度 < 3：接不了水，返回 0
- 单调递增/递减：没有凹槽，结果 0
- 首尾是最高柱：中间全按短边结算，逻辑不变

## 变式

- **单调栈解法**：横向按层结算——栈内递减，遇到更高柱弹栈，凹槽宽 ×（min(两壁) − 槽底），与 [84. 柱状图中最大矩形](84-largest-rectangle-in-histogram.md) 同款结构
- 407. 接雨水 II（二维）：双指针失效，从边界最矮处用小顶堆向内灌水

## 易错点

- 双指针版先更新 `leftMax/rightMax` 再比较，顺序反了会把当前柱漏进 max
- 结算条件比较的是 **leftMax 和 rightMax**，不是 height[l] 和 height[r]——后者在部分用例碰巧对，面试会被抠
- `water += leftMax - height[l]` 不会为负：leftMax 刚被当前柱更新过，最小为 0

## 面试追问

- **为什么矮侧可以直接结算，不怕右边更矮吗？** 结算条件是 `leftMax < rightMax`，右边已经出现过更高的墙，`min` 由左侧锁定；右边后续再矮也不影响本列水位——答不出这句证明就只能写 DP 版
- **三种解法怎么选？** 面试先给双指针 O(1) 空间；追问单调栈就切"按层结算"视角；两个视角一纵一横，能都讲清是加分项

## 关联题

- 同套路：[11. 盛最多水的容器](11-container-with-most-water.md) —— 同为首尾双指针，但那题只选两板，本题全体柱子参与
- 进阶：407. 接雨水 II —— 堆版"从最矮边界灌水"；[84. 柱状图中最大矩形](84-largest-rectangle-in-histogram.md) —— 单调栈视角的姊妹题
- 知识点：木桶短板模型；单调栈专题见[单调栈与单调队列](algorithms/15-单调栈与单调队列/README.md)

