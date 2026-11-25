import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 543: 二叉树的直径 (Diameter of Binary Tree)
 * https://leetcode.cn/problems/diameter-of-binary-tree/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二叉树组 2/6——后序+全局变量原型）
 *
 * <p>■ 题目描述
 *   给二叉树根节点，返回树的**直径**：任意两节点间**最长路径的边数**。
 *   路径：可能经过也可能不经过根节点；路径上每条边只走一次。
 *   （两节点间路径 = 树上唯一路径 = 必经过两者的最近公共祖先）
 *
 * <p>■ 输入输出（约束）
 *   输入：TreeNode root（节点数 [1, 10^4]；-100 ≤ val ≤ 100）
 *   输出：int（边数）
 *
 * <p>■ 示例逐条
 * <pre>
 *   1
 *  / \\
 * 2   3
 *    / \\
 *   4   5
 * → 3（路径 4→3→5 或 4→3→2，边数 3）
 * [1,2] → 1（2→1 一条边）
 * [1] → 0（单节点，无边）
 * 完全二叉树 1/2,3/4,5,6,7 → 4（4→2→1→3→6）
 * 左斜链 5 节点 → 4（沿链走到底）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★★·字节/腾讯/阿里）
 * <p>【标签】树 · 深度优先 · 二叉树
 * <p>【题型】后序 + 全局变量（"递归返回深度、答案沿途收集"原型）：
 * <p>　　直径必过某节点 LCA——以该节点为"拐点"的候选 = 左深 + 右深
 * <p>　　每个节点都要当一次拐点 → 遍历全集时取 max
 * <p>　　递归函数自身返回"以本节点向下最深的边数"（给父亲用）
 * <p>【考点】"返回值"与"答案"分离——返回值是给父用的量，答案是要收集的量；
 * <p>　　　　拐点定义（left+right 不是 left+right+1——边 vs 节点口径）；
 * <p>　　　　为什么必须后序（先知道孩子深度，才算本节点拐点）
 * <p>【关联】P104 最大深度（返回值=深度，直径是其副产品）；P124（家族 Hard 版）；
 * <p>　　　　P94 中序（同家族遍历课）；P226（后序原型第一题）
 *
 * <p>LCS 签名：diameterOfBinaryTree(TreeNode root)。
 */
public class P543DiameterOfBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** 你的实现（后序 dfs：返回值=向下最深边数给父亲，答案=左深+右深沿途收集）。 */
    private int max;

    public int diameterOfBinaryTree(TreeNode root) {
        max = 0;
        dfs(root);
        return max;
    }

    private int dfs(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int l = dfs(root.left);
        int r = dfs(root.right);
        max = Math.max(max, l + r);
        return 1 + Math.max(l, r);
    }

    public static void main(String[] args) {
        P543DiameterOfBinaryTree solver = new P543DiameterOfBinaryTree();

        run(solver, of(1, 2, 3, -1, -1, 4, 5), "官方例 1/2,3(4,5) → 3");
        run(solver, of(1, 2), "两节点 → 1");
        run(solver, of(1), "单节点 → 0");
        run(solver, of(1, 2, 3, 4, 5, 6, 7), "完全二叉树 → 4");
        run(solver, chain(1, 2, 3, 4, 5), "左斜链 5 → 4");
        run(solver, of(4, -1, -1, -1, 9, -1, -1, -1, -1, -1, -1, 7, 6, -1, -1, -1, -1, -1, -1, -1, 2, 3), "深层错位 → 4");
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

    /** 沿左孩子串成斜链（vals[0] 为根）。 */
    private static TreeNode chain(int... vals) {
        TreeNode root = new TreeNode(vals[0]);
        TreeNode cur = root;
        for (int i = 1; i < vals.length; i++) {
            cur.left = new TreeNode(vals[i]);
            cur = cur.left;
        }
        return root;
    }

    /** 独立暴力参考：枚举每个节点为拐点（后序重算子树深度，O(n²) 但机制独立）。 */
    private static int bruteForce(TreeNode root) {
        if (root == null) return 0;
        int best = depth(root.left) + depth(root.right);
        best = Math.max(best, bruteForce(root.left));
        best = Math.max(best, bruteForce(root.right));
        return best;
    }

    private static int depth(TreeNode n) {
        if (n == null) return 0;
        return 1 + Math.max(depth(n.left), depth(n.right));
    }

    private static void run(P543DiameterOfBinaryTree solver, TreeNode root, String label) {
        try {
            int want = bruteForce(root);
            int got = solver.diameterOfBinaryTree(root);
            boolean ok = got == want;

            System.out.printf("[%s] %-22s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-22s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-22s | 异常: %s%n", label, e);
        }
    }
}
