import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * LeetCode 236: 二叉树的最近公共祖先 (Lowest Common Ancestor of a Binary Tree)
 * https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-tree/
 *
 * <p>【难度】Medium
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 递归 · 后序
 * <p>【题型】后序返回值设计的巅峰：一个函数同时完成「查找」和「汇报」
 * <p>【考点】左右都非 null → 当前节点即 LCA；只有一侧 → 继续上抛；用【引用】比较不是 val
 * <p>【关联】P235 BST 版 LCA（利用有序性）；P98 验证 BST
 *
 * <p>给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。
 * 最近公共祖先的定义：对于有根树 T 的两个节点 p、q，最近公共祖先表示为一个节点 x，
 * 满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。
 *
 * <p>约束：树中节点数目在范围 [2, 10^5] 内；-10^9 &lt;= Node.val &lt;= 10^9；
 * 所有 Node.val 互不相同；p != q；p 和 q 均存在于给定的二叉树中。
 *
 * <p>示例：root=[3,5,1,6,2,0,8,null,null,7,4], p=5, q=1 → 3
 * <pre>
 *          3
 *        /   \
 *       5     1
 *      / \   / \
 *     6   2 0   8
 *        / \
 *       7   4
 * </pre>
 *
 * <p>── 返回值语义（这题的核心）──
 * <p>返回 TreeNode，含义有两种：
 * <p>  ① 找到了 LCA → 就是这个节点
 * <p>  ② 只找到了 p 或 q 之一 → 返回那个节点（"我这棵子树里发现了它"）
 * <p>  ③ 什么都没找到 → null
 * <p>正因为"返回找到的 p/q"和"返回 LCA"共用一个类型，才能一次遍历搞定。
 */
public class P236LowestCommonAncestorOfABinaryTree {

    /**
     * 本地测试用的树节点（静态内部类）。
     * ⚠️ 提交 LeetCode 时删除本类（LeetCode 已预定义 TreeNode）。
     */
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }
        if (root == p || root == q) {
            return root;
        }
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left != null && right != null) {
            return root;
        }
        return (left != null) ? left : right;
    }

    public static void main(String[] args) {
        P236LowestCommonAncestorOfABinaryTree solver = new P236LowestCommonAncestorOfABinaryTree();

        // 示例树
        Integer[] t1 = {3, 5, 1, 6, 2, 0, 8, null, null, 7, 4};
        run(solver, t1, 5, 1, 3, "示例1 p=5,q=1 → 3");
        run(solver, t1, 5, 4, 5, "示例2 p=5,q=4 → 5（p 自己是祖先）");
        run(solver, t1, 7, 4, 2, "兄弟节点 → 父 2");
        run(solver, t1, 6, 4, 5, "跨两侧 → 5");
        run(solver, t1, 0, 8, 1, "右子树兄弟 → 1");
        run(solver, t1, 6, 7, 5, "深层 + 浅层 → 5");
        run(solver, new Integer[]{1, 2}, 1, 2, 1, "根是祖先");
        run(solver, new Integer[]{1, 2}, 2, 1, 1, "参数顺序反转");
        run(solver, new Integer[]{1, 2, 3}, 2, 3, 1, "左右各一个 → 根");
    }

    /** 按 val 找节点（题目保证所有 val 互不相同）。 */
    private static TreeNode find(TreeNode root, int val) {
        Queue<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            TreeNode n = q.poll();
            if (n.val == val) return n;
            if (n.left != null) q.offer(n.left);
            if (n.right != null) q.offer(n.right);
        }
        return null;
    }

    private static TreeNode build(Integer[] arr) {
        if (arr == null || arr.length == 0 || arr[0] == null) return null;
        TreeNode root = new TreeNode(arr[0]);
        Queue<TreeNode> q = new ArrayDeque<>();
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < arr.length) {
            TreeNode cur = q.poll();
            if (i < arr.length && arr[i] != null) {
                cur.left = new TreeNode(arr[i]);
                q.offer(cur.left);
            }
            i++;
            if (i < arr.length && arr[i] != null) {
                cur.right = new TreeNode(arr[i]);
                q.offer(cur.right);
            }
            i++;
        }
        return root;
    }

    private static void run(P236LowestCommonAncestorOfABinaryTree solver, Integer[] tree,
                            int pVal, int qVal, int expectedVal, String name) {
        String label = name + " p=" + pVal + " q=" + qVal;
        try {
            TreeNode root = build(tree);
            TreeNode p = find(root, pVal);
            TreeNode q = find(root, qVal);
            TreeNode got = solver.lowestCommonAncestor(root, p, q);
            boolean ok = got != null && got.val == expectedVal;
            System.out.println((ok ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + (got == null ? "null" : got.val) + " exp=" + expectedVal);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
