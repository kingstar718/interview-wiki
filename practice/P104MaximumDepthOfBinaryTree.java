import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 104: 二叉树的最大深度 (Maximum Depth of Binary Tree)
 * https://leetcode.cn/problems/maximum-depth-of-binary-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 递归
 * <p>【题型】后序求高度：1 + max(左深度, 右深度)
 * <p>【考点】递归返回值设计入门；空节点返回 0；深度按节点数算（根为 1）
 * <p>【关联】P111 最小深度（对称陷阱题）；P110 平衡二叉树；P543 直径
 *
 * <p>给定一个二叉树 root，返回其最大深度。
 * 二叉树的最大深度是指从根节点到最远叶子节点的最长路径上的【节点数】。
 *
 * <p>约束：树中节点的数量在 [0, 10^4] 区间内；-100 &lt;= Node.val &lt;= 100。
 *
 * <p>示例：树 [3,9,20,null,null,15,7] → 3
 * <pre>
 *       3
 *      / \
 *     9  20
 *       /  \
 *      15   7
 * </pre>
 *
 * <p>── 递归三部曲 ──
 * <p>① 参数与返回值：maxDepth(TreeNode) → 以该节点为根的子树最大深度
 * <p>② 终止条件：root == null → 0（空子树深度 0）
 * <p>③ 单层逻辑：1 + max(左子树深度, 右子树深度)
 */
public class P104MaximumDepthOfBinaryTree {

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

    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    public static void main(String[] args) {
        P104MaximumDepthOfBinaryTree solver = new P104MaximumDepthOfBinaryTree();

        run(solver, new Integer[]{3, 9, 20, null, null, 15, 7}, 3, "示例1");
        run(solver, new Integer[]{}, 0, "空树");
        run(solver, new Integer[]{1}, 1, "单节点");
        run(solver, new Integer[]{1, 2}, 2, "只有左子");
        run(solver, new Integer[]{1, null, 2}, 2, "只有右子");
        run(solver, new Integer[]{1, 2, 3, 4, 5, 6, 7}, 3, "满二叉树 3 层");
        run(solver, new Integer[]{1, 2, null, 3, null, 4}, 4, "左单链 4 层");
        run(solver, new Integer[]{1, 2, 3, 4, null, null, 5}, 3, "两侧不等");
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

    private static void run(P104MaximumDepthOfBinaryTree solver, Integer[] tree, int expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        try {
            int got = solver.maxDepth(build(tree));
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " exp=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
