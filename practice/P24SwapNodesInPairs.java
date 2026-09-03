import java.util.Arrays;

/**
 * LeetCode 24: 两两交换链表结点 (Swap Nodes in Pairs)
 * https://leetcode.cn/problems/swap-nodes-in-pairs/
 *
 * <p>【难度】Medium
 * <p>【标签】链表(Linked List) · 虚拟头节点(Dummy Node) · 多指针改写
 * <p>【题型】dummy + prev 指向「待交换一对」的前一个；每次取出 first/second，改三条指针完成交换
 * <p>【考点】能否用 dummy 统一处理；指针赋值顺序（prev.next = second 必须晚于 second.next = first）
 * <p>【关联】P206ReverseLinkedList —— 指针改写基本功；P25 K个一组翻转 —— 本题是 k=2 特例
 *
 * <p>给你一个链表，两两交换其中相邻的节点，并返回交换后链表的头节点。
 * 你必须在不修改节点内部的值的情况下完成本题（即，只能进行节点交换）。
 * 约束：0 &lt;= 节点数 &lt;= 100；0 &lt;= Node.val &lt;= 100（力扣原题；本地测试兼容负数）。
 */
public class P24SwapNodesInPairs {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(-1, head);
        ListNode prev = dummy;
        while (prev.next != null && prev.next.next != null) {
            ListNode first = prev.next;
            ListNode second = first.next;

            first.next = second.next;
            second.next = first;
            prev.next = second;

            prev = first;
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
        P24SwapNodesInPairs s = new P24SwapNodesInPairs();

        run(s, list(1, 2, 3, 4), new int[]{2, 1, 4, 3});    // 示例1：偶数长度
        run(s, list(), new int[]{});                         // 示例2：空链表
        run(s, list(1), new int[]{1});                       // 示例3：单节点
        run(s, list(1, 2, 3), new int[]{2, 1, 3});           // 边界：奇数长度，末节点不交换
        run(s, list(1, 2), new int[]{2, 1});                 // 边界：最小偶数对
        run(s, list(1, 2, 3, 4, 5), new int[]{2, 1, 4, 3, 5}); // 边界：奇数长度更长的
        run(s, list(-1, 0, 1), new int[]{0, -1, 1});         // 边界：含负值（本地扩展）
        run(s, list(1, 2, 3, 4, 5, 6), new int[]{2, 1, 4, 3, 6, 5}); // 边界：三对
    }

    private static void run(P24SwapNodesInPairs s, ListNode head, int[] expected) {
        String label = "head=" + Arrays.toString(arr(head));
        try {
            int[] got = arr(s.swapPairs(head));
            System.out.println((Arrays.equals(got, expected) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(got) + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}