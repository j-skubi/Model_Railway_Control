package layout.model;

import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.Event;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class Sensor extends LayoutComponent {
    private boolean isOccupied;
    public Sensor(JsonObject json) {
        super(json);
        isOccupied = json.get("isOccupied").getAsBoolean();
    }
    public boolean isOccupied() {
        return isOccupied;
    }
    @Override
    public boolean hasAddress(JsonObject command, int address) {
        if (!command.get("type").getAsString().equals("SENSOR")) {
            return false;
        }
        return addressSpaceMappings.get(command.get("addressSpace").getAsString()).getAsInt() == address;
    }
    @Override
    public void applyStandaloneMessage(JsonObject json, PriorityBlockingQueueWrapper<Command> queue) {
        isOccupied = json.get("newState").getAsInt() == 1;
        if (json.get("newState").getAsInt() != json.get("oldState").getAsInt()) {
            notifyChange(json,queue);
        }
    }
    @Override
    public void notifyChange(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
        JsonObject additionalInfo = new JsonObject();
        additionalInfo.addProperty("isOccupied", isOccupied);
        notifyListeners(new Event(Event.EventType.S88Event, additionalInfo, queue, this));
    }
}
