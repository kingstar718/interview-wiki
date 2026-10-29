/**
 * LeetCode 20: 有效的括号 (Valid Parentheses)
 * https://leetcode.cn/problems/valid-parentheses/
 *
 * <p>【难度】Easy
 * <p>【标签】栈与队列(Stack &amp; Queue) · 字符串(String)
 * <p>【题型】栈做“就近匹配”：最后打开的括号必须最先关闭
 * <p>【考点】右括号先判空再 pop；遍历结束栈必须为空；三种括号类型映射
 * <p>【关联】P150 逆波兰表达式求值；P1047 删除相邻重复项（同为栈“就近”场景）
 *
 * <p>给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s，判断字符串是否有效。
 * 有效字符串需满足：
 * 1) 左括号必须用相同类型的右括号闭合。
 * 2) 左括号必须以正确的顺序闭合。
 * 3) 每个右括号都有一个对应的相同类型的左括号。
 *
 * <p>约束：1 &lt;= s.length &lt;= 10^4；s 仅由括号组成。
 */
import java.util.ArrayDeque;
import java.util.Deque;

public class P20ValidParentheses {

    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        char[] chars = s.toCharArray();
        for (Character c : chars) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }
                Character pop = stack.pop();
                if ((pop == '(' && c != ')') ||
                        (pop == '[' && c != ']') ||
                        (pop == '{' && c != '}')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        P20ValidParentheses solver = new P20ValidParentheses();

        run(solver, "()", true, "示例1");
        run(solver, "()[]{}", true, "示例2");
        run(solver, "(]", false, "示例3");
        run(solver, "([)]", false, "交叉嵌套");
        run(solver, "{[]}", true, "正确嵌套");
        run(solver, "", true, "空字符串");
        run(solver, "(", false, "只有左括号");
        run(solver, ")", false, "只有右括号");
        run(solver, "(((", false, "全左括号");
        run(solver, "]]", false, "全右括号");
        run(solver, "(([]){})", true, "多层嵌套");
        run(solver, "(((((((((())))))))))", true, "深嵌套");
        run(solver, "{]", false, "左大括号配右方括号");
        run(solver, "[}", false, "左方括号配右大括号");
    }

    private static void run(P20ValidParentheses solver, String s, boolean expected, String name) {
        String label = name + " s=\"" + s + "\"";
        try {
            boolean got = solver.isValid(s);
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " expected=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}