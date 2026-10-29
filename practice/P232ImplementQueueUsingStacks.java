import java.util.Deque;
import java.util.LinkedList;

/**
 * LeetCode 232: 用栈实现队列 (Implement Queue using Stacks)
 * https://leetcode.cn/problems/implement-queue-using-stacks/
 *
 * <p>【难度】Easy
 * <p>【标签】栈与队列(Stack &amp; Queue) · 设计(Design)
 * <p>【题型】双栈倒腾：inStack 只进，outStack 只出；outStack 空时把 inStack 全部倒过来
 * <p>【考点】摊还 O(1) 分析；moveIfNeeded 的条件是 outStack.isEmpty() 而不是 inStack.isEmpty()
 * <p>【关联】P225 用队列实现栈（镜像）；P155 最小栈（结构设计）
 *
 * <p>请你仅使用两个栈实现先入先出队列。队列应当支持一般队列支持的所有操作（push、pop、peek、empty）：
 * void push(int x) 将元素 x 推到队列的末尾
 * int pop() 从队列的开头移除并返回元素
 * int peek() 返回队列开头的元素
 * boolean empty() 如果队列为空，返回 true ；否则，返回 false
 *
 * <p>约束：1 &lt;= x &lt;= 9；最多调用 100 次 push、pop、peek 和 empty；题目保证 pop/peek 时队列非空。
 */
public class P232ImplementQueueUsingStacks {

    /** 用两个栈实现的队列。 */
    static class MyQueue {
        Deque<Integer> in;
        Deque<Integer> out;

        public MyQueue() {
            in = new LinkedList<>();
            out = new LinkedList<>();
        }

        public void push(int x) {
            in.push(x);
        }

        public int pop() {
            if (out.isEmpty()) {
                while (!in.isEmpty()) {
                    out.push(in.pop());
                }
            }
            return out.pop();
        }

        public int peek() {
            if (out.isEmpty()) {
                while (!in.isEmpty()) {
                    out.push(in.pop());
                }
            }
            return out.peek();
        }

        public boolean empty() {
            return in.isEmpty() && out.isEmpty();
        }
    }

    public static void main(String[] args) {
        // 用例1：push 1、2 → peek=1 → pop=1 → empty=false
        run(new String[]{"push", "push", "peek", "pop", "empty"},
                new int[]{1, 2, 0, 0, 0},
                new Object[]{null, null, 1, 1, false},
                "示例1");

        // 用例2：连续 push 三个，按 FIFO 依次弹出
        run(new String[]{"push", "push", "push", "pop", "pop", "peek", "pop", "empty"},
                new int[]{1, 2, 3, 0, 0, 0, 0, 0},
                new Object[]{null, null, null, 1, 2, 3, 3, true},
                "连续 push 后 FIFO");

        // 用例3：交替 push/pop
        run(new String[]{"push", "pop", "push", "pop", "empty"},
                new int[]{1, 0, 2, 0, 0},
                new Object[]{null, 1, null, 2, true},
                "交替 push/pop");

        // 用例4：空队列
        run(new String[]{"empty"}, new int[]{0}, new Object[]{true}, "空队列");

        // 用例5：peek 不消费元素（连续两次 peek 结果一致）
        run(new String[]{"push", "peek", "peek", "pop", "empty"},
                new int[]{7, 0, 0, 0, 0},
                new Object[]{null, 7, 7, 7, true},
                "peek 不消费");
    }

    private static void run(String[] ops, int[] args, Object[] expected, String name) {
        MyQueue q = new MyQueue();
        StringBuilder got = new StringBuilder();
        boolean pass = true;
        try {
            for (int i = 0; i < ops.length; i++) {
                Object r = null;
                switch (ops[i]) {
                    case "push" -> q.push(args[i]);
                    case "pop" -> r = q.pop();
                    case "peek" -> r = q.peek();
                    case "empty" -> r = q.empty();
                    default -> throw new IllegalArgumentException("unknown op " + ops[i]);
                }
                got.append(r == null ? "null" : r).append(i == ops.length - 1 ? "" : ",");
                if (!java.util.Objects.equals(r, expected[i])) pass = false;
            }
            System.out.println((pass ? "[PASS] " : "[FAIL] ") + name + " | got=[" + got + "]");
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + name + " | UnsupportedOperationException: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[FAIL] " + name + " | 抛异常: " + e);
        }
    }
}