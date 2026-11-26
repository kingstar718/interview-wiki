import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 207: 课程表 (Course Schedule)
 * https://leetcode.cn/problems/course-schedule/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（图论章 4/6——拓扑排序原型·引擎切换）
 *
 * <p>■ 题目描述
 *   有 numCourses 门课，编号 0 ~ numCourses-1。
 *   prerequisites[i] = [a, b] 表示**想修课程 a，必须先修课程 b**（有向边 b→a）。
 *   判断**能否修完所有课程**（等价：这张有向图里有没有环——有环则某些课互为前置永远修不完）。
 *
 * <p>■ 输入输出
 *   输入：int numCourses（1 ≤ ≤ 2000）；int[][] prerequisites（0 ≤ ≤ 5000；prerequisites[i].length == 2）
 *   输出：boolean（能修完 → true）
 *
 * <p>■ 示例逐条
 * <pre>
 * n=2, [[1,0]] → true（0 先修 → 再修 1）
 * n=2, [[1,0],[0,1]] → false（互为前置=环）
 * n=1, [] → true（孤课无前置）
 * n=3, [[1,0],[2,1]] → true（链式依赖 0→1→2 顺序修）
 * n=3, [[0,1],[1,2],[2,0]] → false（三课成环）
 * n=4, [[1,0],[2,0],[3,1]] → true（菱形前置+分支）
 * n=2, [[0,1],[0,0]] → false（自环——0 依赖自己）
 * n=3, [[1,0],[1,2]] → true（1 有两个前置 0 和 2，无环）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★★）
 * <p>【标签】拓扑排序 · BFS · DFS · 有向图
 * <p>【题型】判环 = 拓扑排序消元：入度表 + 队列（Kahn 算法）；修掉的课数 < 总数 → 有环
 * <p>【考点】入度定义（箭头指向 = 入度+1）；入度 0 = 没有前置 → 可修；修一门 → 邻居入度−1 → 变 0 入队；
 * <p>　　　　count 收账在弹队时；**邻接表建图方向（b→a 不是 a→b）**
 * <p>【关联】P210（+输出顺序——同一份代码加一行）；P547（并查集镜像）；灌水三题（200/695/130——图论上一课）
 *
 * <p>LCS 签名：canFinish(int numCourses, int[][] prerequisites)。
 */
public class P207CourseSchedule {

    /** 你的实现（Kahn 消元：b→a 建图+入度表，入度0入队，弹队收账，count<n 即环）。 */
    public boolean canFinish(int numCourses, int[][] prerequisites) {
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
        while (!queue.isEmpty()) {
            Integer poll = queue.poll();
            count++;
            for (Integer next : graph.get(poll)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }
        return count == numCourses;
    }

    public static void main(String[] args) {
        P207CourseSchedule solver = new P207CourseSchedule();

        run(solver, 2, new int[][]{{1, 0}}, "单依赖 → true");
        run(solver, 2, new int[][]{{1, 0}, {0, 1}}, "互为前置 → false");
        run(solver, 1, new int[][]{}, "孤课 → true");
        run(solver, 3, new int[][]{{1, 0}, {2, 1}}, "链式 → true");
        run(solver, 3, new int[][]{{0, 1}, {1, 2}, {2, 0}}, "三课成环 → false");
        run(solver, 4, new int[][]{{1, 0}, {2, 0}, {3, 1}}, "菱形 → true");
        run(solver, 2, new int[][]{{0, 1}, {0, 0}}, "自环 → false ★ 哨兵");
        run(solver, 3, new int[][]{{1, 0}, {1, 2}}, "双前置汇流 → true");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：DFS 三色标记判环（0未访/1访问中/2完成）——机制与 Kahn 消元完全不同。 */
    private static boolean bruteForce(int numCourses, int[][] prerequisites) {
        java.util.List<java.util.List<Integer>> graph = new java.util.ArrayList<>();
        for (int i = 0; i < numCourses; i++) graph.add(new java.util.ArrayList<>());
        for (int[] p : prerequisites) graph.get(p[1]).add(p[0]);   // b → a
        int[] color = new int[numCourses];                        // 0未访 1访问中 2完成
        for (int i = 0; i < numCourses; i++) {
            if (color[i] != 0) continue;
            if (hasCycle(graph, color, i)) return false;
        }
        return true;
    }

    private static boolean hasCycle(java.util.List<java.util.List<Integer>> graph, int[] color, int u) {
        color[u] = 1;
        for (int v : graph.get(u)) {
            if (color[v] == 1) return true;
            if (color[v] == 0 && hasCycle(graph, color, v)) return true;
        }
        color[u] = 2;
        return false;
    }

    private static void run(P207CourseSchedule solver, int numCourses, int[][] prerequisites, String label) {
        try {
            boolean got = solver.canFinish(numCourses, copyOf(prerequisites));
            boolean want = bruteForce(numCourses, copyOf(prerequisites));
            boolean ok = got == want;

            System.out.printf("[%s] %-24s n=%-3d pre=%-34s | 参考=%b 实得=%b%n",
                    ok ? "PASS" : "FAIL", label, numCourses, fmt(prerequisites), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s n=%-3d pre=%-34s | TODO 未实现%n", label, numCourses, fmt(prerequisites));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s n=%-3d pre=%-34s | 异常: %s%n", label, numCourses, fmt(prerequisites), e);
        }
    }

    private static int[][] copyOf(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = a[i].clone();
        return c;
    }

    private static String fmt(int[][] a) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a.length; i++) sb.append(java.util.Arrays.toString(a[i])).append(i < a.length - 1 ? ", " : "");
        return sb.append("]").toString();
    }
}
