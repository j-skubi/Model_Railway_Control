package layoutCommunication;

import com.google.gson.JsonObject;
import layoutCommunication.addressSpaceHandlers.MockAddressSpaceHandler;
import utils.Utils;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.util.HashMap;

public class LayoutCommunicationHandler {
    private final HashMap<String,AddressSpaceHandler> addressSpaceHandlers;

    public LayoutCommunicationHandler(PriorityBlockingQueueWrapper<Command> queue) {
        addressSpaceHandlers = new HashMap<>();
        addressSpaceHandlers.put("mock",new MockAddressSpaceHandler(queue));
    }

    public SetStateClass setStateClass(JsonObject command) {
        return new SetStateClass(command);
    }
    public class SetStateClass implements Runnable {
        private final JsonObject command;
        public SetStateClass(JsonObject command) {
            this.command = command;
        }
        @Override
        public void run() {
            System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Working on: SetState");
            command.get("addressMapping").getAsJsonArray().forEach(jsonElement -> {
                if (addressSpaceHandlers.containsKey(jsonElement.getAsJsonObject().get("AddressSpace").getAsString())) {
                    addressSpaceHandlers.get(jsonElement.getAsJsonObject().get("AddressSpace").getAsString()).applyStateMappings(command);
                }
            });
        }
    }
}
