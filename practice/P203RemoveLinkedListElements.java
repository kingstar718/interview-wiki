import java.util.Arrays;

/**
 * LeetCode 203: 移除链表元素 (Remove Linked List Elements)
 * https://leetcode.cn/problems/remove-linked-list-elements/
 *
 * <p>【难度】Easy
 * <p>【标签】链表(Linked List) · 虚拟头节点(Dummy Node)
 * <p>【题型】dummy 头节点统一处理「删的是头节点」特判；删除后 cur 不移动（新 cur.next 可能也要删）
 * <p>【考点】能否想到用 dummy 把「头节点特判」消除；删除节点后指针的移动时机
 * <p>【关联】P19 删除链表的倒数第N个结点 —— 同样用 dummy（本 repo 尚未生成骨架）
 *
 * <p>给定链表的头节点 head 和一个整数 val，删除链表中所有值等于 val 的节点，返回新的头节点。
 * 约束：0 &lt;= 节点数 &lt;= 10^4；1 &lt;= Node.val, val &lt;= 50。
 */
public class P203RemoveLinkedListElements {

    /**
     * 单链表节点
     */
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode removeElements(ListNode head, int val) {
        ListNode dummy = new ListNode(-1);
        dummy.next = head;
        ListNode p1 = dummy;
        while (p1.next != null) {
            if (p1.next.val != val) {
                p1 = p1.next;
            } else {
                p1.next = p1.next.next;
            }
        }
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

    /** 链表 → 数组（遍历校验用；若有环会无限循环，本题无环） */
    private static int[] arr(ListNode head) {
        java.util.List<Integer> out = new java.util.ArrayList<>();
        while (head != null) {
            out.add(head.val);
            head = head.next;
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void main(String[] args) {
        P203RemoveLinkedListElements s = new P203RemoveLinkedListElements();

        run(s, list(1, 2, 6, 3, 4, 5, 6), 6, new int[]{1, 2, 3, 4, 5});  // 示例1：中间+末尾都删
        run(s, list(), 1, new int[]{});                                   // 示例2：空链表
        run(s, list(7, 7, 7, 7), 7, new int[]{});                         // 示例3：全删
        run(s, list(1, 2, 3), 9, new int[]{1, 2, 3});                     // 边界：一个都不删
        run(s, list(1), 1, new int[]{});                                  // 边界：单节点且要删
        run(s, list(2), 1, new int[]{2});                                 // 边界：单节点不删
        run(s, list(1, 2), 1, new int[]{2});                              // 边界：头节点要删
        run(s, list(2, 2, 1, 2, 2), 2, new int[]{1});                     // 边界：删连续头部+尾部
        run(s, list(1, 1, 2, 1, 1), 1, new int[]{2});                     // 边界：删完首尾只剩中间
    }

    private static void run(P203RemoveLinkedListElements s, ListNode head, int val, int[] expected) {
        String label = "val=" + val + " head=" + Arrays.toString(arr(head));
        try {
            int[] got = arr(s.removeElements(head, val));
            System.out.println((Arrays.equals(got, expected) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(got) + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}