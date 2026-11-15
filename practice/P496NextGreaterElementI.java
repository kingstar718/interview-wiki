import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 496: 下一个更大元素 I (Next Greater Element I)
 * https://leetcode.cn/problems/next-greater-element-i/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（单调栈 2/3——739 加一张映射表）
 *
 * <p>■ 题目描述
 *   给两个数组 nums1 和 nums2（**nums1 是 nums2 的子集**，且两数组内均无重复元素）。
 *   对 nums1 的每个元素 x，在 nums2 里找 x 的**右侧第一个更大**的元素值，
 *   作为答案；不存在则 −1。
 *
 * <p>■ 输入输出
 *   输入：int[] nums1（1 ≤ length ≤ nums2.length ≤ 1000）、int[] nums2（无重复）
 *   输出：int[]（与 nums1 等长的答案数组）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums1=[4,1,2], nums2=[1,3,4,2] → [-1,3,-1]
 * 　　（4 的右边无更大 → −1；1 的右边第一个更大是 3；2 的右边无 → −1）
 * nums1=[2,4], nums2=[1,2,3,4] → [3,-1]
 * nums1=[1], nums2=[1] → [-1]（单元素无右邻）
 * nums1=[3,1,2,4], nums2=[1,3,2,4] → [4,3,4,-1]
 * 　　（注意次序：3 的下一个更大是 4 而不是 2——"第一个更大"语义哨兵）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★）
 * <p>【标签】单调栈 · 哈希表
 * <p>【题型】739 四空填法第 2 列：③弹时算什么从"下标差"换成"映射"（map[苦主值]=解救者的值）
 * <p>【考点】两段式结构（先对 nums2 全量单调栈建映射 → 再对 nums1 查表）；值对值映射（存值即可——不需要下标）；
 * <p>　　　　getOrDefault 缺省 −1
 * <p>【关联】P739（原型）；P503（环形版——被砍）；P42（下一题）
 *
 * <p>LCS 签名：nextGreaterElement(int[] nums1, int[] nums2)。
 */
public class P496NextGreaterElementI {

    /** 你的实现（两段式：nums2 建值对值映射 + nums1 查表——③号决策点从下标差换成 map[苦主值]）。 */
    public int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> map = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int n : nums2) {
            while (!stack.isEmpty() && stack.peek() < n) {
                Integer prev = stack.pop();
                map.put(prev, n);
            }
            stack.push(n);
        }
        int[] res = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            res[i] = map.getOrDefault(nums1[i], -1);
        }
        return res;
    }

    public static void main(String[] args) {
        P496NextGreaterElementI solver = new P496NextGreaterElementI();

        run(solver, new int[]{4, 1, 2}, new int[]{1, 3, 4, 2}, "官方例1 → [-1,3,-1]");
        run(solver, new int[]{2, 4}, new int[]{1, 2, 3, 4}, "官方例2 → [3,-1]");
        run(solver, new int[]{1}, new int[]{1}, "单元素 → [-1]");
        run(solver, new int[]{3, 1, 2, 4}, new int[]{1, 3, 2, 4}, "第一个更大语义哨兵 → [4,3,4,-1]");
        run(solver, new int[]{1, 3, 2, 4}, new int[]{1, 3, 2, 4}, "全等集合 → [3,4,4,-1]（参考定）");
        run(solver, new int[]{5, 4, 3, 2}, new int[]{2, 3, 4, 5}, "逆序快查（nums2 中全在右）→ 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：对每个 nums1 元素，在 nums2 里定位后向右线性找第一个更大——O(m·n)。 */
    private static int[] bruteForce(int[] nums1, int[] nums2) {
        int[] res = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            int start = -1;
            for (int j = 0; j < nums2.length; j++) {
                if (nums2[j] == nums1[i]) { start = j; break; }
            }
            res[i] = -1;
            for (int j = start + 1; j < nums2.length; j++) {
                if (nums2[j] > nums1[i]) { res[i] = nums2[j]; break; }
            }
        }
        return res;
    }

    private static void run(P496NextGreaterElementI solver, int[] nums1, int[] nums2, String label) {
        try {
            int[] got = solver.nextGreaterElement(Arrays.copyOf(nums1, nums1.length),
                                                  Arrays.copyOf(nums2, nums2.length));
            int[] want = bruteForce(Arrays.copyOf(nums1, nums1.length),
                                    Arrays.copyOf(nums2, nums2.length));
            boolean ok = Arrays.equals(got, want);

            System.out.printf("[%s] %-38s nums1=%-18s nums2=%-18s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums1), Arrays.toString(nums2),
                    Arrays.toString(want), Arrays.toString(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-38s nums1=%-18s nums2=%-18s | TODO 未实现%n", label,
                    Arrays.toString(nums1), Arrays.toString(nums2));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-38s nums1=%-18s nums2=%-18s | 异常: %s%n", label,
                    Arrays.toString(nums1), Arrays.toString(nums2), e);
        }
    }
}
