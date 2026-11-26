import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 287: 寻找重复数 (Find the Duplicate Number)
 * https://leetcode.cn/problems/find-the-duplicate-number/
 *
 * <p>═══════════════════════════════════════════════════════════════════
 * <p>【完整题面·自足】（二阶段 Hot100 验收·最后一题）
 *
 * <p>■ 题目描述
 *   长度 n+1 的数组，元素范围 [1, n]（**含 n 个可能取值但 n+1 个位置**
 *   → 至少一个重复）。找出那个重复的数字。
 *   约束（不能用的手段）：
 *   - 不能改数组（不许排序/原地标记）
 *   - 空间 O(1)（不许哈希表/Set）
 *   - 时间 < O(n²)（不许暴力双层枚举）
 *
 * <p>■ 输入输出（约束）
 *   输入：int[] nums（1 ≤ n+1 ≤ 10^5；nums[i] ∈ [1, n]；
 *        只有一个重复数字，但可能重复多次）
 *   输出：int
 *
 * <p>■ 示例逐条
 * <pre>
 * [1,3,4,2,2] → 2
 * [3,1,3,4,2] → 3
 * [3,3,3,3,3] → 3（重复多次—— Floyd 照样收敛）
 * [1,1] → 1
 * [1,1,2] → 1
 * </pre>
 *
 * <p>═════════════════════════════════════════════════════════════════════
 *
 * <p>【难度】Medium（页频 ★★★★·字节/微软/亚马逊）
 * <p>【标签】数组 · 双指针 · 二分查找 · 位运算
 * <p>【题型】数组看成隐式链表 + Floyd 判环（141/142 的第三次出场）：
 * <p>　　下标 i → nums[i] 当"next 指针"（值域 [1,n] 保证永远有下标可跳）
 * <p>　　重复数字 = 两个入边 = 环入口（142 同款：快慢相遇后 ptr 从头同速走）
 * <p>【考点】"值当指针"的翻译；为什么入口=重复值（142 的 s≡b mod C 论证复用）；
 * <p>　　　　全等数组（3,3,3,3,3）的收敛验证
 * <p>【关联】P141/142（判环/找入口——直接同构）；P448（找消失的数——对偶题）
 *
 * <p>LCS 签名：findDuplicate(int[] nums)。
 */
public class P287FindTheDuplicateNumber {

    /** 你的实现（隐式链表 Floyd：next(i)=nums[i]；141 判相遇+142 找入口原样复用）。 */
    public int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[nums[0]];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[nums[fast]];
        }
        int ptr = 0;
        while (ptr != slow) {
            ptr = nums[ptr];
            slow = nums[slow];
        }
        return ptr;
    }

    public static void main(String[] args) {
        P287FindTheDuplicateNumber solver = new P287FindTheDuplicateNumber();

        run(solver, a(1, 3, 4, 2, 2), "官方例1 → 2");
        run(solver, a(3, 1, 3, 4, 2), "官方例2 → 3");
        run(solver, a(3, 3, 3, 3, 3), "全等 → 3");
        run(solver, a(1, 1), "最小数组 → 1");
        run(solver, a(1, 1, 2), "重复多次 → 1");
        run(solver, a(2, 2, 2, 2, 2), "全等 2 → 2");
    }

    // ---------- 测试辅助（提交 LeetCode 时删除） ----------

    private static int[] a(int... v) { return v; }

    /** 独立暴力参考：Set 查重（O(n) 空间，机制独立）。 */
    private static int bruteForce(int[] nums) {
        List<Integer> seen = new ArrayList<>();
        for (int x : nums) {
            if (seen.contains(x)) return x;
            seen.add(x);
        }
        return -1;
    }

    private static void run(P287FindTheDuplicateNumber solver, int[] nums, String label) {
        try {
            int want = bruteForce(nums);
            int got = solver.findDuplicate(nums);
            boolean ok = got == want;

            System.out.printf("[%s] %-16s | 参考=%d 实得=%d%n",
                    ok ? "PASS" : "FAIL", label, want, got);
        } catch (UnsupportedOperationException e) {
            System.out.printf("[FAIL] %-16s | TODO 未实现%n", label);
        } catch (Exception e) {
            System.out.printf("[FAIL] %-16s | 异常: %s%n", label, e);
        }
    }
}
