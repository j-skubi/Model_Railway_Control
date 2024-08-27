package layout.model;

import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class Sensor extends LayoutComponent {
    private boolean isActive;
    public Sensor(JsonObject json) {
        super(json);
    }
    @Override
    public boolean hasAddress(String addressSpace, int address) {
        return addressSpaceMappings.get(addressSpace).getAsInt() == address;
    }
    @Override
    public void applyStandaloneMessage(JsonObject json, PriorityBlockingQueueWrapper<Command> queue) {

    }
    @Override
    public void notifyChange(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {

    }
}
