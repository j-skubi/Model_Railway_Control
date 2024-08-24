package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class Lok extends LayoutComponent {
    private JsonArray addressSpaceMappings;
    private int speed;
    private Direction direction;

    public Lok(JsonObject json) {
        super(json);
        addressSpaceMappings = json.get("addressSpaceMappings").getAsJsonArray();
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
        json.add("addressSpaceMappings",addressSpaceMappings);
        json.addProperty("speed", speed);
        return json;
    }
    @Override
    public JsonObject save() {
        JsonObject json = super.save();

        json.add("addressSpaceMappings", addressSpaceMappings);
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
