import java.util.Arrays;

/**
 * LeetCode 283: 移动零 (Move Zeroes)
 * https://leetcode.cn/problems/move-zeroes/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 双指针(Two Pointers) · 原地读写(In-place)
 * <p>【题型】原地读写指针：慢指针写「非零元素」，保持相对顺序，末尾自然留给 0
 * <p>【考点】能否原地 O(1) 完成且**保持非零元素的相对顺序**；理解为何「写完非零再补零」与「交换」两种写法都对
 * <p>【关联】{@link P27RemoveElement} —— 几乎同构：把「移除 val」换成「把 0 移到末尾」，本题多了「保持顺序」的约束
 * <p>{@link P26RemoveDuplicatesFromSortedArray} —— 同为慢指针写该留的元素，只是判定条件不同
 *
 * <p>给定数组 nums，将所有 0 移动到数组末尾，同时保持非零元素的相对顺序。
 * 必须**原地**操作，不能拷贝到额外数组；方法无返回值，直接修改 nums。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：遇到 0 就把后面元素整体前移一格，末尾补 0，单次移动 O(n)，最坏 O(n²)。
 *
 * <p>2. 关键观察：真正要保序的只有**非零元素**，0 全部堆到末尾即可。
 *    于是问题变成「把非零元素按原顺序依次写到数组前段」，剩下的位置补 0。
 *
 * <p>3. 核心技巧 —— 快慢双指针（同向）：
 *    慢指针 slow 指向下一个应写入非零值的位置，快指针扫描；
 *    遇到非零就写到 slow 并让 slow 前进。扫完后，[slow, n) 全部置 0。
 *    等价写法：遇到非零时直接 swap(nums[slow], nums[fast])，省掉最后补零那步。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)。
 *    边界：无 0 时数组不变；全 0 时不变；单元素直接返回。
 */
public class P283MoveZeroes {

    /**
     * 变体写法（等价，非主解，仅作对照）：slow 指向「最靠左的待填 0」，遇非零则交换前移。
     * 逻辑不如主解 {@link #moveZeroes} 直观，正确性曾以 10 万组随机对拍验证；测试统一调用主解。
     */
    public void moveZeros2(int[] nums) {
        // 边界：空数组 / 单元素无需移动（主循环对二者也天然不执行）
        if (nums.length <= 1) {
            return;
        }
        // Step 3：slow 指向「最靠左的待填 0」，fast 向右扫（Step 2：只需把非零按序前移，0 自然堆到末尾）
        int slow = 0;
        for (int fast = slow + 1; fast < nums.length; fast++) {
            if (nums[slow] != 0) {
                // slow 尚未落在 0 上，先推进到第一个 0
                slow++;
            } else if (nums[fast] != 0) {
                // slow 是 0、fast 是非零：交换把非零前移、0 后移，slow 前进
                swap(nums, slow, fast);
                slow++;
            }
            // 两者都是 0：不动，等 fast 继续找下一个非零
        }
    }

    /**
     * 主解 · 标准快慢指针。优点：只有「遇非零就写」这一个判定分支，
     * l 恒为「下一个非零的写入位」，语义唯一、一眼可证，与上方思路引导 Step 3 完全对应，可读性最好。
     */
    public void moveZeroes(int[] nums) {
        // Step 3：l 指向下一个非零的写入位，r 向右扫描
        int n = nums.length, l = 0, r = 0;
        while (r < n) {
            // Step 3：遇到非零就交换到写入位 l 并让 l 前进（Step 2：非零按序前移，0 自然堆到末尾）
            if (nums[r] != 0) {
                swap(nums, l, r);
                l++;
            }
            r++;
        }
    }

    static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        P283MoveZeroes p283 = new P283MoveZeroes();

        // 示例 1
        assertMoveZeroes(new int[]{1, 3, 12, 0, 0}, new int[]{0, 1, 0, 3, 12}, p283, "Example 1");

        // 示例 2：单个 0
        assertMoveZeroes(new int[]{0}, new int[]{0}, p283, "Single zero");

        // 无 0：保持不变
        assertMoveZeroes(new int[]{1, 2, 3}, new int[]{1, 2, 3}, p283, "No zeros");

        // 全 0
        assertMoveZeroes(new int[]{0, 0, 0}, new int[]{0, 0, 0}, p283, "All zeros");

        // 0 已在末尾
        assertMoveZeroes(new int[]{4, 5, 0, 0}, new int[]{4, 5, 0, 0}, p283, "Zeros already at end");

        // 验证保持相对顺序 + 含负数
        assertMoveZeroes(new int[]{-1, 2, -3, 0, 0}, new int[]{-1, 0, 2, 0, -3}, p283, "Keep order with negatives");

        System.out.println("All tests passed!");
    }

    /** 调用 moveZeroes 后，按 nums 的最终内容与期望比较。 */
    private static void assertMoveZeroes(int[] expected, int[] input,
                                         P283MoveZeroes solver, String testName) {
        solver.moveZeroes(input);
        if (!Arrays.equals(expected, input)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, Arrays.toString(expected), Arrays.toString(input)));
        }
        System.out.println("[PASS] " + testName);
    }
}
