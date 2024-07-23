package utils.datastructures;

import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueWrapper<T> {
    private final PriorityBlockingQueue<T> priorityBlockingQueue;
    public PriorityBlockingQueueWrapper(PriorityBlockingQueue<T> priorityBlockingQueue) {
        this.priorityBlockingQueue = priorityBlockingQueue;
    }
    public void add(T elem) { priorityBlockingQueue.add(elem); }
}
