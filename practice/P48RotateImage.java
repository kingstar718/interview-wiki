import java.util.Arrays;

/**
 * LeetCode 48: 旋转图像 (Rotate Image)
 * https://leetcode.cn/problems/rotate-image/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·矩阵组 3/4——转置+翻转）
 *
 * <p>■ 题目描述
 *   给 n×n 矩阵，**顺时针旋转 90°**。必须**原地**旋转（不借助另一个二维数组）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] matrix（n == matrix.length == matrix[i].length；1 ≤ n ≤ 20；
 *         -1000 ≤ 元素 ≤ 1000）——注意是**方阵**
 *   输出：无（原地修改）
 *
 * <p>■ 示例逐条
 * <pre>
 * [[1,2,3],[4,5,6],[7,8,9]] → [[7,4,1],[8,5,2],[9,6,3]]（3×3 官方例）
 * [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]] → [[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]（4×4 官方例2）
 * [[1]] → [[1]]
 * [[1,2],[3,4]] → [[3,1],[4,2]]
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·腾讯/字节）
 * <p>【标签】数组 · 矩阵 · 数学
 * <p>【题型】两步分解：转置（主对角线镜像）+ 每行水平翻转——顺时针 90° = 转置∘左右翻
 * <p>【考点】代数分解的正确性（顺旋 90°= (i,j)→(j, n-1-i)；转置 (i,j)→(j,i)；翻转 (j,i)→(j,n-1-i)）；
 * <p>　　　　转置只扫上三角（j=i+1 起，下三角重复交换会转回去）；**方阵限定**（非方阵没有此分解）
 * <p>【关联】P189 轮转数组（一维版三次反转——同一"分解成可逆操作"思想）；P54（矩阵组 2/4）；P240（矩阵组 4/4）
 *
 * <p>LCS 签名：rotate(int[][] matrix)。
 */
public class P48RotateImage {

    /** 你的实现（完全独立写出：mid 自增实现上三角转置 + 每行双指针对向翻转）。 */
    public void rotate(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int mid = 1;
        for (int i = 0; i < m; i++) {
            for (int j = mid; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
            mid++;
        }
        for (int i = 0; i < m; i++) {
            int l = 0, r = n - 1;
            while (l < r) {
                int temp = matrix[i][l];
                matrix[i][l] = matrix[i][r];
                matrix[i][r] = temp;
                l++;
                r--;
            }
        }
    }

    public static void main(String[] args) {
        P48RotateImage solver = new P48RotateImage();

        run(solver, new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, "3×3 官方例");
        run(solver, new int[][]{{5, 1, 9, 11}, {2, 4, 8, 10}, {13, 3, 6, 7}, {15, 14, 12, 16}}, "4×4 官方例2");
        run(solver, new int[][]{{1}}, "单格 → 不变");
        run(solver, new int[][]{{1, 2}, {3, 4}}, "2×2 → [[3,1],[4,2]]");
        run(solver, new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}}, "4×4 序列矩阵 → 参考定");
        run(solver, new int[][]{{-1, -2}, {-3, -4}}, "负数 → [[-3,-1],[-4,-2]]");
        run(solver, new int[][]{{7, 7}, {7, 7}}, "全同 → 不变");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：直接按映射构造 (i,j)→(n-1-j, i) 再回写——机制与两步分解完全不同。 */
    private static int[][] bruteForce(int[][] matrix) {
        int n = matrix.length;
        int[][] src = new int[n][];
        for (int i = 0; i < n; i++) src[i] = matrix[i].clone();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = src[n - 1 - j][i];
            }
        }
        return matrix;
    }

    private static void run(P48RotateImage solver, int[][] matrix, String label) {
        try {
            int[][] got = copyOf(matrix);
            solver.rotate(got);
            int[][] want = bruteForce(copyOf(matrix));
            boolean ok = Arrays.deepEquals(got, want);

            System.out.printf("[%s] %-14s | 参考=%s%n", ok ? "PASS" : "FAIL", label, fmt(want));
            if (!ok) System.out.printf("       实得=%s%n", fmt(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-14s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-14s | 异常: %s%n", label, e);
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
