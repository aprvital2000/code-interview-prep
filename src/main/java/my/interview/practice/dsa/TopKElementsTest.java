package my.interview.practice.dsa;

import my.interview.practice.test.IterableConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Key Pattern: Most top K problems use a heap (priority queue) of size K to efficiently maintain
 * the K largest/smallest/most frequent elements without sorting the entire dataset.
 */
public class TopKElementsTest {

    // 1. Kth Largest Element in an Array
    @ParameterizedTest
    @CsvSource(value = {"[1, 2, 1, 3, 5, 6, 4]:2:5"}, delimiter = ':')
    void findKthLargest(@ConvertWith(IterableConverter.class) int[] nums, int k, int expected) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        assertEquals(expected, minHeap.peek());
    }

    // 2. Top K Frequent Elements
    @ParameterizedTest
    @CsvSource(value = {"[1, 2, 1, 2, 5, 6, 4]:2:[1, 2]"}, delimiter = ':')
    void topKFrequent(@ConvertWith(IterableConverter.class) int[] nums, int k, @ConvertWith(IterableConverter.class) int[] expected) {
        List<Integer> expectedL = Arrays
                .stream(expected)
                .boxed()
                .toList();
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>((a, b) -> freqMap.get(a) - freqMap.get(b));

        for (int num : freqMap.keySet()) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        for (int i = 0; i < k; i++) {
            assertTrue(expectedL.contains(minHeap.poll()));
        }
    }

    // 3. K Closest Points to Origin
    @Test
    void kClosest() {
        int k = 2;
        int[][] points = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> (b[0] * b[0] + b[1] * b[1]) - (a[0] * a[0] + a[1] * a[1]));

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
    }

    // 4. Kth Smallest Element in a Sorted Matrix
    @Test
    void kthSmallest() {
        int k = 2;
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9},
        };
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        int n = matrix.length;
        for (int i = 0; i < Math.min(n, k); i++) {
            minHeap.offer(new int[]{matrix[i][0], i, 0});
        }

        int result = 0;
        for (int i = 0; i < k; i++) {
            int[] curr = minHeap.poll();
            result = curr[0];
            int row = curr[1];
            int col = curr[2];

            if (col + 1 < n) {
                minHeap.offer(new int[]{matrix[row][col + 1], row, col + 1});
            }
        }
        assertEquals(2, result);
    }

    // 5. Top K Frequent Words
    @ParameterizedTest
    @CsvSource(value = {"one two one too:1:one"}, delimiter = ':')
    void topKFrequent2(String wordsArray, int k, String expected) {
        String[] words = wordsArray.split(" ");
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word : words) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }

        PriorityQueue<String> minHeap = new PriorityQueue<>((a, b) ->
                freqMap
                        .get(a)
                        .equals(freqMap.get(b)) ? b.compareTo(a) : freqMap.get(a) - freqMap.get(b));
//        PriorityQueue<String> minHeap = new PriorityQueue<>(Comparator.comparingInt(freqMap::get));

        for (String word : freqMap.keySet()) {
            minHeap.offer(word);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        assertEquals(expected, minHeap.poll());
    }

    // 6. Find K Pairs with Smallest Sums
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums1.length == 0 || nums2.length == 0 || k == 0) {
            return result;
        }

        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> (a[0] + a[1]) - (b[0] + b[1]));

        for (int i = 0; i < Math.min(k, nums1.length); i++) {
            minHeap.offer(new int[]{nums1[i], nums2[0], 0});
        }

        while (k > 0 && !minHeap.isEmpty()) {
            int[] curr = minHeap.poll();
            result.add(Arrays.asList(curr[0], curr[1]));

            if (curr[2] + 1 < nums2.length) {
                minHeap.offer(new int[]{curr[0], nums2[curr[2] + 1], curr[2] + 1});
            }
            k--;
        }

        return result;
    }

}
