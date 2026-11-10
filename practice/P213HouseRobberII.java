import java.util.Arrays;

/**
 * LeetCode 213: 打家劫舍 II (House Robber II)
 * https://leetcode.cn/problems/house-robber-ii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（打家劫舍 2/3——环形版）
 * <p>■ 题目描述
 *   你是一个小偷，要偷一排房屋里的现金。这排房屋的特殊之处：
 *   它们【首尾相连围成一个圈】——也就是说，除了普通相邻关系（第 i 栋与
 *   第 i+1 栋挨着）之外，最后一栋（下标 n-1）和第一栋（下标 0）也【挨着】。
 * ■ 规则
 *   1) 相邻两栋房屋不能同时偷（报警器联动），这里包括【首尾对（0 号 和 n-1 号）】；
 *   2) 每栋房屋要么偷、要么不偷；
 *   3) 求能偷到的最大总金额（所有被偷房屋的 nums[i] 之和最大）。■ 输入输出
 *   输入 int[] nums（1 ≤ length ≤ 100; 0 ≤ nums[i] ≤ 1000），非空
 *   输出 int——最大金额；不存在约束保证是可行性（至少能不偷 → 答案 ≥ 0）。
 * ■ 示例（本文件 9 例自表；这里给官方三条）
 *   例 1: nums=[2,3,2] → 3：0 号和 2 号在环上互为邻居，不能同时偷 → 只能取 max(单0=2, 单2=2, 偷1=3) = 3
 *   例 2: nums=[1,2,3,1] → 4：偷 1+3
 *   例 3: nums=[1,2,3] → 3：环形禁令使"3+1"、"3+2" 首尾非法 → 单偷中段
 *
 *
 * <p>■ 关键转化提示（环形 → 两段线性，方案原话"拆成两段_linear各跑一次"——自足概念见课堂 / chat）
 * <pre>
 *   首 和 尾 相邻 → 两者**不能同时偷** → 全体方案按"首偷不偷"一分为二：
 *     世界 A：偷 0 号 → 最后一栋必不被偷 → 独立世界 = 198 在 [0..n-2] 上跑一趟
 *     世界 B：不偷 0 号 → 最后一栋自由  → 独立世界 = 198 在 [1..n-1] 上跑一趟
 *   两世界无交集（0 号偷/不偷互败），并起来 = 全方案 → answer = max(A, B)
 * </pre>
 * <p>■ 输入输出
 * <p>　输入：int[] nums（1 ≤ length ≤ 100；0 ≤ nums[i] ≤ 1000）
 * <p>　输出：int（最大金额）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[2,3,2]      → 3　（首尾相邻：0 号和 2 号不能同偷 → max(单0,偷1,单2)=3? 手推：
 *                        世界A [0..1]=max(2,3)=3；世界B [1..2]=max(3,2)=3 → 3）
 * nums=[1,2,3,1]    → 4　（同 198；世界A [0..2]：偷0+2=4；世界B [1..3]：偷1号2+3号1=3 → 4）
 * nums=[1,2,3]      → 3　（世界A [0..1]=max(1,2)=2；世界B [1..2]=max(2,3)=3 → 3，等于"单偷中间"）
 * nums=[200,3,140,20,10] → 340（0 号 + 2 号 = 340；不偷 0 的世界 [1..4]=3+20=23 → 340）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】动态规划(DP) · 环形拆分 · 线性 DP 复用
 * <p>【题型】两段 198 线性取 max；robRange(nums, l, r) 的通用线性扫描
 * <p>【考点】环形拆解（首尾互斥二分世界）；【n=1 的特殊】（只有一个房子时 lo=hi=0，"去头去尾"
 * <p>　　　的两个区间重合要容——首尾"同一栋不存在邻居"，**直接 return nums[0]**）；
 * <p>　　　∅ 空段不可能（nums 非空）
 * <p>【关联】P198（线性原型）；P337（树形态——返回 rob/skip 二元组）
 *
 * <p>LCS 签名：rob(int[] nums)。环形房屋相邻不能都偷，返回最大金额。
 */
public class P213HouseRobberII {

    /** 你的实现（环形拆两段线性：robRange 双调用 + 区间闭右界扫描）。 */
    public int rob(int[] nums) {
        if (nums.length == 1) return nums[0];
        return Math.max(robRange(nums, 0, nums.length - 2), robRange(nums, 1, nums.length - 1));
    }

    private int robRange(int[] nums, int l, int r) {
        int prev2 = 0, prev1 = 0;
        for (int i = l; i <= r; i++) {
            int cur = Math.max(prev2 + nums[i], prev1);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    public static void main(String[] args) {
        P213HouseRobberII solver = new P213HouseRobberII();

        run(solver, new int[]{2, 3, 2}, "官方示例1 环形 → 3（0/2 互斥）");
        run(solver, new int[]{1, 2, 3, 1}, "官方示例2 同 198 → 4");
        run(solver, new int[]{1, 2, 3}, "奇数环 → 3（单偷中立栋）");
        run(solver, new int[]{200, 3, 140, 20, 10}, "首栋大头 → 340（320+ 20? 手推 200+140）");
        run(solver, new int[]{1}, "单栋 → 1（环的自己≠邻居）★ 边界");
        run(solver, new int[]{2}, "两栋只有一对邻居 → 2 ★ 边界");
        run(solver, new int[]{0, 0}, "两邻 → 0");
        run(solver, new int[]{1, 3, 1, 3, 100}, "尾栋大头 → 参考定");
        run(solver, new int[]{2, 7, 9, 3, 1}, "198 例2 环形（参考定）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：
     * 小数组 → 2^n 回溯"偷/不偷"序列 + 首尾禁令检查（机制：全枚举，与拆两段线性完全不同）。
     * 大数组 → 排序后线性"爆栈哨兵"? 不——用贪心从高到低可行性检验不可靠；改用
     * 记忆化 DFS 环形特例（起点定首栋偷/不偷三态展开）依旧较独立。
     */
    private static int bruteForce(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];      // 单栋：无邻居（环的自己≠邻居）
        if (n <= 14) {                       // 2^14 = 16384 掩码全枚举（含首尾禁令判定）
            int best = 0;
            for (int mask = 0; mask < (1 << n); mask++) {
                int sum = 0;
                boolean ok = true;
                for (int i = 0; i < n; i++) {
                    if ((mask & (1 << i)) != 0) {
                        sum += nums[i];
                        if ((mask & (1 << ((i + 1) % n))) != 0) { ok = false; break; }  // 环形邻居
                    }
                }
                if (ok) best = Math.max(best, sum);
            }
            return best;
        }
        // 大数组：两段线性参考（与用户解同机制——独立性由上面 n≤14 位掩码全枚举扛住）
        return Math.max(robRangeRef(nums, 0, n - 2), robRangeRef(nums, 1, n - 1));
    }

    private static int robRangeRef(int[] nums, int l, int r) {
        int prev2 = 0, prev1 = 0;
        for (int i = l; i <= r; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }

    private static void run(P213HouseRobberII solver, int[] nums, String label) {
        try {
            int got = solver.rob(Arrays.copyOf(nums, nums.length));
            int want = bruteForce(Arrays.copyOf(nums, nums.length));
            boolean ok = got == want;

            System.out.printf("[%s] %-38s nums=%-24s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-38s nums=%-24s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-38s nums=%-24s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
