package layout.views.componentView;

import com.google.gson.JsonObject;
import exceptions.IllegalStateException;
import layout.model.LayoutComponent;
import layout.model.Turnout;

import java.util.List;

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
    public JsonObject toClient() {
        JsonObject json = super.save();

        json.addProperty("type", "TURNOUT");
        json.addProperty("state", turnout.getState());
        json.add("legalStates", turnout.getLegalStatesAsJsonArray());

        return json;
    }

    @Override
    public JsonObject changeState() {

        List<String> legalStates = turnout.getLegalStates();
        int currentIndex = legalStates.indexOf(turnout.getState());
        String nextState = legalStates.get((currentIndex + 1) % legalStates.size());

        JsonObject json;
        try {
            json = turnout.setState(nextState);
        } catch (IllegalStateException e) {
            System.err.println("Exception in ChangeState in TurnoutView. Unreachable check Code!!");
            throw new RuntimeException(e);
        }
        return json;
    }
}
