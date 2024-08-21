package server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import utils.Utils;
import utils.datastructures.Command;
import utils.datastructures.PriorityBlockingQueueWrapper;

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
    private final Thread clientHandler;


    private final PriorityBlockingQueueWrapper<Command> queue;
    private final Socket client;
    private final OutputStream out;
    private final int clientID;


    public Client(Socket socket, int clientId, PriorityBlockingQueueWrapper<Command> queue) throws IOException {
        this.queue = queue;
        this.clientID = clientId;
        this.client = socket;
        out = client.getOutputStream();

        clientHandler = new Thread(this, "ClientListener");
        clientHandler.start();
    }

    public void send(JsonObject jsonObject) {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + " " + clientID + "]", "Sending to client " + clientID + ": " + jsonObject.toString());
        new WebSocketFrame(jsonObject.toString().getBytes(StandardCharsets.UTF_8)).writeTo(out);
        try {
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public int getId() {
        return clientID;
    }
    public void shutdown() {
        shutdown = true;
        clientHandler.interrupt();
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
                    handleRequest(json);
                } catch (IllegalStateException e) {
                    System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + " " + clientID + "]", "Received Illegal byte String: " + content);
                    System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + " " + clientID + "]", "Shutting down Client with ID: " + clientID);
                    this.shutdown = true;
                }
            }

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequest(JsonObject json) {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + " " + clientID + "]", "Received: " + json.toString());
        try {
            JsonObject header = json.get("header").getAsJsonObject();
            JsonObject body = json.get("body").getAsJsonObject();
            switch (header.get("messageType").getAsString()) {
                case "Request" -> queue.add(new Command(1000,body));
                case "ChangeState" -> queue.add(new Command(500,body));
                case "ServerShutdown" -> queue.add(new Command(1, body));
                case "Ignore" -> {}
            }
        } catch (NullPointerException e) {
            System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + " " + clientID + "]", "Json Not defined by protocol");
        }
    }
    private JsonObject welcomeMessage() {
        JsonObject json = new JsonObject();

        JsonObject messageHeader = new JsonObject();
        messageHeader.addProperty("messageType", "controlData");
        json.add("header",messageHeader);

        JsonObject messageBody = new JsonObject();

        JsonObject commandHeader = new JsonObject();
        commandHeader.addProperty("commandType", "initialMessage");
        commandHeader.addProperty("from", "server");
        commandHeader.addProperty("to", clientID);
        messageBody.add("header", commandHeader);

        JsonObject commandBody = new JsonObject();
        commandBody.addProperty("clientID", clientID);
        messageBody.add("body",commandBody);

        json.add("body",messageBody);
        return json;
    }
}
