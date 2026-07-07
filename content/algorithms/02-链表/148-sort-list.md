# 148. Sort List (Medium)

## 题目

给定链表头节点，按升序排序并返回排序后的链表。要求时间复杂度 O(n log n)。

**示例**：
```
输入: 4 -> 2 -> 1 -> 3
输出: 1 -> 2 -> 3 -> 4
```

## 思路

数组场景下 O(n log n) 排序常用快排/堆排，但链表**不支持随机访问**，快排的双指针分区、堆排的下标寻址都不方便；链表天然适合**归并排序**：

1. 用快慢指针找到链表中点，从中点断开成两个子链表（快指针从 `head.next` 出发，避免链表长度为 2 时死循环）。
2. 递归排序左右两半。
3. 合并两个有序链表（标准的双指针归并写法，同 21 题）。

## 代码

```java
public ListNode sortList(ListNode head) {
    if (head == null || head.next == null) {
        return head;
    }
    ListNode slow = head, fast = head.next;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    ListNode mid = slow.next;
    slow.next = null; // 从中点断开

    ListNode left = sortList(head);
    ListNode right = sortList(mid);
    return merge(left, right);
}

private ListNode merge(ListNode a, ListNode b) {
    ListNode dummy = new ListNode(0);
    ListNode cur = dummy;
    while (a != null && b != null) {
        if (a.val <= b.val) {
            cur.next = a;
            a = a.next;
        } else {
            cur.next = b;
            b = b.next;
        }
        cur = cur.next;
    }
    cur.next = (a != null) ? a : b;
    return dummy.next;
}
```

## 复杂度

- **时间**：O(n log n) — 归并排序，log n 层，每层合并共 O(n)
- **空间**：O(log n) — 递归调用栈（不算结果链表本身）

## 边界条件

- 空链表或单节点：`head == null || head.next == null` 直接返回，不再继续拆分，是递归的终止条件。
- 两个节点：`fast = head.next` 使 `while` 条件立即不满足，`slow` 停在第一个节点，从中点断开成两个各含一个节点的子链表，避免了"链表长度为 2 时死循环"的经典坑。

## 变式

- 面试官常追问"能不能做到 O(1) 空间"：需要改用**自底向上的迭代归并**（先合并长度为 1 的子链表，再 2、4、8…），避免递归栈开销，属于本题的进阶版本。
- 数组版的归并排序思路完全一致，只是数组支持随机访问，找中点是 O(1)，链表找中点需要额外的快慢指针遍历。

## 易错点

- 找中点用的 `fast` 是从 `head.next` 而不是 `head` 出发，这是为了保证链表长度为偶数时中点偏向左半部分（使得两个子链表的划分更均衡，避免死循环）。
- 断开链表 `slow.next = null` 不能漏掉，否则递归时左半部分仍然连着右半部分，会导致重复排序或栈溢出。

## 面试追问

- **为什么链表更适合归并排序而不是快排？** 快排依赖随机访问做双指针分区（`arr[left]`、`arr[right]` 需要 O(1) 定位），链表只能顺序访问，做分区效率很低；归并排序的"找中点拆分、合并两个有序序列"都只需要顺序遍历，天然契合链表的访问特性。
- **自底向上的迭代版本具体怎么实现？** 用一个变量 `size` 从 1 开始倍增（1, 2, 4, 8…），每一轮把链表切成若干个长度为 `size` 的子链表两两合并，直到 `size >= 链表长度`；不需要递归拆分链表，而是通过控制合并的子链表长度模拟"自底向上"的归并顺序，空间复杂度降为 O(1)。

---

[← 返回训练计划](../社招算法训练计划.md)
