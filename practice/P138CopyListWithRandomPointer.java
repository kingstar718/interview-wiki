/**
 * LeetCode 138: 复制带随机指针的链表 (Copy List with Random Pointer)
 * https://leetcode.cn/problems/copy-list-with-random-pointer/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表组 5/7——随机指针深拷贝）
 *
 * <p>■ 题目描述
 *   链表节点除 next 外还有 **random** 指针（可指向链中任意节点或 null）。
 *   **深拷贝**整条链：新链所有指针都指向【新节点】，与原链完全独立。
 *
 * <p>■ 输入输出（约束）
 *   输入：Node head（0 ≤ 节点数 ≤ 1000；-10^4 ≤ val ≤ 10^4）
 *   输出：Node（新链头）
 *   测试用例以 [[val, random下标], ...] 表示（random 下标 -1 = null）
 *
 * <p>■ 示例逐条
 * <pre>
 * [[7,null],[13,0],[11,4],[10,2],[1,0]] → 深拷贝（官方例）
 * [[1,1],[2,1]] → 深拷贝（random 指自身/前节点）
 * [[3,null]] → 深拷贝（单节点 random=null）
 * [] → null（空表）
 * [[val,0]] → 单节点自环式 random（指自己下标 0）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/美团）
 * <p>【标签】链表 · 哈希表
 * <p>【题型】**两次遍历 + 原新节点映射**（HashMap：第一遍建节点只填 val，第二遍接 next/random）
 * <p>【考点】难点="random 目标可能还没被创建"→ 先建完再接线；**map.get(null) 天然返回 null**
 * <p>　　　　（两处 null 不用特判）；O(1) 空间的"插花法"（进阶：原节点后插副本再拆）
 * <p>【关联】P146 LRU（哈希+链表的复合结构预告）；P2（"节点是数据"的极致——本题节点同时被
 * <p>　　　　next 链和 random 链引用）
 *
 * <p>LCS 签名：copyRandomList(Node head)。
 */
import java.util.HashMap;
import java.util.Map;

public class P138CopyListWithRandomPointer {

    static class Node {
        int val;
        Node next;
        Node random;
        Node(int val) { this.val = val; }
    }

    /** 你的实现（HashMap 两遍法：先造完记本子，再查本子接 next/random——map.get(null) 免特判）。 */
    public Node copyRandomList(Node head) {
        if (head == null) {
            return null;
        }
        Map<Node, Node> map = new HashMap<>();
        Node cur = head;
        while (cur != null) {
            map.put(cur, new Node(cur.val));
            cur = cur.next;
        }

        cur = head;
        while (cur != null) {
            map.get(cur).next = map.get(cur.next);
            map.get(cur).random = map.get(cur.random);
            cur = cur.next;
        }
        return map.get(head);
    }

    public static void main(String[] args) {
        P138CopyListWithRandomPointer solver = new P138CopyListWithRandomPointer();

        run(solver, of(7, -1, 13, 0, 11, 4, 10, 2, 1, 0), "官方例 → 深拷贝");
        run(solver, of(1, 1, 2, 1), "random 指自身/前节点 → 深拷贝");
        run(solver, of(3, -1), "单节点 random=null → 深拷贝");
        run(solver, of(), "空表 → null");
        run(solver, of(5, 0), "单节点自指 → 深拷贝");
        run(solver, of(4, -1, 8, -1, 6, -1), "random 全 null 长链 → 深拷贝");
        run(solver, of(9, 2, 3, 0, 7, 1), "random 指尾+指头 → 深拷贝");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 按 [val, randomIdx] 序偶构造（-1 = null）。 */
    private static Node of(int... seq) {
        if (seq.length == 0) return null;
        Node[] nodes = new Node[seq.length / 2];
        for (int i = 0; i < nodes.length; i++) nodes[i] = new Node(seq[2 * i]);
        for (int i = 0; i < nodes.length; i++) {
            if (i + 1 < nodes.length) nodes[i].next = nodes[i + 1];
            int r = seq[2 * i + 1];
            nodes[i].random = (r == -1) ? null : nodes[r];
        }
        return nodes[0];
    }

    /** 校验：结构一致 + 全新节点 + random 精确对应（逐位比对原链）。 */
    private static boolean check(Node orig, Node copy) {
        Node a = orig, b = copy;
        java.util.Map<Node, Node> map = new java.util.HashMap<>();
        while (a != null && b != null) {
            if (a.val != b.val) return false;
            if (b == a) return false;                       // 复用了原节点 ✗
            if (map.containsKey(b)) return false;           // 新链里复用了节点 ✗
            map.put(b, a);
            a = a.next;
            b = b.next;
        }
        if (a != null || b != null) return false;
        a = orig; b = copy;
        while (a != null) {
            Node aR = a.random, bR = b.random;
            if ((aR == null) != (bR == null)) return false;
            if (aR != null && map.get(bR) != aR) return false;  // random 对应错位
            a = a.next;
            b = b.next;
        }
        return true;
    }

    /** 独立暴力参考：按下标克隆重建（先建数组再接指针）——机制与 HashMap 映射不同。 */
    private static Node bruteForce(Node head) {
        if (head == null) return null;
        java.util.List<Node> orig = new java.util.ArrayList<>();
        for (Node c = head; c != null; c = c.next) orig.add(c);
        Node[] cp = new Node[orig.size()];
        java.util.Map<Node, Integer> idx = new java.util.HashMap<>();
        for (int i = 0; i < orig.size(); i++) {
            idx.put(orig.get(i), i);
            cp[i] = new Node(orig.get(i).val);
        }
        for (int i = 0; i < orig.size(); i++) {
            if (i + 1 < cp.length) cp[i].next = cp[i + 1];
            Node r = orig.get(i).random;
            cp[i].random = (r == null) ? null : cp[idx.get(r)];
        }
        return cp[0];
    }

    private static void run(P138CopyListWithRandomPointer solver, Node head, String label) {
        try {
            Node want = bruteForce(head);   // 参考先算（实现不改原链，但格式统一）
            Node got = solver.copyRandomList(head);
            boolean okWant = check(head, want);
            boolean okGot = check(head, got);
            System.out.printf("[%s] %-30s | 参考=%s 实得=%s%n",
                    okWant && okGot ? "PASS" : "FAIL", label,
                    okWant ? "深拷贝合法" : "参考自身异常", okGot ? "深拷贝合法" : "校验未过");
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s | 异常: %s%n", label, e);
        }
    }
}
