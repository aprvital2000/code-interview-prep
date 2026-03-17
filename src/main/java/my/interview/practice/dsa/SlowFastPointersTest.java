package my.interview.practice.dsa;

import my.interview.practice.test.IterableConverter;
import my.interview.practice.test.ListNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Key Concepts:
 * Slow pointer moves 1 step at a time
 * Fast pointer moves 2 steps at a time
 * When they meet in a cycle, they've covered different distances but are at the same position
 * Time complexity: O(n), Space complexity: O(1)
 */
public class SlowFastPointersTest {

    static Stream<Arguments> testListNodeCycle() {
        return Stream.of(
                Arguments.of(testListNodeWithCycle(), true),
                Arguments.of(testListNodeNoCycle(), false)
        );
    }

    static Stream<Arguments> testDetectListNodeCycle() {
        return Stream.of(
                Arguments.of(testListNodeWithCycle(), 2),
                Arguments.of(testListNodeNoCycle(), -1)
        );
    }

    static Stream<Arguments> testListNodeCycleLength() {
        return Stream.of(
                Arguments.of(testListNodeWithCycle(), 3),
                Arguments.of(testListNodeNoCycle(), 0)
        );
    }

    static Stream<Arguments> testListNodeMid() {
        return Stream.of(
                Arguments.of(testListNodeNoCycle(), 3)
        );
    }

    private static ListNode testListNodeWithCycle() {
        // 1. Create individual nodes
        ListNode head = new ListNode(1);
        ListNode second = new ListNode(2);
        ListNode third = new ListNode(3);
        ListNode fourth = new ListNode(4);

        // 2. Link the nodes sequentially
        head.next = second;
        second.next = third;
        third.next = fourth;

        // 3. Create the cycle: the last node points back to the second node
        fourth.next = second; // This creates the cycle

        // The list now looks like: 1 -> 2 -> 3 -> 4 -> (back to 2)
        return head;
    }

    private static ListNode testListNodeNoCycle() {
        // 1. Create individual nodes
        ListNode head = new ListNode(1);
        ListNode second = new ListNode(2);
        ListNode third = new ListNode(3);
        ListNode fourth = new ListNode(4);

        // 2. Link the nodes sequentially
        head.next = second;
        second.next = third;
        third.next = fourth;

        // The list now looks like: 1 -> 2 -> 3 -> 4
        return head;
    }

    /**
     * Problem 1: Detect Cycle in Linked List
     **/
    @ParameterizedTest
    @MethodSource("testListNodeCycle")
    // Refers to the method name
    void hasCycle(ListNode head, boolean expected) {
        ListNode slow = head;
        ListNode fast = head;
        boolean cycle = false;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                cycle = true;
                break;
            }
        }
        assertEquals(expected, cycle);
    }

    /**
     * Problem 2. Find Middle of Linked List
     **/
    @ParameterizedTest
    @MethodSource("testListNodeMid")
    // Refers to the method name
    void middleNode(ListNode head, int expected) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        assertNotNull(slow);
        assertEquals(expected, slow.val);
    }

    /**
     * Problem 3. Find Start of Cycle in Linked List
     * Variation of Problem 1. Refer to {@link #hasCycle(ListNode, boolean)}
     **/
    @ParameterizedTest
    @MethodSource("testDetectListNodeCycle")
    void detectCycle(ListNode head, int expected) {
        if (head == null)
            return;

        ListNode slow = head;
        ListNode fast = head;

        // Find if cycle exists
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                // Cycle found, find the start
                slow = head;
                while (slow != fast) {
                    slow = slow.next;
                    fast = fast.next;
                }
                assertEquals(expected, slow.val);
                break;
            }
        }
        if (slow != fast) {
            assertEquals(expected, -1);
        }
    }

    /**
     * Problem 4. Happy Number
     * Happy numbers are positive integers that eventually reach 1 when replaced by the sum of the square of their digits, repeated iteratively.
     * If the process enters an endless cycle (typically starting with 4) that does not include 1,
     * the number is considered "sad" or unhappy.
     * The first few happy numbers are 1, 7, 10, 13, 19, 23, 28, 31, 32, and 44.
     * Key Aspects of Happy Numbers:The Process (Example 19):
     * 1^2 + 9^2 =  1 + 81   =  82
     * 8^2 + 2^2 = 64 +  4   =  68
     * 6^2 + 8^2 = 36 + 64   = 100
     * 1^2 + 0^2 + 0^2       =   1
     **/
    @ParameterizedTest
    @CsvSource(value = {"1:true", "7:true", "13:true", "19:true", "33:false"}, delimiter = ':')
    void isHappy(int n, boolean expected) {
        int slow = n;
        int fast = n;
        do {
            slow = getNext(slow);
            fast = getNext(getNext(fast));
        } while (slow != fast);

        assertEquals(expected, slow == 1);
    }

    private int getNext(int n) {
        int sum = 0;
        while (n > 0) {
            int digit = n % 10;
            sum += digit * digit;
            n /= 10;
        }
        return sum;
    }

    /**
     * Problem 5: Remove Duplicates from Sorted Array
     * Given a sorted array nums, remove duplicates in-place such that each element appears only once.
     * Return the new length. Modify the array in-place with O(1) extra memory.
     * Example:
     * Input: nums = [1,1,2,2,3]
     * Output: 3, nums = [1,2,3,_,_]
     */
    @ParameterizedTest
    @CsvSource(value = {"[1,1,2,2,3]"}, delimiter = ':')
    int removeDuplicates(@ConvertWith(IterableConverter.class) int[] nums) {
        if (nums.length == 0)
            return 0;

        int slow = 0; // Points to last unique element

        for (int fast = 1; fast < nums.length; fast++) {
            // Found a new unique element
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
        }

        return slow + 1; // Length of unique elements
    }

    /**
     * 5. Remove Nth Node From End of List
     **/
    @ParameterizedTest
    @MethodSource("testListNodeCycle") // Refers to the method name
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode slow = dummy;
        ListNode fast = dummy;

        // Move fast n+1 steps ahead
        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        // Move both until fast reaches end
        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }

        // Remove the node
        slow.next = slow.next.next;

        return dummy.next;
    }

    /**
     * 6. Palindrome Linked List
     **/
    @ParameterizedTest
    @MethodSource("testListNodeCycle") // Refers to the method name
    public boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null)
            return true;

        // Find middle
        ListNode slow = head;
        ListNode fast = head;

        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Reverse second half
        ListNode secondHalf = reverse(slow.next);

        // Compare both halves
        ListNode p1 = head;
        ListNode p2 = secondHalf;

        while (p2 != null) {
            if (p1.val != p2.val) {
                return false;
            }
            p1 = p1.next;
            p2 = p2.next;
        }

        return true;
    }

    private ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    /**
     * 7. Linked List Cycle II (Length of Cycle)
     **/
    @ParameterizedTest
    @MethodSource("testListNodeCycleLength")
    // Refers to the method name
    void cycleLength(ListNode head, int expected) {
        ListNode slow = head;
        ListNode fast = head;

        // Detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                // Count length
                int length = 1;
                ListNode current = slow.next;
                while (current != slow) {
                    current = current.next;
                    length++;
                }
                assertEquals(expected, length);
                return;
            }
        }
        // No Cycle
        assertEquals(expected, 0);
    }
}
