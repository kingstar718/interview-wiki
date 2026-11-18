import java.util.Arrays;

/**
 * LeetCode 238: 除自身以外数组的乘积 (Product of Array Except Self)
 * https://leetcode.cn/problems/product-of-array-except-self/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 4/50——前缀积×后缀积）
 *
 * <p>■ 题目描述
 *   给整数数组 nums，返回 answer，其中 answer[i] = nums 中**除 nums[i] 之外**
 *   其余所有元素的乘积。**禁止使用除法**，要求 O(n) 时间（除法版被禁的真正原因：
 *   数组含 0 时除法失效——0 不能当分母）。
 *
 * <p>■ 输入输出
 *   输入（约束）：int[] nums（2 ≤ length ≤ 10^5；乘积保证在 32 位 int 内）
 *   输出：int[] answer
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[1,2,3,4] → [24,12,8,6]
 * nums=[-1,1,0,-3,3] → [0,0,9,0,0]（0 在下标 2：左右积 0×0=0；唯一下标 1 的左侧有 0 右侧有 0 → 0）
 * nums=[2,3] → [3,2]（两元素：answer[0]=nums[1]）
 * nums=[0,0] → [0,0]（双 0：每个的"其余积"=对面的 0）
 * nums=[1,1] → [1,1]（全 1）
 * nums=[5,1,4,2] → [8,40,10,20]（无 0 常规）
 * nums=[-2,-3,-4] → [12,8,6]（全负——负号守恒：其余两个负数相乘为正）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★· Hot100）
 * <p>【标签】数组 · 前缀和变体 · 数学
 * <p>【题型】前缀积×后缀积：answer[i] = L[i](左侧全积) × R[i](右侧全积)；
 * <p>　　　　O(1) 额外空间版：answer 先装前缀积，再从右往左用单变量滚后缀
 * <p>【考点】L/R 的边界语义（L[0]=1、R[n-1]=1——"空积=1"）；禁止除法的原因（0 失效）；
 * <p>　　　　0 的自动成立（无需特判）；第二次扫描方向（从右往左）与滚动变量的更新序
 * <p>【关联】P303 区域和检索（前缀和原型）；P121（左右预处理思想同族）
 *
 * <p>LCS 签名：productExceptSelf(int[] nums)。
 */
public class P238ProductOfArrayExceptSelf {

    /** 你的实现（双数组版：L 前缀积 + R 后缀积 → res=L×R——O(n) 时间，空间 O(n)）。 */
    public int[] productExceptSelf(int[] nums) {
        int[] l = new int[nums.length];
        l[0] = 1;
        for (int i = 1; i < nums.length; i++) {
            l[i] = l[i - 1] * nums[i- 1];
        }
        int[] r = new int[nums.length];
        r[nums.length - 1] = 1;
        int[] res = new int[nums.length];
        res[nums.length-1] = l[nums.length-1] * r[nums.length-1];
        for (int i = nums.length - 2; i >= 0; i--) {
            r[i] = r[i+1] * nums[i+1];
            res[i] = r[i] * l[i];
        }
        return res;
    }

    public static void main(String[] args) {
        P238ProductOfArrayExceptSelf solver = new P238ProductOfArrayExceptSelf();

        run(solver, new int[]{1, 2, 3, 4}, "官方例1 → [24,12,8,6]");
        run(solver, new int[]{-1, 1, 0, -3, 3}, "官方例2 含 0 → [0,0,9,0,0]");
        run(solver, new int[]{2, 3}, "两元素 → [3,2]");
        run(solver, new int[]{0, 0}, "双 0 → [0,0]");
        run(solver, new int[]{1, 1}, "全 1 → [1,1]");
        run(solver, new int[]{5, 1, 4, 2}, "常规无 0 → [8,40,10,20]");
        run(solver, new int[]{-2, -3, -4}, "全负 → [12,8,6]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：每个位置左右分别线性乘——O(n²)，机制与前缀积完全不同。 */
    private static int[] bruteForce(int[] nums) {
        int n = nums.length;
        int[] out = new int[n];
        for (int i = 0; i < n; i++) {
            int p = 1;
            for (int j = 0; j < n; j++) {
                if (j != i) p *= nums[j];
            }
            out[i] = p;
        }
        return out;
    }

    private static void run(P238ProductOfArrayExceptSelf solver, int[] nums, String label) {
        try {
            int[] got = solver.productExceptSelf(Arrays.copyOf(nums, nums.length));
            int[] want = bruteForce(nums);
            boolean ok = Arrays.equals(got, want);

            System.out.printf("[%s] %-28s nums=%-24s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums),
                    Arrays.toString(want), Arrays.toString(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s nums=%-24s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s nums=%-24s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
