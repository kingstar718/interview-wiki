---
topics:
  - 哈希表
techniques:
  - 哈希+辅助结构
---

# 432. 全 O(1) 的数据结构（All O`one Data Structure）

频次 ★★★ · 难度 🔴 · 高频：阿里

## 题目

实现一个支持 inc/dec/getMaxKey/getMinKey 全部 O(1) 的数据结构。

## 思路

**双向链表 + HashMap**：每个频次对应一个 Bucket（存该频次所有 key），链表按频次升序。

## 代码

```java
class AllOne {
    class Bucket {
        int freq;
        Set<String> keys = new LinkedHashSet<>();
        Bucket prev, next;
        Bucket(int freq) { this.freq = freq; }
    }

    private Bucket head = new Bucket(0), tail = new Bucket(0);
    { head.next = tail; tail.prev = head; }
    private Map<String, Integer> keyFreq = new HashMap<>();
    private Map<Integer, Bucket> freqBucket = new HashMap<>();

    public void inc(String key) {
        int f = keyFreq.getOrDefault(key, 0);
        keyFreq.put(key, f + 1);
        removeFromBucket(key, f);
        addToBucket(key, f + 1);
    }

    public void dec(String key) {
        int f = keyFreq.get(key);
        if (f == 1) { keyFreq.remove(key); removeFromBucket(key, 1); return; }
        keyFreq.put(key, f - 1);
        removeFromBucket(key, f);
        addToBucket(key, f - 1);
    }

    public String getMaxKey() {
        return tail.prev == head ? "" : tail.prev.keys.iterator().next();
    }

    public String getMinKey() {
        return head.next == tail ? "" : head.next.keys.iterator().next();
    }

    private void removeFromBucket(String key, int f) {
        Bucket b = freqBucket.get(f);
        if (b == null) return;
        b.keys.remove(key);
        if (b.keys.isEmpty()) { b.prev.next = b.next; b.next.prev = b.prev; freqBucket.remove(f); }
    }

    private void addToBucket(String key, int f) {
        Bucket b = freqBucket.get(f);
        if (b == null) {
            b = new Bucket(f);
            freqBucket.put(f, b);
            Bucket prev = freqBucket.get(f - 1);
            if (prev == null) prev = f < head.next.freq ? head : head.next; // 插入排序位置
            Bucket next = prev.next;
            prev.next = b; b.prev = prev; b.next = next; next.prev = b;
        }
        b.keys.add(key);
    }
}
```

## 复杂度

- **时间**：`inc` / `dec` / `getMaxKey` / `getMinKey` 全部 O(1)
- **空间**：O(不同 key 的个数)

`inc`/`dec` 之所以是 O(1)，是因为频次每次只变化 **±1**——目标桶要么是相邻桶，要么需要新建一个插在旁边。频次若能任意跳变，就必须用堆，退化成 O(log n)。

## 边界条件

- **`dec` 到 0**：key 要从 `keyFreq` 和桶里一起删掉，不能留下频次为 0 的条目
- **桶变空**：必须从链表里摘掉，否则 `getMinKey` 会返回一个空桶
- **`dec` 一个不存在的 key**：题目保证不会发生；工程实现要判 null
- **哨兵头尾节点**：`head`/`tail` 不存真实数据，使得桶的插入删除不必特判首尾。`getMinKey` 取 `head.next`，`getMaxKey` 取 `tail.prev`
- **所有 key 都被删完**：`head.next == tail`，两个 `get` 方法要返回空串

## 变式

- **[460. LFU 缓存](460-lfu-cache.md)**：同样按频次分桶，但只需要 `getMinKey`（淘汰最少使用者），不需要 `getMaxKey`
- **只要 `getMaxKey`**：可以用「频次 → 桶」的哈希 + 一个 `maxFreq` 变量，因为 `inc` 只会让 max 增 1、`dec` 只会让它减 1
- **频次可以任意增减 `k`**：桶不再相邻，链表定位失效，改用平衡树或堆，O(log n)
- **[146. LRU 缓存](146-lru-cache.md)**：约束是「最近访问顺序」而非频次，同为哈希 + 双向链表

## 易错点

- **桶必须按频次严格有序**，且相邻桶的频次不一定连续（`1 → 5` 是合法的，中间的桶都空了被摘掉）。`getMaxKey`/`getMinKey` 靠的是**链表有序**，不是频次连续
- **新建桶要插在正确的位置**：`inc` 时 `f+1` 的桶应插在 `f` 的桶**之后**；`dec` 时 `f-1` 的桶应插在 `f` 的桶**之前**。方向搞反会破坏有序性
- **摘空桶的时机**：`removeFromBucket` 之后立刻检查桶是否为空并摘除，同时清理 `freqBucket` 里的映射，否则下次会拿到一个已脱链的桶
- 用 `LinkedHashSet` 存桶内 key，是为了 O(1) 的增删 + 取任意一个（`getMaxKey` 只需返回**任意一个**最大频次的 key）

## 面试追问

- **为什么频次只能 ±1 才有 O(1) 解**：因为「目标桶必然与当前桶相邻」这个性质，把「查找目标桶」从搜索问题变成了一次指针跳转。**这是本题所有 O(1) 的来源。** 一旦允许 `inc(key, k)`，就得在有序结构里定位频次 `f+k` 的桶，O(log n) 起步。
- **为什么不能直接用堆**：堆的 `getMax` 是 O(1)，但 `inc` 一个任意 key 需要先定位它在堆中的位置（哈希可以）再上浮/下沉，O(log n)。**堆维护的是「全序」，而本题只需要「最大和最小」两端**，双向链表 + 分桶恰好只维护了这一点点信息，所以更便宜。
- **它和 LFU 的关系**：LFU 就是这个结构去掉 `getMaxKey`、加上容量限制和淘汰逻辑。**先把 432 写熟，460 就是它的应用题。**
- **桶内为什么用 Set 而不是链表**：需要 O(1) 地把某个指定 key 从桶里删掉（`dec` 时）。链表要 O(n) 查找，除非再存一个「key → 链表节点」的映射——那就是 LRU 的做法。用 `LinkedHashSet` 一步到位。

## 关联题

- 基础：[146. LRU 缓存](146-lru-cache.md)、[460. LFU 缓存](460-lfu-cache.md)

