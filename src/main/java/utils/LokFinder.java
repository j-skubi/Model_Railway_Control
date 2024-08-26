package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

import static java.lang.Thread.sleep;

public class LokFinder implements Runnable {
    private final PriorityBlockingQueueWrapper<Command> queue;

    public LokFinder(PriorityBlockingQueueWrapper<Command> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "LOK");
        json.addProperty("command", "setTrainSpeed");
        json.addProperty("modelID", -1);
        json.addProperty("speed", 0);



        JsonObject header = new JsonObject();
        header.addProperty("from", "view");
        header.addProperty("commandType", "setLokSpeed");

        JsonObject command = new JsonObject();
        command.add("header",header);
        command.add("body",json);

        for (int i = 10; i < 15000; i++) {
            JsonArray addressSpaceMappings = new JsonArray();
            JsonObject cs3 = new JsonObject();
            cs3.addProperty("addressSpace","cs3");
            cs3.addProperty("address", i);
            addressSpaceMappings.add(cs3);
            json.add("addressSpaceMappings", addressSpaceMappings);
            queue.add(new Command(5000, command));
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static LokFinder create(PriorityBlockingQueueWrapper<Command> queue) {
        return new LokFinder(queue);
    }
}
