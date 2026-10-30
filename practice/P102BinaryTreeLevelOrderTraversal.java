import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.Queue;

/**
 * LeetCode 102: 二叉树的层序遍历 (Binary Tree Level Order Traversal)
 * https://leetcode.cn/problems/binary-tree-level-order-traversal/
 *
 * <p>【难度】Medium
 * <p>【标签】二叉树(Binary Tree) · 广度优先搜索(BFS)
 * <p>【题型】BFS 逐层收集：队列 + size 快照控层
 * <p>【考点】q.size() 必须在 for 循环【前】快照；ArrayDeque 不允许 null
 * <p>【关联】P104 最大深度(BFS版)、P199 右视图、P103 锯齿遍历
 *
 * <p>给你二叉树的根节点 root，返回其节点值的层序遍历。（即逐层地，从左到右访问所有节点）
 *
 * <p>约束：树中节点数目在范围 [0, 2000] 内；-1000 &lt;= Node.val &lt;= 1000。
 *
 * <p>── BFS 模板（本章要默写的四大模板之一）──
 * <pre>
 * queue.offer(root)
 * while (!queue.isEmpty()) {
 *     int size = queue.size();          // ← 当前层宽度，必须先快照
 *     for (int i = 0; i &lt; size; i++) {  // 只处理这一层
 *         node = queue.poll();
 *         ...收集 node.val...
 *         if (left != null) offer(left);
 *         if (right != null) offer(right);
 *     }
 * }
 * </pre>
 */
public class P102BinaryTreeLevelOrderTraversal {

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

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> r = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode poll = queue.poll();
                r.add(poll.val);
                if (poll.left != null) {
                    queue.offer(poll.left);
                }
                if (poll.right != null) {
                    queue.offer(poll.right);
                }
            }
            res.add(r);
        }
        return res;
    }

    public static void main(String[] args) {
        P102BinaryTreeLevelOrderTraversal solver = new P102BinaryTreeLevelOrderTraversal();

        run(solver, new Integer[]{3, 9, 20, null, null, 15, 7}, "[[3], [9, 20], [15, 7]]", "示例1");
        run(solver, new Integer[]{}, "[]", "空树");
        run(solver, new Integer[]{1}, "[[1]]", "单节点");
        run(solver, new Integer[]{1, 2}, "[[1], [2]]", "左单支");
        run(solver, new Integer[]{1, null, 2}, "[[1], [2]]", "右单支");
        run(solver, new Integer[]{1, 2, 3, 4, 5, null, 6}, "[[1], [2, 3], [4, 5, 6]]", "不完全树");
        run(solver, new Integer[]{1, 2, 3, 4, 5, 6, 7}, "[[1], [2, 3], [4, 5, 6, 7]]", "满二叉树");
        run(solver, new Integer[]{1, 2, null, 3, null, 4}, "[[1], [2], [3], [4]]", "左单链多深");
    }

    /** BFS 建树：null 表示空位，跳过不建节点（LeetCode 的数组表示法）。 */
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

    private static void run(P102BinaryTreeLevelOrderTraversal solver, Integer[] tree, String expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        try {
            List<List<Integer>> got = solver.levelOrder(build(tree));
            String gotStr = got.toString();
            System.out.println((expected.equals(gotStr) ? "[PASS] " : "[FAIL] ") + label
                    + "\n        got=" + gotStr + "\n        exp=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}

/**
 * 本地测试用的树节点。
 * ⚠️ 提交 LeetCode 时删除本类（LeetCode 已预定义 TreeNode）。
 */
class TreeNode {
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