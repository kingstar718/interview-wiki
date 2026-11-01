import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * LeetCode 105: 从前序与中序遍历序列构造二叉树
 * (Construct Binary Tree from Preorder and Inorder Traversal)
 * https://leetcode.cn/problems/construct-binary-tree-from-preorder-and-inorder-traversal/
 *
 * <p>【难度】Medium
 * <p>【标签】二叉树(Binary Tree) · 分治 · 递归 · 哈希表
 * <p>【题型】双序列分治建树：前序定根，中序定左右子树边界
 * <p>【考点】切分区间的边界计算（本章最易错）；用 HashMap 把根定位降到 O(1)
 * <p>【关联】P108 有序数组转 BST（单序列分治）；P106 中序+后序（镜像）
 *
 * <p>给定两个整数数组 preorder 和 inorder，其中 preorder 是二叉树的【前序】遍历，
 * inorder 是同一棵树的【中序】遍历，请构造二叉树并返回其根节点。
 *
 * <p>约束：1 &lt;= preorder.length &lt;= 3000；
 * inorder.length == preorder.length；
 * -3000 &lt;= preorder[i], inorder[i] &lt;= 3000；
 * preorder 和 inorder 均【无重复】元素；
 * preorder 和 inorder 均保证是同一棵二叉树的前序/中序遍历结果。
 *
 * <p>示例1：preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
 * <pre>
 *        3
 *       / \
 *      9  20
 *         / \
 *        15  7
 * </pre>
 * <p>示例2：preorder = [-1], inorder = [-1] → 单节点
 */
public class P105ConstructBinaryTreeFromPreorderAndInorderTraversal {

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

    /** 你的第 1 版：copyOfRange 传子数组（AC 203/203，O(n²) 最坏）。 */
    public TreeNode buildTreeByCopyRange(int[] preorder, int[] inorder) {
        if (preorder.length == 0 || inorder.length == 0) {
            return null;
        }
        int pix = 0;
        int rootVal = preorder[0];
        TreeNode root = new TreeNode(rootVal);
        for (int i : inorder) {
            if (i == rootVal) {
                break;
            }
            pix++;
        }
        root.left = buildTreeByCopyRange(java.util.Arrays.copyOfRange(preorder, 1, pix + 1),
                java.util.Arrays.copyOfRange(inorder, 0, pix + 1));
        root.right = buildTreeByCopyRange(java.util.Arrays.copyOfRange(preorder, pix + 1, preorder.length),
                java.util.Arrays.copyOfRange(inorder, pix + 1, inorder.length));
        return root;
    }


