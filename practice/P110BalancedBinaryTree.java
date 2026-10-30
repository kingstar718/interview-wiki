import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 110: 平衡二叉树 (Balanced Binary Tree)
 * https://leetcode.cn/problems/balanced-binary-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 递归
 * <p>【题型】后序求高度 + 用 -1 当哨兵标记"不平衡"并剪枝
 * <p>【考点】一次递归同时算高度和判平衡（避免 O(n²)）；-1 必须层层向上传递
 * <p>【关联】P104 最大深度（高度计算基础）；P543 直径（同款后序合并）
 *
 * <p>给定一个二叉树，判断它是否是平衡二叉树。
 * 平衡二叉树：每个节点的左右两个子树的高度差的绝对值不超过 1。
 *
 * <p>约束：树中的节点数在范围 [0, 5000] 内；-10^4 &lt;= Node.val &lt;= 10^4。
 *
 * <p>示例1：[3,9,20,null,null,15,7] → true
 * <p>示例2：[1,2,2,3,3,null,null,4,4] → false
 *
 * <p>── 核心设计 ──
 * <p>普通做法要写两个函数（一个是求高度、一个是判平衡），每个节点被反复求高度 → O(n²)。
 * <p>巧办法：让 height() 兼职"报错"——不平衡就返回 -1，上层看到 -1 立刻跟着返回 -1（剪枝）。
 *
 * <p>height(node) 语义：
 * <p>  - 平衡 → 返回该子树高度（叶子为 1，空为 0）
 * <p>  - 不平衡 → 返回 -1
 */
public class P110BalancedBinaryTree {

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

    public boolean isBalanced(TreeNode root) {
        return height(root) != -1;
    }

    private int height(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int heightL = height(root.left);
        if (heightL == -1) {
            return -1;
        }
        int heightR = height(root.right);
        if (heightR == -1) {
            return -1;
        }
        if (Math.abs(heightL - heightR) > 1) {
            return -1;
        }
        return 1 + Math.max(heightL, heightR);
    }

    public static void main(String[] args) {
        P110BalancedBinaryTree solver = new P110BalancedBinaryTree();

        run(solver, new Integer[]{3, 9, 20, null, null, 15, 7}, true, "示例1 平衡");
        run(solver, new Integer[]{1, 2, 2, 3, 3, null, null, 4, 4}, false, "示例2 不平衡");
        run(solver, new Integer[]{}, true, "空树");
        run(solver, new Integer[]{1}, true, "单节点");
        run(solver, new Integer[]{1, 2}, true, "高度差 1");
        run(solver, new Integer[]{1, 2, null, 3}, false, "链状 3 层");
        run(solver, new Integer[]{1, 2, 3, 4, null, null, 5}, true, "两侧各差一层");
        run(solver, new Integer[]{1, null, 2, null, 3}, false, "右单链");
        run(solver, new Integer[]{1, 2, 2, 3, null, null, 3, 4, null, null, 4}, false, "深层不平衡");
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

    private static void run(P110BalancedBinaryTree solver, Integer[] tree, boolean expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        try {
            boolean got = solver.isBalanced(build(tree));
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " exp=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
