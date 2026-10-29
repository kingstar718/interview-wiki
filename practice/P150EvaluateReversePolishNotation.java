/**
 * LeetCode 150: 逆波兰表达式求值 (Evaluate Reverse Polish Notation)
 * https://leetcode.cn/problems/evaluate-reverse-polish-notation/
 *
 * <p>【难度】Medium
 * <p>【标签】栈与队列(Stack & Queue) · 数组(Array) · 数学(Math)
 * <p>【题型】栈求后缀表达式：遇数字压栈，遇运算符弹两个数算完再压回
 * <p>【考点】弹出顺序（先弹的是右操作数 b，后弹的是左操作数 a）；Java 整数除法向零截断
 * <p>【关联】P20 有效的括号；表达式/计算器系列（224/227）
 *
 * <p>给你一个字符串数组 tokens，表示一个根据逆波兰表示法表示的算术表达式。
 * 请你计算该表达式。返回一个表示表达式值的整数。
 * 有效的算符为 '+'、'-'、'*' 和 '/'。
 * 每个操作数（运算对象）都可以是一个整数或者另一个表达式。
 *
 * <p>约束：1 &lt;= tokens.length &lt;= 10^4；tokens[i] 是一个算符或 [-200, 200] 内的整数；
 * 表达式总是有效的（即表达式总可以求出一个有效值）；除法总是向零截断。
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class P150EvaluateReversePolishNotation {

    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (String s : tokens) {
            if (!s.equals("+") && !s.equals("-") && !s.equals("*") && !s.equals("/")) {
                stack.push(Integer.valueOf(s));
            } else {
                Integer s2 = stack.pop();
                Integer s1 = stack.pop();
                if (s.equals("+")) {
                    stack.push(s1 + s2);
                } else if (s.equals("-")) {
                    stack.push(s1 - s2);
                } else if (s.equals("*")) {
                    stack.push(s1 * s2);
                } else if (s.equals("/")) {
                    stack.push(s1 / s2);
                }
            }
        }
        return stack.pop();
    }

    public static void main(String[] args) {
        P150EvaluateReversePolishNotation solver = new P150EvaluateReversePolishNotation();

        run(solver, new String[]{"2", "1", "+", "3", "*"}, 9, "示例1 (2+1)*3");
        run(solver, new String[]{"4", "13", "5", "/", "+"}, 6, "示例2 4+13/5");
        run(solver, new String[]{"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"},
                22, "示例3 长表达式");
        run(solver, new String[]{"5"}, 5, "单操作数");
        run(solver, new String[]{"-3"}, -3, "负数操作数");
        run(solver, new String[]{"1", "2", "+"}, 3, "加法");
        run(solver, new String[]{"2", "3", "-"}, -1, "减法顺序 a-b");
        run(solver, new String[]{"2", "3", "+", "4", "*"}, 20, "(2+3)*4");
        run(solver, new String[]{"7", "-2", "/"}, -3, "除法向零截断 7/-2");
        run(solver, new String[]{"-7", "2", "/"}, -3, "除法向零截断 -7/2");
        run(solver, new String[]{"3", "4", "*", "2", "/"}, 6, "3*4/2");
    }

    private static void run(P150EvaluateReversePolishNotation solver, String[] tokens, int expected, String name) {
        String label = name + " tokens=" + java.util.Arrays.toString(tokens);
        try {
            int got = solver.evalRPN(tokens);
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " expected=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}