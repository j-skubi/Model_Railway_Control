package layout.views.componentView;

import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.model.LayoutComponent;
import utils.datastructures.AVLTree;
import utils.datastructures.EventListener;

public abstract class ViewComponent implements AVLTree.AVLDataElement, EventListener {
    private final ComponentView parent;


    protected final int viewID;
    protected final LayoutComponent model;

    private final String name;

    public ViewComponent(ComponentView parent, int id, String name, LayoutComponent model) {
        this.viewID = id;
        this.model = model;
        this.parent = parent;
        this.name = name;

        model.addListener(this);
    }

    public ViewComponent(ComponentView parent, JsonObject json, LayoutComponent component) {
        this.viewID = json.get("viewID").getAsInt();
        this.model = component;
        this.parent = parent;
        this.name = json.get("name").getAsString();

        model.addListener(this);
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

        return json;
    }
    public abstract JsonObject changeState();

    public static ViewComponent fromJSON(ComponentView parent, JsonObject json, AVLTree<LayoutComponent> model) throws CorruptedSaveFile {
        return switch (json.get("type").getAsString()) {
            case "TURNOUT-VIEW" -> new TurnoutView(parent,json,model.find(json.get("modelID").getAsInt()));
            case "LOK-VIEW" -> new LokView(parent,json,model.find(json.get("modelID").getAsInt()));
            default -> throw new CorruptedSaveFile("No corresponding view for type " + json.get("type").getAsString() + "!");
        };
    }
    public static ViewComponent fromLayoutComponent(LayoutComponent layoutComponent, ComponentView parent, int viewID, String name) {
        return switch (layoutComponent.getType()) {
            case "TURNOUT" -> new TurnoutView(parent, viewID, name, layoutComponent);
            default -> throw new RuntimeException("Unreachable");
        };
    }

    public boolean isActive() {
        return parent.isActive();
    }
}
