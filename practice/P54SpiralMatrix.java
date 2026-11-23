import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 54: 螺旋矩阵 (Spiral Matrix)
 * https://leetcode.cn/problems/spiral-matrix/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·矩阵组 2/4——边界收缩模拟）
 *
 * <p>■ 题目描述
 *   给 m×n 矩阵，按**螺旋顺序**（顺时针，从外圈向内圈）返回所有元素。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] matrix（1 ≤ m, n ≤ 10；-100 ≤ 元素 ≤ 100）
 *   输出：List<Integer>（螺旋序）
 *
 * <p>■ 示例逐条
 * <pre>
 * [[1,2,3],[4,5,6],[7,8,9]] → [1,2,3,6,9,8,7,4,5]（3×3 标准）
 * [[1,2,3,4],[5,6,7,8],[9,10,11,12]] → [1,2,3,4,8,12,11,10,9,5,6,7]（3×4 非方阵）
 * [[1,2,3],[4,5,6],[7,8,9],[10,11,12]] → [1,2,3,6,9,12,11,10,7,4,5,8]（4×3：末圈 5→8）
 * [[1]] → [1]
 * [[1,2],[3,4]] → [1,2,4,3]（2×2）
 * [[1,2,3]] → [1,2,3]（单行）
 * [[1],[2],[3]] → [1,2,3]（单列）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·字节）
 * <p>【标签】数组 · 矩阵 · 模拟
 * <p>【题型】四边界收缩模拟：上→右→下→左各扫一条边、扫完收缩对应边界，边界交叉即止
 * <p>【考点】**非方阵防重扫**（单行/单列残圈：下边扫前判 top<=bottom、左边扫前判 left<=right）；
 * <p>　　　　收缩时机（上边扫完 top++ 即收缩——右边的起点依赖它）
 * <p>【关联】P59 螺旋矩阵II（反向构造）；P48 旋转图像（矩阵组 3/4）；P240（矩阵组 4/4）
 *
 * <p>LCS 签名：spiralOrder(int[][] matrix)。
 */
public class P54SpiralMatrix {

    /** 你的实现（四边界收缩：上→右→下→左各扫一条边扫完即收；下/左边带防重扫守卫）。 */
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        int m = matrix.length;
        int n = matrix[0].length;
        int left = 0, top = 0;
        int bottom = m - 1, right = n - 1;
        while (left <= right && top <= bottom) {
            for (int i = left; i <= right; i++) {
                res.add(matrix[top][i]);
            }
            top++;
            for (int i = top; i <= bottom; i++) {
                res.add(matrix[i][right]);
            }
            right--;
            if (top <= bottom) {
                for (int i = right; i >= left ; i--) {
                    res.add(matrix[bottom][i]);
                }
            }
            bottom--;
            if (left <= right) {
                for (int i = bottom; i >= top; i--) {
                    res.add(matrix[i][left]);
                }
            }
            left++;
        }
        return res;
    }

    public static void main(String[] args) {
        P54SpiralMatrix solver = new P54SpiralMatrix();

        run(solver, new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, "3×3 标准 → [1,2,3,6,9,8,7,4,5]");
        run(solver, new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}, "3×4 → [1,2,3,4,8,12,11,10,9,5,6,7]");
        run(solver, new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}}, "4×3 → [1,2,3,6,9,12,11,10,7,4,5,8]");
        run(solver, new int[][]{{1}}, "单格 → [1]");
        run(solver, new int[][]{{1, 2}, {3, 4}}, "2×2 → [1,2,4,3]");
        run(solver, new int[][]{{1, 2, 3}}, "单行 → [1,2,3] ★ 防重扫");
        run(solver, new int[][]{{1}, {2}, {3}}, "单列 → [1,2,3] ★ 防重扫");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：方向向量 + visited 标记（右下左上循环转向，撞墙/访问过就转）——机制不同。 */
    private static List<Integer> bruteForce(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean[][] vis = new boolean[m][n];
        int[] dr = {0, 1, 0, -1}, dc = {1, 0, -1, 0};   // 右下左上
        List<Integer> out = new ArrayList<>();
        int r = 0, c = 0, d = 0;
        for (int k = 0; k < m * n; k++) {
            out.add(matrix[r][c]);
            vis[r][c] = true;
            int nr = r + dr[d], nc = c + dc[d];
            if (nr < 0 || nr >= m || nc < 0 || nc >= n || vis[nr][nc]) {
                d = (d + 1) % 4;
                nr = r + dr[d];
                nc = c + dc[d];
            }
            r = nr;
            c = nc;
        }
        return out;
    }

    private static void run(P54SpiralMatrix solver, int[][] matrix, String label) {
        try {
            List<Integer> got = solver.spiralOrder(matrix);
            List<Integer> want = bruteForce(matrix);
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-30s | 参考=%s%n", ok ? "PASS" : "FAIL", label, want);
            if (!ok) System.out.printf("       实得=%s%n", got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s | 异常: %s%n", label, e);
        }
    }
}
