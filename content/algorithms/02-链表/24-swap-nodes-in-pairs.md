# 24. Swap Nodes in Pairs (Medium)

## 题目

给定链表，两两交换相邻节点，返回交换后的链表头（不能只修改节点内部的值，必须真实交换节点）。

**示例**：
```
输入: 1 -> 2 -> 3 -> 4
输出: 2 -> 1 -> 4 -> 3
```

## 思路

用哑节点 `dummy` + 指针 `prev` 指向"待交换这一对"的前一个节点。每次取出这对节点 `first`、`second`，改变三个指针的指向完成交换，再把 `prev` 移到交换后这一对的末尾（也就是原来的 `first`），继续处理下一对。

## 代码

```java
public ListNode swapPairs(ListNode head) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;

    while (prev.next != null && prev.next.next != null) {
        ListNode first = prev.next;
        ListNode second = first.next;

        first.next = second.next;
        second.next = first;
        prev.next = second;

        prev = first;
    }
    return dummy.next;
}
```

## 复杂度

- **时间**：O(n) — 每个节点访问一次
- **空间**：O(1)

## 边界条件

- 空链表或只有一个节点：`prev.next` 为 `null` 或 `prev.next.next` 为 `null`，循环条件不满足，直接返回原链表，不发生交换。
- 节点数为偶数：所有节点两两配对完成交换。
- 节点数为奇数：最后一个节点没有配对，循环条件 `prev.next.next != null` 会自然跳过它，不需要额外处理。

## 变式

- 如果要求"每 k 个一组"而不是固定两个一组，就是 [25. K 个一组翻转链表](25-reverse-nodes-in-k-group.md)，本题是 k=2 的特例。
- 只交换节点的值而不交换节点本身：更简单，但题目通常明确要求"不能只修改节点内部的值"，这是为了考察真正的指针操作能力。

## 易错点

- `prev.next = second` 必须在 `second.next = first` **之后**执行，如果顺序反了，`prev.next` 会被错误地指向还没重新赋值的 `second`（此时 `second.next` 还是旧值），导致链表断裂。
- 循环结束条件用的是 `prev.next != null && prev.next.next != null`，两个条件都要检查，只检查一个在奇数长度或空链表时会抛空指针异常。

## 面试追问

- **能不能只用递归实现，不用哑节点？** 可以：递归函数处理"当前这一对"并返回交换后的头节点，再让 `first.next = swapPairs(second.next)` 递归处理剩余部分；递归写法更简洁但是 O(n) 空间（调用栈），迭代 + 哑节点是 O(1) 空间的版本。
- **这题能不能推广成"每三个一组，中间的元素位置不变，只交换首尾"这种变形？** 可以，本质上都是"固定住一部分节点作为锚点、重新排布另一部分节点的指针"，需要在纸上画出具体的指针关系图，再对照代码逐行验证，而不是死记这道题的写法。

---

[← 返回训练计划](../社招算法训练计划.md)
