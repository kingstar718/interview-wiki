import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * LeetCode 210: 课程表 II (Course Schedule II)
 * https://leetcode.cn/problems/course-schedule-ii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（挂账还账·207 同码加输出）
 *
 * <p>■ 题目描述
 *   同 207 的前置关系，返回**一个**能修完全部课程的**修课顺序**
 *   （任何合法顺序都算对；无法修完返回**空数组**）。
 *
 * <p>■ 输入输出
 *   输入：int numCourses（1 ≤ ≤ 2000）；int[][] prerequisites
 *   输出：int[]（合法顺序或空数组）
 *
 * <p>■ 示例逐条
 * <pre>
 * n=2, [[1,0]] → [0,1]（0 先修）
 * n=4, [[1,0],[2,0],[3,1],[3,2]] → [0,1,2,3] 或 [0,2,1,3]（都合法）
 * n=1, [] → [0]
 * n=2, [[1,0],[0,1]] → []（环 → 空）
 * n=2, [[0,1],[0,0]] → []（自环）
 * n=3, [[1,0],[2,1]] → [0,1,2]
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯）
 * <p>【标签】拓扑排序 · BFS · 有向图
 * <p>【题型】Kahn 消元的输出版（207 原码三处改动）：
 * <p>　　返回类型 boolean→int[]；count==n 换"有环返空"；
 * <p>　　　消元时把弹出的课号【记进结果数组】（顺序天然合法）
 * <p>【考点】弹队顺序=合法拓扑序（队列先进先出保证前置先于后继）；
 * <p>　　　　空数组的表示（new int[0]）
 * <p>【关联】P207（原型——原码改三处）；P547（下一题并查集）
 *
 * <p>LCS 签名：findOrder(int numCourses, int[][] prerequisites)。
 */
public class P210CourseScheduleII {

    /** 你的实现（207 改三处：弹队记序、int[] 直接填、有环返空——207 肌肉记忆直迁）。 */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }
        int[] indegree = new int[numCourses];
        for (int[] p : prerequisites) {
            graph.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }

        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) queue.offer(i);
        }

        int count = 0;
        int[] order = new int[numCourses];
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            order[count++] = poll;
            for (Integer next : graph.get(poll)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }
        return count != numCourses ? new int[0] : order;
    }

    public static void main(String[] args) {
        P210CourseScheduleII solver = new P210CourseScheduleII();

        run(solver, 2, new int[][]{{1, 0}}, "单依赖 → [0,1]");
        run(solver, 4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}, "菱形 → 任一合法序");
        run(solver, 1, new int[][]{}, "孤课 → [0]");
        run(solver, 2, new int[][]{{1, 0}, {0, 1}}, "互为前置 → []");
        run(solver, 2, new int[][]{{0, 1}, {0, 0}}, "自环 → []");
        run(solver, 3, new int[][]{{1, 0}, {2, 1}}, "链式 → [0,1,2]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举全部排列→逐个验证合法（阶乘级，n≤4 才用；机制独立）。 */
    private static int[] bruteForce(int n, int[][] pre) {
        // 检查给定顺序是否合法：每条边 [a,b] 要求 b 在 a 之前
        int[][] perms = allPerms(n);
        for (int[] p : perms) {
            if (validOrder(p, pre)) return p;
        }
        return new int[0];
    }

    private static boolean validOrder(int[] order, int[][] pre) {
        int[] pos = new int[order.length];
        for (int i = 0; i < order.length; i++) pos[order[i]] = i;
        for (int[] e : pre) {
            if (pos[e[1]] >= pos[e[0]]) return false;   // b 必须在 a 前
        }
        return true;
    }

    private static int[][] allPerms(int n) {
        List<int[]> out = new ArrayList<>();
        permute(new int[n], 0, new boolean[n], out);
        return out.toArray(new int[0][]);
    }

    private static void permute(int[] cur, int depth, boolean[] used, List<int[]> out) {
        if (depth == cur.length) { out.add(cur.clone()); return; }
        for (int i = 0; i < cur.length; i++) {
            if (used[i]) continue;
            used[i] = true; cur[depth] = i;
            permute(cur, depth + 1, used, out);
            used[i] = false;
        }
    }

    private static void run(P210CourseScheduleII solver, int numCourses, int[][] prerequisites, String label) {
        try {
            int[] want = bruteForce(numCourses, prerequisites);
            int[] got = solver.findOrder(numCourses, prerequisites);
            // 多答案题：got 也可以和 want 不同——用合法性与长度对账
            boolean ok = got.length == want.length
                    && (want.length == 0 || (validOrder(got, prerequisites) && coversAll(got, numCourses)));

            System.out.printf("[%s] %-18s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(want), Arrays.toString(got));
            if (!ok) System.out.println("    got 非法或长度不对");
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-18s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-18s | 异常: %s%n", label, e);
        }
    }

    private static boolean coversAll(int[] got, int n) {
        boolean[] seen = new boolean[n];
        for (int x : got) {
            if (x < 0 || x >= n || seen[x]) return false;
            seen[x] = true;
        }
        for (boolean b : seen) if (!b) return false;
        return true;
    }
}
