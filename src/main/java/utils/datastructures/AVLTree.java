package utils.datastructures;

import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class AVLTree <T extends AVLTree.AVLDataElement> implements Iterable<T> {
    private Node root;
    private final ReentrantReadWriteLock lock;

    public AVLTree() {
        lock = new ReentrantReadWriteLock();
    }
    //-------------API-Functions-----------------------------
    public void insert (T data) throws InterruptedException {
        lock.writeLock().lock();
        try {
            root = insert(root, data);
        } finally {
            lock.writeLock().unlock();
        }
    }
    public T find (int key) throws InterruptedException {
        lock.readLock().lock();
        Node temp;
        try {
            temp = findNode(key);
        } finally {
            lock.readLock().unlock();
        }
        return temp == null ? null : temp.data;
    }
    public void reverseTraverse(Consumer<T> f) {
        reverseTraverse(f, root);
    }
    public void traverse(Consumer<T> f) {
        traverse(f, root);
    }
    //-----------Utility-Functions---------------------------
    public Iterator<T> iterator() {
        lock.readLock().lock();
        try {
            return new Iterator<>() {
                private final Stack<T> stack;

                {
                    stack = new Stack<>();
                    reverseTraverse(stack::push);
                }

                @Override
                public boolean hasNext() {
                    return !stack.empty();
                }

                @Override
                public T next() {
                    return stack.pop();
                }
            };
        } finally {
            lock.readLock().unlock();
        }
    }
    public int size() {
        AtomicInteger counter = new AtomicInteger();
        traverse((data -> counter.getAndIncrement()));
        return counter.get();
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        traverse((data -> sb.append(data.toString()).append(" | ")));
        return sb.toString();
    }
    //-----------Internal-Functions--------------------------
    private void reverseTraverse(Consumer<T> f, Node node) {
        if (node != null) {
            reverseTraverse(f, node.right);
            f.accept(node.data);
            reverseTraverse(f,node.left);
        }
    }
    private void traverse(Consumer<T> f, Node node) {
        if (node != null) {
            traverse(f, node.left);
            f.accept(node.data);
            traverse(f, node.right);
        }
    }
    private Node findNode (int key) {
        Node current = root;
        while(current != null) {
            if (current.data.compareTo(key) == 0) {
                break;
            }
            current = current.data.compareTo(key) < 0 ? current.right : current.left;
        }
        return current;
    }
    private Node insert(Node node, T data) {
        if (node == null) {
            return new Node(data);
        } else if (node.data.compareTo(data) > 0) {
            node.left = insert(node.left, data);
        } else if (node.data.compareTo(data) < 0) {
            node.right = insert(node.right, data);
        } else {
            node.data = data;
        }
        return rebalance(node);
    }
    private Node rebalance(Node node) {
        updateHeight(node);
        int balance = getBalance(node);
        if (balance > 1) {
            if (height(node.right.right) > height(node.right.left)) {
                node = rotateLeft(node);
            } else {
                node.right = rotateRight(node.right);
                node = rotateLeft(node);
            }
        } else if (balance < -1) {
            if (height(node.left.left) > height(node.left.right))
                node = rotateRight(node);
            else {
                node.left = rotateLeft(node.left);
                node = rotateRight(node);
            }
        }
        return node;
    }

    private void updateHeight(Node node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }
    private int height(Node node) {
        return node == null ? -1 : node.height;
    }
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.right) - height(node.left);
    }
    private Node rotateRight(Node node) {
        Node tempX = node.left;
        Node tempZ = tempX.right;
        tempX.right = node;
        node.left = tempZ;
        updateHeight(node);
        updateHeight(tempX);
        return  tempX;
    }
    private Node rotateLeft(Node node) {
        Node tempX = node.right;
        Node tempZ = tempX.left;
        tempX.left = node;
        node.right = tempZ;
        updateHeight(node);
        updateHeight(tempX);
        return tempX;
    }

    private class Node {
        private T data;
        private Node left;
        private Node right;
        private int height;
        public Node(T data) {
            this.data = data;
        }
        @Override
        public String toString() {
            return data.toString();
        }
    }
    public interface AVLDataElement extends Comparable<AVLDataElement> {
        int calculateKey();
        default int compareTo(int key) {
            return Integer.compare(this.calculateKey(), key);
        }
        default int compareTo(AVLDataElement dataElement) {
            return Integer.compare(this.calculateKey(), dataElement.calculateKey());
        }
    }
}
