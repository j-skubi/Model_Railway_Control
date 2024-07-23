package layout.model;

import utils.datastructures.AVLTree.AVLDataElement;
import utils.datastructures.Event;
import utils.datastructures.EventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class LayoutComponent implements AVLDataElement {
    protected final int id;
    protected final List<EventListener> listeners;

    public LayoutComponent(int id) {
        this.id = id;
        listeners = new ArrayList<>();
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
