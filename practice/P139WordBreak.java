import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LeetCode 139: 单词拆分 (Word Break)
 * https://leetcode.cn/problems/word-break/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（背包组 8/8 收官——字符串上的完全背包判定）
 *
 * <p>■ 题目描述
 *   给一个字符串 s 和一个单词列表 wordDict（单词互不相同？LeetCode 保证可
 *   包含重复？——以官方为准：任意非空字符串列表）。判断 s 是否能由
 *   wordDict 中的单词【拼接而成】：
 *     1) 拼接 = 若干单词依次相连，不留空隙、不重叠；
 *     2) 字典中的单词【可以重复使用】（同一单词可用任意多次）；
 *     3) 拼接不必用完字典里的全部单词（字典是"可用工具箱"而非"必须清单"）。
 *   能拼成 → true；不能 → false。
 *
 * <p>■ 输入输出
 *   输入：String s（1 ≤ s.length ≤ 300）、List&lt;String&gt; wordDict（1 ≤ wordDict.length ≤ 20;
 *   　　　　1 ≤ wordDict[i].length ≤ 20；含小写英文字母；s 和 wordDict[i] 均由小写组成）
 *   输出：boolean
 *
 * <p>■ 示例逐条
 * <pre>
 * s="leetcode", wordDict=["leet","code"] → true（leet + code，无缝）
 * s="applepenapple", wordDict=["apple","pen"] → true（apple+pen+apple——同词可复用！）
 * s="catsandog", wordDict=["cats","dog","sand","and","cat"] → false
 * 　　（cat+? / cats+? 均在 "andog"/"sandog" 处卡死——参考实现可复核）
 * s=""（空串，不在约束内——防御） → true（空串=0 个单词的拼接）
 * s="a", wordDict=["a"] → true
 * s="ab", wordDict=["a","b"] → true
 * s="aaaaaaa", wordDict=["aaaa","aaa"] → true（aaa×2? aaaa+aaa=7 ✓ 参考定）
 * </pre>
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】动态规划(DP) · 完全背包（判定型）· 字符串 · 记忆化 DFS（对照）
 * <p>【题型】"前 i 个字符能否拆"：dp[i] = OR over j&lt;i (dp[j] && dict.contains(s[j..i)))
 * <p>　　　　——外层容量 i 自增、内层枚举"最后一段从哪断开"（377 末步分箱的字符串版）
 * <p>【考点】"完全"= 单词可复用（但**内层枚举天然无方向争议**——j 之间不互相引用）；
 * <p>　　　　dp[0]=true（空串地基——0 个字符 = 0 个单词的拼接，最值流/计数流之外的判定流）；
 * <p>　　　　HashSet O(1) 查词；substring(j,i) 的 [j..i) 左闭右开约定
 * <p>【关联】P416（同判定 || 型——数组→字符串）；P377（末步分箱思想复用）；P518（可复用=完全，
 * <p>　　　　但本题内层是"枚举分割点"不涉及 01/完全方向选择——第五种变体：**分割型 DP**）
 *
 * <p>LCS 签名：wordBreak(String s, List&lt;String&gt; wordDict)。
 */
public class P139WordBreak {

    /** 你的实现（分割型 DP：外层终点 i 自增、内层枚举分割点 j；dp[0]=true 判定流地基）。 */
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> set = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && set.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }

    public static void main(String[] args) {
        P139WordBreak solver = new P139WordBreak();

        run(solver, "leetcode", new String[]{"leet", "code"}, "官方例1 → true");
        run(solver, "applepenapple", new String[]{"apple", "pen"}, "官方例2 → true（复用）");
        run(solver, "catsandog", new String[]{"cats", "dog", "sand", "and", "cat"}, "官方例3 → false（经典卡死）");
        run(solver, "a", new String[]{"a"}, "最小 → true");
        run(solver, "ab", new String[]{"a", "b"}, "逐字拼接 → true");
        run(solver, "aaaaaaa", new String[]{"aaaa", "aaa"}, "超叠词（aaaa+aaa）→ 参考定");
        run(solver, "abcd", new String[]{"a", "abc", "b", "cd"}, "交叠尝试 → 参考定");
        run(solver, "goalspecial", new String[]{"go", "goal", "goals", "special"}, "前缀吞并（goals 吃掉 goal）→ 参考定");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：记忆化 DFS"从位置 pos 出发，枚举每个前缀匹配的单词"——分割视角，与 dp 表不同机制。 */
    private static boolean dfsBreak(String s, Set<String> dict, int pos, Set<Integer> failed) {
        if (pos == s.length()) return true;
        if (failed.contains(pos)) return false;
        for (String w : dict) {
            int n = w.length();
            if (pos + n <= s.length() && s.startsWith(w, pos)) {
                if (dfsBreak(s, dict, pos + n, failed)) return true;
            }
        }
        failed.add(pos);
        return false;
    }

    private static boolean bruteForce(String s, String[] words) {
        Set<String> dict = new HashSet<>(Arrays.asList(words));
        return dfsBreak(s, dict, 0, new HashSet<>());
    }

    private static void run(P139WordBreak solver, String s, String[] words, String label) {
        try {
            boolean got = solver.wordBreak(s, Arrays.asList(words));
            boolean want = bruteForce(s, words);
            boolean ok = got == want;

            System.out.printf("[%s] %-44s s=%-14s dict=%-32s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, s, Arrays.toString(words), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-44s s=%-14s dict=%-32s | TODO 未实现%n", label, s, Arrays.toString(words));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-44s s=%-14s dict=%-32s | 异常: %s%n", label, s, Arrays.toString(words), e);
        }
    }
}
