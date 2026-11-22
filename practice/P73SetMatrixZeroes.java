import java.util.Arrays;

/**
 * LeetCode 73: 矩阵置零 (Set Matrix Zeroes)
 * https://leetcode.cn/problems/set-matrix-zeroes/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 18/50——矩阵组 1/4·原地标记）
 *
 * <p>■ 题目描述
 *   给 m×n 矩阵，若某元素为 0，则将其**所在行和列**的所有元素都置 0。
 *   必须**原地**修改；进阶：O(1) 额外空间（不用"再建一个矩阵抄答案"）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] matrix（1 ≤ m, n ≤ 200；-2^31 ≤ 元素 ≤ 2^31-1）
 *   输出：无（原地修改）
 *
 * <p>■ 示例逐条
 * <pre>
 * [[1,1,1],[1,0,1],[1,1,1]] → [[1,0,1],[0,0,0],[1,0,1]]（官方例1：单个 0 炸十字）
 * [[0,1,2,0],[3,4,5,2],[1,3,1,5]] → [[0,0,0,0],[0,4,5,0],[0,3,1,0]]（官方例2：第一行有 0）
 * [[1]] → [[1]]
 * [[0]] → [[0]]
 * [[1,2],[3,4]] → [[1,2],[3,4]]（无零不动）
 * [[0,1],[1,1],[1,0]] → [[0,0],[0,0],[0,0]]（(0,0) 炸首行列、(2,1) 炸末行末列 → 全零）★
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·百度）
 * <p>【标签】数组 · 矩阵 · 原地标记
 * <p>【题型】原地标记三段式：第一行/列当"旗标位"（flag 哨兵）+ 内部扫描标旗 + 按旗置零 + 收尾首行首列
 * <p>【考点】**第一行列要单独预记**（它们被当旗标用，自己的零信息会丢）；四步顺序不可乱；
 * <p>　　　　内部先行后列的置零序（避免首行首列污染）；O(m+n) 简版 vs O(1) 旗标版的演进
 * <p>【关联】P48 旋转图像（矩阵组 2/4）；P240（矩阵组 4/4）；灌水三题（原地标记同族思想）
 *
 * <p>LCS 签名：setZeroes(int[][] matrix)。
 */
public class P73SetMatrixZeroes {

    /** 你的实现（灵神同款旗标三段式：预记首行列布尔→内部立旗→按旗清→收尾首行列）。 */
    public void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean row = false, col = false;
        for (int i = 0; i < n; i++) {
            if (matrix[0][i] == 0) {
                row = true;
            }
        }
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) {
                col = true;
            }
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }
        if (row) {
            for (int i = 0; i < n; i++) {
                matrix[0][i] = 0;
            }
        }
        if (col) {
            for (int i = 0; i < m; i++) {
                matrix[i][0] = 0;
            }
        }
    }

    public static void main(String[] args) {
        P73SetMatrixZeroes solver = new P73SetMatrixZeroes();

        run(solver, new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}}, "官方例1 → 十字");
        run(solver, new int[][]{{0, 1, 2, 0}, {3, 4, 5, 2}, {1, 3, 1, 5}}, "官方例2 → 首行含零");
        run(solver, new int[][]{{1}}, "单格 → 不变");
        run(solver, new int[][]{{0}}, "单零格 → [[0]]");
        run(solver, new int[][]{{1, 2}, {3, 4}}, "无零 → 不变");
        run(solver, new int[][]{{0, 1}, {1, 1}, {1, 0}}, "双零全炸 → 全零 ★ 哨兵");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：抄一份原矩阵做判断依据，往原矩阵写零——机制与原地旗标完全不同。 */
    private static int[][] bruteForce(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] copy = new int[m][];
        for (int i = 0; i < m; i++) copy[i] = matrix[i].clone();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (copy[i][j] == 0) {
                    for (int k = 0; k < n; k++) matrix[i][k] = 0;
                    for (int k = 0; k < m; k++) matrix[k][j] = 0;
                }
            }
        }
        return matrix;
    }

    private static void run(P73SetMatrixZeroes solver, int[][] matrix, String label) {
        try {
            int[][] got = copyOf(matrix);
            solver.setZeroes(got);
            int[][] want = bruteForce(copyOf(matrix));
            boolean ok = Arrays.deepEquals(got, want);

            System.out.printf("[%s] %-26s | 参考=%s%n", ok ? "PASS" : "FAIL", label, fmt(want));
            if (!ok) System.out.printf("       实得=%s%n", fmt(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s | 异常: %s%n", label, e);
        }
    }

    private static int[][] copyOf(int[][] a) {
        int[][] c = new int[a.length][];
        for (int i = 0; i < a.length; i++) c[i] = a[i].clone();
        return c;
    }

    private static String fmt(int[][] g) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < g.length; i++) { sb.append(Arrays.toString(g[i])); if (i < g.length - 1) sb.append(", "); }
        return sb.append("]").toString();
    }
}
