import java.util.Arrays;

/**
 * LeetCode 189: 轮转数组 (Rotate Array)
 * https://leetcode.cn/problems/rotate-array/
 *
 * <p>【难度】Medium
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 原地读写(In-place)
 * <p>【题型】原地读写指针：用「三次反转」在 O(1) 空间内把数组整体右移 k 位
 * <p>【考点】能否想到先 k%=n 去掉多余整圈，再用三段反转规避额外数组；理解「右移 k = 把后 k 个搬到最前」
 * <p>【关联】{@link P31NextPermutation} —— 同为原地读写指针，都靠「区间反转」这一子操作在 O(1) 空间内重排
 *
 * <p>给定数组 nums，将其中元素向右轮转 k 个位置（k 非负，可能大于数组长度）。
 * 要求**原地**修改、尽量用 O(1) 额外空间；方法无返回值。
 * 例：nums=[1,2,3,4,5,6,7], k=3 → [5,6,7,1,2,3,4]。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：每次整体右移一位、做 k 次，O(n·k)；或开一个新数组按 (i+k)%n 落位，O(n) 额外空间。
 *
 * <p>2. 关键观察：
 *    - k 可能大于 n，右移 n 位等于没动，所以先 **k %= n**；
 *    - 「右移 k」本质是把**后 k 个元素整体搬到最前面**，前 n-k 个顺延到后面，相对顺序都不变。
 *
 * <p>3. 核心技巧 —— 三次反转（O(1) 空间）：
 *    a) 反转整个数组：后 k 个被翻到最前（但内部逆序）；
 *    b) 反转前 k 个：恢复这一段的正序；
 *    c) 反转后 n-k 个：恢复那一段的正序。
 *    三步之后即为右移 k 的结果。（另一思路是「环状替换」，但反转法最好写、最不易错。）
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)。
 *    边界：k%n==0（含 k=0、k 为 n 的倍数）时数组不变；单元素恒不变；k 远大于 n 由取模统一处理。
 */
public class P189RotateArray {

    public void rotate(int[] nums, int k) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P189RotateArray p189 = new P189RotateArray();

        // 示例 1
        assertRotate(new int[]{5, 6, 7, 1, 2, 3, 4}, new int[]{1, 2, 3, 4, 5, 6, 7}, 3, p189, "Example 1");

        // 示例 2：含负数
        assertRotate(new int[]{3, 99, -1, -100}, new int[]{-1, -100, 3, 99}, 2, p189, "Example 2 (negatives)");

        // k = 0：不变
        assertRotate(new int[]{1, 2, 3}, new int[]{1, 2, 3}, 0, p189, "k = 0");

        // k = n：整圈，不变
        assertRotate(new int[]{1, 2, 3}, new int[]{1, 2, 3}, 3, p189, "k = n");

        // k > n：需取模，等价于 k=1
        assertRotate(new int[]{3, 1, 2}, new int[]{1, 2, 3}, 4, p189, "k > n (mod)");

        // 单元素：恒不变
        assertRotate(new int[]{7}, new int[]{7}, 100, p189, "Single element");

        // 两元素右移 1
        assertRotate(new int[]{2, 1}, new int[]{1, 2}, 1, p189, "Two elements");

        System.out.println("All tests passed!");
    }

    /** 调用 rotate 后，按 nums 的最终内容与期望比较。 */
    private static void assertRotate(int[] expected, int[] input, int k,
                                     P189RotateArray solver, String testName) {
        solver.rotate(input, k);
        if (!Arrays.equals(expected, input)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, Arrays.toString(expected), Arrays.toString(input)));
        }
        System.out.println("[PASS] " + testName);
    }
}
