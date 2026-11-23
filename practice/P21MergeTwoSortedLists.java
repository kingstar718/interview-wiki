import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 21: 合并两个有序链表 (Merge Two Sorted Lists)
 * https://leetcode.cn/problems/merge-two-sorted-lists/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表组 3/7——dummy 头节点原型）
 *
 * <p>■ 题目描述
 *   将两个**升序**链表合并为一个新的**升序**链表并返回。
 *   新链表由**拼接原节点**构成（不许 new 节点装值）。
 *
 * <p>■ 输入输出（约束）
 *   输入：ListNode list1, list2（0 ≤ 节点数 ≤ 50；-100 ≤ val ≤ 100）
 *   输出：ListNode（合并后头节点）
 *
 * <p>■ 示例逐条
 * <pre>
 * 1→2→4 + 1→3→4 → 1→1→2→3→4→4
 * [] + [] → []
 * [] + [0] → [0]
 * 1→2→4 + 1→3→4→5→6 → 1→1→2→3→4→4→5→6（长度不等 ★）
 * [-9→3] + [5→7] → -9→3→5→7（负数）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★★·全厂）
 * <p>【标签】链表 · 递归 · 归并
 * <p>【题型】**dummy 头节点** + 双指针归并（每次摘较小头接尾）+ 尾部直连剩余
 * <p>【考点】dummy 消灭"第一个节点是谁"特判（27 移除元素同思想）；摘节点时先摘后进；
 * <p>　　　　走完一条后 cur.next 直接接另一条剩余（它本身有序）
 * <p>【关联】P27 移除元素（dummy 同思想）；P148 排序链表（归并的递归版直接复用本题）；
 * <p>　　　　P2 两数相加（链表组 4/7）
 *
 * <p>LCS 签名：mergeTwoLists(ListNode list1, ListNode list2)。
 */
public class P21MergeTwoSortedLists {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** 你的实现（dummy+双指针归并：值比较用 <，else 兜住相等；尾部 if/if 直连剩余）。 */
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                cur.next = list1;
                list1 = list1.next;
            } else {
                cur.next = list2;
                list2 = list2.next;
            }
            cur = cur.next;
        }
        if (list1 != null) {
            cur.next = list1;
        }
        if (list2 != null) {
            cur.next = list2;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        P21MergeTwoSortedLists solver = new P21MergeTwoSortedLists();

        run(solver, of(1, 2, 4), of(1, 3, 4), "官方例 → 1→1→2→3→4→4");
        run(solver, of(), of(), "双空 → 空");
        run(solver, of(), of(0), "空+单 → [0]");
        run(solver, of(1, 2, 4), of(1, 3, 4, 5, 6), "长度不等 ★ → 1→1→2→3→4→4→5→6");
        run(solver, of(-9, 3), of(5, 7), "负数 → -9→3→5→7");
        run(solver, of(2, 2, 2), of(2, 2), "全等值 → 2→2→2→2→2 ★ 取边稳定性");
        run(solver, of(1, 3, 5), of(2, 4), "穿插归并 → 1→2→3→4→5");
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

    /** 独立暴力参考：全值排序重建——机制与归并不同。 */
    private static List<Integer> bruteForce(ListNode a, ListNode b) {
        List<Integer> all = toList(a);
        all.addAll(toList(b));
        all.sort(java.util.Comparator.naturalOrder());
        return all;
    }

    private static void run(P21MergeTwoSortedLists solver, ListNode a, ListNode b, String label) {
        try {
            List<Integer> want = bruteForce(a, b);   // 参考先算（归并会改原链）
            ListNode merged = solver.mergeTwoLists(a, b);
            List<Integer> got = toList(merged);
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-28s | 参考=%s%n", ok ? "PASS" : "FAIL", label, want);
            if (!ok) System.out.printf("       实得=%s%n", got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s | 异常: %s%n", label, e);
        }
    }
}
