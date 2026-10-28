import java.util.Arrays;

/**
 * LeetCode 160: 相交链表 (Intersection of Two Linked Lists)
 * https://leetcode.cn/problems/intersection-of-two-linked-lists/
 *
 * <p>【难度】Easy
 * <p>【标签】链表(Linked List) · 双指针(Two Pointers) · 路径拼接
 * <p>【题型】pA 走完 A 接着走 B，pB 走完 B 接着走 A，总路程都是 |A|+|B|，相交则会在交点相遇
 * <p>【考点】能否不先算长度差、不额外空间，用「换轨」抹平长度差；引用相等而非值相等
 * <p>【关联】P142 环形链表II —— 双指针相遇家族；a+c+b = b+c+a 的等式思维
 *
 * <p>给你两个单链表的头节点 headA 和 headB，请你找出并返回两个单链表相交的起始节点。
 * 如果两个链表不存在相交节点，返回 null。要求 O(1) 额外空间。
 * 题目数据保证整个链式结构中不存在环。
 */
public class P160IntersectionOfTwoLinkedLists {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if (headA == null || headB == null) {
            return null;
        }
        ListNode pA = headA, pB = headB;
        while (pA != pB) {
            if (pA == null) {
                pA = headB;
            } else {
                pA = pA.next;
            }
            if (pB == null) {
                pB = headA;
            } else {
                pB = pB.next;
            }
        }
        return pA;
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

    /** 把 tail 接到 head 末尾（共享同一段节点，模拟相交） */
    private static ListNode append(ListNode head, ListNode tail) {
        if (head == null) return tail;
        ListNode cur = head;
        while (cur.next != null) cur = cur.next;
        cur.next = tail;
        return head;
    }

    /** 构造相交的一对链表：A = aPre + shared，B = bPre + shared（shared 为空则不相交） */
    private static ListNode[] pair(int[] aPre, int[] bPre, int[] shared) {
        ListNode sharedHead = list(shared);
        return new ListNode[]{append(list(aPre), sharedHead), append(list(bPre), sharedHead)};
    }

    public static void main(String[] args) {
        P160IntersectionOfTwoLinkedLists s = new P160IntersectionOfTwoLinkedLists();

        // 示例：A=4,1,8,4,5  B=5,6,1,8,4,5  交点 8
        run(s, pair(new int[]{4, 1}, new int[]{5, 6, 1}, new int[]{8, 4, 5}), 8);
        // 不相交
        run(s, pair(new int[]{1, 2, 3}, new int[]{4, 5, 6}, new int[]{}), null);
        // 完全重合（从头就相交）
        run(s, pair(new int[]{}, new int[]{}, new int[]{1, 2, 3}), 1);
        // A 为空
        run(s, pair(new int[]{}, new int[]{1, 2}, new int[]{}), null);
        // 只在尾节点相交
        run(s, pair(new int[]{1, 2}, new int[]{3, 4}, new int[]{5}), 5);
        // 长度差很大
        run(s, pair(new int[]{1}, new int[]{1, 2, 3, 4}, new int[]{9}), 9);
        // B 是 A 的后缀
        run(s, pair(new int[]{1, 2}, new int[]{}, new int[]{3, 4}), 3);
    }

    private static void run(P160IntersectionOfTwoLinkedLists s, ListNode[] pair, Integer expected) {
        String label = "A/B 相交预期=" + expected;
        try {
            ListNode gotNode = s.getIntersectionNode(pair[0], pair[1]);
            Integer got = gotNode == null ? null : gotNode.val;
            boolean pass = got == null ? expected == null : got.equals(expected);
            System.out.println((pass ? "[PASS] " : "[FAIL] ") + label + " | got=" + got);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}