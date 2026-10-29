/**
 * LeetCode 239: 滑动窗口最大值 (Sliding Window Maximum)
 * https://leetcode.cn/problems/sliding-window-maximum/
 *
 * <p>【难度】Hard
 * <p>【标签】双指针与滑动窗口(Sliding Window) · 栈与队列(Stack & Queue) · 单调队列
 * <p>【题型】单调递减双端队列（存下标）：队头永远当前窗口最大值的下标
 * <p>【考点】队尾弹掉"更小且更旧"的候选；队头按【下标】判断是否滑出窗口；出结果时机 i >= k-1
 * <p>【关联】P739 每日温度（单调栈版淘汰候选）；堆方案 O(n log n) 对比
 *
 * <p>给你一个整数数组 nums，有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。
 * 你只可以看到在滑动窗口内的 k 个数字。滑动窗口每次只向右移动一位。
 * 返回「滑动窗口中的最大值」组成的数组。
 *
 * <p>约束：1 &lt;= nums.length &lt;= 10^5；-10^4 &lt;= nums[i] &lt;= 10^4；1 &lt;= k &lt;= nums.length。
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class P239SlidingWindowMaximum {

    public int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> dq = new ArrayDeque<>();
        int n = nums.length;
        int[] res = new int[n - k + 1];
        for (int i = 0; i < n; i++) {
            while (!dq.isEmpty() && (nums[dq.peekLast()] <= nums[i])) {
                dq.pollLast();
            }
            dq.offerLast(i);
            if (dq.peekFirst() <= i - k) {
                dq.pollFirst();
            }
            if (i >= k - 1) {
                res[i - k + 1] = nums[dq.peekFirst()];
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P239SlidingWindowMaximum solver = new P239SlidingWindowMaximum();

        run(solver, new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3, new int[]{3, 3, 5, 5, 6, 7}, "示例1");
        run(solver, new int[]{1}, 1, new int[]{1}, "单元素");
        run(solver, new int[]{1, -1}, 1, new int[]{1, -1}, "示例2 k=1");
        run(solver, new int[]{9, 11}, 2, new int[]{11}, "示例 k=n");
        run(solver, new int[]{4, -2}, 2, new int[]{4}, "k=n 含负数");
        run(solver, new int[]{1, 2, 3, 4, 5}, 2, new int[]{2, 3, 4, 5}, "递增数组");
        run(solver, new int[]{5, 4, 3, 2, 1}, 2, new int[]{5, 4, 3, 2}, "递减数组");
        run(solver, new int[]{7, 2, 4}, 2, new int[]{7, 4}, "示例3");
        run(solver, new int[]{2, 2, 2}, 2, new int[]{2, 2}, "全相等");
        run(solver, new int[]{1, 3, -1}, 3, new int[]{3}, "k=n 全局最大");
        run(solver, new int[]{-1, -2, -3, -4}, 2, new int[]{-1, -2, -3}, "全负数递减");
    }

    private static void run(P239SlidingWindowMaximum solver, int[] nums, int k, int[] expected, String name) {
        String label = name + " nums=" + java.util.Arrays.toString(nums) + " k=" + k;
        try {
            int[] got = solver.maxSlidingWindow(nums, k);
            System.out.println((java.util.Arrays.equals(got, expected) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + java.util.Arrays.toString(got) + " expected=" + java.util.Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}