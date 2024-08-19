package layout.views.componentView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.model.LayoutComponent;
import layout.views.View;
import utils.datastructures.AVLTree;

public class ComponentView extends View {
    public final static String TYPE = "COMPONENT-VIEW";

    private final AVLTree<ViewComponent> viewComponents;

    public ComponentView(JsonObject json, AVLTree<LayoutComponent> model) throws CorruptedSaveFile {
        viewComponents = new AVLTree<>();

        JsonArray viewComponentsArray = json.get("viewComponents").getAsJsonArray();
        for (JsonElement viewComponent : viewComponentsArray) {
            viewComponents.insert(ViewComponent.fromJSON(this,viewComponent.getAsJsonObject(), model));
        }
    }
    @Override
    public JsonObject save() {
        JsonObject json = new JsonObject();


        JsonObject metadata = new JsonObject();
        metadata.addProperty("type", TYPE);

        json.add("metadata", metadata);

        JsonArray viewComponentArray = new JsonArray();
        viewComponents.forEach(viewComponent -> {
            viewComponentArray.add(viewComponent.save());
        });

        json.add("viewComponents", viewComponentArray);

        return json;
    }
    public JsonObject toClient() {
        isActive = true;
        JsonObject json = new JsonObject();

        JsonObject metadata = new JsonObject();
        metadata.addProperty("type",TYPE);
        metadata.addProperty("size", viewComponents.size());

        json.add("metadata",metadata);

        JsonArray viewComponentArray = new JsonArray();
        viewComponents.forEach(viewComponent -> {
            viewComponentArray.add(viewComponent.toClient());
        });
        json.add("viewComponents", viewComponentArray);

        return json;
    }
    public JsonObject changeState(int viewID) {
        return viewComponents.find(viewID).changeState();
    }
}
