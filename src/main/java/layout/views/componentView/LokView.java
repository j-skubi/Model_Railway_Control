package layout.views.componentView;

import com.google.gson.JsonObject;
import layout.model.LayoutComponent;
import layout.model.Lok;
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
        return null;
    }

    @Override
    public boolean doesConsume(Event.EventType eventType) {
        return eventType.equals(Event.EventType.LokStateChange);
    }

    @Override
    public void apply(Event event) {

    }
}
