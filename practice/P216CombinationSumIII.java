import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 216: 组合总和 III (Combination Sum III)
 * https://leetcode.cn/problems/combination-sum-iii/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 组合 · 剪枝
 * <p>【题型】77 组合 + 和值约束：从 1~9 中选 k 个数，恰好加起来等于 n，每个数最多用一次
 * <p>【考点】出口条件判 remain == 0；两条剪枝（数量不够 / i > remain）；remain 是"往下传的约束状态"
 * <p>【关联】P77 组合模板原型；P39 组合总和（元素可重复取）
 *
 * <p>找出所有相加之和为 n 的 k 个数的组合，只使用数字 1~9，每个数字最多使用一次。
 *
 * <p>示例：k=3, n=7 → [[1,2,4]]；k=3, n=9 → [[1,2,6],[1,3,5],[2,3,4]]
 * <p>约束：2 &lt;= k &lt;= 9；1 &lt;= n &lt;= 60
 */
public class P216CombinationSumIII {

    /** 你的实现（版本A：出口合并成一行 path.size()==k && remain==0）。 */
    public List<List<Integer>> combinationSum3(int k, int n) {
        List<List<Integer>> res = new ArrayList<>();
        backtrack(k, n, 1, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(int k, int remain, int start, List<Integer> path, List<List<Integer>> res) {
        if (path.size() == k) {
            if (remain == 0) {
                res.add(new ArrayList<>(path));
            }
            return;
        }
        for (int i = start; i <= 9 - (k - path.size()) + 1; i++) {
            if (i > remain) {
                break;
            }
            path.add(i);
            backtrack(k, remain - i, i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        P216CombinationSumIII solver = new P216CombinationSumIII();

        run(solver, 3, 7, 1, "示例1 k=3 n=7 → [1,2,4]");
        run(solver, 3, 9, 3, "示例2 k=3 n=9 → 3 组");
        run(solver, 4, 1, 0, "和不可能（4 个正数最小和 10 > 1）");
        run(solver, 9, 45, 1, "k=9 n=45（1..9 全选恰好 45）");
        run(solver, 2, 18, 0, "k=2 n=18 最大和才 17，0 组");
        run(solver, 8, 36, 1, "k=8 n=36 恰 [1..8]");
        run(solver, 4, 22, 11, "k=4 n=22");
        run(solver, 5, 25, 12, "k=5 n=25");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------
    // 期望数量为人工核算（枚举所有 k-组合算和），不做"猜答案"式校验

    private static void run(P216CombinationSumIII solver, int k, int n, int expectCount, String label) {
        try {
            List<List<Integer>> got = solver.combinationSum3(k, n);

            // 结构校验：数量 + 每组 k 个、值域 [1,9]、互不相同、和恰为 n、无重复组合
            boolean ok = got.size() == expectCount;
            List<List<Integer>> sorted = new ArrayList<>();
            for (List<Integer> c : got) {
                List<Integer> copy = new ArrayList<>(c);
                copy.sort(null);
                sorted.add(copy);
            }
            sorted.sort((a, b) -> {
                for (int j = 0; j < Math.min(a.size(), b.size()); j++) {
                    int d = a.get(j) - b.get(j);
                    if (d != 0) return d;
                }
                return a.size() - b.size();
            });
            for (int i = 0; ok && i < sorted.size(); i++) {
                List<Integer> c = sorted.get(i);
                if (c.size() != k) { ok = false; break; }
                long sum = 0;
                for (int j = 0; j < c.size(); j++) {
                    int v = c.get(j);
                    if (v < 1 || v > 9 || (j > 0 && v == c.get(j - 1))) { ok = false; break; }
                    sum += v;
                }
                if (sum != n) { ok = false; break; }
                if (i > 0 && sorted.get(i).equals(sorted.get(i - 1))) { ok = false; break; }
            }

            System.out.printf("[%s] %-42s k=%d n=%d | 期望=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, k, n, expectCount, got.size());
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-42s k=%d n=%d | TODO 未实现%n", label, k, n);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-42s k=%d n=%d | 异常: %s%n", label, k, n, e);
        }
    }
}
