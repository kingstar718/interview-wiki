import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * LeetCode 111: 二叉树的最小深度 (Minimum Depth of Binary Tree)
 * https://leetcode.cn/problems/minimum-depth-of-binary-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 广度优先搜索(BFS) · 递归
 * <p>【题型】本章最常错：最小深度不能直接 1 + min(左, 右)
 * <p>【考点】单侧空子树【不是叶子】，必须走非空的那一侧；叶子定义 = 左右都空
 * <p>【关联】P104 最大深度（几乎同款但不用特判）；P102 层序遍历（BFS 版）
 *
 * <p>给定一个二叉树，找出其最小深度。
 * 最小深度是从根节点到最近【叶子节点】的最短路径上的节点数量。
 * 叶子节点是指没有子节点的节点。
 *
 * <p>约束：树中节点数的范围在 [0, 10^5] 内；-1000 &lt;= Node.val &lt;= 1000。
 *
 * <p>示例1：[3,9,20,null,null,15,7] → 2   （根 → 9 就是叶子）
 * <p>示例2：[2,null,3,null,4,null,5,null,6] → 5   （单支链 5 个节点，叶子在最底下）
 *
 * <p>── 为什么不能 1 + min(左, 右) ──
 * <p>树 [2,null,3]：根 2 左空、右 3（叶子）。
 * <p>直接 min：min(minDepth(null)=0, minDepth(3)=1) = 0 → 返回 1 ❌（正确应为 2）
 * <p>因为「空子树」返回 0，它不是叶子，不能参与比较。
 */
public class P111MinimumDepthOfBinaryTree {

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

    public int minDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Deque<TreeNode> dq = new ArrayDeque<>();
        dq.offer(root);
        int count = 1;
        while (!dq.isEmpty()) {
            int size = dq.size();
            for (int i = 0; i < size; i++) {
                TreeNode pop = dq.poll();
                if (pop.left == null && pop.right == null) {
                    return count;
                }
                if (pop.left != null) dq.offer(pop.left);
                if (pop.right != null) dq.offer(pop.right);
            }
            count++;
        }
        return count;
    }

    public static void main(String[] args) {
        P111MinimumDepthOfBinaryTree solver = new P111MinimumDepthOfBinaryTree();

        run(solver, new Integer[]{3, 9, 20, null, null, 15, 7}, 2, "示例1 根→9 即叶子");
        run(solver, new Integer[]{2, null, 3, null, 4, null, 5, null, 6}, 5, "示例2 单支链(5节点深5)");
        run(solver, new Integer[]{}, 0, "空树");
        run(solver, new Integer[]{1}, 1, "单节点");
        run(solver, new Integer[]{1, 2}, 2, "只有左子");
        run(solver, new Integer[]{1, null, 2}, 2, "只有右子");
        run(solver, new Integer[]{2, null, 3}, 2, "陷阱：左空右叶子");
        run(solver, new Integer[]{1, 2, 3, 4, null, null, 5}, 3, "两侧叶子都在第3层");
        run(solver, new Integer[]{1, 2, 3, 4, 5, 6, 7}, 3, "满二叉树");
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

    private static void run(P111MinimumDepthOfBinaryTree solver, Integer[] tree, int expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        try {
            int got = solver.minDepth(build(tree));
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " exp=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
