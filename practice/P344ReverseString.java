import java.util.Arrays;

/**
 * LeetCode 344: 反转字符串 (Reverse String)
 * https://leetcode.cn/problems/reverse-string/
 *
 * <p>【难度】Easy
 * <p>【标签】数组与字符串(Array &amp; String) · 双指针(Two Pointers) · 对撞指针
 * <p>【题型】对撞指针的最简形态：left/right 从两端向中间交换
 * <p>【考点】原地 O(1) 空间；循环条件 left &lt; right（不是 &lt;=）
 * <p>【关联】P151 反转字符串中的单词（整体反转 + 局部反转）；P541 反转字符串 II
 *
 * <p>编写一个函数，其作用是将输入的字符串反转过来。输入字符串以字符数组 s 的形式给出。
 * 不要给另外的数组分配额外的空间，你必须原地修改输入数组、使用 O(1) 的额外空间解决这一问题。
 *
 * <p>约束：1 &lt;= s.length &lt;= 10^5；s[i] 都是 ASCII 码表中的可打印字符。
 */
public class P344ReverseString {

    public void reverseString(char[] s) {
        int l = 0, r = s.length - 1;
        while (l < r) {
            char temp = s[l];
            s[l] = s[r];
            s[r] = temp;
            l++;
            r--;
        }
    }

    public static void main(String[] args) {
        P344ReverseString solver = new P344ReverseString();

        run(solver, new char[]{'h', 'e', 'l', 'l', 'o'}, new char[]{'o', 'l', 'l', 'e', 'h'}, "示例1");
        run(solver, new char[]{}, new char[]{}, "空数组");
        run(solver, new char[]{'a'}, new char[]{'a'}, "单字符");
        run(solver, new char[]{'a', 'b'}, new char[]{'b', 'a'}, "两字符");
        run(solver, new char[]{'a', 'b', 'c'}, new char[]{'c', 'b', 'a'}, "奇数长度");
        run(solver, new char[]{'a', 'b', 'c', 'd'}, new char[]{'d', 'c', 'b', 'a'}, "偶数长度");
        run(solver, new char[]{'A', 'b', 'C', 'd', 'E', 'f', 'G'},
                new char[]{'G', 'f', 'E', 'd', 'C', 'b', 'A'}, "混合大小写");
    }

    private static void run(P344ReverseString solver, char[] s, char[] expected, String name) {
        char[] input = s.clone();
        String label = name + " s=" + Arrays.toString(s);
        try {
            solver.reverseString(s);
            System.out.println((Arrays.equals(s, expected) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(s) + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}