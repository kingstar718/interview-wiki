import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 279: 完全平方数 (Perfect Squares)
 * https://leetcode.cn/problems/perfect-squares/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面】（背包 6/8——322 换一套物品集合）
 *
 * <p>■ 题意
 * <p>　给正整数 n。求：**最少用几个完全平方数**（1,4,9,16,25…）相加，能凑出 n。
 * <p>　（同一平方数可重复使用；答案必存在——1 可以凑一切，所以**无 -1 判定**）
 *
 * <p>■ 对照 322（同一张完全背包最值表，换一套"物品"）
 * <pre>
 *   322: 物品=coins（题面给）      dp[j]=min(dp[j], dp[j−coin]+1)
 *   279: 物品=1,4,9,16,…≤n（**现造**: for j=1; j*j≤i; j++）
 *        dp[i]=min(dp[i], dp[i−j*j]+1)
 * </pre>
 * <p>■ 三个差异点（相对 322）
 * <p>　① **物品集合现造**：不是入参带走，而是 while 权界内生产（j*j ≤ i 且共用循环——
 * <p>　　　官方通用 for 写法把"造物品"和"用物品"合并在内层: `for (j=1; j*j≤i; j++)`）
 * <p>　② **无 -1**：1 恒在场 → 永远凑得出 → 直接 `return dp[n]`
 * <p>　③ **同样无需 max overflow 判定**——dp[i] ≤ i（全 1 凑法兜底）→ 伪∞ 用 n+1 同样万无一失
 *
 * <p>■ 输入输出
 * <p>　输入：int n（1 ≤ n ≤ 10^4）
 * <p>　输出：int（最少个数）
 *
 * <p>■ 示例逐条
 * <pre>
 * n=12 → 3（4+4+4）
 * n=13 → 2（4+9）
 * n=1 → 1
 * n=100 → 1（100 本身是平方数）
 * n=7 → 4（1+1+1+4——7 不是平方，≤3 的组合最少 4 枚……手推：4+1+1+1=4 枚，无 3 枚解）✓ 参考定
 * n=12 vs 13 → 差 1 枚（12 无 9 项组合：4+4+4=3 首；13=4+9=2 首）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】动态规划(DP) · 完全背包（现造物品·最值型）· BFS（对照）· 数论（四平方和定理·对照）
 * <p>【题型】完全平方数物品集的最少枚数 —— 322 的换皮（dp[0]=0 语义判读的第一应用场）
 * <p>【考点】内层"造+用"合并；伪∞=n+1 无 overflow；无 -1 判定；贪心不成立（与 322 同理由：[1,3,4] 型反例）
 * <p>【关联】P322（同表换物品）；P279 BFS 解（最短路径意义）；面试口答"四平方和定理 O(n^(1/4)) 数论版"
 *
 * <p>LCS 签名：numSquares(int n)。给正整数 n，返回由完全平方数凑出 n 的最少个数。
 */
public class P279PerfectSquares {

    /** 你的实现（与 322 同表换物品：伪∞ fill + dp[0]=0 + 内层现造平方数 + min）。 */
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, n + 1);
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j * j <= i; j++) {
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        P279PerfectSquares solver = new P279PerfectSquares();

        run(solver, 12, "官方示例1 → 3（4+4+4）");
        run(solver, 13, "官方示例2 → 2（4+9）");
        run(solver, 1, "最小 → 1");
        run(solver, 100, "本身是平方 → 1");
        run(solver, 7, "4+1+1+1 → 4");
        run(solver, 2, "1+1 → 2");
        run(solver, 10000, "LC 上限压力（参考定）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：记忆化 DFS 自顶向下（从 n 出发枚举"减谁"），与自底向上 min 递推互验。
     */
    private static long dfsMin(int remain, Map<Integer, Long> memo) {
        if (remain == 0) return 0L;
        Long cached = memo.get(remain);
        if (cached != null) return cached;
        long best = Long.MAX_VALUE;
        for (int j = 1; (long) j * j <= remain; j++) {
            long sub = dfsMin(remain - j * j, memo);
            if (sub + 1 < best) best = sub + 1;
        }
        memo.put(remain, best);
        return best;
    }

    private static int bruteForce(int n) {
        if (n > 2000) {   // 大 n 递归太深（万层）——放大栈线程跑参考实现
            final long[] out = new long[1];
            Thread t = new Thread(null, () -> out[0] = dfsMin(n, new HashMap<>()),
                    "dfsRef", 64L * 1024 * 1024);   // 64MB 栈
            t.start();
            try { t.join(); } catch (InterruptedException ignored) { }
            return (int) out[0];
        }
        return (int) dfsMin(n, new HashMap<>());
    }

    private static void run(P279PerfectSquares solver, int n, String label) {
        try {
            int got = solver.numSquares(n);
            int want = bruteForce(n);
            boolean ok = got == want;

            System.out.printf("[%s] %-28s n=%-8d | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, n, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s n=%-8d | TODO 未实现%n", label, n);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s n=%-8d | 异常: %s%n", label, n, e);
        }
    }
}
