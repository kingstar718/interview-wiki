import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 235: 二叉搜索树的最近公共祖先 (Lowest Common Ancestor of a BST)
 * https://leetcode.cn/problems/lowest-common-ancestor-of-a-binary-search-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉搜索树(BST) · 二叉树(Binary Tree) · 深度优先搜索(DFS)
 * <p>【题型】利用 BST 有序性一次向下搜索，找到"分叉点"
 * <p>【考点】p、q 都在左 → 往左；都在右 → 往右；否则当前即 LCA（含 p/q 等于当前节点）
 * <p>【关联】P236 普通二叉树 LCA（必须后序，需对照记）；P700 BST 搜索（同一套方向判断）
 *
 * <p>给定一个二叉搜索树, 找到该树中两个指定节点的最近公共祖先。
 *
 * <p>约束：树中节点数目在范围 [2, 10^5] 内；-10^9 &lt;= Node.val &lt;= 10^9；
 * 所有 Node.val 互不相同；p != q；p 和 q 均存在于给定的 BST 中。
 *
 * <p>示例：root=[6,2,8,0,4,7,9,null,null,3,5]
 * <pre>
 *           6
 *         /   \\
 *        2     8
 *       / \\   / \\
 *      0   4 7   9
 *         / \\
 *        3   5
 *
 * p=2, q=8 → 6       （一个在左、一个在右 → 根就是分叉点）
 * p=2, q=4 → 2       （都在 6 的左边 → 继续往左；到 2 时命中自身）
 * </pre>
 *
 * <p>── 和 236 的核心差别 ──
 * <p>236 普通二叉树：不知道 p/q 在哪，必须后序遍历"两边都找到了 → 我是 LCA"。
 * <p>235 BST：值的大小直接告诉你方向，一次向下走，不需要回头看，也不需要返回值合并。
 */
public class P235LowestCommonAncestorOfABinarySearchTree {

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
        if (root.val > p.val && root.val > q.val) {
            return lowestCommonAncestor(root.left, p, q);
        } else if (root.val < p.val && root.val < q.val) {
            return lowestCommonAncestor(root.right, p, q);
        } else {
            return root;
        }
    }

    public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        while (root != null) {
            if (root.val > p.val && root.val > q.val) {
                root = root.left;
            } else if (root.val < p.val && root.val < q.val) {
                root = root.right;
            } else {
                return root;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        P235LowestCommonAncestorOfABinarySearchTree solver = new P235LowestCommonAncestorOfABinarySearchTree();

        Integer[] t = {6, 2, 8, 0, 4, 7, 9, null, null, 3, 5};
        run(solver, t, 2, 8, 6, "示例1 p=2,q=8 分叉在根");
        run(solver, t, 2, 4, 2, "示例2 p=2,q=4 → 2 自己是祖先");
        run(solver, t, 3, 5, 4, "兄弟节点 → 父 4");
        run(solver, t, 0, 5, 2, "跨两侧但都在左子树 → 2");
        run(solver, t, 7, 9, 8, "右子树兄弟 → 8");
        run(solver, t, 0, 9, 6, "最左与最右 → 根 6");
        run(solver, t, 3, 3, 3, "p == q");
        run(solver, new Integer[]{2, 1}, 1, 2, 2, "根是祖先");
        run(solver, new Integer[]{2, 1}, 2, 1, 2, "参数顺序反转");
        run(solver, new Integer[]{5, 3, 8, 1, 4, 7, 9}, 1, 4, 3, "左子树内部 → 3");
    }

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

    private static void run(P235LowestCommonAncestorOfABinarySearchTree solver, Integer[] tree,
                            int pVal, int qVal, int expectedVal, String name) {
        String label = name + " p=" + pVal + " q=" + qVal;
        runOne(label + " [递归]", expectedVal, () -> {
            TreeNode root = build(tree);
            return solver.lowestCommonAncestor(root, find(root, pVal), find(root, qVal));
        });
        runOne(label + " [迭代]", expectedVal, () -> {
            TreeNode root = build(tree);
            return solver.lowestCommonAncestorIterative(root, find(root, pVal), find(root, qVal));
        });
    }

    private static void runOne(String label, int expectedVal, java.util.function.Supplier<TreeNode> call) {
        try {
            TreeNode got = call.get();
            boolean ok = got != null && got.val == expectedVal;
            System.out.println((ok ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + (got == null ? "null" : got.val) + " exp=" + expectedVal);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
