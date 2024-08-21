package layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.model.LayoutComponent;
import layout.views.ViewHandler;
import utils.IDGenerator;
import utils.Utils;
import utils.datastructures.AVLTree;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;


public class Layout {
    private final IDGenerator idGenerator;
    private final ViewHandler viewHandler;
    private final AVLTree<LayoutComponent> components;

    public Layout(JsonObject json) throws CorruptedSaveFile {
        idGenerator = new IDGenerator(json.get("idGenerator").getAsInt());
        components = new AVLTree<>();

        for (JsonElement elem : json.get("components").getAsJsonArray()) {
            components.insert(LayoutComponent.fromJson(elem.getAsJsonObject()));
        }


        viewHandler = new ViewHandler(json.get("views").getAsJsonObject(), components);
    }

    public JsonObject save() {
        JsonObject json = new JsonObject();
        json.addProperty("idGenerator", idGenerator.save());

        JsonArray componentArray = new JsonArray();
        components.forEach(component -> componentArray.add(component.save()));

        json.add("views", viewHandler.save());

        json.add("components",componentArray);
        return json;
    }

    public ViewHandler.RequestViewClass requestView(JsonObject command, PriorityBlockingQueueWrapper<Command> queue, int clientId) {
        return viewHandler.requestViewClass(command,queue,clientId);
    }
    public ViewHandler.ChangeComponentStateClass changeComponentStateClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
        return viewHandler.changeComponentStateClass(command,queue);
    }
    public ViewHandler.AddViewComponentClass addViewComponentClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
        return viewHandler.addViewComponentClass(command, queue, components, idGenerator);
    }

    public NotifyChangeClass notifyChangeClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
        return new NotifyChangeClass(command, queue);
    }
    public class NotifyChangeClass implements Runnable{
        private final JsonObject command;
        private final PriorityBlockingQueueWrapper<Command> queue;
        public NotifyChangeClass(JsonObject command, PriorityBlockingQueueWrapper<Command> queue) {
            this.command = command;
            this.queue = queue;
        }
        @Override
        public void run() {
            System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Working on: " + command.toString());
            LayoutComponent component = components.find(command.get("modelID").getAsInt());
            component.notifyChange(command, queue);
        }
    }


}
