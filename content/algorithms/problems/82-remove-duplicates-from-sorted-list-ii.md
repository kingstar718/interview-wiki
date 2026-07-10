---
topics:
  - 链表
techniques:
  - 虚拟头节点
---

# 82. 删除排序链表中的重复元素 II（Remove Duplicates from Sorted List II）

频次 ★★★★ · 难度 🟡 · 高频：字节/腾讯/美团/阿里

## 题目

给定一个已排序的链表，删除**所有**含有重复数字的节点，只保留原始链表中没有重复出现的数字。与 83 题不同，本题重复元素一个不留。

**示例**：
```
输入: 1 -> 2 -> 3 -> 3 -> 4 -> 4 -> 5
输出: 1 -> 2 -> 5

输入: 1 -> 1 -> 1 -> 2 -> 3
输出: 2 -> 3
```

## 思路

**虚拟头节点 + 双指针**：
- 创建虚拟头节点 `dummy` 指向 `head`，`prev` 指向 `dummy`，`cur` 指向 `head`
- 遍历链表，当发现 `cur.val == cur.next.val` 时，内层循环跳过所有重复值
- 然后将 `prev.next` 指向重复段的下一个节点（跳过整段）
- 如果没有重复，`prev` 正常前移

虚拟头节点的作用是统一处理头节点可能被删除的情况，避免单独处理边界。

## 代码

```java
public ListNode deleteDuplicates(ListNode head) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode prev = dummy;
    ListNode cur = head;

    while (cur != null && cur.next != null) {
        if (cur.val != cur.next.val) {
            prev = cur;
            cur = cur.next;
        } else {
            while (cur.next != null && cur.val == cur.next.val) {
                cur = cur.next;
            }
            prev.next = cur.next;
            cur = cur.next;
        }
    }
    return dummy.next;
}
```

## 复杂度

- **时间**：O(n) —— 每个节点最多访问两次
- **空间**：O(1)

## 边界条件

- 空链表：`dummy.next == null`，直接返回 null
- 单节点：`cur.next == null`，循环不执行，直接返回原链表
- 全部重复（如 `[1,1,1]`）：内层循环跳过所有节点，`prev.next` 置为 null，返回空链表
- 头节点重复（如 `[1,1,2]`）：虚拟头节点确保头节点被跳过，`prev` 从 dummy 开始直接接上 `cur.next`

## 变式

- **[83. 删除排序链表中的重复元素](83-remove-duplicates-from-sorted-list.md)**：重复元素保留一个，不需要虚拟头节点也能处理
- **保留重复元素中的一个**：83 题是 82 题的基础版，每次遇到相邻重复只跳过一个
- **无序链表去重**：需要哈希表记录已出现的值，O(n) 空间

## 易错点

- 内层跳过重复的循环条件必须同时检查 `cur.next != null`，防止 `cur.next.val` 空指针
- `prev.next` 更新后，`prev` **不能**立即前移——因为新的 `prev.next` 指向的节点可能仍然重复（如 `[1,2,2,3,3]` 的场景），需要等下一轮循环判断
- 忘记虚拟头节点时，头节点本身就是重复的情况需要单独判断，容易遗漏；使用 dummy 节点统一了逻辑

## 面试追问

- **为什么要用虚拟头节点？** 头节点可能被删除，有 dummy 就不用单独处理 head 被删的情况，代码更统一。链表中只要涉及"头节点可能被删除"，虚拟头节点都是标准技巧
- **和 83 题的区别？** 83 保留一个副本，`cur` 和 `cur.next` 比较但只跳过重复中的多余部分；82 是整段跳过，一个不留。83 不用 dummy 因为头节点不会被删（仅保留一个），82 头节点可能全被删所以需要 dummy
- **如果链表是无序的怎么办？** 用哈希表记录每个值的出现次数，两次遍历：第一次统计频率，第二次删除频率 > 1 的节点。空间 O(n)，时间 O(n)

## 关联题

- 基础：[83. 删除排序链表中的重复元素](83-remove-duplicates-from-sorted-list.md) —— 保留一个版本，同模板但不需要虚拟头节点
- 同套路：[26. 删除有序数组中的重复项](26-remove-duplicates.md) —— 数组版去重，保留一个的版本
- 易混：[203. 移除链表元素](https://leetcode.cn/problems/remove-linked-list-elements/) —— 按给定值删除，不是去重，但同样可用虚拟头节点
- 知识点：虚拟头节点统一链表删除逻辑，见[链表](链表.md)
