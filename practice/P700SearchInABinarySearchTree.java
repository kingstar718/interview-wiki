import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 700: 二叉搜索树中的搜索 (Search in a Binary Search Tree)
 * https://leetcode.cn/problems/search-in-a-binary-search-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉搜索树(BST) · 二叉树(Binary Tree)
 * <p>【题型】利用 BST 有序性，沿一条路径搜索目标节点
 * <p>【考点】终止条件；递归返回值；递归与迭代对照
 * <p>【关联】P235 BST 最近公共祖先；P98 验证 BST
 *
 * <p>给定二叉搜索树的根节点 root 和整数 val，返回值为 val 的节点，
 * 即以该节点为根的子树；不存在则返回 null。返回原树中的节点，无需复制。
 * <p>树中节点数为 [1, 5000]；节点值和 val 为 [1, 10^7]；保证输入为 BST。
 * 自测额外包含空树，练习边界处理。
 *
 * <pre>
 *       4
 *      / \
 *     2   7
 *    / \
 *   1   3
 *
 * val=2 → 返回以 2 为根的子树 [2,1,3]
 * val=5 → null
 * </pre>
 * <p>── 思路引导 ──
 * <p>1. 终止条件：当前节点为空，或当前节点的值恰好等于目标值。
 * <p>2. 方向判断：根据目标值与当前值的大小关系，选择下一棵子树。
 * <p>3. 两种写法：递归版把子问题的结果返回；迭代版维护当前搜索节点。
 * <p>4. 复杂度：用树高 h 表达时间复杂度，分别考虑平衡树与退化链；
 *    空间复杂度区分递归调用栈和迭代变量。
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
        if (root == null) return null;
        if (root.val > val) return searchBST(root.left, val);
        if (root.val < val) return searchBST(root.right, val);
        return root;
    }

    public TreeNode searchBSTIterative(TreeNode root, int val) {
        while (root != null) {
            if (root.val > val) {
                root = root.left;
            } else if (root.val < val) {
                root = root.right;
            } else {
                return root;
            }
        }
        return root;
    }

    public static void main(String[] args) {
        P700SearchInABinarySearchTree solver = new P700SearchInABinarySearchTree();
        Integer[] example = {4, 2, 7, 1, 3};
        run(solver, example, 2, "示例1：命中内部节点");
        run(solver, example, 5, "示例2：值不存在");
        run(solver, example, 4, "命中根节点");
        run(solver, example, 1, "命中最左叶子");
        run(solver, example, 7, "命中右侧叶子");
        run(solver, example, 0, "小于所有节点");
        run(solver, example, 8, "大于所有节点");
        run(solver, new Integer[]{}, 1, "空树");
        run(solver, new Integer[]{1}, 1, "单节点命中");
        run(solver, new Integer[]{1}, 2, "单节点未命中");
        run(solver, new Integer[]{4, 3, null, 2, null, 1}, 1, "左链");
        run(solver, new Integer[]{1, null, 2, null, 3, null, 4}, 4, "右链");
        run(solver, new Integer[]{5000000, 1, 10000000}, 10000000, "节点值上界");
    }

    private static void run(P700SearchInABinarySearchTree solver, Integer[] tree,
                            int val, String name) {
        String label = name + " val=" + val;
        TreeNode recursiveRoot = build(tree);
        runOne(label + " [递归]", findForTest(recursiveRoot, val),
                () -> solver.searchBST(recursiveRoot, val));
        TreeNode iterativeRoot = build(tree);
        runOne(label + " [迭代]", findForTest(iterativeRoot, val),
                () -> solver.searchBSTIterative(iterativeRoot, val));
    }

    private static void runOne(String label, TreeNode expected,
                               java.util.function.Supplier<TreeNode> call) {
        try {
            TreeNode got = call.get();
            // 比较引用：返回原树中的子树，不能只新建同值节点。
            boolean ok = got == expected;
            System.out.println((ok ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + valueOf(got) + " exp=" + valueOf(expected)
                    + " | same reference=" + ok);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }

    private static String valueOf(TreeNode node) {
        return node == null ? "null" : String.valueOf(node.val);
    }

    /** 测试辅助：全树查找预期节点，不提供待练习的 BST 搜索实现。 */
    private static TreeNode findForTest(TreeNode root, int val) {
        if (root == null) return null;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node.val == val) return node;
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        return null;
    }

    private static TreeNode build(Integer[] values) {
        if (values == null || values.length == 0 || values[0] == null) return null;
        TreeNode root = new TreeNode(values[0]);
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode node = queue.poll();
            if (values[i] != null) {
                node.left = new TreeNode(values[i]);
                queue.offer(node.left);
            }
            i++;
            if (i < values.length && values[i] != null) {
                node.right = new TreeNode(values[i]);
                queue.offer(node.right);
            }
            i++;
        }
        return root;
    }
}
