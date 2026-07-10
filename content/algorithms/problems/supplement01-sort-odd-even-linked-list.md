---
topics:
  - 链表
techniques:
  - 双指针归并
  - 模拟构造
---

# 补充题1. 排序奇升偶降链表（Sort Odd-Even Linked List）

频次 ★★★★ · 难度 🟡 · 高频：字节

## 题目

给定单链表，奇数下标节点按升序排列，偶数下标节点按降序排列，要求合并为一个有序链表（整体升序）。

**示例**：
```
输入: 1 -> 8 -> 3 -> 6 -> 5 -> 4 -> 7 -> 2 -> null
下标: 1    2    3    4    5    6    7    8
输出: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> null
```

## 思路

三步走：

1. **分离**：遍历一次，将奇数位和偶数位拆成两条独立链表（偶数位当前是降序）
2. **反转偶数链表**：偶数位原始顺序是降序，反转后变为升序
3. **合并**：双指针归并两条升序链表

## 代码

```java
public ListNode sortOddEvenList(ListNode head) {
    if (head == null || head.next == null) return head;

    ListNode odd = head, even = head.next;
    ListNode evenHead = even;

    // 1. 分离奇偶
    while (even != null && even.next != null) {
        odd.next = even.next;
        odd = odd.next;
        even.next = odd.next;
        even = even.next;
    }
    odd.next = null;

    // 2. 反转偶数链表
    evenHead = reverse(evenHead);

    // 3. 合并两个升序链表
    return merge(head, evenHead);
}

private ListNode reverse(ListNode head) {
    ListNode prev = null;
    while (head != null) {
        ListNode next = head.next;
        head.next = prev;
        prev = head;
        head = next;
    }
    return prev;
}

private ListNode merge(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode cur = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val < l2.val) {
            cur.next = l1;
            l1 = l1.next;
        } else {
            cur.next = l2;
            l2 = l2.next;
        }
        cur = cur.next;
    }
    cur.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

## 复杂度

- **时间**：O(n) —— 分离 O(n)，反转 O(n/2)，合并 O(n)
- **空间**：O(1) —— 迭代指针，只用了常数额外空间

## 边界条件

- 空链表或只有一个节点：直接返回
- 只有奇数位或偶数位节点时仍要正确处理：while 里 `even != null && even.next != null` 确保不越界
- 反转后的偶数链表可能为空：merge 要处理 null 的情况

## 变式

- **[148. 排序链表](148-sort-list.md)**：链表的归并排序（O(n log n)），本题是其特殊输入形式
- **奇偶分离 + 反转 + 归并** 的三步拆解思路，可推广到更多"分组后各自有序再合并"的场景

## 易错点

- **分离时奇数链最后要置 null**：`odd.next = null`，否则原链表残留的偶节点指针会造成环
- **反转的是偶数链表**，不是整个链表。偶链是降序，反转后变升序，与奇数链表同向才能归并
- **分离后奇偶各自长度可能差 1**（总节点奇数时），merge 时通过 null 判断处理

## 面试追问

- **能省去反转步骤吗？** 不能，因为偶数链是降序，和奇数链的升序方向相反，必须反转对齐方向才能归并
- **如果把偶数位改为也保持原序（升序或降序输入变化），步骤还一样吗？** 只要最终输出升序，方向不同的子链表必须先反转对齐

## 关联题

- 同套路：[21. 合并两个有序链表](21-merge-two-sorted-lists.md) —— 归并基础
- 同套路：[206. 反转链表](206-reverse-linked-list.md) —— 反转基础
- 进阶：[148. 排序链表](148-sort-list.md) —— 归并排序
- 知识点：链表分离与合并技巧见[链表](链表.md)
