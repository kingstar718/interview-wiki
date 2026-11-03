import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * LeetCode 40: 组合总和 II (Combination Sum II)
 * https://leetcode.cn/problems/combination-sum-ii/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 数组 · 剪枝 · 去重
 * <p>【题型】39 组合总和 + candidates 本身有重复：每个元素只能用一次，解集须无重复组合
 * <p>【考点】排序 + 同层去重（i &gt; start && c[i]==c[i-1] → continue）；递归传回 i+1（不可重复取）
 * <p>【关联】P39（可重复取、无重复元素）；P90/P47（同层去重的另外两处用法，47 用 used[] 对照）
 *
 * <p>candidates 数组（可能含重复元素）、目标数 target：
 * 找出所有和为 target 的组合，每个元素最多用一次，解集不得有重复组合。
 *
 * <p>示例：candidates=[10,1,2,7,6,1,5], target=8 → [[1,1,6],[1,2,5],[1,7],[2,6]]
 * <p>约束：1 &lt;= candidates.length &lt;= 30（值 1~50）；1 &lt;= target &lt;= 30
 */
public class P40CombinationSumII {

    /** 你的实现（39 平移版：next<0 剪枝 + i+1 不可重取；暂无同层去重）。 */
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> res = new ArrayList<>();
        backtrack(candidates, target, 0, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] candidates, int remain, int start, List<Integer> path, List<List<Integer>> res) {
        if (remain == 0) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            int next = remain - candidates[i];
            if (next < 0) {
                break;
            }
            if (i > start && candidates[i] == candidates[i - 1]) {
                continue;
            }
            path.add(candidates[i]);
            backtrack(candidates, next, i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        P40CombinationSumII solver = new P40CombinationSumII();

        run(solver, new int[]{10, 1, 2, 7, 6, 1, 5}, 8, "示例1 官方 → [1,1,6],[1,2,5],[1,7],[2,6]");
        run(solver, new int[]{2, 5, 2, 1, 2}, 5, "示例2 官方 → [1,2,2],[5]");
        run(solver, new int[]{1, 1, 2}, 4, "小的重复对 → [1,1,2]");
        run(solver, new int[]{1, 1, 1, 1, 1}, 3, "五个重复 1 → [1,1,1] 只能一组");
        run(solver, new int[]{2, 2, 2, 3}, 7, "三个重复 2 → [2,2,3]");
        run(solver, new int[]{1}, 2, "单元素不够 → 空");
        run(solver, new int[]{5}, 5, "单元素恰好");
        run(solver, new int[]{1, 1, 2, 5, 6, 7, 10}, 8, "混合重复（数量由暴力定）");
        run(solver, new int[]{4, 4, 4, 6, 6}, 12, "两种重复值共用（4×3=12？6+6=12）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：位掩码枚举所有子集，收集和为 target 的【去重组合】。
     * 与回溯解法机制独立（不依赖排序/剪枝/去重参数），是"标准答案"的对照体。
     */
    private static List<List<Integer>> bruteForce(int[] candidates, int target) {
        TreeSet<String> seen = new TreeSet<>();
        List<List<Integer>> res = new ArrayList<>();
        int n = candidates.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> combo = new ArrayList<>();
            int sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    combo.add(candidates[i]);
                    sum += candidates[i];
                }
            }
            if (sum == target) {
                combo.sort(null);
                String key = combo.toString();
                if (seen.add(key)) {
                    res.add(combo);  // 遇重复 key 直接丢弃（res 不重复）
                }
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

    /** 把用户答案按【组合内排序 + 组合间排序】归一化后与暴力结果做全等比对。 */
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

    private static void run(P40CombinationSumII solver, int[] candidates, int target, String label) {
        try {
            List<List<Integer>> got = solver.combinationSum2(Arrays.copyOf(candidates, candidates.length), target);
            List<List<Integer>> want = bruteForce(candidates, target);

            // 全等（不是只比数量）：归一化后逐条对照，缺一、多一、重复组合都会被抓
            List<String> g = normalize(got), w = normalize(want);
            boolean ok = g.equals(w);

            System.out.printf("[%s] %-38s candidates=%-22s target=%d | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(candidates), target, want.size(), got.size());
            if (!ok) {
                System.out.println("    ---- 暴力 ----");
                for (String s : w) System.out.println("    " + s);
                System.out.println("    ---- 实得 ----");
                for (String s : g) System.out.println("    " + s);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-38s candidates=%-22s target=%d | TODO 未实现%n",
                    label, Arrays.toString(candidates), target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-38s candidates=%-22s target=%d | 异常: %s%n",
                    label, Arrays.toString(candidates), target, e);
        }
    }
}
