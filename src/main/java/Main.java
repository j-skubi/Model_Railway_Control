
import com.google.gson.JsonParser;
import exceptions.CorruptedSaveFile;
import server.Server;

import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        } catch (CorruptedSaveFile | FileNotFoundException | SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
