package server;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Server {
    private final ThreadPoolExecutor threadPool;

    public Server (int port) {
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }
}
