import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LeetCode 46: 全排列 (Permutations)
 * https://leetcode.cn/problems/permutations/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 排列
 * <p>【题型】排列型开山：for 从 0 扫全数组（start 消失），顺序有意义，用 used[] 防重复取
 * <p>【考点】used[] 的 add/remove 双向同步；出口回到叶子（path.size == n）；无 start 的 for 全量扫描
 * <p>【关联】P131/P77（叶子收账的形，回归于此）；P47（排列+同层去重，used[] 与 i>start 的对照）
 *
 * <p>给一个不含重复数字的数组 nums，返回其所有可能的全排列（顺序不同算不同）。
 *
 * <p>示例：nums=[1,2,3] → [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]（共 3! = 6）
 * <p>约束：1 &lt;= nums.length &lt;= 6；-10 &lt;= nums[i] &lt;= 10；所有元素互不相同
 */
public class P46Permutations {

    /** 你的实现 v2（原地交换法：path=数组前缀 [0..start-1]，池=[start..]，撤销=同一对 swap 再来一遍）。 */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        backtrack(nums, 0, res);
        return res;
    }

    private void backtrack(int[] nums, int start, List<List<Integer>> res) {
        if (start == nums.length) {
            List<Integer> path = new ArrayList<>();
            for (int i : nums) {
                path.add(i);
            }
            res.add(path);
        }
        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);
            backtrack(nums, start + 1, res);
            swap(nums, i, start);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        P46Permutations solver = new P46Permutations();

        run(solver, new int[]{1, 2, 3}, "示例1 官方 3!=6");
        run(solver, new int[]{1}, "单元素 1! = 1");
        run(solver, new int[]{0, 1}, "两元素 2!=2");
        run(solver, new int[]{5, -1, 0, 2}, "四元素（含负数）4!=24");
        run(solver, new int[]{9, 8, 7, 6, 5, 4}, "六元素（上限）6!=720（压力）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：从空掩码按"已选下标掩码"递归扩展，天然生成全部 n! 排列（值可重复但下标不重复）。 */
    private static List<List<Integer>> bruteForce(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        collect(nums, 0, new ArrayList<>(), res);
        res.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int d = a.get(i) - b.get(i);
                if (d != 0) return d;
            }
            return a.size() - b.size();
        });
        return res;
    }

    private static void collect(int[] nums, int mask, List<Integer> prefix, List<List<Integer>> res) {
        if (prefix.size() == nums.length) {
            res.add(new ArrayList<>(prefix));
            return;
        }
        for (int j = 0; j < nums.length; j++) {
            if ((mask & (1 << j)) != 0) continue;      // 这个下标已在排列里
            prefix.add(nums[j]);
            collect(nums, mask | (1 << j), prefix, res);
            prefix.remove(prefix.size() - 1);
        }
    }

    private static List<String> normalize(List<List<Integer>> got) {
        List<String> keys = new ArrayList<>();
        for (List<Integer> c : got) keys.add(c.toString());
        keys.sort(null);
        return keys;
    }

    private static void run(P46Permutations solver, int[] nums, String label) {
        try {
            List<List<Integer>> got = solver.permute(Arrays.copyOf(nums, nums.length));
            List<List<Integer>> want = bruteForce(nums);

            List<String> g = normalize(got), w = normalize(want);
            boolean ok = new java.util.LinkedHashSet<>(g).size() == g.size()
                    && g.size() == want.size() && g.equals(w);

            System.out.printf("[%s] %-26s nums=%-24s | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want.size(), got.size());
            if (!ok) {
                java.util.Set<String> W = new java.util.TreeSet<>(w), G = new java.util.TreeSet<>(g);
                System.out.println("    ---- 差异（前 5 条）----");
                int shown = 0;
                for (String k : W) if (!G.contains(k) && shown++ < 5) System.out.println("    缺  " + k);
                for (String k : G) if (!W.contains(k) && shown++ < 5) System.out.println("    多  " + k);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s nums=%-24s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s nums=%-24s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
