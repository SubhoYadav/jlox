package com.jlox.experiment;
import java.util.*;

public class Tester {
    private static class Node {
        int data;
        Node left;
        Node right;

        Node (int _data, Node _left, Node _right) {
            this.data = _data;
            this.left = _left;
            this.right = _right;
        }

        Node (int _data) {
            this.data = _data;
            this.left = this.right = null;
        }
    }

    private static class Pair {
        Node n;
        int mode;

        Pair(Node _n, int _mode) {
            this.n = _n;
            this.mode = _mode;
        }
    }

    private static void prePostInorderTraversalInSinglePass(Node root) {
        ArrayList<Integer> preorder = new ArrayList<>();
        ArrayList<Integer> inorder = new ArrayList<>();
        ArrayList<Integer> postorder = new ArrayList<>();

        Stack<Pair> stck = new Stack<>();
        stck.push(new Pair(root, 1));

        while (!stck.isEmpty()) {
            Pair stckTop = stck.pop();
            Node n = stckTop.n;
            int mode = stckTop.mode;

            if (mode == 1) {
                // preorder
                preorder.add(n.data);
                stck.push(new Pair(n, mode + 1));
                
                if (n.left != null) {
                    stck.push(new Pair(n.left, 1));
                }
            }
            else if (mode == 2) {
                // inorder
                inorder.add(n.data);
                stck.push(new Pair(n, mode + 1));
                
                if (n.right != null) {
                    stck.push(new Pair(n.right, 1));
                }
            }
            else {
                // postorder
                postorder.add(n.data);
            }
        }

        System.out.println("Preorder traversal: " + preorder);
        System.out.println("Inorder traversal: " + inorder);
        System.out.println("Postorder traversal: " + postorder);
    }

    

    public static void main(String[] args) {
        Node root = new Node(
            1,
            new Node(
                2,
                new Node(3),
                new Node(4)
            ),
            new Node(5)
        );

        prePostInorderTraversalInSinglePass(root);
    }
}