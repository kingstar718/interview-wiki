import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Deque;

/**
 * LeetCode 155: 最小栈 (Min Stack)
 * https://leetcode.cn/problems/min-stack/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·栈与堆组 1/3——辅助栈同步）
 *
 * <p>■ 题目描述
 *   设计 MinStack：
 *   - push(int val)：入栈
 *   - pop()：出栈（栈顶）
 *   - int top()：看栈顶
 *   - int getMin()：**O(1)** 取当前栈内最小值
 *
 * <p>■ 输入输出（约束）
 *   -2^31 ≤ val ≤ 2^31-1；pop/top/getMin 总在非空栈上调用；
 *   push/pop/top/getMin 总调用数 ≤ 3×10^4
 *
 * <p>■ 示例逐条（官方序列）
 * <pre>
 * push(-2); push(0); push(-3);
 * getMin() → -3
 * pop()
 * top() → 0
 * getMin() → -2   ← ★ -3 已弹出，最小值回退到 -2（不是历史最小）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★·字节/美团）
 * <p>【标签】栈 · 设计
 * <p>【题型】**辅助栈同步**：minStack 逐层压 min(val, 当前最小)——栈顶=该状态的最小值
 * <p>【考点】**最小值跟着状态走不是历史最小**（弹出后要回退——getMin 的-3→-2 正是杀历史最小版）；
 * <p>　　　　同步压同步弹（两栈高度永远相等）；辅助栈空时直接压 val
 * <p>【关联】P20/1047（栈基本功）；P739/496（单调栈是另一支）；P394（双栈嵌套）
 *
 * <p>LCS 签名：MinStack(int) 构造 + push/pop/top/getMin。
 */
public class P155MinStack {

    /** 你的实现（双 Deque 辅助栈：每层快照 min(新值,上快照)，注释自写不变式）。 */
    Deque<Integer> stack;
    Deque<Integer> minStack;

    // TODO: 实现 MinStack（本题唯一入口；可加私有成员）
    public P155MinStack() {
        stack = new LinkedList<>();
        minStack = new LinkedList<>();
    }

    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty()) {
            minStack.push(val);
        } else {
            // min存的是每一次压栈状态的最小值，而不是全栈最小值
            minStack.push(Math.min(val, minStack.peek()));
        }
    }

    public void pop() {
        stack.pop();
        minStack.pop();
    }

    public int top() {
        if (!stack.isEmpty()) {
            return stack.peek();
        } else {
            return -1;
        }
    }

    public int getMin() {
        if (!minStack.isEmpty()) {
            return minStack.peek();
        } else {
            return -1;
        }
    }
    public static void main(String[] args) {
        System.out.println("=== 官方序列 ===");
        P155MinStack solver = null;   // 占位：155 为操作序列型（状态在各实例里）
        P155MinStack s = new P155MinStack();
        s.push(-2);
        s.push(0);
        s.push(-3);
        run(solver, "getMin=-3", s, "getMin", -3);
        s.pop();
        run(solver, "top=0", s, "top", 0);
        run(solver, "getMin=-2（回退）", s, "getMin", -2);

        System.out.println("=== 相等/重复最小值 ===");
        P155MinStack d = new P155MinStack();
        d.push(1);
        d.push(1);
        d.push(1);
        run(solver, "getMin=1（全等）", d, "getMin", 1);
        d.pop();
        run(solver, "getMin=1（弹一个仍 1）", d, "getMin", 1);
        d.pop();
        d.pop();
        d.push(5);
        run(solver, "getMin=5（清空重压）", d, "getMin", 5);

        System.out.println("=== 负数与回升 ===");
        P155MinStack e = new P155MinStack();
        e.push(-5);
        e.push(3);
        run(solver, "top=3", e, "top", 3);
        run(solver, "getMin=-5", e, "getMin", -5);
        e.pop();
        run(solver, "getMin=-5（单元素）", e, "getMin", -5);
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：全栈扫描求最小（bruteForce）——机制与辅助栈不同。 */
    private static int bruteForce(java.util.Deque<Integer> st) {
        int m = Integer.MAX_VALUE;
        for (int v : st) m = Math.min(m, v);
        return m;
    }

    /** 操作序列对账：mirror 记录与用户实现同步的暴力栈。 */
    private static java.util.Deque<Integer> mirror = new java.util.ArrayDeque<>();
    private static void run(P155MinStack solver, String label, P155MinStack s, String op, int expect) {
        int got;
        switch (op) {
            case "getMin": got = s.getMin(); break;
            case "top": got = s.top(); break;
            default: got = expect; break;
        }
        boolean ok = got == expect;
        boolean refOk = bruteForce(mirror) == expect;
        System.out.printf("[%s] %-26s | 参考=%d 实得=%d%s%n",
                ok ? "PASS" : "FAIL", label, expect, got, refOk ? "" : " ⚠️与暴力参考不一致");
    }
}
