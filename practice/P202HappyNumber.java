/**
 * LeetCode 202: 快乐数 (Happy Number)
 * https://leetcode.cn/problems/happy-number/
 *
 * <p>【难度】Easy
 * <p>【标签】哈希表(Hash Table) · 双指针(Two Pointers) · 数学(Math) · 判环
 * <p>【题型】把「各位平方和」看作链表的 next 指针，问题就变成「链表中是否有环」——142 的迁移
 * <p>【考点】能否识别出「检测循环」本质；快慢指针 O(1) 空间 vs HashSet O(log n) 空间
 * <p>【关联】P142 环形链表II（判环模板）；P141 环形链表
 *
 * <p>编写一个算法来判断一个数 n 是不是快乐数。
 * 「快乐数」定义为：对于一个正整数，每一次将该数替换为它每个位置上的数字的平方和。
 * 然后重复这个过程直到这个数变为 1，也可能是无限循环但始终变不到 1。
 * 如果这个过程结果为 1，那么这个数就是快乐数。
 * 如果 n 是快乐数就返回 true；不是，则返回 false。
 *
 * <p>约束：1 &lt;= n &lt;= 2^31 - 1。
 *
 * <p>── 思路要点 ──
 * <p>1. 关键转化：把 getNext(n)（各位平方和）当成链表的 next，整个计算过程就是一条链表。
 * <p>2. 「永远变不到 1」等价于「这条链表里有环」——于是问题变成 141/142 的判环。
 * <p>3. 两种实现：HashSet 记录出现过的小数字（直观）；快慢指针判环（O(1) 空间，面试加分）。
 */
public class P202HappyNumber {

    public boolean isHappy(int n) {
        int slow = n;
        int fast = getNext(n);
        while (fast != 1 && slow != fast) {
            slow = getNext(slow);
            fast = getNext(getNext(fast));
        }
        return fast == 1;
    }

    /** 各位数字的平方和（题目定义的「下一步」）。 */
    private static int getNext(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }

    public static void main(String[] args) {
        P202HappyNumber solver = new P202HappyNumber();

        run(solver, 19, true);          // 示例：19 → 82 → 68 → 100 → 1
        run(solver, 1, true);           // 边界：1 本身是快乐数
        run(solver, 2, false);          // 非快乐数：进入 4 → 16 → 37 → ... 循环
        run(solver, 4, false);          // 边界：循环里的数字
        run(solver, 7, true);           // 7 → 49 → 97 → 130 → 10 → 1
        run(solver, 100, true);         // 末尾多个 0
        run(solver, 1111111, true);     // 全 1：7 → 49 → ... → 1
        run(solver, 999999999, false);  // 大数：729 → ... → 4 循环
    }

    private static void run(P202HappyNumber solver, int n, boolean expected) {
        String label = "n=" + n;
        try {
            boolean got = solver.isHappy(n);
            System.out.println((got == expected ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + got + " expected=" + expected);
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}