package utils.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    private boolean checkOrderRestrain(AVLTree<Int> avlTree) {
        int last = Integer.MIN_VALUE;
        for (Int i : avlTree) {
            if (i.d < last) {
                return false;
            }
            last = i.d;
        }
        return true;
    }

    @BeforeEach
    void setUp() {
    }
    @Test
    void basicInsertTest() {
        AVLTree<Int> avlTree = new AVLTree<>();
        for (int i = 0; i < 10; i++) {
            avlTree.insert(new Int(i));
        }
        int counter = 0;
        for (Int i : avlTree) {
            assertEquals(counter, i.d);
            counter++;
        }
    }
    @Test
    void randomizedInsertTest() {
        AVLTree<Int> avlTree = new AVLTree<>();
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            avlTree.insert(new Int(random.nextInt()));
        }
        assertTrue(checkOrderRestrain(avlTree));
    }
    @Test
    void multithreadedInsertTest() throws InterruptedException {
        AVLTree<Int> avlTree = new AVLTree<>();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                Random random = new Random();
                for (int r = 0; r < 100; r++) {
                    avlTree.insert(new Int(random.nextInt()));
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        assertTrue(checkOrderRestrain(avlTree));
    }
    @Test
    void randomizedAsyncIteratorTest() throws InterruptedException {
        final Random rd = new Random();
        AVLTree<Int> avlTree = new AVLTree<>();
        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Thread t = new Thread(() -> {
                if (rd.nextInt(0,10) > 3) {
                    avlTree.insert(new Int(rd.nextInt()));
                } else {
                    assertTrue(checkOrderRestrain(avlTree));
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }
    @Test
    void basicFindTest() {
        AVLTree<Int> avlTree = new AVLTree<>();
        for (int i = 0; i < 10; i++) {
            avlTree.insert(new Int(i));
        }
        for (int i = 0; i < 10; i++) {
            assertEquals(new Int(i), avlTree.find(i));
        }
    }

    private static class Int implements AVLTree.AVLDataElement {
        private final int d;
        public Int(int i) {
            this.d = i;
        }
        @Override
        public int calculateKey() {
            return d;
        }
        @Override
        public boolean equals(Object o) {
            return o instanceof Int && ((Int) o).d == d;
        }
        @Override
        public String toString() {
            return d + "";
        }
    }
}