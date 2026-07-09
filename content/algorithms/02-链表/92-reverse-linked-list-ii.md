# 92. 反转链表II（Reverse Linked List II）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

给定链表头节点 `head` 和两个整数 `left`、`right`（1-indexed，`left <= right`），反转从位置 `left` 到 `right` 的链表节点，返回反转后的链表。

**示例**：
```
输入: head = 1 -> 2 -> 3 -> 4 -> 5, left = 2, right = 4
输出: 1 -> 4 -> 3 -> 2 -> 5
```

## 思路

**头插法（一次遍历）**：
1. 用哑节点 `dummy` 简化"反转区间包含头节点"的边界情况，找到 `left` 前一个节点 `prev`。
2. `cur` 固定指向区间起点（反转后会变成区间尾），每次把 `cur.next` 摘下来，头插到 `prev` 之后。
3. 循环 `right - left` 次即可把区间整体反转。

这样不需要额外记录区间长度或先断开再拼接，一次遍历、原地完成。

## 代码

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;
    for (int i = 0; i < left - 1; i++) {
        prev = prev.next;
    }
    ListNode cur = prev.next;
    for (int i = 0; i < right - left; i++) {
        ListNode next = cur.next;
        cur.next = next.next;
        next.next = prev.next;
        prev.next = next;
    }
    return dummy.next;
}
```

## 复杂度

- **时间**：O(n) — 最多遍历一遍链表
- **空间**：O(1) — 只用了常数个指针

## 边界条件

- `left == right`：循环 0 次，直接返回原链表，逻辑天然兼容，不需要特判。
- `left == 1`：`prev` 就是 `dummy`，同样天然兼容，这也是引入哑节点的意义。
- `right` 等于链表长度（反转到链表末尾）：`cur.next` 最终变为 `null`，`next.next = prev.next` 正常执行，不会越界。

## 变式

- 反转**整个**链表（`left=1, right=n`）：退化为 [206. 反转链表](206-reverse-linked-list.md) 的头插法版本。
- 每 k 个节点一组反转多段：见 [25. K 个一组翻转链表](25-reverse-nodes-in-k-group.md)，是本题"反转一段"的多次重复应用。

## 易错点

- 头插法里 `cur` 指针**始终不变**（它会变成反转后的区间尾），每次操作的是 `cur.next`，容易误写成移动 `cur` 本身导致丢失位置。
- 三行指针操作的顺序不能打乱：必须先 `cur.next = next.next`（摘下 `next`），再 `next.next = prev.next`（把 `next` 接到区间头部），最后 `prev.next = next`（更新区间头），顺序颠倒会断链或形成环。

## 面试追问

- **头插法和"先整体反转再拼接"两种做法，哪种更好？** 头插法只需一次遍历、原地完成，不需要额外记录区间长度或断开重连三段链表；"先断开区间、反转、再拼接"的做法需要多次遍历定位边界，代码更长且容易在拼接时接错顺序，头插法是更简洁的标准答案。
- **能不能递归实现？** 可以，但递归版本需要额外传递"是否已经到达 left 位置"等状态，实现复杂度高于迭代版，且是 O(n) 空间，链表区间反转类问题通常迭代解法更受青睐。

## 关联题

- 同套路：[206. 反转链表](206-reverse-linked-list.md) —— 本题 = 定位区间 + 局部 206 + 首尾接回
- 进阶：[25. K个一组翻转链表](25-reverse-nodes-in-k-group.md) —— 本题的区间反转是它每组的子过程
- 易混：[24. 两两交换链表结点](24-swap-nodes-in-pairs.md) —— 24 是 25 的 k=2 特例，不是本题的特例

