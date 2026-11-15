import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * LeetCode 739: 每日温度 (Daily Temperatures)
 * https://leetcode.cn/problems/daily-temperatures/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（单调栈章 1/3——单调递减栈原型）
 *
 * <p>■ 题目描述
 *   给每日气温数组 temperatures（整数，表示摄氏度）。
 *   返回数组 answer，answer[i] = 第 i 天之后【至少等多少天】才能等到
 *   【更高】的气温；如果之后都没有更高，answer[i] = 0。
 *
 * <p>■ 输入输出
 *   输入：int[] temperatures（1 ≤ length ≤ 10^5；30 ≤ temperatures[i] ≤ 100）
 *   输出：int[]（等待天数数组）
 *
 * <p>■ 示例逐条
 * <pre>
 * temperatures=[73,74,75,71,69,72,76,73]
 *   → [1,1,4,2,1,1,0,0]
 *   （73 等 1 天（74）、74 等 1 天（75）、75 等 4 天（76）、71 等 2 天（72）……）
 * temperatures=[30,30,30] → [0,0,0]（等温不算"更高"——严格大于）
 * temperatures=[100]      → [0]（单日）
 * temperatures=[1,2]      → [1,0]
 * temperatures=[2,1]      → [0,0]（递减无希望）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】单调栈 · 数组
 * <p>【题型】"下一个更大元素"原型：单调递减栈 + **栈里存下标**（不是值）——
 * <p>　　　　新元素把"比它小的一切"弹出，弹出的瞬间就是答案产生的时刻
 * <p>【考点】存下标不存值（要算"距离"）；严格大于（等温不触发）；O(n)（每元素最多出入栈各一次）
 * <p>【关联】P496（映射表加成）；P42（Hard 接雨水）；P239（滑动窗口——队列的另一形态）
 *
 * <p>LCS 签名：dailyTemperatures(int[] temperatures)。
 */
public class P739DailyTemperatures {

    /** 你的实现（单调递减栈：四空填法=存下标/新值>栈顶弹/弹时算距离/剩余默认0）。 */
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prevIndex = stack.pop();
                result[prevIndex] = i - prevIndex;
            }
            stack.push(i);
        }
        return result;
    }

    public static void main(String[] args) {
        P739DailyTemperatures solver = new P739DailyTemperatures();

        run(solver, new int[]{73, 74, 75, 71, 69, 72, 76, 73}, "官方例1 → [1,1,4,2,1,1,0,0]");
        run(solver, new int[]{30, 30, 30}, "全等温 → [0,0,0]（严格！）");
        run(solver, new int[]{100}, "单日 → [0]");
        run(solver, new int[]{1, 2}, "纯增 → [1,0]");
        run(solver, new int[]{2, 1}, "纯减 → [0,0]");
        run(solver, new int[]{89, 62, 70, 58, 47, 47, 46, 44, 40, 25},
                "LC 压力例（含等温 47）→ 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：对每一天向右线性找第一个严格更高——O(n²)，机制与单调栈不同。 */
    private static int[] bruteForce(int[] temps) {
        int n = temps.length;
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (temps[j] > temps[i]) { ans[i] = j - i; break; }
            }
        }
        return ans;
    }

    private static void run(P739DailyTemperatures solver, int[] temps, String label) {
        try {
            int[] got = solver.dailyTemperatures(Arrays.copyOf(temps, temps.length));
            int[] want = bruteForce(Arrays.copyOf(temps, temps.length));
            boolean ok = Arrays.equals(got, want);

            System.out.printf("[%s] %-42s temps=%-40s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(temps),
                    Arrays.toString(want), Arrays.toString(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-42s temps=%-40s | TODO 未实现%n", label, Arrays.toString(temps));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-42s temps=%-40s | 异常: %s%n", label, Arrays.toString(temps), e);
        }
    }
}
