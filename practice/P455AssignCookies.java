import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 455: 分发饼干 (Assign Cookies)
 * https://leetcode.cn/problems/assign-cookies/
 *
 * <p>【难度】Easy
 * <p>【标签】贪心(Greedy) · 数组 · 排序 · 双指针
 * <p>【题型】贪心入门典题：最小饼干喂最小胃口，"局部最优不浪费大饼干 → 全局最优"
 * <p>【考点】先排序（贪心的前提是可比）；双指针滑动；答案 = 孩子指针挪了多少次
 * <p>【关联】贪心章三条线之"入门"；面试追问：为什么局部最优不会错过全局最优（交换论证）
 *
 * <p>胃口 g[i]，饼干 s[j]，s[j] &gt;= g[i] 可满足孩子 i（每人最多一块）。求最多满足几个孩子。
 *
 * <p>示例：g=[1,2,3], s=[1,1] → 1；g=[1,2], s=[1,2,3] → 2
 * <p>约束：1 &lt;= g.length, s.length &lt;= 3*10^4；1 &lt;= g[i], s[j] &lt;= 2^31-1
 */
public class P455AssignCookies {

    /** 你的实现（双层 while + 显式早归——功能与标准版等价，冗余稍后收干净）。 */
    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int i = 0, j = 0;
        if (s.length == 0) {
            return 0;
        }
        while (i < g.length) {
            while (j < s.length) {
                if (s[j] >= g[i]) {
                    i++;
                }
                j++;
                if (i == g.length || j == s.length) {
                    return i;
                }
            }
            i++;
        }
        return i;
    }

    public static void main(String[] args) {
        P455AssignCookies solver = new P455AssignCookies();

        run(solver, new int[]{1, 2, 3}, new int[]{1, 1}, 1, "示例1 官方");
        run(solver, new int[]{1, 2}, new int[]{1, 2, 3}, 2, "示例2 官方");
        run(solver, new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3}, 3, "饼干少 → 满足前 3 个");
        run(solver, new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5}, 5, "乱序（排序后恰好全满足）");
        run(solver, new int[]{10, 9, 8}, new int[]{}, 0, "空饼干 → 0");
        run(solver, new int[]{}, new int[]{1, 2}, 0, "空孩子 → 0");
        run(solver, new int[]{1, 2, 3}, new int[]{3}, 1, "大饼干只够一个（不能切分）");
        run(solver, new int[]{2, 2, 2}, new int[]{2, 2}, 2, "全等值配对");
        run(solver, new int[]{7, 8, 9, 10}, new int[]{5, 6, 7, 8}, 2, "部分满足 7/8,8/9? → 8<=8,9>8 只满足 7,8 两个");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：回溯枚举"每个孩子 吃哪块/不吃"的全部组合，记录最大满足数。
     * 与贪心机制完全独立（不排序、不双指针），规模小可信。
     */
    private static int bruteForce(int[] g, int[] s) {
        return dfs(g, s, new boolean[s.length], 0, 0);
    }

    private static int dfs(int[] g, int[] s, boolean[] usedCookie, int child, int satisfied) {
        if (child == g.length) {
            return satisfied;
        }
        int best;
        best = dfs(g, s, usedCookie, child + 1, satisfied);          // 选项A：这孩子不吃
        for (int j = 0; j < s.length; j++) {                          // 选项B：吃某一块
            if (usedCookie[j] || s[j] < g[child]) continue;
            usedCookie[j] = true;
            best = Math.max(best, dfs(g, s, usedCookie, child + 1, satisfied + 1));
            usedCookie[j] = false;
        }
        return best;
    }

    private static void run(P455AssignCookies solver, int[] g, int[] s, int expect, String label) {
        try {
            int got = solver.findContentChildren(Arrays.copyOf(g, g.length), Arrays.copyOf(s, s.length));
            int bf = bruteForce(Arrays.copyOf(g, g.length), Arrays.copyOf(s, s.length));
            boolean ok = got == bf;
            if (bf != expect) {
                System.out.printf("[WARN] %-44s 期望值手填 %d 与暴力 %d 不符——以暴力为准%n", label, expect, bf);
            }
            System.out.printf("[%s] %-40s g=%-16s s=%-16s | 期望=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(g), Arrays.toString(s), bf, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-40s g=%-16s s=%-16s | TODO 未实现%n", label, Arrays.toString(g), Arrays.toString(s));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-40s g=%-16s s=%-16s | 异常: %s%n", label, Arrays.toString(g), Arrays.toString(s), e);
        }
    }
}
