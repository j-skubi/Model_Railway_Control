package utils.datastructures;

public interface EventListener {
    boolean doesConsume(Event.EventType eventType);
    void apply(Event event);
}
