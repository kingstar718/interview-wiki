import java.util.Arrays;

/**
 * LeetCode 189: 轮转数组 (Rotate Array)
 * https://leetcode.cn/problems/rotate-array/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 3/50——三次反转）
 *
 * <p>■ 题目描述
 *   给一个数组 nums，把元素**向右轮转 k 个位置**（尾部元素绕回首部），**原地修改**。
 *
 * <p>■ 输入输出
 *   输入：int[] nums（1 ≤ length ≤ 10^5）、int k（0 ≤ k ≤ 10^5，**可能大于 n**）
 *   输出：无（原地修改）
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[1,2,3,4,5,6,7], k=3 → [5,6,7,1,2,3,4]
 * nums=[-1,-100,3,99], k=2 → [3,99,-1,-100]
 * nums=[1,2], k=5 → [2,1]（k>n → 实际移 1 位）★ 取模哨兵
 * nums=[1], k=0 → [1]
 * nums=[1,2,3], k=3 → [1,2,3]（k==n → 原地不动）★
 * nums=[1,1,1], k=2 → [1,1,1]（全等元素）
 * nums=[1,2,3,4,5], k=4 → [2,3,4,5,1]
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·美团/京东）
 * <p>【标签】数组 · 双指针 · 数学
 * <p>【题型】解法演进叙事题：O(n) 拼接（简单版）→ 三次反转（O(n)/O(1) 主解）→ 环形替换（gcd 圈）
 * <p>【考点】k %= n（k 可能 > n——忘取模全错）；三次反转分解（整体→前 k→后 n-k）；
 * <p>　　　　反转双指针（头尾相向交换）；k==n/k==0 不动（取模后自然归一）
 * <p>【关联】P151（反转单词——同构：整体反转+局部扶正）
 *
 * <p>LCS 签名：rotate(int[] nums, int k)。
 */
public class P189RotateArray {

    /** 你的实现（拼接版 O(n)/O(n)：下标映射 (i+k)%n 搬 temp 再拷回——k==n 特判+取模守卫）。 */
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        if (n == k) {
            return;
        }
        if (k > n) {
            k = k % n;
        }
        int[] temp = new int[n];
        for (int i = 0; i < n; i++) {
            int newI = (i + k) % n;
            temp[newI] = nums[i];
        }
        for (int i = 0; i < n; i++) {
            nums[i] = temp[i];
        }
    }

    public static void main(String[] args) {
        P189RotateArray solver = new P189RotateArray();

        run(solver, new int[]{1, 2, 3, 4, 5, 6, 7}, 3, "官方例1 → [5,6,7,1,2,3,4]");
        run(solver, new int[]{-1, -100, 3, 99}, 2, "官方例2 → [3,99,-1,-100]");
        run(solver, new int[]{1, 2}, 5, "k>n → [2,1] ★ 取模哨兵");
        run(solver, new int[]{1}, 0, "单元素 k=0 → 不变");
        run(solver, new int[]{1, 2, 3}, 3, "k==n → 不变 ★");
        run(solver, new int[]{1, 1, 1}, 2, "全等 → 不变");
        run(solver, new int[]{1, 2, 3, 4, 5}, 4, "k=n-1 → [2,3,4,5,1]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 你的实现【三次反转版】（自逆性代数：整体反转调段序，再各段扶正）。 */
    public void rotateReverse(int[] nums, int k) {
        int n = nums.length;
        k %= n;
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }

    /** 独立暴力参考：逐次右移一位（k % n 次 × 全体挪动）——机制与三次反转完全不同。 */
    private static int[] bruteForce(int[] nums, int k) {
        int n = nums.length;
        k %= n;
        int[] out = nums.clone();
        for (int step = 0; step < k; step++) {
            int last = out[n - 1];
            for (int i = n - 1; i > 0; i--) out[i] = out[i - 1];
            out[0] = last;
        }
        return out;
    }

    private static void run(P189RotateArray solver, int[] nums, int k, String label) {
        try {
            int[] want = bruteForce(nums, k);
            int[] gotA = nums.clone();
            solver.rotate(gotA, k);
            int[] gotB = nums.clone();
            solver.rotateReverse(gotB, k);
            boolean ok = Arrays.equals(gotA, want) && Arrays.equals(gotB, want);
            String extra;
            if (ok) {
                extra = "（拼接版+三反版双过）";
            } else if (!Arrays.equals(gotB, want)) {
                extra = "（三反版不一致：实得=" + Arrays.toString(gotB) + "）";
            } else {
                extra = "（拼接版不一致）";
            }

            System.out.printf("[%s] %-30s nums=%-22s k=%-3d | 参考=%s 实得=%s%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), k,
                    Arrays.toString(want), Arrays.toString(gotA), extra);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s nums=%-22s k=%-3d | TODO 未实现%n", label, Arrays.toString(nums), k);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s nums=%-22s k=%-3d | 异常: %s%n", label, Arrays.toString(nums), k, e);
        }
    }
}
