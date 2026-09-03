import java.util.Arrays;

/**
 * LeetCode 206: 反转链表 (Reverse Linked List)
 * https://leetcode.cn/problems/reverse-linked-list/
 *
 * <p>【难度】Easy
 * <p>【标签】链表(Linked List) · 三指针反转(Two/Three Pointers) · 递归(Recursion)
 * <p>【题型】迭代三指针 pre/cur/next；**全场最高频手撕题**，迭代和递归两版都要能闭眼写
 * <p>【考点】「先备份再修改」：改 head.next 前必须先用 next 保存原后继；返回 prev（新头）
 * <p>【关联】P203RemoveLinkedListElements —— 链表题起点；92 反转链表II / 25 K个一组翻转 —— 反转族进阶
 *
 * <p>给你单链表的头节点 head，请你反转链表，并返回反转后的链表。
 * 约束：0 &lt;= 节点数 &lt;= 5000；-5000 &lt;= Node.val &lt;= 5000。
 */
public class P206ReverseLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode reverseList(ListNode head) {
        ListNode cur = head;
        ListNode pre = null;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }

    /** 递归版（白板手撕也要求）：先反转后面，再把 head 接到段尾 */
    public ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHead = reverseListRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
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
        P206ReverseLinkedList s = new P206ReverseLinkedList();

        run(s, list(1, 2, 3, 4, 5), new int[]{5, 4, 3, 2, 1});        // 示例1：标准反转
        run(s, list(), new int[]{});                                   // 示例2：空链表
        run(s, list(1), new int[]{1});                                 // 示例3：单节点
        run(s, list(1, 2), new int[]{2, 1});                           // 边界：两个节点
        run(s, list(0, -1, 2), new int[]{2, -1, 0});                   // 边界：含负数
        run(s, list(1, 2, 3), new int[]{3, 2, 1});                     // 边界：三个节点

        // —— 递归版（reverseListRecursive）同一组用例 ——
        System.out.println("--- 递归版 reverseListRecursive ---");
        runR(s, list(1, 2, 3, 4, 5), new int[]{5, 4, 3, 2, 1});
        runR(s, list(), new int[]{});
        runR(s, list(1), new int[]{1});
        runR(s, list(1, 2), new int[]{2, 1});
        runR(s, list(0, -1, 2), new int[]{2, -1, 0});
        runR(s, list(1, 2, 3), new int[]{3, 2, 1});
    }

    private static void runR(P206ReverseLinkedList s, ListNode head, int[] expected) {
        String label = "head=" + Arrays.toString(arr(head));
        try {
            int[] got = arr(s.reverseListRecursive(head));
            System.out.println((Arrays.equals(got, expected) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(got) + " expected=" + Arrays.toString(expected));
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }

    private static void run(P206ReverseLinkedList s, ListNode head, int[] expected) {
        String label = "head=" + Arrays.toString(arr(head));
        try {
            int[] got = arr(s.reverseList(head));
            System.out.println((Arrays.equals(got, expected) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(got) + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}