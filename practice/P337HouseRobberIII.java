import java.util.Arrays;

/**
 * LeetCode 337: 打家劫舍 III (House Robber III)
 * https://leetcode.cn/problems/house-robber-iii/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（打家劫舍 3/3——树形态）
 *
 * <p>■ 题目描述
 *   小偷发现了一片新的作案区域——这次的房屋排列在【一棵二叉树】的节点上
 *   （不再是一条街，也不是环）。每个节点代表一栋房屋，节点值是其现金数量。
 * ■ 规则
 *   1) 相邻（直接相连的父子）两栋房屋【不能同时被偷】——报警器联动；
 *   2) 每个节点要么被偷、要么不偷；
 *   3) 求【能偷到的最大总金额】（被偷节点值之和的最大值）。
 *   注意：树上的"邻居"是父子边——**祖父与孙子之间没有直接连线，可以同时偷**。
 *
 * <p>■ 输入约定（LeetCode 树输入——数组层序表示）
 *   输入是【层序数组】：根在下标 0，随后逐层展开；值为 null 表示"该位置没有节点"
 *   （祖先存在而子缺席的空缺——**不是完全二叉树**）。
 *   例：[3,2,3,null,3,null,1] 表示——
 * <pre>
 *        3        ← 根（下标0）
 *       / \
 *      2   3      ← 第二层（2 有右子 3，3 有右子 1）
 *       \   \
 *        3   1
 * </pre>
 * <p>■ 输入输出
 *   输入：TreeNode root（1 ≤ 节点数 ≤ 10^4；0 ≤ val ≤ 10^4）
 *   输出：int（最大偷盗金额；约束保证至少能偷根以上某非空子集 → 0 元节点输出合法 ≥0）
 *
 * <p>■ 示例（本文件 8 例自表；官方两例如下）
 *   官方例 1: [3,2,3,null,3,null,1] → 7（偷 root 3 + 右孙 3 + 右孙 1 —— 三点都不相邻）
 *   官方例 2: [3,4,5,1,3,null,1]    → 9（偷 4 + 3 + 5，见 harness）
 *
 * <p>═══════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★）
 * <p>【标签】动态规划(DP) · 树形 DP · 二叉树（后序遍历）
 * <p>【题型】树上不相邻节点组最大收益；【返回长度为 2 的数组】（方案原话）——“偷/不偷本节点”两态分离
 * <p>【考点】后序递归（先孩子后自己——二叉树章递归三部曲续用）；
 * <p>　　　“偷我 = val + 孩子们不偷和”、“不偷我 = Σ max(孩子偷, 孩子不偷)”；
 * <p>　　　与 198/213 的“延续+入伙”同型号，显式化为 pair 的正式登场
 * <p>【关联】P198（线性版）；P213（环形版）；P121（同样“天然 1D 自变量+二态”——股票状态机的先声）
 *
 * <p>LCS 签名：rob(TreeNode root)。相邻父子不能同时偷，返回树的最大偷盗金额。
 */
public class P337HouseRobberIII {

    /** 你的实现（树形 DP：后序 dfs 返回 [skip, take] 二元组——与 wiki 流派同序）。 */
    public int rob(TreeNode root) {
        return Math.max(dfs(root)[0], dfs(root)[1]);
    }

