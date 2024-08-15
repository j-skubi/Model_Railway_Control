package utils.datastructures;

import com.google.gson.JsonObject;

public class Command implements Comparable<Command> {
    private final JsonObject jsonObject;
    private final int priority;

    public Command(int priority, JsonObject json) {
        this.priority = priority;
        this.jsonObject = json;
    }

    public JsonObject getJson() {
        return jsonObject;
    }
    public int getPriority() {
        return priority;
    }
    @Override
    public int compareTo(Command o) {
        return Integer.compare(priority,o.priority);
    }
}
