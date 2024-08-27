package server;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exceptions.CorruptedSaveFile;
import layout.Layout;
import layoutCommunication.LayoutCommunicationHandler;
import utils.Utils;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.io.*;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.PriorityBlockingQueue;
import utils.datastructures.Command;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    private boolean shutdown;
    private final String saveFilePath = System.getProperty("user.dir") + "/saves/saveFile.json";

    private final ThreadPoolExecutor threadPool;
    private final PriorityBlockingQueue<Command> commandQueue;
    private final PriorityBlockingQueueWrapper<Command> queueWrapper;

    private final Layout model;
    private final LayoutCommunicationHandler layout;
    private final ClientHandler clientHandler;

    public Server() throws CorruptedSaveFile, FileNotFoundException, SocketException, UnknownHostException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(saveFilePath));
        JsonObject json = JsonParser. parseReader(bufferedReader).getAsJsonObject();

        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        commandQueue = new PriorityBlockingQueue<>();
        queueWrapper = new PriorityBlockingQueueWrapper<>(commandQueue);

        model = new Layout(json.get("layoutData").getAsJsonObject());
        layout = new LayoutCommunicationHandler(queueWrapper);
        clientHandler = new ClientHandler(json.get("serverData").getAsJsonObject().get("port").getAsInt(),queueWrapper);
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
                    case "clientHandler" -> handleClientHandlerMessages(command.getJson());
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
            case "notifyChange", "addViewComponent" -> threadPool.submit(clientHandler.sendByActiveView(json));
            case "setState","setLokSpeed","activateLokFunction" -> threadPool.submit(layout.setStateClass(body));
            default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "CommandType not known");
        }
    }
    private void handleLayoutMessages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();

        switch (header.get("commandType").getAsString()) {
            case "notifyChange" -> threadPool.submit(model.notifyChangeClass(body,queueWrapper));
            case "standaloneMessage" -> threadPool.submit(model.applyStandaloneMessageClass(body, queueWrapper));
            case "Error" -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Error in LayoutCommunication");
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
            case "setLokSpeed" -> threadPool.submit(model.setTrainSpeedClass(body,queueWrapper));
            case "addViewComponent" -> threadPool.submit(model.addViewComponentClass(body, queueWrapper));
            case "activateLokFunction" -> threadPool.submit(model.activateLokFunctionClass(body,queueWrapper));
            case "ServerShutdown" -> shutdown();
            default -> System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "CommandType not known");

        }
    }
    private void handleClientHandlerMessages(JsonObject json) {
        JsonObject header = json.get("header").getAsJsonObject();
        JsonObject body = json.get("body").getAsJsonObject();

        switch (header.get("commandType").getAsString()) {
            case "shutdownClient" -> clientHandler.shutdown(body.get("clientID").getAsInt());
        }
    }
    private void shutdown() {
        shutdown = true;
        try {
            threadPool.shutdown();
            boolean threadPoolShutdown = threadPool.awaitTermination(2, TimeUnit.SECONDS);

            if (threadPoolShutdown) {
                System.out.println("Shutdown ThreadPool");
            } else {
                System.err.println("Error while shutting down ThreadPool: Trying to save to different location");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        save();
        clientHandler.shutdown();

    }
    private void save() {
        JsonObject saveFile = new JsonObject();

        JsonObject serverData = new JsonObject();
        serverData.addProperty("port", 50745);

        saveFile.add("layoutData", model.save());
        saveFile.add("serverData", serverData);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.saveFilePath));
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            writer.write(gson.toJson(saveFile));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
