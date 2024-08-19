package layout.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import exceptions.IllegalStateException;
import utils.Utils;
import utils.datastructures.Command;
import utils.datastructures.Event;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.util.ArrayList;
import java.util.List;

public class Turnout extends LayoutComponent {
    private final List<String> legalStates;
    private String state = "straight";
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
    public JsonArray getLegalStatesAsJsonArray() {
        JsonArray json = new JsonArray();
        legalStates.forEach(json::add);
        return json;
    }
    public List<String> getLegalStates() {
        return legalStates;
    }
    public JsonObject setState(String newState) throws IllegalStateException {
        JsonObject json = new JsonObject();

        if (!legalStates.contains(newState)) {
            throw new IllegalStateException("State " + newState + " is not included in legalStates");
        }

        json.addProperty("type", this.type);
        json.addProperty("modelID", this.id);
        json.addProperty("newState", newState);
        json.add("addressMapping", this.getAddressMappingAsJsonArray());

        return json;
    }
    @Override
    public void notifyChange(JsonObject json, PriorityBlockingQueueWrapper<Command> queue) {
        if (!json.get("type").getAsString().equals(type)) {
            System.err.println("Wrong TYPE");
            return;
        }
        if (legalStates.contains(json.get("newState").getAsString())) {
            JsonObject additionalInfo = new JsonObject();
            additionalInfo.addProperty("oldState", state);
            state = json.get("newState").getAsString();
            additionalInfo.addProperty("newState", state);

            this.notifyListeners(new Event(Event.EventType.StateChange, additionalInfo, queue));
            System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Notified All Listeners");
        }


    }

}
