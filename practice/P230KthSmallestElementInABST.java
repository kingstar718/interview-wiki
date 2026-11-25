import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * LeetCode 230: 二叉搜索树中第 K 小的元素 (Kth Smallest Element in a BST)
 * https://leetcode.cn/problems/kth-smallest-element-in-a-bst/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二叉树组 3/6——中序复用+BST 性质）
 *
 * <p>■ 题目描述
 *   给**二叉搜索树（BST）**根节点和整数 k，返回树中**第 k 小**的元素值。
 *   （BST：左子树所有节点 &lt; 根 &lt; 右子树所有节点——对**所有**节点成立）
 *
 * <p>■ 输入输出（约束）
 *   输入：TreeNode root（节点数 [1, 10^4]；0 ≤ val ≤ 10^4；k ∈ [1, 树的节点数]）
 *   输出：int
 *   进阶：如果 BST 常被修改（插入/删除）且频繁找第 k 小，怎么优化？
 *
 * <p>■ 示例逐条
 * <pre>
 *   3
 *  / \\
 * 1   4
 *  \\
 *   2
 *   k=1 → 1（最小）
 *   k=2 → 2
 *   5
 *  / \\
 * 3   6
 * / \\
 *2   4
 * /
 *1
 *   k=3 → 3
 * [1] k=1 → 1（单节点）
 * [2,1,3] k=2 → 2
 * 左斜链 1←2←3←4←5（5/4/3/2/1 层序 [5,4,null,3,...]）k=4 → 2
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/美团）
 * <p>【标签】树 · 深度优先 · 二叉搜索树
 * <p>【题型】中序遍历的应用（94 直接复用）：
 * <p>　　BST 中序 = 升序序列（性质）——第 k 小 = 中序的第 k 个被访问节点
 * <p>　　提前终止变体：数到第 k 个就停（不用遍历全树）
 * <p>【考点】BST 性质到"排序数组"的翻译；访问 vs 遍历的区分（k 倒数）；
 * <p>　　　　进阶：节点带左子树大小（树状排名）→ O(h) 查询
 * <p>【关联】P94（中序原型直接复用）；P543（后序家族对照——这题是中序的应用位）；
 * <p>　　　　P215（第 k 大的数组版——快选；树上没有"随机划分"但有"左子树计数"）
 *
 * <p>LCS 签名：kthSmallest(TreeNode root, int k)。
 */
public class P230KthSmallestElementInABST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** 你的实现（迭代栈版主入口：94 复用，弹栈访问时 --k 归 0 直接返回）。 */
    public int kthSmallest(TreeNode root, int k) {
        int v;
        Deque<TreeNode> stack = new LinkedList<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();
            v = cur.val;
            if (--k == 0) {
                return v;
            }
            cur = cur.right;
        }
        return 0;
    }

    private int cnt, val;   // 递归对照版用

    /** 对照入口（双 int 计数器递归版：cnt 正数到达 k——v2 双卫兵结构）。 */
    public int kthSmallestRec(TreeNode root, int k) {
        cnt = 0;
        dfs(root, k);
        return val;
    }

    private void dfs(TreeNode root, int k) {
        if (root == null || cnt == k) {
            return;
        }
        dfs(root.left, k);
        if (cnt == k) {
            return;
        }
        cnt++;
        val = root.val;
        dfs(root.right, k);
    }

    public static void main(String[] args) {
        P230KthSmallestElementInABST solver = new P230KthSmallestElementInABST();

        run(solver, of(3, 1, 4, -1, 2), "官方例1 3/1(右2),4 k=1 → 1", 1);
        run(solver, of(3, 1, 4, -1, 2), "官方例1 k=2 → 2", 2);
        run(solver, of(5, 3, 6, 2, 4, -1, -1, 1), "官方例2 5/3(2(1),4),6 k=3 → 3", 3);
        run(solver, of(1), "单节点 k=1 → 1", 1);
        run(solver, of(2, 1, 3), "2/1,3 k=2 → 2", 2);
        run(solver, chain(5, 4, 3, 2, 1), "左斜链 5/4/3/2/1 k=4 → 2", 4);
        run(solver, of(3, 1, 4, -1, 2), "官方例1 k=4 → 4（最大）", 4);
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

    /** 沿左孩子串成斜链（vals[0] 为根、最深在左）。 */
    private static TreeNode chain(int... vals) {
        TreeNode root = new TreeNode(vals[0]);
        TreeNode cur = root;
        for (int i = 1; i < vals.length; i++) {
            cur.left = new TreeNode(vals[i]);
            cur = cur.left;
        }
        return root;
    }

    /** 独立暴力参考：任意树中序收集 → 排序 → 取第 k-1 个（与 BST 性质无关的双机制）。 */
    private static int bruteForce(TreeNode root, int k) {
        List<Integer> all = new ArrayList<>();
        collect(root, all);
        all.sort(null);
        return all.get(k - 1);
    }

    private static void collect(TreeNode n, List<Integer> out) {
        if (n == null) return;
        collect(n.left, out);
        out.add(n.val);
        collect(n.right, out);
    }

    private static void run(P230KthSmallestElementInABST solver, TreeNode root, String label, int k) {
        try {
            int want = bruteForce(root, k);
            int got = solver.kthSmallest(root, k);
            int gotRec = solver.kthSmallestRec(root, k);
            boolean ok = got == want && gotRec == want;

            System.out.printf("[%s] %-26s | 参考=%d 迭代=%d 递归=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got, gotRec);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-26s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-26s | 异常: %s%n", label, e);
        }
    }
}
