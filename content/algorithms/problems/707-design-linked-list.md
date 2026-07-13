# 707. 设计链表（Design Linked List）

频次 ★★ · 难度 🟡 · 高频：字节

## 题目

设计链表实现，支持：`get(index)` 获取第 index 个节点值、`addAtHead(val)` 头插、`addAtTail(val)` 尾插、`addAtIndex(index, val)` 在 index 前插入、`deleteAtIndex(index)` 删除第 index 个节点。

## 思路

**哨兵节点 + size 维护**：用 dummy 头节点简化边界处理，维护 `size` 字段使 index 校验 O(1)。所有操作在 `index` 前一个节点处执行。

## 代码

```java
class MyLinkedList {
    private ListNode dummy;
    private int size;

    public MyLinkedList() {
        dummy = new ListNode(0);
        size = 0;
    }

    public int get(int index) {
        if (index < 0 || index >= size) return -1;
        ListNode cur = dummy;
        for (int i = 0; i <= index; i++) cur = cur.next;
        return cur.val;
    }

    public void addAtHead(int val) { addAtIndex(0, val); }

    public void addAtTail(int val) { addAtIndex(size, val); }

    public void addAtIndex(int index, int val) {
        if (index < 0 || index > size) return;
        ListNode cur = dummy;
        for (int i = 0; i < index; i++) cur = cur.next;
        ListNode node = new ListNode(val, cur.next);
        cur.next = node;
        size++;
    }

    public void deleteAtIndex(int index) {
        if (index < 0 || index >= size) return;
        ListNode cur = dummy;
        for (int i = 0; i < index; i++) cur = cur.next;
        cur.next = cur.next.next;
        size--;
    }
}
```

## 复杂度

- **时间**：所有操作 O(n)（单向链表找前驱需遍历），可改用双向链表优化到 O(min(index, size-index))
- **空间**：O(1)

## 边界条件

- `addAtIndex` 的 index 范围是 `[0, size]`（允许在末尾插入），`get`/`delete` 是 `[0, size-1]`
- 空链表：`get` 直接返回 -1，`deleteAtIndex` 直接 return

## 变式

- 双向链表优化：维护 tail 指针 + 双向遍历，`addAtIndex` 和 `deleteAtIndex` 可优化到 O(min(index, size-index))

## 易错点

- 忘记维护 `size` 导致后续操作 index 越界

## 面试追问

- **单向链表 vs 双向链表在这个场景下的取舍？** 单向实现简单、内存省；双向在 index 较大时可以从尾部倒着找，时间减半，适合频繁操作中间位置的场景

## 关联题

- 进阶：[146. LRU 缓存](146-lru-cache.md)（双向链表 + HashMap）