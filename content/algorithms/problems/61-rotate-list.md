---
topics:
  - 链表
techniques:
  - 双指针模拟
---

# 61. 旋转链表（Rotate List）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯

## 题目

给定一个链表头节点 `head`，将链表每个节点向右移动 `k` 个位置。

**示例**：
```
输入: head = 1 -> 2 -> 3 -> 4 -> 5, k = 2
输出: 4 -> 5 -> 1 -> 2 -> 3
```

## 思路

先遍历链表得到长度 `n` 并找到尾节点，将尾节点与头节点相连形成环。实际有效右移次数为 `k % n`，新头节点在从原头部开始数 `n - k%n` 处，新尾节点在其前一个位置。断开环并返回新头。

## 代码

```java
public ListNode rotateRight(ListNode head, int k) {
    if (head == null || head.next == null || k == 0) return head;

    // 1. 计算长度，找到尾节点
    ListNode oldTail = head;
    int n = 1;
    while (oldTail.next != null) {
        oldTail = oldTail.next;
        n++;
    }

    // 2. 连成环
    oldTail.next = head;

    // 3. 找到新尾节点和新头节点
    ListNode newTail = head;
    for (int i = 0; i < n - k % n - 1; i++) {
        newTail = newTail.next;
    }
    ListNode newHead = newTail.next;

    // 4. 断开环
    newTail.next = null;

    return newHead;
}
```

## 复杂度

- **时间**：O(n) — 两次遍历链表
- **空间**：O(1)

## 边界条件

- 空链表或只有一个节点：直接返回 `head`。
- `k = 0`：直接返回 `head`。
- `k` 远大于 `n`：取模后等价于右移 `k % n` 次。
- `k % n == 0`：旋转后回到原位，直接返回 `head`（但别忘了取模判断，否则环无法断开）。

## 变式

- **左旋转**：只需把找新头的位置从 `n - k%n` 改为 `k%n`，其余逻辑完全一致。
- **旋转数组**（[189. 旋转数组](189-rotate-array.md)）：整体反转 + 分段反转三步法，思路不同但都是"偏移后重新对齐"。

## 易错点

- 忘记取模 `k % n`：如果 `k > n` 会多转一圈或多圈，必须取模简化。
- 环没断开：必须将 `newTail.next` 置为 `null`，否则链表成环，遍历时会死循环。
- `n - k%n - 1` 的 `-1` 容易漏写：`newTail` 要走 `n - k%n - 1` 步才能到达新尾节点（从 0 计步）。

## 面试追问

- **能不能不做成环，用双指针一步到位？** 可以先用一个指针遍历到尾并计长，再用另一个指针定位新尾。但"连成环/断开"的方式逻辑统一、代码简洁，面试推荐。
- **如果 k 可能为负数（左旋转）怎么处理？** 统一取模后如果为负，加上 n 转为等价正偏移即可。

## 关联题

- 同套路：[189. 旋转数组](189-rotate-array.md) —— 同是"右移 k 步"，数组用三次反转
- 进阶：[143. 重排链表](143-reorder-list.md) —— 找中点 + 反转 + 穿插合并，更复杂的链表重组
- 基础：链表长度与指针移动基本功见[链表](链表.md)
