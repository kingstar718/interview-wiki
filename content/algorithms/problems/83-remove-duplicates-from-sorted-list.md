---
topics:
  - 链表
techniques:
  - 双指针归并
---

# 83. 删除排序链表中的重复元素（Remove Duplicates from Sorted List）

频次 ★★★★ · 难度 🟢 · 高频：字节/美团

## 题目

给定一个已排序的链表，删除所有重复的元素，使得每个元素只出现一次。与 82 题不同，本题重复元素**保留一个**。

**示例**：
```
输入: 1 -> 1 -> 2
输出: 1 -> 2

输入: 1 -> 1 -> 2 -> 3 -> 3
输出: 1 -> 2 -> 3
```

## 思路

**单指针遍历**：因为链表已排序，重复值一定相邻。用 `cur` 遍历链表，只要 `cur.val == cur.next.val`，就跳过下一个节点（`cur.next = cur.next.next`）；否则 `cur` 前移。

不需要虚拟头节点，因为头节点永远不会被删除（每个重复值保留一个）。

## 代码

```java
public ListNode deleteDuplicates(ListNode head) {
    ListNode cur = head;
    while (cur != null && cur.next != null) {
        if (cur.val == cur.next.val) {
            cur.next = cur.next.next;   // 跳过重复
        } else {
            cur = cur.next;             // 不重复，前移
        }
    }
    return head;
}
```

## 复杂度

- **时间**：O(n) —— 每个节点访问一次
- **空间**：O(1)

## 边界条件

- 空链表：返回 null
- 单节点：`cur.next == null`，循环不执行，直接返回原链表
- 全部重复（如 `[1,1,1]`）：第一次跳过第二个 1，第二次跳过第三个 1，结束后 `cur.next == null`，链表只剩 `[1]`
- 无重复：`cur` 一路前移到末尾

## 变式

- **[82. 删除排序链表中的重复元素 II](82-remove-duplicates-from-sorted-list-ii.md)**：重复元素**一个不留**，需要虚拟头节点
- **[26. 删除有序数组中的重复项](26-remove-duplicates.md)**：数组版去重（保留一个），双指针原地修改
- **无序链表去重**：需哈希表记录已有值，空间 O(n)

## 易错点

- 发现重复时**只动 `cur.next`，不动 `cur`**：因为跳过当前重复后，下一个节点可能仍然与当前值重复，需要继续判断。例如 `[1,1,1]`，跳过第一个 1 后 `cur` 不动，再次比较 `1 == 1` 再跳，直到 `cur.next` 为 null 或值不同
- 不重复时才 `cur = cur.next`
- 循环条件必须检查 `cur.next != null`，否则 `cur.next.val` 空指针

## 面试追问

- **为什么这题不用虚拟头节点？** 因为重复元素保留一个，头节点永远不会被删除（哪怕整条链表全相同，也只删后面的副本），而 82 题重复元素一个不留，头节点可能被删，所以需要虚拟头节点
- **如果链表无序，怎么去重保留一个？** 哈希表记录出现过的值，一次遍历，如果值已存在则跳过节点，否则加入哈希表并前移。空间 O(n)，时间 O(n)
- **这题用递归怎么写？** 递归处理子链表：`head.next = deleteDuplicates(head.next)`，如果 `head.val == head.next.val` 返回 `head.next` 否则返回 `head`。递归深度 O(n)，不推荐（链表可能很长）

## 关联题

- 进阶：[82. 删除排序链表中的重复元素 II](82-remove-duplicates-from-sorted-list-ii.md) —— 重复项一个不留
- 同套路：[26. 删除有序数组中的重复项](26-remove-duplicates.md) —— 数组版双指针去重
- 易混：[203. 移除链表元素](https://leetcode.cn/problems/remove-linked-list-elements/) —— 按给定值删除，不是去重逻辑
- 知识点：链表遍历与删除的标准模板，见[链表](链表.md)
