import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 90: 子集 II (Subsets II)
 * https://leetcode.cn/problems/subsets-ii/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 排序 · 去重
 * <p>【题型】78 子集 + 数组含重复：解集不得有重复子集（去重的 78）
 * <p>【考点】78 的"无条件收账"形态保持；40 的同层去重一行搬入；排序是其前提
 * <p>【关联】P78（收账位置 vs 收账形状——收账 GMT 位置变化），P40（同层去重 i &gt; start）
 *
 * <p>给你一个含重复元素的整数数组 nums，返回所有不重复子集（幂集去重）。
 *
 * <p>示例：nums=[1,2,2] → [[], [1], [1,2], [1,2,2], [2], [2,2]]（共 6，不是 8）
 * <p>约束：1 &lt;= nums.length &lt;= 10；-10 &lt;= nums[i] &lt;= 10（**元素可重复**）
 */
public class P90SubsetsII {

    /** 你的实现（goto 话版本：有去重、无撤销——撤销那行丢了）。 */
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        backtrack(nums, 0, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
        res.add(new ArrayList<>(path));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) {
                continue;
            }
            path.add(nums[i]);
            backtrack(nums, i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        P90SubsetsII solver = new P90SubsetsII();

        run(solver, new int[]{1, 2, 2}, "示例1 官方 → 6 个（8 个去重 2 个）");
        run(solver, new int[]{1}, "单元素 2 个");
        run(solver, new int[]{1, 1}, "两个 1 → [ [], [1], [1,1] ] 3 个");
        run(solver, new int[]{2, 2, 2}, "三个 2 → 4 个（2^3=8 去重一半）");
        run(solver, new int[]{0}, "0 单元素");
        run(solver, new int[]{5, 4, 4, 3, 3, 3}, "乱序+重复组 24 个？暴力定");
        run(solver, new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, "十个 1 → 11 个（压力+全重复）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：位掩码枚举 2^n 个下标子集，把值序列化去重。
     * 与回溯机制独立（不排序、不去重参数），值集合就是标准答案。
     */
    private static List<List<Integer>> bruteForce(int[] nums) {
        java.util.TreeSet<String> seen = new java.util.TreeSet<>();
        List<List<Integer>> res = new ArrayList<>();
        int n = nums.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) subset.add(nums[j]);
            }
            subset.sort(null);
            String key = subset.toString();
            if (seen.add(key)) {
                res.add(subset);   // 同值子集只留一份
            }
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

    private static List<String> normalize(List<List<Integer>> got) {
        List<String> keys = new ArrayList<>();
        for (List<Integer> c : got) {
            List<Integer> copy = new ArrayList<>(c);
            copy.sort(null);
            keys.add(copy.toString());
        }
        keys.sort(null);
        return keys;
    }

    private static void run(P90SubsetsII solver, int[] nums, String label) {
        try {
            List<List<Integer>> got = solver.subsetsWithDup(Arrays.copyOf(nums, nums.length));
            List<List<Integer>> want = bruteForce(nums);

            List<String> g = normalize(got), w = normalize(want);
            boolean ok = new java.util.LinkedHashSet<>(g).size() == g.size() && g.equals(w);

            System.out.printf("[%s] %-34s nums=%-30s | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want.size(), got.size());
            if (!ok) {
                java.util.Set<String> W = new java.util.TreeSet<>(w), G = new java.util.TreeSet<>(g);
                int shown = 0;
                System.out.println("    ---- 差异（前 5 条）----");
                for (String k : W) if (!G.contains(k) && shown++ < 5) System.out.println("    缺  " + k);
                for (String k : G) if (!W.contains(k) && shown++ < 5) System.out.println("    多  " + k);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s nums=%-30s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s nums=%-30s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
