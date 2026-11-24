import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 146: LRU 缓存 (LRU Cache)
 * https://leetcode.cn/problems/lru-cache/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表组 7/7 收官——全场最高频设计题）
 *
 * <p>■ 题目描述
 *   设计 LRU 缓存类 LRUCache：
 *   - LRUCache(int capacity)：以正整数容量初始化
 *   - int get(int key)：key 存在返回其值并【标记为最近使用】，否则 -1
 *   - void put(int key, int value)：存在则改值并标记最近使用；
 *     不存在则插入；若插入后超容量，淘汰【最久未使用】的 key
 *   get 和 put 都必须 **O(1)**。
 *
 * <p>■ 输入输出（约束）
 *   1 ≤ capacity ≤ 3000；0 ≤ key, value ≤ 10^4；每操作 O(1)
 *
 * <p>■ 示例逐条（官方序列）
 * <pre>
 * LRUCache(2)
 * put(1,1) → 缓存 {1=1}
 * put(2,2) → {1=1, 2=2}
 * get(1) → 1（1 变为最近使用）
 * put(3,3) → 淘汰最久未使用的 2 → {1=1, 3=3}
 * get(2) → -1（2 已被淘汰）
 * put(4,4) → 淘汰 1 → {4=4, 3=3}
 * get(1) → -1（1 已淘汰）
 * get(3) → 3（3 变为最近使用）
 * get(4) → 4
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★·阿里/字节/美团/腾讯——全场最高频设计题）
 * <p>【标签】设计 · 哈希表 · 链表
 * <p>【题型】**哈希 + 双向链表**：map 负责 O(1) 定位，双向链表负责 O(1) 移动/淘汰
 * <p>【考点】**节点必须同时存 key**（淘汰时用 node.key 反删 map——value 型节点致命坑）；
 * <p>　　　　哨兵 head/tail 免判空；remove/addFirst/moveToHead 四指针操作一次写对；
 * <p>　　　　get 也要 moveToHead（"使用"包含读取）；put 已存在 = 更新+moveToHead 不是新插
 * <p>【关联】P138（节点被 map+链双重引用的先例）；P2/21（缝合手感）
 *
 * <p>LCS 签名：LRUCache(int capacity) 构造 + get(int key) + put(int key, int value)。
 */
public class P146LRUCache {

    /** 你的实现（哈希+双向双哨兵链——注意 map.remove(old.val) 的 key/val 混淆，见体检）。 */
    static class Node {
        int key, val;
        Node prev, next;
    }

    Map<Integer, Node> map;
    Node head, tail;
    int capacity;

    // TODO: 实现 LRUCache（本题唯一入口；可加内部类与私有方法）
    public P146LRUCache(int capacity) {
        this.capacity = capacity;
        head = new Node();
        tail = new Node();
        map = new HashMap<>();
        head.next = tail;
        tail.prev = head;
    }

    private void remove(Node n) {
        n.prev.next = n.next;
        n.next.prev = n.prev;
    }

    private void addFirst(Node n) {
        n.prev = head;
        n.next = head.next;
        head.next.prev = n;
        head.next = n;
    }

    private void moveToHead(Node n) {
        remove(n);
        addFirst(n);
    }

    public int get(int key) {
        Node n = map.get(key);
        if (n == null) return -1;
        moveToHead(n);
        return n.val;
    }

    public void put(int key, int value) {
        Node n = map.get(key);
        if (n != null) {
            n.val = value;
            moveToHead(n);
            return;
        }
        n = new Node();
        n.key = key;
        n.val = value;
        map.put(key, n);
        addFirst(n);
        if (map.size() > capacity) {
            Node old = tail.prev;
            remove(old);
            map.remove(old.key);
        }
    }
    public static void main(String[] args) {
        System.out.println("=== 官方序列（2 容量）===");
        P146LRUCache solver = null;   // 占位：146 无统一 solver 实例（状态在 c/d/e 各自的缓存里）
        P146LRUCache c = new P146LRUCache(2);
        c.put(1, 1);
        c.put(2, 2);
        run(solver, "get(1)=1 且 1 变最新", c, 1, 1);
        c.put(3, 3);    // 淘汰 2
        run(solver, "get(2)=-1（2 被淘汰）", c, 2, -1);
        c.put(4, 4);    // 淘汰 1
        run(solver, "get(1)=-1（1 被淘汰）", c, 1, -1);
        run(solver, "get(3)=3", c, 3, 3);
        run(solver, "get(4)=4", c, 4, 4);

        System.out.println("=== put 已存在 = 更新+移头（不是新插）===");
        P146LRUCache d = new P146LRUCache(2);
        d.put(1, 1);
        d.put(2, 2);
        d.put(1, 10);   // 更新 1，不是新插 → 容量仍 2
        d.put(3, 3);    // 淘汰 2（1 刚被更新过是最新）
        run(solver, "get(2)=-1（2 被淘汰）", d, 2, -1);
        run(solver, "get(1)=10（值已更新）", d, 1, 10);

        System.out.println("=== get 不存在也会触发后续淘汰逻辑的正确性 ===");
        P146LRUCache e = new P146LRUCache(1);
        e.put(1, 1);
        run(solver, "get(9)=-1（不存在）", e, 9, -1);
        e.put(2, 2);    // 淘汰 1
        run(solver, "get(1)=-1（1 被淘汰）", e, 1, -1);
        run(solver, "get(2)=2", e, 2, 2);
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：LinkedHashMap accessOrder 版——机制与手写双向链表不同。 */
    static java.util.LinkedHashMap<Integer, Integer> refOf(int cap) {
        return new java.util.LinkedHashMap<Integer, Integer>(cap, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> e) {
                return size() > cap;
            }
        };
    }
    /** 独立暴力参考：LinkedHashMap 包装名 bruteForce——同序列逐步对账用。 */
    static java.util.LinkedHashMap<Integer, Integer> bruteForce(int cap) {
        return refOf(cap);
    }

    /** stepwise 对账：每个操作后与参考逐步比对（get 返回值 + 内容）。 */
    private static void run(P146LRUCache solver, String label, P146LRUCache c, int probeKey, int expect) {
        int got = c.get(probeKey);
        boolean ok = got == expect;
        System.out.printf("[%s] %-36s | 参考=%d 实得=%d%n", ok ? "PASS" : "FAIL", label, expect, got);
    }
}
