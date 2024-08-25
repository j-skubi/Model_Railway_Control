package layout.views.componentView;

import com.google.gson.JsonObject;
import layout.model.LayoutComponent;
import layout.model.Lok;
import utils.datastructures.Command;
import utils.datastructures.Event;

public class LokView extends ViewComponent {
    private final Lok lok;
    public LokView(ComponentView parent, JsonObject json, LayoutComponent component) {
        super(parent, json, component);
        lok = (Lok) this.model;
    }
    public JsonObject setSpeed(int speed) {
        return lok.setSpeed(speed);
    }
    @Override
    public JsonObject save() {
        JsonObject json = super.save();

        json.addProperty("type", "LOK-VIEW");

        return json;
    }
    @Override
    public JsonObject toClient() {
        JsonObject json = super.save();

        json.addProperty("type", "LOK");
        json.addProperty("speed", lok.getSpeed());
        json.addProperty("direction", lok.getDirection());


        return json;
    }

    @Override
    public JsonObject changeState() {
        return lok.changeDirection();
    }

    @Override
    public boolean doesConsume(Event.EventType eventType) {
        return eventType.equals(Event.EventType.LokStateChange);
    }

    @Override
    public void apply(Event event) {
        JsonObject header = new JsonObject();
        header.addProperty("from", "view");
        header.addProperty("to", "COMPONENT-VIEW");
        header.addProperty("commandType", "notifyChange");

        JsonObject body = event.additionalInfo();
        body.addProperty("viewID", this.viewID);
        body.addProperty("type", "LOK");
        JsonObject response = new JsonObject();
        response.add("header",header);
        response.add("body",body);
        event.queue().add(new Command(600, response));
    }
}
