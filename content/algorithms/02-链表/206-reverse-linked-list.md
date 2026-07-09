# 206. 反转链表（Reverse Linked List）

频次 ★★★★★ · 难度 🟢 · 高频：全厂

## 题目

反转一个单链表，返回反转后的头节点。

**示例**：
```
输入: 1 -> 2 -> 3 -> 4 -> 5
输出: 5 -> 4 -> 3 -> 2 -> 1
```

## 思路

**迭代**：用 `prev` 记录已反转部分的头，遍历时把 `head.next` 指向 `prev`，再把 `prev`、`head` 同步后移。

也可以用**递归**：先反转 `head.next` 之后的部分，再把 `head` 接到反转后链表的尾部；递归写法更简洁但需要 O(n) 栈空间。

## 代码

```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

public ListNode reverseList(ListNode head) {
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

递归版本：

```java
public ListNode reverseList(ListNode head) {
    if (head == null || head.next == null) return head;
    ListNode newHead = reverseList(head.next);
    head.next.next = head;
    head.next = null;
    return newHead;
}
```

## 复杂度

- **时间**：O(n) — 每个节点访问一次
- **空间**：迭代 O(1)；递归 O(n)（调用栈）

## 边界条件

- 空链表（`head == null`）：迭代版 `while` 循环不执行，直接返回 `prev == null`；递归版第一行判断成立，直接返回 `head`（即 `null`），两者都正确。
- 只有一个节点：迭代一次后 `prev` 指向该节点，`next` 为 `null`，返回正确；递归版同样直接返回该节点。

## 变式

- 只反转链表的一段（`[left, right]` 区间）：见 [92. 反转链表 II](92-reverse-linked-list-ii.md)。
- 每 k 个节点一组反转：见 [25. K 个一组翻转链表](25-reverse-nodes-in-k-group.md)。
- 双向链表的反转：思路类似，但每个节点需要同时交换 `prev`/`next` 两个指针，而不只是单向的 `next`。

## 易错点

- 必须先用 `next` 保存 `head.next`，再修改 `head.next = prev`，否则会丢失后面还没处理的链表——这是链表题里最常见的"先备份再修改"原则。
- 递归版本容易漏写 `head.next = null`：如果不断开原来的 `next`，反转后的链表尾部会形成环（原来的 `head` 仍然指向反转前的下一个节点）。

## 面试追问

- **递归实现能不能做到 O(1) 空间？** 不能，递归本身依赖调用栈保存每一层的状态，栈深度等于链表长度，天然是 O(n) 空间；只有迭代版本才能做到 O(1) 空间，这也是面试中通常更推荐迭代解法的原因。
- **如果只允许遍历一次、且不能使用递归和额外数据结构，还有别的实现方式吗？** 迭代版本本身就是"只遍历一次、O(1) 空间"的答案；这题也是很多更复杂链表题（如 K 个一组翻转、回文链表判断）的基础子过程，值得作为板子背熟。

## 关联题

- 进阶：[92. 反转链表II](92-reverse-linked-list-ii.md) → [25. K个一组翻转链表](25-reverse-nodes-in-k-group.md) —— 反转族难度链：整条 → 区间 → 分组
- 同套路：[24. 两两交换链表结点](24-swap-nodes-in-pairs.md) —— 相邻指针改写的最小练习
- 知识点：递归解法调用栈深度 O(n)，超长链表有栈溢出风险，虚拟机栈结构见[JVM](JVM.md)

