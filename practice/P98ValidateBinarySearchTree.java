import java.util.ArrayDeque;
import java.util.Queue;

/**
 * LeetCode 98: 验证二叉搜索树 (Validate Binary Search Tree)
 * https://leetcode.cn/problems/validate-binary-search-tree/
 *
 * <p>【难度】Medium
 * <p>【标签】二叉树(Binary Tree) · 二叉搜索树(BST) · 深度优先搜索(DFS) · 中序
 * <p>【题型】BST 性质判定：两条路——中序递增 / 上下界传参
 * <p>【考点】不能只比父子；BST 是严格不等（相等即非法）；上下界要用 Integer/long 表示"无边界"
 * <p>【关联】P230 第K小（中序）；P235 BST 的 LCA（利用有序性剪枝）；P538 反中序
 *
 * <p>给你一个二叉树的根节点 root，判断其是否是一个有效的二叉搜索树。
 * 有效二叉搜索树定义如下：
 *  - 节点的左子树只包含【严格小于】当前节点的数。
 *  - 节点的右子树只包含【严格大于】当前节点的数。
 *  - 所有左子树和右子树自身必须也是二叉搜索树。
 *
 * <p>约束：树中节点数目范围在 [1, 10^4] 内；-2^31 &lt;= Node.val &lt;= 2^31 - 1。
 *
 * <p>示例1：[2,1,3] → true
 * <p>示例2：[5,1,4,null,null,3,6] → false（4 在 5 的右子树却小于 5）
 *
 * <p>── 两条路，都要写 ──
 * <p>① 中序递增：BST 的中序 = 严格升序。遍历时记住上一个值 prev，要求 node.val > prev。
 * <p>   注意 prev 必须是"跨递归共享"的，用成员变量或数组容器。
 * <p>② 上下界传参：给每个节点传开区间 (low, high)，节点值必须在其中；
 * <p>   往左走把 high 收窄为 node.val，往右走把 low 收窄为 node.val。
 */
public class P98ValidateBinarySearchTree {

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

    /** 中序版共享状态：上一个被访问的节点值。⚠️ 每次调用 isValidBST 前要重置为 null。 */
    private Integer prev = null;

    public boolean isValidBST(TreeNode root) {
        prev = null;
        return dfs(root);
    }

    private boolean dfs(TreeNode root) {
        if (root == null) {
            return true;
        }
        if (!dfs(root.left)) {
            return false;
        }
        if (prev != null && prev >= root.val) {
            return false;
        }
        prev = root.val;
        return dfs(root.right);
    }

    private boolean dfs(TreeNode root, Integer prev) {
        if (root == null) {
            return true;
        }
        if (!dfs(root.left, prev)) {
            return false;
        }
        if (prev != null && prev >= root.val) {
            return false;
        }
        prev = root.val;
        return dfs(root.right, prev);
    }

    public boolean isValidBSTByBounds(TreeNode root) {
        return validate(root, null, null);
    }

    private boolean validate(TreeNode root, Integer low, Integer high) {
        if (root == null) {
            return true;
        }
        if (low != null && low >= root.val) {
            return false;
        }
        if (high != null && high <= root.val) {
            return false;
        }
        return validate(root.left, low, root.val) && validate(root.right, root.val, high);
    }

    public static void main(String[] args) {
        P98ValidateBinarySearchTree solver = new P98ValidateBinarySearchTree();

        run(solver, new Integer[]{2, 1, 3}, true, "示例1 合法 BST");
        run(solver, new Integer[]{5, 1, 4, null, null, 3, 6}, false, "示例2 4 在 5 右子树却更小");
        run(solver, new Integer[]{1, 1}, false, "相等值非法（严格不等）");
        run(solver, new Integer[]{5, 4, 6, null, null, 3, 7}, false, "3 在 5 右子树却更小");
        run(solver, new Integer[]{}, true, "空树");
        run(solver, new Integer[]{1}, true, "单节点");
        run(solver, new Integer[]{1, null, 2}, true, "只有右子");
        run(solver, new Integer[]{2, null, 1}, false, "右子更小");
        run(solver, new Integer[]{2147483647}, true, "int 最大值边界");
        run(solver, new Integer[]{-2147483648, null, 2147483647}, true, "int 两端边界");
        run(solver, new Integer[]{10, 5, 15, null, null, 6, 20}, false, "6 在 10 右子树却更小");
        run(solver, new Integer[]{3, 1, 5, 0, 2, 4, 6}, true, "完整三层合法 BST");
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

    private static void run(P98ValidateBinarySearchTree solver, Integer[] tree, boolean expected, String name) {
        String label = name + " tree=" + java.util.Arrays.toString(tree);
        runOne(label + " [中序]", expected, () -> solver.isValidBST(build(tree)));
        runOne(label + " [上下界]", expected, () -> solver.isValidBSTByBounds(build(tree)));
    }

    private static void runOne(String label, boolean expected, java.util.function.BooleanSupplier call) {
        try {
            boolean got = call.getAsBoolean();
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " exp=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
