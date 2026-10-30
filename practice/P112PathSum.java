import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 112: 路径总和 (Path Sum)
 * https://leetcode.cn/problems/path-sum/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 递归
 * <p>【题型】递归带「累减」的目标值，叶子处判断是否减到 0
 * <p>【考点】必须在【叶子】判断，不能在中间节点判断；空树返回 false（不是 targetSum==0）
 * <p>【关联】P257 所有路径（要收集则需回溯）；P113 路径总和 II；P437 路径总和 III
 *
 * <p>给你二叉树的根节点 root 和一个表示目标和的整数 targetSum。
 * 判断该树中是否存在【根节点到叶子节点】的路径，这条路径上所有节点值相加等于目标和 targetSum。
 * 叶子节点是指没有子节点的节点。
 *
 * <p>约束：树中节点的数目在范围 [0, 5000] 内；-1000 &lt;= Node.val &lt;= 1000；
 * -1000 &lt;= targetSum &lt;= 1000。
 *
 * <p>示例：root=[5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum=22 → true（5→4→11→2）
 *
 * <p>── 思路：减法代替累加 ──
 * <p>进入节点时 targetSum -= node.val，到叶子时只需判断"剩余值 == 当前节点值"。
 * <p>这样做的好处：不需要额外参数记录"路径累加和"，targetSum 本身就在传递状态。
 */
public class P112PathSum {

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

    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }
        if (root.left == null && root.right == null) {
            return targetSum - root.val == 0;
        }
        return hasPathSum(root.left, targetSum - root.val)
                || hasPathSum(root.right, targetSum - root.val);
    }

    public static void main(String[] args) {
        P112PathSum solver = new P112PathSum();

        run(solver, new Integer[]{5, 4, 8, 11, null, 13, 4, 7, 2, null, null, null, 1}, 22, true, "示例1 5→4→11→2");
        run(solver, new Integer[]{1, 2, 3}, 5, false, "示例2 没有根到叶路径");
        run(solver, new Integer[]{}, 0, false, "空树（即使 target=0 也 false）");
        run(solver, new Integer[]{1}, 1, true, "根即叶子 命中");
        run(solver, new Integer[]{1}, 2, false, "根即叶子 未命中");
        run(solver, new Integer[]{1, 2}, 1, false, "非叶子不能当终点");
        run(solver, new Integer[]{1, 2}, 3, true, "根→左子");
        run(solver, new Integer[]{-2, null, -3}, -5, true, "全负数");
        run(solver, new Integer[]{1, -2, -3, 1, 3, -2, null, -1}, -1, true, "含负数多路径");
        run(solver, new Integer[]{1, 2, 3, 4, 5}, 4, true, "根→右子(1→3)命中");
        run(solver, new Integer[]{1, 2, 3, 4, 5}, 9, false, "所有路径和都≠9");
        run(solver, new Integer[]{1, 2, 3, 4, 5}, 8, true, "1→2→5 命中");
        run(solver, new Integer[]{1, 2, 3, 4, 5}, 15, false, "总和是15但不是根到叶路径");
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

    private static void run(P112PathSum solver, Integer[] tree, int target, boolean expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree) + " target=" + target;
        try {
            boolean got = solver.hasPathSum(build(tree), target);
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " exp=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
