package layout.views;

import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.model.LayoutComponent;
import layout.views.componentView.ComponentView;
import utils.Utils;
import utils.datastructures.AVLTree;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.util.HashMap;

public class ViewHandler {
    private final HashMap<String,View> views;

    public ViewHandler(JsonObject json, AVLTree<LayoutComponent> model) throws CorruptedSaveFile {
        views = new HashMap<>();
        views.put(ComponentView.TYPE, new ComponentView(json.get(ComponentView.TYPE).getAsJsonObject(),model));
    }

    public JsonObject save() {
        JsonObject json = new JsonObject();
        views.forEach((viewType, view) -> {
            json.add(viewType, view.save());
        });
        return json;
    }




    public RequestViewClass requestViewClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue, int clientId) {
        return new RequestViewClass(command,queue, clientId);
    }
    public class RequestViewClass implements Runnable {
        private final PriorityBlockingQueueWrapper<Command> queue;
        private final JsonObject command;
        private final int clientId;
        public RequestViewClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue, int clientId) {
            this.queue = queue;
            this.command = command;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Working on: RequestView from Client " + clientId);

            String viewType = command.get("viewType").getAsString();

            JsonObject header = new JsonObject();
            header.addProperty("from", "view");
            header.addProperty("to", clientId);
            header.addProperty("commandType", "requestViewAnswer");

            JsonObject body = views.get(viewType).toClient();

            JsonObject response = new JsonObject();
            response.add("header", header);
            response.add("body", body);

            queue.add(new Command(1000, response));
        }
    }
    public ChangeComponentStateClass changeComponentStateClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
        return new ChangeComponentStateClass(command,queue);
    }
    public class ChangeComponentStateClass implements Runnable {
        private final PriorityBlockingQueueWrapper<Command> queue;
        private final JsonObject command;

        public ChangeComponentStateClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
            this.queue = queue;
            this.command = command;
        }

        @Override
        public void run() {
            System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Working on: ChangeState");


            String viewType = command.get("viewType").getAsString();

            JsonObject header = new JsonObject();
            header.addProperty("from", "view");
            header.addProperty("to", "cs3");
            header.addProperty("commandType", "setState");

            JsonObject body = views.get(viewType).changeState(command.get("viewID").getAsInt());

            JsonObject response = new JsonObject();
            response.add("header", header);
            response.add("body", body);

            queue.add(new Command(200,response));
        }
    }

}
