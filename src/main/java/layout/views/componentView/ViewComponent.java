package layout.views.componentView;

import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.model.LayoutComponent;
import utils.datastructures.AVLTree;

public abstract class ViewComponent implements AVLTree.AVLDataElement {
    private final ComponentView parent;


    private final int viewID;
    protected final LayoutComponent model;

    private String name;

    public ViewComponent(ComponentView parent, int id, LayoutComponent model) {
        this.viewID = id;
        this.model = model;
        this.parent = parent;
        this.name = "";
    }

    public ViewComponent(ComponentView parent, JsonObject json, LayoutComponent component) {
        this.viewID = json.get("viewID").getAsInt();
        this.model = component;
        this.parent = parent;
        this.name = json.get("name").getAsString();
    }

    @Override
    public int calculateKey() {
        return viewID;
    }

    public JsonObject save() {
        JsonObject json = new JsonObject();

        json.addProperty("viewID", viewID);
        json.addProperty("modelID", model.calculateKey());
        json.addProperty("name",name);

        return json;
    }
    public JsonObject toClient() {
        JsonObject json = new JsonObject();

        json.addProperty("viewID", viewID);
        json.addProperty("name", name);
        json.add("addressMapping", model.getAddressMappingAsJsonArray());

        return json;
    }

    public static ViewComponent fromJSON(ComponentView parent, JsonObject json, AVLTree<LayoutComponent> model) throws CorruptedSaveFile {

        return switch (json.get("type").getAsString()) {
            case "TURNOUT-VIEW" -> new TurnoutView(parent,json,model.find(json.get("modelID").getAsInt()));
            default -> throw new CorruptedSaveFile("No corresponding view for type " + json.get("type").getAsString() + "!");
        };
    }

    public boolean isActive() {
        return parent.isActive();
    }
}
