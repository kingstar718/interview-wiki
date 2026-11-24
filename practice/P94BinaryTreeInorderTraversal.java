import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * LeetCode 94: 二叉树的中序遍历 (Binary Tree Inorder Traversal)
 * https://leetcode.cn/problems/binary-tree-inorder-traversal/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二叉树组 1/6——递归+栈双版）
 *
 * <p>■ 题目描述
 *   给二叉树根节点，返回**中序遍历**（左→根→右）的节点值序列。
 *   要求**递归**和**迭代（栈模拟）**两种方式都能写。
 *
 * <p>■ 输入输出（约束）
 *   输入：TreeNode root（节点数 [0, 100]；-100 ≤ val ≤ 100）
 *   输出：List&lt;Integer&gt;
 *
 * <p>■ 示例逐条
 * <pre>
 * [1,null,2,3]（1右2，2左3）→ [1,3,2]
 * [] → []
 * [1] → [1]
 * [1,2]（1左2）→ [2,1]（先下到 2）
 * [3,1,2]（3 左1 右2）→ [1,3,2]
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Easy（页频 ★★★★·字节/腾讯/美团/阿里）
 * <p>【标签】栈 · 树 · 深度优先
 * <p>【题型】中序遍历双版：
 * <p>　　递归：左→根→右天然对齐（三部曲第一遍）
 * <p>　　迭代：一路向左压栈 → 空了弹顶访问 → 转右子树（手工模拟递归——155/394 的栈课继续）
 * <p>【考点】迭代版的两句核心（左链全压 + 弹后转右）；空树；
 * <p>　　　　中序的"根在哪"（左之后）——pre/in/post 三兄弟的分辨原型
 * <p>【关联】P144 前序/P145 后序（一阶段已刷，三序对照）；P230（中序第 k 个直接复用）；P394（手工栈课）
 *
 * <p>LCS 签名：inorderTraversal(TreeNode root)。
 */
public class P94BinaryTreeInorderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** 你的实现（递归版：dfs 左→根→右；迭代版待补第二入口）。 */
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        dfs(root, res);
        return res;
    }
    /** 迭代栈版（你的实现）：一路向左压栈→弹顶访问→转右，手工模拟递归。 */
    public List<Integer> inorderTraversalIter(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            res.add(cur.val);
            cur = cur.right;
        }
        return res;
    }

    private void dfs(TreeNode root, List<Integer> res) {
        if (root == null) return;
        dfs(root.left, res);
        res.add(root.val);
        dfs(root.right, res);
    }

    public static void main(String[] args) {
        P94BinaryTreeInorderTraversal solver = new P94BinaryTreeInorderTraversal();

        run(solver, of(1, -1, 2, 3), "官方例 1右2左3 → [1,3,2]");
        run(solver, of(), "空树 → []");
        run(solver, of(1), "单节点 → [1]");
        run(solver, of(1, 2), "左链 → [2,1]");
        run(solver, of(3, 1, 2), "3左1右2 → [1,3,2]");
        run(solver, of(2, 1, 3, -1, -1, -1, 4), "BST 2/1,3(右4) → [1,2,3,4]");

        System.out.println("=== 迭代栈版 ===");
        run(solver, of(1, -1, 2, 3), "官方例 1右2左3 → [1,3,2]");
        run(solver, of(), "空树 → []");
        run(solver, of(1), "单节点 → [1]");
        run(solver, of(1, 2), "左链 → [2,1]");
        run(solver, of(3, 1, 2), "3左1右2 → [1,3,2]");
        run(solver, of(2, 1, 3, -1, -1, -1, 4), "BST 2/1,3(右4) → [1,2,3,4]");
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

    /** 独立暴力参考：显式递归中序（与迭代版机制对照）。 */
    private static List<Integer> bruteForce(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        rec(root, out);
        return out;
    }

    private static void rec(TreeNode n, List<Integer> out) {
        if (n == null) return;
        rec(n.left, out);
        out.add(n.val);
        rec(n.right, out);
    }

    private static void run(P94BinaryTreeInorderTraversal solver, TreeNode root, String label) {
        try {
            List<Integer> want = bruteForce(root);
            List<Integer> got = solver.inorderTraversal(root);
            List<Integer> gotIter = solver.inorderTraversalIter(root);
            boolean ok = got.equals(want) && gotIter.equals(want);

            System.out.printf("[%s] %-24s | 参考=%s 递归=%s 迭代=%s%n",
                    ok ? "PASS" : "FAIL", label, want, got, gotIter);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-24s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-24s | 异常: %s%n", label, e);
        }
    }
}
