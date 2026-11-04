import java.util.Arrays;

/**
 * LeetCode 376: 摆动序列 (Wiggle Subsequence)
 * https://leetcode.cn/problems/wiggle-subsequence/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · 数组
 * <p>【题型】统计"极值拐点"：单调坡上删哪个元素答案不变，只有方向反转时才 +1
 * <p>【考点】prediff/curdiff 异号判定；平坡（curdiff==0）跳过不更新 prediff；首元素自带 count=1
 * <p>【关联】贪心入门三件套之二（455 配对 → 376 拐点计数 → 53 前缀结构）
 *
 * <p>连续数字之间的差值严格正负交替 = 摆动序列。第一个差可正可负。
 * 只有一个元素或两个不等元素也算。返回最长摆动【子序列】的长度（允许删元素、保持相对顺序）。
 *
 * <p>示例：[1,7,4,9,2,5] → 6（本身摆动）；[1,17,5,10,13,15,10,5,16,8] → 7；[1,2,3,...,9]（全升）→ 2
 * <p>约束：1 &lt;= nums.length &lt;= 1000；0 &lt;= nums[i] &lt;= 1000
 */
public class P376WiggleSubsequence {

    /** 你的实现（prediff/curdiff 异号判定 + 平坡透明化 + count 初值 1）。 */
    public int wiggleMaxLength(int[] nums) {
        int preDiff = 0;
        int count = 1;
        if (nums.length <= 1) {
            return nums.length;
        }
        for (int i = 1; i < nums.length; i++) {
            int curDiff = nums[i] - nums[i - 1];
            if (preDiff >= 0 && curDiff < 0) {
                count++;
                preDiff = curDiff;
            } else if (preDiff <= 0 && curDiff > 0) {
                count++;
                preDiff = curDiff;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        P376WiggleSubsequence solver = new P376WiggleSubsequence();

        run(solver, new int[]{1, 7, 4, 9, 2, 5}, "示例1 全程摆动 → 6");
        run(solver, new int[]{1, 17, 5, 10, 13, 15, 10, 5, 16, 8}, "示例2 删中间 → 7");
        run(solver, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, "示例3 全升 → 2");
        run(solver, new int[]{1}, "单元素 → 1");
        run(solver, new int[]{1, 1}, "两个相等 → 1");
        run(solver, new int[]{1, 1, 1, 1}, "全相等 → 1");
        run(solver, new int[]{5, 5, 5, 4, 4, 3, 3}, "平台缩级 5,5,5,4,4,3,3 → 降列只会 2？暴力定");
        run(solver, new int[]{1, 2, 2, 2, 1}, "升-平-降 1,2,2,2,1 → [1,2,1] 3");
        run(solver, new int[]{3, 3, 3, 2, 5}, "平后降再升 3,3,3,2,5 → [3,2,5] 3");
        run(new int[0], "空数组 → 0（LeetCode 约束 nums.length>=1 但防御）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举 2^n 子序列依次验证摆动性，取最长（n <= 12 的用例才可靠）。 */
    private static int bruteForce(int[] nums) {
        int n = nums.length;
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            int prev = -1;
            int len = 0;
            boolean ok = true;
            int lastVal = 0;
            int dir = 0;   // 0 未定，1 上，-1 下
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) == 0) continue;
                if (len == 0) {
                    lastVal = nums[j];
                    len = 1;
                } else {
                    int d = nums[j] - lastVal;
                    if (d == 0) { ok = false; break; }
                    int nd = d > 0 ? 1 : -1;
                    if (dir != 0 && dir == nd) { ok = false; break; }
                    dir = nd;
                    lastVal = nums[j];
                    len++;
                }
            }
            if (ok) best = Math.max(best, len);
        }
        return best;
    }

    private static void run(Object o, int[] nums) {
        // 占位重载防误用
    }

    private static P376WiggleSubsequence lastSolver;

    private static void run(int[] empty, String label) {
        runWith(lastSolver, empty, label);
    }

    private static void run(P376WiggleSubsequence solver, int[] nums, String label) {
        lastSolver = solver;
        runWith(solver, nums, label);
    }

    private static void runWith(P376WiggleSubsequence solver, int[] nums, String label) {
        try {
            int got = solver.wiggleMaxLength(Arrays.copyOf(nums, nums.length));
            int bf = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == bf;

            System.out.printf("[%s] %-34s nums=%-30s | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), bf, got);
            if (!ok) {
                System.out.println("    ---- 暴力最长摆动子序列长度=" + bf + "，你的实现给了 " + got);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s nums=%-30s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s nums=%-30s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
