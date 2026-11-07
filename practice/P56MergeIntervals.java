import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * LeetCode 56: 合并区间 (Merge Intervals)
 * https://leetcode.cn/problems/merge-intervals/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · 排序 · 区间
 * <p>【题型】区间线压轴：按左端点排序 + "start <= curEnd 就吸收"（与 435 的 >= 保留是镜像）
 * <p>【考点】curEnd = max(curEnd, e)（被包含区间不得拉回墙）；收尾补 add；端点相接算重叠（与 435 相反）
 * <p>【关联】P435（右端点/删除）与本题（左端点/合并）——区间线两条配对模板
 *
 * <p>给 intervals（可含重叠），把所有有重叠的区间合并。返回不重叠的区间数组（升序）。
 * 注意：[1,4] 与 [4,5] 相接也算重叠（合并成 [1,5]）——**与 435 的"相接不算"规则相反**。
 *
 * <p>示例：[[1,3],[2,6],[8,10],[15,18]] → [[1,6],[8,10],[15,18]]；[[1,4],[4,5]] → [[1,5]]
 * <p>约束：1 &lt;= n &lt;= 10^4；0 &lt;= start &lt;= end &lt;= 10^4
 */
public class P56MergeIntervals {

    /** 你的实现（按 start 排序 + 墙 max 合并 + 收尾补 add；Comparator 代替减法）。 */
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
        List<int[]> res = new ArrayList<>();
        int start = intervals[0][0];
        int curEnd = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {
            int[] se = intervals[i];
            int s = se[0];
            int e = se[1];
            if (s <= curEnd) {
                curEnd = Math.max(curEnd, e);
            } else {
                res.add(new int[]{start, curEnd});
                start = s;
                curEnd = e;
            }
        }
        res.add(new int[]{start, curEnd});
        int[][] result = new int[res.size()][2];
        for (int i = 0; i < res.size(); i++) {
            result[i] = res.get(i);
        }
        return result;
    }

    public static void main(String[] args) {
        P56MergeIntervals solver = new P56MergeIntervals();

        run(solver, new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}, "示例1 官方 → [1,6],[8,10],[15,18]");
        run(solver, new int[][]{{1, 4}, {4, 5}}, "示例2 相接算重叠 → [1,5]");
        run(solver, new int[][]{{1, 4}, {2, 3}}, "被包含（内不拉墙）→ [1,4]");
        run(solver, new int[][]{{1, 4}, {1, 4}}, "全等 → [1,4]");
        run(solver, new int[][]{{5, 6}, {1, 2}, {3, 4}}, "乱序但天然不重叠 → 原样（升序）");
        run(solver, new int[][]{{1, 2}}, "单区间 → 原样");
        run(solver, new int[][]{{2, 3}, {1, 2}, {4, 5}, {1, 7}}, "乱序混合 → [1,7],[4,5]合并？暴力定");
        run(solver, new int[][]{{1, 10}, {2, 3}, {4, 5}, {11, 12}}, "胖子吃三段 → [1,10],[11,12]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：两两判重叠的并查合并（试合并直到不动点，机制与排序扫描无关，O(n^3) 可行）。 */
    private static int[][] bruteForce(int[][] intervals) {
        List<int[]> cur = new ArrayList<>();
        for (int[] iv : intervals) cur.add(iv.clone());

        boolean changed = true;
        while (changed) {
            changed = false;
            outer:
            for (int i = 0; i < cur.size(); i++) {
                for (int j = i + 1; j < cur.size(); j++) {
                    int[] a = cur.get(i), b = cur.get(j);
                    if (overlap(a, b)) {
                        int[] m = new int[]{Math.min(a[0], b[0]), Math.max(a[1], b[1])};
                        cur.remove(j);
                        cur.remove(i);
                        cur.add(m);
                        changed = true;
                        break outer;
                    }
                }
            }
        }
        cur.sort((a, b) -> a[0] - b[0]);
        return cur.toArray(new int[0][]);
    }

    private static boolean overlap(int[] a, int[] b) {
        // [s1,e1] [s2,e2] 重叠（含相接）：s2 <= e1 && s1 <= e2
        return b[0] <= a[1] && a[0] <= b[1];
    }

    private static void run(P56MergeIntervals solver, int[][] intervals, String label) {
        try {
            int[][] in = clone2D(intervals);
            int[][] got = solver.merge(in);
            int[][] want = bruteForce(clone2DDepth(intervals));

            boolean ok = Arrays.deepEquals(got, want);

            System.out.printf("[%s] %-40s intervals=%-34s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, pretty(intervals), Arrays.deepToString(want), Arrays.deepToString(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-40s intervals=%-34s | TODO 未实现%n", label, pretty(intervals));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-40s intervals=%-34s | 异常: %s%n", label, pretty(intervals), e);
        }
    }

    private static int[][] clone2D(int[][] a) {
        int[][] r = new int[a.length][];
        for (int i = 0; i < a.length; i++) r[i] = Arrays.copyOf(a[i], a[i].length);
        return r;
    }

    private static int[][] clone2DDepth(int[][] a) {
        return clone2D(a);
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
