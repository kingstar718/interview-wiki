import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 77: 组合 (Combinations)
 * https://leetcode.cn/problems/combinations/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 组合
 * <p>【题型】回溯模板的原型题：从 [1, n] 中选 k 个数的所有组合，顺序不看（{1,2} 和 {2,1} 是同一个）
 * <p>【考点】startIndex（避免重复组合）；回溯三步（做选择 → 递归 → 撤销）；结果记录用 path 的拷贝
 * <p>【关联】本章模板原型，后面 9 道全是它的变形
 *
 * <p>给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。
 * <p>组合内数字不重复、返回结果只做"集合"，{1,2} 和 {2,1} 只算一种。
 *
 * <p>示例1：n=4, k=2 → [[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
 * <p>示例2：n=1, k=1 → [[1]]
 *
 * <p>约束：1 &lt;= n &lt;= 20；1 &lt;= k &lt;= n
 */
public class P77Combinations {

    /** 你的实现：start 从 1 起，闭边界剪枝 i <= n - need + 1，撤销 remove(size-1)。 */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        backtrack(n, k, 1, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(int n, int k, int start, List<Integer> path, List<List<Integer>> res) {
        if (path.size() == k) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i <= n - (k - path.size()) + 1; i++) {
            path.add(i);
            backtrack(n, k, i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        P77Combinations solver = new P77Combinations();

        run(solver, 4, 2, 6, "示例1 官方");
        run(solver, 1, 1, 1, "示例2 最小");
        run(solver, 2, 1, 2, "单元素组合 n=2,k=1");
        run(solver, 2, 2, 1, "k=n 只有一种");
        run(solver, 4, 0, 1, "k=0 返回 [[]]");
        run(solver, 4, 3, 4, "n=4,k=3 共4种");
        run(solver, 5, 3, 10, "n=5,k=3 共10种");
        run(solver, 13, 11, 78, "中规模剪枝观察 C(13,11)=78");
        run(solver, 20, 10, 184756, "压力上限 C(20,10)");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static int passed = 0;

    private static void run(P77Combinations solver, int n, int k, int expectCount, String label) {
        try {
            List<List<Integer>> got = solver.combine(n, k);
            boolean countOk = got.size() == expectCount;

            // 组合答案是"集合"：把每个组合排序、整体排序后看是否有重复或遗漏
            List<List<Integer>> sorted = new ArrayList<>();
            for (List<Integer> c : got) {
                List<Integer> copy = new ArrayList<>(c);
                copy.sort(null);
                sorted.add(copy);
            }
            sorted.sort((a, b) -> {
                for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
                    int d = a.get(i) - b.get(i);
                    if (d != 0) return d;
                }
                return a.size() - b.size();
            });
            boolean noDup = true;
            for (int i = 1; i < sorted.size(); i++) {
                if (sorted.get(i).equals(sorted.get(i - 1))) {
                    noDup = false;
                    System.out.println("    重复组合：" + sorted.get(i));
                    break;
                }
            }
            // 范围检查：每个组合内部值在 [1,n] 且互不相同、长度=k（k>0 时）
            boolean rangeOk = true;
            for (List<Integer> c : sorted) {
                if (k == 0) { if (!c.isEmpty()) { rangeOk = false; break; } continue; }
                if (c.size() != k) { rangeOk = false; break; }
                for (int v : c) {
                    if (v < 1 || v > n) { rangeOk = false; break; }
                }
            }

            String mark = (countOk && noDup && rangeOk) ? "PASS" : "FAIL";
            if (mark.equals("PASS")) passed++;
            System.out.printf("[%s] %-22s n=%d k=%d | 期望组合数=%d 实得=%d%n",
                    mark, label, n, k, expectCount, got.size());
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-22s n=%d k=%d | %s%n", label, n, k, "TODO 未实现");
        } catch (Exception e) {
            System.out.printf("[FAIL] %-22s n=%d k=%d | 异常: %s%n", label, n, k, e);
        }
    }
}
