---
topics:
  - 链表
techniques:
  - 虚拟头节点
---

# 203. 移除链表元素（Remove Linked List Elements）

频次 ★★ · 难度 🟢 · 高频：全厂

## 题目

给定链表头节点 `head` 和整数 `val`，删除链表中所有值等于 `val` 的节点。

## 思路

**哨兵节点（dummy）**：因为头节点也可能被删除，用 dummy 统一处理。遍历链表，`cur.next.val == val` 时跳过。

## 代码

```java
public ListNode removeElements(ListNode head, int val) {
    ListNode dummy = new ListNode(0, head);
    ListNode cur = dummy;
    while (cur.next != null) {
        if (cur.next.val == val) {
            cur.next = cur.next.next; // 跳过，cur 不动
        } else {
            cur = cur.next;
        }
    }
    return dummy.next;
}
```

## 复杂度

- **时间**：O(n)
- **空间**：O(1)

## 边界条件

- 头节点就是要删的：dummy 保证统一处理
- 全部节点都要删：返回 null
- 空链表：dummy.next 为 null，循环不执行

## 变式

- 不保留原顺序：可从两端向中间夹逼，遇到要删的用尾部元素替换

## 易错点

- 删除节点后 `cur` 不要移动——新的 `cur.next` 可能也要删

## 面试追问

- **不用 dummy 头节点怎么写？** 头节点单独处理（while 循环删前导匹配节点），其余节点正常遍历删除——代码更冗长，容易漏边界

## 关联题

- 同套路：[19. 删除链表的倒数第N个节点](19-remove-nth-node-from-end-of-list.md)（也用 dummy）