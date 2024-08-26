package layoutCommunication;

import com.google.gson.JsonObject;
import layoutCommunication.addressSpaceHandlers.CS3Handler;
import layoutCommunication.addressSpaceHandlers.MockAddressSpaceHandler;
import utils.Utils;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LayoutCommunicationHandler {
    private final HashMap<String,AddressSpaceHandler> addressSpaceHandlers;
    private final PriorityBlockingQueueWrapper<Command> queue;
    private final List<LayoutTask> activeTasks;

    public LayoutCommunicationHandler(PriorityBlockingQueueWrapper<Command> queue) throws SocketException, UnknownHostException {
        this.queue = queue;
        activeTasks = new ArrayList<>();

        addressSpaceHandlers = new HashMap<>();
        addressSpaceHandlers.put("mock",new MockAddressSpaceHandler(this));
        addressSpaceHandlers.put("cs3", new CS3Handler("192.168.178.153", this));
    }

    public void taskIsDone(int id) {
        List<LayoutTask> toBeRemoved = new ArrayList<>();
        for (LayoutTask task : activeTasks) {
            if (task.taskIsDone(id)) {
                toBeRemoved.add(task);
                queue.add(new Command(200, task.createResponse()));
            }
        }
        activeTasks.removeAll(toBeRemoved);
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
            activeTasks.add(new LayoutTask(command));
        }
    }

    private class LayoutTask {
        private static int IDCOUNTER;

        private final JsonObject command;

        private final List<Integer> tasks;

        public LayoutTask(JsonObject command) {
            this.command = command;
            tasks = new ArrayList<>();

            command.get("addressSpaceMappings").getAsJsonObject().entrySet().forEach(entry -> {
                if (addressSpaceHandlers.containsKey(entry.getKey())) {
                    int id = IDCOUNTER++;
                    tasks.add(id);
                    JsonObject layoutCommand;
                    layoutCommand = command;
                    layoutCommand.remove("addressSpaceMappings");
                    layoutCommand.add(entry.getKey(), entry.getValue());
                    addressSpaceHandlers.get(entry.getKey()).send(id, layoutCommand);
                }
            });
        }
        public boolean taskIsDone(int id) {
            tasks.remove(Integer.valueOf(id));
            return tasks.isEmpty();
        }
        public JsonObject createResponse() {

            JsonObject header = new JsonObject();
            header.addProperty("from","layout");
            header.addProperty("to","model");
            header.addProperty("commandType", "notifyChange");

            JsonObject body = new JsonObject();
            body.addProperty("modelID", command.get("modelID").getAsInt());
            body.addProperty("type", command.get("type").getAsString());

            switch (command.get("type").getAsString()) {
                case "TURNOUT" -> body.addProperty("newState", command.get("newState").getAsString());
                case "LOK" -> {
                    body.addProperty("command", command.get("command").getAsString());
                    switch (command.get("command").getAsString()) {
                        case "setTrainSpeed" -> body.addProperty("speed", command.get("speed").getAsInt());
                        case "setTrainDirection" -> body.addProperty("direction", command.get("direction").getAsString());
                        case "activateLokFunction" -> {
                            body.addProperty("value", command.get("isToggle").getAsBoolean() ? command.get("value").getAsInt() : 0);
                            body.addProperty("index", command.get("index").getAsInt());
                        }
                    }
                }
            }

            JsonObject response = new JsonObject();
            response.add("header",header);
            response.add("body",body);

            return response;
        }
    }
}
