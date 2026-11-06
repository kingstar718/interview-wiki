import java.util.Arrays;

/**
 * LeetCode 45: 跳跃游戏 II (Jump Game II)
 * https://leetcode.cn/problems/jump-game-ii/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · BFS 分层视角 · 数组
 * <p>【题型】同 55 地图换问法：最少跳跃次数；greedy 按"层"跳（curEnd/nextFarthest 双边界）
 * <p>【考点】steps++ 时机（走到本层边界时）；curEnd 与 nextFarthest 分家（55 只有一个 maxReach）；终点在本层白酒早退
 * <p>【关联】P55（可达性：单 maxReach）；BFS 分层！
 *
 * <p>非负整数数组 nums（【保证可达】），从下标 0 出发，nums[i] 为最大可跳步数，
 * 求到达最后一个下标的最少跳跃次数。
 *
 * <p>示例：[2,3,1,1,4] → 2（0→1→4）；[1,1,1,1] → 3；[0,...? 题保证可达但单元素 0 可达且 0 步]
 * <p>约束：1 &lt;= nums.length &lt;= 10^4；0 &lt;= nums[i] &lt;= 1000
 */
public class P45JumpGameII {

    /** 你的实现（BFS 按层贪心：双边界 curEnd/maxReach + 边界结算 + 早 break）。 */
    public int jump(int[] nums) {
        int jumps = 0;
        int curEnd = 0;
        int maxReach = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            maxReach = Math.max(maxReach, i + nums[i]);
            if (i == curEnd) {
                jumps++;
                curEnd = maxReach;
                if (curEnd >= nums.length - 1) {
                    break;
                }
            }
        }
        return jumps;
    }

    public static void main(String[] args) {
        P45JumpGameII solver = new P45JumpGameII();

        run(solver, new int[]{2, 3, 1, 1, 4}, "示例1 官方 → 2（0→1→4）");
        run(solver, new int[]{2, 3, 0, 1, 4}, "官方变体 → 2");
        run(solver, new int[]{1, 1, 1, 1}, "全 1 一步一步 → 3");
        run(solver, new int[]{1}, "单元素 → 0（已在终点）");
        run(solver, new int[]{5, 1, 1, 1, 1, 1}, "首跳直达 → 1");
        run(solver, new int[]{3, 4, 3, 2, 6, 1}, "乱序经典（可达保证）→ 2？暴力定");
        run(solver, new int[]{2, 1, 1, 1, 4}, "束深 → 3 还是 4？暴力定");
        run(solver, new int[]{1, 3, 5, 8, 9, 2, 6, 7, 6, 8, 9}, "100 元素压力的缩小版 → 3");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：BFS 从 0 展开到可达集合扩展，返回"最少层数"（机制与贪心无关，O(n²)）。 */
    private static int bruteForce(int[] nums) {
        int n = nums.length;
        if (n == 1) return 0;
        boolean[] visited = new boolean[n];
        java.util.List<Integer> cur = new java.util.ArrayList<>();
        cur.add(0);
        visited[0] = true;
        int steps = 0;
        while (!cur.isEmpty()) {
            steps++;
            java.util.List<Integer> next = new java.util.ArrayList<>();
            for (int i : cur) {
                for (int step = 1; step <= nums[i]; step++) {
                    int to = i + step;
                    if (to >= n - 1) return steps;   // 从任何当前层位一口气到终点
                    if (!visited[to]) {
                        visited[to] = true;
                        next.add(to);
                    }
                }
            }
            cur = next;
        }
        return -1;   // 不可达（题保证不会发生）
    }

    private static void run(P45JumpGameII solver, int[] nums, String label) {
        try {
            int got = solver.jump(Arrays.copyOf(nums, nums.length));
            int want = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-40s nums=%-30s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-40s nums=%-30s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-40s nums=%-30s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
