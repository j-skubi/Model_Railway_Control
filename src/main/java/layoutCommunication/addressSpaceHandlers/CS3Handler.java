package layoutCommunication.addressSpaceHandlers;

import com.google.gson.JsonObject;
import exceptions.LayoutCommandException;
import layoutCommunication.AddressSpaceHandler;
import layoutCommunication.LayoutCommunicationHandler;
import utils.Utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class CS3Handler extends AddressSpaceHandler implements Runnable {
    private final InetAddress cs3Address;
    private final DatagramSocket outSocket;
    private final DatagramSocket inSocket;
    private final Thread reveiver;

    private final ArrayList<CS3Task> activeTasks;

    private boolean shutdown = false;
    private final int hash;

    public CS3Handler(String cs3Ip, LayoutCommunicationHandler layoutCommunicationHandler) throws UnknownHostException, SocketException {
        super("cs3", layoutCommunicationHandler);
        cs3Address = InetAddress.getByName(cs3Ip);
        outSocket = new DatagramSocket();
        inSocket = new DatagramSocket(15730, InetAddress.getLocalHost());
        reveiver = new Thread(this, "cs3PortListener");

        activeTasks = new ArrayList<>();

        hash = generateHash();

        reveiver.start();
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
                //TODO Error Handling
            } catch (LayoutCommandException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void handleIncoming(CanDataPacket incoming) {
        System.out.format(Utils.getFormatString(), "[" + Thread.currentThread().getName() + "]", "[" + this.getClass().getSimpleName() + "]", "Received CanBusMessage: " + incoming);
        for (CS3Task task : activeTasks) {
            if (task.incomingMessage(incoming)) {
                activeTasks.remove(task);
                layoutCommunicationHandler.taskIsDone(task.id);
                break;
            }
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

    private class CS3Task {
        private final int id;
        private final ArrayList<CanDataPacket> packets;

        public CS3Task(int id, JsonObject json) {
            this.id = id;
            packets = new ArrayList<>();

            switch (json.get("type").getAsString()) {
                case "TURNOUT" -> {
                    json.get("stateMappings").getAsJsonArray().forEach(state -> {
                        if (state.getAsJsonObject().get("state").getAsString().equals(json.get("newState").getAsString())) {
                            state.getAsJsonObject().get("mapping").getAsJsonArray().forEach(mapping -> {
                                CanDataPacket packet = new CanDataPacket();
                                packet.hash = hash;
                                packet.command = 0x0b;
                                packet.setUid(mapping.getAsJsonObject().get("address").getAsInt());
                                System.err.println(mapping.getAsJsonObject().get("address").getAsInt());
                                packet.data[4] = (byte) mapping.getAsJsonObject().get("mapping").getAsInt();
                                packet.data[5] = (byte) (mapping.getAsJsonObject().get("power") != null ? mapping.getAsJsonObject().get("power").getAsInt() : 1);
                                if (mapping.getAsJsonObject().get("time") != null) {
                                    packet.data[6] = (byte) (mapping.getAsJsonObject().get("time").getAsInt() >> 8);
                                    packet.data[7] = (byte) (mapping.getAsJsonObject().get("time").getAsInt());
                                    packet.dlc = 8;
                                } else {
                                    packet.dlc = 6;
                                }
                                packets.add(packet);
                            });
                        }
                    });
                }
                case "setTrainSpeed" -> {
                    CanDataPacket packet = new CanDataPacket();
                    packet.hash = hash;
                    packet.command = 0x04;
                    packet.setUid(json.get("address").getAsInt());
                    if (json.get("speed") != null) {
                        packet.data[4] = (byte) (json.get("speed").getAsInt() >> 8);
                        packet.data[5] = (byte) (json.get("speed").getAsInt());
                        packet.dlc = 6;
                    } else {
                        packet.dlc = 4;
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
        synchronized public boolean incomingMessage(CanDataPacket incoming) {
            packets.removeIf(incoming::isResponseTo);
            return packets.isEmpty();
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
            bytes[1] = (byte) (bytes[1] | (byte) response);
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
