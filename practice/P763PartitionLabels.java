import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * LeetCode 763: 划分字母区间 (Partition Labels)
 * https://leetcode.cn/problems/partition-labels/
 *
 * <p>【难度】Medium
 * <p>【标签】贪心(Greedy) · 哈希表 · 双指针
 * <p>【题型】区间线压轴变形：每个字母的 [首现, 末现] 就是一个区间——
 * <p>　　　　"划分成尽可能多的段，段内字母不出场" = "找区间并集切点"（455 的隐式版）
 * <p>【考点】last[] 末现表；end = max(end, last[c]) 的"墙"思想；i == end 收割
 * <p>【关联】P435（显式区间排序贪心——本题是其隐式版）；P45（i==end 事件结算的同族）
 *
 * <p>给字符串 s，划分成尽可能多的段，使每个字母**最多出现在一个段里**，
 * 返回每段的长度列表。
 *
 * <p>示例：s="ababcbacadefegdehijhklij" → [9,7,8]
 * <p>约束：1 &lt;= s.length &lt;= 500；小写英文
 */
public class P763PartitionLabels {

    /** 你的实现（last[] 末现表 + 墙 max 右移 + i==end 收割）。 */
    public List<Integer> partitionLabels(String s) {
        int[] last = new int[26];
        List<Integer> res;
        for (int i = 0; i < s.length(); i++) {
            last[s.charAt(i) - 'a'] = i;
        }
        res = new ArrayList<>();
        int start = 0;
        int end = 0;
        for (int i = 0; i < s.length(); i++) {
            end = Math.max(end, last[s.charAt(i) - 'a']);
            if (i == end) {
                res.add(end - start + 1);
                start = end + 1;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P763PartitionLabels solver = new P763PartitionLabels();

        run(solver, "ababcbacadefegdehijhklij", "示例1 官方 → [9,7,8]");
        run(solver, "eccbbbbdec", "官方同款变体 → [10]");
        run(solver, "ababcbacadefegdehijhklij", "重复校验官方");
        run(solver, "a", "单字符 → [1]");
        run(solver, "ab", "两个不同 → [1,1]");
        run(solver, "aa", "重复单字母 → [2]");
        run(solver, "abcba", "首尾同字母包夹 → [5]");
        run(solver, "abccbaabccba", "两段大循环 → [6,6]? 暴力定");
        run(solver, "abab", "交替循环 → [4]");
        run(solver, "cbdzxl", "全不重复 → [1,1,1,1,1,1]");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /**
     * 独立暴力参考：每个字母的 [首现, 末现] 区间，合并区间（贪心并集），
     * 每个并集区间的长度就是一段。与回溯法无关、与 last[] 法机制不同（它先拆区间再合并，最 Indies 复杂度 O(n log n)）。
     */
    private static List<Integer> bruteForce(String s) {
        TreeSet<Character> ch = new TreeSet<>();
        for (char c : s.toCharArray()) ch.add(c);

        // 每个字母建一个 [首现, 末现] 区间
        List<int[]> intervals = new ArrayList<>();
        for (char c : ch) {
            int first = s.indexOf(c), lastPos = s.lastIndexOf(c);
            intervals.add(new int[]{first, lastPos});
        }
        // 排序+合并（56 的合并区间）
        intervals.sort((a, b) -> a[0] - b[0]);
        List<Integer> res = new ArrayList<>();
        int start = intervals.get(0)[0], end = intervals.get(0)[1];
        for (int i = 1; i < intervals.size(); i++) {
            if (intervals.get(i)[0] <= end) {           // 重叠/相接则并入
                end = Math.max(end, intervals.get(i)[1]);
            } else {                                    // 有空隙 → new 段
                res.add(end - start + 1);
                start = intervals.get(i)[0];
                end = intervals.get(i)[1];
            }
        }
        res.add(end - start + 1);
        return res;
    }

    private static void run(P763PartitionLabels solver, String s, String label) {
        try {
            List<Integer> got = solver.partitionLabels(s);
            List<Integer> want = bruteForce(s);

            boolean ok = gol(got) == gol(want)
                    && new ArrayList<>(got).equals(want);
            // 两个校验各自独立：和相同 + 序列完全一致（顺序对）
            System.out.printf("[%s] %-28s s=%-26s | 参考=\"%s\" 实得=\"%s\"%n",
                    ok ? "PASS" : "FAIL", label, "\"" + s + "\"", want, got);
            if (!ok) {
                System.out.println("    ---- 暴力 —— 序列差如上 ----");
            }
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-28s s=%-26s | TODO 未实现%n", label, "\"" + s + "\"");
        } catch (Exception e) {
            System.out.printf("[FAIL] %-28s s=%-26s | 异常: %s%n", label, "\"" + s + "\"", e);
        }
    }

    private static int gol(List<Integer> list) {
        return list == null ? -1 : list.size();
    }
}
