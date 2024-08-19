package layoutCommunication;

import com.google.gson.JsonObject;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AddressSpaceHandler {
    private String type;

    public abstract void send(JsonObject json);

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
    private boolean hasStateMapping(String state, JsonObject addressSpace) {
        try {
            if (!addressSpace.get("type").getAsString().equals(type)) {
                return false;
            }
            AtomicBoolean containsStateMapping = new AtomicBoolean(false);
            addressSpace.get("StateMappings").getAsJsonArray().forEach(mapping -> {
                if (mapping.getAsJsonObject().has(state)) {
                    containsStateMapping.set(true);
                }
            });
            return containsStateMapping.get();

        } catch (NullPointerException e) {
            return false;
        }
    }
}
