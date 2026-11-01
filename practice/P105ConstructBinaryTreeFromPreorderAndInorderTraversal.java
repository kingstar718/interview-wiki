import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 105: 从前序与中序遍历序列构造二叉树
 * (Construct Binary Tree from Preorder and Inorder Traversal)
 * https://leetcode.cn/problems/construct-binary-tree-from-preorder-and-inorder-traversal/
 *
 * <p>【难度】Medium
 * <p>【标签】二叉树(Binary Tree) · 分治 · 递归 · 哈希表
 * <p>【题型】双序列分治建树：前序定根，中序定左右子树边界
 * <p>【考点】切分区间的边界计算（本章最易错）；用 HashMap 把根定位降到 O(1)
 * <p>【关联】P108 有序数组转 BST（单序列分治）；P106 中序+后序（镜像）
 *
 * <p>给定两个整数数组 preorder 和 inorder，其中 preorder 是二叉树的【前序】遍历，
 * inorder 是同一棵树的【中序】遍历，请构造二叉树并返回其根节点。
 *
 * <p>约束：1 &lt;= preorder.length &lt;= 3000；
 * inorder.length == preorder.length；
 * -3000 &lt;= preorder[i], inorder[i] &lt;= 3000；
 * preorder 和 inorder 均【无重复】元素；
 * preorder 和 inorder 均保证是同一棵二叉树的前序/中序遍历结果。
 *
 * <p>示例1：preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
 * <pre>
 *        3
 *       / \
 *      9  20
 *         / \
 *        15  7
 * </pre>
 * <p>示例2：preorder = [-1], inorder = [-1] → 单节点
 */
public class P105ConstructBinaryTreeFromPreorderAndInorderTraversal {

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

    public TreeNode buildTree(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0) {
            return null;
        }
        int pix = 0;
        int rootVal = preorder[0];
        TreeNode root = new TreeNode(rootVal);
        for (int i : inorder) {
            if (i == rootVal) {
                break;
            }
            pix++;
        }
        root.left = buildTree(java.util.Arrays.copyOfRange(preorder, 1, pix + 1),
                java.util.Arrays.copyOfRange(inorder, 0, pix + 1));
        root.right = buildTree(java.util.Arrays.copyOfRange(preorder, pix + 1, preorder.length),
                java.util.Arrays.copyOfRange(inorder, pix + 1, inorder.length));
        return root;
    }

    public static void main(String[] args) {
        P105ConstructBinaryTreeFromPreorderAndInorderTraversal solver =
                new P105ConstructBinaryTreeFromPreorderAndInorderTraversal();

        run(solver, new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7}, "示例1");
        run(solver, new int[]{-1}, new int[]{-1}, "示例2 单节点");
        run(solver, new int[]{1, 2}, new int[]{1, 2}, "左单链");
        run(solver, new int[]{1, 2}, new int[]{2, 1}, "右单链");
        run(solver, new int[]{1, 2, 3}, new int[]{2, 1, 3}, "小满树");
        run(solver, new int[]{1, 2, 4, 5, 3, 6, 7}, new int[]{4, 2, 5, 1, 6, 3, 7}, "满二叉树 7 节点");
        run(solver, new int[]{3, 1, 2, 4}, new int[]{1, 3, 2, 4}, "左偏树");
        run(solver, new int[]{1, 2, 3}, new int[]{2, 3, 1}, "反例：preL≠inL 时崩");
        run(solver, new int[]{1, 2, 3}, new int[]{3, 2, 1}, "反例：左深链");
        run(solver, new int[]{5, 3, 1, 4, 8}, new int[]{1, 3, 4, 5, 8}, "反例：左右都不对称");
        run(solver, new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5}, "纯右链 5 节点");
        run(solver, new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5}, "纯左链 5 节点");
    }

    private static void run(P105ConstructBinaryTreeFromPreorderAndInorderTraversal solver,
                            int[] preorder, int[] inorder, String name) {
        String label = name + " pre=" + java.util.Arrays.toString(preorder)
                + " in=" + java.util.Arrays.toString(inorder);
        try {
            TreeNode root = solver.buildTree(preorder, inorder);

            // 校验1：重建出的树，前序遍历必须等于 preorder
            List<Integer> pre = new ArrayList<>();
            collectPre(root, pre);
            boolean preOk = pre.size() == preorder.length;
            for (int i = 0; preOk && i < preorder.length; i++) {
                if (pre.get(i) != preorder[i]) preOk = false;
            }

            // 校验2：中序遍历必须等于 inorder
            List<Integer> in = new ArrayList<>();
            collectIn(root, in);
            boolean inOk = in.size() == inorder.length;
            for (int i = 0; inOk && i < inorder.length; i++) {
                if (in.get(i) != inorder[i]) inOk = false;
            }

            if (!preOk) {
                System.out.println("[FAIL] " + label + " | 前序不符：" + pre);
            } else if (!inOk) {
                System.out.println("[FAIL] " + label + " | 中序不符：" + in);
            } else {
                System.out.println("[PASS] " + label + " | 树=" + serialize(root));
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }

    private static void collectPre(TreeNode n, List<Integer> out) {
        if (n == null) return;
        out.add(n.val);
        collectPre(n.left, out);
        collectPre(n.right, out);
    }

    private static void collectIn(TreeNode n, List<Integer> out) {
        if (n == null) return;
        collectIn(n.left, out);
        out.add(n.val);
        collectIn(n.right, out);
    }

    private static String serialize(TreeNode root) {
        if (root == null) return "[]";
        List<String> out = new ArrayList<>();
        Deque<TreeNode> q = new java.util.LinkedList<>();
        q.offer(root);
        while (!q.isEmpty()) {
            TreeNode n = q.poll();
            if (n == null) {
                out.add("null");
                continue;
            }
            out.add(String.valueOf(n.val));
            q.offer(n.left);
            q.offer(n.right);
        }
        int end = out.size();
        while (end > 0 && out.get(end - 1).equals("null")) end--;
        return "[" + String.join(" ", out.subList(0, end)) + "]";
    }
}
