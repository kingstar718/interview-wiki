---
topics:
  - 哈希表
techniques:
  - 哈希+辅助结构
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

- **时间**：三个操作均摊 O(1)。`LinkedHashSet` 的增删查都是 O(1)，`iterator().next()` 取任意一个下标也是 O(1)
- **空间**：O(n)

## 边界条件

- **`val == lastVal`（删的值和末尾值相同）**：`map.get(val)` 和 `map.get(lastVal)` 是**同一个集合**。代码先 `remove(idx)`，再 `remove(lastIdx)` / `add(idx)`——三步作用在同一个集合上仍然正确，因为 `idx` 已被移除，重新加回来是幂等的
- **`idx == lastIdx`（删的就是末尾）**：跳过整个交换分支，直接删末尾。若不跳过，会把刚删掉的下标又加回集合
- **重复值全部删完**：集合变空但**键还留在 map 里**，所以 `remove` 的判断是 `!map.containsKey(val) || map.get(val).isEmpty()` 两个条件。想省内存就在集合空时 `map.remove(val)`
- **`getRandom` 的概率**：某个值出现 k 次，就在 `list` 里占 k 个槽位，被抽中的概率自然是 k/n——**这正是题目要的「与出现次数成正比」**

## 变式

- **[380. 不含重复值](380-insert-delete-getrandom-o1.md)**：`Map<Integer, Integer>` 即可，是本题的退化
- **要求 `getRandom` 对**不同值**等概率**（不按出现次数）：再维护一个「去重值列表」，两套结构同步
- **删除某个值的全部实例**：直接遍历它的下标集合，但每次删都要交换，实现要小心下标失效
- **[432. 全 O(1) 的数据结构](432-all-oone-data-structure.md)**：另一类 O(1) 容器设计，约束是「取最大/最小频次」

## 易错点

- **必须用 `LinkedHashSet` 而不是 `HashSet`**：不是为了顺序，而是为了 `iterator().next()` 的**行为稳定**。用 `HashSet` 在极端情况下也能过，但取出的下标不可预测，调试时无法复现
- **`if (idx != lastIdx)` 这个判断不能省**。省掉后，当删的正好是末尾元素时，会执行 `map.get(lastVal).remove(lastIdx); add(idx);`——而 `idx == lastIdx`，等于把刚删的下标加了回来，集合里留下一个指向已删槽位的悬垂下标
- 交换时要更新的是 **`lastVal` 的下标集合**（把 `lastIdx` 换成 `idx`），不是 `val` 的
- 集合空了不清理 map 键，会导致内存随不同值的个数单调增长——面试里说出来是加分项

## 面试追问

- **380 到 381 的难点跃迁在哪**：380 里「值 → 下标」是一对一，一个 `Integer` 就够；381 是一对多，必须存**下标集合**。而删除时要从集合里**任取一个**下标，这就要求集合本身支持 O(1) 的「取任意元素 + 删除指定元素」——`HashSet` 恰好满足。
- **为什么删除时可以任取一个下标，不必是特定的**：因为题目只要求「删掉一个匹配的实例」，实例之间不可区分。**「不可区分」这个性质把「删指定元素」放宽成了「删任意元素」，是 O(1) 的前提。**
- **`getRandom` 的正确性怎么论证**：`list` 里每个槽位被抽中的概率相同（`rand.nextInt(size)` 均匀），而值 v 占据了 count(v) 个槽位，所以 P(v) = count(v)/n。**关键是 `list` 必须始终紧密无空洞**——末尾交换法保证了这一点。
- **能不能不用交换，用「墓碑标记」删除**：能，但 `list` 会出现空洞，`getRandom` 可能抽中已删元素，需要重试，退化成期望 O(1) 而非最坏 O(1)，且空洞率高时性能崩溃。**这正是很多存储引擎在「原地删除 vs 标记删除 + 压缩」之间的权衡。**

## 关联题

- 基础：[380. O(1)插入删除随机](380-insert-delete-getrandom-o1.md)

