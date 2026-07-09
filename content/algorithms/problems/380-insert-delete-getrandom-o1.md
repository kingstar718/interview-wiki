---
topics:
  - 哈希表
---

# 380. O(1) 时间插入、删除和获取随机元素（Insert Delete GetRandom O(1)）

频次 ★★★★ · 难度 🟡 · 高频：美团/字节

## 题目

设计数据结构，支持 insert/remove/getRandom 全部 O(1)。

## 思路

**HashMap + ArrayList**：Map 存 `val → index`，List 存 val。删除时将最后一个元素移到删除位，O(1)。

## 代码

```java
class RandomizedSet {
    private Map<Integer, Integer> map = new HashMap<>();
    private List<Integer> list = new ArrayList<>();
    private Random rand = new Random();

    public boolean insert(int val) {
        if (map.containsKey(val)) return false;
        map.put(val, list.size());
        list.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val)) return false;
        int idx = map.get(val);
        int last = list.get(list.size() - 1);
        list.set(idx, last);
        map.put(last, idx);
        list.remove(list.size() - 1);
        map.remove(val);
        return true;
    }

    public int getRandom() {
        return list.get(rand.nextInt(list.size()));
    }
}
```

## 复杂度

## 边界条件

## 变式

## 易错点

## 面试追问

## 关联题

- 进阶：[381. O(1)插入删除随机-允许重复](381-insert-delete-getrandom-o1-duplicates-allowed.md)

