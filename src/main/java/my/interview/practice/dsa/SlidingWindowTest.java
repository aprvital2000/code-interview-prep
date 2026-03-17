package my.interview.practice.dsa;

import my.interview.practice.test.IterableConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Key Sliding Window Patterns:
 * 1. Fixed Window Size: Calculate once, then slide (add right, remove left)
 * 2. Dynamic Window: Expand with right pointer, shrink with left when condition breaks
 * 3 Two Pointers: Both move in same direction, maintaining window property
 * Using HashMap/Array: Track frequencies or counts in current window
 */
public class SlidingWindowTest {

    /**
     * TODO - Understand
     * Problem 1: Find the maximum sum of any contiguous subarray of size k.
     **/
    @ParameterizedTest
    @CsvSource(value = {"[1, 2, 1, 3, 5, 6, 4]:2:11", "[1, 2, 1, 3, 5, 6, 4]:3:15"}, delimiter = ':')
    void maxSumSubarray(@ConvertWith(IterableConverter.class) int[] arr, int k, int expected) {
        int n = arr.length;
        if (n < k) {
            assertEquals(expected, -1);
            return;
        }

        // Calculate sum of first window
        int windowSum = 0;
        for (int i = 0; i < k; i++) {
            windowSum += arr[i];
        }

        int maxSum = windowSum;

        // Slide the window
        for (int i = k; i < n; i++) {
            windowSum += arr[i] - arr[i - k];  // Add new element, remove old
            maxSum = Math.max(maxSum, windowSum);
        }
        assertEquals(expected, maxSum);
    }

    /**
     * TODO - Understand
     * Problem 2: Find the length of the longest substring without repeating characters.
     **/
    @ParameterizedTest
    @CsvSource(value = {"abcdabc:4", "abccabc:3"}, delimiter = ':')
    void lengthOfLongestSubstring(String s, int expected) {
        Map<Character, Integer> charIndex = new HashMap<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            // If character is already in window, move left pointer
            if (charIndex.containsKey(current)) {
                left = Math.max(left, charIndex.get(current) + 1);
            }

            charIndex.put(current, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }
        assertEquals(expected, maxLength);
    }

    /**
     * TODO - Understand and Test
     * Problem 3: Find the minimum window in string s that contains all characters of string t
     **/
    @ParameterizedTest
    @CsvSource(value = {"abcdabc:abc:as"}, delimiter = ':')
    String minWindow(String s, String t) {
        if (s.length() < t.length())
            return "";

        Map<Character, Integer> required = new HashMap<>();
        for (char c : t.toCharArray()) {
            required.put(c, required.getOrDefault(c, 0) + 1);
        }

        Map<Character, Integer> window = new HashMap<>();
        int left = 0, right = 0;
        int formed = 0;  // Number of unique chars in window with desired frequency
        int requiredChars = required.size();

        int[] result = {-1, 0, 0};  // {window length, left, right}

        while (right < s.length()) {
            char c = s.charAt(right);
            window.put(c, window.getOrDefault(c, 0) + 1);

            if (required.containsKey(c) && window
                    .get(c)
                    .intValue() == required
                    .get(c)
                    .intValue()) {
                formed++;
            }

            // Try to contract the window
            while (left <= right && formed == requiredChars) {
                c = s.charAt(left);

                // Update result if this window is smaller
                if (result[0] == -1 || right - left + 1 < result[0]) {
                    result[0] = right - left + 1;
                    result[1] = left;
                    result[2] = right;
                }

                window.put(c, window.get(c) - 1);
                if (required.containsKey(c) && window.get(c) < required.get(c)) {
                    formed--;
                }

                left++;
            }

            right++;
        }

        return result[0] == -1 ? "" : s.substring(result[1], result[2] + 1);
    }

    /**
     * TODO - Understand and Test
     * Problem 4: Find the length of the longest substring with at most k distinct characters.
     **/
    @ParameterizedTest
    @CsvSource(value = {"abcdabc:3:3"}, delimiter = ':')
    public int lengthOfLongestSubstringKDistinct(String s, int k, int expected) {
        if (k == 0 || s.isEmpty())
            return 0;

        Map<Character, Integer> charCount = new HashMap<>();
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);

            // Shrink window if we have more than k distinct characters
            while (charCount.size() > k) {
                char leftChar = s.charAt(left);
                charCount.put(leftChar, charCount.get(leftChar) - 1);
                if (charCount.get(leftChar) == 0) {
                    charCount.remove(leftChar);
                }
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    /**
     * TODO - Understand and Test
     * Problem 5: Find the maximum value in each sliding window of size k.
     **/
    @ParameterizedTest
    @CsvSource(value = {"abcdabc:3:3"}, delimiter = ':')
    public int[] maxSlidingWindow(@ConvertWith(IterableConverter.class) int[] nums, int k) {
        if (nums.length == 0 || k == 0)
            return new int[0];

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();  // Stores indices

        for (int i = 0; i < n; i++) {
            // Remove indices outside the window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove smaller elements from the back (they won't be max)
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i);

            // Add to result when window is fully formed
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }
}
