import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 416: 分割等和子集 (Partition Equal Subset Sum)
 * https://leetcode.cn/problems/partition-equal-subset-sum/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面】（背包组 8 题的入门第一课）
 *
 * <p>■ 题意
 * <p>　给你一个【非空·只含正整数】的数组 nums。问：能不能把它**分成两个子集**
 * <p>　（注意：是把元素**分配**到 A、B 两筐里，每筐至少合法时允许为空吗——
 * <p>　　由于总和固定，一筐空则另一筐=总和，两筐相等要求总和=2×0 不可能，
 * <p>　　所以本题 true 时两筐都非空），两筐的**和相等**？
 *
 * <p>■ 关键转化（题库思路第一课，先把"问题语言"换成"背包语言"再动手）
 * <p>　两筐和相等 ⟺ 每筐各得总和的一半。
 * <p>　→ 先求 sum；若 sum 是**奇数** → 直接 false（半数不存在）
 * <p>　→ 设 target = sum / 2。
 * <p>　→ 问题变体："**能否从 nums 里选出若干个数，使它们的和恰好 = target？**"
 * <p>　　　（一旦选出一半，另一半自动就是另一半 —— 因为全元素和 = 2 × target）
 * <p>　→ 每个 num **只能被选或不选**（不能重复使用）→ 这是【01 背包】的"判定型"：
 * <p>　　　容量 = target，物品 = 各 num（重量=数值、价值无关——只问能不能装满）。
 *
 * <p>■ 输入输出
 * <p>　输入：int[] nums（1 ≤ length ≤ 200；1 ≤ nums[i] ≤ 100）
 * <p>　输出：boolean（能否分割）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums = [1,5,11,5]
 *   sum = 22 → target = 11 → [1,5,5] 和=[11] 两筐各 11 → true
 * nums = [1,2,3,5]
 *   sum = 11 → 奇数 → false（连"分半"都不可能）
 * nums = [1,1]
 *   sum = 2 → target = 1 → [1]&[1] → true
 * nums = [100]
 *   sum = 100 → target = 50 → 谁都不足 50 → false
 * nums = [2,2,3,5]
 *   sum = 12 → target = 6 → 各子集和：4,5,7,8,10,10,5,12...没有 6 → false
 * nums = [1,3,1,1] （奇？ 6 even target 3）→ [1,1,1] 或 [3]？等一下 [3] 和 [1,1,1] 都是 3 → true
 * nums = [99, 1, 4]
 *   sum = 104 → target = 52 → 99 太胖，1+4=5 → false
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium
 * <p>【标签】动态规划(DP) · 0-1 背包 · 数组
 * <p>【题型】01 背包判定型：dp[j] = "能否凑出和 j"；容量倒序遍历是本章**核心机制**
 * <p>【考点】奇偶剪枝；负数不存在（全正）；倒序防止"同一物品被用两次"；dp[0]=true 地基
 * <p>【关联】P494（同型"方案数"）、P322（完全背包"可重复取"，用**正序**——三章最大 contrast）、P1049
 *
 * <p>给非空正整数数组 nums，能否分割成两个和相等的子集。返回 boolean。
 */
public class P416PartitionEqualSubsetSum {

    /** 你的实现（排序+sum 奇剪+max 剪枝 + 01 背包倒序判定表）。 */
    public boolean canPartition(int[] nums) {
        Arrays.sort(nums);
        int sum = 0;
        for (int i : nums) {
            sum += i;
        }
        if (sum % 2 != 0) {
            return false;
        }
        int target = sum / 2;
        if (nums[nums.length - 1] > target) {
            return false;
        }
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int num : nums) {
            for (int j = target; j >= num; j--) {
                dp[j] = dp[j] || dp[j - num];
            }
        }
        return dp[target];
    }

    public static void main(String[] args) {
        P416PartitionEqualSubsetSum solver = new P416PartitionEqualSubsetSum();

        run(solver, new int[]{1, 5, 11, 5}, "官方示例1 简单分配 → true");
        run(solver, new int[]{1, 2, 3, 5}, "官方示例2 sum=11 奇 → false");
        run(solver, new int[]{1, 1}, "最小配对 → true");
        run(solver, new int[]{100}, "超大单元素（target=50 无人可凑）→ false");
        run(solver, new int[]{2, 2, 3, 5}, "均匀但拆不出[2,2,?]6-4=2 不够、3+? 无 1 → false");
        run(solver, new int[]{1, 3, 1, 1}, "拆 [1,1,1] 和 [3] → true");
        run(solver, new int[]{99, 1, 4}, "胖子无解 → false");
        run(solver, new int[]{1, 1, 1, 1}, "四元均分 [1,1]/[1,1] → true");
        run(solver, new int[]{2, 8, 4, 6}, "拆 [8] 和 [2,4,6] 各 12 → true");
        run(solver, new int[]{5, 18, 25, 51, 1}, "sum=100 target=50 拋不出 → false");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：2^n 子集枚举，对每个子集判 sum == half（位掩码，n ≤ 16 内可信）。 */
    private static boolean bruteForce(int[] nums) {
        int total = 0;
        for (int v : nums) total += v;
        if (total % 2 != 0) return false;
        int target = total / 2;
        int n = nums.length;
        if (n > 16) {      // 大数组不走暴力（规模下仍用经典判：表中标记法独立重算）
            return bigArrayCheck(nums, target);
        }
        for (int mask = 0; mask < (1 << n); mask++) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[i];
            }
            if (sum == target) return true;
        }
        return false;
    }

    /** 大数组时的独立校验：规约成【可重置的 DFS 扫描】——不走 DP 表（另机制） */
    private static boolean bigArrayCheck(int[] nums, int target) {
        // 排序+贪心法仅作 sanity，不可靠；此处用 HashSet 能达集合法（机制与 dp 表不同）
        Set<Integer> sums = new HashSet<>();
        sums.add(0);
        for (int v : nums) {
            Set<Integer> next = new HashSet<>(sums);
            for (int s : sums) {
                if (s + v <= target) next.add(s + v);
            }
            sums = next;
            if (sums.contains(target)) return true;
        }
        return sums.contains(target);
    }

    private static void run(P416PartitionEqualSubsetSum solver, int[] nums, String label) {
        try {
            boolean got = solver.canPartition(Arrays.copyOf(nums, nums.length));
            boolean want = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-52s nums=%-20s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-52s nums=%-20s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-52s nums=%-20s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
