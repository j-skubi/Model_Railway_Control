package layoutCommunication.addressSpaceHandlers;

import com.google.gson.JsonObject;
import exceptions.LayoutCommandException;
import layoutCommunication.AddressSpaceHandler;
import layoutCommunication.LayoutCommunicationHandler;
import utils.Utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class CS3Handler extends AddressSpaceHandler implements Runnable {
    private final InetAddress cs3Address;
    private final DatagramSocket outSocket;
    private final DatagramSocket inSocket;
    private final Thread receiver;

    private final ArrayList<CS3Task> activeTasks;

    private boolean shutdown = false;
    private CanDataPacket last = new CanDataPacket();
    private final int hash;

    public CS3Handler(String cs3Ip, LayoutCommunicationHandler layoutCommunicationHandler) throws UnknownHostException, SocketException {
        super("cs3", layoutCommunicationHandler);
        cs3Address = InetAddress.getByName(cs3Ip);
        outSocket = new DatagramSocket();
        inSocket = new DatagramSocket(15730, InetAddress.getLocalHost());
        receiver = new Thread(this, "cs3PortListener");

        activeTasks = new ArrayList<>();

        hash = generateHash();

        receiver.start();
    }
    @Override
    public void run() {
        byte[] temp = new byte[13];
        DatagramPacket packet = new DatagramPacket(temp, temp.length);
        while(!shutdown) {
            try {
                inSocket.receive(packet);
                handleIncoming(CanDataPacket.fromBytes(packet.getData()));
            } catch (IOException e) {
                System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "IOException");
            } catch (LayoutCommandException e) {
                System.err.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "LayoutCommandException");
                throw new RuntimeException(e);
            }
        }
    }
    private boolean filterDoubleMessages(CanDataPacket incoming) {
        boolean ret = incoming.equals(last);
        last = incoming;
        return ret;
    }
    private void handleIncoming(CanDataPacket incoming) {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Received CanBusMessage: " + incoming);
        if (incoming.response == 0) {
            return;
        }
        if (filterDoubleMessages(incoming)) {
            return;
        }
        boolean wasUsed = false;
        List<CS3Task> toBeRemoved = new ArrayList<>();
        for (CS3Task task : activeTasks) {
            if (task.incomingMessage(incoming)) {
                wasUsed = true;
                if (task.isDone()) {
                    toBeRemoved.add(task);
                    layoutCommunicationHandler.taskIsDone(task.id);
                }
            }
        }
        activeTasks.removeAll(toBeRemoved);
        if (!wasUsed) {
            JsonObject body = new JsonObject();
            body.addProperty("addressSpace", "cs3");
            body.addProperty("address", incoming.getUid());
            switch (incoming.command) {
                case 0x0b -> {
                    body.addProperty("type","MA");
                    body.addProperty("state", incoming.data[4]);
                    body.addProperty("power", incoming.data[5]);
                }
                case 0x11 -> {
                    body.addProperty("type","SENSOR");
                    body.addProperty("oldState", incoming.data[4]);
                    body.addProperty("newState", incoming.data[5]);
                }
                case 0x04 -> {
                    body.addProperty("type","LOK");
                    body.addProperty("command", "setTrainSpeed");
                    body.addProperty("speed", ((incoming.data[4] & 0xff) << 8) | (incoming.data[5] & 0xff));
                }
                case 0x05 -> {
                    body.addProperty("type","LOK");
                    body.addProperty("command", "setTrainDirection");
                    body.addProperty("direction", incoming.data[4] == 1 ? "FORWARD": "BACKWARDS");
                }
                case 0x06 -> {
                    body.addProperty("type","LOK");
                    body.addProperty("command", "activateLokFunction");
                    body.addProperty("index", incoming.data[4]);
                    body.addProperty("value", incoming.data[5]);
                }
                default -> {return;}
            }
            layoutCommunicationHandler.standaloneMessage(body);
        }
    }

    @Override
    public void send(int id, JsonObject json) {
        CS3Task task = new CS3Task(id, json);
        activeTasks.add(task);
        task.activate();
    }
    private void send(byte[] bytes) {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, cs3Address, 15731);
        try {
            outSocket.send(packet);
        } catch (IOException e) {
            //TODO Error Handling
        }
    }

    private int generateHash() {
        int uid = 0x18FF;

         return ((((0xff80 & uid) << 3) | (0x007f & uid) & 0xff70) | 0x0300);
        //return 6912;
    }
    private class Response extends Thread {
        private final List<CanDataPacket> response;
        public Response (List<CanDataPacket> response) {
            this.response = response;
        }
        public void activate() {
            this.start();
        }
        @Override
        public void run() {
            try {
                sleep(100);
                response.forEach(p -> send(p.toBytes()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private class CS3Task {
        private final int id;
        private final ArrayList<CanDataPacket> packets;
        private Response response;

        public CS3Task(int id, JsonObject json) {
            this.id = id;
            packets = new ArrayList<>();

            switch (json.get("type").getAsString()) {
                case "TURNOUT" -> json.get("cs3").getAsJsonObject().entrySet().forEach(state -> {
                    if (state.getKey().equals(json.get("newState").getAsString())) {
                        state.getValue().getAsJsonObject().entrySet().forEach(address -> {
                            CanDataPacket packet = new CanDataPacket();
                            packet.hash = hash;
                            packet.command = 0x0b;
                            packet.setUid(Integer.parseInt(address.getKey()));
                            packet.data[4] = (byte) address.getValue().getAsInt();
                            packet.data[5] = (byte) 1;
                            packet.dlc = 6;
                            packets.add(packet);
                        });
                    }
                });
                case "LOK" -> {
                    CanDataPacket packet = new CanDataPacket();
                    packet.hash = hash;
                    packet.setUid(json.get("cs3").getAsInt());
                    switch (json.get("command").getAsString()) {
                        case "setTrainSpeed" -> {
                            packet.command = 0x04;
                            if (json.get("speed") != null) {
                                packet.data[4] = (byte) (json.get("speed").getAsInt() >> 8);
                                packet.data[5] = (byte) (json.get("speed").getAsInt());
                                packet.dlc = 6;
                            } else {
                                packet.dlc = 4;
                            }

                        }
                        case "setTrainDirection" -> {
                            packet.command = 0x05;
                            if (json.get("direction") != null) {
                                packet.data[4] = (byte) (json.get("direction").getAsString().equals("FORWARD") ? 1 : 2);
                                packet.dlc = 5;
                            } else {
                                packet.dlc = 4;
                            }
                        }
                        case "activateLokFunction" -> {
                            packet.command = 0x06;
                            packet.data[4] = (byte) (json.get("index").getAsInt());
                            if (json.get("value") != null) {
                                packet.data[5] = (byte) (json.get("value").getAsInt());
                            } else {
                                packet.dlc = 5;
                            }
                            if (json.get("functionValue") != null) {
                                packet.data[6] = (byte) (json.get("functionValue").getAsInt() >> 8);
                                packet.data[7] = (byte) (json.get("functionValue").getAsInt());
                                packet.dlc = 8;
                            } else {
                                packet.dlc = 6;
                            }
                            if (!json.get("isToggle").getAsBoolean()) {
                                try {
                                    CanDataPacket off = CanDataPacket.fromBytes(packet.toBytes());
                                    off.data[5] = 0;
                                    response = new Response(List.of(off));
                                } catch (LayoutCommandException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    packets.add(packet);
                }
            }

        }
        public void activate() {
            packets.forEach(packet -> {
                System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Sending CanBusMessage: " + packet);
                send(packet.toBytes());
            });
        }
        public boolean isDone() {
            return packets.isEmpty();
        }
        synchronized public boolean incomingMessage(CanDataPacket incoming) {
            boolean wasUsed = packets.removeIf(incoming::isResponseTo);
            if (packets.isEmpty() && response != null) {
                response.activate();
            }
            return wasUsed;
        }
    }
    private static class CanDataPacket {
        private byte prio;
        private byte command;
        private byte response;
        private int hash;
        private byte dlc;
        private byte[] data;

        public CanDataPacket() {
            data = new byte[8];
        }
        private byte[] toBytes() {
            byte[] bytes = new byte[13];
            bytes[0] = (byte) (prio << 1);
            bytes[0] = (byte) (bytes[0] | command >>> 7);
            bytes[1] = (byte) (command << 1);
            bytes[1] = (byte) (bytes[1] | response);
            bytes[2] = (byte) (hash >> 8);
            bytes[3] = (byte) hash;
            bytes[4] = dlc;
            System.arraycopy(data, 0, bytes, 5, bytes.length - 5);
            return bytes;
        }
        public void setUid(int uid) {
            data[0] = (byte) (uid >> 24);
            data[1] = (byte) (uid >> 16);
            data[2] = (byte) (uid >> 8);
            data[3] = (byte) (uid);

        }
        public int getUid() {
            return (data[0] << 24) | (data[1] << 16) | (data[2] << 8) | data[3];
        }
        public boolean isResponseTo(CanDataPacket canDataPacket) {
            return this.response == 1 &&
                canDataPacket.getUid() == this.getUid() &&
                canDataPacket.command == this.command &&
                canDataPacket.dlc == this.dlc;
        }
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CanDataPacket in)) {
                return false;
            }
            boolean ret = prio == in.prio && dlc == in.dlc && response == in.response
                    && command == in.command && hash == in.hash;
            for (int i = 0; i < data.length; i++) {
                if (data[i] != in.data[i]) {
                    ret = false;
                    break;
                }
            }
            return ret;
        }
        @Override
        public String toString() {
            return String.format("Prio: %02x | Response: %x | Command: %02x | Hash: %02x | uid: %06x | state: %x | Power: %x", prio, response, command, hash, getUid(), data[4], data[5]);
        }
        public static CanDataPacket fromBytes(byte[] bytes) throws LayoutCommandException {
            if (bytes.length != 13) {
                throw new LayoutCommandException("Illegal length of Bytes");
            }
            CanDataPacket canMessage = new CanDataPacket();
            canMessage.prio = (byte) (bytes[0] >> 1);
            canMessage.command = (byte) (((bytes[0] & 1) << 7) | (bytes[1] >>> 1));
            canMessage.response = (byte) (bytes[1] & 1);
            canMessage.hash = (short) (((bytes[2]) << 8) | (bytes[3]));
            canMessage.dlc = bytes[4];
            canMessage.data = new byte[8];
            System.arraycopy(bytes, 5, canMessage.data, 0, 8);
            return canMessage;
        }
    }
}
