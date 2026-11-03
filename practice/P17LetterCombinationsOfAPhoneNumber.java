import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 17: 电话号码的字母组合 (Letter Combinations of a Phone Number)
 * https://leetcode.cn/problems/letter-combinations-of-a-phone-number/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 哈希表 · 字符串
 * <p>【题型】每个层候选择集合都不同的回溯：数字串第 idx 位决定本层能选哪些字母
 * <p>【考点】不需要 startIndex（集合由层决定，天然无重复枚举）；路径用 StringBuilder；char→字母表映射
 * <p>【关联】P77/P216 的 start 由"同集合递增删除"；本题集合每层都不同，等价的"防重复"由层序天然保证
 *
 * <p>给一个只含 2~9 的数字串，返回所有可能的字母组合（按电话键盘映射）。
 *
 * <p>示例：digits="23" → ["ad","ae","af","bd","be","bf","cd","ce","cf"]
 * <p>约束：0 &lt;= digits.length &lt;= 4（**注意：空串返回空列表，不是 [""]**）；digits[i] ∈ ['2','9']
 */
public class P17LetterCombinationsOfAPhoneNumber {

    /** 你的实现（Map 版 + 入口判空 + connectLayer 传递 mapping）。 */
    public List<String> letterCombinations(String digits) {
        Map<Character, String> MAPPING = new HashMap<>();
        MAPPING.put('2', "abc");
        MAPPING.put('3', "def");
        MAPPING.put('4', "ghi");
        MAPPING.put('5', "jkl");
        MAPPING.put('6', "mno");
        MAPPING.put('7', "pqrs");
        MAPPING.put('8', "tuv");
        MAPPING.put('9', "wxyz");

        List<String> res = new ArrayList<>();
        if (digits.isEmpty()) {
            return res;
        }
        backtrack(digits, MAPPING, 0, new StringBuilder(), res);
        return res;
    }

    private void backtrack(String digits, Map<Character, String> mapping, int start, StringBuilder path, List<String> res) {
        if (digits.length() == start) {
            res.add(path.toString());
            return;
        }
        char s = digits.charAt(start);
        String s2 = mapping.get(s);

        for (int i = 0; i < s2.length(); i++) {
            char charAt = s2.charAt(i);
            path.append(charAt);
            backtrack(digits, mapping, start + 1, path, res);
            path.deleteCharAt(path.length() - 1);
        }
    }

    public static void main(String[] args) {
        P17LetterCombinationsOfAPhoneNumber solver = new P17LetterCombinationsOfAPhoneNumber();

        run(solver, "23", 9, Arrays.asList("ad", "ae", "af", "bd", "be", "bf", "cd", "ce", "cf"),
                "示例1 两个数字");
        run(solver, "", 0, Arrays.asList(),
                "空串 → 空列表（不是 [\"\"]）");
        run(solver, "2", 3, Arrays.asList("a", "b", "c"),
                "单数字 3 组");
        run(solver, "7", 4, Arrays.asList("p", "q", "r", "s"),
                "7 和 9 是四字母键");
        run(solver, "234", 27, null, "三个数字 3×3×3=27 组");
        run(solver, "79", 16, null, "四字母键×四字母键=16 组");
        run(solver, "23456789", 11664, null, "全数字 8 位 3^6·4^2=11664 组（压力）");
        run(solver, "2222", 81, null, "重复数字也正常展开");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static final Map<Character, String> KEY = new HashMap<>();
    static {
        KEY.put('2', "abc"); KEY.put('3', "def"); KEY.put('4', "ghi");
        KEY.put('5', "jkl"); KEY.put('6', "mno"); KEY.put('7', "pqrs");
        KEY.put('8', "tuv"); KEY.put('9', "wxyz");
    }

    /** 暴力参考（笛卡尔积）用于核对数量；期望列表题直接比对内容。 */
    private static List<String> bruteForce(String digits) {
        List<String> res = Arrays.asList("");
        for (int idx = 0; idx < digits.length(); idx++) {
            String letters = KEY.get(digits.charAt(idx));
            List<String> next = new ArrayList<>();
            for (String prefix : res) {
                for (int j = 0; j < letters.length(); j++) {
                    next.add(prefix + letters.charAt(j));
                }
            }
            res = next;
        }
        return res;
    }

    private static void run(P17LetterCombinationsOfAPhoneNumber solver, String digits,
                            int expectCount, List<String> expect, String label) {
        try {
            List<String> got = solver.letterCombinations(digits);

            boolean ok;
            if (expect != null) {
                List<String> g = new ArrayList<>(got), e = new ArrayList<>(expect);
                g.sort(null); e.sort(null);
                ok = g.equals(e);
            } else {
                List<String> bf = bruteForce(digits);
                List<String> g = new ArrayList<>(got);
                g.sort(null); bf.sort(null);
                ok = g.equals(bf);
            }

            System.out.printf("[%s] %-30s digits=%-9s | 期望=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, "\"" + digits + "\"", expectCount, got.size());
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s digits=%-9s | TODO 未实现%n", label, "\"" + digits + "\"");
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s digits=%-9s | 异常: %s%n", label, "\"" + digits + "\"", e);
        }
    }
}
