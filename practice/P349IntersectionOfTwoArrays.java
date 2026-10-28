import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 349: 两个数组的交集 (Intersection of Two Arrays)
 * https://leetcode.cn/problems/intersection-of-two-arrays/
 *
 * <p>【难度】Easy
 * <p>【标签】哈希表(Hash Table) · 数组(Array) · 集合(Set)
 * <p>【题型】set1 装 nums1 去重，遍历 nums2 命中就放 resultSet（自动去重），再转 int[]
 * <p>【考点】结果要唯一 → 两个 Set；返回值必须是 int[] 不能是 Set
 * <p>【关联】P350 两个数组的交集 II —— 不要求唯一，要看次数
 *
 * <p>给定两个数组 nums1 和 nums2，返回它们的交集。输出结果中的每个元素一定是唯一的。
 * 我们可以不考虑输出结果的顺序。
 * 约束：1 &lt;= nums1.length, nums2.length &lt;= 1000；0 &lt;= nums1[i], nums2[i] &lt;= 1000。
 */
public class P349IntersectionOfTwoArrays {

    public int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> h1 = new HashSet<>();
        Set<Integer> resultSet = new HashSet<>();
        for (int i : nums1) {
            h1.add(i);
        }
        for (int i : nums2) {
            if (h1.contains(i)) {
                resultSet.add(i);
            }
        }
        int[] r = new int[resultSet.size()];
        Integer[] array = resultSet.toArray(new Integer[0]);
        for (int i = 0; i < array.length; i++) {
            r[i] = array[i];
        }
        return r;
    }

    public static void main(String[] args) {
        P349IntersectionOfTwoArrays solver = new P349IntersectionOfTwoArrays();

        run(solver, new int[]{1, 2, 2, 1}, new int[]{2, 2}, new int[]{2});              // 示例1
        run(solver, new int[]{4, 9, 5}, new int[]{9, 4, 9, 8, 4}, new int[]{4, 9});      // 示例2（顺序无关）
        run(solver, new int[]{}, new int[]{1, 2}, new int[]{});                          // 边界：空数组
        run(solver, new int[]{1, 2}, new int[]{3, 4}, new int[]{});                      // 边界：无交集
        run(solver, new int[]{1, 2, 3}, new int[]{1, 2, 3}, new int[]{1, 2, 3});         // 边界：完全相同
        run(solver, new int[]{1, 1, 1}, new int[]{1, 1}, new int[]{1});                  // 边界：重复元素只输出一次
        run(solver, new int[]{-1, 0, 1}, new int[]{0, -1}, new int[]{-1, 0});            // 边界：含负数（本地扩展）
        run(solver, new int[]{1}, new int[]{1}, new int[]{1});                           // 边界：单元素
    }

    private static void run(P349IntersectionOfTwoArrays solver, int[] nums1, int[] nums2, int[] expected) {
        String label = Arrays.toString(nums1) + " ∩ " + Arrays.toString(nums2);
        try {
            int[] got = solver.intersection(nums1, nums2);
            int[] gotSorted = got.clone();
            Arrays.sort(gotSorted);
            int[] expSorted = expected.clone();
            Arrays.sort(expSorted);
            System.out.println((Arrays.equals(gotSorted, expSorted) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + Arrays.toString(got) + " expected=" + Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}