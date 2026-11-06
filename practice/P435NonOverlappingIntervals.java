import java.util.Arrays;

/**
 * LeetCode 435: 无重叠区间 (Non-overlapping Intervals)
 * https://leetcode.cn/problems/non-overlapping-intervals/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · 排序 · 区间
 * <p>【题型】区间线主场：按右端点排序选最多不重叠，删除数 = 总数 - 可选数
 * <p>【考点】相接不算重叠（start >= prevEnd 合法）；端值溢出与 negative start 的 comparator 陷阱
 * <p>【关联】P56（按左端点排序合并——对照模板）；P763（划分字母区间）
 *
 * <p>给 intervals（可含负坐标），移除最少使其互不重叠；返回最少移除数。
 * 注意：尾端相接（[1,2] 与 [2,3]）不算重叠；区间内可能含 -2^31 端值。
 *
 * <p>示例：[[1,2],[2,3],[3,4],[1,3]] → 1；[[1,2],[1,2],[1,2]] → 2；[[1,2],[2,3]] → 0
 * <p>约束：1 &lt;= n &lt;= 10^5；-5*10^4 &lt;= start &lt; end &lt;= 5*10^4
 */
public class P435NonOverlappingIntervals {

    /** 你的实现 v2（排序 by end + keptEnd 判定 + 补集换算）。 */
    public int eraseOverlapIntervals(int[][] intervals) {
        Arrays.sort(intervals, java.util.Comparator.comparingInt(a -> a[1]));
        int keep = 1;
        int keepEnd = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= keepEnd) {
                keep++;
                keepEnd = intervals[i][1];
            }
        }
        return intervals.length - keep;
    }

    public static void main(String[] args) {
        P435NonOverlappingIntervals solver = new P435NonOverlappingIntervals();

        run(solver, new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 3}}, "示例1 官方 → 1");
        run(solver, new int[][]{{1, 2}, {1, 2}, {1, 2}}, "示例2 官方 全等 → 2");
        run(solver, new int[][]{{1, 2}, {2, 3}}, "示例3 官方 相接不算 → 0");
        run(solver, new int[][]{{1, 4}, {2, 3}, {3, 5}}, "含被包含区间 → 1");
        run(solver, new int[][]{{5, 6}, {1, 2}, {3, 4}}, "乱序但天然无重叠 → 0");
        run(solver, new int[][]{{-2, -1}}, "单个负区间 → 0");
        run(solver, new int[][]{{-5, 0}, {-3, 2}}, "负端重叠 → 1");
        run(solver, new int[][]{{-1, 1}, {1, 1}}, "单点区间 vs 前一段——相接不算 → 0");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：删干净所有子集逐个验证互不重叠，找最小删除数（n ≤ 8 可行）。 */
    private static int bruteForce(int[][] intervals) {
        int n = intervals.length;
        for (int remove = 0; remove <= n; remove++) {
            int keep = n - remove;
            int[] idx = new int[keep];
            if (tryAll(intervals, 0, 0, idx, remove)) return remove;
        }
        return n;
    }

    private static boolean tryAll(int[][] intervals, int totalKept, int pos, int[] idx, int remove) {
        int n = intervals.length;
        if (totalKept == n - remove) {
            // 验证保留集互不重叠（按保留顺序排序后相邻判定）
            int[][] kept = new int[totalKept][];
            for (int i = 0; i < totalKept; i++) kept[i] = intervals[idx[i]];
            Arrays.sort(kept, (a, b) -> a[0] - b[0]);
            for (int i = 1; i < totalKept; i++) {
                if (kept[i][0] < kept[i - 1][1]) return false;
            }
            return true;
        }
        for (int i = pos; i < n; i++) {
            idx[totalKept] = i;
            if (tryAll(intervals, totalKept + 1, i + 1, idx, remove)) return true;
        }
        return false;
    }

    private static void run(P435NonOverlappingIntervals solver, int[][] intervals, String label) {
        try {
            int[][] copy = new int[intervals.length][];
            for (int i = 0; i < intervals.length; i++) copy[i] = Arrays.copyOf(intervals[i], intervals[i].length);
            int got = solver.eraseOverlapIntervals(copy);

            int[][] copy2 = new int[intervals.length][];
            for (int i = 0; i < intervals.length; i++) copy2[i] = Arrays.copyOf(intervals[i], intervals[i].length);
            int want = bruteForce(copy2);
            boolean ok = got == want;

            String pretty = pretty(intervals);
            System.out.printf("[%s] %-34s intervals=%-34s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, pretty, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s intervals=%-34s | TODO 未实现%n", label, pretty(intervals));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s intervals=%-34s | 异常: %s%n", label, pretty(intervals), e);
        }
    }

    private static String pretty(int[][] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) {
            sb.append('[').append(a[i][0]).append(',').append(a[i][1]).append(']');
            if (i < a.length - 1) sb.append(',');
        }
        return sb.append(']').toString();
    }
}
