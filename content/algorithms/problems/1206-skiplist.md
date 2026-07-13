---
topics:
  - 二分查找
techniques:
  - 跳表设计
---

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

- **时间**：`search` / `add` / `erase` **期望** O(log n)，最坏 O(n)
- **空间**：期望 O(n)。每个节点期望层数是 `1/(1-p) = 2`（p = 0.5），所以指针总数期望为 2n

**注意是「期望」不是「保证」**。跳表的层高由随机数决定，理论上可以退化成一条链表——只是概率低到可以忽略（n 个节点全部只有 1 层的概率是 `0.5^n`）。这与红黑树「保证」O(log n) 是本质区别。

## 边界条件

- **`head` 是哨兵**，`val = -1`，拥有 `MAX_LEVEL` 层，保证任何层的搜索都有起点
- **`MAX_LEVEL = 16`**：`p = 0.5` 时能支撑约 `2^16 = 65536` 个节点。题目数据量内够用，Redis 用的是 32
- **允许重复值**：`while (cur.next[i].val < num)` 用的是**严格小于**，所以新节点插在所有相等值**之前**
- **`erase` 不存在的值**：每层都找不到相等节点，返回 false
- **`randomLevel` 至少返回 1**：每个节点必须出现在第 0 层，否则 `search` 找不到它

## 变式

- **带 span 的跳表**：每个前进指针额外记录跨越了多少节点，就能 O(log n) 做「按排名查找」（`ZRANGE`）。Redis 的 zset 正是这么实现的
- **双向跳表**：Redis 的跳表节点有 `backward` 指针，支持 `ZREVRANGE` 反向遍历
- **p 取 0.25**：Redis 的选择。层数更矮、指针更省，但搜索时同层前进的步数变多——**空间与时间的旋钮**
- **[Redis](Redis.md) 的 zset**：跳表 + 哈希表双结构，跳表管范围查询，哈希表管 O(1) 单点查

## 易错点

- **`add` 里的 `if (i < level)` 必须在 while 之后**：先在第 i 层走到插入位置，再判断新节点是否有这一层。写反了会漏接指针
- **搜索的 while 条件是 `< num` 而不是 `<= num`**：走到「最后一个小于 num 的节点」，然后下探。用 `<=` 会跳过目标
- **`search` 最后要 `cur = cur.next[0]` 再比较**：循环结束时 `cur` 停在目标的**前驱**上
- **`erase` 在有重复值时，各层摘除的未必是同一个物理节点**（第 0 层的首个相等节点可能是后插入的那个）。结果仍然正确——总occurrence 恰好减一——但会留下「只在低层存活」的节点。工业实现会先在第 0 层定位到具体节点，再用 `update[]` 数组精确摘除
- 别忘了 `newNode.next[i] = cur.next[i]` 要在 `cur.next[i] = newNode` **之前**，否则自己指向自己

## 面试追问

- **跳表凭什么能替代平衡树**：它用**随机化**换掉了旋转。平衡树靠旋转维持高度，代码复杂且并发下难加锁；跳表的每个节点独立随机层数，插入删除只改几个指针，**局部性好、易于加细粒度锁或做无锁实现**。代价是只有期望复杂度。
- **Redis 为什么用跳表而不是 B+ 树**：B+ 树是**为磁盘设计**的——多路分支压低树高，节点对齐页大小，目标是减少 I/O 次数。Redis 全在内存，一次指针跳转是纳秒级，压低树高没有收益，反而节点内的顺序扫描成了浪费。**同一个取舍在两种介质下有两个解**，见树。
- **为什么不用红黑树**：范围查询（`ZRANGE`）在红黑树上要中序遍历，跳来跳去；跳表第 0 层本身就是有序链表，范围查询就是顺着走。**加上实现简单、并发友好，跳表在内存有序集合这个场景上全面占优。**
- **`p = 0.5` 和 `p = 0.25` 怎么选**：p 越小，节点期望层数 `1/(1-p)` 越小（省指针），但同层要走的步数期望 `1/p` 越大（多比较）。Redis 选 0.25，是因为内存比 CPU 更金贵。**这是一个纯粹的空间-时间旋钮，没有对错。**
- **这题为什么归在二分查找套路下**：因为「逐层下探、每层跳过一大段」就是在有序结构上做**多级二分**的近似——只不过分界点不是中点，而是随机采样出来的索引层。见[二分查找](二分查找.md)。

## 关联题

- 同套路：[146. LRU 缓存](146-lru-cache.md)、[460. LFU 缓存](460-lfu-cache.md) —— 同属结构设计题

