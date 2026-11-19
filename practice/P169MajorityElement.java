import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 169: 多数元素 (Majority Element)
 * https://leetcode.cn/problems/majority-element/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 6/50——摩尔投票）
 *
 * <p>■ 题目描述
 *   给一个大小为 n 的数组 nums，返回其中的**多数元素**——出现次数
 *   **大于 ⌊n/2⌋** 的元素（题目**保证存在**，无需处理不存在的情况）。
 *   进阶：O(n) 时间 + O(1) 空间。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ length ≤ 5×10^4；-10^9 ≤ nums[i] ≤ 10^9）
 *   输出：int（多数元素）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[3,2,3] → 3（出现 2 次 > 1）
 * nums=[2,2,1,1,1,2,2] → 2（出现 4 次 > 3）
 * nums=[1] → 1（单元素即多数）
 * nums=[6,5,5] → 5（多数在尾部——候选人中途被换，最终仍归位）★
 * nums=[-1,1,1,-1,1] → 1（负正交替，1 共 3 次 > 2）
 * nums=[4,4,4,4] → 4（全等）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★· Hot100）
 * <p>【标签】数组 · 哈希表 · 分治 · 计数
 * <p>【题型】摩尔投票（Boyer-Moore）：相异抵消——多数票 > n/2，两两抵消后必有剩
 * <p>【考点】候选人与票数两变量；count==0 换候选；多数的"抵消不完"论证（配对上界）；
 * <p>　　　　**题目保证存在 → 无需二次校验**（若不保证则需再数一遍——面试追问点）
 * <p>【关联】P136（异或湮灭——同族：136 相同湮灭 / 169 相异湮灭）；P229 求众数II（> n/3 进阶——未收）
 *
 * <p>LCS 签名：majorityElement(int[] nums)。
 */
public class P169MajorityElement {

    /** 你的实现【Map 计数版】（通用稳解：计数取最大——不依赖"多数存在"，可与摩尔投票对照）。 */
    public int majorityElement(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int n : nums) {
            if (count.containsKey(n)) {
                count.put(n, count.get(n) + 1);
            } else {
                count.put(n, 1);
            }
        }
        int max = 0;
        int maxKey = 0;
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            if (max < value) {
                max = value;
                maxKey = key;
            }
        }
        return maxKey;
    }

    public static void main(String[] args) {
        P169MajorityElement solver = new P169MajorityElement();

        run(solver, new int[]{3, 2, 3}, "官方例1 → 3");
        run(solver, new int[]{2, 2, 1, 1, 1, 2, 2}, "官方例2 → 2");
        run(solver, new int[]{1}, "单元素 → 1");
        run(solver, new int[]{6, 5, 5}, "多数在尾 → 5 ★ 中途换候选");
        run(solver, new int[]{-1, 1, 1, -1, 1}, "负正交替 → 1");
        run(solver, new int[]{4, 4, 4, 4}, "全等 → 4");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 你的实现【摩尔投票版】（相异湮灭：count==0 换候选——与 Map 版双版对照，三机制互验）。 */
    public int majorityVote(int[] nums) {
        int cur = nums[0], count = 1;
        for (int i = 1; i < nums.length; i++) {
            if (count == 0) {
                cur = nums[i];
                count = 1;
            } else if (cur == nums[i]) {
                count++;
            } else {
                count--;
            }
        }
        return cur;
    }

    /** 独立暴力参考：Map 计数取 > n/2 者——机制与摩尔投票完全不同。 */
    private static int bruteForce(int[] nums) {
        java.util.Map<Integer, Integer> cnt = new java.util.HashMap<>();
        for (int n : nums) cnt.merge(n, 1, Integer::sum);
        int half = nums.length / 2;
        for (java.util.Map.Entry<Integer, Integer> e : cnt.entrySet()) {
            if (e.getValue() > half) return e.getKey();
        }
        throw new IllegalStateException("多数不存在（题面保证不会发生）");
    }

    private static void run(P169MajorityElement solver, int[] nums, String label) {
        try {
            int want = bruteForce(nums);
            int gotA = solver.majorityElement(Arrays.copyOf(nums, nums.length));
            int gotB = solver.majorityVote(Arrays.copyOf(nums, nums.length));
            boolean ok = gotA == want && gotB == want;
            String extra = (gotB != want) ? "（摩尔版不一致！）" : "（Map版+摩尔版双过）";

            System.out.printf("[%s] %-22s nums=%-28s | 参考=%d 实得=%d%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, gotA, extra);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-22s nums=%-28s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-22s nums=%-28s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
