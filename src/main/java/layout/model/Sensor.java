package layout.model;

import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class Sensor extends LayoutComponent {
    private int address;
    private boolean isActive;
    public Sensor(JsonObject json) {
        super(json);
    }

    @Override
    public void notifyChange(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {

    }
}
