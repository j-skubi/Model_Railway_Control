package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;
import utils.datastructures.WebSocketFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client implements Runnable{
    private boolean shutdown;

    private final PriorityBlockingQueueWrapper<Command> queue;
    private final Socket client;
    private final OutputStream out;
    private final int clientId;

    public Client(Socket socket, int clientId, PriorityBlockingQueueWrapper<Command> queue) throws IOException {
        this.queue = queue;
        this.clientId = clientId;
        this.client = socket;
        out = client.getOutputStream();

        Thread clientHandler = new Thread(this, "clientListener");
        clientHandler.start();
    }

    public void send(JsonObject jsonObject) {
        new WebSocketFrame(jsonObject.toString().getBytes(StandardCharsets.UTF_8)).writeTo(out);
        try {
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            Scanner s = new Scanner(in, StandardCharsets.UTF_8);

            String data = s.useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);

            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                        + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
                out.write(response, 0, response.length);

            }
            this.send(welcomeMessage());
            while (!shutdown) {
                WebSocketFrame w = new WebSocketFrame(in);
                String content = new String(w.getPayload());
                try {
                    JsonObject json = JsonParser.parseString(content).getAsJsonObject();
                    System.out.println(json);
                    handleRequest(json);
                } catch (IllegalStateException e) {
                    System.err.println("Received Illegal byte String from Client " + clientId + "!! Possible Connection Loss");
                    System.err.println(content);
                    System.err.println("Shutting down Client with ID: " + clientId);
                    this.shutdown = true;
                }
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequest(JsonObject json) {
        System.out.println("[Client " + clientId + "] " + json.toString());
        try {
            JsonObject header = json.get("header").getAsJsonObject();
            JsonObject body = json.get("body").getAsJsonObject();
            switch (header.get("messageType").getAsString()) {
                case "Request" -> queue.add(new Command(1000,body));
                case "Set" -> queue.add(new Command(500,body));
            }
        } catch (NullPointerException e) {
            System.err.println("[Client " + clientId + "] " + "Json Not defined by protocol");
        }
    }
    private JsonObject welcomeMessage() {
        JsonObject json = new JsonObject();

        json.addProperty("store", "control");

        JsonObject action = new JsonObject();
        action.addProperty("type", "initialMessage");

        JsonObject payload = new JsonObject();
        payload.addProperty("clientID", clientId);

        action.add("payload",payload);
        json.add("action", action);

        return json;
    }
}
