---
topics:
  - 哈希表
---

# 381. O(1) 时间插入、删除和获取随机元素 - 允许重复（Insert Delete GetRandom O(1) - Duplicates allowed）

频次 ★★★ · 难度 🔴 · 高频：字节

## 题目

在 380 基础上允许重复值，insert 总是 true，remove 删除一个匹配的实例。

## 思路

**HashMap<Integer, Set<Integer>> + ArrayList**：Map 存 `val → 下标集合`（LinkedHashSet），List 存值。删除同 380 的"末尾交换"。

## 代码

```java
class RandomizedCollection {
    private Map<Integer, Set<Integer>> map = new HashMap<>();
    private List<Integer> list = new ArrayList<>();
    private Random rand = new Random();

    public boolean insert(int val) {
        boolean notPresent = !map.containsKey(val);
        map.computeIfAbsent(val, k -> new LinkedHashSet<>()).add(list.size());
        list.add(val);
        return notPresent;
    }

    public boolean remove(int val) {
        if (!map.containsKey(val) || map.get(val).isEmpty()) return false;
        int idx = map.get(val).iterator().next();
        map.get(val).remove(idx);

        int lastIdx = list.size() - 1;
        int lastVal = list.get(lastIdx);
        if (idx != lastIdx) {
            list.set(idx, lastVal);
            map.get(lastVal).remove(lastIdx);
            map.get(lastVal).add(idx);
        }
        list.remove(lastIdx);
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

- 基础：[380. O(1)插入删除随机](380-insert-delete-getrandom-o1.md)

