# 25. Reverse Nodes in k-Group (Hard)

## 题目

给定链表，每 `k` 个节点一组进行反转，返回修改后的链表。如果节点总数不是 `k` 的整数倍，剩余节点保持原有顺序。

**示例**：
```
输入: head = 1 -> 2 -> 3 -> 4 -> 5, k = 2
输出: 2 -> 1 -> 4 -> 3 -> 5
```

## 思路

先遍历一遍链表数出总长度 `count`，就能知道还能凑出多少个完整的 k 组，避免每组反转前都重新判断"剩余节点是否够 k 个"。

之后用哑节点 `dummy` + `prevGroupTail`（上一组反转后的尾节点）迭代处理：每组内用标准的单链表反转（`prev/cur/next` 三指针）反转 k 个节点，再把这一组接回前一组的尾部，本组反转前的头节点（现在是组尾）再接到下一组的头。

## 代码

```java
public ListNode reverseKGroup(ListNode head, int k) {
    int count = 0;
    for (ListNode node = head; node != null; node = node.next) {
        count++;
    }

    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prevGroupTail = dummy;

    while (count >= k) {
        ListNode cur = prevGroupTail.next;
        ListNode prev = null;
        for (int i = 0; i < k; i++) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        ListNode groupTail = prevGroupTail.next; // 反转前的头，现在是组尾
        groupTail.next = cur;                    // 接上剩余部分
        prevGroupTail.next = prev;                // 前一组接上新的组头
        prevGroupTail = groupTail;
        count -= k;
    }
    return dummy.next;
}
```

## 复杂度

- **时间**：O(n) — 每个节点被访问常数次
- **空间**：O(1) — 迭代实现，不用递归栈

## 边界条件

- `k == 1`：每组只有一个节点，"反转"没有实际效果，循环体执行后链表不变，逻辑天然兼容。
- 链表长度不是 `k` 的整数倍：剩余不足 `k` 个的尾部节点保持原样，`while (count >= k)` 循环结束后 `dummy.next` 正确串联了已反转的分组和未处理的尾部。
- 链表为空：`count == 0`，循环不执行，直接返回 `dummy.next`（即 `null`）。

## 变式

- `k` 固定为 2 就是 [24. 两两交换链表结点](24-swap-nodes-in-pairs.md)，本题是它的通用化版本。
- 递归实现：先递归反转后面的链表，再处理当前这一组，代码更短但是 O(n/k) 递归深度，空间不是最优。

## 易错点

- 反转前必须先记下 `prevGroupTail.next`（原组头），反转后它会变成组尾，需要重新接上下一组。
- 用预先数好的 `count` 判断"是否够 k 个"，比每组反转到一半才发现不够再回滚要简单得多。

## 面试追问

- **为什么要先数一遍长度，而不是每组反转时动态检查？** 动态检查需要先探测 k 个节点是否存在，如果不够还要把已经反转的部分复原，实现复杂且容易出错；预先数出总长度后用一次减法判断，逻辑更清晰，也是这道 Hard 题能保持 O(1) 空间又不写复杂回滚逻辑的关键。
- **这题和 [206. 反转链表](206-reverse-linked-list.md) 是什么关系？** 内层反转 k 个节点用的正是标准单链表反转的三指针模板，本题的难点在于"多组反转后如何正确拼接"，把已经掌握的基础反转模板套用 `count/k` 次即可。

---

[← 返回训练计划](社招算法训练计划.md)
