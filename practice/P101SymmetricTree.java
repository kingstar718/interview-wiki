import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 101: 对称二叉树 (Symmetric Tree)
 * https://leetcode.cn/problems/symmetric-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 递归
 * <p>【题型】同步递归比较「左右两棵子树是否镜像」
 * <p>【考点】交叉比较（左的左 vs 右的右、左的右 vs 右的左）；先判"都空"再判"一空一不空"
 * <p>【关联】P226 翻转二叉树（翻转后自比 = 对称判定）；P100 相同的树（直接比而非交叉）
 *
 * <p>给你一个二叉树的根节点 root，检查它是否轴对称。
 *
 * <p>约束：树中节点数目在范围 [1, 1000] 内；-100 &lt;= Node.val &lt;= 100。
 *
 * <p>示例1（true）：        示例2（false）：
 * <pre>
 *       1                        1
 *      / \                      / \
 *     2   2                    2   2
 *    / \ / \                    \   \
 *   3  4 4  3                    3   3
 * </pre>
 *
 * <p>── 核心转化 ──
 * <p>"整棵树是否对称" → "root 的左右两棵子树是否【镜像】"
 * <p>镜像(左a, 右b) 条件：a.val == b.val 且 镜像(a.left, b.right) 且 镜像(a.right, b.left)
 */
public class P101SymmetricTree {

    /**
     * 本地测试用的树节点（静态内部类，避免多文件同名冲突）。
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

    public boolean isSymmetric(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isMirror(root.left, root.right);
    }

    private boolean isMirror(TreeNode left, TreeNode right) {
        // 终止条件
        if (left == null && right == null) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        if (left.val != right.val) {
            return false;
        }
        return isMirror(left.left, right.right) && isMirror(left.right, right.left);
    }

    public static void main(String[] args) {
        P101SymmetricTree solver = new P101SymmetricTree();

        run(solver, new Integer[]{1, 2, 2, 3, 4, 4, 3}, true, "示例1 对称");
        run(solver, new Integer[]{1, 2, 2, null, 3, null, 3}, false, "示例2 结构不对称");
        run(solver, new Integer[]{}, true, "空树");
        run(solver, new Integer[]{1}, true, "单节点");
        run(solver, new Integer[]{1, 2, 2}, true, "只有一层孩子");
        run(solver, new Integer[]{1, 2, 3}, false, "左右值不同");
        run(solver, new Integer[]{1, 2, 2, 3, null, null, 3}, true, "深一层对称");
        run(solver, new Integer[]{1, 2, 2, 2, null, 2}, false, "值相同但结构不对称");
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

    private static void run(P101SymmetricTree solver, Integer[] tree, boolean expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        try {
            boolean got = solver.isSymmetric(build(tree));
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " exp=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
