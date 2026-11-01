import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

/**
 * LeetCode 108: 将有序数组转换为二叉搜索树 (Convert Sorted Array to BST)
 * https://leetcode.cn/problems/convert-sorted-array-to-binary-search-tree/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉搜索树(BST) · 二叉树(Binary Tree) · 分治 · 递归
 * <p>【题型】二分递归建树：取中点为根，左半建左子树，右半建右子树
 * <p>【考点】终止条件 `left &gt; right`（不是 `==`）；mid 用 `left + (right-left)/2` 防溢出；子树区间要排除 mid
 * <p>【关联】P105 前序+中序构造（同一套分治）；P109 链表版；P110 平衡判定（反向）
 *
 * <p>给你一个整数数组 nums，其中元素已经按【升序】排列，请你将其转换为一棵【高度平衡】二叉搜索树。
 * 高度平衡二叉树是一棵满足「每个节点的左右两个子树的高度差的绝对值不超过 1」的二叉树。
 *
 * <p>约束：1 &lt;= nums.length &lt;= 10^4；-10^4 &lt;= nums[i] &lt;= 10^4；
 * nums 按严格递增顺序排列。
 *
 * <p>示例：nums = [-10,-3,0,5,9]
 * <pre>
 *        0
 *       / \
 *     -3   9
 *     /   /
 *  -10   5
 * </pre>
 * <p>答案【不唯一】：（取左中位数或右中位数都合法）
 *
 * <p>── 为什么取中点就平衡 ──
 * <p>升序数组的中序就是原数组本身。取 mid 当根后，左半长度与右半长度最多差 1，
 * <p>递归下去每层都这样，所以树高差 ≤ 1。
 */
public class P108ConvertSortedArrayToBinarySearchTree {

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

    public TreeNode sortedArrayToBST(int[] nums) {
        return build(nums, 0, nums.length - 1);
    }

    private TreeNode build(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }
        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        root.left = build(nums, left, mid - 1);
        root.right = build(nums, mid + 1, right);
        return root;
    }


    public static void main(String[] args) {
        P108ConvertSortedArrayToBinarySearchTree solver = new P108ConvertSortedArrayToBinarySearchTree();

        run(solver, new int[]{-10, -3, 0, 5, 9}, "示例1");
        run(solver, new int[]{1, 3}, "示例2 两元素");
        run(solver, new int[]{}, "空数组");
        run(solver, new int[]{1}, "单元素");
        run(solver, new int[]{1, 2, 3}, "三元素");
        run(solver, new int[]{1, 2, 3, 4, 5, 6, 7}, "满二叉树规模");
        run(solver, new int[]{-5, -4, -3, -2, -1}, "全负数");
    }

    private static void run(P108ConvertSortedArrayToBinarySearchTree solver, int[] nums, String name) {
        String label = name + " nums=" + java.util.Arrays.toString(nums);
        try {
            TreeNode root = solver.sortedArrayToBST(nums);
            // 校验1：中序必须严格递增且长度一致（BST 性质）
            java.util.List<Integer> inorder = new java.util.ArrayList<>();
            collectInorder(root, inorder);
            boolean inorderOk = inorder.size() == nums.length;
            for (int i = 0; inorderOk && i < nums.length; i++) {
                if (inorder.get(i) != nums[i]) inorderOk = false;
            }
            // 校验2：必须高度平衡
            boolean balanced = checkBalanced(root).balanced;

            if (!inorderOk) {
                System.out.println("[FAIL] " + label + " | 中序不等于原数组：" + inorder);
            } else if (!balanced) {
                System.out.println("[FAIL] " + label + " | 不是高度平衡树");
            } else {
                System.out.println("[PASS] " + label + " | 中序正确且平衡，树=" + serialize(root));
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }

    private static void collectInorder(TreeNode n, java.util.List<Integer> out) {
        if (n == null) return;
        collectInorder(n.left, out);
        out.add(n.val);
        collectInorder(n.right, out);
    }

    private static class HeightInfo {
        boolean balanced;
        int height;
        HeightInfo(boolean b, int h) { balanced = b; height = h; }
    }

    private static HeightInfo checkBalanced(TreeNode n) {
        if (n == null) return new HeightInfo(true, 0);
        HeightInfo l = checkBalanced(n.left);
        HeightInfo r = checkBalanced(n.right);
        boolean ok = l.balanced && r.balanced && Math.abs(l.height - r.height) <= 1;
        return new HeightInfo(ok, 1 + Math.max(l.height, r.height));
    }

    private static String serialize(TreeNode root) {
        if (root == null) return "[]";
        java.util.List<String> out = new java.util.ArrayList<>();
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
        return "[" + String.join(" ", out.subList(0, end)) + "]";
    }
}
