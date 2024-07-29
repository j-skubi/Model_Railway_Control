package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import utils.datastructures.AVLTree.AVLDataElement;
import utils.datastructures.Event;
import utils.datastructures.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LayoutComponent implements AVLDataElement {
    protected final String type;
    protected final int id;
    protected final List<EventListener> listeners;
    protected final Map<String, Map<String, JsonArray>> addressMapping;     //State to Address Mapping for all AddressSpaces

    public LayoutComponent(int id, String type) {
        this.id = id;
        this.type = type;
        this.listeners = new ArrayList<>();
        this.addressMapping = new HashMap<>();
    }
    public LayoutComponent(JsonObject json) {
        this(json.get("id").getAsInt(), json.get("type").getAsString());
        json.get("AddressSpaceMappings").getAsJsonArray().forEach(add -> {
            HashMap<String, JsonArray> address = new HashMap<>();
            addressMapping.put(add.getAsJsonObject().get("AddressSpace").getAsString(), address);
            add.getAsJsonObject().get("StateMappings").getAsJsonArray().forEach(elem -> {
                address.put(elem.getAsJsonObject().get("State").getAsString(), elem.getAsJsonObject().get("Mapping").getAsJsonArray());
            });
        });
    }

    private void notifyListeners(Event event) {
        listeners.stream().filter(s -> s.doesConsume(event.getEventType())).forEach(e -> e.apply(event));
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

        JsonArray addresses = new JsonArray();
        addressMapping.forEach((key, value) -> {
            JsonObject addressSpace = new JsonObject();
            addressSpace.addProperty("AddressSpace", key);

            JsonArray states = new JsonArray();
            value.forEach((key1, value1) -> {
                JsonObject mapping = new JsonObject();
                mapping.addProperty("State", key1);
                mapping.add("Mapping", value1);
                states.add(mapping);
            });
            addressSpace.add("StateMappings", states);
            addresses.add(addressSpace);
        });
        json.add("AddressSpaceMappings", addresses);

        return json;
    }
    public void addAddressMapping(String addressSpace, String state, JsonObject mapping) {
        if (!addressMapping.containsKey(addressSpace)) {
            addressMapping.put(addressSpace, new HashMap<>());
        }
        if (!addressMapping.get(addressSpace).containsKey(state)) {
            addressMapping.get(addressSpace).put(state, new JsonArray());
        }
        addressMapping.get(addressSpace).get(state).add(mapping);
    }

}
