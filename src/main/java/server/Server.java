package server;

import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.Layout;
import layoutCommunication.LayoutCommunicationHandler;
import utils.Utils;
import utils.datastructures.PriorityBlockingQueueWrapper;
import java.util.concurrent.PriorityBlockingQueue;
import utils.datastructures.Command;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private boolean shutdown;

    private final ThreadPoolExecutor threadPool;
    private final PriorityBlockingQueue<Command> commandQueue;
    private final PriorityBlockingQueueWrapper<Command> queueWrapper;

    private final Layout model;
    private final LayoutCommunicationHandler layout;
    private final ClientHandler clientHandler;

    public Server (int port, JsonObject saveFile) throws CorruptedSaveFile {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        commandQueue = new PriorityBlockingQueue<>();
        queueWrapper = new PriorityBlockingQueueWrapper<>(commandQueue);

        model = new Layout(saveFile);
        layout = new LayoutCommunicationHandler(queueWrapper);
        clientHandler = new ClientHandler(port,queueWrapper);
    }

    public void run() {
        while (!shutdown) {
            try {
                Command command = commandQueue.take();
                System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Dispatching: " + command.getJson());
                switch (command.getJson().get("header").getAsJsonObject().get("from").getAsString()) {
                    case "view" -> handleViewMessages(command.getJson());
                    case "layout" -> handleLayoutMessages(command.getJson());
                    case "webClient" -> handleUIMessages(command.getJson());
                    default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Message Origin not known");

                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleViewMessages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();

        switch (header.get("commandType").getAsString()) {
            case "requestViewAnswer" -> threadPool.submit(clientHandler.sendToClientClass(json));
            case "notifyChange" -> threadPool.submit(clientHandler.sendByActiveView(json));
            case "setState" -> threadPool.submit(layout.setStateClass(body));
            default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "CommandType not known");
        }
    }
    private void handleLayoutMessages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();

        switch (header.get("commandType").getAsString()) {
            case "notifyChange" -> threadPool.submit(model.notifyChangeClass(body,queueWrapper));
            default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "CommandType not known");
        }
    }
    private void handleUIMessages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();

        switch (header.get("commandType").getAsString()) {
            case "shutdown" -> this.shutdown = true;                                                                    //TODO: Interrupt all Running Threads;
            case "requestView" -> threadPool.submit(model.requestView(body,queueWrapper,header.get("clientID").getAsInt()));
            case "changeState" -> threadPool.submit(model.changeComponentStateClass(body, queueWrapper));
            default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "CommandType not known");

        }
    }
}
