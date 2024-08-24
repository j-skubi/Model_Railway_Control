package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class Lok extends LayoutComponent {
    private int address;
    private int speed;
    private Direction direction;

    public Lok(JsonObject json) {
        super(json);
        address = json.get("address").getAsInt();
        speed = json.get("speed").getAsInt();
        direction = Direction.valueOf(json.get("direction").getAsString());
    }
    public int getSpeed() {
        return speed;
    }
    public String getDirection() {
        return direction.name();
    }
    public JsonObject setSpeed(int speed) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "setTrainSpeed");

        JsonArray addressSpaceMappings = new JsonArray();                           //TODO Add Multiple AddressSpaces to Lok
        JsonObject cs3 = new JsonObject();
        cs3.addProperty("address",address);
        cs3.addProperty("addressSpace","cs3");
        addressSpaceMappings.add(cs3);

        json.add("addressSpaceMappings",addressSpaceMappings);
        json.addProperty("speed", speed);
        return json;
    }
    @Override
    public JsonObject save() {
        JsonObject json = super.save();

        json.addProperty("address", address);
        json.addProperty("speed", speed);
        json.addProperty("direction", direction.name());

        return json;
    }

    @Override
    public void notifyChange(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {

    }
    private enum Direction {
        FORWARD,
        BACKWARDS
    }
}
