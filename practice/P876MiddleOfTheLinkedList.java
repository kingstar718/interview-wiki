import java.util.Arrays;

/**
 * LeetCode 876: 链表的中间结点 (Middle of the Linked List)
 * https://leetcode.cn/problems/middle-of-the-linked-list/
 *
 * <p>【难度】Easy
 * <p>【标签】链表(Linked List) · 快慢指针(Two Pointers)
 * <p>【题型】快指针走 2 步、慢指针走 1 步；快指针到尾时慢指针在中点
 * <p>【考点】偶数长度时返回「第二个中间节点」的条件写法；循环条件两个判断都不能少
 * <p>【关联】P142 环形链表II（同族）；P148 排序链表（找中点断链）
 *
 * <p>给你单链表的头节点 head，请你找出并返回链表的中间结点。
 * 如果有两个中间结点，则返回第二个中间结点。
 */
public class P876MiddleOfTheLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode middleNode(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
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

    public static void main(String[] args) {
        P876MiddleOfTheLinkedList s = new P876MiddleOfTheLinkedList();

        run(s, list(1, 2, 3, 4, 5), 3);        // 示例1：奇数，正中间
        run(s, list(1, 2, 3, 4, 5, 6), 4);     // 示例2：偶数，返回第二个中间
        run(s, list(1), 1);                    // 边界：单节点
        run(s, list(1, 2), 2);                 // 边界：两个节点，返回第二个
        run(s, list(1, 2, 3), 2);              // 边界：三个节点
        run(s, list(1, 2, 3, 4), 3);           // 边界：四个节点
        run(s, list(1, 2, 3, 4, 5, 6, 7), 4);  // 边界：七个节点
        run(s, list(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 6); // 边界：十个节点，第二个中间
    }

    private static void run(P876MiddleOfTheLinkedList s, ListNode head, Integer expected) {
        String label = "head=" + Arrays.toString(arr(head));
        try {
            ListNode gotNode = s.middleNode(head);
            Integer got = gotNode == null ? null : gotNode.val;
            boolean pass = got == null ? expected == null : got.equals(expected);
            System.out.println((pass ? "[PASS] " : "[FAIL] ") + label + " | got=" + got + " expected=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }

    private static int[] arr(ListNode head) {
        java.util.List<Integer> out = new java.util.ArrayList<>();
        while (head != null) {
            out.add(head.val);
            head = head.next;
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }
}