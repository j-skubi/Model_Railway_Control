package server;

import layout.Layout;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private final ThreadPoolExecutor threadPool;
    private final Layout model;

    public Server (int port) {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        model = new Layout();
    }

}
