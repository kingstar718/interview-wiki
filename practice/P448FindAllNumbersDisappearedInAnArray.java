import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 448: 找到所有数组中消失的数字 (Find All Numbers Disappeared in an Array)
 * https://leetcode.cn/problems/find-all-numbers-disappeared-in-an-array/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 哈希表(Hash Table) · 原地读写(In-place)
 * <p>【题型】原地读写指针（原地标记）：值域恰为 [1,n] 时，把数组自身当哈希表，用「下标 ↔ 值」互映射做标记
 * <p>【考点】能否利用「值都在 1..n」把 O(n) 额外空间的哈希标记压成 O(1)；几种标记法（加 n / 置负 / 交换归位）的取舍
 * <p>【关联】{@link P26RemoveDuplicatesFromSortedArray} —— 同属「原地读写指针」套路，但本题的手法是「原地标记」而非快慢指针
 *
 * <p>给定长度为 n 的数组 nums，元素取值范围为 [1, n]（可能重复）。
 * 找出 [1, n] 中所有**没有出现**在 nums 里的数字，以列表返回（顺序不限）。
 * 进阶要求：不使用额外空间（返回列表不计），且时间 O(n)。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：开一个大小 n 的布尔数组/HashSet 标记出现过的数，再扫一遍找缺失，需 O(n) 额外空间。
 *
 * <p>2. 关键观察：值域恰好是 [1, n]，和「下标 0..n-1」几乎一一对应。
 *    这意味着**数组自己就能当标记表用**——把「数字 v 出现过」记录在「下标 v-1」这个位置上。
 *
 * <p>3. 核心技巧 —— 原地标记，三种等价手法任选其一：
 *    a) 加 n 法（本文实现）：给「下标 v-1」处 +n 表示 v 出现过；因值可能已被 +n，用 (v-1)%n 还原下标。
 *       第二趟仍 ≤ n 的下标 j，说明 j+1 从未被标记，即缺失。
 *    b) 置负法：把下标 |v|-1 处置为负数表示出现过（读取时取绝对值）；仍为正的下标即缺失。
 *    c) 交换归位法：把 v 交换到它该在的下标 v-1 上，最后「值与下标对不上」的位置即缺失。
 *
 * <p>4. 复杂度：时间 O(n)，空间 O(1)（不含返回列表）。
 *    注意：加 n 法会让值涨到最多 2n，n 很大时留意 int 溢出；置负 / 交换法无此问题。
 *    边界：无缺失返回空列表；全部相同（如 [2,2]）时其余数字都缺失。
 *
 * <p>── 为什么这是「套路」而非巧技 ──
 * 可迁移的不是 +n / 置负 这些具体手法，而是识别信号：
 * **当值域恰好落在 [1,n]（与下标一一对应）时，可把输入数组本身当哈希表，把 O(n) 计数/标记压成 O(1)**。
 * 同一模式贯穿一整类题：41 缺失的第一个正数、442 数组中重复的数据、645 错误的集合、287 寻找重复数。
 * 面试若只要 O(n) 空间，HashSet 版即可；原地标记是「进阶优化」，会一种手法能讲清即可。
 */
public class P448FindAllNumbersDisappearedInAnArray {

    public List<Integer> findDisappearedNumbers(int[] nums) {
        int n = nums.length;
        // Step 3（加 n 标记法）：对每个值 v，给「下标 v-1」处 +n，表示 v 出现过
        for (int num : nums) {
            // 取模还原真实下标：num 可能已被前面的标记 +n 过，(num-1)%n 与原值 v-1 相等
            int x = (num - 1) % n;
            // 只在未标记（≤ n）时 +n，保证每个下标至多标记一次，重复值不会叠加
            if (nums[x] <= n) {
                nums[x] += n;
            }
        }

        // Step 3：仍 ≤ n 的下标 j 从未被标记 → j+1 从未出现 → 缺失
        List<Integer> res = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            if (nums[j] <= n) {
                res.add(j + 1);
            }
        }
        return res;
    }

    public static void main(String[] args) {
        P448FindAllNumbersDisappearedInAnArray p448 = new P448FindAllNumbersDisappearedInAnArray();

        // 示例 1
        assertDisappeared(new int[]{5, 6}, new int[]{4, 3, 2, 7, 8, 2, 3, 1}, p448, "Example 1");

        // 示例 2
        assertDisappeared(new int[]{2}, new int[]{1, 1}, p448, "Example 2");

        // 无缺失：1..n 全都出现
        assertDisappeared(new int[]{}, new int[]{1, 2, 3}, p448, "None missing");

        // 单元素缺失
        assertDisappeared(new int[]{2}, new int[]{1, 1}, p448, "Single duplicate");

        // 全部相同：其余都缺失
        assertDisappeared(new int[]{1, 3, 4}, new int[]{2, 2, 2, 2}, p448, "All same value");

        // 单元素数组，唯一值即全部
        assertDisappeared(new int[]{}, new int[]{1}, p448, "Single element present");

        System.out.println("All tests passed!");
    }

    /**
     * 结果顺序不限，故把期望与实际都排序后按多重集（这里为集合）比较。
     */
    private static void assertDisappeared(int[] expected, int[] input,
                                          P448FindAllNumbersDisappearedInAnArray solver, String testName) {
        List<Integer> actual = solver.findDisappearedNumbers(input);
        List<Integer> actualSorted = new ArrayList<>(actual);
        java.util.Collections.sort(actualSorted);
        List<Integer> expectedSorted = new ArrayList<>();
        for (int v : expected) {
            expectedSorted.add(v);
        }
        java.util.Collections.sort(expectedSorted);
        if (!expectedSorted.equals(actualSorted)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, expectedSorted, actualSorted));
        }
        System.out.println("[PASS] " + testName);
    }
}
