import java.util.Arrays;

/**
 * LeetCode 240: 搜索二维矩阵 II (Search a 2D Matrix II)
 * https://leetcode.cn/problems/search-a-2d-matrix-ii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·矩阵组 4/4 收官——Z 字走位）
 *
 * <p>■ 题目描述
 *   给 m×n 矩阵：**每行从左到右升序**、**每列从上到下升序**。
 *   判断 target 是否在矩阵中。注意与 74 题不同：**不保证**每行首元素大于
 *   上一行末元素（全局不完全有序——不能拉平做一维二分）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] matrix（m, n ≤ 300；-10^9 ≤ 值 ≤ 10^9）、int target
 *   输出：boolean
 *
 * <p>■ 示例逐条
 * <pre>
 * 官方矩阵（5×5），target=5 → true（(1,1)）
 * 官方矩阵，target=20 → false
 * [[1]], target=1 → true；target=2 → false
 * [[1,3],[2,5]], target=5 → true；target=4 → false（4 不在）
 * [[1,2],[1,3]] → 不满足约束？——列升序 1<3 行升序 1,2/1,3 ✓（2 和 1 重复不冲突）
 * [[-5]], target=-5 → true（负数边界）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/美团）
 * <p>【标签】二分查找 · 分治 · 矩阵
 * <p>【题型】Z 字走位（右上角出发）：小→下移整行排除、大→左移整列排除——每步排除一行或一列
 * <p>【考点】**起点选择**（右上/左下两个"鞍点"——行列单调方向相反的角）；排除论证；
 * <p>　　　　**为什么不能拉平二分**（行首不大于上行尾——全局序不存在）；步数上界 m+n
 * <p>【关联】P74 搜索二维矩阵（拉平二分版——全局有序）；P54/48/73（矩阵组同门）
 *
 * <p>LCS 签名：searchMatrix(int[][] matrix, int target)。
 */
public class P240Search2DMatrixII {

    /** 你的实现（Z 字走位：右上角鞍点出发，小下行/大左列，走出界即 false）。 */
    public boolean searchMatrix(int[][] matrix, int target) {
        int right = matrix[0].length - 1;
        int top = 0;
        while (top < matrix.length && right >= 0) {
            int value = matrix[top][right];
            if (value == target) {
                return true;
            } else if (value < target) {
                top++;
            } else {
                right--;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        P240Search2DMatrixII solver = new P240Search2DMatrixII();

        int[][] big = {{1, 4, 7, 11, 15}, {2, 5, 8, 12, 19}, {3, 6, 9, 16, 22},
                       {10, 13, 14, 17, 24}, {18, 21, 23, 26, 30}};
        run(solver, big, 5, "官方例 → true");
        run(solver, big, 20, "官方例 → false");
        run(solver, new int[][]{{1}}, 1, "单格命中 → true");
        run(solver, new int[][]{{1}}, 2, "单格未中 → false");
        run(solver, new int[][]{{1, 3}, {2, 5}}, 5, "2×2 命中 → true");
        run(solver, new int[][]{{1, 3}, {2, 5}}, 4, "2×2 未中 → false");
        run(solver, new int[][]{{-5}}, -5, "负数边界 → true");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：逐格扫描 O(mn)——机制与 Z 字排除完全不同。 */
    private static boolean bruteForce(int[][] matrix, int target) {
        for (int[] row : matrix) {
            for (int v : row) {
                if (v == target) return true;
            }
        }
        return false;
    }

    private static void run(P240Search2DMatrixII solver, int[][] matrix, int target, String label) {
        try {
            boolean got = solver.searchMatrix(matrix, target);
            boolean want = bruteForce(matrix, target);
            boolean ok = got == want;

            System.out.printf("[%s] %-24s target=%-4d | 参考=%b 实得=%b%n",
                    ok ? "PASS" : "FAIL", label, target, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s target=%-4d | TODO 未实现%n", label, target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s target=%-4d | 异常: %s%n", label, target, e);
        }
    }
}
