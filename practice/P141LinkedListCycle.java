/**
 * LeetCode 141: 环形链表 (Linked List Cycle)
 * https://leetcode.cn/problems/linked-list-cycle/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表组 2/7——Floyd 判圈）
 *
 * <p>■ 题目描述
 *   给单链表头节点，判断链表中是否存在环（某节点的 next 指回链表中它之前的某个节点）。
 *   **不许修改链表**。
 *
 * <p>■ 输入输出（约束）
 *   输入：ListNode head（0 ≤ 节点数 ≤ 10^4；-10^5 ≤ val ≤ 10^5）
 *         测试用例用 pos 表示尾节点指回的【下标】（-1 = 无环；pos 仅供构造，不作为参数传入）
 *   输出：boolean
 *
 * <p>■ 示例逐条
 * <pre>
 * 3→2→0→-4→(回到下标2) → true（官方例：尾指回 idx1）
 * 1→2→(回到下标0) → true（两节点成环）
 * [1] 无环 → false
 * [] 空表 → false
 * 1→2→3 无环 → false
 * 大环（长10^4 自环绕尾）→ true（性能哨兵）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★★·全厂）
 * <p>【标签】链表 · 双指针
 * <p>【题型】**Floyd 判圈**（快慢指针：slow 走1步、fast 走2步；有环必相遇、无环 fast 先出界）
 * <p>【考点】为什么有环【必】相遇（相对速度1步/轮——不会跳过）；循环条件的出界写法；
 * <p>　　　　判相遇用**引用相等**（slow==fast）不是值相等（142 一阶段已记：值可能重复不可靠）
 * <p>【关联】P142 环形链表II（一阶段已过：找入环点——Floyd 第二阶段推导）；P234（快慢找中点）
 *
 * <p>LCS 签名：hasCycle(ListNode head)。
 */
public class P141LinkedListCycle {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** 你的实现（Floyd 判圈：slow==fast 引用相等判相遇——142 错题本条目已内化）。 */
    public boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        P141LinkedListCycle solver = new P141LinkedListCycle();

        run(solver, cycleOf(3, 2, 0, -4, 1), "官方例：尾指回 idx1 → true");
        run(solver, cycleOf(1, 2, 0), "两节点成环 → true");
        run(solver, linearOf(1), "单节点无环 → false");
        run(solver, linearOf(), "空表 → false");
        run(solver, linearOf(1, 2, 3), "无环 → false");
        run(solver, cycleOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0), "大环 → true ★");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 无环链表。 */
    private static ListNode linearOf(int... vals) {
        ListNode dummy = new ListNode(0), cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    /** 带 pos 环的链表（pos=-1 无环；pos=环入口下标）。 */
    private static ListNode cycleOf(int[] vals, int pos) {
        ListNode dummy = new ListNode(0), cur = dummy, entry = null;
        for (int i = 0; i < vals.length; i++) {
            cur.next = new ListNode(vals[i]);
            cur = cur.next;
            if (i == pos) entry = cur;
        }
        if (pos >= 0) cur.next = entry;
        return dummy.next;
    }

    private static ListNode cycleOf(int pos, int... vals) {
        return cycleOf(vals, pos);
    }

    /** 独立暴力参考：沿 next 标记访问集——机制与 Floyd 不同（O(n) 空间）。 */
    private static boolean bruteForce(ListNode head) {
        java.util.Set<ListNode> seen = new java.util.HashSet<>();
        for (ListNode c = head; c != null; c = c.next) {
            if (!seen.add(c)) return true;
        }
        return false;
    }

    private static void run(P141LinkedListCycle solver, ListNode head, String label) {
        try {
            boolean want = bruteForce(head);   // 参考先算（234 教训：防原地改坏链）
            boolean got = solver.hasCycle(head);
            boolean ok = got == want;

            System.out.printf("[%s] %-26s | 参考=%b 实得=%b%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s | 异常: %s%n", label, e);
        }
    }
}
