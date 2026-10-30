/**
 * LeetCode 347: 前 K 个高频元素 (Top K Frequent Elements)
 * https://leetcode.cn/problems/top-k-frequent-elements/
 *
 * <p>【难度】Medium
 * <p>【标签】排序与堆(Sorting & Heap) · 哈希表(Hash Table) · 优先队列
 * <p>【题型】哈希计数 + 小顶堆求 Top K
 * <p>【考点】求前 K 大要用【小顶堆】（堆顶是这 K 个里最小的，方便淘汰）
 * <p>【关联】P215 数组中的第 K 大；P295 数据流的中位数（双堆）
 *
 * <p>给你一个整数数组 nums 和一个整数 k，请你返回其中出现频率前 k 高的元素。你可以按任意顺序返回答案。
 *
 * <p>约束：1 &lt;= nums.length &lt;= 10^5；k 的取值范围是 [1, 数组中不相同的元素的个数]；
 * 题目数据保证答案唯一（前 k 个高频元素的集合唯一）。
 *
 * <p>【注意】返回顺序任意，判题时按集合比较。
 * <p>【可用工具】下方 MinHeap 是标准最小堆（纯 int）。若不用它，可改用 PriorityQueue。
 * <p>【编码提示（若用 MinHeap）】code = 频率 * 10000 + (值 + 1000)；解码 值 = code % 10000 - 1000。
 */
import java.util.HashMap;
import java.util.Map;

public class P347TopKFrequentElements {

    /** 标准最小堆（小顶堆）：纯 int，零依赖。这里存的是编码后的 code。 */
    static class MinHeap {
        private final int[] heap;
        private int size;

        MinHeap(int capacity) {
            heap = new int[capacity];
        }

        int size()        { return size; }
        boolean isEmpty() { return size == 0; }
        int peek()        { return heap[0]; }

        void push(int v) {
            heap[size] = v;
            siftUp(size);
            size++;
        }

        int pop() {
            int top = heap[0];
            size--;
            heap[0] = heap[size];
            siftDown(0);
            return top;
        }

        private void siftUp(int i) {
            while (i > 0) {
                int p = (i - 1) / 2;
                if (heap[p] <= heap[i]) break;
                swap(p, i);
                i = p;
            }
        }

        private void siftDown(int i) {
            while (true) {
                int l = 2 * i + 1;
                int r = l + 1;
                int min = i;
                if (l < size && heap[l] < heap[min]) min = l;
                if (r < size && heap[r] < heap[min]) min = r;
                if (min == i) break;
                swap(i, min);
                i = min;
            }
        }

        private void swap(int i, int j) {
            int t = heap[i];
            heap[i] = heap[j];
            heap[j] = t;
        }
    }

    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int i : nums) {
            count.merge(i, 1, Integer::sum);
        }

        MinHeap minHeap = new MinHeap(k + 1);
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            int code = entry.getValue() * 10000 + (entry.getKey() + 1000);
            minHeap.push(code);
            if (minHeap.size() > k) {
                minHeap.pop();
            }
        }

        int[] res = new int[k];
        for (int i = 0; i < k; i++) {
            res[i] = minHeap.pop() % 10000 - 1000;
        }
        return res;
    }

    public static void main(String[] args) {
        P347TopKFrequentElements solver = new P347TopKFrequentElements();

        run(solver, new int[]{1, 1, 1, 2, 2, 3}, 2, new int[]{1, 2}, "示例1");
        run(solver, new int[]{1}, 1, new int[]{1}, "单元素");
        run(solver, new int[]{1, 2}, 2, new int[]{1, 2}, "k=n");
        run(solver, new int[]{4, 4, 4, 4, 5, 5, 5, 6, 6, 7}, 3, new int[]{4, 5, 6}, "频次 4:4 5:3 6:2 7:1");
        run(solver, new int[]{1, 1, 1}, 1, new int[]{1}, "全相同");
        run(solver, new int[]{1, 2, 3}, 3, new int[]{1, 2, 3}, "k=n 各不相同");
        run(solver, new int[]{1, 2, 1, 2, 3}, 2, new int[]{1, 2}, "频次平局");
        run(solver, new int[]{-1, -1, 2, 2, 3}, 2, new int[]{-1, 2}, "含负数");
    }

    private static void run(P347TopKFrequentElements solver, int[] nums, int k, int[] expected, String name) {
        String label = name + " nums=" + java.util.Arrays.toString(nums) + " k=" + k;
        try {
            int[] got = solver.topKFrequent(nums, k);
            int[] gotSorted = got.clone();
            int[] expSorted = expected.clone();
            java.util.Arrays.sort(gotSorted);
            java.util.Arrays.sort(expSorted);
            System.out.println((java.util.Arrays.equals(gotSorted, expSorted) ? "[PASS] " : "[FAIL] ") + label
                    + " | got=" + java.util.Arrays.toString(got) + " expected(任意序)=" + java.util.Arrays.toString(expected));
        } catch (UnsupportedOperationException e) {
            System.out.println("[FAIL] " + label + " | UnsupportedOperationException: TODO 未实现");
        } catch (Exception e) {
            System.out.println("[FAIL] " + label + " | 抛异常: " + e);
        }
    }
}