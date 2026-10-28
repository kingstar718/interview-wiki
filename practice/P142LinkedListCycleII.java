import java.util.Arrays;

/**
 * LeetCode 142: 环形链表 II (Linked List Cycle II)
 * https://leetcode.cn/problems/linked-list-cycle-ii/
 *
 * <p>【难度】Medium
 * <p>【标签】链表(Linked List) · 快慢指针(Two Pointers) · Floyd 判圈
 * <p>【题型】快慢指针判相遇；再「头」与「相遇点」同步走一步，第二次相遇点即环入口
 * <p>【考点】能否推导 a = (n-1)(b+c) + c（面试会让证明）；第二阶段两指针都走一步
 * <p>【关联】P141 判环（本题简化版）；P287 寻找重复数 —— 数组版找环入口
 *
 * <p>给定一个链表的头节点 head，返回链表开始入环的第一个节点。如果链表无环，则返回 null。
 * 不允许修改给定的链表。
 */
public class P142LinkedListCycleII {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode detectCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                ListNode ptr = head;
                while (ptr != slow) {
                    ptr = ptr.next;
                    slow = slow.next;
                }
                return ptr;
            }
        }
        return null;
    }

    /**
     * 构造链表；cyclePos >= 0 时把尾节点的 next 指向下标 cyclePos 的节点（造环）；
     * cyclePos == -1 表示无环。
     */
    private static ListNode list(int[] vals, int cyclePos) {
        if (vals.length == 0) return null;
        ListNode[] nodes = new ListNode[vals.length];
        for (int i = 0; i < vals.length; i++) {
            nodes[i] = new ListNode(vals[i]);
        }
        for (int i = 0; i < vals.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        if (cyclePos >= 0) {
            nodes[vals.length - 1].next = nodes[cyclePos];
        }
        return nodes[0];
    }

    public static void main(String[] args) {
        P142LinkedListCycleII s = new P142LinkedListCycleII();

        run(s, list(new int[]{3, 2, 0, -4}, 1), 2);   // 示例1：环入口是值为 2 的节点
        run(s, list(new int[]{1, 2}, 0), 1);          // 示例2：环入口即头
        run(s, list(new int[]{1}, -1), null);         // 示例3：单节点无环
        run(s, list(new int[]{1, 2, 3}, -1), null);   // 边界：无环
        run(s, list(new int[]{1}, 0), 1);             // 边界：单节点自环
        run(s, list(new int[]{1, 2}, 1), 2);          // 边界：环入口在尾
        run(s, list(new int[]{1, 2, 3, 4}, 0), 1);    // 边界：整条链都是环（入口=头）
        run(s, list(new int[]{1, 2, 3, 4}, 2), 3);    // 边界：环入口在中间
    }

    private static void run(P142LinkedListCycleII s, ListNode head, Integer expected) {
        String label = "head=" + (head == null ? "[]" : "[" + head.val + ",...]");
        try {
            ListNode gotNode = s.detectCycle(head);
            Integer got = gotNode == null ? null : gotNode.val;
            boolean pass = got == null ? expected == null : got.equals(expected);
            System.out.println((pass ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " expected=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}