import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 39: 组合总和 (Combination Sum)
 * https://leetcode.cn/problems/combination-sum/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 数组 · 组合
 * <p>【题型】77 组合的第四变：候选从"连续区间 [1,n]"换成"给定数组 candidates"，且【每个元素可重复选无限次】
 * <p>【考点】递归传 i 不传 i+1（可重复取）；排序后 c[i] > remain 剪枝；出路只剩"remain==0"（数量维度退场）
 * <p>【关联】P216（每个数限一次，传 i+1）；P40（candidates 有重复 + 每个限一次 = 排序+同层去重）
 *
 * <p>无重复元素数组 candidates 和目标值 target，找出所有和为 target 的组合。
 * 每个元素可以被无限重复选取；解集内不能有重复组合（[2,2,3] 和 [2,3,2] 算一个）。
 *
 * <p>示例：candidates=[2,3,6,7], target=7 → [[2,2,3],[7]]
 * <p>约束：1 &lt;= candidates.length &lt;= 30；2 &lt;= candidates[i] &lt;= 40；1 &lt;= target &lt;= 40
 */
public class P39CombinationSum {

    /** 你的实现（有序 + 剪枝 x=remain-c[i]<0 即 break + 可重复取传 i）。 */
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(candidates);
        backtrack(candidates, target, 0, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(int[] candidates, int remain, int start, List<Integer> path, List<List<Integer>> res) {
        if (remain == 0) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            int x = remain - candidates[i];
            if (x < 0) {
                break;
            }
            path.add(candidates[i]);
            backtrack(candidates, x, i, path, res);
            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {
        P39CombinationSum solver = new P39CombinationSum();

        run(solver, new int[]{2, 3, 6, 7}, 7, 2, "示例1 官方 → [2,2,3],[7]");
        run(solver, new int[]{2, 3, 5}, 8, 3, "示例2 官方 → [2,2,2,2],[2,3,3],[3,5]");
        run(solver, new int[]{2}, 1, 0, "最小候选2 > target=1 → 空");
        run(solver, new int[]{2, 3}, 5, 1, "2+3 唯一组合（不能 [3,2]）");
        run(solver, new int[]{3, 12, 9, 11, 6, 7, 8, 5, 4}, 15, 15, "乱序 9 元素（暴力核算 15 组）");
        run(solver, new int[]{7, 3, 2}, 18, 7, "乱序 7,3,2 target=18（暴力核算 7 组）");
        run(solver, new int[]{8, 7, 4, 3}, 11, 3, "乱序经典 3 组");
        run(solver, new int[]{2, 4, 6}, 40, 44, "全偶数大 target（暴力核算 44 组）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 期望数量用暴力 DFS 独立核算（candidates 递增，j 从 i 起可重复，保证不重复组合）。 */
    private static int bruteCount(int[] c, int target) {
        Arrays.sort(c);  // 排序只是让暴力自身更稳（剪枝少一个），数量不受顺序影响
        return brute(new long[c.length], c, target, 0, 0);
    }

    private static int brute(long[] chosen, int[] c, int targetRemain, int start, int depth) {
        if (targetRemain == 0) return 1;
        if (start >= c.length || c[start] > targetRemain) return 0;  // 已排序可借此提前
        int count = 0;
        for (int i = start; i < c.length; i++) {
            if (c[i] > targetRemain) break;
            count += brute(chosen, c, targetRemain - c[i], i, depth + 1);
        }
        return count;
    }

    private static void run(P39CombinationSum solver, int[] candidates, int target, int expectCount, String label) {
        try {
            List<List<Integer>> got = solver.combinationSum(Arrays.copyOf(candidates, candidates.length), target);

            // 独立核算理想答案
            int bf = bruteCount(Arrays.copyOf(candidates, candidates.length), target);
            if (bf != expectCount) {
                System.out.printf("[WARN] %-32s 期望值与暴力值不一致（期望=%d 暴力=%d）——用暴力值判定%n",
                        label, expectCount, bf);
            }
            int want = bf;

            boolean ok = got.size() == want;
            // 逐组校验：和恰为 target、每组值都来自 candidates；序列化排序检查重复
            List<String> seen = new ArrayList<>();
            for (List<Integer> c : got) {
                long sum = 0;
                boolean memberOk = true;
                for (int v : c) {
                    sum += v;
                    boolean found = false;
                    for (int cv : candidates) if (cv == v) { found = true; break; }
                    if (!found) { memberOk = false; break; }
                }
                if (!memberOk || sum != target) { ok = false; break; }
                List<Integer> cp = new ArrayList<>(c); cp.sort(null);
                seen.add(cp.toString());
            }
            seen.sort(null);
            for (int i = 1; ok && i < seen.size(); i++) {
                if (seen.get(i).equals(seen.get(i - 1))) { ok = false; break; }
            }

            System.out.printf("[%s] %-32s candidates=%-18s target=%d | 期望=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(candidates), target, want, got.size());
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-32s candidates=%-18s target=%d | TODO 未实现%n",
                    label, Arrays.toString(candidates), target);
        } catch (StackOverflowError e) {
            System.out.printf("[FAIL] %-32s candidates=%-18s target=%d | StackOverflow（死循环/递归不收敛?）%n",
                    label, Arrays.toString(candidates), target);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-32s candidates=%-18s target=%d | 异常: %s%n",
                    label, Arrays.toString(candidates), target, e);
        }
    }
}
