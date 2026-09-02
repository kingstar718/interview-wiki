/**
 * LeetCode 303: 区域和检索 - 数组不可变 (Range Sum Query - Immutable)
 * https://leetcode.cn/problems/range-sum-query-immutable/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 前缀和(Prefix Sum) · 设计(Design)
 * <p>【题型】前缀和：一次预处理，换来「任意区间和」的 O(1) 查询
 * <p>【考点】能否想到用前缀和把「多次区间求和」从每次 O(n) 降到 O(1)；prefix 取长度 n+1 以统一边界
 * <p>【关联】{@link P560SubarraySumEqualsK} —— 同为前缀和：那题用「前缀和之差」找区间，本题直接返回区间和
 *
 * <p>设计一个类，用整数数组 nums 初始化后，多次查询下标区间 [left, right] 的元素之和（含两端）。
 * 数组初始化后不再改变。要求 sumRange 尽量做到 O(1)。
 *
 * <p>── 思路引导 ──
 * <p>1. 朴素想法：每次 sumRange 都从 left 累加到 right，单次 O(n)，多次查询会退化成 O(q·n)。
 *
 * <p>2. 关键观察：区间和 = 两个「前缀和」之差。
 *    设 prefix[i] = nums[0]+...+nums[i-1]（前 i 个之和），则 sum(left,right) = prefix[right+1] - prefix[left]。
 *
 * <p>3. 核心技巧 —— 构造时预处理前缀和：
 *    构造函数里一趟求出 prefix（长度取 n+1，prefix[0]=0，避免 left=0 的边界特判），
 *    之后每次查询只做一次减法，O(1)。这是「预处理换查询」的最典型范例。
 *
 * <p>4. 复杂度：构造 O(n)，单次查询 O(1)，空间 O(n)。
 *    边界：left==right 查单个元素；left==0 靠 prefix[0]=0 自然成立。
 */
public class P303RangeSumQueryImmutable {

    private final int[] nums;

    public P303RangeSumQueryImmutable(int[] nums) {
        // 骨架仅存原数组；实现时建议在此预处理「前缀和」数组，使 sumRange 达到 O(1)
        this.nums = nums;
    }

    public int sumRange(int left, int right) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        // 用一个实例做多次区间查询
        P303RangeSumQueryImmutable arr = new P303RangeSumQueryImmutable(new int[]{-2, 0, 3, -5, 2, -1});
        assertEqual(1, arr.sumRange(0, 2), "sumRange(0,2)");    // -2+0+3
        assertEqual(-1, arr.sumRange(2, 5), "sumRange(2,5)");   // 3-5+2-1
        assertEqual(-3, arr.sumRange(0, 5), "sumRange(0,5)");   // 整段
        assertEqual(-2, arr.sumRange(0, 0), "sumRange(0,0)");   // 首元素
        assertEqual(-1, arr.sumRange(5, 5), "sumRange(5,5)");   // 末元素

        P303RangeSumQueryImmutable one = new P303RangeSumQueryImmutable(new int[]{10});
        assertEqual(10, one.sumRange(0, 0), "single-element array");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new AssertionError(String.format("[FAIL] %s: expected %d, but got %d",
                testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
