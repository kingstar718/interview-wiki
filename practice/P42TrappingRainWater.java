import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * LeetCode 42: 接雨水 (Trapping Rain Water)
 * https://leetcode.cn/problems/trapping-rain-water/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（单调栈 3/3 收官——🔴 Top6 硬题）
 *
 * <p>■ 题目描述
 *   给 n 个非负整数表示每个宽度为 1 的柱子的高度图
 *   （height[i] 是第 i 根柱子的高度，0 表示该位置没有柱子）。
 *   计算按此排列的柱子，下雨之后【能接多少雨水】（每格水宽 1、
 *   水位由两侧更高的柱子拦住；单位为 1×1 的格子数）。
 *
 * <p>■ 输入输出
 *   输入：int[] height（0 ≤ length ≤ 2×10^4；0 ≤ height[i] ≤ 10^5）
 *   输出：int（接水总量）
 *
 * <p>■ 示例逐条
 * <pre>
 * height=[0,1,0,2,1,0,1,3,2,1,2,1] → 6（官方：洼地在 2/4/5/6/9/10 各格积水）
 * height=[4,2,0,3,2,5]             → 9（官方例2：左侧 4 拦、右侧 5 拦）
 * height=[3,0,3]                   → 3（单洼地：底 0，两侧 3 → 3 格）
 * height=[1,2,3,4]                 → 0（单调增不存水）
 * height=[4,3,2,1]                 → 0（单调减不存水）
 * height=[2,0,2]                   → 2（最简单洼地）
 * height=[5,4,1,2]                 → 1（只有 1 上方 1 格：min(5,2)−1=1）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Hard（页频 ★★★★★——Top6 硬题）
 * <p>【标签】单调栈 · 双指针 · 动态规划预处理 · 数组
 * <p>【题型】"按列积水"= min(左最高, 右最高) − 自身高（短板原理）；
 * <p>　　　　双解：①单调递减栈（四空第 3 列最重的一空：弹时算"洼地面积"）②双指针（短板提前锁死）
 * <p>【考点】水的归属判定（哪侧 max 小，哪侧水位已定）；单调栈弹时三要素（左边界/宽度/水深）；
 * <p>　　　　双指针"矮侧先行"的正确性（水不高于短边——对侧再高也由本侧 max 决定）
 * <p>【关联】P739/496（单调栈三题前两空）；P84（柱状图最大矩形——同族延伸被砍）；P11 盛水容器（双指针同族不同题）
 *
 * <p>LCS 签名：trap(int[] height)。
 */
public class P42TrappingRainWater {

    /** 你的实现（双指针流：矮侧先行——leftMax<rightMax 时左格水位已锁死）。 */
    public int trap(int[] height) {
        int l = 0, r = height.length - 1;
        int leftMax = 0, rightMax = 0;
        int water = 0;
        while (l < r) {
            leftMax = Math.max(leftMax, height[l]);
            rightMax = Math.max(rightMax, height[r]);
            if (leftMax < rightMax) {
                water += leftMax - height[l];
                l++;
            } else {
                water += rightMax - height[r];
                r--;
            }
        }
        return water;
    }

    public static void main(String[] args) {
        P42TrappingRainWater solver = new P42TrappingRainWater();

        run(solver, new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}, "官方例1 → 6");
        run(solver, new int[]{4, 2, 0, 3, 2, 5}, "官方例2 → 9");
        run(solver, new int[]{3, 0, 3}, "单洼地 → 3");
        run(solver, new int[]{1, 2, 3, 4}, "单调增 → 0");
        run(solver, new int[]{4, 3, 2, 1}, "单调减 → 0");
        run(solver, new int[]{2, 0, 2}, "最简洼地 → 2");
        run(solver, new int[]{5, 4, 1, 2}, "右矮墙 → 1（min(5,2)−1）");
        run(solver, new int[]{}, "空数组 → 0");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：按列积水公式 + 每列向两边线性扫最高——O(n²)，机制与双解全不同。 */
    private static int bruteForce(int[] height) {
        int total = 0;
        for (int i = 0; i < height.length; i++) {
            int leftMax = 0, rightMax = 0;
            for (int j = 0; j <= i; j++) leftMax = Math.max(leftMax, height[j]);
            for (int j = i; j < height.length; j++) rightMax = Math.max(rightMax, height[j]);
            total += Math.min(leftMax, rightMax) - height[i];
        }
        return total;
    }

    /** 你的实现【单调栈流】（修正版：depth=min(两墙)−底——"min 里只能放墙不能放减法"）。 */
    public int trapStack(int[] height) {
        Deque<Integer> stack = new ArrayDeque<>();
        int water = 0;
        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[stack.peek()] < height[i]) {
                int bottom = stack.pop();
                if (stack.isEmpty()) {
                    break;
                }
                int left = stack.peek();
                int width = i - left - 1;
                int depth = Math.min(height[left], height[i]) - height[bottom];
                water += width * depth;
            }
            stack.push(i);
        }
        return water;
    }

    /** 参考二（单调栈流——四空第 3 列最重一空的完整形态，对照用）。 */
    private static int refStack(int[] height) {
        Deque<Integer> stack = new ArrayDeque<>();   // 递减栈存下标
        int water = 0;
        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
                int bottom = stack.pop();            // 洼地底
                if (stack.isEmpty()) break;          // 左边没墙 → 积不住
                int left = stack.peek();
                int width = i - left - 1;
                int depth = Math.min(height[left], height[i]) - height[bottom];
                water += width * depth;
            }
            stack.push(i);
        }
        return water;
    }

    private static void run(P42TrappingRainWater solver, int[] height, String label) {
        try {
            int got = solver.trap(Arrays.copyOf(height, height.length));
            int want = bruteForce(Arrays.copyOf(height, height.length));
            int ref2 = solver.trapStack(Arrays.copyOf(height, height.length));
            boolean ok = got == want && got == ref2;
            String extra = (got != ref2) ? "（你的单调栈版与暴力参考不一致！）" : "";

            System.out.printf("[%s] %-20s height=%-34s | 参考=%d 你的栈=%d 实得=%d %s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(height), want, ref2, got, extra);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-20s height=%-34s | TODO 未实现%n", label, Arrays.toString(height));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-20s height=%-34s | 异常: %s%n", label, Arrays.toString(height), e);
        }
    }
}
