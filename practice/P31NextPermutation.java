import java.util.Arrays;

/**
 * LeetCode 31: 下一个排列 (Next Permutation)
 * https://leetcode.cn/problems/next-permutation/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 8/50——字典序推进）
 *
 * <p>■ 题目描述
 *   整数数组的一个**排列**，将它**原地**重排成字典序中**下一个更大**的排列
 *   （下一个比它大的排列，不是任意更大的）。
 *   若已是最大排列（整个数组倒序），则重排成**最小排列**（升序）。
 *   要求 O(n) 时间、原地 O(1) 空间。
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ length ≤ 100；0 ≤ nums[i] ≤ 100）——**原地修改无返回值**
 *
 * <p>■ 示例逐条
 * <pre>
 * nums=[1,2,3] → [1,3,2]
 * nums=[3,2,1] → [1,2,3]（最大排列 → 回到最小）
 * nums=[1,1,5] → [1,5,1]（重复元素）
 * nums=[1] → [1]（单元素无下一个=原地不动）
 * nums=[1,3,5,4,2] → [1,4,2,3,5]（wiki 手推例：i=1(3<5)，j=3(4)，交换后翻转尾部）
 * nums=[1,5,1] → [5,1,1]（翻转哨兵：i=0，j=1，交换后翻转 [1] 区）
 * nums=[5,4,7,5,3,2] → [5,5,2,3,4,7]（重复元素选 j 的"从右第一个大于"）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★·阿里）
 * <p>【标签】数组 · 双指针 · 数学
 * <p>【题型】字典序推进三步法：从右找第一个下降 i → 从右找第一个 > nums[i] 的 j 交换 → 翻转 i+1..末尾
 * <p>【考点】**尾后缀全降序**（为什么能保证——下降点右侧必是最大尾部）；"从右第一个大于"的取 j 正确性
 * <p>　　　　（重复元素时相等不算大于）；全降序回最小；翻转后缀 = 变最小（升序）
 * <p>【关联】P46/47（回溯章全排列——本题是"迭代版全排列递进"）；P556 下一个更大元素III（字符串版——未收）
 *
 * <p>LCS 签名：nextPermutation(int[] nums)。
 */
public class P31NextPermutation {

    /** 你的实现（三步法：右找降点→右找大者交换→翻转后缀最小化；i=-1 时整体翻转回最小）。 */
    public void nextPermutation(int[] nums) {
        int n = nums.length;
        int i = n - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }
        if (i >= 0) {
            int j = n - 1;
            while (nums[j] <= nums[i]) {
                j--;
            }
            swap(nums, i, j);
        }
        reverse(nums, i + 1, n - 1);
    }

    private void swap(int[] nums, int i, int j) {
        int t = nums[i];
        nums[i] = nums[j];
        nums[j] = t;
    }

    private void reverse(int[] nums, int s, int e) {
        while (s < e) {
            swap(nums, s, e);
            s++;
            e--;
        }
    }

    public static void main(String[] args) {
        P31NextPermutation solver = new P31NextPermutation();

        run(solver, new int[]{1, 2, 3}, "官方例1 → [1,3,2]");
        run(solver, new int[]{3, 2, 1}, "全降序回最小 → [1,2,3]");
        run(solver, new int[]{1, 1, 5}, "重复 → [1,5,1]");
        run(solver, new int[]{1}, "单元素 → [1]");
        run(solver, new int[]{1, 3, 5, 4, 2}, "wiki 手推例 → [1,4,2,3,5]");
        run(solver, new int[]{1, 5, 1}, "翻转哨兵 → [5,1,1]");
        run(solver, new int[]{5, 4, 7, 5, 3, 2}, "重复取 j → [5,5,2,3,4,7]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：生成全排列排序后找下一个——O(n!)，机制与三步法完全不同（n≤8 内用）。 */
    private static int[] bruteForce(int[] nums) {
        int n = Math.min(nums.length, 8);
        int[] input = Arrays.copyOf(nums, n);
        java.util.Set<String> perms = new java.util.TreeSet<>();   // 排序+去重（重复元素会生成重复排列）
        permute(input, new boolean[n], new int[n], 0, perms);
        String cur = Arrays.toString(Arrays.copyOf(nums, n));
        java.util.List<String> sorted = new java.util.ArrayList<>(perms);
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).equals(cur)) {
                String next = sorted.get((i + 1) % sorted.size());
                next = next.substring(1, next.length() - 1);
                String[] parts = next.split(", ");
                int[] out = new int[nums.length];
                for (int j = 0; j < n; j++) out[j] = Integer.parseInt(parts[j]);
                for (int j = n; j < nums.length; j++) out[j] = nums[j];
                return out;
            }
        }
        throw new IllegalStateException("not found");
    }

    private static void permute(int[] nums, boolean[] used, int[] path, int depth, java.util.Set<String> out) {
        if (depth == nums.length) {
            out.add(Arrays.toString(path.clone()));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (!used[i]) {
                used[i] = true;
                path[depth] = nums[i];
                permute(nums, used, path, depth + 1, out);
                used[i] = false;
            }
        }
    }

    private static void run(P31NextPermutation solver, int[] nums, String label) {
        try {
            int[] got = nums.clone();
            solver.nextPermutation(got);
            int[] want = bruteForce(nums);
            boolean ok = Arrays.equals(got, want);

            System.out.printf("[%s] %-26s nums=%-24s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums),
                    Arrays.toString(want), Arrays.toString(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s nums=%-24s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s nums=%-24s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
