import java.util.ArrayList;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 56: 合并区间 (Merge Intervals)
 * https://leetcode.cn/problems/merge-intervals/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 9/50——排序+贪心合并）
 *
 * <p>■ 题目描述
 *   给一个区间集合 intervals，其中 intervals[i] = [start_i, end_i]。
 *   合并所有**重叠**的区间，返回**不重叠**的区间数组（恰好覆盖输入的所有区间）。
 *   区间 [a,b] 与 [c,d] 重叠 = 有公共点（端点相接也算：[1,3] 与 [3,5] 重叠）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] intervals（1 ≤ length ≤ 10^4；0 ≤ start ≤ end ≤ 10^4）
 *   输出：int[][]（合并后的区间，按 start 升序）
 *
 * <p>■ 示例逐条
 * <pre>
 * [[1,3],[2,6],[8,10],[15,18]] → [[1,6],[8,10],[15,18]]（官方例1：1,3 与 2,6 相接合并）
 * [[1,4],[4,5]] → [[1,5]]（端点相等也合并——"有公共点"）
 * [[1,4],[0,4]] → [[0,4]]（乱序输入——排序兜住）
 * [[1,4],[2,3]] → [[1,4]]（完全包含——end 取 max 而非直接换）
 * [[5,6],[1,2]] → [[1,2],[5,6]]（无重叠保持分开）
 * [[1,10],[2,3],[4,5],[11,12]] → [[1,10],[11,12]]（大区间吞小区间们）
 * [[1,1],[1,1]] → [[1,1]]（点区间相等）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·全厂）
 * <p>【标签】数组 · 排序 · 贪心
 * <p>【题型】排序后一遍贪心合并：当前 start ≤ 上一 end → 吞并（end 取 max）；否则开新区间
 * <p>【考点】排序键=start（java lambda 比较器）；重叠判据 **≤**（端点相接算重叠——`<` 就漏了）；
 * <p>　　　　合并时 end = max（完全包含的区间不能被小 end 拉低）；最后一个区间收尾（循环外补 add）
 * <p>【关联】P435 无重叠区间（同族计数版）；P452 射气球（同族）；贪心章 P56 已见过兄弟题
 *
 * <p>LCS 签名：merge(int[][] intervals)。
 */
public class P56MergeIntervals {

    /** 你的实现（排序+一次贪心合并：≤ 判据吞并、end 取 max、循环外补结算）。 */
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        List<int[]> res = new ArrayList<>();
        int start = intervals[0][0];
        int end = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= end) {
                end = Math.max(end, intervals[i][1]);
            } else {
                res.add(new int[]{start, end});
                start = intervals[i][0];
                end = intervals[i][1];
            }
        }
        res.add(new int[]{start, end});
        return res.toArray(new int[0][]);
    }

    public static void main(String[] args) {
        P56MergeIntervals solver = new P56MergeIntervals();

        run(solver, new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}, "官方例1 → [[1,6],[8,10],[15,18]]");
        run(solver, new int[][]{{1, 4}, {4, 5}}, "端点相接 → [[1,5]] ★");
        run(solver, new int[][]{{1, 4}, {0, 4}}, "乱序 → [[0,4]]");
        run(solver, new int[][]{{1, 4}, {2, 3}}, "完全包含 → [[1,4]] ★ end取max哨兵");
        run(solver, new int[][]{{5, 6}, {1, 2}}, "无重叠 → [[1,2],[5,6]]");
        run(solver, new int[][]{{1, 10}, {2, 3}, {4, 5}, {11, 12}}, "大吞小 → [[1,10],[11,12]]");
        run(solver, new int[][]{{1, 1}, {1, 1}}, "点区间 → [[1,1]]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：两两归并不动点法（任意一对重叠就换成并集，重复直到无变化）——机制与一次扫描完全不同。 */
    private static int[][] bruteForce(int[][] intervals) {
        List<int[]> cur = new ArrayList<>();
        for (int[] iv : intervals) cur.add(new int[]{iv[0], iv[1]});
        boolean changed = true;
        while (changed) {
            changed = false;
            outer:
            for (int i = 0; i < cur.size(); i++) {
                for (int j = i + 1; j < cur.size(); j++) {
                    int[] a = cur.get(i), b = cur.get(j);
                    if (a[0] <= b[1] && b[0] <= a[1]) {          // 有公共点 = 重叠
                        cur.set(i, new int[]{Math.min(a[0], b[0]), Math.max(a[1], b[1])});
                        cur.remove(j);
                        changed = true;
                        break outer;
                    }
                }
            }
        }
        cur.sort((a, b) -> a[0] - b[0]);
        return cur.toArray(new int[0][]);
    }

    private static void run(P56MergeIntervals solver, int[][] intervals, String label) {
        try {
            int[][] got = solver.merge(copy(intervals));
            int[][] want = bruteForce(intervals);
            boolean ok = fmt(got).equals(fmt(want));

            System.out.printf("[%s] %-30s in=%-40s | 参考=%s%n",
                    ok ? "PASS" : "FAIL", label, fmt(intervals), fmt(want));
            if (!ok) System.out.printf("       实得=%s%n", fmt(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s in=%-40s | TODO 未实现%n", label, fmt(intervals));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s in=%-40s | 异常: %s%n", label, fmt(intervals), e);
        }
    }

    private static int[][] copy(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = a[i].clone();
        return c;
    }

    private static String fmt(int[][] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) sb.append(Arrays.toString(a[i])).append(i < a.length - 1 ? ", " : "");
        return sb.append("]").toString();
    }
}
