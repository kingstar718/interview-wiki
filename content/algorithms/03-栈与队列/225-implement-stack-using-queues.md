# 225. 用队列实现栈（Implement Stack using Queues）

频次 ★★★ · 难度 🟢 · 高频：美团

## 题目

仅使用队列实现一个后进先出（LIFO）栈，支持 `push`、`pop`、`top`、`empty`。

## 思路

队列没有"倒腾一次就能反转顺序"这种捷径（队列本身就是单向的），所以换一个思路：**每次 push 完新元素后，立刻把它之前的所有旧元素依次出队再入队**，相当于把新元素"转"到了队首。这样队首永远是最后 push 进来的元素，`pop`/`top` 直接操作队首就等价于操作栈顶。

## 代码

```java
class MyStack {
    private final Queue<Integer> queue = new LinkedList<>();

    public void push(int x) {
        queue.offer(x);
        int size = queue.size();
        for (int i = 0; i < size - 1; i++) {
            queue.offer(queue.poll());
        }
    }

    public int pop() {
        return queue.poll();
    }

    public int top() {
        return queue.peek();
    }

    public boolean empty() {
        return queue.isEmpty();
    }
}
```

## 复杂度

- **时间**：`push` O(n)（每次都要转一遍旧元素）；`pop`/`top` O(1)
- **空间**：O(n)

## 边界条件

- 空栈调用 `pop`/`top`：题目通常保证调用前非空；工程实现中 `queue.poll()`/`peek()` 在队列为空时会返回 `null`，可按需额外判断。
- 只 `push` 一个元素：`size - 1 == 0`，`for` 循环不执行，队首就是这个元素本身。
- 连续 `push` 多个元素：每次 `push` 后都会把之前的所有元素轮转到新元素后面，保证队首始终是最后压入的元素。

## 变式

- 也可以把开销换到 `pop`：用两个队列，`push` 只管往主队列里加，`pop` 时把主队列前 n-1 个元素倒到辅助队列，弹出最后一个，再交换两个队列的角色。两种写法本质都是"把 O(n) 的开销放在 push 还是 pop 上"，二选一即可，不需要同时优化。
- 反过来"用栈实现队列"是 [232. 用栈实现队列](232-implement-queue-using-stacks.md)，思路不对称：那边是"倒腾一次换方向后均摊 O(1)"，这边是"每次 push 都要重新排队"。

## 易错点

- `for` 循环的次数是 `queue.size() - 1`（旋转除新元素外的所有元素），如果用固定次数或者忘记减一，会把新元素自己也转一圈，导致顺序错误。
- 必须先 `offer(x)` 再做旋转，如果顺序反了，旋转时新元素还没入队，轮转的元素个数会算错。

## 面试追问

- **这题为什么不能像"用栈实现队列"那样做到均摊 O(1)？** 因为队列只能从一端进、另一端出，没有办法像栈那样"倒腾一次就整体反转顺序"；要让新元素处于队首（模拟栈顶），只能每次 push 后手动把它之前的元素逐个移到它后面，这个操作本身就是 O(n)，无法避免。
- **能不能用一个队列 + 计数，让 push 变成 O(1)，把开销转移到 pop？** 可以，`push` 只管往队尾加（O(1)），`pop`/`top` 时需要把队列前 n-1 个元素倒到另一个队列，取出最后一个再倒回来，这样 `push` 是 O(1)，但 `pop`/`top` 变成 O(n)，是这道题两种等价的开销分配方式。

## 关联题

- 同套路：[232. 用栈实现队列](232-implement-queue-using-stacks.md) —— 对比记忆：本题"入时旋转"（入队后把前面元素轮转到队尾），它"出时倒腾"
- 知识点：[155. 最小栈](155-min-stack.md)同属"用基础结构组合出新语义"的设计题

