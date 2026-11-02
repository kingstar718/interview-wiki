import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 617: 合并二叉树 (Merge Two Binary Trees)
 * https://leetcode.cn/problems/merge-two-binary-trees/
 *
 * <p>【难度】Easy
 * <p>【标签】二叉树(Binary Tree) · 深度优先搜索(DFS) · 递归
 * <p>【题型】同步递归两棵树：同位置都在就相加，只有一个在就直接用它
 * <p>【考点】返回值语义（合并后的子树根）；三/四个分支的穷举；直接把 root1 当结果树修改
 * <p>【关联】P101 对称二叉树（也是双树同步递归）；P226 翻转（单树）
 *
 * <p>给你两棵二叉树：root1 和 root2。
 * 想象一下，当你将其中一棵覆盖到另一棵之上时，两棵树上的一些节点将会重叠（而另一些不会）。
 * 你需要将这两棵树合并成一棵新二叉树。合并的规则是：
 *  - 如果两个节点重叠，那么将这两个节点的值相加作为合并后节点的新值；
 *  - 否则，不为 null 的节点将直接作为新二叉树的节点。
 * 返回合并后的二叉树。
 *
 * <p>注意：合并过程必须从两个树的根节点开始。
 *
 * <p>约束：两棵树中的节点数目在范围 [0, 2000] 内；-10^4 &lt;= Node.val &lt;= 10^4。
 *
 * <p>示例1：root1=[1,3,2,5], root2=[2,1,3,null,4,null,7]
 * <pre>
 *     树1          树2           合并后
 *      1            2              3
 *     / \          / \            / \
 *    3   2        1   3          4   5
 *   /    \      / \   \        / \   \
 *  5      4    4  (5)  7      5   4   7
 * </pre>
 *
 * <p>示例2：root1=[1], root2=[1,2] → [2,2]
 */
public class P617MergeTwoBinaryTrees {

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

    /**
     * 你的递归版（AC 182/182）。
     *
     * <p>出口 = 穷举：一边空 → 直接返回另一边（整棵子树原样嫁接，连 val 都不碰）。
     * <p>曾踩的坑：写成 {@code root1 = root2;}（赋值搬运）而不是 {@code return root2;}，
     * <p>　　之后 {@code mergeTrees(root1.left, root2.left)} 变成 merge(n, n) 自己加自己 → 子树值翻倍。
     * <p>教训：树里"原样用"不叫赋值，叫直接返回那个指针。
     */
    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {
        if (root1 == null) {
            return root2;
        }
        if (root2 == null) {
            return root1;
        }
        root1.val += root2.val;
        root1.left = mergeTrees(root1.left, root2.left);
        root1.right = mergeTrees(root1.right, root2.right);
        return root1;
    }

    /**
     * 你的层序版（AC 182/182，成对入队/出队）。
     *
     * <p>【易混点】{@code dq.poll()} 官方约定"空则返回 null"，IDEA 会报 NPE 虚警——
     * <p>　　但 offer 永远两两成对、poll 也永远成对 → 队列长度永远 ≥2 且为偶数，
     * <p>　　第二轮 poll 前队列必非空。空返回值永远取不到。
     *
     * <p>【分支穷举】以 a 为挂载点：
     * <p>　　两边都有 → 成对入队，下层继续同步走
     * <p>　　a 有 b 无 → 什么都不做（a 自己的孩子留着，b.left=null 不需要动）
     * <p>　　a 无 b 有（含双空）→ a.left = b.left，掉队的一侧整棵嫁接
     */
    public TreeNode mergeTreesBFS(TreeNode root1, TreeNode root2) {
        if (root1 == null) return root2;
        if (root2 == null) return root1;
        Deque<TreeNode> dq = new ArrayDeque<>();
        dq.offer(root1);
        dq.offer(root2);
        while (!dq.isEmpty()) {
            TreeNode a = dq.poll(), b = dq.poll();
            a.val += b.val;
            if (a.left != null && b.left != null) {
                dq.offer(a.left);
                dq.offer(b.left);
            } else if (a.left == null) {
                a.left = b.left;
            }
            if (a.right != null && b.right != null) {
                dq.offer(a.right);
                dq.offer(b.right);
            } else if (a.right == null) {
                a.right = b.right;
            }
        }
        return root1;
    }

    public static void main(String[] args) {
        P617MergeTwoBinaryTrees solver = new P617MergeTwoBinaryTrees();

        run(solver, new Integer[]{1, 3, 2, 5}, new Integer[]{2, 1, 3, null, 4, null, 7},
                "3 4 5 5 4 null 7", "示例1 官方");
        run(solver, new Integer[]{1}, new Integer[]{1, 2}, "2 2", "示例2 尺寸不同");
        run(solver, new Integer[]{}, new Integer[]{1}, "1", "树1为空");
        run(solver, new Integer[]{1}, new Integer[]{}, "1", "树2为空");
        run(solver, new Integer[]{}, new Integer[]{}, "", "两棵都空");
        run(solver, new Integer[]{1, 2, 3}, new Integer[]{1, 2, 3}, "2 4 6", "完全相同结构");
        run(solver, new Integer[]{1, null, 2}, new Integer[]{0, 3, null}, "1 3 2", "错位不重叠");
        run(solver, new Integer[]{}, new Integer[]{1, 2, 3}, "1 2 3", "树1空·树2带孩子");
        run(solver, new Integer[]{-1, -2}, new Integer[]{-3, -4}, "-4 -6", "含负数相加");
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

    private static void run(P617MergeTwoBinaryTrees solver, Integer[] t1, Integer[] t2,
                            String expected, String name) {
        String label = name + " t1=" + java.util.Arrays.toString(t1)
                + " t2=" + java.util.Arrays.toString(t2);
        try {
            String got = serialize(solver.mergeTrees(build(t1), build(t2)));
            String gotBfs = serialize(solver.mergeTreesBFS(build(t1), build(t2)));
            if (!got.equals(gotBfs)) {
                System.out.println("[MISMATCH] " + label + " | 递归=[" + got + "] 层序=[" + gotBfs + "]");
            }
            System.out.println((expected.equals(got) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=[" + got + "] exp=[" + expected + "]");
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}
