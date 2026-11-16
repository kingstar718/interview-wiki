import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 49: 字母异位词分组 (Group Anagrams)
 * https://leetcode.cn/problems/group-anagrams/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收 1/50）
 *
 * <p>■ 题目描述
 *   给一个字符串数组 strs，把【字母异位词】分组——异位词 = 字母相同、
 *   排列不同（如 eat/tea/ate）。返回分组结果（每组内、组间顺序不限）。
 *
 * <p>■ 输入输出
 *   输入：String[] strs（1 ≤ length ≤ 10^4；0 ≤ strs[i].length ≤ 100；全小写字母，可含空串）
 *   输出：List<List<String>>（异位词组列表）
 *
 * <p>■ 示例逐条
 * <pre>
 * strs=["eat","tea","tan","ate","nat","bat"] → [[bat],[nat,tan],[ate,eat,tea]]（顺序不限）
 * strs=[""] → [[""]]（空串组）
 * strs=["a"] → [["a"]]
 * strs=["abc","cba","bca","abd"] → [[abc,cba,bca],[abd]]（前三互为异位词）
 * strs=["aaa","aaa"] → [[aaa,aaa]]（重复词同组）
 * strs=["ab","ba","cd","dc"] → [[ab,ba],[cd,dc]]（两组平行）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★· Hot100）
 * <p>【标签】哈希表 · 字符串 · 排序
 * <p>【题型】分组哈希：为每个词算一个"组标识 key"（异位词共享同一 key）→ map 分桶
 * <p>【考点】key 设计两流派：排序串（O(k log k)/词）vs 字符计数串（O(k)/词）；
 * <p>　　　　map.computeIfAbsent；分组题的"key 归一化"思想（49 是模板首题）
 * <p>【关联】P128（Set 扩展链）；P242（异位词判定——key 的单点版）；P347（分组哈希兄弟）
 *
 * <p>LCS 签名：groupAnagrams(String[] strs)。
 */
public class P49GroupAnagrams {

    /** 你的实现（排序串 key 分桶：JDK8 合规版 new ArrayList<>(map.values())）。 */
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String s : strs) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            String newKey = new String(chars);
            if (map.containsKey(newKey)) {
                map.get(newKey).add(s);
            } else {
                List<String> r = new ArrayList<>();
                r.add(s);
                map.put(newKey, r);
            }
        }
        return new ArrayList<>(map.values());
    }

    public static void main(String[] args) {
        P49GroupAnagrams solver = new P49GroupAnagrams();

        run(solver, new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}, "官方例1 → 3 组");
        run(solver, new String[]{""}, "空串 → 1 组");
        run(solver, new String[]{"a"}, "单词 → 1 组");
        run(solver, new String[]{"abc", "cba", "bca", "abd"}, "三连异位+独行 → 2 组");
        run(solver, new String[]{"aaa", "aaa"}, "重复词 → 同组 2 元");
        run(solver, new String[]{"ab", "ba", "cd", "dc"}, "平行两组 → 2 组");
        run(solver, new String[]{"eat", "tea", "tan", "ate", "nat", "bat", "tab", "tan"}, "官方例+延伸 → 3 组（tan 出现两次）");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：两两判异位（26 计数比对）O(n²·k)——机制与"排序 key 哈希"完全不同。 */
    private static List<List<String>> bruteForce(String[] strs) {
        List<List<String>> groups = new ArrayList<>();
        for (String s : strs) {
            boolean placed = false;
            for (List<String> g : groups) {
                if (sameChars(g.get(0), s)) { g.add(s); placed = true; break; }
            }
            if (!placed) {
                List<String> g = new ArrayList<>();
                g.add(s);
                groups.add(g);
            }
        }
        return groups;
    }

    private static boolean sameChars(String a, String b) {
        if (a.length() != b.length()) return false;
        int[] c1 = new int[26], c2 = new int[26];
        for (int i = 0; i < a.length(); i++) { c1[a.charAt(i) - 'a']++; c2[b.charAt(i) - 'a']++; }
        return Arrays.equals(c1, c2);
    }

    /** 组间/组内排序成规范形再比较（LeetCode 允许任意顺序）。 */
    private static List<List<String>> canonical(List<List<String>> groups) {
        List<List<String>> out = new ArrayList<>();
        for (List<String> g : groups) {
            List<String> c = new ArrayList<>(g);
            java.util.Collections.sort(c);
            out.add(c);
        }
        out.sort((x, y) -> x.get(0).compareTo(y.get(0)));
        return out;
    }

    private static void run(P49GroupAnagrams solver, String[] strs, String label) {
        try {
            List<List<String>> got = canonical(solver.groupAnagrams(Arrays.copyOf(strs, strs.length)));
            List<List<String>> want = canonical(bruteForce(Arrays.copyOf(strs, strs.length)));
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-30s strs=%-52s | 参考=%s%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(strs), fmt(want));
            if (!ok) System.out.printf("       实得=%s%n", fmt(got));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s strs=%-52s | TODO 未实现%n", label, Arrays.toString(strs));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s strs=%-52s | 异常: %s%n", label, Arrays.toString(strs), e);
        }
    }

    private static String fmt(List<List<String>> groups) {
        StringBuilder sb = new StringBuilder();
        for (List<String> g : groups) sb.append(g);
        return sb.toString();
    }
}
