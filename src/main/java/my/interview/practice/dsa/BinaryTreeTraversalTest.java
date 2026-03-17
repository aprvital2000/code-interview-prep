package my.interview.practice.dsa;

import my.interview.practice.test.Pair;
import my.interview.practice.test.TreeNode;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinaryTreeTraversalTest {
    //                  1
    //                 / \
    //                 2   3
    //                / \
    //               4   5
    TreeNode getTreeNode() {
        // Create the leaf nodes
        TreeNode node4 = new TreeNode(4);
        TreeNode node5 = new TreeNode(5);
        TreeNode node3 = new TreeNode(3);

        // Create the intermediate node (node 2) and link its children
        TreeNode node2 = new TreeNode(2);
        node2.left = node4;
        node2.right = node5;

        // Create the root node (node 1) and link its children
        TreeNode root = new TreeNode(1);
        root.left = node2;
        root.right = node3;
        return root;
    }

    // 1. Inorder Traversal (Recursive)
    @Test
    void inorderTraversal1() {
        TreeNode root = getTreeNode();
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        assertEquals(Arrays.asList(4, 2, 5, 1, 3), result);
    }

    private void inorder(TreeNode node, List<Integer> result) {
        if (node == null)
            return;
        inorder(node.left, result);
        result.add(node.val);
        inorder(node.right, result);
    }

    @Test
        // 2. Inorder Traversal (Iterative)
    void inorderTraversal2() {
        TreeNode root = getTreeNode();
        List<Integer> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.val);
            current = current.right;
        }
        assertEquals(Arrays.asList(4, 2, 5, 1, 3), result);
    }

    // 3. Preorder Traversal (Recursive)
    @Test
    void preorderTraversal1() {
        TreeNode root = getTreeNode();

        List<Integer> result = new ArrayList<>();
        preorder(root, result);
        assertEquals(Arrays.asList(1, 2, 4, 5, 3), result);
    }

    private void preorder(TreeNode node, List<Integer> result) {
        if (node == null)
            return;
        result.add(node.val);
        preorder(node.left, result);
        preorder(node.right, result);
    }

    // 4. Preorder Traversal (Iterative)
    @Test
    void preorderTraversal2() {
        TreeNode root = getTreeNode();
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return;

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);
            if (node.right != null)
                stack.push(node.right);
            if (node.left != null)
                stack.push(node.left);
        }
        assertEquals(Arrays.asList(1, 2, 4, 5, 3), result);
    }

    // 5. Postorder Traversal (Recursive)
    @Test
    void postorderTraversal1() {
        TreeNode root = getTreeNode();
        List<Integer> result = new ArrayList<>();
        postorder(root, result);
        assertEquals(Arrays.asList(4, 5, 2, 3, 1), result);
    }

    private void postorder(TreeNode node, List<Integer> result) {
        if (node == null)
            return;
        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.val);
    }

    //6. Postorder Traversal (Iterative)
    @Test
    void postorderTraversal2() {
        TreeNode root = getTreeNode();
        List<Integer> result = new ArrayList<>();
        if (root == null)
            return;

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.addFirst(node.val); // Add to front
            if (node.left != null)
                stack.push(node.left);
            if (node.right != null)
                stack.push(node.right);
        }
        assertEquals(Arrays.asList(4, 5, 2, 3, 1), result);
    }

    // 7. Level Order Traversal (BFS)
    @Test
    void levelOrder() {
        TreeNode root = getTreeNode();
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
            result.add(currentLevel);
        }
        assert !result.isEmpty();
        System.out.println(result);
    }

    // 8. Zigzag Level Order Traversal
    @Test
    void zigzagLevelOrder() {
        TreeNode root = getTreeNode();
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (leftToRight) {
                    currentLevel.add(node.val);
                } else {
                    currentLevel.add(0, node.val);
                }
                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
            result.add(currentLevel);
            leftToRight = !leftToRight;
        }
        assert !result.isEmpty();
        System.out.println(result);
    }

    // 9. Vertical Order Traversal
    @Test
    void verticalOrder() {
        TreeNode root = getTreeNode();
        List<List<Integer>> result = new ArrayList<>();
        if (root == null)
            return;

        Map<Integer, List<Integer>> map = new TreeMap<>();
        Queue<Pair<TreeNode, Integer>> queue = new LinkedList<>();
        queue.offer(new Pair<>(root, 0));

        while (!queue.isEmpty()) {
            Pair<TreeNode, Integer> pair = queue.poll();
            TreeNode node = pair.getKey();
            int col = pair.getValue();

            map.putIfAbsent(col, new ArrayList<>());
            map
                    .get(col)
                    .add(node.val);

            if (node.left != null)
                queue.offer(new Pair<>(node.left, col - 1));
            if (node.right != null)
                queue.offer(new Pair<>(node.right, col + 1));
        }

        result.addAll(map.values());
        assert !result.isEmpty();
        System.out.println(result);
    }

    // 10. Right Side View
    @Test
    void rightSideView() {
        TreeNode root = getTreeNode();

        List<Integer> result = new ArrayList<>();
        if (root == null)
            return;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                if (i == levelSize - 1) { // Last node in level
                    result.add(node.val);
                }
                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }
        }
        assert !result.isEmpty();
        System.out.println(result);
    }
}
