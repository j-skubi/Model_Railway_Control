package layout.views.componentView;

import com.google.gson.JsonObject;
import layout.model.LayoutComponent;
import layout.model.Turnout;

public class TurnoutView extends ViewComponent {
    private final Turnout turnout;
    public TurnoutView(ComponentView parent, int id, LayoutComponent model) {
        super(parent,id,model);
        turnout = (Turnout) this.model;
    }
    public TurnoutView(ComponentView parent, JsonObject json, LayoutComponent model) {
        super(parent, json, model);
        turnout = (Turnout) this.model;

    }

    @Override
    public JsonObject save() {
        JsonObject json = super.save();

        json.addProperty("type", "TURNOUT-VIEW");

        return json;
    }

    @Override
    public JsonObject basicClientInfo() {
        JsonObject json = super.save();

        json.addProperty("type", "TURNOUT");
        json.addProperty("state", turnout.getState());

        return json;
    }
}
