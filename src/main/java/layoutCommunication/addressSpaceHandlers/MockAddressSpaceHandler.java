package layoutCommunication.addressSpaceHandlers;

import com.google.gson.JsonObject;
import layoutCommunication.AddressSpaceHandler;
import utils.Utils;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class MockAddressSpaceHandler extends AddressSpaceHandler {

    public MockAddressSpaceHandler(PriorityBlockingQueueWrapper<Command> queue) {
        super("mock", queue);
    }

    @Override
    public void applyStateMappings(JsonObject command) {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Send to Layout: " + command.toString());

        JsonObject header = new JsonObject();
        header.addProperty("from","layout");
        header.addProperty("to","model");
        header.addProperty("commandType", "notifyChange");

        JsonObject body = new JsonObject();
        body.addProperty("modelID", command.get("modelID").getAsInt());
        body.addProperty("type", command.get("type").getAsString());
        body.addProperty("newState", command.get("newState").getAsString());

        JsonObject response = new JsonObject();
        response.add("header",header);
        response.add("body",body);

        this.queue.add(new Command(50, response));

    }
}
