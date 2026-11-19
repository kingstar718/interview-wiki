import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 560: 和为 K 的子数组 (Subarray Sum Equals K)
 * https://leetcode.cn/problems/subarray-sum-equals-k/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 10/50——前缀和+哈希计数）
 *
 * <p>■ 题目描述
 *   给整数数组 nums 和整数 k，统计并返回该数组中**和恰为 k** 的
 *   **连续子数组**的个数（子数组 = 原数组中一段连续元素，个数可多可少）。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ length ≤ 2×10^4；-1000 ≤ nums[i] ≤ 1000，**有负数**）、int k（≤ 10^6）
 *   输出：int（和为 k 的连续子数组个数）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[1,1,1], k=2 → 2（[0,1] 和 [1,2] 两段 [1,1]）
 * nums=[1,2,3], k=3 → 2（[1,2] 和 [3]）
 * nums=[1], k=0 → 0（无）
 * nums=[-1,-1,1], k=0 → 1（[-1,-1+1]? 不：[-1,-1,1] 无和 0 的？有：[-1,-1,1] 全体=−1；[-1,-1]=−2；[-1,1]=0 ✓ 1 个）
 * nums=[3,4,7,2,-3,1,4,2], k=7 → 4（3,4 / 7 / 7,2,-3,1 / 4,-3,1,... 暴力可证）
 * nums=[0,0,0], k=0 → 6（三个 0 的所有子数组 [0],[0],[0],[0,0],[0,0],[0,0,0]——计数型重复陷阱）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/美团）
 * <p>【标签】数组 · 哈希表 · 前缀和
 * <p>【题型】前缀和 + 哈希计数：pre[j]-pre[i]=k → 查 pre-k 出现次数累加（**计数型 +=**，非判定型 if）
 * <p>【考点】map.put(0,1) 地基（空前缀——从 0 开头的子数组）；**为什么不能滑动窗口**（有负数——
 * <p>　　　　窗口和与长度不同单调）；计数型 map（getOrDefault += 0 语义——0 也入账）
 * <p>【关联】P1 TwoSum（前缀和版孪生）；P303（前缀和判定型）；P494 目标和（计数型 += 同族）
 *
 * <p>LCS 签名：subarraySum(int[] nums, int k)。
 */
public class P560SubarraySumEqualsK {

    /** 你的实现（前缀和+哈希计数：查 pre-k 出现次数累加；put(0,1) 地基+先查后记）。 */
    public int subarraySum(int[] nums, int k) {
        int count = 0;
        int prefixSum = 0;
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);

        for (int num : nums) {
            prefixSum += num;
            count += map.getOrDefault(prefixSum - k, 0);
            map.merge(prefixSum, 1, Integer::sum);
        }
        return count;
    }

    public static void main(String[] args) {
        P560SubarraySumEqualsK solver = new P560SubarraySumEqualsK();

        run(solver, new int[]{1, 1, 1}, 2, "官方例1 → 2");
        run(solver, new int[]{1, 2, 3}, 3, "官方例2 → 2");
        run(solver, new int[]{1}, 0, "无匹配 → 0");
        run(solver, new int[]{-1, -1, 1}, 0, "负数窗口 → 1");
        run(solver, new int[]{3, 4, 7, 2, -3, 1, 4, 2}, 7, "多段 → 参考定");
        run(solver, new int[]{0, 0, 0}, 0, "全 0 → 6 ★ 计数重复陷阱");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举所有 (i,j) 求和计数 O(n³→前缀 O(n²))——机制完全不同。 */
    private static int bruteForce(int[] nums, int k) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum == k) count++;
            }
        }
        return count;
    }

    private static void run(P560SubarraySumEqualsK solver, int[] nums, int k, String label) {
        try {
            int got = solver.subarraySum(Arrays.copyOf(nums, nums.length), k);
            int want = bruteForce(nums, k);
            boolean ok = got == want;

            System.out.printf("[%s] %-24s nums=%-32s k=%-3d | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), k, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s nums=%-32s k=%-3d | TODO 未实现%n", label, Arrays.toString(nums), k);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s nums=%-32s k=%-3d | 异常: %s%n", label, Arrays.toString(nums), k, e);
        }
    }
}
