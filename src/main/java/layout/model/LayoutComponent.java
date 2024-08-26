package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import utils.datastructures.AVLTree.AVLDataElement;
import utils.datastructures.Command;
import utils.datastructures.Event;
import utils.datastructures.EventListener;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class LayoutComponent implements AVLDataElement {
    protected final JsonObject addressSpaceMappings;
    protected final String type;
    protected final int id;
    protected final List<EventListener> listeners;

    public LayoutComponent(int id, String type) {
        this.id = id;
        this.type = type;
        this.listeners = new ArrayList<>();
        this.addressSpaceMappings = new JsonObject();
    }
    public LayoutComponent(JsonObject json) {
        this.id = json.get("id").getAsInt();
        this.type = json.get("type").getAsString();
        addressSpaceMappings = json.get("addressSpaceMappings").getAsJsonObject();
        this.listeners = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    protected void notifyListeners(Event event) {
        listeners.stream().filter(s -> s.doesConsume(event.eventType())).forEach(e -> e.apply(event));
    }
    public void addListener(EventListener eventListener) {
        listeners.add(eventListener);
    }
    @Override
    public int calculateKey() {
        return id;
    }
    public JsonObject save() {
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("type", type);
        json.add("addressSpaceMappings",addressSpaceMappings);
        return json;
    }

    public abstract void notifyChange(JsonObject command, PriorityBlockingQueueWrapper<Command> queue);

    public static LayoutComponent fromJson(JsonObject json) throws CorruptedSaveFile {
        return switch (json.get("type").getAsString()) {
            case "TURNOUT" -> new Turnout(json);
            case "LOK" -> new Lok(json);
            default -> throw new CorruptedSaveFile("Provided Json was not a legal LayoutComponent");
        };
    }
}
