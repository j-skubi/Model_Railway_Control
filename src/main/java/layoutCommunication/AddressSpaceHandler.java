package layoutCommunication;

import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AddressSpaceHandler {
    private final String type;
    protected final PriorityBlockingQueueWrapper<Command> queue;

    public AddressSpaceHandler(String type, PriorityBlockingQueueWrapper<Command> queue) {
        this.type = type;
        this.queue = queue;
    }

    public abstract void applyStateMappings(JsonObject command);

    @Override
    public boolean equals(Object o) {
        if (o instanceof String) {
            return type.equals(o);
        }
        if (o instanceof AddressSpaceHandler) {
            return type.equals(((AddressSpaceHandler) o).type);
        }
        return false;
    }
}
