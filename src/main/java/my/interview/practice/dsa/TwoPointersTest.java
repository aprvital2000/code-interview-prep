package my.interview.practice.dsa;

import my.interview.practice.test.IterableConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TwoPointersTest {

    /**
     * Problem 1: Two Sum II - Input Array Is Sorted
     * Given an array of integers numbers that is already sorted in non-decreasing order,
     * find two numbers such that they add up to a specific target number. Return the indices of the two numbers.
     * Example:
     * Input: numbers = [2,7,11,15], target = 9
     * Output: [0,1]
     * Explanation: 2 + 7 = 9, so return [0,1]
     **/
    @ParameterizedTest
    @CsvSource(value = {"[2,7,11,15]:9:0:1", "[2,7,11,15]:18:1:2", "[2,7,11,15]:16:-1:-1"}, delimiter = ':')
    void twoSum(@ConvertWith(IterableConverter.class) int[] numbers, int target, int expectedLeft, int expectedRight) {
        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                // Return 1-indexed positions
                assertEquals(expectedLeft, left);
                assertEquals(expectedRight, right);
                return;
            } else if (sum < target) {
                // Sum too small, move left pointer right
                left++;
            } else {
                // Sum too large, move right pointer left
                right--;
            }
        }
        // No solution found (problem guarantees one exists)
        assertEquals(-1, expectedLeft);
        assertEquals(-1, expectedRight);
    }

    /**
     * Problem 2: Container With Most Water
     * Given an array `height` where `height[i]` represents the height of a vertical line at position `i`,
     * find two lines that together with the x-axis form a container that holds the most water. Return the maximum area.
     * Example:
     * Input: height = [1,8,6,2,5,4,8,3,7]
     * Output: 49
     * Explanation: Lines at index 1 (height=8) and 8 (height=7) form container with area = 7 * 7 = 49
     **/
    @ParameterizedTest
    @CsvSource(value = {"[1,8,6,2,5,4,8,3,7]:49", "[1,3,6,2,5,4,8,3,7]:36"}, delimiter = ':')
    void maxArea(@ConvertWith(IterableConverter.class) int[] height, int expectedArea) {
        int left = 0;
        int right = height.length - 1;
        int maxArea = 0;

        while (left < right) {
            // Calculate current area
            // Width = right - left
            // Height = minimum of the two heights
            int width = right - left;
            int currentHeight = Math.min(height[left], height[right]);
            int currentArea = width * currentHeight;

            // Update max area
            maxArea = Math.max(maxArea, currentArea);

            // Move the pointer pointing to the shorter line
            // This is the key insight: moving the shorter one
            // gives us a chance to find a taller line
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        assertEquals(expectedArea, maxArea);
    }

    /**
     * Problem 4: Valid Palindrome
     * Given a string `s`, determine if it is a palindrome, considering only alphanumeric characters and ignoring cases.
     * Example:
     * Input: s = "A man, a plan, a canal: Panama"
     * Output: true
     */
    @ParameterizedTest
    @CsvSource(value = {"A man, a plan, a canal Panama:true"}, delimiter = ':')
    void isPalindrome(String s, boolean expected) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            // Skip non-alphanumeric from left
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }

            // Skip non-alphanumeric from right
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            // Compare characters (case-insensitive)
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                assertFalse(expected);
                return;
            }

            left++;
            right--;
        }
        assertTrue(expected);
    }

    /**
     * Problem 5: 3Sum
     * Problem: Given an array `nums`, find all unique triplets that sum to zero.
     * Example:
     * Input: nums = [-1,0,1,2,-1,-4]
     * Output: [[-1,-1,2],[-1,0,1]]
     **/
    @ParameterizedTest
    @CsvSource(value = {"[-1,0,1,2,-1,-4]"}, delimiter = ':')
    public List<List<Integer>> threeSum(@ConvertWith(IterableConverter.class) int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Sort first

        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates for first element
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            int left = i + 1;
            int right = nums.length - 1;
            int target = -nums[i];

            while (left < right) {
                int sum = nums[left] + nums[right];

                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    // Skip duplicates for second element
                    while (left < right && nums[left] == nums[left + 1])
                        left++;
                    // Skip duplicates for third element
                    while (left < right && nums[right] == nums[right - 1])
                        right--;

                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }

    /**
     * Problem 6: Squares of a Sorted Array
     * Given a sorted array `nums` (could have negative numbers), return an array of the squares of each number sorted in non-decreasing order.
     * Example:
     * Input: nums = [-4,-1,0,3,10]
     * Output: [0,1,9,16,100]
     **/
    @ParameterizedTest
    @CsvSource(value = {"[-4,-1,0,3,10]:[0,1,9,16,100]"}, delimiter = ':')
    public int[] sortedSquares(@ConvertWith(IterableConverter.class) int[] nums, @ConvertWith(IterableConverter.class) int[] expected) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0;
        int right = n - 1;
        int pos = n - 1; // Fill from the end

        while (left <= right) {
            int leftSquare = nums[left] * nums[left];
            int rightSquare = nums[right] * nums[right];

            // Put the larger square at the current position
            if (leftSquare > rightSquare) {
                result[pos] = leftSquare;
                left++;
            } else {
                result[pos] = rightSquare;
                right--;
            }
            pos--;
        }

        return result;
    }

    /**
     * Problem 7: Trapping Rain Water (Hard)
     * Problem: Given `n` non-negative integers representing an elevation map where the width of each bar is 1,
     * compute how much water it can trap after raining.
     * Example:
     * Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
     * Output: 6
     **/
    @ParameterizedTest
    @CsvSource(value = {"[0,1,0,2,1,0,1,3,2,1,2,1]:6"}, delimiter = ':')
    int trap(@ConvertWith(IterableConverter.class) int[] height, int expected) {
        if (height == null || height.length == 0)
            return 0;

        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                // Process left side
                if (height[left] >= leftMax) {
                    leftMax = height[left];
                } else {
                    water += leftMax - height[left];
                }
                left++;
            } else {
                // Process right side
                if (height[right] >= rightMax) {
                    rightMax = height[right];
                } else {
                    water += rightMax - height[right];
                }
                right--;
            }
        }
        assertEquals(expected, water);
        return water;
    }

}

