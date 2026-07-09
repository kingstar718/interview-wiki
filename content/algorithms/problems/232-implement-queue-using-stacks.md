---
topics:
  - 栈与队列
---

# 232. 用栈实现队列（Implement Queue using Stacks）

频次 ★★★ · 难度 🟢 · 高频：全厂

## 题目

仅使用两个栈实现一个先进先出（FIFO）队列，支持 `push`、`pop`、`peek`、`empty`。

## 思路

栈是后进先出，队列是先进先出，正好相反。用**两个栈倒腾一次顺序**就能互相转换：

- `inStack` 只负责接收 `push` 进来的元素。
- `outStack` 负责 `pop`/`peek`：当它为空时，把 `inStack` 里的元素全部弹出再压入 `outStack`——这个过程会把顺序整体反转一次，最早 `push` 的元素就跑到了 `outStack` 的栈顶，也就是队首。
- 只要 `outStack` 不为空，就一直从它出队，不需要重新倒腾（摊还下来每个元素只被"倒腾"一次）。

## 代码

```java
class MyQueue {
    private final Deque<Integer> inStack = new ArrayDeque<>();
    private final Deque<Integer> outStack = new ArrayDeque<>();

    public void push(int x) {
        inStack.push(x);
    }

    public int pop() {
        moveIfNeeded();
        return outStack.pop();
    }

    public int peek() {
        moveIfNeeded();
        return outStack.peek();
    }

    public boolean empty() {
        return inStack.isEmpty() && outStack.isEmpty();
    }

    private void moveIfNeeded() {
        if (outStack.isEmpty()) {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop());
            }
        }
    }
}
```

## 复杂度

- **时间**：`push` O(1)；`pop`/`peek` 最坏 O(n)，但**均摊 O(1)**——每个元素一生只会从 `inStack` 倒到 `outStack` 一次
- **空间**：O(n) — 两个栈总共存 n 个元素

## 边界条件

- 空队列调用 `pop`/`peek`：题目通常保证调用前队列非空，`moveIfNeeded()` 在两栈都为空时不会出错（`while` 循环不执行），但 `outStack.pop()`/`peek()` 会在真正空的情况下抛异常，工程实现中可按需要额外判空。
- 连续多次 `push` 后只 `pop` 一次：只有第一次 `pop`（此时 `outStack` 为空）才会触发倒腾，之后的 `pop` 直接从 `outStack` 取，不会重复倒腾。
- 交替 `push`/`pop`：每次 `outStack` 为空时才倒腾，均摊下来每个元素只被倒腾一次。

## 变式

- 反过来"用队列实现栈"是 [225. 用队列实现栈](225-implement-stack-using-queues.md)，思路不对称：栈转队列靠"倒腾一次换方向"，队列转栈靠"每次 push 后手动把新元素转到队首"。
- 只用**一个栈** + 递归实现队列：`push` 时先递归弹出所有元素，压入新元素后再依次放回——本质上是用递归调用栈模拟第二个栈，思路相通但代码更绕。

## 易错点

- `moveIfNeeded()` 的判断条件是 `outStack.isEmpty()`，不是 `inStack.isEmpty()`——只有当 `outStack` 空了才需要倒腾，如果 `outStack` 还有剩余元素就不能倒腾（会打乱已经调整好的顺序）。
- 倒腾操作必须**一次性把 `inStack` 全部倒完**，不能只倒一部分，否则两个栈中元素的相对顺序会被打乱。

## 面试追问

- **均摊 O(1) 具体是怎么算出来的？** 每个元素从 `push` 进 `inStack`到最终被 `pop` 出去，最多经历"一次压入 `inStack`、一次弹出 `inStack` 压入 `outStack`、一次从 `outStack` 弹出"，一共 3 次基本操作；n 个元素总共最多 3n 次操作，均摊到每次 `pop` 调用就是 O(1)。
- **能不能只用一个栈实现队列？** 不能用一个栈简单实现（栈本身的后进先出特性和队列的先进先出是反的），至少需要两个栈（或一个栈+递归调用栈）来完成一次"顺序反转"，才能把后进先出转换成先进先出。

## 关联题

- 同套路：[225. 用队列实现栈](225-implement-stack-using-queues.md) —— 互为反向问题；[155. 最小栈](155-min-stack.md) —— 同属结构设计
- 知识点：倒栈[摊还](摊还.md) O(1)——每个元素一生最多进出两次；同一分析方法用在 ArrayList 扩容上，见[集合框架](集合框架.md#arraylist-扩容机制)

