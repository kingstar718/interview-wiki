# 138. 复制带随机指针的链表（Copy List with Random Pointer）

频次 ★★★★ · 难度 🟡 · 高频：字节/美团

## 题目

链表每个节点除了 `next` 指针，还有一个 `random` 指针，可能指向链表中任意节点或 `null`。要求深拷贝这个链表（新链表的所有指针都指向新节点，不能复用原节点）。

**示例**：
```
输入: [[7,null],[13,0],[11,4],[10,2],[1,0]]  (val, random 指向的下标)
输出: 一份完全独立的深拷贝
```

## 思路

难点在于拷贝 `random` 时，目标节点可能还没被创建。**HashMap 映射法**：

1. 第一遍遍历，为每个原节点创建对应的新节点（只填 `val`），并记录 `原节点 -> 新节点` 的映射。
2. 第二遍遍历，利用映射把新节点的 `next`、`random` 接好：`map.get(cur).next = map.get(cur.next)`，`random` 同理（`map.get(null)` 天然是 `null`，不用特判）。

## 代码

```java
class Node {
    int val;
    Node next;
    Node random;
    Node(int val) { this.val = val; }
}

public Node copyRandomList(Node head) {
    if (head == null) return null;

    Map<Node, Node> map = new HashMap<>();
    for (Node cur = head; cur != null; cur = cur.next) {
        map.put(cur, new Node(cur.val));
    }
    for (Node cur = head; cur != null; cur = cur.next) {
        map.get(cur).next = map.get(cur.next);
        map.get(cur).random = map.get(cur.random);
    }
    return map.get(head);
}
```

## 复杂度

- **时间**：O(n) — 两次遍历
- **空间**：O(n) — HashMap 存储 n 个映射

## 边界条件

- 空链表（`head == null`）：直接返回 `null`。
- `random` 指向 `null`：`map.get(null)` 返回 `null`（HashMap 允许 `null` 作为 key 查询未命中），天然正确，不需要特判。
- `random` 指向自身：`map.get(cur)` 在第一次遍历时已经建立好映射，第二次遍历能正确取到"自己对应的新节点"。

## 变式

- **O(1) 额外空间**的做法：在每个原节点后面插入一个复制节点（`A -> A' -> B -> B'`），这样 `A'.random = A.random.next`（复制节点紧跟在对应原节点后面），最后再把两条链表拆开。空间换成了对原链表的临时改写，思路更绕但省掉了 HashMap。
- 如果节点除了 `random` 还有更多种类的额外指针：HashMap 映射法可以直接扩展（多复制几个字段），O(1) 空间的插入法则需要更精细地处理多种指针关系。

## 易错点

- 第一遍遍历只创建节点、不接指针，第二遍才接 `next`/`random`——如果想在一遍遍历里同时接好指针，会遇到"要用的目标节点还没被创建"的问题（尤其是 `random` 可能指向链表后面还没访问到的节点）。
- 用 `HashMap<Node, Node>` 的 key 是**节点引用**而不是节点的值，因为不同节点可能有相同的 `val`，用值做 key 会导致映射错乱。

## 面试追问

- **能不能不用 HashMap，把空间降到 O(1)？** 可以，用"原节点后插入复制节点"的三步法：先插入交织的复制节点，再利用这种相邻关系设置 `random`（`copy.random = original.random.next`），最后把两条链表拆开分离；这个方法牺牲了代码的直观性换取了 O(1) 额外空间。
- **如果链表里存在环（`next` 指针成环），这个算法还适用吗？** HashMap 映射法依然适用，因为它是先枚举所有节点建立映射，再重新连指针，不依赖"链表在有限步内能走到 `null`"的假设；但需要注意第一遍遍历如果用 `while (cur != null)` 判断结束条件，成环的链表会导致死循环，需要换成按已知节点集合遍历的方式。

## 关联题

- 同套路：[133. 克隆图](133-clone-graph.md) —— 同为"哈希映射旧结点 → 新结点"的深拷贝，图版要配合 BFS/DFS
- 进阶：O(1) 空间的交织复制法（新结点插在原结点后 → 复制 random → 拆链）
- 知识点：深拷贝 vs 浅拷贝的语义区别见[Java基础](Java基础.md)"深浅拷贝"

---

[← 返回训练计划](社招算法训练计划.md)
