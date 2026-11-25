import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 22: 括号生成 (Generate Parentheses)
 * https://leetcode.cn/problems/generate-parentheses/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·回溯家族收官★★★★★）
 *
 * <p>■ 题目描述
 *   数字 n 代表生成括号的对数，请生成**所有**可能的、
 *   【有效的】括号组合。（有效=任意前缀里 '(' 数 ≥ ')' 数，且恰好用完 n 对）
 *
 * <p>■ 输入输出（约束）
 *   输入：int n（1 ≤ n ≤ 8）
 *   输出：List&lt;String&gt;（所有组合，顺序不限）
 *
 * <p>■ 示例逐条
 * <pre>
 * n=1 → ["()"]
 * n=2 → ["(())","()()"]
 * n=3 → ["((()))","(()())","(())()","()(())","()()()"]  共 5 个
 * （结果顺序不限；n=3 的 5 个是 Catalan 数 C(3)=5）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★·字节/腾讯/阿里/微软）
 * <p>【标签】字符串 · 动态规划 · 回溯
 * <p>【题型】回溯 + 剪枝（46/47/90 三连后的第 4 题，但不再有 used/排序去重）：
 * <p>　　两个计数器做剪枝条件：open ≤ n、close < open（或 close < n 且只差一式）
 * <p>　　出口=长度 2n；收账无条件在出口处
 * <p>【考点】"有效"到计数器条件的翻译（前缀合法性=close 永不超过 open）；
 * <p>　　　　每次分支恰好两个选择（加'('或加')'），剪枝砍掉非法支；
 * <p>　　　　与 46 家族的对照：这题不用撤销 String（不可变对象天然快照）
 * <p>【关联】P46/47/90（回溯三连——撤销/收账/止损三分法）；P32 最长有效括号（子串版另一题）；
 * <p>　　　　P39/40/77/216（组合型回溯）
 *
 * <p>LCS 签名：generateParenthesis(int n)。
 */
public class P22GenerateParentheses {

    /** 你的实现（两计数器剪枝回溯：open<n 走 '(' 支、close<open 走 ')' 支）。 */
    public List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        dfs(n, 0, 0, new StringBuilder(), res);
        return res;
    }

    private void dfs(int n, int open, int close, StringBuilder path, List<String> res) {
        if (path.length() == 2 * n) {
            res.add(path.toString());
            return;
        }

        if (open < n) {
            path.append('(');
            dfs(n, open + 1, close, path, res);
            path.deleteCharAt(path.length() - 1);
        }

        if (close < open) {
            path.append(')');
            dfs(n, open, close + 1, path, res);
            path.deleteCharAt(path.length() - 1);
        }

    }

    public static void main(String[] args) {
        P22GenerateParentheses solver = new P22GenerateParentheses();

        run(solver, 1, "n=1 → 1 个");
        run(solver, 2, "n=2 → 2 个");
        run(solver, 3, "n=3 → 5 个");
        run(solver, 4, "n=4 → 14 个");
        run(solver, 0, "n=0 → 1 个（空串）");
        run(solver, 5, "n=5 → 42 个");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举所有 2^(2n) 个括号串→过滤"有效"（栈判定）。 */
    private static List<String> bruteForce(int n) {
        List<String> out = new ArrayList<>();
        int len = 2 * n;
        for (int mask = 0; mask < (1 << len); mask++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append((mask >> i & 1) == 1 ? '(' : ')');
            if (valid(sb.toString())) out.add(sb.toString());
        }
        return out;
    }

    /** 栈判定：前缀 close 永不超 open，且整体恰好用完。 */
    private static boolean valid(String s) {
        int bal = 0;
        for (char c : s.toCharArray()) {
            bal += c == '(' ? 1 : -1;
            if (bal < 0) return false;
        }
        return bal == 0;
    }

    private static void run(P22GenerateParentheses solver, int n, String label) {
        try {
            List<String> want = bruteForce(n);
            List<String> got = solver.generateParenthesis(n);
            boolean ok = got.size() == want.size()
                    && got.containsAll(want) && want.containsAll(got);

            System.out.printf("[%s] %-12s | 参考=%d 个 实得=%d 个%n",
                    ok ? "PASS" : "FAIL", label, want.size(), got.size());
            if (!ok) System.out.printf("    参考=%s%n    实得=%s%n", want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-12s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-12s | 异常: %s%n", label, e);
        }
    }
}
