import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 226: 翻转二叉树 (Invert Binary Tree)
 * https://leetcode.cn/problems/invert-binary-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 递归
 * <p>【题型】递归三部曲最简形态：交换左右子树 + 递归处理两侧
 * <p>【考点】先交换再递归 / 先递归再交换都可以；不能写成 root.left = invert(root.right) 后再用 root.left
 * <p>【关联】P101 对称二叉树（镜像判定的本质就是翻转后自比）
 *
 * <p>给你一棵二叉树的根节点 root，翻转这棵二叉树，并返回其根节点。
 *
 * <p>约束：树中节点数目范围在 [0, 100] 内；-100 &lt;= Node.val &lt;= 100。
 *
 * <p>示例：
 * <pre>
 * 输入        4              输出        4
 *           /   \                     /   \
 *          2     7                   7     2
 *         / \   / \                 / \   / \
 *        1   3 6   9               9   6 3   1
 * </pre>
 *
 * <p>── 递归三部曲（本章要默写的模板）──
 * <p>① 参数与返回值：invertTree(TreeNode) 返回翻转后的根
 * <p>② 终止条件：root == null 返回 null
 * <p>③ 单层逻辑：交换 root 的左右子 → 递归翻转左右子树 → 返回 root
 */
public class P226InvertBinaryTree {

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

    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        invertTree(root.left);
        invertTree(root.right);
        return root;
    }

    public static void main(String[] args) {
        P226InvertBinaryTree solver = new P226InvertBinaryTree();

        run(solver, new Integer[]{4, 2, 7, 1, 3, 6, 9}, "4 7 2 9 6 3 1", "示例1");
        run(solver, new Integer[]{2, 1, 3}, "2 3 1", "小树");
        run(solver, new Integer[]{}, "", "空树");
        run(solver, new Integer[]{1}, "1", "单节点");
        run(solver, new Integer[]{1, 2}, "1 null 2", "只有左子→翻转后变右子");
        run(solver, new Integer[]{1, null, 2}, "1 2", "只有右子→翻转后变左子");
        run(solver, new Integer[]{1, 2, 3, 4}, "1 3 2 null null null 4", "不完全树");
    }

    /** 层序序列化，保留 null 占位（否则丢失左右位置，测不出翻转），去掉尾部连续 null。 */
    private static String serialize(TreeNode root) {
        if (root == null) return "";
        java.util.List<String> out = new java.util.ArrayList<>();
        // LinkedList 允许 null（ArrayDeque 不允许，会抛 NPE）
        java.util.Deque<TreeNode> q = new java.util.LinkedList<>();
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

    private static void run(P226InvertBinaryTree solver, Integer[] tree, String expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        try {
            TreeNode got = solver.invertTree(build(tree));
            String gotStr = serialize(got);
            System.out.println((expected.equals(gotStr) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=[" + gotStr + "] exp=[" + expected + "]");
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
