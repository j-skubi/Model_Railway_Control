package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.Event;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class Lok extends LayoutComponent {
    private JsonArray addressSpaceMappings;
    private int speed;
    private Direction direction;

    private SpeedLimiter speedLimiter;

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

}
