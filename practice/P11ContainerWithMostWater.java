import java.util.Arrays;

/**
 * LeetCode 11: 盛最多水的容器 (Container With Most Water)
 * https://leetcode.cn/problems/container-with-most-water/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 16/50——对撞指针·剪枝正确性）
 *
 * <p>■ 题目描述
 *   n 条垂线，第 i 条高度 height[i]，选**两条**线与 x 轴构成容器，
 *   求能盛的**最多水**。面积 = min(两线高) × 两线下标距离。
 *   （不能倾斜容器；板宽忽略）
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] height（2 ≤ length ≤ 10^5；0 ≤ height[i] ≤ 10^4）
 *   输出：int（最大面积）
 *
 * <p>■ 示例逐条
 * <pre>
 * height=[1,8,6,2,5,4,8,3,7] → 49（下标 1 和 8：宽 7 × min(8,7)=7）
 * height=[1,1] → 1（最小形状）
 * height=[4,3,2,1,4] → 16（两端 4：宽 4 × min=4）
 * height=[1,2,1] → 2（中高两侧矮：min(1,1)×2=2 vs min(2,1)×1=1 → 2）
 * height=[2,3,4,5,18,17,6] → 17（18 与 6：宽 5×min=6? 不——18@4 与 17@5... 暴力可证）
 * height=[0,0] → 0（零高线）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·腾讯/字节）
 * <p>【标签】贪心 · 双指针（对撞）· 数组
 * <p>【题型】对撞指针+剪枝：首尾最宽起步，每步记面积、移矮侧——"矮板所有组合一次性排除"的论证
 * <p>【考点】**移动矮板的安全论证**（面积=min(h[l],h[r])×(r-l)：保留矮板则 min 不可能增，
 * <p>　　　　而宽度必减——矮板为边的所有候选全被当前记录覆盖）；相等时移哪边都对的对称性；
 * <p>　　　　与 42 的分野（42 是"接水总量"、11 是"单容器面积"——模板同为对撞但故事不同）
 * <p>【关联】P42 接雨水（同族对撞——矮侧先行同源）；P15 三数之和（对撞同款骨架）
 *
 * <p>LCS 签名：maxArea(int[] height)。
 */
public class P11ContainerWithMostWater {

    /** 你的实现（对撞+剪枝：min(两高)×下标差先记账，后移矮板——矮板组合一次性排除）。 */
    public int maxArea(int[] height) {
        int l = 0, r = height.length - 1;
        int max = 0;
        while (l < r) {
            max = Math.max(max, Math.min(height[l], height[r]) * (r - l));
            if (height[l] < height[r]) {
                l++;
            } else {
                r--;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        P11ContainerWithMostWater solver = new P11ContainerWithMostWater();

        run(solver, new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}, "官方例1 → 49");
        run(solver, new int[]{1, 1}, "最小 → 1");
        run(solver, new int[]{4, 3, 2, 1, 4}, "两端高 → 16");
        run(solver, new int[]{1, 2, 1}, "中高 → 2");
        run(solver, new int[]{2, 3, 4, 5, 18, 17, 6}, "多峰 → 参考定");
        run(solver, new int[]{0, 0}, "零高 → 0");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：枚举全部 C(n,2) 组合取面积最大——O(n²)，机制与对撞剪枝完全不同。 */
    private static int bruteForce(int[] height) {
        int best = 0;
        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                best = Math.max(best, Math.min(height[i], height[j]) * (j - i));
            }
        }
        return best;
    }

    private static void run(P11ContainerWithMostWater solver, int[] height, String label) {
        try {
            int got = solver.maxArea(Arrays.copyOf(height, height.length));
            int want = bruteForce(height);
            boolean ok = got == want;

            System.out.printf("[%s] %-24s height=%-34s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(height), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s height=%-34s | TODO 未实现%n", label, Arrays.toString(height));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s height=%-34s | 异常: %s%n", label, Arrays.toString(height), e);
        }
    }
}
