---
topics:
  - 链表
techniques:
  - 快慢指针
  - 三指针反转
---

# 234. 回文链表（Palindrome Linked List）

频次 ★★★★ · 难度 🟢 · 高频：字节/腾讯/美团

## 题目

给定单链表，判断它是否回文。要求 O(n) 时间、O(1) 额外空间。

**示例**：
```
输入: 1 -> 2 -> 2 -> 1
输出: true

输入: 1 -> 2
输出: false
```

## 思路

**快慢指针找中点 + 反转前半段（或后半段）比较**。

方法一（反转后半段）：快慢指针找到中点后，反转后半段链表，然后同时遍历前半段和反转后的后半段，逐个比较节点值。

方法二（反转前半段）：一边遍历一边反转前半段（边遍历边反转），到达中点时前半段已经反转好，再与后半段比较。此法只需一次遍历但指针操作更复杂。

## 代码

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

// 方法一：找中点 + 反转后半段
public boolean isPalindrome(ListNode head) {
    if (head == null || head.next == null) return true;

    // 1. 找中点（慢指针停在左半段最后一个节点）
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }

    // 2. 反转后半段
    ListNode second = reverseList(slow.next);
    ListNode first = head;

    // 3. 比较两半
    while (second != null) {
        if (first.val != second.val) return false;
        first = first.next;
        second = second.next;
    }
    return true;
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

```java
// 方法二：边遍历边反转前半段（一次遍历）
public boolean isPalindrome(ListNode head) {
    if (head == null || head.next == null) return true;

    ListNode slow = head, fast = head;
    ListNode prev = null;

    while (fast != null && fast.next != null) {
        fast = fast.next.next;

        // 反转前半段
        ListNode next = slow.next;
        slow.next = prev;
        prev = slow;
        slow = next;
    }

    // 奇数长度时 slow 正好在中间节点，跳过它
    if (fast != null) {
        slow = slow.next;
    }

    // 比较：prev 是反转后的前半段，slow 是后半段
    while (slow != null) {
        if (prev.val != slow.val) return false;
        prev = prev.next;
        slow = slow.next;
    }
    return true;
}
```

## 复杂度

- **时间**：O(n) — 每个节点最多访问两次
- **空间**：O(1) — 只用了指针变量

## 边界条件

- 空链表或单节点：`null` 或单个节点自然回文，返回 `true`。
- 两个相同值的节点：`fast` 一步走完，`slow.next` 指向第二个节点，反转后半段后比较一次即返回 `true`。
- 偶数长度回文：如 `[1,2,2,1]`，左半段 `[1,2]`，右半段反转后为 `[1,2]`，逐位比较通过。
- 奇数长度回文：如 `[1,2,1]`，方法一中 `slow` 停在 `1`（左半段末尾），反转 `slow.next`（即 `[2,1]` 反转成 `[1,2]`），逐位比较前半段 `[1,2]` 与反转后 `[1,2]` 通过（中间节点 `2` 属于前半段，不参与比较）。

## 变式

- 判断回文数组或回文字符串：双指针从两端向中间比较。
- 回文链表用 O(n) 空间（复制到数组）判断：更简单，但不符合面试的 O(1) 空间要求。
- 回文链表递归法：用递归栈从后往前比较，空间 O(n)。

## 易错点

- 方法一的找中点循环条件是 `fast.next != null && fast.next.next != null`（让 `slow` 停在左半段最后一个节点），而不是 `fast != null && fast.next != null`（后者让 `slow` 停在中点位置）。两种写法影响后续反转的范围，要统一。
- 比较时用 `second != null` 作为循环条件，而不是 `first != null` —— 因为可能前半段比后半段多一个节点（奇数长度时），用 `first` 会导致误判。
- 比较结束后最好恢复链表结构（把后半段再反转回来），虽然不恢复也能通过测试，但实际工程中修改输入是不良习惯。

## 面试追问

- **为什么这题不能直接用栈（先全部入栈再依次出栈比较）？** 能用，而且代码很简洁（遍历一遍入栈，第二遍遍历并出栈比较），但空间复杂度是 O(n)，不满足题目 O(1) 空间的要求。面试中可以先提"用栈方便但不满足空间要求"，再给出 O(1) 空间的反转解法。
- **如果链表是双向链表，判断回文有什么更简单的方法？** 双向链表可以直接用双指针——头指针和尾指针向中间移动比较即可，不需要反转。
- **判断回文链表的方法一和方法二各有什么优劣？** 方法一（反转后半段）思路更清晰、代码更易理解，但不破坏原链表结构（如果最后恢复）；方法二（反转前半段）只需要一次遍历且不需要额外的找中点步骤，但指针操作更复杂，容易出错。面试推荐方法一。

## 关联题

- 同套路：[876. 链表的中间结点](876-middle-of-the-linked-list.md)（找中点）、[206. 反转链表](206-reverse-linked-list.md)（反转子过程）
- 进阶：[143. 重排链表](143-reorder-list.md) —— 同样用到找中点 + 反转 + 双指针遍历的组合
- 易混：[9. 回文数](9-palindrome-number.md)（数字回文，反转一半比较）、[125. 验证回文串](125-valid-palindrome.md)（字符串回文，双指针）
