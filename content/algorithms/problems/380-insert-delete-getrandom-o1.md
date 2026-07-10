---
topics:
  - 哈希表
techniques:
  - 哈希+辅助结构
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

- **时间**：`insert` / `remove` / `getRandom` 均摊 O(1)
- **空间**：O(n)

「均摊」来自 `ArrayList` 的扩容：单次 `add` 最坏 O(n)，但 n 次 `add` 总代价 O(n)，见[摊还](摊还.md)。

## 边界条件

- **删除的正好是最后一个元素**：`idx == list.size()-1`，`list.set(idx, last)` 是自赋值，`map.put(last, idx)` 重写同一个键，随后 `map.remove(val)` 把它删掉——**顺序保证了正确性**，先 put 后 remove 不能反
- **删除后集合为空**：`getRandom` 会对 `rand.nextInt(0)` 抛异常。题目保证调用时非空
- **重复 insert 同一个值**：返回 false，不修改任何结构
- **`list.remove(list.size()-1)`** 调用的是 `remove(int index)`（按下标删末尾，O(1)），不是 `remove(Object)`。若元素类型是 `Integer` 且传入包装类型，会走成按值删除的 O(n) 版本

## 变式

- **[381. 允许重复值](381-insert-delete-getrandom-o1-duplicates-allowed.md)**：`Map<Integer, Set<Integer>>` 存一个值的所有下标
- **按权重随机**：末尾交换法失效，改用前缀和 + 二分（O(log n)）或 Alias Method（O(1) 但预处理 O(n)）
- **要求 `getRandom` 不重复地遍历全部元素**：Fisher-Yates 洗牌
- **[146. LRU 缓存](146-lru-cache.md)**：同样是「哈希表 + 辅助结构」的 O(1) 容器设计，只是辅助结构换成双向链表

## 易错点

- **末尾交换的三步顺序不能乱**：先把末尾值搬到 `idx`，再更新末尾值在 map 里的新下标，最后删末尾、删 map 条目。任何一步提前都会丢引用
- **`map.put(last, idx)` 必须在 `map.remove(val)` 之前**。当 `val == last`（删的就是末尾）时，先 remove 再 put 会把已删的键加回来
- 不能用 `list.remove(idx)` 直接删中间元素——那是 O(n) 的数组搬移，本题的全部难点就是绕开它
- `getRandom` 必须基于 `list` 的**连续存储**才能 O(1) 定位。如果把 `list` 换成 `LinkedList`，随机访问退化成 O(n)

## 面试追问

- **为什么 `getRandom` 要求底层是数组**：等概率随机取一个元素，需要「按下标 O(1) 访问」+「元素紧密排列无空洞」。数组两条都满足，链表和哈希表都不满足。**`getRandom` 这个需求单独就把数据结构锁死成了数组。**
- **既然要数组，`remove` 怎么绕开 O(n) 搬移**：因为题目不要求**保持顺序**。既然顺序无所谓，删中间元素时就把末尾元素填过来，数组依然紧密。**「不要求有序」这个宽松条件，正是 O(1) 删除的授权。**
- **和 LRU 的「哈希表 + 双向链表」是同一类设计吗**：是。都是「哈希表负责 O(1) 定位，辅助结构负责满足另一个约束」。LRU 的约束是「维护访问顺序」，所以用双向链表；本题的约束是「等概率随机」，所以用数组。**选哪个辅助结构，由第二个约束决定。**
- **并发场景怎么做**：三个操作都要跨两个结构，必须原子。简单做法一把锁；进阶做法是分段（但 `getRandom` 需要全局视图，分段后概率不再均匀）。**这类「多结构联动」的容器天然难以无锁化。**

## 关联题

- 进阶：[381. O(1)插入删除随机-允许重复](381-insert-delete-getrandom-o1-duplicates-allowed.md)

