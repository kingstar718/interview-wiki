import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 538: 把二叉搜索树转换为累加树 (Convert BST to Greater Tree)
 * https://leetcode.cn/problems/convert-bst-to-greater-tree/
 *
 * <p>【难度】Medium
 * <p>【标签】二叉搜索树(BST) · 二叉树(Binary Tree) · 深度优先搜索(DFS) · 中序
 * <p>【题型】反中序（右 → 根 → 左）一边降序遍历一边累加
 * <p>【考点】顺序必须反中序；先 `sum += val` 再 `val = sum`；sum 必须跨层共享
 * <p>【关联】P98 验证 BST（中序升序）；P230 第 K 小（正序中序）；P1038 同题
 *
 * <p>给出二叉【搜索】树的根节点，请你将它转换为累加树（Greater Sum Tree），
 * 使每个节点 node 的新值等于原树中【大于或等于】 node.val 的值之和。
 *
 * <p>约束：树中的节点数介于 [0, 10^4] 之间；-10^4 &lt;= Node.val &lt;= 10^4；
 * 树中的所有值互不相同；保证为 BST。
 *
 * <p>示例：root=[4,1,6,0,2,5,7,null,null,null,3,null,null,null,8]
 * <pre>
 * 输入:            输出:
 *        4               30
 *       / \             /  \\
 *      1   6          36    21
 *     / \ / \        / \   /  \\
 *    0  2 5  7      36 35 26   15
 *        \   \           \      \\
 *         3   8          33      8
 * </pre>
 *
 * <p>── 为什么反中序 ──
 * <p>中序（左→根→右）= 升序；反中序（右→根→左）= 降序。
 * <p>降序遍历时，"所有比我大的节点"已经全部访问过了，累加和 sum 正好是它们的和。
 */
public class P538ConvertBstToGreaterTree {

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

    /** 递归版共享累加和。⚠️ 每次调用 convertBST 前要重置为 0。 */
    private int sum = 0;

    public TreeNode convertBST(TreeNode root) {
        sum = 0;
        return cBST(root);
    }

    private TreeNode cBST(TreeNode root) {
        if (root == null) {
            return null;
        }
        cBST(root.right);

        sum += root.val;
        root.val = sum;

        cBST(root.left);
        return root;
    }

    public TreeNode convertBSTIterative(TreeNode root) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode curr = root;
        int sum = 0;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.right;
            }
            curr = stack.pop();
            sum += curr.val;
            curr.val = sum;
            curr = curr.left;
        }
        return root;
    }

    public static void main(String[] args) {
        P538ConvertBstToGreaterTree solver = new P538ConvertBstToGreaterTree();

        run(solver, new Integer[]{4, 1, 6, 0, 2, 5, 7, null, null, null, 3, null, null, null, 8},
                "示例1 官方用例");
        run(solver, new Integer[]{0, null, 1}, "示例2 只有右子");
        run(solver, new Integer[]{}, "空树");
        run(solver, new Integer[]{1}, "单节点");
        run(solver, new Integer[]{2, 1, 3}, "满二叉小树");
        run(solver, new Integer[]{5, 3, 8, 1, 4, 7, 9}, "完整三层");
        run(solver, new Integer[]{-3, -5, 0}, "含负数");
    }

    /** 层序序列化（保留 null 占位，去尾部 null）。 */
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

    /** 从原始 BST 推导期望结果：反中序降序累加，保持中序与原树一致。 */
    private static String expectedOf(Integer[] arr) {
        TreeNode root = build(arr);
        List<TreeNode> order = new ArrayList<>();
        collectInorder(root, order);          // 升序
        int total = 0;
        for (TreeNode n : order) total += n.val;
        // 升序第 i 个的新值 = 它和它之后所有值之和
        StringBuilder sb = new StringBuilder();
        int suffix = 0;
        int[] newVals = new int[order.size()];
        for (int i = order.size() - 1; i >= 0; i--) {
            suffix += order.get(i).val;
            newVals[i] = suffix;
        }
        for (int i = 0; i < order.size(); i++) order.get(i).val = newVals[i];
        return serialize(root);
    }

    private static void collectInorder(TreeNode n, List<TreeNode> out) {
        if (n == null) return;
        collectInorder(n.left, out);
        out.add(n);
        collectInorder(n.right, out);
    }

    private static void run(P538ConvertBstToGreaterTree solver, Integer[] tree, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        String expected = expectedOf(tree);
        runOne(label + " [递归]", expected, () -> {
            solver.sum = 0;
            return solver.convertBST(build(tree));
        });
        runOne(label + " [迭代]", expected, () -> solver.convertBSTIterative(build(tree)));
        System.out.println("        exp=" + expected);
    }

    private static void runOne(String label, String expected, java.util.function.Supplier<TreeNode> call) {
        try {
            String got = serialize(call.get());
            System.out.println((expected.equals(got) ? "[PASS] " : "[FAIL] ") + label + " | got=" + got);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
