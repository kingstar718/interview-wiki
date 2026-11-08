import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 509: 斐波那契数 (Fibonacci Number)
 * https://leetcode.cn/problems/fibonacci-number/
 *
 * <p>【难度】Easy
 * <p>【标签】动态规划(DP) · 递归 · 记忆化
 * <p>【题型】DP 五部曲的最简载体：dp[i]=F(i)，转移抄题，双变量滚动空间 O(1)
 * <p>【考点】dp[0]=0/dp[1]=1 双地基；i 从 2 起；朴素递归 O(2^n) 与记忆化的对照（面试口答题）
 * <p>【关联】P70 爬楼梯（同式换皮，DP 第 2 题）；P53（滚动变量手法同源）
 *
 * <p>F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2)（n ≥ 2）。给 n（0 ≤ n ≤ 30），返回 F(n)。
 *
 * <p>示例：n=4 → 3（0,1,1,2,3）
 */
public class P509FibonacciNumber {

    /** 你的实现（dp 数组版：五部曲全显式，O(n)/O(n)）。 */
    public int fib(int n) {
        if (n == 0 || n == 1) return n;
        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i < n + 1; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    /** 你的实现 v2（滚动两变量版：空间 O(1)，等价对照用）。 */
    public int fibRoll(int n) {
        if (n <= 1) return n;
        int a = 0, b = 1;
        for (int i = 2; i <= n; i++) {
            int c = a + b;
            a = b;
            b = c;
        }
        return b;
    }

    public static void main(String[] args) {
        P509FibonacciNumber solver = new P509FibonacciNumber();

        run(solver, 0, "F(0)=0 边界");
        run(solver, 1, "F(1)=1 边界（★ 写成 return 0 的会在右上角翻车）");
        run(solver, 2, "第一个递推数 1");
        run(solver, 3, "2");
        run(solver, 4, "示例1 官方 → 3");
        run(solver, 5, "5");
        run(solver, 10, "55");
        run(solver, 20, "6765");
        run(solver, 25, "75025");
        run(solver, 30, "LeetCode 上限 832040");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立参考实现：记忆化递归（与滚动变量机制不同——自顶向下 vs 自底向上）。
     * 另含朴素递归的调用计数（演示 O(2^n) 为什么不可交）。
     */
    private static final Map<Integer, Integer> MEMO = new HashMap<>();

    private static int refMemo(int n) {
        if (n <= 1) return n;
        if (MEMO.containsKey(n)) return MEMO.get(n);
        int v = refMemo(n - 1) + refMemo(n - 2);
        MEMO.put(n, v);
        return v;
    }

    private static void run(P509FibonacciNumber solver, int n, String label) {
        try {
            int got = solver.fib(n);
            int want = refMemo(n);
            int roll = solver.fibRoll(n);
            if (roll != want) {
                System.out.println("    [MISMATCH] 滚动版与记忆化参考不一致！");
            }
            boolean ok = got == want && roll == want;

            System.out.printf("[%s] %-42s n=%-3d | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, n, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-42s n=%-3d | TODO 未实现%n", label, n);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-42s n=%-3d | 异常: %s%n", label, n, e);
        }
    }
}
