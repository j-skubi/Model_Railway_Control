package layout.views.componentView;

import com.google.gson.JsonObject;
import layout.model.LayoutComponent;
import layout.model.Sensor;
import utils.datastructures.Command;
import utils.datastructures.Event;

public class SensorView extends ViewComponent {
    private final Sensor sensor;
    public SensorView(ComponentView parent, JsonObject json, LayoutComponent component) {
        super(parent, json, component);
        this.sensor = (Sensor) component;
    }

    @Override
    public JsonObject toClient() {
        JsonObject json = super.toClient();
        json.addProperty("type", "SENSOR");
        json.addProperty("isOccupied",sensor.isOccupied());
        return json;
    }
    @Override
    public JsonObject changeState() {
        return null;
    }

    @Override
    public boolean doesConsume(Event.EventType eventType) {
        return eventType.equals(Event.EventType.S88Event);
    }

    @Override
    public void apply(Event event) {
        JsonObject header = new JsonObject();
        header.addProperty("from", "view");
        header.addProperty("to", "COMPONENT-VIEW");
        header.addProperty("commandType", "notifyChange");

        JsonObject body = event.additionalInfo();
        body.addProperty("viewID", this.viewID);
        body.addProperty("type", "SENSOR");
        JsonObject response = new JsonObject();
        response.add("header",header);
        response.add("body",body);
        event.queue().add(new Command(100, response));
    }
}
