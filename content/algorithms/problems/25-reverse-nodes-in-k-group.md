---
topics:
  - 链表
techniques:
  - 虚拟头节点
  - 三指针反转
---

# 25. K个一组翻转链表（Reverse Nodes in k-Group）

频次 ★★★★★ · 难度 🔴 · 高频：字节/阿里/腾讯

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

## 探资格+一物三用组后继 + 自查 206 双 bug（链表 Hard 压轴）

```
四步帧分工：探资格（走 k 步）→ 翻 [head,p) → 接缝（head.next=递归子链头）→ 报新头
一物三用：试探 k 步后的 p=【下一组的头】——兼任 reverse 的墙 end、下一组起点、
  （本组新尾的接缝目标是递归返回值）
206 基础自查（用户提问"这个反转写的对吗"抓出自己两 bug）：
  ① cur.next=cur 自环（应为 cur.next=prev——"抄变量抄错目标"与 347 heap[j]=heap[i] 同族）
  ② return prev.next（应为 return prev——prev 循环末=新头）
  25 的 reverse 变体：终止条件 cur!=end（墙=下一组头，不是 null——end 绝不卷入）
不足 k 组：探资格失败直接 return head（链上什么都不动）
复杂度 O(n) 每节点进出 reverse 恰一次；迭代版（dummy+prevTail）面试同源可提
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

## 关联题

- 同套路：[206. 反转链表](206-reverse-linked-list.md)、[92. 反转链表II](92-reverse-linked-list-ii.md) —— 反转族终点，每组内部就是一次区间反转
- 易混：[24. 两两交换链表结点](24-swap-nodes-in-pairs.md) —— 即 k=2 的本题，但常被单独要求"只改指针不换值"
- 知识点：dummy 结点 + 组前驱/组尾多指针的命名管理，是链表题从"能做对"到"写得稳"的分水岭

