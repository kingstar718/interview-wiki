import java.util.Arrays;

/**
 * LeetCode 19: 删除链表的倒数第 N 个结点 (Remove Nth Node From End of List)
 * https://leetcode.cn/problems/remove-nth-node-from-end-of-list/
 *
 * <p>【难度】Medium
 * <p>【标签】链表(Linked List) · 快慢双指针(Two Pointers) · 虚拟头节点(Dummy Node)
 * <p>【题型】快指针先走 n 步，再与慢指针同步走；快指针到 null 时慢指针停在待删节点前一个
 * <p>【考点】能否不用「先数总长再定位」（两趟遍历），一趟搞定；dummy 统一删头边界
 * <p>【关联】P24SwapNodesInPairs —— dummy 同款；876 链表的中间结点 —— 双指针定位对照
 *
 * <p>给你一个链表，删除链表的倒数第 n 个结点，并且返回链表的头结点。
 * 约束：1 &lt;= n &lt;= 链表长度（n 保证合法）。
 */
public class P19RemoveNthNodeFromEndOfList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode slow = dummy;
        ListNode fast = dummy;
        for (int i = 0; i < n; i++) {
            fast = fast.next;
        }
        while (fast.next != null) {
            slow = slow.next;
            fast = fast.next;
        }
        slow.next = slow.next.next;
        return dummy.next;
    }

    /** 数组 → 链表 */
    private static ListNode list(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) {
            cur.next = new ListNode(v);
            cur = cur.next;
        }
        return dummy.next;
    }

    /** 链表 → 数组 */
    private static int[] arr(ListNode head) {
        java.util.List<Integer> out = new java.util.ArrayList<>();
        while (head != null) {
            out.add(head.val);
            head = head.next;
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void main(String[] args) {
        P19RemoveNthNodeFromEndOfList s = new P19RemoveNthNodeFromEndOfList();

        run(s, list(1, 2, 3, 4, 5), 2, new int[]{1, 2, 3, 5});  // 示例1：删倒数第 2
        run(s, list(1), 1, new int[]{});                         // 示例2：单节点删自己
        run(s, list(1, 2), 1, new int[]{1});                     // 示例3：删尾
        run(s, list(1, 2), 2, new int[]{2});                     // 边界：删头（n == 长度）
        run(s, list(1, 2, 3), 3, new int[]{2, 3});               // 边界：删头
        run(s, list(1, 2, 3), 1, new int[]{1, 2});               // 边界：删尾
        run(s, list(1, 2, 3, 4), 2, new int[]{1, 2, 4});         // 边界：中间节点
        run(s, list(0, -1, 2, 3), 3, new int[]{0, 2, 3});        // 边界：含负值（本地扩展）
    }

    private static void run(P19RemoveNthNodeFromEndOfList s, ListNode head, int n, int[] expected) {
        String label = "n=" + n + " head=" + Arrays.toString(arr(head));
        try {
            int[] got = arr(s.removeNthFromEnd(head, n));
            System.out.println((Arrays.equals(got, expected) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(got) + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}