    private int[] dfs(TreeNode root) {
        if (root == null) {
            return new int[]{0, 0};
        }
        int[] left = dfs(root.left);
        int[] right = dfs(root.right);
        int skip = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        int take = root.val + left[0] + right[0];
        return new int[]{skip, take};
    }

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static void main(String[] args) {
        P337HouseRobberIII solver = new P337HouseRobberIII();

        run(solver, new Integer[]{3, 2, 3, null, 3, null, 1}, "官方例1 → 7（root+右孙3+右孙1）");
        run(solver, new Integer[]{3, 4, 5, 1, 3, null, 1}, "官方例2 → 9（4+3+5? 深层）");
        run(solver, new Integer[]{2}, "单节点 → 2 ★ 地基");
        run(solver, new Integer[]{1, 2, 3}, "子均小 → 3（max(根1, 2+3)=5? 由参考定）");
        run(solver, new Integer[]{4, 1, null, 2, null, null, null, 3}, "左深链 → 参考（4+2+3 中隔的错）");
        run(solver, new Integer[]{5, 5, 5}, "三节点两子对称 → 参考定");
        run(solver, new Integer[]{1, 100, 2, null, 3, null, 4}, "子重孙轻 → 参考定（父随文的经典）");
        run(solver, new Integer[]{0, 0, 0}, "全零 → 0");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    /** int[] vals 里 null 用 Integer 表示——层序数组构造树（含空缺、非完全）。 */
    private static TreeNode build(Integer[] vals) {
        if (vals == null || vals.length == 0 || vals[0] == null) return null;
        java.util.Queue<TreeNode> q = new java.util.LinkedList<>();
        TreeNode root = new TreeNode(vals[0]);
        q.offer(root);
        int i = 1;
        while (!q.isEmpty() && i < vals.length) {
            TreeNode cur = q.poll();
            if (i < vals.length) {
                Integer v = vals[i++];
                if (v != null) { cur.left = new TreeNode(v); q.offer(cur.left); }
            }
            if (i < vals.length) {
                Integer v = vals[i++];
                if (v != null) { cur.right = new TreeNode(v); q.offer(cur.right); }
            }
        }
        return root;
    }

    /** 独立暴力参考：节点级位掩码全枚举（子集枚举 + 父子边禁令检查）——
     *  与用户的后序取对 DP 机制无关，n≤12 案例以内可信。 */
    private static int bruteForce(TreeNode root) {
        if (root == null) return 0;
        // 序列化节点：BFS 先仅收集节点列表，再用 idxOf 回填父子下标（避免入队时带参搞混 parent）
        java.util.List<TreeNode> nodes = new java.util.ArrayList<>();
        java.util.Map<TreeNode, Integer> idxOf = new java.util.HashMap<>();
        java.util.List<TreeNode> parents = new java.util.ArrayList<>();   // 与 nodes 平行：每个节点的父
        nodes.add(root); idxOf.put(root, 0); parents.add(null);
        java.util.Queue<TreeNode> q = new java.util.LinkedList<>();
        q.offer(root);
        while (!q.isEmpty()) {
            TreeNode cur = q.poll();
            for (TreeNode child : new TreeNode[]{cur.left, cur.right}) {
                if (child != null) {
                    nodes.add(child);
                    idxOf.put(child, nodes.size() - 1);
                    parents.add(cur);
                    q.offer(child);
                }
            }
        }
        java.util.List<Integer> parentIdx = new java.util.ArrayList<>();
        for (TreeNode nd : nodes) {
            Integer pi = parents.get(idxOf.get(nd)) == null ? -1 : idxOf.get(parents.get(idxOf.get(nd)));
            parentIdx.add(pi);
        }
        int n = nodes.size();
        int best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            boolean legal = true;
            int sum = 0;
            for (int i = 0; i < n && legal; i++) {
                if ((mask & (1 << i)) == 0) continue;
                sum += nodes.get(i).val;
                int ps = parentIdx.get(i);
                if (ps >= 0 && (mask & (1 << ps)) != 0) {
                    legal = false;   // 父子同取 → 违禁
                }
            }
            if (legal) best = Math.max(best, sum);
        }
        return best;
    }

    private static void run(P337HouseRobberIII solver, Integer[] vals, String label) {
        try {
            TreeNode root = build(vals);
            int got = solver.rob(root);
            int want = bruteForce(root);
            boolean ok = got == want;

            System.out.printf("[%s] %-22s 数组=%-34s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, Arrays.toString(vals), want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-22s 数组=%-34s | TODO 未实现%n", label, Arrays.toString(vals));
        } catch (Exception e) {
            System.out.printf("[FAIL] %-22s 数组=%-34s | 异常: %s%n", label, Arrays.toString(vals), e);
        }
    }
}
