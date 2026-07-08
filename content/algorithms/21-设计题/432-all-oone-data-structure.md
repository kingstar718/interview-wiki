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

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 基础：[146. LRU 缓存](146-lru-cache.md)、[460. LFU 缓存](460-lfu-cache.md)

---

[← 返回训练计划](社招算法训练计划.md)
