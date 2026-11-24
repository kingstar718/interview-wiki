import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 2: 两数相加 (Add Two Numbers)
 * https://leetcode.cn/problems/add-two-numbers/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·链表组 4/7——进位缝合）
 *
 * <p>■ 题目描述
 *   两个非空链表表示两个非负整数，每位**逆序**存储（个位在头）。
 *   将两数相加，同样以逆序链表返回。
 *
 * <p>■ 输入输出（约束）
 *   输入：ListNode l1, l2（1 ≤ 节点数 ≤ 100；0 ≤ val ≤ 9；无前导零——除非数本身是 0）
 *   输出：ListNode
 *
 * <p>■ 示例逐条
 * <pre>
 * 2→4→3 + 5→6→4 → 7→0→8（342+465=807 ★ 中间进位）
 * 0 + 0 → 0
 * 9→9→9→9→9→9→9 + 9→9→9→9 → 8→9→9→9→0→0→0→1（★ 长度不等+末位进位爆长）
 * 1 + 9→9 → 0→0→1（进位连爆两轮）
 * 5 + 5 → 0→1（单进位）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·全厂）
 * <p>【标签】链表 · 数学 · 模拟
 * <p>【题型】**逐位模拟加法**：sum = a + b + carry，本位 sum%10，进位 carry=sum/10
 * <p>【考点】循环条件三合一（l1!=null || l2!=null || carry!=0——【carry!=0 不能漏】，
 * <p>　　　　否则末位进位丢——9999999+9999 哨兵专杀）；空链补 0 参与（不 null 访问）；
 * <p>　　　　新链必须 new 节点（本题与 21 的"拼接原节点"相反）
 * <p>【关联】P21 合并链表（dummy+cur 缝合同族）；P67 二进制求和（同进位模型字符串版）
 *
 * <p>LCS 签名：addTwoNumbers(ListNode l1, ListNode l2)。
 */
public class P2AddTwoNumbers {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** 你的实现（逐位模拟加法：三合一循环条件 + 空链补0 + carry 传递，新节点顺手落尾）。 */
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode cur = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            carry = sum / 10;
            cur.next = new ListNode(sum % 10);
            cur = cur.next;
        }
        return dummy.next;
    }

    public static void main(String[] args) {
        P2AddTwoNumbers solver = new P2AddTwoNumbers();

        run(solver, of(2, 4, 3), of(5, 6, 4), "官方例 → 7→0→8");
        run(solver, of(0), of(0), "0+0 → 0");
        run(solver, of(9, 9, 9, 9, 9, 9, 9), of(9, 9, 9, 9), "长度不等+末位进位爆长 ★ → 8→9→9→9→0→0→0→1");
        run(solver, of(1), of(9, 9), "进位连爆两轮 → 0→0→1");
        run(solver, of(5), of(5), "单进位 → 0→1");
        run(solver, of(9, 9, 9), of(1), "尾进位一轮 → 0→0→0→1");
        run(solver, of(7, 2, 4, 3), of(5, 6, 4), "长不等四加三(3427+465=3892) → 2→9→8→3");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static ListNode of(int... vals) {
        ListNode dummy = new ListNode(0), cur = dummy;
        for (int v : vals) { cur.next = new ListNode(v); cur = cur.next; }
        return dummy.next;
    }

    private static List<Integer> toList(ListNode head) {
        List<Integer> a = new ArrayList<>();
        for (ListNode c = head; c != null; c = c.next) a.add(c.val);
        return a;
    }

    /** 独立暴力参考：解析成大整数相加再逆序重建（用 long 溢出风险→用字符串加法，机制完全不同）。 */
    private static List<Integer> bruteForce(ListNode a, ListNode b) {
        String x = str(a), y = str(b);   // 逆序链 → 反转即真实数字字符串
        StringBuilder s1 = new StringBuilder(x).reverse();
        StringBuilder s2 = new StringBuilder(y).reverse();
        String sum = addStr(s1.toString(), s2.toString());
        List<Integer> out = new ArrayList<>();
        for (int i = sum.length() - 1; i >= 0; i--) out.add(sum.charAt(i) - '0');
        return out;
    }

    private static String str(ListNode h) {
        StringBuilder sb = new StringBuilder();
        for (ListNode c = h; c != null; c = c.next) sb.append(c.val);
        return sb.toString();
    }

    /** 大数加法（字符串版）。 */
    private static String addStr(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, carry = 0;
        while (i >= 0 || j >= 0 || carry != 0) {
            int s = carry;
            if (i >= 0) s += a.charAt(i--) - '0';
            if (j >= 0) s += b.charAt(j--) - '0';
            sb.append(s % 10);
            carry = s / 10;
        }
        return sb.reverse().toString();
    }

    private static void run(P2AddTwoNumbers solver, ListNode a, ListNode b, String label) {
        try {
            List<Integer> want = bruteForce(a, b);
            List<Integer> got = toList(solver.addTwoNumbers(a, b));
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-34s | 参考=%s%n", ok ? "PASS" : "FAIL", label, want);
            if (!ok) System.out.printf("       实得=%s%n", got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s | 异常: %s%n", label, e);
        }
    }
}
