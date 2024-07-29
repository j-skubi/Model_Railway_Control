package layout.model;

import com.google.gson.JsonArray;
import utils.datastructures.AVLTree.AVLDataElement;
import utils.datastructures.Event;
import utils.datastructures.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class LayoutComponent implements AVLDataElement {
    protected final int id;
    protected final List<EventListener> listeners;
    protected final Map<String, Map<String, JsonArray>> addressMapping;     //State to Address Mapping for all AddressSpaces

    public LayoutComponent(int id) {
        this.id = id;
        this.listeners = new ArrayList<>();
        this.addressMapping = new HashMap<>();
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

}
