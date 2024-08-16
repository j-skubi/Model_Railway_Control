package layout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.model.LayoutComponent;
import layout.views.ViewHandler;
import utils.IDGenerator;
import utils.datastructures.AVLTree;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;


public class Layout {
    private final IDGenerator idGenerator;
    private final ViewHandler viewHandler;
    private final AVLTree<LayoutComponent> components;

    public Layout(JsonObject json) throws CorruptedSaveFile {
        idGenerator = new IDGenerator(Integer.MIN_VALUE);
        components = new AVLTree<>();

        json.get("components").getAsJsonArray().forEach(elem -> {
            components.insert(LayoutComponent.fromJson(elem.getAsJsonObject()));
        });


        viewHandler = new ViewHandler(json.get("views").getAsJsonObject(), components);
    }

    public ViewHandler.RequestViewClass requestView(JsonObject command, PriorityBlockingQueueWrapper<Command> queue, int clientId) {
        return viewHandler.requestViewClass(command,queue,clientId);
    }

    public JsonObject save() {
        JsonObject json = new JsonObject();
        json.addProperty("idGenerator", idGenerator.save());

        JsonArray componentArray = new JsonArray();
        components.forEach(component -> {
            componentArray.add(component.save());
        });

        json.add("views", viewHandler.save());

        json.add("components",componentArray);
        return json;
    }

}
