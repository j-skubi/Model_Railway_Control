package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.Event;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Lok extends LayoutComponent {
    private JsonArray addressSpaceMappings;
    private int speed;
    private Direction direction;
    private final HashMap<Integer,LokFunction> lokFunctions;

    private SpeedLimiter speedLimiter;

    public Lok(JsonObject json) {
        super(json);
        lokFunctions = new HashMap<>();
        json.get("lokFunctions").getAsJsonArray().forEach(function -> {
            lokFunctions.put(function.getAsJsonObject().get("index").getAsInt(), new LokFunction(function.getAsJsonObject()));
        });
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
    public Collection<LokFunction> getLokFunctions() {
        return lokFunctions.values();
    }
    public JsonObject setSpeed(int speed) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "LOK");
        json.addProperty("command", "setTrainSpeed");
        json.addProperty("modelID", id);
        json.add("addressSpaceMappings",addressSpaceMappings);
        json.addProperty("speed", speed);
        return json;
    }
    public JsonObject changeDirection() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "LOK");
        json.addProperty("command", "setTrainDirection");
        json.addProperty("modelID",id);
        json.add("addressSpaceMappings",addressSpaceMappings);
        json.addProperty("direction", direction.flip().name());
        return json;
    }
    public JsonObject activateLokFunction(int index) {
        JsonObject json = lokFunctions.get(index).activateFunction();
        json.addProperty("type", "LOK");
        json.addProperty("command", "activateLokFunction");
        json.add("addressSpaceMappings",addressSpaceMappings);
        json.addProperty("modelID", id);
        return json;
    }
    @Override
    public JsonObject save() {
        JsonObject json = super.save();

        JsonArray lokFunctionJson = new JsonArray();
        lokFunctions.forEach((index,function) -> lokFunctionJson.add(function.save()));
        json.add("lokFunctions", lokFunctionJson);
        json.add("addressSpaceMappings", addressSpaceMappings);
        json.addProperty("speed", speed);
        json.addProperty("direction", direction.name());

        return json;
    }

    @Override
    public void notifyChange(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
        switch (command.get("command").getAsString()) {
            case "setTrainSpeed" -> {
                speed = command.get("speed").getAsInt();

                if (speedLimiter == null || !speedLimiter.isAlive()) {
                    speedLimiter = new SpeedLimiter(queue);
                    speedLimiter.start();
                }
                speedLimiter.collect(speed);
            }
            case "setTrainDirection" -> {
                direction = Direction.valueOf(command.get("direction").getAsString());
                JsonObject additionalInfo = new JsonObject();
                additionalInfo.addProperty("command", "setTrainDirection");
                additionalInfo.addProperty("direction", direction.name());
                notifyListeners(new Event(Event.EventType.LokStateChange, additionalInfo, queue));
            }
            case "activateLokFunction" -> {
                lokFunctions.get(command.get("index").getAsInt()).isActive = (command.get("value").getAsInt() == 1);
            }
        }
    }
    private enum Direction {
        FORWARD,
        BACKWARDS;
        public Direction flip() {
            return this.equals(FORWARD) ? BACKWARDS : FORWARD;
        }
    }

    private class SpeedLimiter extends Thread {
        private final PriorityBlockingQueueWrapper<Command> queue;
        private int speed;
        private SpeedLimiter(PriorityBlockingQueueWrapper<Command> queue) {
            this.queue = queue;
        }
        @Override
        public void run() {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            JsonObject additionalInfo = new JsonObject();
            additionalInfo.addProperty("newSpeed", speed);
            additionalInfo.addProperty("command", "setTrainSpeed");
            notifyListeners(new Event(Event.EventType.LokStateChange, additionalInfo, queue));
        }
        public void collect(int speed) {
            this.speed = speed;
        }
    }
    public static class LokFunction {
        private int index;
        private boolean isToggle;
        private boolean isActive;
        private boolean hasInputField;
        private String name;
        private LokFunction(JsonObject json) {
            isActive = false;
            index = json.get("index").getAsInt();
            isToggle = json.get("isToggle").getAsBoolean();
            hasInputField = json.get("hasInputField").getAsBoolean();
            name = json.get("name").getAsString();
        }
        public JsonObject save() {
            JsonObject json = new JsonObject();
            json.addProperty("index", index);
            json.addProperty("isToggle", isToggle);
            json.addProperty("hasInputField", hasInputField);
            json.addProperty("name", name);
            return json;
        }
        public JsonObject toClient() {
            JsonObject json = new JsonObject();
            json.addProperty("index", index);
            json.addProperty("isToggle", isToggle);
            json.addProperty("hasInputField", hasInputField);
            json.addProperty("name",name);
            json.addProperty("isActive", isActive);
            return json;
        }
        public JsonObject activateFunction() {
            JsonObject json = new JsonObject();
            json.addProperty("index", index);
            json.addProperty("isToggle", isToggle);
            json.addProperty("value", isActive ? 0 : 1);
            return json;
        }
    }
}
