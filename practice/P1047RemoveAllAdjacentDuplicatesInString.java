/**
 * LeetCode 1047: 删除字符串中的所有相邻重复项 (Remove All Adjacent Duplicates In String)
 * https://leetcode.cn/problems/remove-all-adjacent-duplicates-in-string/
 *
 * <p>【难度】Easy
 * <p>【标签】栈与队列(Stack & Queue) · 字符串(String)
 * <p>【题型】栈做“就近消除”：栈顶 == 当前字符就弹出，否则压入
 * <p>【考点】删除后产生新相邻重复项，需要“回头看”上一个未删除的字符 → LIFO
 * <p>【关联】P20 有效的括号（同为栈“就近”场景）；P27 移除元素（快慢指针原地版）
 *
 * <p>给出由小写字母组成的字符串 s，重复项删除操作会选择两个相邻且相同的字母，并删除它们。
 * 在 s 上反复执行重复项删除操作，直到无法继续删除。返回最终的字符串。
 *
 * <p>示例：s = "abbaca" → "ca"（删除 "bb" 得 "aaca"，再删除 "aa" 得 "ca"）
 *
 * <p>约束：1 <= s.length <= 10^5；s 仅由小写英文字母组成。
 */
public class P1047RemoveAllAdjacentDuplicatesInString {

    public String removeDuplicates(String s) {
        StringBuilder sb = new StringBuilder();
        for (Character c : s.toCharArray()) {
            int len = sb.length();
            if (len > 0 && c == sb.charAt(len - 1)) {
                sb.deleteCharAt(len - 1);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        P1047RemoveAllAdjacentDuplicatesInString solver = new P1047RemoveAllAdjacentDuplicatesInString();

        run(solver, "abbaca", "ca", "示例1");
        run(solver, "azxxzy", "ay", "示例2");
        run(solver, "", "", "空字符串");
        run(solver, "abc", "abc", "无相邻重复");
        run(solver, "aabb", "", "全消");
        run(solver, "a", "a", "单字符");
        run(solver, "aa", "", "一对");
        run(solver, "aaa", "a", "奇数个");
        run(solver, "aaaa", "", "偶数个");
        run(solver, "abba", "", "首尾相消");
        run(solver, "abcba", "abcba", "无相邻重复回文");
        run(solver, "aabbccddeeff", "", "全偶数");
    }

    private static void run(P1047RemoveAllAdjacentDuplicatesInString solver, String s, String expected, String name) {
        String label = name + " s=\"" + s + "\"";
        try {
            String got = solver.removeDuplicates(s);
            System.out.println((expected.equals(got) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=\"" + got + "\" expected=\"" + expected + "\"");
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}