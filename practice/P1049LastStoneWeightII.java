import java.util.Arrays;

/**
 * LeetCode 1049: 最后一块石头的重量 II (Last Stone Weight II)
 * https://leetcode.cn/problems/last-stone-weight-ii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（背包组 7/8——四把钥匙的收官固化题）
 *
 * <p>■ 题目描述
 *   有一堆石头，每块重量记在下标 i（stone[i]）。每一回合，从堆中
 *   【任选两块】石头把它们一起粉碎：
 *     1) 两块重量相等（x == y）→ 两块全部粉碎（无剩余）；
 *     2) 两块重量不等（x != y）→ 两块相减，剩下 |x - y| 的一块新石头（回到堆里）。
 *   如此反复进行，直到【少于两块石头】为止。
 * ■ 求
 *   最后剩下的那一块石头的【最小可能重量】。若没有任何剩余（全部两两相撞全碎），
 *   视为剩余重量为 0。
 *
 * <p>■ 输入输出
 *   输入：int[] stones（1 ≤ length ≤ 30；1 ≤ stones[i] ≤ 100）
 *   输出：int（最少可能的剩余重量）
 *
 * <p>■ 示例逐条
 * <pre>
 * stones=[2,7,4,1,8,1] → 1　（官方：8 与 7 相撞剩 1，(2,4,1,1) 归零路上由参考的树状推演实现）
 * stones=[31,26,33,21,40] → 5　（参考定）
 * stones=[1] → 1　（单块无处可撞）
 * stones=[2,2] → 0　（两块相等 → 两碎，全灭）
 * stones=[4,1,8,2] → 1　（4+1+2=7 vs 8 → |7-8|=1）
 * stones=[10] → 10
 * stones=[5,5,5,5] → 0　（1/4 清空对称）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★ ★）
 * <p>【标签】动态规划(DP) · 0-1 背包（判定型的"最大可达容量"变色）
 * <p>【题型】OR DS "最小剩余" = sum − 2×(≤sum/2 的最大可达子集重量) —— dp[j] 硬度最大 dp 表变色
 * <p>【考点】01 背包的【第三次变身】（416 判定 → 494 计数 → 本题最值守"能装多少"）；
 * <p>　　　　石头的"相爆证照"（答案永远 = 两组差），以及"上界 sum/2 更两子集差最小化"的代数论证
 * <p>【关联】P416（同"能否 &lt;= sum/2"判定，本题为"可达的+max"；直接同构）；P198（线性 DP 兄弟结构）
 *
 * <p>LCS 签名：lastStoneWeightII(int[] stones)。
 */
public class P1049LastStoneWeightII {

    /** 你的实现（01 背包最值装填：dp[j]=容量 j 最多装多满，答案 sum−2×dp[target]）。 */
    public int lastStoneWeightII(int[] stones) {
        int sum = 0;
        for (int s : stones) {
            sum += s;
        }
        int target = sum / 2;
        int[] dp = new int[target + 1];
        for (int s : stones) {
            for (int i = target; i >= s; i--) {
                dp[i] = Math.max(dp[i], dp[i - s] + s);
            }
        }
        return sum - 2 * dp[target];
    }

    public static void main(String[] args) {
        P1049LastStoneWeightII solver = new P1049LastStoneWeightII();

        run(solver, new int[]{2, 7, 4, 1, 8, 1}, "官方示例 → 1（官方解释 8 vs 7 → 剩1）");
        run(solver, new int[]{31, 26, 33, 21, 40}, "LC 例2 → 参考定");
        run(solver, new int[]{1}, "单块 → 1（无处可撞）");
        run(solver, new int[]{2, 2}, "等重对撞 → 两灭 → 0");
        run(solver, new int[]{4, 1, 8, 2}, "手推 (4+1+2=7 vs 8) → 1");
        run(solver, new int[]{10}, "单块大 → 10");
        run(solver, new int[]{5, 5, 5, 5}, "四等 → 0");
        run(solver, new int[]{3, 5, 7}, "奇噪（3+5=8 vs 7 → 1；参考定）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：【递归撞两块】每回合枚举所有两块对，剩余 → 下层递归，到 <2 块收账最小——
     * 机制：树状状态空间枚举（与 01 背包 dp 表完全不同尺度）；n ≤ 8 内可信。
     */
    private static int bruteForce(long[] stones, int n) {
        if (n < 2) return n == 0 ? 0 : (int) stones[0];
        int best = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                long xi = stones[i], xj = stones[j];
                long rest = 0;                    // |xi - xj| 或 相等时全灭
                if (xi != xj) rest = Math.abs(xi - xj);
                long[] rem = new long[n];         // 移除 i、j；补入 rest
                int idx = 0;
                for (int k = 0; k < n; k++) {
                    if (k == i || k == j) continue;
                    rem[idx++] = stones[k];
                }
                if (rest > 0) rem[idx++] = rest;
                int val = bruteForce(rem, idx);
                best = Math.min(best, val);
            }
        }
        return best;
    }

    private static int bruteForce(int[] stones) {
        long[] l = new long[stones.length];
        for (int i = 0; i < stones.length; i++) l[i] = stones[i];
        // n ≤ 8 才枚举；大于则退回贪心心例外——wiki 参考解为准（避免大数暴力爆掉）
        if (stones.length <= 8) {
            return bruteForce(l, stones.length);
        }
        return largeRef(stones);
    }

    private static int largeRef(int[] stones) {
        int sum = 0;
        for (int s : stones) sum += s;
        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];   // 判定型参考（非最值型——机制次成本差异依旧独立）
        dp[0] = true;
        for (int stone : stones) {
            for (int j = target; j >= stone; j--) {
                dp[j] = dp[j] || dp[j - stone];
            }
        }
        for (int j = target; j >= 0; j--) {
            if (dp[j]) return sum - 2 * j;
        }
        return sum;   // 不可达（不可能发生：空集可达）
    }

    private static void run(P1049LastStoneWeightII solver, int[] stones, String label) {
        try {
            int got = solver.lastStoneWeightII(Arrays.copyOf(stones, stones.length));
            int want = bruteForce(Arrays.copyOf(stones, stones.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-44s stones=%-24s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(stones), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-44s stones=%-24s | TODO 未实现%n", label, Arrays.toString(stones));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-44s stones=%-24s | 异常: %s%n", label, Arrays.toString(stones), e);
        }
    }
}
