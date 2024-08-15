package server;

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
    @Override
    public void run() {
        System.out.println("[" + Thread.currentThread().getName() + "] Listening for incoming connections on port: " + socket.getLocalPort());
        while (!shutdown) {
            try {
                clients.add(new Client(socket.accept(),nextClientId++,commandQueue));
                System.out.println("[" + Thread.currentThread().getName() + "] Accepted new Client with clientID: " + (nextClientId - 1));
            } catch (IOException e) {
                //TODO: Error Handling
            }
        }
    }
}
