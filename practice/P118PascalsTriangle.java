import java.util.List;

/**
 * LeetCode 118: 杨辉三角 (Pascal's Triangle)
 * https://leetcode.cn/problems/pascals-triangle/
 *
 * <p>【难度】Easy
 * <p>【标签】数组(Array) · 动态规划(DP) · 模拟(Simulation)
 * <p>【题型】模拟构造：每一行由上一行「相邻两数之和」推出
 * <p>【考点】递推关系 row[j] = prev[j-1] + prev[j]，且每行首尾恒为 1
 * <p>【关联】{@link P66PlusOne} —— 同为在数组上做按位递推 / 模拟构造
 *
 * <p>给定非负整数 numRows，生成杨辉三角的前 numRows 行。
 * 每行首尾为 1，其余每个数等于它「左上 + 右上」两数之和。
 *
 * <p>── 思路引导 ──
 * <p>1. 观察结构：第 i 行（从 0 计）有 i+1 个元素，首尾都是 1。
 *
 * <p>2. 关键观察：中间第 j 个 = 上一行第 j-1 与第 j 个之和，故每行只依赖上一行，逐行往下推即可。
 *
 * <p>3. 核心技巧：外层 i 从 0 到 numRows-1 构建每一行，先把两端置 1，
 *    内层 j 从 1 到 i-1 用上一行求中间值。
 *
 * <p>4. 复杂度：时间 O(numRows²)，空间 O(1)（不含输出）。
 *    边界：numRows=0 返回空列表；numRows=1 只有 [1]。
 */
public class P118PascalsTriangle {

    public List<List<Integer>> generate(int numRows) {
        // 实现时请按 CLAUDE.md 约定，补上与「思路引导」编号对应的 // Step N 注释
        throw new UnsupportedOperationException("TODO: 待实现");
    }

    public static void main(String[] args) {
        P118PascalsTriangle p118 = new P118PascalsTriangle();

        assertEqual("[[1]]", p118.generate(1).toString(), "numRows = 1");
        assertEqual("[]", p118.generate(0).toString(), "numRows = 0");
        assertEqual("[[1], [1, 1]]", p118.generate(2).toString(), "numRows = 2");
        assertEqual("[[1], [1, 1], [1, 2, 1], [1, 3, 3, 1], [1, 4, 6, 4, 1]]",
            p118.generate(5).toString(), "numRows = 5");

        System.out.println("All tests passed!");
    }

    private static void assertEqual(String expected, String actual, String testName) {
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format("[FAIL] %s: expected %s, but got %s",
                testName, expected, actual));
        }
        System.out.println("[PASS] " + testName);
    }
}
