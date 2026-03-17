package my.interview.practice.dsa;

import my.interview.practice.test.IterableConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

// Always use mid = left + (right - left) / 2 to avoid integer overflow
// Watch out for left <= right vs left < right conditions
// Practice identifying when a problem can use binary search (sorted/monotonic property)
public class BinarySearchTest {

    // Problem: Find the index of a target value in a sorted array. Return -1 if not found.
    @ParameterizedTest
    @CsvSource(value = {"[1,2,3,4,6]:5:-1", "[2,3,4,6]:3:1"}, delimiter = ':')
    void binarySearch(@ConvertWith(IterableConverter.class) int[] nums, int target, int expected) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                assertEquals(expected, mid);
                return;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        assertEquals(expected, -1);
    }

    // Problem: Find the starting and ending position of a target value in a sorted array.
    // TODO - If it sorted array why do we need to run 2 functions. After first find, it should be +/-1 of the index
    @ParameterizedTest
    @CsvSource(value = {"[1,2,3,3,6]:3:2:3", "[2,3,4,6,6]:6:3:4"}, delimiter = ':')
    void searchRange(@ConvertWith(IterableConverter.class) int[] nums, int target, int expected1, int expected2) {
        assertEquals(expected1, findFirst(nums, target));
        assertEquals(expected2, findLast(nums, target));
    }

    private int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                right = mid - 1; // Continue searching left
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    private int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                left = mid + 1; // Continue searching right
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    // Problem: Search in a rotated sorted array (e.g., [4,5,6,7,0,1,2]).
    @ParameterizedTest
    @CsvSource(value = {"[4,5,6,7,0,1,2]:1:5", "[4,5,6,7,0,1,2]:5:1", "[4,5,6,7,0,1,2]:3:-1"}, delimiter = ':')
    void search(@ConvertWith(IterableConverter.class) int[] nums, int target, int expected) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                assertEquals(expected, mid);
                return;
            }

            // Determine which half is sorted
            if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (target >= nums[left] && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (target > nums[mid] && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        assertEquals(expected, -1);
    }

    // Problem: Find the minimum element in a rotated sorted array.
    @ParameterizedTest
    @CsvSource(value = {"[4,5,6,7,0,1,2]:0"}, delimiter = ':')
    void findMin(@ConvertWith(IterableConverter.class) int[] nums, int expected) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                // Minimum is in right half
                left = mid + 1;
            } else {
                // Minimum is in left half (including mid)
                right = mid;
            }
        }
        assertEquals(expected, nums[left]);
    }

    // Problem: Search in a 2D matrix where each row is sorted and
    // first element of each row > last element of previous row.
    @ParameterizedTest
    @CsvSource(value = {"6:true", "10:false"}, delimiter = ':')
    void searchMatrix(int target, boolean expected) {
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        int rows = matrix.length;
        int cols = matrix[0].length;
        int left = 0, right = rows * cols - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midValue = matrix[mid / cols][mid % cols];

            if (midValue == target) {
                assertTrue(expected);
                return;
            } else if (midValue < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        assertFalse(expected);
    }

    // Problem: Find the integer square root of x (rounded down).
    @ParameterizedTest
    @CsvSource(value = {"17:4", "16:4", "32:5", "1:1"}, delimiter = ':')
    void mySqrt(int x, int expected) {
        if (x == 0 || x == 1) {
            assertEquals(expected, x);
        }

        int left = 1, right = x;
        int result = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (mid <= x / mid) { // Avoid overflow
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        assertEquals(expected, result);
    }

    // Problem: Find a peak element (element greater than its neighbors).
    // If you are on a slope, a peak must exist in the direction of the ascent.
    @ParameterizedTest
    @CsvSource(value = {"[1, 2, 1, 3, 5, 6, 4]:5"}, delimiter = ':')
    void findPeakElement(@ConvertWith(IterableConverter.class) int[] nums, int expected) {
        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < nums[mid + 1]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        assertEquals(expected, left);
    }
}
