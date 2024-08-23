package layoutCommunication;

import com.google.gson.JsonObject;

public abstract class AddressSpaceHandler {
    private final String type;
    protected final LayoutCommunicationHandler layoutCommunicationHandler;

    public AddressSpaceHandler(String type, LayoutCommunicationHandler layoutCommunicationHandler) {
        this.type = type;
        this.layoutCommunicationHandler = layoutCommunicationHandler;
    }

    public abstract void send(int id, JsonObject command);

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
