import java.util.Arrays;

/**
 * LeetCode 55: 跳跃游戏 (Jump Game)
 * https://leetcode.cn/problems/jump-game/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · 数组
 * <p>【题型】可达性判定：维护"最远可达位置"，扫到某格时它必须在范围内，否则后面全靠不上
 * <p>【考点】maxReach = max(maxReach, i + nums[i])；淘汰点 i > maxReach 提前 false；返回值是"能不能"不是步数
 * <p>【关联】P45（跳跃 II：问最少几步，按层跳——同地图不同问法）；P121/122（股票线后跳跃线）
 *
 * <p>非负整数数组 nums，开始位于下标 0，nums[i] 代表在该位置最多可向前跳的步数。
 * 判断是否能够到达最后一个下标。
 *
 * <p>示例：[2,3,1,1,4] → true；[3,2,1,0,4] → false（卡在下标 3）
 * <p>约束：1 &lt;= nums.length &lt;= 10^4；0 &lt;= nums[i] &lt;= 10^5
 */
public class P55JumpGame {

    /** 你的实现（maxReach 覆盖竞赛：踩不着→false，够到终点→早 true）。 */
    public boolean canJump(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) {
                return false;
            }
            maxReach = Math.max(maxReach, i + nums[i]);
            if (maxReach >= nums.length - 1) {
                return true;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        P55JumpGame solver = new P55JumpGame();

        run(solver, new int[]{2, 3, 1, 1, 4}, "示例1 官方 → true");
        run(solver, new int[]{3, 2, 1, 0, 4}, "示例2 官方 → false（卡 3）");
        run(solver, new int[]{0}, "单元素 0 → true（已站在终点）");
        run(solver, new int[]{2, 0, 0}, "中段有 0 但可跨过 → true");
        run(solver, new int[]{0, 2, 3}, "首格即 0 → false（出生即困死）");
        run(solver, new int[]{3, 2, 1, 0, 4}, "重复校验官方 false");
        run(solver, new int[]{2, 5, 0, 0}, "中途大跳跨过双 0 → true");
        run(solver, new int[]{1, 0, 1, 0}, "卡在 1（0 后面有路但到不了）→ false");
        run(solver, new int[]{5, 0, 0, 0, 0, 0, 0, 0}, "首跳 5 刚好坏不过第 5 个 0");
        run(solver, new int[]{4, 4, 4, 0, 4}, "借力保举 → true");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：从每一格回推能站的位置 BFS 展开（O(n²)，机制与贪心无关）。 */
    private static boolean bruteForce(int[] nums) {
        int n = nums.length;
        boolean[] can = new boolean[n];
        can[0] = true;
        for (int i = 0; i < n; i++) {
            if (!can[i]) continue;
            for (int step = 1; step <= i + nums[i]; step++) {
                if (step < n) can[step] = true;       // 注意：站上 i 后能到 [i+1 .. i+nums[i]]
            }
        }
        return can[n - 1];
    }

    private static void run(P55JumpGame solver, int[] nums, String label) {
        try {
            boolean got = solver.canJump(Arrays.copyOf(nums, nums.length));
            boolean want = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-40s nums=%-28s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-40s nums=%-28s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-40s nums=%-28s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
