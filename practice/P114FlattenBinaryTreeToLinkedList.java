import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 114: 二叉树展开为链表 (Flatten Binary Tree to Linked List)
 * https://leetcode.cn/problems/flatten-binary-tree-to-linked-list/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二叉树组 5/6——原地重组名场面）
 *
 * <p>■ 题目描述
 *   给二叉树根节点，把它**原地**展开为一个"链表"：
 *   - "链表"仍用 TreeNode 表示：每个节点 left 指针全为 null，
 *     right 指针指向下一个节点（**按前序遍历顺序**串联）
 *   - 必须原地（不得 new 节点，只改指针）
 *
 * <p>■ 输入输出（约束）
 *   输入：TreeNode root（节点数 [1, 2000]；-100 ≤ val ≤ 100）
 *   输出：无（原地改 root，从 root.right 一路读下去）
 *
 * <p>■ 示例逐条
 * <pre>
 *     1                  1
 *    / \\        →        \\
 *   2   5                 2
 *  / \\   \\                \\
 * 3   4   6                 3
 *                           \\
 *                            4
 *                             \\
 *                              5
 *                               \\
 *                                6
 * 输入 [1,2,5,3,4,null,6] → 前序 1,2,3,4,5,6 串成右链
 * [1] → [1]
 * [1,2]（1 左 2）→ 1.right=2，1.left=null
 * [1,null,2] → 已是链（1.right=2）
 * [3,1,2]（3/1,2）→ 3→1→2
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/阿里）
 * <p>【标签】树 · 深度优先 · 链表 · 二叉树
 * <p>【题型】原地改指针（树→链的破坏式重组）：
 * <p>　　前序序列 = 根、左链全展开、右链全展开——拼接处是坑
 * <p>　　经典思路：逆后序（右→左→根）逐个挂 right；或找到左子树最右节点接右子树
 * <p>【考点】原地语义（指针改写顺序敏感——先存后改）；前序与"展开"的对齐；
 * <p>　　　　展开后原 left 的子树去哪了（会整个挂到右侧）
 * <p>【关联】P94/144（前序序列）；P116/117（填充右指针——同族）；P206 反转链表（原地改指针课）；
 * <p>　　　　P234（参考先算纪律——原地实现会污染复制型参考，骨架已内置）
 *
 * <p>LCS 签名：flatten(TreeNode root)。
 */
public class P114FlattenBinaryTreeToLinkedList {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** 对照入口（后序"三改一找"拼接版：先展开孩子再拼；存指针时序=递归后、改前）。 */
    public void flattenMerge(TreeNode root) {
        dfs(root);
    }

    private void dfs(TreeNode root) {
        if (root == null) return;
        dfs(root.left);
        dfs(root.right);
        TreeNode l = root.left;
        TreeNode r = root.right;
        root.left = null;
        root.right = l;
        TreeNode tail = root;
        while (tail.right != null) {
            tail = tail.right;
        }
        tail.right = r;
    }

    /** 你的实现（灵神版：反前序头插——右→左→根，每帧 O(1) 头插无需找尾巴）。 */
    private TreeNode head;

    public void flatten(TreeNode root) {
        head = null;
        dfsRev(root);
    }

    public void dfsRev(TreeNode root) {
        if (root == null) return;
        dfsRev(root.right);
        dfsRev(root.left);
        root.left = null;
        root.right = head;
        head = root;
    }

    public static void main(String[] args) {
        P114FlattenBinaryTreeToLinkedList solver = new P114FlattenBinaryTreeToLinkedList();

        run(solver, of(1, 2, 5, 3, 4, -1, 6), "官方例 1/2(3,4),5(右6) → 1→2→3→4→5→6");
        run(solver, of(1), "单节点 → [1]");
        run(solver, of(1, 2), "1左2 → 1→2");
        run(solver, of(1, -1, 2), "1右2（已链）→ 1→2");
        run(solver, of(3, 1, 2), "3/1,2 → 3→1→2");
        run(solver, chain(1, 2, 3, 4, 5), "左斜链 → 1→2→3→4→5");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 按 [val, l, r, ...] 层序构造（-1 = null 子树）。 */
    private static TreeNode of(int... seq) {
        if (seq.length == 0) return null;
        java.util.Queue<TreeNode> q = new java.util.ArrayDeque<>();
        TreeNode root = new TreeNode(seq[0]);
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < seq.length) {
            TreeNode n = q.poll();
            if (i < seq.length && seq[i] != -1) { n.left = new TreeNode(seq[i]); q.offer(n.left); }
            i++;
            if (i < seq.length && seq[i] != -1) { n.right = new TreeNode(seq[i]); q.offer(n.right); }
            i++;
        }
        return root;
    }

    /** 沿左孩子串成斜链。 */
    private static TreeNode chain(int... vals) {
        TreeNode root = new TreeNode(vals[0]);
        TreeNode cur = root;
        for (int i = 1; i < vals.length; i++) {
            cur.left = new TreeNode(vals[i]);
            cur = cur.left;
        }
        return root;
    }

    /** 深拷贝（原地题必须每次喂新树——参考先算纪律）。 */
    private static TreeNode copy(TreeNode n) {
        if (n == null) return null;
        return new TreeNode(n.val, copy(n.left), copy(n.right));
    }

    /** 独立暴力参考：前序收集→逐个挂 right（复制树上跑，机制独立）。 */
    private static TreeNode bruteForce(TreeNode root) {
        List<TreeNode> pre = new ArrayList<>();
        preorder(root, pre);
        for (int i = 0; i < pre.size(); i++) {
            pre.get(i).left = null;
            pre.get(i).right = i + 1 < pre.size() ? pre.get(i + 1) : null;
        }
        return pre.get(0);
    }

    private static void preorder(TreeNode n, List<TreeNode> out) {
        if (n == null) return;
        out.add(n);
        preorder(n.left, out);
        preorder(n.right, out);
    }

    /** 断言两链逐节点 val 相等且全部 left==null。 */
    private static boolean sameChain(TreeNode a, TreeNode b) {
        while (a != null && b != null) {
            if (a.left != null) return false;
            if (a.val != b.val) return false;
            a = a.right; b = b.right;
        }
        return a == null && b == null;
    }

    private static void run(P114FlattenBinaryTreeToLinkedList solver, TreeNode root, String label) {
        try {
            TreeNode want = bruteForce(copy(root));      // 参考先算（copy 喂参考）
            TreeNode got = copy(root);                   // 再算用户实现（不污染参考）
            solver.flatten(got);                          // 灵神版（头插）
            solver.flattenMerge(copy(root));              // 拼接版对照
            boolean ok = sameChain(want, got);

            System.out.printf("[%s] %-30s | 参考=%s%n",
                    ok ? "PASS" : "FAIL", label, chainStr(want, ok));
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s | 异常: %s%n", label, e);
        }
    }

    private static String chainStr(TreeNode n, boolean ok) {
        StringBuilder sb = new StringBuilder(ok ? "✓ " : "✗ ");
        while (n != null) { sb.append(n.val).append(n.right != null ? "→" : ""); n = n.right; }
        return sb.toString();
    }
}
