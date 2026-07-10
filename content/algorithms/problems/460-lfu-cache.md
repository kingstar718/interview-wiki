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

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[146. LRU 缓存](146-lru-cache.md)、[380. O(1)插入删除随机](380-insert-delete-getrandom-o1.md)

