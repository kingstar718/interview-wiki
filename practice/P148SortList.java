import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 148: 排序链表 (Sort List)
 * https://leetcode.cn/problems/sort-list/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表组 6/7——归并排序上链表）
 *
 * <p>■ 题目描述
 *   给链表头节点，升序排序返回。要求 **O(n log n)** 时间
 *   （进阶：O(1) 空间——递归栈算 O(log n)，自底向上可到 O(1)，本题递归版可 AC）。
 *
 * <p>■ 输入输出（约束）
 *   输入：ListNode head（0 ≤ 节点数 ≤ 5×10^4；-10^5 ≤ val ≤ 10^5）
 *   输出：ListNode（排序后头）
 *
 * <p>■ 示例逐条
 * <pre>
 * 4→2→1→3 → 1→2→3→4
 * -1→5→3→4→0 → -1→0→3→4→5
 * [] → []
 * [1] → [1]
 * 3→2→1（逆序最坏）→ 1→2→3
 * 2→2→2（全等）→ 2→2→2
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（Hard 难度实质，页频 ★★★★·字节/阿里）
 * <p>【标签】链表 · 排序 · 归并
 * <p>【题型】**归并排序上链表**：快慢找中点断开 → 递归两半 → merge 合并（21 的 merge 原样）
 * <p>【考点】为什么链表用归并不用快排/堆排（无随机访问）；断开的落点（fast 从 head.next 起
 * <p>　　　　防 2 节点死循环）；递归出口（null || 单节点）；merge 直接复用 P21
 * <p>【关联】P21（merge 原件）；P234（快慢找中点原件）；P2（缝合手感）
 *
 * <p>LCS 签名：sortList(ListNode head)。
 */
public class P148SortList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** 你的实现（归并上链表：出口挡单节点 + 双层判空 + slow.next=null 断开 + 21 merge 复用）。 */
    public ListNode sortList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode mid = slow.next;
        slow.next = null;
        ListNode l = sortList(head);
        ListNode r = sortList(mid);
        return merge(l, r);
    }

    private ListNode merge(ListNode p1, ListNode p2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (p1 != null && p2 != null) {
            if (p1.val <= p2.val) {
                cur.next = p1;
                p1 = p1.next;
            } else {
                cur.next = p2;
                p2 = p2.next;
            }
            cur = cur.next;
        }
        if (p1 != null) {
            cur.next = p1;
        }
        if (p2 != null) {
            cur.next = p2;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        P148SortList solver = new P148SortList();

        run(solver, of(4, 2, 1, 3), "官方例 → 1→2→3→4");
        run(solver, of(-1, 5, 3, 4, 0), "官方例2 → -1→0→3→4→5");
        run(solver, of(), "空表 → 空");
        run(solver, of(1), "单节点 → [1]");
        run(solver, of(3, 2, 1), "逆序最坏 → 1→2→3 ★");
        run(solver, of(2, 2, 2), "全等 → 2→2→2");
        run(solver, of(1, 2), "两节点 → 1→2 ★ 断开防死循环");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static ListNode of(int... vals) {
        ListNode dummy = new ListNode(0), cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    private static List<Integer> toList(ListNode head) {
        List<Integer> a = new ArrayList<>();
        for (ListNode c = head; c != null; c = c.next) a.add(c.val);
        return a;
    }

    /** 独立暴力参考：拆数组排序重建——机制与链表归并完全不同。 */
    private static List<Integer> bruteForce(ListNode head) {
        List<Integer> a = toList(head);
        a.sort(java.util.Comparator.naturalOrder());
        return a;
    }

    private static void run(P148SortList solver, ListNode head, String label) {
        try {
            List<Integer> want = bruteForce(head);   // 参考先算（排序会重排原链）
            List<Integer> got = toList(solver.sortList(head));
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-24s | 参考=%s%n", ok ? "PASS" : "FAIL", label, want);
            if (!ok) System.out.printf("       实得=%s%n", got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s | 异常: %s%n", label, e);
        }
    }
}
