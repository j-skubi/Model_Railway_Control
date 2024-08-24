package utils.datastructures;

import com.google.gson.JsonObject;

public record Event(utils.datastructures.Event.EventType eventType, JsonObject additionalInfo, PriorityBlockingQueueWrapper<Command> queue) {
    public enum EventType {
        StateChange,
        LokStateChange
    }
}
