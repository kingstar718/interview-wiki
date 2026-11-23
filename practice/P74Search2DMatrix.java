import java.util.Arrays;

/**
 * LeetCode 74: 搜索二维矩阵 (Search a 2D Matrix)
 * https://leetcode.cn/problems/search-a-2d-matrix/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二分组 2/5——拉平一维二分）
 *
 * <p>■ 题目描述
 *   给 m×n 矩阵：
 *   ① 每行从左到右升序；② **每行第一个元素 &gt; 上一行最后一个元素**。
 *   ⟹ 按行展开是一个严格递增的一维数组。判断 target 是否存在。
 *   （与 240 的分野：240 只保证行列各自有序、不保证全局展开有序。）
 *
 * <p>■ 输入输出（约束）
 *   输入：int[][] matrix（m, n ≤ 100；-10^4 ≤ 值 ≤ 10^4）、int target
 *   输出：boolean
 *
 * <p>■ 示例逐条
 * <pre>
 * [[1,3,5,7],[10,11,16,20],[23,30,34,60]], 3 → true（(0,1)）
 * 同矩阵, 13 → false（13 在 11 和 16 之间的缝隙里）
 * 同矩阵, 60 → true（末尾 ★ 边界）
 * [[1]], 1 → true；[[1]], 2 → false
 * [[1,2],[3,4]], 4 → true
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·百度）
 * <p>【标签】数组 · 二分查找 · 矩阵
 * <p>【题型】**拉平一维二分**：行展开递增 → 区间 [0, m*n-1]，mid 映射 row=mid/n, col=mid%n
 * <p>【考点】**为什么可以拉平**（行首&gt;上行尾=无跨行缝隙，与 240 对照）；
 * <p>　　　　一维下标 ↔ 二维坐标映射（整除取行、取模取列）；O(log(mn)) 优于 Z 字 O(m+n)
 * <p>【关联】P240（矩阵版 Z 字——无全局序的对照组，两题天生一对）；P35（二分模板同源）
 *
 * <p>LCS 签名：searchMatrix(int[][] matrix, int target)。
 */
public class P74Search2DMatrix {

    /** 你的实现（拉平一维二分：mid/n 取行、mid%n 取列，O(log(mn)) 合规）。 */
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length;
        int n = matrix[0].length;
        int l = 0;
        int r = m * n - 1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            int row = mid / n;
            int col = mid % n;
            int value = matrix[row][col];
            if (value == target) {
                return true;
            } else if (value > target) {
                r = mid - 1;
            } else {
                l = mid + 1;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        P74Search2DMatrix solver = new P74Search2DMatrix();

        int[][] big = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}};
        run(solver, big, 3, "官方例 → true");
        run(solver, big, 13, "缝隙未中 → false ★");
        run(solver, big, 60, "末尾命中 → true");
        run(solver, big, 1, "开头命中 → true");
        run(solver, big, 0, "小于全阵 → false");
        run(solver, new int[][]{{1}}, 1, "单格命中 → true");
        run(solver, new int[][]{{1}}, 2, "单格未中 → false");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：逐格扫描 O(mn)——机制与拉平二分完全不同。 */
    private static boolean bruteForce(int[][] matrix, int target) {
        for (int[] row : matrix) {
            for (int v : row) {
                if (v == target) return true;
            }
        }
        return false;
    }

    private static void run(P74Search2DMatrix solver, int[][] matrix, int target, String label) {
        try {
            boolean got = solver.searchMatrix(matrix, target);
            boolean want = bruteForce(matrix, target);
            boolean ok = got == want;

            System.out.printf("[%s] %-18s target=%-3d | 参考=%b 实得=%b%n",
                    ok ? "PASS" : "FAIL", label, target, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-18s target=%-3d | TODO 未实现%n", label, target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-18s target=%-3d | 异常: %s%n", label, target, e);
        }
    }
}
