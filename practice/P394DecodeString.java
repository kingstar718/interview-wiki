/**
 * LeetCode 394: 字符串解码 (Decode String)
 * https://leetcode.cn/problems/decode-string/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·栈与堆组 2/3——双栈嵌套展开）
 *
 * <p>■ 题目描述
 *   编码字符串形如 k[encoded_string]：方括号内重复 k 次。
 *   **支持嵌套**（如 3[a2[c]] → accaccacc）。返回解码字符串。
 *   输入保证合法（k 为正、括号匹配、无非法嵌套）。
 *
 * <p>■ 输入输出（约束）
 *   输入：String s（1 ≤ len ≤ 30；s 由数字/字母/[] 组成）
 *   输出：String
 *
 * <p>■ 示例逐条
 * <pre>
 * "3[a]2[bc]" → "aaabcbc"
 * "3[a2[c]]" → "accaccacc"（嵌套）
 * "2[abc]3[cd]ef" → "abcabccdcdcdef"（并列+尾部散字）
 * "abc3[de]fgh" → "abcdedefdefgh"（前散字）
 * "10[a]" → "aaaaaaaaaa"（多位数 k）
 * "2[3[a]b]" → "aaabaaab"（内层先展开再重复）
 * "2[2[b]]" → "bbbb"
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·腾讯/字节）
 * <p>【标签】栈 · 递归 · 字符串
 * <p>【题型】**双栈嵌套展开**：countStack 存重复次数、stringStack 存外层已拼串；
 * <p>　　　　遇 [ 压栈重置开新层，遇 ] 弹栈拼回外层
 * <p>【考点】**多位数 num 累积**（10[a]——num=num*10+digit）；[ 时"保存现场再开层"；
 * <p>　　　　] 时"内层结果拼回外层"（cur = 外层 + cur×count）；
 * <p>　　　　散字符直接 append cur（嵌套层级意识——在哪层就拼哪层的 cur）
 * <p>【关联】P155（辅助栈状态快照）；P20（栈匹配原型）；P224 括号计算器（同族递归下降）
 *
 * <p>LCS 签名：decodeString(String s)。
 */
import java.util.Deque;
import java.util.LinkedList;

public class P394DecodeString {

    /** 你的实现（双栈迭代——看了伪代码写出：'[ 保存现场/ ] 恢复现场，散字拼当前纸）。 */
    public String decodeString(String s) {
        Deque<Integer> countStack = new LinkedList<>();
        Deque<StringBuilder> stringStack = new LinkedList<>();
        StringBuilder cur = new StringBuilder();
        int num = 0;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (c == '[') {
                countStack.push(num);
                stringStack.push(cur);
                num = 0;
                cur = new StringBuilder();
            } else if (c == ']') {
                int count = countStack.pop();
                StringBuilder outer = stringStack.pop();
                while (count > 0) {
                    outer.append(cur);
                    count--;
                }
                cur = outer;
            } else if (c <= '9') {
                num = num * 10 + c - '0';
            } else {
                cur.append(c);
            }
        }
        return cur.toString();
    }
    /** 递归下降对照版（灵神原生主解）：pos 跨层共享（int[]），] 由调用方消费——对称性：谁开 [ 谁吃 ]。 */
    public String decodeStringRecursive(String s) {
        int[] pos = new int[1];
        return parse1(s, pos).toString();
    }

    private StringBuilder parse1(String s, int[] pos) {
        StringBuilder sb = new StringBuilder();
        while (pos[0] < s.length() && s.charAt(pos[0]) != ']') {
            char c = s.charAt(pos[0]);
            if (Character.isDigit(c)) {
                int num = 0;
                while (pos[0] < s.length() && Character.isDigit(s.charAt(pos[0]))) {
                    num = num * 10 + (s.charAt(pos[0]) - '0');
                    pos[0]++;
                }
                pos[0]++;
                StringBuilder inner = parse1(s, pos);
                pos[0]++;
                while (num > 0) {
                    sb.append(inner);
                    num--;
                }
            } else {
                sb.append(c);
                pos[0]++;
            }
        }
        return sb;
    }

    public static void main(String[] args) {
        P394DecodeString solver = new P394DecodeString();

        run(solver, "3[a]2[bc]", "并列 → aaabcbc");
        run(solver, "3[a2[c]]", "嵌套 → accaccacc ★");
        run(solver, "2[abc]3[cd]ef", "并列+尾散字 → abcabccdcdcdef");
        run(solver, "abc3[de]fgh", "前散字 → abcdedefdefgh");
        run(solver, "10[a]", "多位数 → aaaaaaaaaa ★");
        run(solver, "2[3[a]b]", "内层先展开 → aaabaaab");
        run(solver, "2[2[b]]", "纯嵌套 → bbbb");

        System.out.println("=== 递归下降对照版 ===");
        recRun("9[b] → bbbbbbbbb", "9[b]", "bbbbbbbbb");
        recRun("19[a] → 19 个 a", "19[a]", "a".repeat(19));
        recRun("3[a2[c]] → accaccacc", "3[a2[c]]", "accaccacc");
        recRun("2[abc]3[cd]ef", "2[abc]3[cd]ef", "abcabccdcdcdef");
        recRun("10[a]", "10[a]", "a".repeat(10));
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：递归下降解析（找到内层 [..] 递归展开）——机制与双栈迭代不同。 */
    private static String bruteForce(String s) {
        return parse(s, new int[]{0});
    }

    private static String parse(String s, int[] i) {
        StringBuilder sb = new StringBuilder();
        while (i[0] < s.length() && s.charAt(i[0]) != ']') {
            char c = s.charAt(i[0]);
            if (Character.isDigit(c)) {
                int num = 0;
                while (Character.isDigit(s.charAt(i[0]))) {
                    num = num * 10 + (s.charAt(i[0]++) - '0');
                }
                i[0]++;  // 跳过 [
                String inner = parse(s, i);
                i[0]++;  // 跳过 ]
                for (int k = 0; k < num; k++) sb.append(inner);
            } else {
                sb.append(c);
                i[0]++;
            }
        }
        return sb.toString();
    }

    /** 递归版对账（解法本身作主解对账，参考=bruteForce）。 */
    private static void recRun(String label, String input, String want) {
        try {
            String wantRef = bruteForce(input);   // 参考先算
            String got = new P394DecodeString().decodeStringRecursive(input);
            boolean ok = got.equals(want) && got.equals(wantRef);
            System.out.printf("[%s] %-26s | 参考=%s 实得=%s%n", ok ? "PASS" : "FAIL", label, wantRef, got);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s | 异常: %s%n", label, e);
        }
    }

    private static void run(P394DecodeString solver, String input, String label) {
        try {
            String want = bruteForce(input);   // 参考先算
            String got = solver.decodeString(input);
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-26s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s | 异常: %s%n", label, e);
        }
    }
}
