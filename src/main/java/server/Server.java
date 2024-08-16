package server;

import com.google.gson.JsonObject;
import exceptions.CorruptedSaveFile;
import layout.Layout;
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

    private Layout model;
    private final ClientHandler clientHandler;

    public Server (int port, JsonObject saveFile) throws CorruptedSaveFile {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        commandQueue = new PriorityBlockingQueue<>();
        queueWrapper = new PriorityBlockingQueueWrapper<>(commandQueue);

        model = new Layout(saveFile);
        clientHandler = new ClientHandler(port,queueWrapper);
    }

    public void run() {
        while (!shutdown) {
            try {
                Command command = commandQueue.take();
                System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "working on: " + command.getJson());
                switch (command.getJson().get("header").getAsJsonObject().get("from").getAsString()) {
                    case "model" -> handleServerMessages(command.getJson());
                    case "cs3" -> handleCS3Messages(command.getJson());
                    case "webClient" -> handleUIMessages(command.getJson());
                    default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Message Origin not known");

                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void handleServerMessages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();

        switch (header.get("commandType").getAsString()) {
            case "requestViewAnswer" -> threadPool.submit(clientHandler.sendToClientClass(json));
            default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "CommandType not known");
        }
    }
    private void handleCS3Messages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();
    }
    private void handleUIMessages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();

        switch (header.get("commandType").getAsString()) {
            case "shutdown" -> this.shutdown = true;                                                                    //TODO: Interrupt all Running Threads;
            case "requestView" -> threadPool.submit(model.requestView(body,queueWrapper,header.get("clientID").getAsInt()));
            default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "CommandType not known");

        }
    }
}
