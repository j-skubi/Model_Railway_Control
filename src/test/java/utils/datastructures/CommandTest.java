package utils.datastructures;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {
    private PriorityQueue<Command> queue;
    private Random random = new Random();

    @BeforeEach
    void fillQueue() {
        queue = new PriorityQueue<>();
        for (int i = 0; i < 10; i++) {
            queue.add(new Command(random.nextInt(), new JsonObject()));
        }
    }

    @Test
    void testOrder() {
        Command last = new Command(Integer.MIN_VALUE, new JsonObject());
        int limit = 100;
        int i = 0;
        try {
            while (i <= limit) {
                assertTrue(queue.remove().getPriority() >= last.getPriority());
                i++;
            }
        } catch (NoSuchElementException ignored) {}
    }
}