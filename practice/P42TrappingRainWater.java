/**
 * LeetCode 42: 接雨水 (Trapping Rain Water)
 * https://leetcode.cn/problems/trapping-rain-water/
 *
 * <p>【难度】Hard
 * <p>【标签】双指针(Two Pointers) · 栈(Stack) · 动态规划(DP) · 单调栈(Monotonic Stack)
 * <p>【题型】按列计算「可蓄水 = min(左max, 右max) − 自身高度」；同一思路有 DP / 双指针 / 单调栈三套实现
 * <p>【考点】能否看出每列蓄水量只取决于「两侧最高柱的较小值」，以及双指针如何把空间压到 O(1)
 * <p>【关联】{@link P11ContainerWithMostWater} —— 同用双指针，但 11 求「两线夹的最大容积」（容量=短线×距离），42 求「柱间蓄水」（每柱水位由两侧更高者定），方向相反，对照记
 * <p>{@link P84LargestRectangleInHistogram} —— 同用单调栈，但 84 求「柱围成的最大矩形面积」（找两侧更矮边界），42 求「柱间凹槽蓄水」（找两侧更高边界），单调栈方向相反的经典对照
 *
 * <p>给定 n 个非负整数表示每根柱子的高度图，计算下雨后该图能接住多少雨水。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：按列枚举，对每个 i 求它上方蓄水 = max(0, min(左侧最高, 右侧最高) − height[i])。
 *    每列左右各扫一遍求 max → O(n²)。
 *
 * <p>2. 关键观察：min(左max, 右max) 里的左max、右max 是固定结构，可预处理：
 *    leftMax[i] = max(leftMax[i-1], height[i])，rightMax[i] = max(rightMax[i+1], height[i])，
 *    两个 DP 数组一趟填好，之后每列蓄水量 O(1) 得到 → 整体 O(n) 时间，O(n) 空间。
 *
 * <p>3. 核心技巧 —— 双指针 O(1) 空间：
 *    用 left、right 两指针从两端向中间收窄，维护 leftMax、rightMax。
 *    哪边的 max 更小，就处理哪边——因为较小的一侧决定了该列水位（另一侧更高，min 由小的一侧拍板）：
 *      - 若 leftMax < rightMax：处理 left，蓄水 = max(0, leftMax − height[left])，再 left++；
 *      - 否则：处理 right，蓄水 = max(0, rightMax − height[right])，再 right--。
 *    每步 O(1)，总 O(n)。
 *    （另有单调栈解法：栈存下标，遇比栈顶高者就弹出并累加弹出柱的蓄水。）
 *
 * <p>⚠️ 易错点：双指针的分支条件比的是「两侧已知 max 的大小」(leftMax vs rightMax)，
 *    不是 height[left] vs height[right]，更不是 left vs right。
 *    初学常把条件写反，导致某些用例少算或多算。
 *
 * <p>4. 复杂度：DP 解法 O(n) 时间 O(n) 空间；双指针 O(n) 时间 O(1) 空间；单调栈 O(n) 时间 O(n) 空间。
 *    边界：长度 < 3 不可能蓄水（中间无槽）返回 0；单调递增/递减/全等数组都蓄 0。
 */
public class P42TrappingRainWater {

    public int trap(int[] height) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P42TrappingRainWater p42 = new P42TrappingRainWater();

        // 示例 1
        assertEqual(6, p42.trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}), "Example 1");

        // 示例 2
        assertEqual(9, p42.trap(new int[]{4, 2, 0, 3, 2, 5}), "Example 2");

        // 单根柱
        assertEqual(0, p42.trap(new int[]{5}), "Single bar");

        // 两根柱
        assertEqual(0, p42.trap(new int[]{1, 1}), "Two bars");

        // 单调递增
        assertEqual(0, p42.trap(new int[]{1, 2, 3, 4}), "Ascending");

        // 单调递减
        assertEqual(0, p42.trap(new int[]{4, 3, 2, 1}), "Descending");

        // 全等
        assertEqual(0, p42.trap(new int[]{0, 0, 0}), "All zero");

        // 单槽
        assertEqual(3, p42.trap(new int[]{3, 0, 3}), "Single dip");

        // 平底槽
        assertEqual(6, p42.trap(new int[]{3, 0, 0, 3}), "Plateau dip");

        // 双槽
        assertEqual(12, p42.trap(new int[]{4, 2, 0, 2, 0, 4}), "Two dips");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(String.format("[FAIL] %s: expected %d, but got %d",
                testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