    /**
     * 你的第 2 版：下标 + HashMap（AC 203/203，O(n)）。
     *
     * <p>【注释说明】左子树前序终点写成 `preL + leftSize + 1`，比自洽值多 1：
     * <p>　左·前序窗口长度 = leftSize + 1，左·中序窗口长度 = leftSize（不相等）。
     * <p>　多出的那个位置在窗口末尾，递归永远从头部读根，所以读不到 → 不影响结果。
     * <p>　但这行正确性依赖 `inL > inR` 守卫：若只留 `preL > preR`，会 ArrayIndexOutOfBounds。
     * <p>　习惯写法：左终点用 `preL + leftSize`，则 `preL > preR` 单独即可判空。
     */
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> pos = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            pos.put(inorder[i], i);
        }
        return buildTreeLR(preorder, inorder, 0, preorder.length - 1, 0, inorder.length - 1, pos);
    }

    /** 递归体：preL/preR 前序闭区间，inL/inR 中序闭区间，pos 为「值 → 中序下标」。 */
    public TreeNode buildTreeLR(int[] pre, int[] in,
                                int preL, int preR,
                                int inL, int inR,
                                Map<Integer, Integer> pos) {
        if (preL > preR || inL > inR) {
            return null;
        }
        int rootVal = pre[preL];              // 前序第一个 = 根
        int pix = pos.get(rootVal);           // 根在 inorder 的绝对下标
        int leftSize = pix - inL;             // ★ 左子树节点数（绝对下标 → 数量）

        TreeNode root = new TreeNode(rootVal);
        root.left = buildTreeLR(pre, in, preL + 1, preL + leftSize + 1, inL, pix - 1, pos);
        root.right = buildTreeLR(pre, in, preL + leftSize + 1, preR, pix + 1, inR, pos);
        return root;
    }

    // ==================== 参考实现（带注释，供对照） ====================

    /**
     * 四参数闭区间版。
     *
     * <p>【核心不变量】preorder 窗口和 inorder 窗口装的是同一批节点 → 长度必然相等
     * <p>　　　　　　　preEnd - preStart == inEnd - inStart
     *
     * <p>【四个参数】
     * <p>　preStart, preEnd —— 当前子树在 preorder 里的闭区间 [preStart, preEnd]
     * <p>　inStart,  inEnd  —— 当前子树在 inorder  里的闭区间 [inStart,  inEnd]
     */
    public TreeNode buildByIndex(int[] pre, int preStart, int preEnd,
                                 int[] in, int inStart, int inEnd,
                                 Map<Integer, Integer> inToIndex) {
        // 出口：闭区间的"空"就是 start > end。
        // 只判 preorder 侧就够 —— 两窗口等长，前序空则中序必空。
        if (preStart > preEnd) {
            return null;
        }

        int rootVal = pre[preStart];                  // 前序第一个 = 根
        int rootInIndex = inToIndex.get(rootVal);     // 根在 inorder 的【绝对下标】

        // ★ 全题最关键的一行：把"绝对下标"翻译成"左子树节点数"
        //   根左边（在当前 inorder 窗口内）全是左子树节点。
        //   减 inStart 是因为窗口可能不从数组开头开始，窗口外是祖先已消费的节点。
        int leftSize = rootInIndex - inStart;

        TreeNode root = new TreeNode(rootVal);

        // 左子树：前序跳过根（preStart+1 起），长度 leftSize → 终点 preStart+leftSize
        //         中序取根左边的部分 [inStart, rootInIndex-1]
        root.left = buildByIndex(pre, preStart + 1, preStart + leftSize,
                in, inStart, rootInIndex - 1, inToIndex);

        // 右子树：前序跳过 根 + 整块左子树（preStart+leftSize+1 起），终点继承 preEnd
        //         中序取根右边的部分 [rootInIndex+1, inEnd]
        root.right = buildByIndex(pre, preStart + leftSize + 1, preEnd,
                in, rootInIndex + 1, inEnd, inToIndex);

        return root;
    }

    /** 四参数版的入口。 */
    public TreeNode buildTreeByIndex(int[] preorder, int[] inorder) {
        Map<Integer, Integer> inToIndex = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inToIndex.put(inorder[i], i);
        }
        return buildByIndex(preorder, 0, preorder.length - 1,
                inorder, 0, inorder.length - 1, inToIndex);
    }

    /**
     * 三参数长度版（LeetCode 官方 Wiki 写法）。
     *
     * <p>【核心洞察】前序窗口与中序窗口长度相同 → 只存一个长度 n，终点由 起点+n-1 隐含
     *
     * <p>【三个参数】
     * <p>　i —— 当前子树在 preorder 里的起点（preorder[i .. i+n-1]）
     * <p>　j —— 当前子树在 inorder  里的起点（inorder [j .. j+n-1]）
     * <p>　n —— 当前子树的节点个数，两个数组共用这一个数
     */
    public TreeNode buildByLen(int i, int j, int n) {
        // 出口：没有节点就不建（比 preStart > preEnd 更直白）
        if (n <= 0) {
            return null;
        }

        int v = preorderForLen[i];       // 前序起点 = 根
        int k = inToIndexForLen.get(v);  // 根在 inorder 的绝对下标
        int leftLen = k - j;             // 左子树节点数 = 根位置 - 窗口起点

        // 左子树：前序跳过根(i+1)，中序起点不变(j)，个数 = leftLen
        TreeNode l = buildByLen(i + 1, j, leftLen);

        // 右子树：前序跳过 根+左块(i+1+leftLen)，中序跳过根(k+1)，
        //         个数 = 总数 - 根 - 左块
        TreeNode r = buildByLen(i + 1 + leftLen, k + 1, n - 1 - leftLen);

        return new TreeNode(v, l, r);
    }

    private int[] preorderForLen;
    private Map<Integer, Integer> inToIndexForLen;

    /** 三参数版的入口。 */
    public TreeNode buildTreeByLen(int[] preorder, int[] inorder) {
        preorderForLen = preorder;
        inToIndexForLen = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inToIndexForLen.put(inorder[i], i);
        }
        return buildByLen(0, 0, preorder.length);
    }

    public static void main(String[] args) {
        P105ConstructBinaryTreeFromPreorderAndInorderTraversal solver =
                new P105ConstructBinaryTreeFromPreorderAndInorderTraversal();

        run(solver, new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7}, "示例1");
        run(solver, new int[]{-1}, new int[]{-1}, "示例2 单节点");
        run(solver, new int[]{1, 2}, new int[]{1, 2}, "左单链");
        run(solver, new int[]{1, 2}, new int[]{2, 1}, "右单链");
        run(solver, new int[]{1, 2, 3}, new int[]{2, 1, 3}, "小满树");
        run(solver, new int[]{1, 2, 4, 5, 3, 6, 7}, new int[]{4, 2, 5, 1, 6, 3, 7}, "满二叉树 7 节点");
        run(solver, new int[]{3, 1, 2, 4}, new int[]{1, 3, 2, 4}, "左偏树");
        run(solver, new int[]{1, 2, 3}, new int[]{2, 3, 1}, "反例：preL≠inL 时崩");
        run(solver, new int[]{1, 2, 3}, new int[]{3, 2, 1}, "反例：左深链");
        run(solver, new int[]{5, 3, 1, 4, 8}, new int[]{1, 3, 4, 5, 8}, "反例：左右都不对称");
        run(solver, new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5}, "纯右链 5 节点");
        run(solver, new int[]{5, 4, 3, 2, 1}, new int[]{1, 2, 3, 4, 5}, "纯左链 5 节点");
    }

    private static void run(P105ConstructBinaryTreeFromPreorderAndInorderTraversal solver,
                            int[] preorder, int[] inorder, String name) {
        String label = name + " pre=" + java.util.Arrays.toString(preorder)
                + " in=" + java.util.Arrays.toString(inorder);
        try {
            TreeNode root = solver.buildTree(preorder, inorder);

            // 校验1：重建出的树，前序遍历必须等于 preorder
            List<Integer> pre = new ArrayList<>();
            collectPre(root, pre);
            boolean preOk = pre.size() == preorder.length;
            for (int i = 0; preOk && i < preorder.length; i++) {
                if (pre.get(i) != preorder[i]) preOk = false;
            }

            // 校验2：中序遍历必须等于 inorder
            List<Integer> in = new ArrayList<>();
            collectIn(root, in);
            boolean inOk = in.size() == inorder.length;
            for (int i = 0; inOk && i < inorder.length; i++) {
                if (in.get(i) != inorder[i]) inOk = false;
            }

            // 三版一致性检查（仅打印，不影响主判定）
            String s0 = serialize(root);
            String s1 = serialize(solver.buildTreeByIndex(preorder, inorder));
            String s2 = serialize(solver.buildTreeByLen(preorder, inorder));
            String s3 = serialize(solver.buildTreeByCopyRange(preorder, inorder));
            if (!s0.equals(s1) || !s0.equals(s2) || !s0.equals(s3)) {
                System.out.println("[MISMATCH] " + label + "\n        yours=" + s0
                        + "\n        index=" + s1 + "\n         len=" + s2 + "\n        copy=" + s3);
            }

            if (!preOk) {
                System.out.println("[FAIL] " + label + " | 前序不符：" + pre);
            } else if (!inOk) {
                System.out.println("[FAIL] " + label + " | 中序不符：" + in);
            } else {
                System.out.println("[PASS] " + label + " | 树=" + serialize(root));
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }

    private static void collectPre(TreeNode n, List<Integer> out) {
        if (n == null) return;
        out.add(n.val);
        collectPre(n.left, out);
        collectPre(n.right, out);
    }

    private static void collectIn(TreeNode n, List<Integer> out) {
        if (n == null) return;
        collectIn(n.left, out);
        out.add(n.val);
        collectIn(n.right, out);
    }

    private static String serialize(TreeNode root) {
        if (root == null) return "[]";
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
        return "[" + String.join(" ", out.subList(0, end)) + "]";
    }
}
