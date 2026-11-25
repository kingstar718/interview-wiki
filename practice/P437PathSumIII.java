import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LeetCode 437: 路径总和 III (Path Sum III)
 * https://leetcode.cn/problems/path-sum-iii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·二叉树组 6/6 压轴——树上版 560）
 *
 * <p>■ 题目描述
 *   给二叉树根节点和整数 targetSum，返回**路径数目**——路径满足：
 *   - 只能从父节点走向子节点（向下，不折返）
 *   - 路径上节点值之和 = targetSum
 *   - 路径**不必**从根开始、**不必**在叶子结束，但必须方向一致（父子链）
 *
 * <p>■ 输入输出（约束）
 *   输入：TreeNode root（节点数 [0, 1000]；-10^9 ≤ val ≤ 10^9；
 *        -1000 ≤ targetSum ≤ 1000——注意：和可能溢出 int，Java 用 long）
 *   输出：int（路径条数）
 *
 * <p>■ 示例逐条
 * <pre>
 *        10
 *       /  \\
 *      5   -3
 *     / \\    \\
 *    3   2   11
 *   / \\   \\
 *  3  -2   1
 *  targetSum=8 → 3 条：
 *    5→3（=8）、5→2→1（=8）、-3→11（=8）
 * [5,4,8,11,null,13,4,7,2,null,null,5,1] targetSum=22 → 3 条
 * [1] targetSum=1 → 1
 * [1,2] targetSum=3 → 1（1→2）
 * [1,null,2,null,3,null,4] targetSum=3 → 2（1→2 和 1→2→3——重叠也各算一条！）
 * [1,-2,-3] targetSum=-1 → 2（1→-2 和 -3）
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/腾讯/亚马逊）
 * <p>【标签】树 · 深度优先 · 前缀和 · 二叉树
 * <p>【题型】前缀和 + 哈希回退（560 和为 K 的子数组·树上版）：
 * <p>　　根到当前节点的前缀和 prefix；路径(i,j] 和 = prefix[j]-prefix[i]
 * <p>　　树上"子数组"= 从某祖先到当前节点的父子链——回退=查哈希里 prefix-target 的次数
 * <p>【考点】DFS 往下走时【携带】前缀和 + 回溯时【撤销】计数（进一退一）；
 * <p>　　　　先查后记（防自己减自己 target=0）；long 防溢出
 * <p>【关联】P560（数组版直接同构）；P112/113（路径总和 I/II——必须从根到叶，本题放开）；
 * <p>　　　　P543（后序+全局变量家族——本题答案也是沿途收集）
 *
 * <p>LCS 签名：pathSum(TreeNode root, long targetSum)。
 */
public class P437PathSumIII {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) { this.val = val; this.left = left; this.right = right; }
    }

    /** 你的实现（灵神同款：前缀和+哈希，先查后记，回溯撤销——树上版 560）。 */
    int count;
    Map<Long, Long> cnt = new HashMap<>();
    public int pathSum(TreeNode root, long targetSum) {
        count = 0;
        cnt.clear();
        cnt.put(0L, 1L);
        dfs(root, 0L, targetSum);
        return count;
    }

    private void dfs(TreeNode root, long cur, long target) {
        if (root == null) return;
        cur += root.val; // 根到n的前缀和
        count += cnt.getOrDefault(cur - target, 0L);
        cnt.merge(cur, 1L, Long::sum);
        dfs(root.left, cur, target);
        dfs(root.right, cur, target);
        cnt.merge(cur, -1L, Long::sum);
    }

    public static void main(String[] args) {
        P437PathSumIII solver = new P437PathSumIII();

        run(solver, of(10, 5, -3, 3, 2, -1, 11, 3, -2, -1, 1), "官方例 target=8 → 3", 8);
        run(solver, of(1), "单节点 target=1 → 1", 1);
        run(solver, of(1, 2), "1左2 target=3 → 1", 3);
        run(solver, chain(1, 2, 3, 4), "右斜链 1→2→3→4 target=6 → 1（只有 1→2→3）", 6);
        run(solver, of(1, -2, -3), "1/-2,-3 target=-1 → 1（只有 1→-2）", -1);
        run(solver, of(0, 0, 0), "0/0,0 target=0 → 5（单节点0也算路径）", 0);
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

    /** 沿右孩子串成斜链。 */
    private static TreeNode chain(int... vals) {
        TreeNode root = new TreeNode(vals[0]);
        TreeNode cur = root;
        for (int i = 1; i < vals.length; i++) {
            cur.right = new TreeNode(vals[i]);
            cur = cur.right;
        }
        return root;
    }

    /** 独立暴力参考：枚举每个起点（树上 DFS 到所有后代，O(n²)）。 */
    private static int bruteForce(TreeNode root, long target) {
        if (root == null) return 0;
        return fromNode(root, target, 0) + bruteForce(root.left, target) + bruteForce(root.right, target);
    }

    /** 从 n 出发（n 必须是路径起点）向下延伸，值和恰为 target 的路径数。 */
    private static int fromNode(TreeNode n, long target, long cur) {
        if (n == null) return 0;
        cur += n.val;
        int cnt = cur == target ? 1 : 0;
        return cnt + fromNode(n.left, target, cur) + fromNode(n.right, target, cur);
    }

    private static void run(P437PathSumIII solver, TreeNode root, String label, long target) {
        try {
            int want = bruteForce(root, target);
            int got = solver.pathSum(root, target);
            boolean ok = got == want;

            System.out.printf("[%s] %-34s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-34s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-34s | 异常: %s%n", label, e);
        }
    }
}
