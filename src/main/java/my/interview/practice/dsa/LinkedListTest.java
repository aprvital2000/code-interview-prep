package my.interview.practice.dsa;

import my.interview.practice.test.IterableConverter;
import my.interview.practice.test.ListNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Key Interview Tips:
 * Master the iterative approach first (most common)
 * Understand the recursive approach for follow-up questions
 * Practice drawing the pointer movements on paper
 * Always consider edge cases: null, single node, two nodes
 * Dummy nodes simplify edge case handling
 */
public class LinkedListTest {

    // Problem: Reverse a singly linked list.
    @ParameterizedTest
    @CsvSource(value = {"[0,1,2,3,4,5]"}, delimiter = ':')
    void reverseList(@ConvertWith(IterableConverter.class) int[] nums) {
        ListNode head = ListNode.toListNode(nums);
        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode nextTemp = current.next;  // Store next node
            current.next = prev;                // Reverse the link
            prev = current;                     // Move prev forward
            current = nextTemp;                 // Move current forward
        }
        assertNotNull(prev);// prev is the new head
        ListNode.printList(prev);
    }

    // 2. Reverse Linked List (Recursive)
    @ParameterizedTest
    @CsvSource(value = {"[0,1,2,3,4,5]"}, delimiter = ':')
    void reverseListRecursive(@ConvertWith(IterableConverter.class) int[] nums) {
        ListNode head = ListNode.toListNode(nums);
        ListNode newHead = reverseListRecursive(head);
        assertNotNull(newHead);
        ListNode.printList(newHead);
    }

    private ListNode reverseListRecursive(ListNode head) {
        // Base case: empty list or single node
        if (head == null || head.next == null) {
            return head;
        }

        // Recursively reverse the rest of the list
        ListNode newHead = reverseListRecursive(head.next);

        // Make the next node point back to current
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    // 3. Reverse Linked List II (Reverse Between Positions)
    // Problem: Reverse nodes from position left to right.
    ListNode reverseBetween(ListNode head, int left, int right) {
        if (head == null || left == right)
            return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        // Move to the node before 'left'
        for (int i = 1; i < left; i++) {
            prev = prev.next;
        }

        // Reverse the sublist
        ListNode current = prev.next;
        ListNode next = null;

        for (int i = 0; i < right - left; i++) {
            next = current.next;
            current.next = next.next;
            next.next = prev.next;
            prev.next = next;
        }

        return dummy.next;
    }

    // 4. Reverse Nodes in k-Group
    //Problem: Reverse nodes in groups of k.
    ListNode reverseKGroup(ListNode head, int k) {
        if (head == null || k == 1)
            return head;

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        while (true) {
            // Check if there are k nodes remaining
            ListNode checker = prev;
            for (int i = 0; i < k; i++) {
                checker = checker.next;
                if (checker == null)
                    return dummy.next;
            }

            // Reverse k nodes
            ListNode current = prev.next;
            ListNode next = null;

            for (int i = 0; i < k - 1; i++) {
                next = current.next;
                current.next = next.next;
                next.next = prev.next;
                prev.next = next;
            }

            prev = current;
        }
    }

    //5. Palindrome Linked List
    // Problem: Check if a linked list is a palindrome by reversing half.
    boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null)
            return true;

        // Find middle using slow/fast pointers
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Reverse second half
        ListNode secondHalf = reverseList0(slow);
        ListNode firstHalf = head;

        // Compare both halves
        while (secondHalf != null) {
            if (firstHalf.val != secondHalf.val)
                return false;
            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }

        return true;
    }

    ListNode reverseList0(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    // 6. Reverse Alternate K Nodes
    // Problem: Reverse every alternate k nodes.
    ListNode reverseAlternateKNodes(ListNode head, int k) {
        if (head == null || k <= 1)
            return head;

        ListNode current = head;
        ListNode prev = null;
        ListNode next = null;
        int count = 0;

        // Reverse first k nodes
        while (current != null && count < k) {
            next = current.next;
            current.next = prev;
            prev = current;
            current = next;
            count++;
        }

        // head now becomes the last node of reversed group
        head.next = current;

        // Skip next k nodes
        count = 0;
        while (current != null && count < k - 1) {
            current = current.next;
            count++;
        }

        // Recursively reverse alternate k nodes
        if (current != null) {
            current.next = reverseAlternateKNodes(current.next, k);
        }

        return prev;
    }
}
