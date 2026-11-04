import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 47: 全排列 II (Permutations II)
 * https://leetcode.cn/problems/permutations-ii/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 排序 · 去重
 * <p>【题型】46 全排列 + 数组含重复：解集不得有重复排列（去重的排列）
 * <p>【考点】used 必须【下标身份】（值身份 HashSet 在重复值下归零）；同层去重多一个 used[i-1] 条件（vs 40 的 i&gt;start）
 * <p>【关联】P46（used 的形）；P40（i&gt;start 去重——组合用 start 定层，排列没有 start 用 used[i-1] 定层）
 *
 * <p>给一个可含重复数字的序列 nums，按任意顺序返回所有不重复的全排列。
 *
 * <p>示例：nums=[1,1,2] → [[1,1,2],[1,2,1],[2,1,1]]（3!/2! = 3 种唯一）
 * <p>约束：1 &lt;= nums.length &lt;= 8；-10 &lt;= nums[i] &lt;= 10
 */
public class P47PermutationsII {

    /** 你的实现（v1：i>1、used 整盘重置、start 计数层号——3 处问题待修）。 */
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        Arrays.sort(nums);
        backtrack(nums, 0, used, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] nums, int start, boolean[] used, List<Integer> path, List<List<Integer>> res) {
        if (nums.length == start) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i] || (i > 0 && nums[i] == nums[i - 1] && !used[i - 1])) {
                continue;
            }
            path.add(nums[i]);
            used[i] = true;
            backtrack(nums, start + 1, used, path, res);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        P47PermutationsII solver = new P47PermutationsII();

        run(solver, new int[]{1, 1, 2}, "示例1 官方 → 3 种");
        run(solver, new int[]{1, 2, 3}, "无重复 = 46 题退化 3!=6");
        run(solver, new int[]{1}, "单元素 1 种");
        run(solver, new int[]{1, 1, 1, 1}, "全重复 → 只有 1 种");
        run(solver, new int[]{2, 2, 1, 1}, "两对重复 4!/2!/2!=6");
        run(solver, new int[]{-1, 1, 0, 0, 1, -1, 1}, "LeetCode 讨论区压力用例");
        run(solver, new int[]{0, 0, 0, 1, 1}, "3+2 重复组合 5!/(3!·2!)=10");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：下标掩码递归枚举全部 n! 原始排列，再按值序列去重（与回溯机制独立）。 */
    private static List<List<Integer>> bruteForce(int[] nums) {
        List<List<Integer>> raw = new ArrayList<>();
        collect(nums, new boolean[nums.length], new ArrayList<>(), raw);
        java.util.TreeSet<String> seen = new java.util.TreeSet<>();
        List<List<Integer>> res = new ArrayList<>();
        for (List<Integer> p : raw) {
            if (seen.add(p.toString())) res.add(p);
        }
        res.sort((a, b) -> {
            for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                int d = a.get(i) - b.get(i);
                if (d != 0) return d;
            }
            return a.size() - b.size();
        });
        return res;
    }

    private static void collect(int[] nums, boolean[] used, List<Integer> prefix, List<List<Integer>> res) {
        if (prefix.size() == nums.length) {
            res.add(new ArrayList<>(prefix));
            return;
        }
        for (int j = 0; j < nums.length; j++) {
            if (used[j]) continue;
            used[j] = true;
            prefix.add(nums[j]);
            collect(nums, used, prefix, res);
            prefix.remove(prefix.size() - 1);
            used[j] = false;
        }
    }

    private static List<String> normalize(List<List<Integer>> got) {
        List<String> keys = new ArrayList<>();
        for (List<Integer> c : got) keys.add(c.toString());
        keys.sort(null);
        return keys;
    }

    private static void run(P47PermutationsII solver, int[] nums, String label) {
        try {
            List<List<Integer>> got = solver.permuteUnique(Arrays.copyOf(nums, nums.length));
            List<List<Integer>> want = bruteForce(nums);

            List<String> g = normalize(got), w = normalize(want);
            boolean ok = new java.util.LinkedHashSet<>(g).size() == g.size() && g.equals(w);

            System.out.printf("[%s] %-30s nums=%-26s | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want.size(), got.size());
            if (!ok) {
                java.util.Set<String> W = new java.util.TreeSet<>(w), G = new java.util.TreeSet<>(g);
                System.out.println("    ---- 差异（前 5 条）----");
                int shown = 0;
                for (String k : W) if (!G.contains(k) && shown++ < 5) System.out.println("    缺  " + k);
                for (String k : G) if (!W.contains(k) && shown++ < 5) System.out.println("    多  " + k);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s nums=%-26s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s nums=%-26s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
