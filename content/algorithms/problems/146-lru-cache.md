---
topics:
  - 哈希表
techniques:
  - 哈希+辅助结构
---

# 146. LRU缓存（LRU Cache）

频次 ★★★★★ · 难度 🟡 · 高频：阿里/字节/美团/腾讯

## 题目

设计支持 `get(key)` 与 `put(key, value)` 的 LRU（最近最少使用）缓存，容量固定，两操作均要求 **O(1)**；容量满时淘汰最久未使用的键。

## 思路

O(1) 的 get 要哈希表，O(1) 的"移到最新/淘汰最旧"要能任意位置删除并头插的**双向链表**——两者组合：

```text
HashMap<key, Node>  ──指向──>  双向链表结点(key, value)
                               head(最新) <-> ... <-> tail(最旧)
```

- `get`：哈希查到结点 → 摘下 → 头插 → 返回值
- `put`：已存在则更新值并移头；不存在则新建头插，超容量摘掉尾结点，**并用结点里存的 key 反删哈希表**（这就是结点必须存 key 的原因）

## 代码

```java
class LRUCache {
    class Node { int key, val; Node prev, next; }
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(), tail = new Node(); // 哨兵,免判空
    private final int capacity;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail; tail.prev = head;
    }

    public int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;
        moveToHead(n);
        return n.val;
    }

    public void put(int key, int value) {
        Node n = map.get(key);
        if (n != null) { n.val = value; moveToHead(n); return; }
        n = new Node(); n.key = key; n.val = value;
        map.put(key, n); addFirst(n);
        if (map.size() > capacity) {
            Node old = tail.prev;      // 最久未使用
            remove(old);
            map.remove(old.key);       // 结点存 key 就是为了这一步
        }
    }

    private void remove(Node n) { n.prev.next = n.next; n.next.prev = n.prev; }
    private void addFirst(Node n) {
        n.next = head.next; n.prev = head;
        head.next.prev = n; head.next = n;
    }
    private void moveToHead(Node n) { remove(n); addFirst(n); }
}
```

## 复杂度

- **时间**：get/put 均 O(1)
- **空间**：O(capacity)

## 边界条件

- `put` 更新已存在的 key：只改值移头，**不触发淘汰**
- 容量 1：每次 put 新 key 都先淘汰
- `get` 不存在的 key 返回 -1，且不影响链表顺序

## 变式

- 用 `LinkedHashMap(capacity, 0.75f, true)` + 重写 `removeEldestEntry` 十行实现——面试先问"能不能用现成的"，答出来再手写
- 线程安全版：整体加锁最简单；分段锁或用 `ConcurrentLinkedHashMap`（Caffeine 前身）思路作加分项

## 易错点

- 链表结点不存 key → 淘汰尾结点时无法删哈希表对应项（最经典的错）
- 双哨兵（dummy head/tail）省掉所有"头尾为空"的特判，别省这两个结点
- `put` 已存在时忘记 `moveToHead`——更新也算"使用"
- 先 `map.put` 后判容量，注意 `>` 不是 `>=`（新元素已计入）

## 面试追问

- **为什么是哈希表+双向链表，单向链表行不行？** 摘除任意结点需要前驱，单向链表找前驱 O(n)；双向链表让"摘除"自身 O(1)
- **JDK 里有现成的吗？** `LinkedHashMap` 开 accessOrder 就是 LRU 链，`removeEldestEntry` 钩子控制淘汰，见[集合框架](集合框架.md)
- **Redis 的 LRU 是这么实现的吗？** 不是，Redis 用**近似 LRU**（随机采样淘汰最旧），全量链表内存开销太大，见[Redis](Redis.md)内存淘汰策略

## 关联题

- 进阶：[460. LFU 缓存](460-lfu-cache.md) —— 按频率淘汰，双哈希 + 频率桶链表
- 同套路：[380. O(1) 时间插入删除和获取随机元素](380-insert-delete-getrandom-o1.md) —— 同为"组合基础结构凑出全 O(1)"
- 知识点：[Redis](Redis.md)内存淘汰(allkeys-lru 近似采样)、[集合框架](集合框架.md) LinkedHashMap

