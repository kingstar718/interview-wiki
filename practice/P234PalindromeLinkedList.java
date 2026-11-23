/**
 * LeetCode 234: 回文链表 (Palindrome Linked List)
 * https://leetcode.cn/problems/palindrome-linked-list/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表组 1/7——快慢中点+原地反转）
 *
 * <p>■ 题目描述
 *   给单链表头节点，判断是否**回文**（正读反读相同）。
 *   进阶要求：O(n) 时间 + **O(1) 额外空间**（不许开数组/集合复制）。
 *
 * <p>■ 输入输出（约束）
 *   输入：ListNode head（0 ≤ 节点数 ≤ 10^5；0 ≤ val ≤ 9）
 *   输出：boolean
 *   约定：本题链表节点直接用静态内部类定义（提交 LeetCode 时只交方法与依赖）。
 *
 * <p>■ 示例逐条
 * <pre>
 * 1→2→2→1 → true
 * 1→2 → false
 * []（空表）→ true（约定空表回文）
 * [1] → true（单节点）
 * 1→2→3→2→1 → true（奇数长：中点自比）
 * 1→0→1 → true
 * 1→2→3 → false
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★·字节/腾讯/美团）
 * <p>【标签】链表 · 双指针 · 递归
 * <p>【题型】O(1) 空间两件套：**快慢指针找中点** + **原地反转半段** + 逐值比较
 * <p>【考点】找中点的落点语义（slow 停哪边——决定前半/后半怎么切）；
 * <p>　　　　反转半段的接线；比较循环的终止条件（奇偶长度都成立）；
 * <p>　　　　（可选：比较完把反转段复原——工程加分项）
 * <p>【关联】P876 链表中点（快慢原型）；P206 反转链表（反转半段直接复用）；P21/2/138/148/146（链表组后续）
 *
 * <p>LCS 签名：isPalindrome(ListNode head)。
 */
public class P234PalindromeLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** 你的实现（走法 B：fast 条件宽松版，slow 停右半首——反转 slow 起的后半段）。 */
    public boolean isPalindrome(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode second = reverse(slow);

        ListNode p = head, q = second;
        while (q != null) {
            if (p.val != q.val) {
                return false;
            }
            p = p.next;
            q = q.next;
        }
        return true;
    }

    private ListNode reverse(ListNode head) {
        ListNode pre = null, cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }

    public static void main(String[] args) {
        P234PalindromeLinkedList solver = new P234PalindromeLinkedList();

        run(solver, of(1, 2, 2, 1), "1→2→2→1 → true");
        run(solver, of(1, 2), "1→2 → false");
        run(solver, of(), "空表 → true");
        run(solver, of(1), "单节点 → true");
        run(solver, of(1, 2, 3, 2, 1), "奇数长回文 → true");
        run(solver, of(1, 0, 1), "含0回文 → true");
        run(solver, of(1, 2, 3), "非回文 → false");
        run(solver, of(1, 2, 2, 2, 1), "奇数长五节点回文 → true");
        run(solver, of(1, 2, 3, 1), "似是而非 → false ★");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static ListNode of(int... vals) {
        ListNode dummy = new ListNode(0), cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    /** 独立暴力参考：复制到数组双指针比较——机制与原地两件套完全不同。 */
    private static boolean bruteForce(ListNode head) {
        java.util.List<Integer> a = new java.util.ArrayList<>();
        for (ListNode c = head; c != null; c = c.next) a.add(c.val);
        int l = 0, r = a.size() - 1;
        while (l < r) {
            if (!a.get(l++).equals(a.get(r--))) return false;
        }
        return true;
    }

    private static void run(P234PalindromeLinkedList solver, ListNode head, String label) {
        try {
            boolean want = bruteForce(head);   // 参考先算——原地反转实现会改坏链
            boolean got = solver.isPalindrome(head);
            boolean ok = got == want;

            System.out.printf("[%s] %-24s | 参考=%b 实得=%b%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s | 异常: %s%n", label, e);
        }
    }
}
