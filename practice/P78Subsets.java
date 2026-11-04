import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 78: 子集 (Subsets)
 * https://leetcode.cn/problems/subsets/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 位运算 · 数组
 * <p>【题型】收集所有"路径节点"，不只叶子：每进一层先收账，再往下走（77 的出口搬进循环顶部）
 * <p>【考点】res.add 放在递归开头（所有前缀都是子集）；start 防倒序；出口"没有显式出口"
 * <p>【关联】P77（叶子收账）/P40（同层去重前传）；P90（子集+同层去重，下一题）
 *
 * <p>给你一个元素互异的整数数组 nums，返回该数组所有可能的子集（幂集），含空集。
 * 解集不能包含重复的子集（顺序任意）。
 *
 * <p>示例：nums=[1,2,3] → [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]（共 2^3=8）
 * <p>约束：1 &lt;= nums.length &lt;= 10；-10 &lt;= nums[i] &lt;= 10；所有元素互不相同
 */
public class P78Subsets {

    /** 你的实现（收账提升到 for 之前，无显式出口——所有前缀都是子集）。 */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
        res.add(new ArrayList<>(path));
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);
            backtrack(nums, i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        P78Subsets solver = new P78Subsets();

        run(solver, new int[]{1, 2, 3}, "示例1 官方 8 个");
        run(solver, new int[]{1}, "单元素 → [ [], [1] ]");
        run(solver, new int[]{0}, "负界值单元素 0");
        run(solver, new int[]{5, 4, 3, 2, 1}, "乱序 5 元素 2^5=32 个");
        run(solver, new int[]{-1, 0, 1, 2}, "负数+0 混合 16 个");
        run(solver, new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0}, "上限 10 元素 2^10=1024 个（压力）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：经典位掩码，第 j 个子集 = 掩码 j 中为 1 的那些 num。
     * 元素互异保证掩码 ↔ 子集一一对应，天而无重复。
     */
    private static List<List<Integer>> bruteForce(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        int n = nums.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) subset.add(nums[j]);
            }
            res.add(subset);
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

    /** 归一化：子集内排（跨元素顺序无关）+ 整体字典序，然后与暴力集全等。 */
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

    private static void run(P78Subsets solver, int[] nums, String label) {
        try {
            List<List<Integer>> got = solver.subsets(Arrays.copyOf(nums, nums.length));
            List<List<Integer>> want = bruteForce(nums);

            List<String> g = normalize(got), w = normalize(want);
            boolean ok = new java.util.LinkedHashSet<>(g).size() == g.size() && g.equals(w);

            System.out.printf("[%s] %-30s nums=%-28s | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(nums), want.size(), got.size());
            if (!ok) {
                java.util.Set<String> W = new java.util.TreeSet<>(w), G = new java.util.TreeSet<>(g);
                System.out.println("    ---- 差异（前 5 条）----");
                int shown = 0;
                for (String k : W) if (!G.contains(k) && shown++ < 5) System.out.println("    缺  " + k);
                for (String k : G) if (!W.contains(k) && shown++ < 5) System.out.println("    多  " + k);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s nums=%-28s | TODO 未实现%n", label, Arrays.toString(nums));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s nums=%-28s | 异常: %s%n", label, Arrays.toString(nums), e);
        }
    }
}
