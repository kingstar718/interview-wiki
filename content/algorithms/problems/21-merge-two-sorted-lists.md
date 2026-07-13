---
topics:
  - 链表
techniques:
  - 虚拟头节点
---

# 21. 合并两个有序链表（Merge Two Sorted Lists）

频次 ★★★★★ · 难度 🟢 · 高频：全厂

## 题目

将两个升序链表合并为一个新的升序链表并返回，新链表通过拼接原两链表的节点构成。

**示例**：
```
输入: l1 = [1,2,4], l2 = [1,3,4]
输出: [1,1,2,3,4,4]
```

## 思路

**迭代（双指针归并）**：用哑节点简化头节点处理，两个指针分别遍历两条链表，每次取较小值接在新链表末尾，直到某条链表走完，再把另一条剩余部分直接接上（因为剩余部分本身有序）。

递归解法：`merge(l1, l2)` 返回合并后的头节点，比较两个头节点后递归合并剩余部分。

## 代码

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode cur = dummy;

    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
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

递归版本：

```java
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    if (l1 == null) return l2;
    if (l2 == null) return l1;
    if (l1.val <= l2.val) {
        l1.next = mergeTwoLists(l1.next, l2);
        return l1;
    } else {
        l2.next = mergeTwoLists(l1, l2.next);
        return l2;
    }
}
```

## 复杂度

- **时间**：O(m + n) — 每条链表每个节点访问一次
- **空间**：迭代 O(1)；递归 O(m + n)（调用栈深度）

## 边界条件

- 两条链表都为空：`dummy.next` 为 `null`，返回空链表。
- 其中一条为空：`while` 循环不执行，`cur.next` 直接接到另一条非空链表，返回正确。
- 值相等时：`<=` 保证了稳定性，先取 `l1` 再取 `l2`，有序性不变。

## 变式

- 合并 K 个有序链表：见 [23. 合并K个升序链表](23-merge-k-sorted-lists.md)（优先队列/分治归并）。
- 合并两个有序数组：见 [88. 合并两个有序数组](88-merge-sorted-array.md)（从后往前填充）。
- 合并后去重：在取较小值时跟前一个已合并节点比较值，相等则跳过。

## 易错点

- 循环条件是 `&&` 不是 `||`：只要有一条链表走完就退出循环，剩余部分用 `cur.next` 直接拼接——如果用 `||` 会导致空指针访问 `val`。
- 哑节点的使用：`dummy.next` 返回真正的头节点，**不能直接返回 `dummy`**。
- 递归版本要确保终止条件写在最前面（`l1 == null` 和 `l2 == null`），否则会无限递归。

## 面试追问

- **为什么迭代解法的时间复杂度是 O(m+n) 而不是 O(min(m, n))？** while 循环执行次数受限于较短的链表，但最后 `cur.next = (l1 != null) ? l1 : l2` 将剩余部分整体接入，这并没有遍历剩余节点，所以总执行次数确实是 min(m, n)。但严格说每个节点都被访问了一次（遍历自己所在的链表时被比较，或作为剩余部分被整体连接），因此整体仍视为 O(m + n)。
- **如果要合并 K 个有序链表，用分治法还是优先队列更好？** 分治法归并是 O(nk·logk)，优先队列是 O(nk·logk)，复杂度相同；但分治法的常数更小（不需要每次从堆里取元素的重排开销），通常在有 GPU / SIMD 优化的场景也更容易并行；优先队列写起来更直观。
- **如果内存有限，不能一次性把 K 个链表全部加载，你有什么替代方案？** 可以用外部归并排序的思路——先分块排序到磁盘，再用多路归并（堆）逐块读取合并。

## 关联题

- 同套路：[23. 合并K个升序链表](23-merge-k-sorted-lists.md)（K 路归并）、[88. 合并两个有序数组](88-merge-sorted-array.md)（数组版）
- 进阶：[148. 排序链表](148-sort-list.md)（归并排序的切分 + 合并）
- 知识点：双指针归并是"合并有序序列"的通用框架，也是归并排序 Merge 阶段的原子操作
