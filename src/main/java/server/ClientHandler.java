package server;

import com.google.gson.JsonObject;
import utils.Utils;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private int nextClientId;

    private boolean shutdown;
    private final PriorityBlockingQueueWrapper<Command> commandQueue;
    private final List<Client> clients;
    private final ServerSocket socket;
    private final Thread t;

    public ClientHandler(int port, PriorityBlockingQueueWrapper<Command> queue) {
        t = new Thread(this,"ClientHandler");
        clients = new ArrayList<>();
        this.commandQueue = queue;

        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        t.start();
    }
    public void shutdown() {
        shutdown = true;
        clients.forEach(Client::shutdown);
        t.interrupt();
    }
    public void shutdown(int clientID) {
        clients.stream().filter(client -> client.getId() == clientID).findFirst().ifPresent(client -> {clients.remove(client); client.shutdown();});
    }
    @Override
    public void run() {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Listening for incoming connections on port: " + socket.getLocalPort());
        while (!shutdown) {
            try {
                clients.add(new Client(socket.accept(),nextClientId++,commandQueue));
                System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Accepted new Client with clientID: " + (nextClientId - 1));
            } catch (IOException e) {
                //TODO: Error Handling
            }
        }
    }

    private JsonObject wrapJsonObject(JsonObject json) {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Wrapping Message: " + json);
        JsonObject message = new JsonObject();
        JsonObject messageHeader = new JsonObject();
        messageHeader.addProperty("messageType",
                switch (json.get("header").getAsJsonObject().get("commandType").getAsString()) {
                    case "requestViewAnswer" -> "RequestAnswer";
                    case "notifyChange", "addViewComponent" -> "notifyChange";
                    default -> "Ignore";
                });
        message.add("header",messageHeader);
        message.add("body",json);
        return message;
    }

    public SendToClientClass sendToClientClass(JsonObject json) {
        return new SendToClientClass(json);
    }
    public class SendToClientClass implements  Runnable {
        private final JsonObject json;
        public SendToClientClass(JsonObject json) {
            this.json = json;
        }
        @Override
        public void run() {
            JsonObject message = wrapJsonObject(json);
            clients.stream().filter(client -> client.getId() == json.get("header").getAsJsonObject().get("to").getAsInt())
                    .findFirst().ifPresent(client -> client.send(message));
        }
    }

    public SendByActiveView sendByActiveView(JsonObject json) {
        return new SendByActiveView(json);
    }
    public class SendByActiveView implements  Runnable {
        private final JsonObject json;
        public SendByActiveView (JsonObject command) {
            this.json = command;
        }

        @Override
        public void run() {
            System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Sending Message to all active Viewers");
            JsonObject message = wrapJsonObject(json);
            clients.forEach(client -> client.send(message));

        }
    }
}
