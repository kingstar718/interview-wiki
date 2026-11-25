import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * LeetCode 199: 二叉树的右视图 (Binary Tree Right Side View)
 * https://leetcode.cn/problems/binary-tree-right-side-view/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二叉树组 4/6——BFS 首秀·每层最后一个）
 *
 * <p>■ 题目描述
 *   给二叉树根节点，想象你站在树的**右侧**，从上到下看——
 *   返回你能看到的节点值的列表（**每层最靠右**的那个节点）。
 *
 * <p>■ 输入输出（约束）
 *   输入：TreeNode root（节点数 [0, 100]；-100 ≤ val ≤ 100）
 *   输出：List&lt;Integer&gt;（从上到下，每层一个值）
 *
 * <p>■ 示例逐条
 * <pre>
 *     1          ← 右视图第一层看到 1
 *    / \\
 *   2   3        ← 第二层看到 3（2 被 3 挡住）
 *    \\   \\
 *     5   4      ← 第三层看到 4
 * 输入 [1,2,3,null,5,null,4] → 输出 [1,3,4]
 * [1,null,3] → [1,3]
 * [] → []（空树）
 * [1] → [1]
 * 左斜链 1/2/3/4/5（全靠左）→ [1,2,3,4,5]（每层只有一个，全可见）
 *   1
 *  / \\
 * 2   3
 *    /
 *   6
 * 输入 [1,2,3,null,null,6] → 输出 [1,3,6]（第三层只有 6，虽然 6 是左孩子也可见！）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/美团）
 * <p>【标签】树 · 深度优先 · 广度优先 · 二叉树
 * <p>【题型】层序遍历（BFS）的应用——每层取【最后一个】：
 * <p>　　"按层处理"是 BFS 的主场（方向判据：横向分层的问题用队列）
 * <p>　　关键动作：每层开始时记下当前队列长度（=本层节点数），
 * <p>　　　　　　　只在内层循环的【最后一轮】收集本层值
 * <p>【考点】分层技巧（size 快照）；"最后一个"的取法（不删队首、数到本层末）；
 * <p>　　　　 DFS 也能做（右子树优先，每层第一个到达的收）——两版对照
 * <p>【关联】P94/230（中序家族——纵向视角）；P102 层序遍历（一阶段，本题是它的变体）；
 * <p>　　　　P104 深度（层数概念）
 *
 * <p>LCS 签名：rightSideView(TreeNode root)。
 */
public class P199BinaryTreeRightSideView {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.right = right; }
    }

    /** 你的实现（BFS v1：整层全收+右优先择一入队——待对照哨兵验证）。 */
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        bfs(root, res);
        return res;
    }

    private void bfs(TreeNode root, List<Integer> res) {
        Deque<TreeNode> queue = new ArrayDeque<>();
        if (root != null) {
            queue.offer(root);
        }
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode poll = queue.poll();
                int val = poll.val;
                if (i == size - 1) {
                    res.add(val);
                }
                if (poll.left != null) queue.offer(poll.left);
                if (poll.right != null) queue.offer(poll.right);
            }
        }
    }

    public static void main(String[] args) {
        P199BinaryTreeRightSideView solver = new P199BinaryTreeRightSideView();

        run(solver, of(1, 2, 3, -1, 5, -1, 4), "官方例 1/2(右5),3(右4) → [1,3,4]");
        run(solver, of(1, -1, 3), "1右3 → [1,3]");
        run(solver, of(), "空树 → []");
        run(solver, of(1), "单节点 → [1]");
        run(solver, chain(1, 2, 3, 4, 5), "左斜链 → [1,2,3,4,5]");
        run(solver, of(1, 2, 3, -1, -1, 6), "1/2,3(左6) → [1,3,6]（左孩子也可见）");
        run(solver, of(1, 2, 3, 4, 5), "右路断头 1/2(4,5),3 → [1,3,5] ★贪心陷阱");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** 按 [val, l, r, ...] 层序构造（-1 = null 子树）。 */
    private static TreeNode of(int... seq) {
        if (seq.length == 0) return null;
        java.util.Queue<TreeNode> q = new java.util.ArrayDeque<>();
        TreeNode root = new TreeNode(seq[0]);
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < seq.length) {
            TreeNode n = q.poll();
            if (i < seq.length && seq[i] != -1) { n.left = new TreeNode(seq[i]); q.offer(n.left); }
            i++;
            if (i < seq.length && seq[i] != -1) { n.right = new TreeNode(seq[i]); q.offer(n.right); }
            i++;
        }
        return root;
    }

    /** 沿左孩子串成斜链（vals[0] 为根）。 */
    private static TreeNode chain(int... vals) {
        TreeNode root = new TreeNode(vals[0]);
        TreeNode cur = root;
        for (int i = 1; i < vals.length; i++) {
            cur.left = new TreeNode(vals[i]);
            cur = cur.left;
        }
        return root;
    }

    /** 独立暴力参考：先算每层深度收集所有节点→按层分组→每组取最后写入的。 */
    private static List<Integer> bruteForce(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        walk(root, 0, out);
        return out;
    }

    /** 前序但【右优先】——每层第一次到达的必是最右（只收首个，不覆盖）。 */
    private static void walk(TreeNode n, int depth, List<Integer> out) {
        if (n == null) return;
        if (depth == out.size()) out.add(n.val);   // 本层首次到达 = 最右
        walk(n.right, depth + 1, out);
        walk(n.left, depth + 1, out);
    }

    private static void run(P199BinaryTreeRightSideView solver, TreeNode root, String label) {
        try {
            List<Integer> want = bruteForce(root);
            List<Integer> got = solver.rightSideView(root);
            boolean ok = got.equals(want);

            System.out.printf("[%s] %-30s | 参考=%s 实得=%s%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-30s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-30s | 异常: %s%n", label, e);
        }
    }
}
