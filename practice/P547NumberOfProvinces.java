/**
 * LeetCode 547: 省份数量 (Number of Provinces)
 * https://leetcode.cn/problems/number-of-provinces/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（挂账还账·最后一题·并查集入门）
 *
 * <p>■ 题目描述
 *   n 个城市，n×n 的邻接矩阵 isConnected：isConnected[i][j]=1 表示 i 和 j **直接相连**。
 *   省份 = 直接或间接相连的城市构成的**极大连通块**。
 *   返回省份的数量。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] isConnected（n ∈ [1,200]；对称矩阵，对角线全 1）
 *   输出：int（连通块个数）
 *
 * <p>■ 示例逐条
 * <pre>
 * [1,1,0 / 1,1,0 / 0,0,1] → 2（{0,1} 一省，{2} 一省）
 * [1,1,1 / 1,1,1 / 1,1,1] → 1（全连通）
 * [1,0,0 / 0,1,0 / 0,0,1] → 3（全孤立）
 * [1,0 / 0,1] → 2
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/亚马逊）
 * <p>【标签】并查集 · DFS · BFS · 图
 * <p>【题型】连通块计数（并查集入门题）：
 * <p>　　parent 数组 + find + union——初始 n 个独立集合，合并一次减一
 * <p>【考点】find 的路径压缩（递归一行）；union 按根合并；
 * <p>　　　　初始 count=n、有效 union 减一（判定"真的合了两个不同集合"）；
 * <p>　　　　对称矩阵只扫上三角（i<j）避免重复并
 * <p>【关联】P200 岛屿（连通块 DFS 版对照）；P684 冗余连接；P990 等式成立
 *
 * <p>LCS 签名：findCircleNum(int[][] isConnected)。
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class P547NumberOfProvinces {

    /** 你的实现（并查集三件套：parent+路径压缩 find+按根 union；count 从 n 往下并）。 */
    int[] parent;
    int count;
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        count = n;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    union(i, j);
                }

            }
        }
        return count;
    }

    private void union(int a, int b){
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) return;
        parent[ra] = rb;
        count--;
    }

    private int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public static void main(String[] args) {
        P547NumberOfProvinces solver = new P547NumberOfProvinces();

        run(solver, m("1,1,0!1,1,0!0,0,1"), "两省 → 2");
        run(solver, m("1,1,1!1,1,1!1,1,1"), "全连通 → 1");
        run(solver, m("1,0,0!0,1,0!0,0,1"), "全孤立 → 3");
        run(solver, m("1,0!0,1"), "2 城全孤 → 2");
        run(solver, m("1,1,0,0!1,1,1,0!0,1,1,0!0,0,0,1"), "链+孤 → 2");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** "1,1,0|1,1,0" → int[][]（行分隔符 !，避开正则竖线坑）。 */
    private static int[][] m(String s) {
        String[] rows = s.split("!");
        int[][] out = new int[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] cs = rows[i].split(",");
            out[i] = new int[cs.length];
            for (int j = 0; j < cs.length; j++) out[i][j] = Integer.parseInt(cs[j].trim());
        }
        return out;
    }

    /** 独立暴力参考：BFS 染色（visited 数组+队列逐块扫，机制独立于并查集）。 */
    private static int bruteForce(int[][] g) {
        int n = g.length;
        boolean[] visited = new boolean[n];
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (visited[i]) continue;
            count++;
            Deque<Integer> q = new ArrayDeque<>();
            q.offer(i); visited[i] = true;
            while (!q.isEmpty()) {
                int c = q.poll();
                for (int j = 0; j < n; j++) {
                    if (g[c][j] == 1 && !visited[j]) {
                        visited[j] = true;
                        q.offer(j);
                    }
                }
            }
        }
        return count;
    }

    private static void run(P547NumberOfProvinces solver, int[][] g, String label) {
        try {
            int want = bruteForce(g);
            int got = solver.findCircleNum(g);
            boolean ok = got == want;

            System.out.printf("[%s] %-12s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-12s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-12s | 异常: %s%n", label, e);
        }
    }
}
