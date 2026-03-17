package my.interview.practice.test;

public class ListNode {
    public int val;
    public ListNode next;

    public ListNode(int val) {
        this.val = val;
        this.next = null;
    }

    public static ListNode toListNode(int[] nums) {
        ListNode head = new ListNode(nums[0]);
        ListNode current = head;
        for (int i = 1; i < nums.length; i++) {
            while (current.next != null) {
                current = current.next;
            }
            current.next = new ListNode(nums[i]);
        }
        printList(head);
        return head;
    }

    public static void printList(ListNode current) {
        while (current != null) {
            System.out.print(current.val + " ");
            current = current.next;
        }
        System.out.println();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ListNode [");
        sb.append(val);
        sb.append("-->");
        if (next != null) {
            sb.append(next.val);
        }
        sb.append("]");
        return sb.toString();
    }
}
