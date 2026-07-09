# 1206. 跳表（Design Skiplist）

频次 ★★★ · 难度 🔴 · 高频：字节

## 题目

设计跳表，支持搜索/插入/删除 O(log n)。不用内置数据结构。

## 思路

跳表是多层链表，`maxLevel=16`，每层数据有序。搜索从顶层开始找最接近的节点，逐层下探。

## 代码

```java
class Skiplist {
    private static final int MAX_LEVEL = 16;
    private Node head = new Node(-1, MAX_LEVEL);
    private Random rand = new Random();

    class Node {
        int val;
        Node[] next;
        Node(int val, int level) { this.val = val; next = new Node[level]; }
    }

    public boolean search(int target) {
        Node cur = head;
        for (int i = MAX_LEVEL - 1; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < target) cur = cur.next[i];
        }
        cur = cur.next[0];
        return cur != null && cur.val == target;
    }

    public void add(int num) {
        int level = randomLevel();
        Node newNode = new Node(num, level);
        Node cur = head;
        for (int i = MAX_LEVEL - 1; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < num) cur = cur.next[i];
            if (i < level) {
                newNode.next[i] = cur.next[i];
                cur.next[i] = newNode;
            }
        }
    }

    public boolean erase(int num) {
        boolean found = false;
        Node cur = head;
        for (int i = MAX_LEVEL - 1; i >= 0; i--) {
            while (cur.next[i] != null && cur.next[i].val < num) cur = cur.next[i];
            if (cur.next[i] != null && cur.next[i].val == num) {
                cur.next[i] = cur.next[i].next[i];
                found = true;
            }
        }
        return found;
    }

    private int randomLevel() {
        int level = 1;
        while (rand.nextDouble() < 0.5 && level < MAX_LEVEL) level++;
        return level;
    }
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 同套路：[146. LRU 缓存](146-lru-cache.md)、[460. LFU 缓存](460-lfu-cache.md) —— 同属结构设计题

