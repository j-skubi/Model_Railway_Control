package utils.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    private boolean checkOrderRestrain(AVLTree<Int> avlTree) {
        boolean ret = true;
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
    void basicInsertTest() throws InterruptedException {
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
    void randomizedInsertTest() throws InterruptedException {
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
                    try {
                        avlTree.insert(new Int(random.nextInt()));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
                    try {
                        avlTree.insert(new Int(rd.nextInt()));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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