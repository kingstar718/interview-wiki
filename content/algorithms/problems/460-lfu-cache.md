---
topics:
  - 哈希表
techniques:
  - 哈希+辅助结构
---

# 460. LFU 缓存（LFU Cache）

频次 ★★★★ · 难度 🔴 · 高频：字节/阿里

## 题目

设计 LFU 缓存，`get`/`put` O(1)，访问频次最低的缓存满时淘汰。

## 思路

**双哈希表**：`key→(val, freq)` + `freq→LinkedHashSet<key>`，维护最小频次变量 minFreq。

## 代码

```java
class LFUCache {
    private Map<Integer, int[]> kv = new HashMap<>();         // key → [val, freq]
    private Map<Integer, LinkedHashSet<Integer>> freqMap = new HashMap<>();
    private int capacity, minFreq;

    public LFUCache(int capacity) { this.capacity = capacity; }

    public int get(int key) {
        if (!kv.containsKey(key)) return -1;
        increaseFreq(key);
        return kv.get(key)[0];
    }

    public void put(int key, int value) {
        if (capacity == 0) return;
        if (kv.containsKey(key)) {
            kv.get(key)[0] = value;
            increaseFreq(key);
            return;
        }
        if (kv.size() == capacity) evict();
        kv.put(key, new int[]{value, 0});
        increaseFreq(key);
        minFreq = 0;
    }

    private void increaseFreq(int key) {
        int[] entry = kv.get(key);
        int oldFreq = entry[1];
        entry[1]++;  // freq++

        freqMap.get(oldFreq).remove(key);
        if (freqMap.get(oldFreq).isEmpty() && oldFreq == minFreq) minFreq++;

        freqMap.computeIfAbsent(oldFreq + 1, k -> new LinkedHashSet<>()).add(key);
    }

    private void evict() {
        LinkedHashSet<Integer> set = freqMap.get(minFreq);
        int key = set.iterator().next();
        set.remove(key);
        kv.remove(key);
    }
}
```

## 复杂度

- **时间**：O(1) get/put
- **空间**：O(capacity)

## 边界条件

- **`capacity == 0`**：`put` 直接返回，不能进入淘汰逻辑（否则会对空缓存做 `evict`）
- **`minFreq` 的维护**：插入新 key 后 `minFreq` 必须重置为 1；`increaseFreq` 把最后一个频次为 `minFreq` 的 key 挪走后，`minFreq++`
- **频次相同时淘汰谁**：淘汰**最久未使用**的那个。所以桶内用 `LinkedHashSet`——它保留插入顺序，`iterator().next()` 取到的就是最老的
- **`get` 一个不存在的 key**：返回 -1，且**不能**改变任何频次
- **更新已存在的 key**：既要改值，也要涨频次，不能只改值

## 变式

- **[146. LRU 缓存](146-lru-cache.md)**：只按时间淘汰，不看频次。哈希表 + 双向链表
- **[432. 全 O(1) 的数据结构](432-all-oone-data-structure.md)**：本题的骨架，去掉容量与淘汰、加上 `getMaxKey`
- **LFU 带老化（aging）**：纯 LFU 有「历史高频项永不淘汰」的缺陷，实际系统会周期性把所有频次减半或按时间衰减
- **[Redis](Redis.md) 的近似 LFU**：不维护精确频次，用 8 bit 的对数计数器 + 衰减时间戳，牺牲精度换内存

## 易错点

- **`minFreq` 只在两处变化**：`put` 新 key 时置 1；`increaseFreq` 中若 `freqMap.get(minFreq)` 变空则 `minFreq++`。**它永远不需要遍历去重新计算**——这是 O(1) 的关键
- **`increaseFreq` 后要清理空桶**，否则 `evict` 可能取到空集合
- **淘汰要同时删两处**：`kv` 和 `freqMap.get(minFreq)`。只删一个会留下幽灵条目
- **新插入的 key 频次是 1 不是 0**。代码里先 `new int[]{value, 0}` 再 `increaseFreq` 涨到 1，是为了复用同一段逻辑，但不能忘了那一步
- 淘汰发生在**插入新 key 之前**，且只在 `kv.size() == capacity` 时。写成 `>` 会导致缓存超容

## 面试追问

- **LFU 比 LRU 好在哪、差在哪**：LFU 抗「一次性扫描」——一次全表扫描会把 LRU 缓存彻底冲垮，而 LFU 因为这些 key 频次只有 1，不会挤掉热点。**代价是 LFU 对访问模式的变化反应迟钝**：曾经的热点靠历史频次赖着不走，新热点挤不进来。所以工程上纯 LFU 很少见，都要加老化。
- **为什么 `minFreq` 不用堆维护**：因为频次每次只 +1。**「最小值只会 +1 或在插入时被重置为 1」这个性质，让一个整数变量就足以维护最小值**，不需要任何有序结构。这与 [432](432-all-oone-data-structure.md) 用相邻桶跳转是同一个道理。
- **频次相同时为什么要用 LRU 兜底**：不然行为不确定。`LinkedHashSet` 让同频次桶内部保持插入顺序，等价于「频次为第一关键字、时间为第二关键字」的复合淘汰策略。
- **Redis 是怎么做 LFU 的**：`maxmemory-policy allkeys-lfu`。它不存精确计数，而是每个对象 24 bit 里塞 16 bit 时间戳 + 8 bit 对数计数器：计数越大越难增长（概率性递增），并按空闲时间衰减。**用近似换内存,这是缓存系统的典型取舍**，见 [Redis](Redis.md)。

## 关联题

- 基础：[146. LRU 缓存](146-lru-cache.md)、[380. O(1)插入删除随机](380-insert-delete-getrandom-o1.md)

