import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import layout.model.Turnout;

public class Main {
    public static void main(String[] args) {
        Turnout turnout = new Turnout(0);
        JsonObject json = new JsonObject();
        json.addProperty("Address", 0x10);
        json.addProperty("Mapping", 1);
        turnout.addAddressMapping("cs3", "turnout", json);
        turnout.addAddressMapping("cs3", "straight", json);

        turnout.addAddressMapping("virtual", "turnout", json);
        turnout.addAddressMapping("virtual", "straight", json);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println(gson.toJson(turnout.save()));
    }
}
