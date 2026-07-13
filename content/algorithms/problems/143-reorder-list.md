---
topics:
  - 链表
techniques:
  - 快慢指针
  - 三指针反转
---

# 143. 重排链表（Reorder List）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯/美团

## 题目

给定单链表 `L0 → L1 → … → Ln-1 → Ln`，将其重排为 `L0 → Ln → L1 → Ln-1 → L2 → Ln-2 → …`，不能修改节点值，只能改变节点引用。

**示例**：
```
输入: 1 -> 2 -> 3 -> 4
输出: 1 -> 4 -> 2 -> 3

输入: 1 -> 2 -> 3 -> 4 -> 5
输出: 1 -> 5 -> 2 -> 4 -> 3
```

## 思路

三步法：

1. **找中点**（快慢指针）：`slow` 走到中间节点，`fast` 走到末尾（偶数长度时 `slow` 停在第一个中间节点）。
2. **反转后半段**：从 `slow.next` 开始反转链表。
3. **交叉合并**：前半段和反转后的后半段交替连接。

```text
例：1 -> 2 -> 3 -> 4 -> 5 -> 6
                 ↓ 找中点
前半段：1 -> 2 -> 3
后半段：4 -> 5 -> 6
                 ↓ 反转后半
前半段：1 -> 2 -> 3
后半段：6 -> 5 -> 4
                 ↓ 交叉合并
结果：1 -> 6 -> 2 -> 5 -> 3 -> 4
```

## 代码

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public void reorderList(ListNode head) {
    if (head == null || head.next == null) return;

    // 1. 找中点（快慢指针）
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    // 2. 反转后半段
    ListNode second = reverseList(slow.next);
    slow.next = null;               // 断开前后两段
    ListNode first = head;

    // 3. 交叉合并
    while (second != null) {
        ListNode tmp1 = first.next;
        ListNode tmp2 = second.next;
        first.next = second;
        second.next = tmp1;
        first = tmp1;
        second = tmp2;
    }
}

private ListNode reverseList(ListNode head) {
    ListNode prev = null;
    while (head != null) {
        ListNode next = head.next;
        head.next = prev;
        prev = head;
        head = next;
    }
    return prev;
}
```

## 复杂度

- **时间**：O(n) — 三次线性遍历（找中点 + 反转 + 合并）
- **空间**：O(1) — 只用了常数个指针

## 边界条件

- 空链表或只有一个节点：直接返回。
- 两个节点：`slow` 停在第一个，`second` 仅一个节点，合并后为 `L0 → L1`，无变化（正确）。
- 奇数个节点：前半段比后半段多一个，最后 `second` 先变为 `null`，合并结束；剩余的前半段最后一个节点不动，也符合 `L0 → Ln → L1 → Ln-1 → ...` 的模式。

## 变式

- 仅重排链表的偶数位节点到前面：不需要反转，只需拆链后交叉合并即可。
- 将链表按奇偶位置拆分再重组：思路类似，但不是取前半段和后半段，而是根据位置奇偶拆分。
- 回文链表：[234. 回文链表](234-palindrome-linked-list.md) —— 同样用到找中点 + 反转，但比较阶段用"同时遍历"而不是"交叉合并"。

## 易错点

- 找中点结束时必须把 `slow.next` 置为 `null`，否则前后两段还连着，交叉合并时会产生环。
- 反转后半段时传入的是 `slow.next`，不是 `slow`（前半段包含 `slow`）。
- 交叉合并的循环里需要用临时变量先保存 `first.next` 和 `second.next`，否则修改指针后丢失后续节点。
- 偶数长度时要确保前半段不包含后半段的节点：快慢指针找中点时，如果要求 `slow` 停在第一个中间节点，可以用 `while (fast.next != null && fast.next.next != null)` 作为循环条件。

## 面试追问

- **这个三步法的时间复杂度能优化到比 O(n) 更好吗？** 不能，因为无论如何都需要遍历整个链表才能完成重排，O(n) 是理论下界（每个节点至少被访问一次才能调整其 next 指针）。
- **如果链表用数组存储（随机访问），这题是不是更简单？** 是的，数组可以用双指针从两端向中间构建新顺序，O(1) 空间就能完成，不需要找中点、反转这些步骤；链表版就是因为不能随机访问才需要三步法。
- **为什么不能直接复制节点值到数组，用数组重排后再写回链表？** 这是一种空间换时间的解法，时间复杂度 O(n)、空间复杂度 O(n)，可以接受但不符合面试的考察意图——面试官想看你是否掌握链表的指针操作。

## 关联题

- 同套路：[876. 链表的中间结点](876-middle-of-the-linked-list.md)（找中点）、[206. 反转链表](206-reverse-linked-list.md)（反转子过程）
- 易混：[148. 排序链表](148-sort-list.md)（同样用到找中点 + 反转/归并，但目的不同）
- 进阶：143 的变体要求原地重排且不能修改节点值，只能改指针——本题就是标准做法
