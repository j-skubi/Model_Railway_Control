package layoutCommunication;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class LayoutCommunicationHandler {
    private final HashMap<String,AddressSpaceHandler> addressSpaceHandlers;

    public LayoutCommunicationHandler() {
        addressSpaceHandlers = new HashMap<>();
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
            addressSpaceHandlers.values().forEach(addressSpaceHandler -> addressSpaceHandler.send(command));
        }
    }
}
