import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 257: 二叉树的所有路径 (Binary Tree Paths)
 * https://leetcode.cn/problems/binary-tree-paths/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 回溯(Backtracking)
 * <p>【题型】本章第一次出现「回溯撤销」：选择 → 递归 → 撤销
 * <p>【考点】StringBuilder 用 setLength(len) 回溯；箭头只在【非叶子】后添加
 * <p>【关联】P112 路径总和；P113 路径总和 II（收集路径 + 按和过滤）
 *
 * <p>给你一个二叉树的根节点 root，按【任意顺序】，返回所有从根节点到叶子节点的路径。
 * 叶子节点是指没有子节点的节点。
 *
 * <p>约束：树中节点的数目在范围 [1, 100] 内；-100 &lt;= Node.val &lt;= 100。
 *
 * <p>示例：树 [1,2,3,null,5] → ["1->2->5", "1->3"]
 * <pre>
 *        1
 *       / \
 *      2   3
 *       \
 *        5
 * </pre>
 *
 * <p>── 回溯模板 ──
 * <p>① 选择：把当前节点接到路径上（sb.append）
 * <p>② 递归：处理左右子树
 * <p>③ 撤销：sb.setLength(进入本层前的长度) —— 把本层的改动抹掉，交给兄弟分支
 */
public class P257BinaryTreePaths {

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

    public List<String> binaryTreePaths(TreeNode root) {
        List<String> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        dfs(root, new StringBuilder(), res);
        return res;
    }

    private void dfs(TreeNode root, StringBuilder sb, List<String> res) {
        int len = sb.length();
        sb.append(root.val);
        if (root.left == null && root.right == null) {
            res.add(sb.toString());
        } else {
            sb.append("->");
            if (root.left != null) {
                dfs(root.left, sb, res);
            }
            if (root.right != null) {
                dfs(root.right, sb, res);
            }
        }
        sb.setLength(len);
    }

    public static void main(String[] args) {
        P257BinaryTreePaths solver = new P257BinaryTreePaths();

        run(solver, new Integer[]{1, 2, 3, null, 5}, "示例1");
        run(solver, new Integer[]{1}, "单节点");
        run(solver, new Integer[]{1, 2}, "只有左子");
        run(solver, new Integer[]{1, null, 2}, "只有右子");
        run(solver, new Integer[]{1, 2, 3, 4, 5, 6, 7}, "满二叉树");
        run(solver, new Integer[]{-1, -2, 3}, "含负数");
        run(solver, new Integer[]{1, 2, 3, null, 5, null, 7}, "两侧不对称");
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

    private static void run(P257BinaryTreePaths solver, Integer[] tree, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        try {
            List<String> got = solver.binaryTreePaths(build(tree));
            List<String> sorted = new ArrayList<>(got);
            java.util.Collections.sort(sorted);
            System.out.println("[INFO] " + label + "\n        got(排序后)=" + sorted
                    + "\n        （顺序任意，人工对照上面是否覆盖全部根到叶路径）");
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
