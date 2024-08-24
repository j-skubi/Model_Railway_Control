package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public abstract class MagnetArticle extends LayoutComponent {
    protected final Map<String, Map<String, JsonArray>> addressMapping;     //State to Address Mapping for all AddressSpaces

    public MagnetArticle(JsonObject json) {
        super(json);
        this.addressMapping = new HashMap<>();
        json.get("addressSpaceMappings").getAsJsonArray().forEach(add -> {
            HashMap<String, JsonArray> address = new HashMap<>();
            addressMapping.put(add.getAsJsonObject().get("addressSpace").getAsString(), address);
            add.getAsJsonObject().get("stateMappings").getAsJsonArray().forEach(elem -> {
                address.put(elem.getAsJsonObject().get("state").getAsString(), elem.getAsJsonObject().get("mapping").getAsJsonArray());
            });
        });
    }
    public MagnetArticle(int id, String type) {
        super(id,type);
        this.addressMapping = new HashMap<>();
    }
    @Override
    public JsonObject save() {
        JsonObject json = super.save();
        json.add("addressSpaceMappings", getAddressMappingAsJsonArray());
        return json;
    }

    public JsonArray getAddressMappingAsJsonArray() {
        JsonArray addresses = new JsonArray();
        addressMapping.forEach((key, value) -> {
            JsonObject addressSpace = new JsonObject();
            addressSpace.addProperty("addressSpace", key);

            JsonArray states = new JsonArray();
            value.forEach((key1, value1) -> {
                JsonObject mapping = new JsonObject();
                mapping.addProperty("state", key1);
                mapping.add("mapping", value1);
                states.add(mapping);
            });
            addressSpace.add("stateMappings", states);
            addresses.add(addressSpace);
        });
        return addresses;
    }
    public void addAddressMapping(String addressSpace, String state, JsonObject mapping) {
        if (!addressMapping.containsKey(addressSpace)) {
            addressMapping.put(addressSpace, new HashMap<>());
        }
        if (!addressMapping.get(addressSpace).containsKey(state)) {
            addressMapping.get(addressSpace).put(state, new JsonArray());
        }
        addressMapping.get(addressSpace).get(state).add(mapping);
    }
}
