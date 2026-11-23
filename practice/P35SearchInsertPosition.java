import java.util.Arrays;

/**
 * LeetCode 35: 搜索插入位置 (Search Insert Position)
 * https://leetcode.cn/problems/search-insert-position/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二分查找组 1/5——704 同构+插入位）
 *
 * <p>■ 题目描述
 *   给升序**无重复**整型数组 nums 和 target：
 *   存在 → 返回其下标；不存在 → 返回它**按顺序插入的位置**。
 *   要求 O(log n)。必须用二分。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ n ≤ 10^4；严格升序无重复）、int target（-10^4 ≤ x ≤ 10^4）
 *   输出：int（下标 0..n，注意**插入位可到 n**——插到末尾之后）
 *
 * <p>■ 示例逐条
 * <pre>
 * [1,3,5,6], 5 → 2（命中）
 * [1,3,5,6], 2 → 1（插中间）
 * [1,3,5,6], 7 → 4（插末尾之后 ★ 可到 n）
 * [1,3,5,6], 0 → 0（插开头 ★）
 * [1], 0 → 0；[1], 2 → 1
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★·字节/美团）
 * <p>【标签】数组 · 二分查找
 * <p>【题型】左闭右闭二分（704 同构）——没找到时 return l（= 第一个 > target 的下标 = 插入位）
 * <p>【考点】**退出态语义**（l==r+1 时 [0,r] 全 &lt; target、[l,n-1] 全 &gt; target，l 恰是插入位）；
 * <p>　　　　返回 l 而非 -1——"查找左边界"族的心法原型
 * <p>【关联】P704 二分查找（同构母题）；P74（二分组 2/5）；"查找左边界"族（34/69/278…）
 *
 * <p>LCS 签名：searchInsert(int[] nums, int target)。
 */
public class P35SearchInsertPosition {

    /** 你的实现（标准左闭右闭 l<=r 二分，退出 return l 即插入位）。 */
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int value = nums[mid];
            if (target == value) {
                return mid;
            } else if (value < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        P35SearchInsertPosition solver = new P35SearchInsertPosition();

        run(solver, new int[]{1, 3, 5, 6}, 5, "命中 → 2");
        run(solver, new int[]{1, 3, 5, 6}, 2, "插中间 → 1");
        run(solver, new int[]{1, 3, 5, 6}, 7, "插末尾 → 4 ★ 可到 n");
        run(solver, new int[]{1, 3, 5, 6}, 0, "插开头 → 0 ★");
        run(solver, new int[]{1}, 0, "单元素小 → 0");
        run(solver, new int[]{1}, 2, "单元素大 → 1");
        run(solver, new int[]{-4, -1, 3}, -1, "负数命中 → 1");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：线性找第一个 ≥ target 的下标——机制与二分不同。 */
    private static int bruteForce(int[] nums, int target) {
        int i = 0;
        while (i < nums.length && nums[i] < target) i++;
        return i;
    }

    private static void run(P35SearchInsertPosition solver, int[] nums, int target, String label) {
        try {
            int got = solver.searchInsert(nums, target);
            int want = bruteForce(nums, target);
            boolean ok = got == want;

            System.out.printf("[%s] %-16s target=%-3d | 参考=%d 实得=%d nums=%s%n",
                    ok ? "PASS" : "FAIL", label, target, want, got, Arrays.toString(nums));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-16s target=%-3d | TODO 未实现%n", label, target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-16s target=%-3d | 异常: %s%n", label, target, e);
        }
    }
}
