package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Turnout extends LayoutComponent {
    private final List<String> legalStates;
    private String state;
    public Turnout(int id) {
        super(id, "TURNOUT");
        this.legalStates = new ArrayList<>();
    }
    public Turnout(JsonObject json) {
        super(json);
        legalStates = new ArrayList<>();
        json.get("LegalStates").getAsJsonArray().forEach(state -> legalStates.add(state.getAsString()));
    }
    @Override
    public JsonObject save() {
        JsonObject json = super.save();

        JsonArray legalStatesArray = new JsonArray();
        legalStates.forEach(legalStatesArray::add);
        json.add("LegalStates", legalStatesArray);

        return json;
    }
    public String getState() {
        return state;
    }


}
