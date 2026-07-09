# 155. 最小栈（Min Stack）

频次 ★★★★ · 难度 🟢 · 高频：字节/美团

## 题目

设计一个支持 `push`、`pop`、`top`，并能在 **O(1)** 时间内检索最小元素的栈。

**示例**：
```
push(-2); push(0); push(-3);
getMin(); // -3
pop();
top();    // 0
getMin(); // -2
```

## 思路

普通栈的 `push/pop/top` 都是 O(1)，难点在最小值。用**一个辅助栈同步记录"当前状态下的最小值"**：每次 `push(val)` 时，往辅助栈压入 `min(val, 辅助栈当前栈顶)`（辅助栈为空则直接压 `val`）；`pop()` 时两个栈同步弹出。这样辅助栈的栈顶永远是"主栈从栈底到当前栈顶"这段元素的最小值。

## 代码

```java
class MinStack {
    private final Deque<Integer> stack = new ArrayDeque<>();
    private final Deque<Integer> minStack = new ArrayDeque<>();

    public void push(int val) {
        stack.push(val);
        minStack.push(minStack.isEmpty() ? val : Math.min(val, minStack.peek()));
    }

    public void pop() {
        stack.pop();
        minStack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }
}
```

## 复杂度

- **时间**：所有操作均为 O(1)
- **空间**：O(n) — 辅助栈和主栈一样大（每个元素都对应一条最小值记录）

## 边界条件

- 只 `push` 一个元素：`minStack` 为空触发 `minStack.isEmpty() ? val : ...` 分支，直接压入 `val` 本身，`getMin()` 正确返回它。
- 连续 `push` 相同的最小值：辅助栈会重复压入该值多次，`pop()` 时一一对应弹出，不会出现"最小值提前消失"的问题。
- 空栈调用 `pop`/`top`/`getMin`：题目通常保证调用前栈非空，不需要额外判空；工程实现中可按需抛异常或返回哨兵值。

## 变式

- 空间优化：辅助栈只在"新值 ≤ 当前最小值"时才压入，`pop()` 时也只在弹出的值等于辅助栈栈顶时才同步弹出。能省一些空间，但代码更绕，面试中先给出同步栈版本更稳妥。
- 同时维护最大值（`MaxStack`）：思路完全对称，再加一个 `maxStack` 同步记录当前最大值即可。

## 易错点

- 判断辅助栈是否为空要用 `minStack.isEmpty()` 而不是和主栈共用一个判断，两个栈必须严格同步 push/pop，一旦不同步会导致 `getMin()` 和 `top()` 的栈深度对不上。
- 不能只在"新值比当前最小值更小"时才压入辅助栈（除非同时也做了 pop 时的条件判断），否则 `pop()` 弹出主栈的非最小值时，辅助栈没有同步弹出，会导致最小值记录过期。

## 面试追问

- **为什么不直接维护一个 `min` 变量，而要用一整个辅助栈？** 单个变量只能记录"全局最小值"，一旦这个最小值被 `pop()` 弹出，就无法知道"次小值"是多少了；辅助栈相当于给每个历史时刻都保存了一份"当时的最小值"，`pop()` 时同步退回上一个时刻的最小值记录。
- **能不能不用额外栈，只用主栈本身记录最小值信息？** 可以：每次 push 新的最小值时，先把旧的最小值也压入栈（形成一对），`pop()` 时如果发现弹出的是最小值就再多弹一次恢复旧最小值；这种写法能省下一个栈对象，但逻辑更绕，属于进一步的空间压缩技巧。

## 关联题

- 同套路：[232. 用栈实现队列](232-implement-queue-using-stacks.md)、[225. 用队列实现栈](225-implement-stack-using-queues.md) —— 数据结构设计三连
- 进阶：只存"当前值与最小值差"的 O(1) 额外空间解法；716. 最大栈
- 知识点：辅助栈本质是给每个历史状态存最值快照——空间换"任意时刻可回溯"

