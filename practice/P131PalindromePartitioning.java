import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 131: 分割回文串 (Palindrome Partitioning)
 * https://leetcode.cn/problems/palindrome-partitioning/
 *
 * <p>【难度】Medium
 * <p>【标签】回溯(Backtracking) · 动态规划 · 字符串 · 剪枝
 * <p>【题型】把"切割"映射成组合的回溯：枚举每段的结束位置，前缀须是回文才入 path
 * <p>【考点】出口 start==len；候选集 = 本层所有 end；isPalindrome 双指针 O(n) 判定（不建新串）
 * <p>【关联】P77/P216 组合模板（这里的"候选"是切割点）；P9 回文数/977 对撞指针经验
 *
 * <p>给字符串 s，将 s 分割成一些子串，使每个子串都是回文串。返回 s 所有可能的分割方案。
 *
 * <p>示例："aab" → [["a","a","b"],["aa","b"]]
 * <p>约束：1 &lt;= s.length &lt;= 16；只含小写英文字母
 */
public class P131PalindromePartitioning {

    /** 你的实现（end 为切段右边界，isPalindrome 双指针对撞，substring 拷贝收账）。 */
    public List<List<String>> partition(String s) {
        List<List<String>> res = new ArrayList<>();
        backtrack(s, 0, new ArrayList<>(), res);
        return res;
    }

    private void backtrack(String s, int start, List<String> path, List<List<String>> res) {
        if (start == s.length()) {
            res.add(new ArrayList<>(path));
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (!isPalindrome(s, start, end)) continue;
            path.add(s.substring(start, end + 1));
            backtrack(s, end + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    private boolean isPalindrome(String s, int l, int r) {
        while (l < r) {
            if (s.charAt(l) == s.charAt(r)) {
                l++;
                r--;
            } else {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        P131PalindromePartitioning solver = new P131PalindromePartitioning();

        run(solver, "aab", "示例1 → [a|a|b],[aa|b]");
        run(solver, "a", "单字符 1 种");
        run(solver, "aba", "全回文：[a|b|a],[aba]");
        run(solver, "aaa", "三个相同：[a|a|a],[aa|a],[a|aa],[aaa]");
        run(solver, "abc", "无回文段可拼 → 只能 [a|b|c]");
        run(solver, "aabaa", "两端回文嵌套");
        run(solver, "abacdc", "混合");
        run(solver, "aaaaaaaaaa", "十个 a（压力：全 512 切法）");
        run(solver, "amanaplanacanalpanama", "回文长句");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：位掩码枚举"每道刀位"（第 i 位=1 表示在 i 后切一刀），
     * 逐段验证回文，收集全部合法分割方案（无重复、天然全序）。
     */
    private static List<List<String>> bruteForce(String s) {
        List<List<String>> res = new ArrayList<>();
        int n = s.length();
        int cuts = n - 1;
        for (int mask = 0; mask < (1 << cuts); mask++) {
            List<String> segs = new ArrayList<>();
            List<Integer> cutAfter = new ArrayList<>();
            for (int i = 0; i < cuts; i++) {
                if ((mask & (1 << i)) != 0) cutAfter.add(i);
            }
            boolean ok = true;
            int st = 0;
            for (int c : cutAfter) {
                String seg = s.substring(st, c + 1);
                if (!isPal(seg)) { ok = false; break; }
                segs.add(seg);
                st = c + 1;
            }
            if (!ok) continue;
            String last = s.substring(st);
            if (!isPal(last)) continue;
            segs.add(last);
            res.add(segs);
        }
        return res;
    }

    private static boolean isPal(String t) {
        int l = 0, r = t.length() - 1;
        while (l < r) {
            if (t.charAt(l) != t.charAt(r)) return false;
            l++; r--;
        }
        return true;
    }

    /** 归一化：方案内保持元素顺序（分割方案有序），方案间整体排序。 */
    private static List<String> normalize(List<List<String>> got) {
        List<String> keys = new ArrayList<>();
        for (List<String> segs : got) {
            String key = String.join("\u0001", segs);
            keys.add(key);
        }
        keys.sort(null);
        return keys;
    }

    private static void run(P131PalindromePartitioning solver, String s, String label) {
        try {
            List<List<String>> got = solver.partition(s);
            List<List<String>> want = bruteForce(s);

            List<String> g = normalize(got), w = normalize(want);
            // 先去重检查同一方案重复出现
            boolean ok = new java.util.LinkedHashSet<>(g).size() == g.size();
            g = new ArrayList<>(new java.util.LinkedHashSet<>(g));
            ok = ok && g.equals(w);

            System.out.printf("[%s] %-34s s=%-24s | 暴力=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, "\"" + s + "\"", want.size(), got.size());
            if (!ok) {
                System.out.println("    ---- 暴力（缺/多/重）----");
                java.util.LinkedHashSet<String> W = new java.util.LinkedHashSet<>(w);
                for (String k : W) if (!g.contains(k)) System.out.println("    缺  " + k);
                for (String k : g) if (!W.contains(k)) System.out.println("    多  " + k);
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s s=%-24s | TODO 未实现%n", label, "\"" + s + "\"");
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s s=%-24s | 异常: %s%n", label, "\"" + s + "\"", e);
        }
    }
}
