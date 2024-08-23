package layoutCommunication.addressSpaceHandlers;

import com.google.gson.JsonObject;
import layoutCommunication.AddressSpaceHandler;
import layoutCommunication.LayoutCommunicationHandler;
import utils.Utils;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

public class MockAddressSpaceHandler extends AddressSpaceHandler {

    public MockAddressSpaceHandler(LayoutCommunicationHandler layoutCommunicationHandler) {
        super("mock", layoutCommunicationHandler);
    }

    @Override
    public void send(int id, JsonObject command) {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Send to Layout: " + command.toString());
        layoutCommunicationHandler.taskIsDone(id);
    }
}
