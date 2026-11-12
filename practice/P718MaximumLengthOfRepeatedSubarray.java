import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 718: 最长重复子数组 (Maximum Length of Repeated Subarray)
 * https://leetcode.cn/problems/maximum-length-of-repeated-subarray/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（子序列组 2/7——"连续"锚定 + 二维 DP 登场）
 *
 * <p>■ 题目描述
 *   给两个整数数组 A 和 B，返回两个数组中【公共的、长度最长的子数组】的长度。
 *   "子数组" = **必须连续**的一段（区别于"子序列"可跳——这是本题与 1143 的分野）。
 *   公共 = 两个数组各自取一段连续区间，两段完全相同（逐元素相等）。
 *
 * <p>■ 输入输出
 *   输入：int[] A（1 ≤ A.length ≤ 1000；0 ≤ A[i] ≤ 100）、int[] B（1 ≤ B.length ≤ 1000；0 ≤ B[i] ≤ 100）
 *   输出：int（最长公共子数组的长度）
 *
 * <p>■ 示例逐条
 * <pre>
 * A=[1,2,3,2,1], B=[3,2,1,4,7] → 3（官方：[3,2,1] 在 B 的开头连续出现）
 * A=[0,0,0,0,0], B=[0,0,0,0,0] → 5（全零互消——直接整段对上）
 * A=[1,2,3], B=[4,5,6]         → 0（无公共）
 * A=[1], B=[1]                 → 1（单元素公共）
 * A=[1,2,1,2], B=[2,1,2]       → 3（[2,1,2] 的镜像——B 整段）
 * A=[1,0,0,0,1], B=[1,0,0,1,1] → 3（[1,0,0]——参考定）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】动态规划(DP) · 二维 · 滑动窗口（对照）· 二分（对照）
 * <p>【题型】"双锚"二维 dp[i][j] = "以 A[i-1]、B[j-1] 同时结尾的公共子数组长度"——
 * <p>　　　　匹配 +1、不匹配**归零**（连续性的全部表达）；answer = 全表 max
 * <p>【考点】二维坐标语义（dp[i][j] 的 i-1/j-1 偏移——70 的流派 B 坐标约定在二维的投影）；
 * <p>　　　　"不匹配归零"（1143 的"不匹配继承"对照组）；全表 max（同 300 的结尾锚定型收账）；
 * <p>　　　　一维滚动优化（倒序——0-1 背包同款"引用左上旧值"机制）
 * <p>【关联】P1143（不连续版——下一题的对照组）；P300（一维锚定的二维化）；P139（串匹配同族）
 *
 * <p>LCS 签名：findLength(int[] nums1, int[] nums2)。
 */
public class P718MaximumLengthOfRepeatedSubarray {

    /** 你的实现（转移/归零/收账全对；上界修正 i<=m、j<=n——含末锚）。 */
    public int findLength(int[] nums1, int[] nums2) {
        int m = nums1.length;
        int n = nums2.length;
        int[][] dp = new int[m + 1][n + 1];
        int best = 0;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    best = Math.max(dp[i][j], best);
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        return best;
    }

    public static void main(String[] args) {
        P718MaximumLengthOfRepeatedSubarray solver = new P718MaximumLengthOfRepeatedSubarray();

        run(solver, new int[]{1, 2, 3, 2, 1}, new int[]{3, 2, 1, 4, 7}, "官方例1 → 3（[3,2,1]）");
        run(solver, new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}, "全零 → 5");
        run(solver, new int[]{1, 2, 3}, new int[]{4, 5, 6}, "无公共 → 0");
        run(solver, new int[]{1}, new int[]{1}, "单元素 → 1");
        run(solver, new int[]{1, 2, 1, 2}, new int[]{2, 1, 2}, "B 整段镜像 → 3");
        run(solver, new int[]{1, 0, 0, 0, 1}, new int[]{1, 0, 0, 1, 1}, "部分重叠 → 参考定");
        run(solver, new int[]{1, 2, 3, 2, 1}, new int[]{3, 2, 1, 4, 7, 3, 2, 1}, "B 变长（后段公共）→ 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：三重循环枚举所有 (i, j) 起点 + 逐元素延伸——O(m·n·min(m,n))，
     * 机制与 dp 表完全不同；n≤7 用例内可信。
     */
    private static int bruteForce(int[] A, int[] B) {
        int best = 0;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                int len = 0;
                while (i + len < A.length && j + len < B.length && A[i + len] == B[j + len]) {
                    len++;
                }
                best = Math.max(best, len);
            }
        }
        return best;
    }

    private static void run(P718MaximumLengthOfRepeatedSubarray solver, int[] A, int[] B, String label) {
        try {
            int got = solver.findLength(Arrays.copyOf(A, A.length), Arrays.copyOf(B, B.length));
            int want = bruteForce(Arrays.copyOf(A, A.length), Arrays.copyOf(B, B.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-34s A=%-22s B=%-24s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(A), Arrays.toString(B), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s A=%-22s B=%-24s | TODO 未实现%n", label, Arrays.toString(A), Arrays.toString(B));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s A=%-22s B=%-24s | 异常: %s%n", label, Arrays.toString(A), Arrays.toString(B), e);
        }
    }
}
