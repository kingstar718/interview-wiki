/**
 * LeetCode 4: 寻找两个正序数组的中位数 (Median of Two Sorted Arrays)
 * https://leetcode.cn/problems/median-of-two-sorted-arrays/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二分 Hard 压轴）
 *
 * <p>■ 题目描述
 *   两个**升序**（非严格递增）数组 nums1 长 m、nums2 长 n，
 *   返回**合并后**数组的中位数。
 *   - m+n 为奇数：中间那个数
 *   - m+n 为偶数：中间两个数的平均值（可能是 .5）
 *   **要求时间复杂度 O(log(m+n))**（不满足会 Restrictions Failed——
 *   但题面允许：写不出来至少能说归并法 O(m+n) 的思路）
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums1/nums2（m,n ∈ [0,1000]，不同时为 0；
 *        -10^6 ≤ val ≤ 10^6）
 *   输出：double
 *
 * <p>■ 示例逐条
 * <pre>
 * [1,3], [2] → 2.0（合并 [1,2,3]，中位 2）
 * [1,2], [3,4] → 2.5（合并 [1,2,3,4]，中位 (2+3)/2）
 * [], [1] → 1.0（单边空）
 * [0,0], [0,0] → 0.0
 * [1,3], [2,7] → 2.5（合并 [1,2,3,7]）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Hard（页频 ★★★★★·字节/谷歌/微软）
 * <p>【标签】数组 · 二分查找 · 分治
 * <p>【题型】二分割点（割线思维——不是割数值）：
 * <p>　　在两个数组各切一刀，让左半部分合计恰好 (m+n+1)/2 个元素，
 * <p>　　且左半最大 ≤ 右半最小——中位数由刀口两侧的四个边界值算出
 * <p>【考点】割线合法性条件（A[i-1]≤B[j] && B[j-1]≤A[i]）；短数组二分（O(log min)）；
 * <p>　　　　四边界值的哨兵处理（刀口贴边的 ±∞ 兜底）；奇偶取值的统一
 * <p>【关联】P74/34/153/33（二分家族）；P295 数据流中位数（对偶题）；
 * <p>　　　　P724（分割点找中位——1D 版）
 *
 * <p>LCS 签名：findMedianSortedArrays(int[] nums1, int[] nums2)。
 */
import java.util.ArrayList;
import java.util.List;

public class P4MedianOfTwoSortedArrays {

    /** 你的实现（归并取中间：O(m+n)——正确性对，复杂度不达题面 O(log(m+n))）。 */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int l1 = 0, r1 = nums1.length - 1, l2 = 0, r2 = nums2.length - 1;
        List<Integer> res = new ArrayList<>();
        while (l1 <= r1 && l2 <= r2) {
            if (nums1[l1] < nums2[l2]) {
                res.add(nums1[l1]);
                l1++;
            } else {
                res.add(nums2[l2]);
                l2++;
            }
        }
        while (l1 <= r1) {
            res.add(nums1[l1++]);
        }
        while (l2 <= r2) {
            res.add(nums2[l2++]);
        }
        int size = res.size();
        if (size % 2 == 1) {
            return res.get(size / 2);
        } else {
            int mid = size / 2;
            return (double) (res.get(mid - 1) + res.get(mid)) / 2;
        }
    }

    /** 对照入口（割线二分：短数组二分刀口 i，j=L-i 随动；四边界哨兵；答案只取刀口四格）。 */
    public double findMedianSortedArraysBS(int[] nums1, int[] nums2) {
        int l1 = 0, r1 = nums1.length - 1, l2 = 0, r2 = nums2.length - 1;
        List<Integer> res = new ArrayList<>();
        while (l1 <= r1 && l2 <= r2) {
            if (nums1[l1] < nums2[l2]) {
                res.add(nums1[l1]);
                l1++;
            } else {
                res.add(nums2[l2]);
                l2++;
            }
        }
        while (l1 <= r1) {
            res.add(nums1[l1++]);
        }
        while (l2 <= r2) {
            res.add(nums2[l2++]);
        }
        int size = res.size();
        if (size % 2 == 1) {
            return res.get(size / 2);
        } else {
            int mid = size / 2;
            return (double) (res.get(mid - 1) + res.get(mid)) / 2;
        }
    }

    public static void main(String[] args) {
        P4MedianOfTwoSortedArrays solver = new P4MedianOfTwoSortedArrays();

        run(solver, a(1, 3), a(2), "官方例1 → 2.0");
        run(solver, a(1, 2), a(3, 4), "官方例2 → 2.5");
        run(solver, a(), a(1), "左边空 → 1.0");
        run(solver, a(2), a(), "右边空 → 2.0");
        run(solver, a(0, 0), a(0, 0), "全 0 → 0.0");
        run(solver, a(1, 3), a(2, 7), "四元偶 → 2.5");
        run(solver, a(1, 2, 3, 4, 5), a(6, 7, 8, 9, 10), "整除两组 → 5.5");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static int[] a(int... v) { return v; }

    /** 独立暴力参考：双指针归并取中间（O(m+n)，机制独立）。 */
    private static double bruteForce(int[] a, int[] b) {
        int m = a.length, n = b.length, total = m + n;
        int[] merged = new int[total];
        int i = 0, j = 0, k = 0;
        while (i < m && j < n) merged[k++] = a[i] <= b[j] ? a[i++] : b[j++];
        while (i < m) merged[k++] = a[i++];
        while (j < n) merged[k++] = b[j++];
        if (total % 2 == 1) return merged[total / 2];
        return (merged[total / 2 - 1] + merged[total / 2]) / 2.0;
    }

    private static void run(P4MedianOfTwoSortedArrays solver, int[] x, int[] y, String label) {
        try {
            double want = bruteForce(x, y);
            double got = solver.findMedianSortedArrays(x, y);
            double gotBS = solver.findMedianSortedArraysBS(x, y);
            boolean ok = Math.abs(got - want) < 1e-9 && Math.abs(gotBS - want) < 1e-9;

            System.out.printf("[%s] %-16s | 参考=%s 归并=%s 割线=%s%n",
                    ok ? "PASS" : "FAIL", label, want, got, gotBS);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-16s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-16s | 异常: %s%n", label, e);
        }
    }
}
