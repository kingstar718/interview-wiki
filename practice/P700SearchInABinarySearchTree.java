import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 700: 二叉搜索树中的搜索 (Search in a Binary Search Tree)
 * https://leetcode.cn/problems/search-in-a-binary-search-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉搜索树(BST) · 二叉树(Binary Tree) · 深度优先搜索(DFS)
 * <p>【题型】BST 方向搜索的基线题：比大小决定往左还是往右
 * <p>【考点】返回值是"以该节点为根的整棵子树"，不是单个节点；用 BST 性质而非全遍历
 * <p>【关联】P235 BST 的 LCA（同一套方向判断）；P98 验证 BST；P701 插入
 *
 * <p>给定二叉搜索树（BST）的根节点 root 和一个整数值 val。
 * 你需要在 BST 中找到节点值等于 val 的节点，返回以该节点为根的【子树】。
 * 如果节点不存在，则返回 null。
 *
 * <p>约束：树中节点数在 [1, 5000] 范围内；1 &lt;= Node.val &lt;= 10^7；
 * root 是二叉搜索树；1 &lt;= val &lt;= 10^7。
 *
 * <p>示例：root=[4,2,7,1,3], val=2
 * <pre>
 *       4                 2
 *      / \       →       / \
 *     2   7             1   3
 *    / \
 *   1   3
 * </pre>
 *
 * <p>── BST 搜索 vs 全遍历 ──
 * <p>val &lt; root.val → 一定在左子树（右子树全比 root 大，不可能有 val）
 * <p>val &gt; root.val → 一定在右子树
 * <p>val == root.val → 命中
 * <p>每一步排除一半，O(h)；全遍历则 O(n)，没有利用 BST 性质。
 */
public class P700SearchInABinarySearchTree {

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

    public TreeNode searchBST(TreeNode root, int val) {
        while (root != null) {
            if (root.val > val) {
                root = root.left;
            } else if (root.val < val) {
                root = root.right;
            } else {
                return root;
            }
        }
        return null;
    }

    public TreeNode searchBSTRecursive(TreeNode root, int val) {
        if (root == null) return null;
        if (root.val > val) return searchBSTRecursive(root.left, val);
        if (root.val < val) return searchBSTRecursive(root.right, val);
        return root;
    }

    public static void main(String[] args) {
        P700SearchInABinarySearchTree solver = new P700SearchInABinarySearchTree();

        Integer[] t = {4, 2, 7, 1, 3};
        run(solver, t, 2, "2 1 3", "示例1 命中中间");
        run(solver, t, 1, "1", "叶子节点");
        run(solver, t, 7, "7", "右子树单节点");
        run(solver, t, 4, "4 2 7 1 3", "命中根 → 返回整棵树");
        run(solver, t, 5, "", "不存在 → null");
        run(solver, new Integer[]{1}, 1, "1", "单节点命中");
        run(solver, new Integer[]{1}, 2, "", "单节点未命中");
        run(solver, new Integer[]{8, 3, 10, 1, 6, null, 14, null, null, 4, 7, 13},
                6, "6 4 7", "深层命中");
    }

    private static String serialize(TreeNode root) {
        if (root == null) return "";
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
        return String.join(" ", out.subList(0, end));
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

    private static void run(P700SearchInABinarySearchTree solver, Integer[] tree, int val,
                            String expected, String name) {
        String label = name + " val=" + val;
        runOne(label + " [迭代]", expected, () -> solver.searchBST(build(tree), val));
        runOne(label + " [递归]", expected, () -> solver.searchBSTRecursive(build(tree), val));
    }

    private static void runOne(String label, String expected, java.util.function.Supplier<TreeNode> call) {
        try {
            String got = serialize(call.get());
            System.out.println((expected.equals(got) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=[" + got + "] exp=[" + expected + "]");
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
