import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 208: 实现 Trie (前缀树) (Implement Trie - Prefix Tree)
 * https://leetcode.cn/problems/implement-trie-prefix-tree/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·字典树组 2/2——自建数据结构）
 *
 * <p>■ 题目描述
 *   实现 Trie（前缀树/字典树）类，支持三个操作：
 *   - insert(String word)：把 word 存入树
 *   - search(String word)：word 是否【完整存在】于树中（是存过的完整单词）
 *   - startsWith(String prefix)：树中是否有单词以 prefix 开头
 *
 * <p>■ 输入输出（约束）
 *   所有输入仅小写字母 a-z；单次调用串长 ≤ 10^5；总调用 ≤ 3*10^4
 *
 * <p>■ 示例序列（依次调用）
 * <pre>
 * Trie t = new Trie();
 * t.insert("apple");
 * t.search("apple")   → true
 * t.search("app")     → false（app 只是前缀，不是存过的完整单词！）
 * t.startsWith("app") → true
 * t.insert("app");
 * t.search("app")     → true（现在存了）
 * t.startsWith("appl")→ true
 * t.search("banana")  → false
 * t.startsWith("ban") → false
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/微软）
 * <p>【标签】字典树 · 设计 · 字符串
 * <p>【题型】自建数据结构（26 叉树）：
 * <p>　　每节点：26 个孩子的槽位 + isEnd 标记（"是否是某个单词的终点"）
 * <p>　　search 和 startsWith 共用"沿边走"的逻辑，只差终点的判定条件
 * <p>【考点】isEnd 标记的意义（"存在完整单词"≠"存在路径"——app/apple 案例正体）；
 * <p>　　　　走空即返回 false（路径不存在）；insert 尾部立 isEnd
 * <p>【关联】P1047/150（字符串处理家族）；P212/211（Trie 变体）；P560（哈希对账——本题的
 * <p>　　　　"前缀查询"是 O(L) 而哈希是 O(前缀总数)）
 *
 * <p>LCS 签名：class Trie { insert/search/startsWith }。
 */
public class P208ImplementTrie {

    /** 节点：26 叉槽位 + isEnd（"有单词在此终结"的标记）。 */
    static class Node {
        Node[] children = new Node[26];
        boolean isEnd;
    }

    /** 你的实现（灵神同款：26 叉槽位 + isEnd；search 双条件/startsWith 单条件共用 walk）。 */
    static class Trie {

        Node root;

        public Trie() {
            root = new Node();
        }

        public void insert(String word) {
            Node cur = root;
            char[] chars = word.toCharArray();
            for (char c : chars) {
                int idx = c - 'a';
                if (cur.children[idx] == null) {
                    cur.children[idx] = new Node();
                }
                cur = cur.children[idx];
            }
            cur.isEnd = true;
        }

        private Node walk(String s) {
            Node cur = root;
            char[] chars = s.toCharArray();
            for (char c : chars) {
                cur = cur.children[c - 'a'];
                if (cur == null) {
                    return null;
                }
            }
            return cur;
        }

        public boolean search(String word) {
            Node walk = walk(word);
            return walk != null && walk.isEnd;
        }

        public boolean startsWith(String prefix) {
            return walk(prefix) != null;
        }
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 独立暴力参考：一个 List<String> 全存——search 全等匹配，startsWith 逐个 startsWith。 */
    static class RefBrute {
        private final List<String> words = new ArrayList<>();

        public void insert(String word) { words.add(word); }

        public boolean search(String word) { return words.contains(word); }

        public boolean startsWith(String prefix) {
            for (String w : words) if (w.startsWith(prefix)) return true;
            return false;
        }
    }

    public static void main(String[] args) {
        String[] script = {
            // op, arg, expect  (search/startsWith 才有 expect)
            "insert apple", "search apple:true", "search app:false",
            "startsWith app:true", "insert app", "search app:true",
            "startsWith appl:true", "search banana:false", "startsWith ban:false"
        };
        run(script, "官方示例序列");
        String[] script2 = {
            "insert a", "search a:true", "search ab:false",
            "startsWith ab:false", "insert ab", "startsWith ab:true",
            "search ab:true", "startsWith abc:false"
        };
        run(script2, "前缀升级序列");
    }

    private static void run(String[] script, String label) {
        Trie t = new Trie();
        RefBrute r = new RefBrute();
        int ok = 0, total = 0;
        for (String line : script) {
            String[] parts = line.split(" ", 2);
            String op = parts[0];
            if (op.equals("insert")) {
                t.insert(parts[1]); r.insert(parts[1]);
                continue;
            }
            String[] rest = parts[1].split(":");
            String arg = rest[0], expect = rest[1];
            boolean got, want;
            if (op.equals("search")) { got = t.search(arg); want = Boolean.parseBoolean(expect); }
            else { got = t.startsWith(arg); want = Boolean.parseBoolean(expect); }
            boolean refGot = op.equals("search") ? r.search(arg) : r.startsWith(arg);
            boolean lineOk = got == want && refGot == want;
            total++;
            if (lineOk) ok++;
            else System.out.printf("    ✗ %s %s → 实得=%s 期望=%s%n", op, arg, got, want);
        }
        System.out.printf("[%s] %-16s | 参考=RefBrute 实得=Trie %d/%d 步一致%n",
                ok == total ? "PASS" : "FAIL", label, ok, total);
    }
}
