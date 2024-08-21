
import com.google.gson.JsonParser;
import exceptions.CorruptedSaveFile;
import server.Server;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.run();
        } catch (CorruptedSaveFile | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
