/**
 * LeetCode 151: 反转字符串中的单词 (Reverse Words in a String)
 * https://leetcode.cn/problems/reverse-words-in-a-string/
 *
 * <p>【难度】Medium
 * <p>【标签】数组与字符串(Array &amp; String) · 双指针(Two Pointers) · 原地读写
 * <p>【题型】三步法：去多余空格 → 整体反转 → 逐词反转
 * <p>【考点】双指针原地去空格（27 的迁移）；整体反转 + 局部反转的组合（344 的迁移）；边界处理
 * <p>【关联】P344ReverseString（反转工具）；P27RemoveElement（原地删除思想）
 *
 * <p>给你一个字符串 s，请你反转字符串中单词的顺序。
 * 单词是由非空格字符组成的字符串。s 中使用至少一个空格将字符串中的单词分隔开。
 * 返回单词顺序颠倒且单词之间用单个空格连接的结果字符串。
 * 注意：输入字符串 s 中可能会存在前导空格、尾随空格或者单词间的多个空格。
 * 返回的结果字符串中，单词间应当仅用单个空格分隔，且不包含任何额外的空格。
 *
 * <p>约束：1 &lt;= s.length &lt;= 10^4；s 包含英文大小写字母、数字和空格 ' '；s 中至少存在一个单词。
 *
 * <p>── 思路要点 ──
 * <p>1. 先去掉多余空格（首尾空格 + 单词间连续的多个空格），用双指针原地压缩（参考 27）。
 * <p>2. 把整个有效部分反转 —— 此时单词顺序反了，但每个单词内部也是反的。
 * <p>3. 再把每个单词内部反转回来 —— 单词顺序正确、内部也正确。
 * <p>4. 最后返回 new String(chars, 0, newLen)，注意别把尾部残留字符带出来。
 */
public class P151ReverseWordsInAString {

    public String reverseWords(String s) {
        char[] chars = s.toCharArray();
        int n = s.length();
        int slow = 0;
        for (int fast = 0; fast < n; fast++) {
            if (chars[fast] != ' ') {
                if (slow > 0) {
                    chars[slow++] = ' ';
                }
                while (fast < n && chars[fast] != ' ') {
                    chars[slow++] = chars[fast++];
                }
            }
        }
        int newLen = slow;

        reverse(chars, 0, newLen - 1);

        int start = 0;
        for (int i = 0; i <= newLen; i++) {
            if (i == newLen || chars[i] == ' ') {
                reverse(chars, start, i - 1);
                start = i + 1;
            }
        }
        return new String(chars, 0, newLen);
    }

    private void reverse(char[] chars, int start, int end) {
        while (start < end) {
            char temp = chars[start];
            chars[start] = chars[end];
            chars[end] = temp;
            start++;
            end--;
        }
    }

    public static void main(String[] args) {
        P151ReverseWordsInAString solver = new P151ReverseWordsInAString();

        run(solver, "the sky is blue", "blue is sky the", "示例1");
        run(solver, "  hello world  ", "world hello", "示例2：首尾空格");
        run(solver, "a good   example", "example good a", "单词间多空格");
        run(solver, "   ", "", "全空格");
        run(solver, "single", "single", "单单词");
        run(solver, "  single  ", "single", "单单词带空格");
        run(solver, "  Bob    Loves  Alice   ", "Alice Loves Bob", "首尾+多空格");
        run(solver, "a b", "b a", "两单词");
        run(solver, "asdasd df f", "f df asdasd", "普通三单词");
    }

    private static void run(P151ReverseWordsInAString solver, String s, String expected, String name) {
        String label = name + " s=\"" + s + "\"";
        try {
            String got = solver.reverseWords(s);
            System.out.println((expected.equals(got) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=\"" + got + "\" expected=\"" + expected + "\"");
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}