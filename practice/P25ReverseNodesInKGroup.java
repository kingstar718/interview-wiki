/**
 * LeetCode 25: K 个一组翻转链表 (Reverse Nodes in k-Group)
 * https://leetcode.cn/problems/reverse-nodes-in-k-group/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表 Hard 压轴·字节最爱）
 *
 * <p>■ 题目描述
 *   给链表头节点 head，每 **k 个节点一组**进行翻转，返回修改后的链表。
 *   - k 是正整数，且 ≤ 链表长度
 *   - 节点数不是 k 的倍数：最后剩余的节点（不足 k 个）**保持原有顺序**
 *   - 只能改指针（不得只改节点值）
 *
 * <p>■ 输入输出（约束）
 *   输入：ListNode head（节点数 [1, 5000]；0 ≤ val ≤ 1000）；int k（1 ≤ k ≤ 5000）
 *   输出：ListNode 新头
 *
 * <p>■ 示例逐条
 * <pre>
 * [1,2,3,4,5], k=2 → [2,1,4,3,5]（末尾 5 不足一组保持）
 * [1,2,3,4,5], k=3 → [3,2,1,4,5]（末尾 [4,5] 不足一组保持）
 * [1], k=1 → [1]（k=1 等价不翻）
 * [1,2], k=2 → [2,1]（整除正好翻完）
 * [1,2,3,4], k=4 → [4,3,2,1]
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Hard（页频 ★★★★★·字节/阿里）
 * <p>【标签】递归 · 链表
 * <p>【题型】分组处理（206 反转链表的分段版）：
 * <p>　　每组先探够 k 个才翻（不够保持）；翻完接回主线，递归下一组
 * <p>【考点】"探一组"（提前看 k 个再决定翻不翻）；反转与拼接的接缝四指针
 * <p>　　　　（组前驱 prevTail、组头 newHead、组尾 oldTail、组后继 nextHead）；
 * <p>　　　　k=1/末尾不足两组的边界
 * <p>【关联】P206（整链反转——本题主料）；P24（两两交换=本题 k=2 的特例版）；P92（区间反转）
 *
 * <p>LCS 签名：reverseKGroup(ListNode head, int k)。
 */
import java.util.ArrayList;
import java.util.List;

public class P25ReverseNodesInKGroup {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** 你的实现（探资格→区间反转→递归接缝：206 主料+一物三用的组后继 p）。 */
    public ListNode reverseKGroup(ListNode head, int k) {
        ListNode p = head;
        for (int i = 0; i < k; i++) {
            if (p == null) {
                return head;
            }
            p = p.next;
        }

        ListNode newHead = reverse(head, p);

        head.next = reverseKGroup(p, k);

        return newHead;
    }

    private ListNode reverse(ListNode start, ListNode end) {
        ListNode prev = null, cur = start;
        while (cur != end) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    public static void main(String[] args) {
        P25ReverseNodesInKGroup solver = new P25ReverseNodesInKGroup();

        run(solver, of(1, 2, 3, 4, 5), 2, "官方例1 k=2 → [2,1,4,3,5]");
        run(solver, of(1, 2, 3, 4, 5), 3, "官方例2 k=3 → [3,2,1,4,5]");
        run(solver, of(1), 1, "单节点 k=1 → [1]");
        run(solver, of(1, 2), 2, "正好一组 k=2 → [2,1]");
        run(solver, of(1, 2, 3, 4), 4, "整链一组 k=4 → [4,3,2,1]");
        run(solver, of(1, 2, 3, 4, 5, 6, 7), 3, "7 个 k=3 → [3,2,1,6,5,4,7]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static ListNode of(int... vals) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    /** 独立暴力参考：转数组按组分块反转再串回（机制独立）。 */
    private static ListNode bruteForce(ListNode head, int k) {
        List<Integer> vals = new java.util.ArrayList<>();
        for (ListNode p = head; p != null; p = p.next) vals.add(p.val);
        int n = vals.size();
        for (int i = 0; i + k <= n; i += k) {
            for (int l = i, r = i + k - 1; l < r; l++, r--) {
                int t = vals.get(l); vals.set(l, vals.get(r)); vals.set(r, t);
            }
        }
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    private static String show(ListNode n) {
        StringBuilder sb = new StringBuilder("[");
        for (; n != null; n = n.next) sb.append(n.val).append(n.next != null ? "," : "");
        return sb.append("]").toString();
    }

    private static void run(P25ReverseNodesInKGroup solver, ListNode head, int k, String label) {
        try {
            ListNode want = bruteForce(head, k);      // 参考先算（原地实现不污染）
            ListNode got = solver.reverseKGroup(head, k);
            boolean ok = show(want).equals(show(got));

            System.out.printf("[%s] %-24s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, show(want), show(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s | 异常: %s%n", label, e);
        }
    }
}
